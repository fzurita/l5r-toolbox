/**
 * 
 */
package l5r.toolbox.rollmods;

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
import android.widget.RadioButton;

/**
 * @author Francisco
 * 
 */
public class RollModDialogFragment extends SherlockDialogFragment {

    private String fragmentTag = null;
    private EditText rollPenalty = null;
    private EditText keepPenalty = null;
    private EditText staticPenalty = null;
    private EditText rollBonus = null;
    private EditText keepBonus = null;
    private EditText staticBonus = null;
    private CheckBox fullAttackCheckBox = null;
    private CheckBox useVpCheckBox = null;
    private CheckBox saveCheckBox = null;
    private RadioButton highestRadioButton = null;
    private RadioButton lowestRadioButton = null;
    private View dialogView = null;

    public static final String ROLL_MODS_KEY = "roll_mods";
    public static final String TAG_KEY = "tag";
    private static final String dialogTag = "dialog";

    private boolean canceled = false;

    public static RollModDialogFragment newInstance(RollMods rollMods, String fragmentTag) {
        RollModDialogFragment rollModDialog = new RollModDialogFragment();

        // Supply input arguments.
        Bundle args = new Bundle();
        args.putParcelable(ROLL_MODS_KEY, rollMods);
        args.putString(TAG_KEY, fragmentTag);
        rollModDialog.setArguments(args);

        return rollModDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        RollMods rollMods = getArguments().getParcelable(ROLL_MODS_KEY);
        fragmentTag = getArguments().getString(TAG_KEY);

        LayoutInflater factory = LayoutInflater.from(getSherlockActivity());
        dialogView = factory.inflate(R.layout.temp_mods, null);

        rollPenalty = (EditText) dialogView.findViewById(R.id.RollPenaltyText);
        keepPenalty = (EditText) dialogView.findViewById(R.id.KeepPenaltyText);
        staticPenalty = (EditText) dialogView.findViewById(R.id.StaticPenaltyText);
        rollBonus = (EditText) dialogView.findViewById(R.id.RollBonusText);
        keepBonus = (EditText) dialogView.findViewById(R.id.KeepBonusText);
        staticBonus = (EditText) dialogView.findViewById(R.id.StaticBonusText);
        fullAttackCheckBox = (CheckBox) dialogView.findViewById(R.id.FulllAttackCheckbox);
        useVpCheckBox = (CheckBox) dialogView.findViewById(R.id.VPCheckbox);
        saveCheckBox = (CheckBox) dialogView.findViewById(R.id.SaveCheckbox);
        highestRadioButton = (RadioButton) dialogView.findViewById(R.id.KeepHighestRadio);
        lowestRadioButton = (RadioButton) dialogView.findViewById(R.id.KeepLowestRadio);

        canceled = false;

        Dialog tempAlertDialog = buildAlertDialog();
        tempAlertDialog.setCanceledOnTouchOutside(false);

        setDialogValues(rollMods);

        return tempAlertDialog;
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
     * Sets the value of an EditText to blank if the given value is -1 otherwise
     * it will use the given value.
     * 
     * @param textEdit
     * @param value
     */
    private void setEditText(EditText editText, int value) {
        if (value == 0) {
            editText.setText("");
        } else {
            editText.setText(Integer.toString(value));
        }
    }

    /**
     * 
     */
    private void setDialogValues(RollMods rollMods) {

        if (rollMods != null) {

            setEditText(rollPenalty, rollMods.getRollPenalty());
            setEditText(keepPenalty, rollMods.getKeepPenalty());
            setEditText(staticPenalty, rollMods.getStaticPenalty());
            setEditText(rollBonus, rollMods.getRollBonus());
            setEditText(keepBonus, rollMods.getKeepBonus());
            setEditText(staticBonus, rollMods.getStaticBonus());

            fullAttackCheckBox.setChecked(rollMods.isFullAttack());
            useVpCheckBox.setChecked(rollMods.isVpBonus());
            saveCheckBox.setChecked(rollMods.isSave());

            highestRadioButton.setChecked(rollMods.isKeepHighest());
            lowestRadioButton.setChecked(!rollMods.isKeepHighest());

        }
    }

    /**
     * @param activity
     * @return
     */
    private Dialog buildAlertDialog() {
        return new AlertDialog.Builder(getSherlockActivity()).setTitle("Select Modifications").setView(dialogView)
                .setNeutralButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        canceled = true;
                    }
                }).create();
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

            if (!canceled) {

                String rollPenaltyText = rollPenalty.getText().toString();
                String keepPenaltyText = keepPenalty.getText().toString();
                String staticPenaltyText = staticPenalty.getText().toString();
                String rollBonusText = rollBonus.getText().toString();
                String keepBonusText = keepBonus.getText().toString();
                String staticBonusText = staticBonus.getText().toString();
                boolean fullAttack = fullAttackCheckBox.isChecked();
                boolean useVpBonus = useVpCheckBox.isChecked();
                boolean keepHighest = highestRadioButton.isChecked();
                boolean save = saveCheckBox.isChecked();

                int rollPenalty = 0;
                int keepPenalty = 0;
                int staticPenalty = 0;
                int rollBonus = 0;
                int keepBonus = 0;
                int staticBonus = 0;

                // We want to keep all the valid fields when we show this dialog
                // again after there is an error
                if (rollPenaltyText.length() != 0) {
                    rollPenalty = Integer.parseInt(rollPenaltyText);
                }
                if (keepPenaltyText.length() != 0) {
                    keepPenalty = Integer.parseInt(keepPenaltyText);
                }
                if (staticPenaltyText.length() != 0) {
                    staticPenalty = Integer.parseInt(staticPenaltyText);
                }
                if (rollBonusText.length() != 0) {
                    rollBonus = Integer.parseInt(rollBonusText);
                }
                if (keepBonusText.length() != 0) {
                    keepBonus = Integer.parseInt(keepBonusText);
                }
                if (staticBonusText.length() != 0) {
                    staticBonus = Integer.parseInt(staticBonusText);
                }

                CommonInterface commonInterface = (CommonInterface) activity.getTabFragment(fragmentTag);
                commonInterface.getRollMods().setRollPenalty(rollPenalty);
                commonInterface.getRollMods().setKeepPenalty(keepPenalty);
                commonInterface.getRollMods().setStaticPenalty(staticPenalty);
                commonInterface.getRollMods().setRollBonus(rollBonus);
                commonInterface.getRollMods().setKeepBonus(keepBonus);
                commonInterface.getRollMods().setStaticBonus(staticBonus);
                commonInterface.getRollMods().setFullAttack(fullAttack);
                commonInterface.getRollMods().setVpBonus(useVpBonus);
                commonInterface.getRollMods().setKeepHighest(keepHighest);
                commonInterface.getRollMods().setSave(save);
            }

            // Remove soft keyboard
            InputMethodManager imm = (InputMethodManager) getSherlockActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rollPenalty.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(keepPenalty.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(staticPenalty.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(rollBonus.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(keepBonus.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(staticBonus.getWindowToken(), 0);

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

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.DialogFragment#onCancel(android.content.
     * DialogInterface)
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        canceled = true;
    }
}
