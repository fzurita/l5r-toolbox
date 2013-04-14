package l5r.toolbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import l5r.toolbox.common.ProfilesArrayAdapter;
import l5r.toolbox.health.HealthTracker;
import l5r.toolbox.health.HealthTrackerFragment;
import l5r.toolbox.profile.ProfileData;
import l5r.toolbox.profile.ProfileDialogFragment;
import l5r.toolbox.profile.ProfileManager;
import l5r.toolbox.roller.RollerFragment;
import l5r.toolbox.roller.RollerManager;
import l5r.toolbox.rollmods.RollModFragment;
import l5r.toolbox.simulator.SimulatorFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class L5RToolboxActivity extends SherlockFragmentActivity {

    private static RollerManager rollerManager = null;
    private static HealthTracker healthTracker = null;
    private static ProfileManager profileManager = null;
    private static boolean firstTime = true;
    private static boolean sleepFirstTime = true;
    private static String rollerTag = null;
    private static String simTag = null;
    private static String healthTag = null;
    private static final String dialogTag = "dialog";

    private ViewPager viewPager = null;
    private TabsAdapter tabsAdapter = null;
    private Spinner profileSpinner = null;
    private ProfilesArrayAdapter profilesAdapter = null;

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            ProfileData data = (ProfileData) profileSpinner.getSelectedItem();
            setCurrentProfile(data.getId());
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        } else {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        }

        setContentView(R.layout.main);
        viewPager = (ViewPager) findViewById(R.id.main_pager);
        profileSpinner = (Spinner) findViewById(R.id.main_profile_spinner);

        Tab rollerTab = actionBar.newTab().setText(R.string.roller);
        Tab simTab = actionBar.newTab().setText(R.string.simulator);
        Tab healthTab = actionBar.newTab().setText(R.string.health_tracker);

        Resources res = getResources(); // Resource object to get Drawables

        viewPager.setOffscreenPageLimit(2);

        tabsAdapter = new TabsAdapter(this, actionBar, viewPager);

        rollerTag = res.getString(R.string.roller_tag);
        simTag = res.getString(R.string.simulator_tag);
        healthTag = res.getString(R.string.health_tracker_tag);

        tabsAdapter.addTab(rollerTab, RollerFragment.class, rollerTag, null);
        tabsAdapter.addTab(simTab, SimulatorFragment.class, simTag, null);
        tabsAdapter.addTab(healthTab, HealthTrackerFragment.class, healthTag, null);

        // Hide the keyboard on creation
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Load rollers only on first time
        if (firstTime) {

            profileManager = new ProfileManager(getResources().getString(R.string.save_file_profiles));
            profileManager.loadProfiles(this);
            int currentProfile = profileManager.getCurrentProfileId();

            rollerManager = new RollerManager(getRollerFileName(currentProfile));
            healthTracker = new HealthTracker(getHealthFileName(currentProfile));

            rollerManager.loadRollers(this);
            healthTracker.loadHealthData(this);

            firstTime = false;
        }

        // Build profiles
        if (profileSpinner != null) {
            profilesAdapter = new ProfilesArrayAdapter(this);
            for (ProfileData data : profileManager) {
                profilesAdapter.add(data);
            }

            profileSpinner.setAdapter(profilesAdapter);
            
            //Set the position of the profile spinner
            int currentProfileId = profileManager.getCurrentProfileId();
            ProfileData data = profileManager.getWithId(currentProfileId);
            int position = profilesAdapter.getPosition(data);
            profileSpinner.setSelection(position, false);

            MyOnItemSelectedListener itemSelectedListener = new MyOnItemSelectedListener();
            profileSpinner.setOnItemSelectedListener(itemSelectedListener);
        }

        if (sleepFirstTime) {
            // TODO sleep here to fix race condition with actionbar sherlock
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sleepFirstTime = false;
        }

        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt("tab");
            actionBar.setSelectedNavigationItem(index);
            tabsAdapter.restoreData(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sleepFirstTime = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
        tabsAdapter.saveData(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_profiles:
            if (!isFinishing()) {
                ProfileDialogFragment profileDialog = ProfileDialogFragment.newInstance();
                profileDialog.show(getSupportFragmentManager(), dialogTag);
            }
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @return
     */
    public static RollerManager getRollerManager() {
        return rollerManager;
    }

    /**
     * @return
     */
    public static HealthTracker getHealthTracker() {
        return healthTracker;
    }

    /**
     * @return
     */
    public static ProfileManager getProfileManager() {
        return profileManager;
    }

    /**
     * @return
     */
    private String getRollerFileName(int profileId) {
        String rollerFileName = getResources().getString(R.string.save_file_rolls);
        if (profileId != 0) {
            rollerFileName = profileId + "_" + rollerFileName;
        }

        return rollerFileName;
    }

    /**
     * @return
     */
    private String getHealthFileName(int profileId) {
        String healthFileName = getResources().getString(R.string.save_file_health);
        if (profileId != 0) {
            healthFileName = profileId + "_" + healthFileName;
        }

        return healthFileName;
    }

    /**
     * Deletes a profile
     * 
     * @param id
     */
    public void deleteProfileId(int id) {

        if (profilesAdapter != null) {
            profilesAdapter.remove(profileManager.getWithId(id));
        }

        profileManager.deleteWithId(id);
        profileManager.saveProfiles(this);

        String healthFileName = getHealthFileName(id);
        String rollerFileName = getRollerFileName(id);

        deleteFile(healthFileName);
        deleteFile(rollerFileName);

        setCurrentProfile(0);
    }

    public void addProfile(ProfileData profileData) {
        profileManager.addWithId(profileData);
        profileManager.saveProfiles(this);

        if (profileSpinner != null) {
            ProfileData data = profileManager.get(profileManager.size() - 1);
            profilesAdapter.add(data);
        }
    }

    /**
     * Sets the current profile.
     * 
     * @param id
     */
    public void setCurrentProfile(int id) {
        if (profileManager.getCurrentProfileId() != id) {
            profileManager.setCurrentProfileId(id);
            profileManager.saveProfiles(this);

            rollerManager = new RollerManager(getRollerFileName(id));
            healthTracker = new HealthTracker(getHealthFileName(id));

            rollerManager.loadRollers(this);
            healthTracker.loadHealthData(this);

            rollManagerChanged();
            healthTrackerChanged();

            if (profileSpinner != null) {
                ProfileData data = profileManager.getWithId(id);
                int position = profilesAdapter.getPosition(data);
                profileSpinner.setSelection(position, false);
            }
        }
    }

    /**
     * Get current wound penalties
     * 
     * @return
     */
    public int getCurrentWoundPenalties() {
        int healthPenalty = 0;

        HealthTrackerFragment healthTrackerFragment = (HealthTrackerFragment) getTabFragment(healthTag);

        if (healthTrackerFragment.isApplyWoundPenalties()) {
            healthPenalty = healthTracker.getCurrentWoundPenalty();
        }

        return healthPenalty;
    }

    /**
     * @param tag
     * @return
     */
    public Fragment getTabFragment(String tag) {
        return tabsAdapter.getFragmentByTag(tag);
    }

    /**
     */
    public void rollManagerChanged() {
        SimulatorFragment simFragment = (SimulatorFragment) tabsAdapter.getFragmentByTag(simTag);
        simFragment.updateRollers();

        RollerFragment rollFragment = (RollerFragment) tabsAdapter.getFragmentByTag(rollerTag);
        rollFragment.updateRollers();
    }

    public void healthTrackerChanged() {
        HealthTrackerFragment healthFragment = (HealthTrackerFragment) tabsAdapter.getFragmentByTag(healthTag);
        healthFragment.updateHealthData();
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes in tabs, and takes care of
     * switch to the correct paged in the ViewPager whenever the selected tab
     * changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener,
            ActionBar.OnNavigationListener, ViewPager.OnPageChangeListener {
        private final SherlockFragmentActivity mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private SimpleSpinnerArrayAdapter spinnerArrayAdapter;
        private HashMap<String, Fragment> fragments = new HashMap<String, Fragment>();
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;
            public String tag;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
                tag = _tag;
            }
        }

        class SimpleSpinnerArrayAdapter extends ArrayAdapter<String> implements SpinnerAdapter {

            ActionBar actionBar = null;

            public SimpleSpinnerArrayAdapter(Context context, ActionBar actionBar) {
                super(context, R.layout.sherlock_spinner_item);

                this.actionBar = actionBar;
                this.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);

                if (position == actionBar.getSelectedNavigationIndex()) {
                    view.setBackgroundResource(R.color.abs__holo_blue_light);
                    view.setTypeface(null, Typeface.BOLD);
                }
                return view;
            }
        }

        public TabsAdapter(SherlockFragmentActivity activity, ActionBar actionBar, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = actionBar;
            mViewPager = pager;
            spinnerArrayAdapter = new SimpleSpinnerArrayAdapter(mContext, mActionBar);

            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);

            mActionBar.setListNavigationCallbacks(spinnerArrayAdapter, this);
        }

        public void addTab(Tab tab, Class<?> clss, String tag, Bundle args) {
            TabInfo info = new TabInfo(tag, clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            spinnerArrayAdapter.add(tab.getText().toString());
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            Fragment returnFragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
            fragments.put(info.tag, returnFragment);
            return returnFragment;
        }

        private void removeKeyboard(String tabId) {
            if (tabId == simTag) {
                // Change the focus
                LinearLayout dummyLayout = (LinearLayout) mContext.findViewById(R.id.Simulator_FakeLinearLayout);
                if (dummyLayout != null) {
                    dummyLayout.requestFocus();
                }
                // hide the keyboard on tab/pager change
                SimulatorFragment simFragment = (SimulatorFragment) fragments.get(tabId);
                if (simFragment != null) {
                    InputMethodManager imm = (InputMethodManager) mContext
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(simFragment.getTnEditText().getWindowToken(), 0);

                    RollModFragment rollModFragment = simFragment.getRollModFragment();

                    if (rollModFragment != null) {
                        rollModFragment.hideKeyboardFromFragment();
                    }

                }
            } else if (tabId == rollerTag) {
                // Change the focus
                LinearLayout dummyLayout = (LinearLayout) mContext.findViewById(R.id.FakeLinearLayout);
                if (dummyLayout != null) {
                    dummyLayout.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mContext
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(dummyLayout.getWindowToken(), 0);
                }

                // hide the keyboard on tab/pager change
                RollerFragment rollerFragment = (RollerFragment) fragments.get(tabId);
                if (rollerFragment != null) {
                    RollModFragment rollModFragment = rollerFragment.getRollModFragment();

                    if (rollModFragment != null) {
                        rollModFragment.hideKeyboardFromFragment();
                    }
                }
            } else if (tabId == healthTag) {
                // Change the focus
                LinearLayout dummyLayout = (LinearLayout) mContext.findViewById(R.id.Health_FakeLinearLayout);
                if (dummyLayout != null) {
                    dummyLayout.requestFocus();
                }
                // hide the keyboard on tab/pager change
                HealthTrackerFragment healthFragment = (HealthTrackerFragment) fragments.get(tabId);
                if (healthFragment != null) {

                    if (healthFragment != null) {
                        healthFragment.hideKeyboard();
                    }

                }
            }
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            TabInfo info = (TabInfo) tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == info) {
                    mViewPager.setCurrentItem(i);
                }
            }

            removeKeyboard(info.tag);
        }

        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            mViewPager.setCurrentItem(itemPosition);
            removeKeyboard(mTabs.get(itemPosition).tag);
            return true;
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        public Fragment getFragmentByTag(String tag) {
            return fragments.get(tag);
        }

        public void saveData(Bundle bundle) {

            String[] fragmentTags = new String[fragments.size()];
            bundle.putStringArray("fragments", fragments.keySet().toArray(fragmentTags));
            for (String fragmentKey : fragments.keySet()) {
                mContext.getSupportFragmentManager().putFragment(bundle, fragmentKey, fragments.get(fragmentKey));
            }

        }

        public void restoreData(Bundle bundle) {
            ArrayList<String> fragmentsArray = new ArrayList<String>();
            fragmentsArray.addAll(Arrays.asList(bundle.getStringArray("fragments")));

            for (String fragmentTag : fragmentsArray) {
                fragments.put(fragmentTag, mContext.getSupportFragmentManager().getFragment(bundle, fragmentTag));
            }
        }
    }
}