/**
 * 
 */
package l5r.toolbox.editor;

import com.actionbarsherlock.app.SherlockDialogFragment;

import l5r.toolbox.L5RToolboxActivity;
import l5r.toolbox.R;
import l5r.toolbox.common.CommonInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Francisco
 * 
 */
public class EditorDialogFragment extends SherlockDialogFragment {

    private EditText titleText = null;
    private EditText rollText = null;
    private EditText keepText = null;
    private EditText explodeText = null;
    private EditText bonusText = null;
    private CheckBox emphasisCheckBox = null;
    private CheckBox allowOnlyOneExplosionCheckBox = null;
    private CheckBox affectedByHealthPenalty = null;
    private int id = -1;
    private String fragmentTag = null;
    private boolean quick = false;
    private View dialogView = null;

    public static final String EDITOR_DATA_KEY = "editor_data";
    public static final String TAG_KEY = "tag";
    public static final String QUICK_KEY = "quick";
    private static final String dialogTag = "dialog";

    private Boolean canceled = false;

    public static EditorDialogFragment newInstance(EditorData data, String fragmentTag, boolean quick) {
        EditorDialogFragment editorDialog = new EditorDialogFragment();

        // Supply input arguments.
        Bundle args = new Bundle();
        args.putParcelable(EDITOR_DATA_KEY, data);
        args.putString(TAG_KEY, fragmentTag);
        args.putBoolean(QUICK_KEY, quick);
        editorDialog.setArguments(args);

        return editorDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        EditorData data = getArguments().getParcelable(EDITOR_DATA_KEY);
        fragmentTag = getArguments().getString(TAG_KEY);
        quick = getArguments().getBoolean(QUICK_KEY);

        LayoutInflater factory = LayoutInflater.from(getSherlockActivity());
        dialogView = factory.inflate(R.layout.editor, null);

        titleText = (EditText) dialogView.findViewById(R.id.TitleText);
        rollText = (EditText) dialogView.findViewById(R.id.RollText);
        keepText = (EditText) dialogView.findViewById(R.id.KeepText);
        explodeText = (EditText) dialogView.findViewById(R.id.ExplodeText);
        bonusText = (EditText) dialogView.findViewById(R.id.BonusText);
        emphasisCheckBox = (CheckBox) dialogView.findViewById(R.id.EmphasisCheckbox);
        allowOnlyOneExplosionCheckBox = (CheckBox) dialogView.findViewById(R.id.AllowOnlyOneExplosionCheckBox);
        affectedByHealthPenalty = (CheckBox) dialogView.findViewById(R.id.affected_by_wound_penalties);

        canceled = false;

        Dialog tempAlertDialog = buildAlertDialog();
        tempAlertDialog.setCanceledOnTouchOutside(false);

        setDialogValues(data);

        return tempAlertDialog;
    }

    /**
     * @param data
     * 
     */
    private void setDialogValues(EditorData data) {

        if (data != null) {
            id = data.getId();

            if (!quick) {
                titleText.setText(data.getTitle());
            } else {
                titleText.setText(getResources().getString(R.string.roller_quick));
                titleText.setEnabled(false);
                titleText.setKeyListener(null);
            }

            // In the first 2 we check for id of -1 because we want them
            // to be cleared when this is a new roller
            if (id != -1 || data.getRoll() != -1) {
                rollText.setText(Integer.toString(data.getRoll()));
            } else {
                rollText.setText("");
            }

            if (id != -1 || data.getKeep() != -1) {
                keepText.setText(Integer.toString(data.getKeep()));
            } else {
                keepText.setText("");
            }
            
            if (data.getExplode() != -1) {
                explodeText.setText(Integer.toString(data.getExplode()));
            } else {
                explodeText.setText("");
            }

            if (data.getBonus() != -1) {
                bonusText.setText(Integer.toString(data.getBonus()));
            } else {
                explodeText.setText("");
            }

            emphasisCheckBox.setChecked(data.isEmphasis());
            allowOnlyOneExplosionCheckBox.setChecked(data.isExplodeOnlyOnce());
            affectedByHealthPenalty.setChecked(data.isAffectedByWoundPenalty());
        }
    }

    /*
     * @return
     */
    private AlertDialog buildAlertDialog() {
        String title = quick? getResources().getString(R.string.roller_quick):
                getResources().getString(R.string.editor);
        
        return new AlertDialog.Builder(getSherlockActivity()).setTitle(title).setView(dialogView)
                .setNeutralButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        canceled = true;
                    }
                }).create();
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

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.DialogFragment#onDismiss(android.content.
     * DialogInterface)
     */
    public void onDismiss(DialogInterface dialog) {

        L5RToolboxActivity activity = (L5RToolboxActivity) getSherlockActivity();
        FragmentManager fragManager = getFragmentManager();

        if (activity != null && fragManager != null) {

            if (!canceled) {
                String title = titleText.getText().toString();
                String rollTextString = rollText.getText().toString();
                String keepTextString = keepText.getText().toString();
                String explodeTextString = explodeText.getText().toString();
                String bonusTextString = bonusText.getText().toString();

                int roll = -1;
                int keep = -1;
                int explode = -1;
                int bonus = -1;
                boolean emphasis = emphasisCheckBox.isChecked();
                boolean explodeOnlyOnce = allowOnlyOneExplosionCheckBox.isChecked();
                boolean affectedByWoundPenalty = affectedByHealthPenalty.isChecked();

                boolean fieldsBlank = title.length() == 0 || rollTextString.length() == 0
                        || keepTextString.length() == 0 || explodeTextString.length() == 0
                        || bonusTextString.length() == 0;

                // Show error message for having invalid data
                if (fieldsBlank ) {
                    Toast.makeText(getSherlockActivity().getBaseContext(), "All fields must be filled.",
                            Toast.LENGTH_SHORT).show();
                }

                // We want to keep all the valid fields when we show this dialog
                // again after there is an error
                if (rollTextString.length() != 0) {
                    roll = Integer.parseInt(rollTextString);
                }
                if (keepTextString.length() != 0) {
                    keep = Integer.parseInt(keepTextString);
                }
                if (explodeTextString.length() != 0) {
                    explode = Integer.parseInt(explodeTextString);
                }
                if (bonusTextString.length() != 0) {
                    bonus = Integer.parseInt(bonusTextString);
                }

                // Create EditorData object to be passed to whoever needs it
                EditorData data = new EditorData(id, title, roll, keep, explode, emphasis, bonus, explodeOnlyOnce,
                        affectedByWoundPenalty);

                // Determine if we have a valid explode number
                boolean validExplode = (explode >= 7 && explode <= 10) || explode == 0;

                // Show error message for having invalid explode only if first
                // message wasn't shown.
                if (!fieldsBlank && !validExplode) {
                    Toast.makeText(getSherlockActivity().getBaseContext(),
                            "Explode On must be a number between 7 and 10 or 0.", Toast.LENGTH_SHORT).show();
                }

                if (!fieldsBlank && validExplode) {
                    CommonInterface commonInterface = (CommonInterface) activity.getTabFragment(fragmentTag);
                    commonInterface.onRollerCreated(data, quick);
                } else {
                    removePreviousDialog();

                    if (!getSherlockActivity().isFinishing()) {

                        EditorDialogFragment rollModDialog = EditorDialogFragment.newInstance(data, fragmentTag, quick);
                        rollModDialog.show(fragManager, dialogTag);
                    }
                }
            }

            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rollText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(keepText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(explodeText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(bonusText.getWindowToken(), 0);

            // Remove dialog from stack, so that it doesn't show again when
            // oncreate is called
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

    @Override
    public void onCancel(DialogInterface dialog) {
        canceled = true;
    }
}
