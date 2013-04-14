package l5r.toolbox.health;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;

import l5r.toolbox.L5RToolboxActivity;
import l5r.toolbox.R;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

/**
 * @author Francisco
 * 
 */
public class HealthTrackerFragment extends SherlockFragment {

    private TableLayout table = null;
    private EditText healthyText = null;
    private EditText perLevelText = null;
    private EditText changeText = null;
    private Button applyButton = null;
    private Button applyPenaltiesButton = null;
    private Button damageButton = null;
    private Button healButton = null;
    private TextView output = null;
    private ScrollView outputScrollView = null;
    private CheckBox affectedByWoundPenalties = null;

    private ArrayList<TableRow> healthRows = null;
    HealthTracker healthTracker = null;

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
        return inflater.inflate(R.layout.health_tracker, container, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        table = (TableLayout) getSherlockActivity().findViewById(R.id.HealthTable);
        TableRow titleRow = (TableRow) getSherlockActivity().getLayoutInflater().inflate(R.layout.health_title_row, table,
                false);
        table.addView(titleRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        healthRows = new ArrayList<TableRow>();
        healthRows.add(generateRow(table, getResources().getString(R.string.health_out)));
        healthRows.add(generateRow(table, getResources().getString(R.string.health_down)));
        healthRows.add(generateRow(table, getResources().getString(R.string.health_crippled)));
        healthRows.add(generateRow(table, getResources().getString(R.string.health_injured)));
        healthRows.add(generateRow(table, getResources().getString(R.string.health_hurt)));
        healthRows.add(generateRow(table, getResources().getString(R.string.health_grazed)));
        healthRows.add(generateRow(table, getResources().getString(R.string.health_nicked)));
        healthRows.add(generateRow(table, getResources().getString(R.string.health_healthy)));

        for (int index = healthRows.size() - 1; index >= 0; index--) {
            table.addView(healthRows.get(index), new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }

        healthyText = (EditText) getSherlockActivity().findViewById(R.id.HealthyText);
        perLevelText = (EditText) getSherlockActivity().findViewById(R.id.PerLevelText);
        changeText = (EditText) getSherlockActivity().findViewById(R.id.HealthChangeText);
        applyButton = (Button) getSherlockActivity().findViewById(R.id.health_apply_health_change);
        applyPenaltiesButton = (Button) getSherlockActivity().findViewById(R.id.health_apply_penalties);
        damageButton = (Button) getSherlockActivity().findViewById(R.id.health_take_damage);
        healButton = (Button) getSherlockActivity().findViewById(R.id.health_heal);
        output = (TextView) getSherlockActivity().findViewById(R.id.Health_Output);
        outputScrollView = (ScrollView) getSherlockActivity().findViewById(R.id.Health_OutputScroll);
        affectedByWoundPenalties = (CheckBox) getSherlockActivity().findViewById(R.id.affected_by_wound_penalties);

        healthTracker = L5RToolboxActivity.getHealthTracker();

        initApplyButton();
        initDamageHealButtons();

        if (savedInstanceState != null) {
            restoreStateData(savedInstanceState);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();

        updateHealthData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI
        savedInstanceState.putString("healthOutput", output.getText().toString());
        int outputScrollPosition = outputScrollView.getScrollY();
        savedInstanceState.putInt("outputScrollPosition", outputScrollPosition);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * @param savedInstanceState
     */
    public void restoreStateData(Bundle savedInstanceState) {
        output.setText(savedInstanceState.getString("healthOutput"));
        // Restore scroll position
        final int outputScrollPosition = savedInstanceState.getInt("outputScrollPosition");

        outputScrollView.post(new Runnable() {
            public void run() {
                outputScrollView.scrollTo(0, outputScrollPosition);
            }
        });
    }
    
    public void updateHealthData(){
        healthTracker = L5RToolboxActivity.getHealthTracker();
        updateHealthFields();
        updateHealthLevel();
        updatePenaltyFields();
    }

    /**
     * Initializes the apply button
     */
    private void initApplyButton() {
        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Update health at healthy for the health tracker
                String healthTextValue = healthyText.getText().toString();
                if (healthTextValue.length() == 0) {
                    healthTextValue = "0";
                }
                int healthy = Integer.parseInt(healthTextValue);
                healthTracker.setHealthAtHealthy(healthy);

                // Update health per level for the health tracker
                String healthPerLevelValue = perLevelText.getText().toString();
                if (healthPerLevelValue.length() == 0) {
                    healthPerLevelValue = "0";
                }

                int perLevel = Integer.parseInt(healthPerLevelValue);
                healthTracker.setHealthPerLevel(perLevel);

                updateHealthFields();
                updateHealthLevel();
                hideKeyboard();
                generateHealthChangedOutput();
                healthTracker.saveHealthData(getSherlockActivity());
            }
        });

        applyPenaltiesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                for (int index = 0; index < healthRows.size(); index++) {
                    EditText level = (EditText) healthRows.get(index).findViewById(R.id.HealthPenalty);
                    String levelPenalty = level.getText().toString();

                    if (levelPenalty.length() != 0) {
                        int penalty = Integer.parseInt(levelPenalty);
                        healthTracker.setHealthPenalty(index, penalty);
                    }
                }
                hideKeyboard();
                healthTracker.saveHealthData(getSherlockActivity());
            }
        });
    }

    /**
     * Initializes the health and damage buttons
     */
    private void initDamageHealButtons() {
        healButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String changeTextValue = changeText.getText().toString();
                if (changeTextValue.length() != 0) {
                    int change = Integer.parseInt(changeTextValue);
                    healthTracker.addHealth(change);
                }

                updateHealthLevel();
                hideKeyboard();
                generateOutput("Healed: " + changeTextValue);
                healthTracker.saveHealthData(getSherlockActivity());
            }
        });

        damageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String changeTextValue = changeText.getText().toString();
                if (changeTextValue.length() != 0) {
                    int change = Integer.parseInt(changeTextValue);
                    healthTracker.subtractHealth(change);
                }

                updateHealthLevel();
                hideKeyboard();
                generateOutput("Took Damage: " + changeTextValue);
                healthTracker.saveHealthData(getSherlockActivity());
            }
        });
    }

    /**
     * Updates the health level and value
     */
    private void updateHealthLevel() {
        // Reset everything to nothing
        for (TableRow row : healthRows) {
            EditText healthValue = (EditText) row.findViewById(R.id.HealthWounds);
            healthValue.setText("");
        }

        // Set the current health level and value
        EditText health = (EditText) healthRows.get(healthTracker.getCurrentHealthLevel()).findViewById(
                R.id.HealthWounds);
        health.setText(Integer.toString(healthTracker.getCurrentHealthValue()));
    }

    /**
     * Update all the health fields
     */
    private void updateHealthFields() {

        String healthAtHealthy = Integer.toString(healthTracker.getHealthAtHealthy());
        String healthAtPerLevel = Integer.toString(healthTracker.getHealthPerLevel());

        // Update text fields
        TextView healthLevel = (TextView) healthRows.get(healthRows.size() - 1).findViewById(R.id.HealthLevel);
        healthLevel.setText(healthAtHealthy);

        for (int index = 0; index < healthRows.size() - 1; index++) {
            TextView level = (TextView) healthRows.get(index).findViewById(R.id.HealthLevel);
            level.setText(healthAtPerLevel);
        }
    }
    
    /**
     * Update all the penalties
     */
    private void updatePenaltyFields() {

        for (int index = 0; index < healthRows.size(); index++) {
            EditText level = (EditText) healthRows.get(index).findViewById(R.id.HealthPenalty);
            Integer penalty = healthTracker.getHealthPenalty(index);
            level.setText(penalty.toString());
        }
    }

    /**
     * Hides the keyboard
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSherlockActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(healthyText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(perLevelText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(changeText.getWindowToken(), 0);
    }

    /**
     * Generates a table row
     * 
     * @param tableLayout
     * @param text
     * @return
     */
    private TableRow generateRow(TableLayout tableLayout, String text) {
        LayoutInflater factory = getSherlockActivity().getLayoutInflater();
        TableRow tableRow = (TableRow) factory.inflate(R.layout.health_level_row, tableLayout, false);
        TextView textView = (TextView) tableRow.findViewById(R.id.HealthLabel);

        textView.setText(text);

        return tableRow;
    }

    /**
     * @param header
     */
    private void generateOutput(String header) {

        String output = header + "\n";
        String currentHealthLevel = ((TextView) healthRows.get(healthTracker.getCurrentHealthLevel()).findViewById(
                R.id.HealthLabel)).getText().toString();
        output += "Current Health Level: " + currentHealthLevel + "\n";
        output += "Current Value at Level: "
                + healthTracker.getCurrentHealthValue() + "\n";
        output += "Current Wound Penalties: "
                + healthTracker.getCurrentWoundPenalty() + "\n";
        output += "Total Damage Taken: " + healthTracker.getDamageTaken() + "\n";
        output += "-----------------------------------------\n";
        addOutput(output);
    }
    
    /**
     *
     */
    private void generateHealthChangedOutput() {

        String output = new String();
        output += "Health per level changed\n";
        output += "Health at healthy: " + healthTracker.getHealthAtHealthy() + "\n";
        output += "Health per level: " + healthTracker.getHealthPerLevel();
        generateOutput(output);
    }

    /**
     * @param text
     */
    public void addOutput(String text) {
        output.setText(text + "\n" + output.getText());

        // Always scroll to the end on screen changes
        output.post(new Runnable() {
            public void run() {
                outputScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }
    
    /**
     * @return
     */
    public boolean isApplyWoundPenalties(){        
        return affectedByWoundPenalties.isChecked();
    }
}
