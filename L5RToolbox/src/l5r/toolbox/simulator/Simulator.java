package l5r.toolbox.simulator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import l5r.toolbox.roller.Roller;

public class Simulator {

    private Roller roller;
    private SimulatorFragment simulatorFragment;
    private ProgressDialog progressDialog;
    private static final int TOTAL_ROLLS = 10000;
    private SimulatorThread progressThread = null;
    private boolean inProgress = false;

    public Simulator(SimulatorFragment fragment, Roller roller) {
        this.roller = roller;
        this.simulatorFragment = fragment;
    }

    /**
     * @param simulatorFragment
     *            the fragment to set
     */
    public void setFragment(SimulatorFragment simulatorFragment) {
        this.simulatorFragment = simulatorFragment;

        if (inProgress) {
            progressDialog = ProgressDialog.show(simulatorFragment.getSherlockActivity(), "Please wait", "Simulating...", true,
                    false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
                        return true; // Pretend we processed it
                    }
                    return false; // Any other keys are still processed as
                                  // normal
                }
            });
        }
    }

    private ArrayList<Integer> simulateRolls() {
        ArrayList<Integer> results = new ArrayList<Integer>();
        results.ensureCapacity(TOTAL_ROLLS);

        for (int index = 0; index < TOTAL_ROLLS; index++) {
            results.add(roller.roll(false));
        }

        return results;
    }

    private double getAverage(ArrayList<Integer> rolls) {
        double count = 0;
        for (Integer roll : rolls) {
            count += roll;
        }

        double average = count / rolls.size();

        return average;
    }

    /**
     * @param simulatedRolls
     * @param tnValue
     * @return
     */
    private double calculatePercentAboveTn(ArrayList<Integer> simulatedRolls, int tnValue) {

        int index = 0;
        Collections.sort(simulatedRolls);

        while (index < simulatedRolls.size() && simulatedRolls.get(index) <= tnValue) {
            index++;
        }

        Double indexDouble = new Double(index);
        Double totalDouble = new Double(simulatedRolls.size());
        double percentage = (1 - indexDouble / totalDouble) * 100;

        return percentage;
    }

    public void simulate() {

        inProgress = true;
        
        progressThread = new SimulatorThread(handler);
        progressThread.start();

        progressDialog = ProgressDialog.show(simulatorFragment.getSherlockActivity(), "Please wait", "Simulating...", true,
                false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
                    return true; // Pretend we processed it
                }
                return false; // Any other keys are still processed as normal
            }
        });
    }

    /**
     * @return the inProgress
     */
    public boolean isInProgress() {
        return inProgress;
    }

    /**
     * 
     */
    public void informOfPause() {

        progressDialog.dismiss();
    }

    volatile Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // This only gets called after fragment resumes
            Bundle data = msg.getData();
            inProgress = false;
            simulatorFragment.addOutput(data.getString("output"));
            progressDialog.dismiss();
        }
    };

    /**
     * @author Francisco
     * 
     */
    private class SimulatorThread extends Thread {
        Handler mHandler;

        public SimulatorThread(Handler h) {
            mHandler = h;
        }

        public void run() {

            String output = new String();
            output += "***" + roller.getTitle() + "***\n";

            ArrayList<Integer> simulatedRolls = simulateRolls();
            double average = getAverage(simulatedRolls);

            int tnValue = simulatorFragment.getTnValue();
            double percentChange = calculatePercentAboveTn(simulatedRolls, tnValue);

            DecimalFormat myFormatter = new DecimalFormat("###,###,###.##");
            
            output += roller.getRollData();
            output += "Average over 10K rolls: " + myFormatter.format(average) + "\n";
            output += "Chance to hit " + tnValue + " TN: " + myFormatter.format(percentChange) + "%\n";
            output += "-----------------------------------------\n";

            Message msg = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("output", output);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }
}
