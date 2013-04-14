/**
 * 
 */
package l5r.toolbox.profile;

import com.actionbarsherlock.app.SherlockDialogFragment;

import l5r.toolbox.L5RToolboxActivity;
import l5r.toolbox.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import l5r.toolbox.common.ProfilesArrayAdapter;

/**
 * @author Francisco
 * 
 */
public class ProfileDialogFragment extends SherlockDialogFragment {

    private Spinner profileSpinner = null;
    private Button okButton = null;
    private Button cancelButton = null;
    private Button newButton = null;
    private Button deleteButton = null;
    private EditText newProfileName = null;
    private Button newProfileOkButton = null;
    private ProfileManager profileManager = null;
    private ProfilesArrayAdapter adapter = null;

    private TextView newProfileTitle = null;
    private LinearLayout newProfileLayout = null;

    private static final String dialogTag = "dialog";

    private View dialogView = null;

    public static ProfileDialogFragment newInstance() {
        ProfileDialogFragment profileDialog = new ProfileDialogFragment();
        return profileDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileManager = L5RToolboxActivity.getProfileManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Profiles");

        dialogView = inflater.inflate(R.layout.profiles, container, false);

        profileSpinner = (Spinner) dialogView.findViewById(R.id.profile_spinner);

        adapter = new ProfilesArrayAdapter(getSherlockActivity());
        for(ProfileData data: profileManager){
            adapter.add(data);
        }

        profileSpinner.setAdapter(adapter);

        int currentProfileId = profileManager.getCurrentProfileId();
        ProfileData data = profileManager.getWithId(currentProfileId);
        int position = adapter.getPosition(data);
        profileSpinner.setSelection(position, false);

        // New profile layout
        newProfileName = (EditText) dialogView.findViewById(R.id.new_profile_title_text);
        newProfileOkButton = (Button) dialogView.findViewById(R.id.new_profile_button_ok);
        newProfileTitle = (TextView) dialogView.findViewById(R.id.new_profile_title);
        newProfileLayout = (LinearLayout) dialogView.findViewById(R.id.new_layout);

        // Buttons
        cancelButton = (Button) dialogView.findViewById(R.id.button_cancel);
        okButton = (Button) dialogView.findViewById(R.id.button_ok);
        newButton = (Button) dialogView.findViewById(R.id.button_new);
        deleteButton = (Button) dialogView.findViewById(R.id.button_delete);
        
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {

            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
                    return true; // Pretend we processed it
                }
                return false; // Any other keys are still processed as
                              // normal
            }
        });

        // Cancel listener
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();

                hideKeyboard();
            }
        });

        // Ok listener
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();

                ProfileData data = (ProfileData) profileSpinner.getSelectedItem();
                L5RToolboxActivity parentActivity = (L5RToolboxActivity) getSherlockActivity();
                parentActivity.setCurrentProfile(data.getId());
                hideKeyboard();
            }
        });

        // New listener
        newButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newProfileTitle.setVisibility(View.VISIBLE);
                newProfileLayout.setVisibility(View.VISIBLE);

                hideKeyboard();
            }
        });

        // delete listener
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProfileData data = (ProfileData) profileSpinner.getSelectedItem();

                if (data.getId() != 0) {
                    //Delete data depending on results of dialog
                    warningDialog(data);
                }

                hideKeyboard();
            }
        });

        // new profile ok button listener
        newProfileOkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                
                L5RToolboxActivity parentActivity = (L5RToolboxActivity) getSherlockActivity();
                parentActivity.addProfile(new ProfileData(-1, newProfileName.getText().toString()));
                
                ProfileData data = profileManager.get(profileManager.size() - 1);
                adapter.add(data);
                profileSpinner.setSelection(adapter.getCount() - 1, false);
                newProfileName.setText("");
                newProfileTitle.setVisibility(View.GONE);
                newProfileLayout.setVisibility(View.GONE);

                hideKeyboard();
            }
        });

        return dialogView;
    }

    @Override
    public void onStart() {
        super.onStart();

        ViewGroup parentView = (ViewGroup) dialogView.getParent();
        parentView.setPadding(0, 0, 0, 0);
        
        float dip = getSherlockActivity().getResources().getDisplayMetrics().density;
        LayoutParams params = getDialog().getWindow().getAttributes();
        int dialogWidth = getSherlockActivity().getResources().getInteger(R.integer.dialog_width);
        params.width = (int) (dip * dialogWidth);
        getDialog().getWindow().setAttributes(params);
    }

    /**
     * 
     */
    private void hideKeyboard() {
        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSherlockActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newProfileName.getWindowToken(), 0);
    }

    /**
     * @return
     */
    private void warningDialog(final ProfileData data) {

        String title = "Are you sure you want to delete " + data.getTitle() + "?";
        String message = "Are you sure you want to delete this profile? This will permanently remove all rolls and health data associated with this profile.";

        Builder alertDialogBuilder = new AlertDialog.Builder(getSherlockActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
        alertDialogBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                
                //Remove then show dialog again
                adapter.remove(data);
                L5RToolboxActivity parentActivity = (L5RToolboxActivity) getSherlockActivity();
                parentActivity.deleteProfileId(data.getId());
            }
        });
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.DialogFragment#onDismiss(android.content.
     * DialogInterface)
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        L5RToolboxActivity activity = (L5RToolboxActivity) getSherlockActivity();
        FragmentManager fragManager = getFragmentManager();

        if (activity != null && fragManager != null) {
            removePreviousDialog();
        }
    }

    private void removePreviousDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSherlockActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getSherlockActivity().getSupportFragmentManager().findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.commitAllowingStateLoss();
    }
}
