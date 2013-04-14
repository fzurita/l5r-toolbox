package l5r.toolbox.roller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l5r.toolbox.editor.EditorData;
import l5r.toolbox.rollmods.RollMods;

/**
 * @author Francisco
 * 
 */
public class Roller {

    private String title = null;
    private int roll = 0;
    private int keep = 0;
    private int explode = 0;
    private boolean emphasis = false;
    private boolean explodeOnlyOnce = false;
    private int bonus = 0;
    private int finalRoll = 0;
    private int finalKeep = 0;
    private int finalBonus = 0;
    private int savedFinalRoll = 0;
    private int savedFinalKeep = 0;
    private int savedFinalBonus = 0;
    private boolean isAffectedByWoundPenalties = false;
    private int woundPenalty = 0;
    private RollMods rollMods = null;
    private String resultString = null;

    private static final int MAX_DICE_RESULT = 10;
    private static final int MAX_DICE = 10;
    private static final int MIN_DICE_RESULT = 1;
    private static final int FA_ROLL = 2;
    private static final int FA_KEEP = 1;

    /**
     * @param rollerActivity
     * @param title
     * @param roll
     * @param keep
     * @param explode
     * @param emphasis
     */
    public Roller(RollMods rollMods, EditorData data, int woundPenalty) {
        this.title = data.getTitle();
        this.roll = data.getRoll();
        this.keep = data.getKeep();
        this.explode = data.getExplode();
        this.emphasis = data.isEmphasis();
        this.explodeOnlyOnce = data.isExplodeOnlyOnce();
        this.bonus = data.getBonus();
        this.isAffectedByWoundPenalties = data.isAffectedByWoundPenalty();
        this.rollMods = rollMods;
        this.woundPenalty = woundPenalty;

        calculateFinalRoll();
    }

    /**
     * Random number between MIN and MAX
     * 
     * @return
     */
    private Integer randomNumber() {
        return MIN_DICE_RESULT + (int) (Math.random() * ((MAX_DICE_RESULT - MIN_DICE_RESULT) + 1));
    }

    /**
     * Rolls one dice and re-rolls if there are explosions
     * 
     * @return
     */
    private ArrayList<Integer> rollOneDiceUntilTheEnd() {
        ArrayList<Integer> diceResults = new ArrayList<Integer>();

        int singleResult = 0;
        int rollCount = 0;

        do {
            singleResult = randomNumber();

            // If we have emphasis, we try again if we get a one
            // and keep the second result
            if (rollCount == 0 && emphasis && singleResult == 1) {
                singleResult = randomNumber();
            }
            diceResults.add(singleResult);
            rollCount++;
        } while (singleResult >= explode && explode != 0 && (rollCount <= 1 || !explodeOnlyOnce));

        return diceResults;
    }

    /**
     * Roll the dice
     * @return
     */
    private ArrayList<ArrayList<Integer>> rollAllDice() {
        ArrayList<ArrayList<Integer>> results = new ArrayList<ArrayList<Integer>>();

        for (int index = 0; index < finalRoll; index++) {
            results.add(rollOneDiceUntilTheEnd());
        }
        return results;
    }

    
    /**
     * Adds up the results of a dice
     * @param rolls
     * @return
     */
    private Integer consolidateRoll(ArrayList<Integer> rolls) {
        Integer total = 0;

        for (Integer roll : rolls) {
            total += roll;
        }
        return total;
    }

    
    /**
     * Adds up the results of all dice
     * @param results
     * @return
     */
    private ArrayList<Integer> consolidateRolls(ArrayList<ArrayList<Integer>> results) {
        ArrayList<Integer> consolidatedResults = new ArrayList<Integer>();

        for (ArrayList<Integer> roll : results) {
            consolidatedResults.add(consolidateRoll(roll));
        }
        return consolidatedResults;
    }

    
    /**
     * Pick the highest/lowest rolls from a set of results
     * @param consolidatedRolls
     * @return
     */
    private ArrayList<Integer> pickHighest(ArrayList<Integer> consolidatedRolls) {

        Collections.sort(consolidatedRolls);

        List<Integer> highestResults = null;

        boolean keepHighest = rollMods.isKeepHighest();

        if (keepHighest) {
            highestResults = consolidatedRolls.subList(finalRoll - finalKeep, consolidatedRolls.size());
        } else {
            highestResults = consolidatedRolls.subList(0, finalKeep);
        }

        ArrayList<Integer> highestResultsArray = new ArrayList<Integer>();
        highestResultsArray.addAll(highestResults);
        return highestResultsArray;
    }

    
    /**
     * Prints out dice results
     * @param results
     * @return
     */
    private String printResults(ArrayList<ArrayList<Integer>> results) {
        String resultString = new String();

        for (ArrayList<Integer> oneDiceResults : results) {
            resultString += "[";
            for (int index = 0; index < oneDiceResults.size(); index++) {
                Integer oneResult = oneDiceResults.get(index);
                resultString += Integer.toString(oneResult);

                if (index != oneDiceResults.size() - 1) {
                    resultString += ",";
                }
            }
            resultString += "]";
        }

        return resultString;
    }
    
    /**
     * Calculate the final roll
     * @param mods
     */
    private void calculateFinalRoll(){
        finalRoll = roll;
        finalKeep = keep;
        finalBonus = bonus;

        //Add full attack bonus if any
        if (rollMods.isFullAttack()) {
            finalRoll += FA_ROLL;
            finalKeep += FA_KEEP;
        }

        //Add void point bonus
        if (rollMods.isVpBonus()) {
            finalRoll += 1;
            finalKeep += 1;
        }

        //Substract roll penalty
        finalRoll -= rollMods.getRollPenalty();
        finalKeep -= rollMods.getKeepPenalty();
        finalBonus -= rollMods.getStaticPenalty() + (isAffectedByWoundPenalties?woundPenalty:0);
        
        //Add roll bonus
        finalRoll += rollMods.getRollBonus();
        finalKeep += rollMods.getKeepBonus();
        finalBonus += rollMods.getStaticBonus();
        
        //Save final roll before doing conversions
        savedFinalRoll = finalRoll;
        savedFinalKeep = finalKeep;
        savedFinalBonus = finalBonus;

        // Figure out any roll to keep conversion
        if (finalRoll > MAX_DICE && finalKeep < MAX_DICE) {
            int additionalDice = finalRoll - MAX_DICE;
            int maxAddedToKeep = MAX_DICE - finalKeep;
            int maxSubstraction = maxAddedToKeep * 2;

            if (additionalDice > maxSubstraction) {
                finalRoll = MAX_DICE + (additionalDice - maxSubstraction);
                finalKeep += maxAddedToKeep;
            } else {
                int maxAddToKeep = (int) Math.floor(additionalDice / 2);
                finalRoll -= maxAddToKeep * 2;
                finalKeep += maxAddToKeep;
            }
        }

        // Figure out any additional bonuses if there are any
        if (finalRoll >= MAX_DICE && finalKeep >= MAX_DICE) {
            int additionalRollDice = finalRoll - MAX_DICE;
            int additionalKeepDice = finalKeep - MAX_DICE;
            finalBonus += additionalRollDice * 2;
            finalBonus += additionalKeepDice * 2;
        }

        // Don't allow greater than 10
        finalRoll = finalRoll > MAX_DICE ? MAX_DICE : finalRoll;
        finalKeep = finalKeep > MAX_DICE ? MAX_DICE : finalKeep;

        // Don't allow less than 0
        finalRoll = finalRoll < 0 ? 0 : finalRoll;
        finalKeep = finalKeep < 0 ? 0 : finalKeep;

        // Don't let the keep dice be more than the roll dice
        if (finalRoll < finalKeep) {
            finalKeep = finalRoll;
        }
    }
    
    /**
     * Get roll data in string form
     * @return
     */
    public String getRollData(){
        String output = new String();
        
        output += "Roll: " + roll + "k" + keep + " + " + bonus + " | Ex: " + explode + " | Ex1: "
                + (explodeOnlyOnce ? "Y" : "N") + " | Em: " + (emphasis ? "Y" : "N") + "\n";
        output += "Penalty: " + rollMods.getRollPenalty() + "k" + rollMods.getKeepPenalty() + " - "
                + (rollMods.getStaticPenalty() + (isAffectedByWoundPenalties?woundPenalty:0)) + "\n";
        output += "Bonus: " + rollMods.getRollBonus() + "k" + rollMods.getKeepBonus() + " + "
                + rollMods.getStaticBonus() + " | FA: " + (rollMods.isFullAttack() ? "Y" : "N") + " | VP: "
                + (rollMods.isVpBonus() ? "Y" : "N") + " | K: " + (rollMods.isKeepHighest() ? "H" : "L") + "\n";

        String saveFinalBonusSign = savedFinalBonus < 0 ? " - " : " + ";
        String finalBonusSign = finalBonus < 0 ? " - " : " + ";
        if (finalKeep != keep || finalRoll != roll || finalBonus != bonus) {
            output += "Modded Roll: " + savedFinalRoll + "k" + savedFinalKeep + saveFinalBonusSign
                    + Math.abs(savedFinalBonus) + " OR " + finalRoll + "k" + finalKeep + finalBonusSign
                    + Math.abs(finalBonus) + "\n";
        }
        
        return output;
    }

    /**
	 * Rolls the dice
	 */
    public int roll(boolean shouldOutput) {

        ArrayList<ArrayList<Integer>> results = rollAllDice();
        ArrayList<Integer> consolidatedRolls = consolidateRolls(results);
        ArrayList<Integer> highestRolls = pickHighest(consolidatedRolls);
        Integer consolidatedHighest = consolidateRoll(highestRolls) + finalBonus;

        if (shouldOutput) {
            resultString = new String();
            resultString += "***" + title + "***\n";
            resultString += "Result: " + consolidatedHighest + "\n";
            resultString += getRollData();
            resultString += "Dice: " + printResults(results) + "\n";
            resultString += "-----------------------------------------\n";
        }

        return consolidatedHighest;
    }

    /**
     * @return the resultString
     */
    public String getResultString() {
        return resultString;
    }

    /**
     * @return
     */
    public String getTitle() {
        return title;
    }
}
