package l5r.toolbox.health;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

public class HealthTracker implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 210177965346323295L;
    
    
    private int healthPerLevel = 0;
    private int healthAtHealthy = 0;
    private int damageTaken = 0;
    private int maxHealth = 0;
    private int currentHealthLevel = 0;
    private int currentHealthValue = 0;
    private ArrayList<Integer> healthPenalties = null;
    private String filename = null;
    
    private static final int HEALTH_LEVELS = 7;
    
    public HealthTracker(String filename){
        this.filename = filename;
        
        healthPenalties = new ArrayList<Integer>();
        healthPenalties.ensureCapacity(HEALTH_LEVELS + 1);
        
        for(int index = 0; index <= HEALTH_LEVELS; index++){
            healthPenalties.add(0);
        }
    }
    
    private void recalculateMaxHealth(){
        maxHealth = healthAtHealthy + HEALTH_LEVELS*healthPerLevel;
        
        if(damageTaken > maxHealth){
            damageTaken = maxHealth;
        }
    }
    
    /**
     * @param healthPerLevel the healthPerLevel to set
     */
    public void setHealthPerLevel(int healthPerLevel) {
        this.healthPerLevel = healthPerLevel;
        recalculateMaxHealth();
        recalculateHealthLevelAndValue();
    }

    /**
     * @param healthAtHealthy the healthAtHealthy to set
     */
    public void setHealthAtHealthy(int healthAtHealthy) {
        this.healthAtHealthy = healthAtHealthy;
        recalculateMaxHealth();
        recalculateHealthLevelAndValue();
    }
    
    /**
     * Adds the given amount of health
     * @param health health to add
     */
    public void addHealth(int health){
        damageTaken -= health;
        
        if(damageTaken < 0){
            damageTaken = 0;
        }
        
        recalculateHealthLevelAndValue();
    }
    
    /**
     * Subtracts the given amount of health
     * @param health health to subtract
     */
    public void subtractHealth(int health){
        damageTaken += health;
        
        if(damageTaken > maxHealth){
            damageTaken = maxHealth;
        }
        
        recalculateHealthLevelAndValue();
    }
    
    private void recalculateHealthLevelAndValue(){
        //Calculate health level
        
        int currentHealth = maxHealth - damageTaken;
        
        //We don't want to divide by zero, this happens when initially
        //setting health per level value
        if(healthPerLevel != 0){
            currentHealthLevel = currentHealth/healthPerLevel;
            
            if(currentHealthLevel > HEALTH_LEVELS){
                currentHealthLevel = HEALTH_LEVELS;
            }
            
            //Calculate health value for the current level
            //Note that we can't use modulus because the healthy level has more health
            //than the other levels
            currentHealthValue = currentHealth - healthPerLevel*currentHealthLevel;   
        }else{
            currentHealthLevel = 0;
            currentHealthValue = 0;
        }
    }

    /**
     * @return the currentHealthLevel
     */
    public int getCurrentHealthLevel() {
        return currentHealthLevel;
    }

    /**
     * @return the currentHealthValue at the current level
     */
    public int getCurrentHealthValue() {
        return currentHealthValue;
    }

    /**
     * @return the healthPerLevel
     */
    public int getHealthPerLevel() {
        return healthPerLevel;
    }

    /**
     * @return the healthAtHealthy
     */
    public int getHealthAtHealthy() {
        return healthAtHealthy;
    }

    /**
     * @return the damageTaken
     */
    public int getDamageTaken() {
        return damageTaken;
    }
    
    /**
     * Sets the health penalty at the given level
     * @param level
     * @param penalty
     */
    public void setHealthPenalty(int level, int penalty){
        healthPenalties.add(level, penalty);
    }
    
    /**
     * Gets the health penalty at the given level
     * @param level
     * @return
     */
    public int getHealthPenalty(int level){
        return healthPenalties.get(level);
    }
    
    /**
     * Gets the health penalty at the current level
     * @return
     */
    public int getCurrentWoundPenalty(){
        return healthPenalties.get(currentHealthLevel);
    }

    /**
     * Saves all health data to local storage
     */
    public void saveHealthData(Context context){
        try {
            //Save rollers
            FileOutputStream fos = context.openFileOutput(filename,
                    Context.MODE_PRIVATE);
            ObjectOutputStream os;
            os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads health data from local storage
     */
    public void loadHealthData(Context context){
        try {           
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            HealthTracker healthTracker = (HealthTracker) is.readObject();
            this.healthPerLevel = healthTracker.healthPerLevel;
            this.healthAtHealthy = healthTracker.healthAtHealthy;
            this.damageTaken = healthTracker.damageTaken;
            this.maxHealth = healthTracker.maxHealth;
            this.currentHealthLevel = healthTracker.currentHealthLevel;
            this.currentHealthValue = healthTracker.currentHealthValue;
            this.healthPenalties = healthTracker.healthPenalties;
            is.close();
        } catch (IOException e) {
            //File not found... do nothing
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
