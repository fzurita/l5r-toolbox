package l5r.toolbox.simulator;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import l5r.toolbox.L5RToolboxActivity;
import l5r.toolbox.R;
import l5r.toolbox.common.CommonInterface;
import l5r.toolbox.common.GuiUtils;
import l5r.toolbox.editor.EditorData;
import l5r.toolbox.editor.EditorDialogFragment;
import l5r.toolbox.roller.Roller;
import l5r.toolbox.roller.RollerManager;
import l5r.toolbox.rollmods.RollModDialogFragment;
import l5r.toolbox.rollmods.RollModFragment;
import l5r.toolbox.rollmods.RollMods;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class SimulatorFragment extends SherlockFragment implements CommonInterface {

    private TableLayout tableLayout = null;
    private LinearLayout dummyLayout = null;
    private TextView rollerOutput = null;
    private TableRow currentlySelectedRow = null;
    private EditText tnText = null;
    private ScrollView outputScrollView = null;
    private ScrollView rollerScrollView = null;
    private RollerManager rollerManager = null;
    private RollMods rollMods;
    private String tag = null;
    private RollModFragment rollModFragment = null;
    private SlidingDrawer slidingDrawer = null;
    private L5RToolboxActivity parentActivity = null;
    private boolean slidingDrawerOpen = true;
    private static final String dialogTag = "dialog";
    private static Simulator currentSim = null;
    private static final int OUTPUT_FILLER = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.simulator, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sim_actionbar, menu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tableLayout = (TableLayout) getSherlockActivity().findViewById(R.id.Simulator_RollTable);
        rollerOutput = (TextView) getSherlockActivity().findViewById(R.id.Simulator_RollerOutput);
        outputScrollView = (ScrollView) getSherlockActivity().findViewById(R.id.Simulator_OutputScroll);
        rollerScrollView = (ScrollView) getSherlockActivity().findViewById(R.id.Simulator_RollScroll);
        tnText = (EditText) getSherlockActivity().findViewById(R.id.SimulatorTnText);
        dummyLayout = (LinearLayout) getSherlockActivity().findViewById(R.id.Simulator_FakeLinearLayout);
        tag = getSherlockActivity().getResources().getString(R.string.simulator_tag);
        rollModFragment = (RollModFragment) getFragmentManager().findFragmentById(R.id.Simulator_RollModFragment);
        slidingDrawer = (SlidingDrawer) getSherlockActivity().findViewById(R.id.Simulator_SlidingDrawer);

        rollMods = new RollMods(0, 0, 0, 0, 0, 0, false, false, true, false);

        parentActivity = (L5RToolboxActivity) getSherlockActivity();
        rollerManager = L5RToolboxActivity.getRollerManager();
        populateRollers();

        if (slidingDrawer != null) {
            slidingDrawer.open();
        }

        if (savedInstanceState != null) {
            restoreStateData(savedInstanceState);
        } else {
            // Add additional filler at the beginning
            for (int index = 0; index < OUTPUT_FILLER; index++) {
                addOutput("");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.simulator_menu_new:
            removePreviousDialog();

            if (!getSherlockActivity().isFinishing()) {
                EditorDialogFragment editorDialog = EditorDialogFragment.newInstance(new EditorData(-1, null, -1, -1,
                        10, false, 0, false, true), tag, false);
                editorDialog.show(getFragmentManager(), dialogTag);
            }
            return true;
        case R.id.simulator_menu_quick:
            removePreviousDialog();

            if (!getSherlockActivity().isFinishing()) {
                EditorDialogFragment editorDialog = EditorDialogFragment.newInstance(new EditorData(-1, null, -1, -1,
                        10, false, 0, false, true), tag, true);
                editorDialog.show(getFragmentManager(), dialogTag);
            }
            return true;
        case R.id.simulator_menu_mods:
            removePreviousDialog();

            if (!getSherlockActivity().isFinishing()) {
                RollModDialogFragment rollModDialog = RollModDialogFragment.newInstance(rollMods, tag);
                rollModDialog.show(getFragmentManager(), dialogTag);
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void removePreviousDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.commit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
     * android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        // Set the currently selected row
        currentlySelectedRow = (TableRow) view;
        TextView textView = (TextView) currentlySelectedRow.getChildAt(1);

        menu.setHeaderTitle(textView.getText());

        // creates a menu inflater
        android.view.MenuInflater inflater = getSherlockActivity().getMenuInflater();
        // generates a Menu from a menu resource file
        inflater.inflate(R.menu.simulator_context_menu, menu);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        boolean returnValue = true;

        int id = 0;
        if (currentlySelectedRow != null) {
            id = currentlySelectedRow.getId();
        }

        // check selected menu item
        switch (item.getItemId()) {
        case R.id.simulator_menu_simulate:
            simulate(rollerManager.getWithId(id));
            break;
        case R.id.simulator_menu_delete:
            rollerManager.deleteWithId(id);
            populateRollers();
            rollerManager.saveRollers(getSherlockActivity());
            parentActivity.rollManagerChanged();
            break;
        case R.id.simulator_menu_edit:
            if (!getSherlockActivity().isFinishing()) {
                EditorDialogFragment editorDialog = EditorDialogFragment.newInstance(rollerManager.getWithId(id), tag,
                        false);
                editorDialog.show(getFragmentManager(), dialogTag);
            }
            break;
        default:
            returnValue = super.onContextItemSelected(item);
        }

        return returnValue;
    }
    
    /**
     * 
     */
    public void updateRollers() {
        rollerManager = L5RToolboxActivity.getRollerManager();
        populateRollers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        if (currentSim != null) {
            currentSim.informOfPause();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        if (currentSim != null) {
            currentSim.setFragment(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI
        savedInstanceState.putString("rollerOutput", rollerOutput.getText().toString());
        int rollerScrollPosition = rollerScrollView.getScrollY();
        int outputScrollPosition = outputScrollView.getScrollY();
        if (slidingDrawer != null) {
            slidingDrawerOpen = slidingDrawer.isOpened();
        }
        savedInstanceState.putInt("rollerScrollPosition", rollerScrollPosition);
        savedInstanceState.putInt("outputScrollPosition", outputScrollPosition);
        savedInstanceState.putParcelable("mods", rollMods);
        savedInstanceState.putString("tnText", tnText.getText().toString());
        savedInstanceState.putBoolean("drawerOpen", slidingDrawerOpen);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * @param savedInstanceState
     */
    public void restoreStateData(Bundle savedInstanceState) {
        // Restore UI

        rollerOutput.setText(savedInstanceState.getString("rollerOutput"));

        // Restore scroll position
        final int rollerScrollPosition = savedInstanceState.getInt("rollerScrollPosition");
        final int outputScrollPosition = savedInstanceState.getInt("outputScrollPosition");
        slidingDrawerOpen = savedInstanceState.getBoolean("drawerOpen");

        if (slidingDrawer != null) {

            if (slidingDrawerOpen) {
                slidingDrawer.open();
            } else {
                slidingDrawer.close();
            }
        }

        rollerScrollView.post(new Runnable() {
            public void run() {
                rollerScrollView.scrollTo(0, rollerScrollPosition);
            }
        });

        outputScrollView.post(new Runnable() {
            public void run() {
                outputScrollView.scrollTo(0, outputScrollPosition);
            }
        });

        // Restore RollMods
        rollMods = savedInstanceState.getParcelable("mods");
        tnText.setText(savedInstanceState.getString("tnText"));
    }

    public void simulate(EditorData data) {
        if (currentSim == null || !currentSim.isInProgress()) {
            // Update the static instance to allow an in progress
            // simulation
            // to continue during destruction and creation.
            currentSim = new Simulator(SimulatorFragment.this, new Roller(getRollMods(), data,
                    parentActivity.getCurrentWoundPenalties()));
            currentSim.simulate();
        }

        InputMethodManager imm = (InputMethodManager) getSherlockActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tnText.getWindowToken(), 0);

        if (dummyLayout != null) {
            dummyLayout.requestFocus();
        }

        if (rollModFragment != null) {
            rollModFragment.hideKeyboardFromFragment();
            rollModFragment.clearDataIfApplicable();
        }

        if (!rollMods.isSave()) {
            rollMods = new RollMods(0, 0, 0, 0, 0, 0, false, false, true, false);
        }
    }

    public void addRow(String text, int id) {

        View.OnClickListener action = new View.OnClickListener() {

            public void onClick(View view) {
                int id = view.getId();

                simulate(rollerManager.getWithId(id));
            }
        };

        // Generate table row to be added
        TableRow tableRow = GuiUtils.generateRow(tableLayout, text, "Sim", action, getSherlockActivity());

        // Add context menu
        tableRow.setId(id);
        registerForContextMenu(tableRow);

        // Add row to TableLayout
        tableLayout.addView(tableRow,
                new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        View separator = GuiUtils.generateSeparator(getSherlockActivity());
        tableLayout.addView(separator, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
    }

    /**
     * @param text
     */
    public void addOutput(String text) {
        rollerOutput.setText(text + "\n" + rollerOutput.getText());

        // Always scroll to the end on screen changes
        outputScrollView.post(new Runnable() {
            public void run() {
                outputScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    /**
	 * 
	 */
    public void removeAllRows() {
        tableLayout.removeAllViews();
    }

    /**
     * 
     */
    private void populateRollers() {
        removeAllRows();
        for (EditorData roller : rollerManager) {
            addRow(roller.getTitle(), roller.getId());
        }
    }

    /**
     * @return
     */
    public int getTnValue() {
        String tnString = tnText.getText().toString();

        int returnValue = tnString.length() != 0 ? Integer.parseInt(tnString) : 0;

        return returnValue;
    }

    /**
     * @return
     */
    public EditText getTnEditText() {
        return tnText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * l5r.toolbox.common.CommonInterface#setRollMods(l5r.toolbox.common.RollMods
     * )
     */
    public void setRollMods(RollMods rollMods) {
        this.rollMods = rollMods;
    }

    /*
     * (non-Javadoc)
     * 
     * @see l5r.toolbox.common.CommonInterface#getRollMods()
     */
    public RollMods getRollMods() {
        if (rollModFragment != null) {
            rollMods = rollModFragment.getRollMods();
        }
        return rollMods;
    }

    /**
     * @return the rollModFragment
     */
    public RollModFragment getRollModFragment() {
        return rollModFragment;
    }

    @Override
    public void onRollerCreated(EditorData data, boolean quick) {
        if (!quick) {
            if (data.getId() == -1) {
                rollerManager.addWithId(data);
                addRow(data.getTitle(), data.getId());
                // Always scroll to the end when we create a new roller
                rollerScrollView.post(new Runnable() {
                    public void run() {
                        rollerScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            } else {
                rollerManager.modify(data);
                populateRollers();
            }

            rollerManager.saveRollers(getSherlockActivity());
            parentActivity.rollManagerChanged();
        } else {
            simulate(data);
        }
    }
}
