/**
 * 
 */
package l5r.toolbox.rollmods;

import l5r.toolbox.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * @author Francisco
 * 
 */
public class RollModFragment extends Fragment {

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
    private RollMods rollMods = null;
    
    public RollModFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.temp_mods, container, false);
        fragmentView.setBackgroundResource(R.drawable.fragment_color);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rollPenalty = (EditText) getView().findViewById(R.id.RollPenaltyText);
        keepPenalty = (EditText) getView().findViewById(R.id.KeepPenaltyText);
        staticPenalty = (EditText) getView().findViewById(R.id.StaticPenaltyText);
        rollBonus = (EditText) getView().findViewById(R.id.RollBonusText);
        keepBonus = (EditText) getView().findViewById(R.id.KeepBonusText);
        staticBonus = (EditText) getView().findViewById(R.id.StaticBonusText);
        fullAttackCheckBox = (CheckBox) getView().findViewById(R.id.FulllAttackCheckbox);
        useVpCheckBox = (CheckBox) getView().findViewById(R.id.VPCheckbox);
        highestRadioButton = (RadioButton) getView().findViewById(R.id.KeepHighestRadio);
        lowestRadioButton = (RadioButton) getView().findViewById(R.id.KeepLowestRadio);
        saveCheckBox = (CheckBox)getView().findViewById(R.id.SaveCheckbox);
        
        if(savedInstanceState != null){
            restoreStateData(savedInstanceState);
        }
        
        if(rollMods == null){
            rollMods = new RollMods(0, 0, 0, 0, 0, 0, false, false, true, false);
        }
        setDialogValues();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("mods", rollMods);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * @param savedInstanceState
     */
    public void restoreStateData(Bundle savedInstanceState) {
        rollMods = savedInstanceState.getParcelable("mods");
    }

    private void setEditText(EditText editText, int value){
        if(value == 0){
            editText.setText("");
        }else{
            editText.setText(Integer.toString(value));
        }
    }

    /**
     * @param rollMods
     * 
     */
    private void setDialogValues() {
        
        setEditText(rollPenalty, rollMods.getRollPenalty());
        setEditText(keepPenalty, rollMods.getKeepPenalty());
        setEditText(staticPenalty, rollMods.getStaticPenalty());
        setEditText(rollBonus, rollMods.getRollBonus());
        setEditText(keepBonus, rollMods.getKeepBonus());
        setEditText(staticBonus, rollMods.getStaticBonus());

        fullAttackCheckBox.setChecked(rollMods.isFullAttack());
        useVpCheckBox.setChecked(rollMods.isVpBonus());

        highestRadioButton.setChecked(rollMods.isKeepHighest());
        lowestRadioButton.setChecked(!rollMods.isKeepHighest());
    }
    
    /**
     * 
     */
    public void clearDataIfApplicable(){
        if(!saveCheckBox.isChecked()){
            //Update values
            rollMods = new RollMods(0, 0, 0, 0, 0, 0, false, false, true, false);
            setDialogValues();
        }
    }

    /**
     * @return
     */
    public RollMods getRollMods() {
        updateData();
        return rollMods;
    }
    
    public void hideKeyboardFromFragment(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rollPenalty.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(keepPenalty.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(staticPenalty.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(rollBonus.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(keepBonus.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(staticBonus.getWindowToken(), 0);
    }
    
    private void updateData(){
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

        rollMods = new RollMods(rollPenalty, keepPenalty, staticPenalty, rollBonus, keepBonus, staticBonus, fullAttack, useVpBonus,
                keepHighest, save);
    }
}
