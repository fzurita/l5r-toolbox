package l5r.toolbox.roller;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;

import l5r.toolbox.L5RToolboxActivity;
import l5r.toolbox.R;
import l5r.toolbox.common.CommonInterface;
import l5r.toolbox.common.GuiUtils;
import l5r.toolbox.editor.EditorData;
import l5r.toolbox.editor.EditorDialogFragment;
import l5r.toolbox.rollmods.RollModDialogFragment;
import l5r.toolbox.rollmods.RollModFragment;
import l5r.toolbox.rollmods.RollMods;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.actionbarsherlock.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Francisco
 * 
 */
public class RollerFragment extends SherlockFragment implements CommonInterface {

    private TableLayout tableLayout = null;
    private TextView rollerOutput = null;
    private TableRow currentlySelectedRow = null;
    private ScrollView outputScrollView = null;
    private ScrollView rollerScrollView = null;
    public static final int ROLLER_ACTIVITY = 0;
    private RollMods rollMods;
    private L5RToolboxActivity parentActivity = null;
    private RollerManager rollerManager = null;
    private String tag = null;
    private RollModFragment rollModFragment = null;
    private SlidingDrawer slidingDrawer = null;
    private boolean slidingDrawerOpen = true;
    private static final String dialogTag = "dialog";
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
        return inflater.inflate(R.layout.roller, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.roll_actionbar, menu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tableLayout = (TableLayout) getSherlockActivity().findViewById(R.id.RollTable);
        rollerOutput = (TextView) getSherlockActivity().findViewById(R.id.RollerOutput);
        outputScrollView = (ScrollView) getSherlockActivity().findViewById(R.id.OutputScroll);
        rollerScrollView = (ScrollView) getSherlockActivity().findViewById(R.id.RollScroll);
        rollModFragment = (RollModFragment) getFragmentManager().findFragmentById(R.id.RollModFragment);
        tag = getSherlockActivity().getResources().getString(R.string.roller_tag);
        slidingDrawer = (SlidingDrawer) getSherlockActivity().findViewById(R.id.SlidingDrawer);

        initGui(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

        switch (item.getItemId()) {
        case R.id.menu_new:
            removePreviousDialog();

            if (!getSherlockActivity().isFinishing()) {
                EditorDialogFragment editorDialog = EditorDialogFragment.newInstance(new EditorData(-1, null, -1, -1,
                        10, false, 0, false, true), tag, false);
                editorDialog.show(getFragmentManager(), dialogTag);
            }
            return true;
        case R.id.menu_quick:
            removePreviousDialog();

            if (!getSherlockActivity().isFinishing()) {
                EditorDialogFragment editorDialog = EditorDialogFragment.newInstance(new EditorData(-1, null, -1, -1,
                        10, false, 0, false, true), tag, true);
                editorDialog.show(getFragmentManager(), dialogTag);
            }
            return true;
        case R.id.menu_mods:
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

    /**
     * 
     */
    private void initGui(Bundle savedInstanceState) {

        rollMods = new RollMods(0, 0, 0, 0, 0, 0, false, false, true, false);

        if (slidingDrawer != null) {
            slidingDrawer.open();
        }

        parentActivity = (L5RToolboxActivity) getSherlockActivity();
        rollerManager = L5RToolboxActivity.getRollerManager();
        populateRollers();

        if (savedInstanceState != null) {
            restoreStateData(savedInstanceState);
        } else {
            // Add additional filler at the beginning
            for (int index = 0; index < OUTPUT_FILLER; index++) {
                addOutput("");
            }
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
        inflater.inflate(R.menu.roller_context_menu, menu);
    }

    public void roll(EditorData data) {
        Roller tempRoller = new Roller(getRollMods(), data, parentActivity.getCurrentWoundPenalties());
        tempRoller.roll(true);
        addOutput(tempRoller.getResultString());

        if (rollModFragment != null) {
            rollModFragment.hideKeyboardFromFragment();
            rollModFragment.clearDataIfApplicable();
        }

        if (!rollMods.isSave()) {
            rollMods = new RollMods(0, 0, 0, 0, 0, 0, false, false, true, false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        boolean returnValue = true;

        int id = 0;
        if (currentlySelectedRow != null) {
            id = currentlySelectedRow.getId();
        }

        // check selected menu item
        switch (item.getItemId()) {
        case R.id.roller_menu_roll:
            roll(rollerManager.getWithId(id));
            break;
        case R.id.roller_menu_delete:
            rollerManager.deleteWithId(id);
            populateRollers();
            rollerManager.saveRollers(getSherlockActivity());
            parentActivity.rollManagerChanged();
            break;
        case R.id.roller_menu_edit:
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * l5r.toolbox.common.CommonInterface#onRollerCreated(l5r.toolbox.editor
     * .EditorData, boolean)
     */
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
            roll(data);
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
        savedInstanceState.putParcelable("mods", rollMods);

        int rollerScrollPosition = rollerScrollView.getScrollY();
        int outputScrollPosition = outputScrollView.getScrollY();

        if (slidingDrawer != null) {
            slidingDrawerOpen = slidingDrawer.isOpened();
        }

        savedInstanceState.putInt("rollerScrollPosition", rollerScrollPosition);
        savedInstanceState.putInt("outputScrollPosition", outputScrollPosition);
        savedInstanceState.putBoolean("drawerOpen", slidingDrawerOpen);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * @param savedInstanceState
     */
    public void restoreStateData(Bundle savedInstanceState) {

        // Restore UI
        rollerOutput.setText(savedInstanceState.getString("rollerOutput"));

        // Restore RollMods
        rollMods = savedInstanceState.getParcelable("mods");

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
    }

    /**
     * @param text
     * @param action
     * @param id
     */
    public void addRow(String text, int id) {

        View.OnClickListener action = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                roll(rollerManager.getWithId(id));
            }
        };

        // Create row to be added
        TableRow tableRow = GuiUtils.generateRow(tableLayout, text, "Roll", action, getSherlockActivity());

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

    /**
     * 
     */
    public void updateRollers() {
        rollerManager = L5RToolboxActivity.getRollerManager();
        populateRollers();
    }
}
