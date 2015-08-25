package com.google.android.gcm.GolAGol.ui;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.google.android.gcm.GolAGol.R;


import java.util.LinkedHashMap;

/**
 * Drawer menu for the app
 */
public class MainMenu {
    private final MainActivity mActivity;
    private LinkedHashMap<CharSequence, Class<? extends Fragment>> mMenu;

    public MainMenu(MainActivity a) {
        mActivity = a;
        mMenu = new LinkedHashMap<>();
        mMenu.put(a.getText(R.string.main_menu_instanceid), InstanceIdFragment.class);
        mMenu.put(a.getText(R.string.main_menu_matches), MatchFragment.class);
        mMenu.put(a.getText(R.string.main_menu_topics), TopicsFragment.class);

    }

    public CharSequence[] getEntries() {
        return mMenu.keySet().toArray(new CharSequence[mMenu.size()]);
    }

    public Fragment createFragment(int position)
            throws InstantiationException, IllegalAccessException {
        return mMenu.get(getEntries()[position]).newInstance();
    }

    public boolean onOverflowMenuItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_logs: {
                CharSequence showLogs = mActivity.getString(R.string.show_logs);
                if (showLogs.equals(item.getTitle())) {
                    mActivity.toggleLogsView(true);
                    item.setTitle(R.string.hide_logs);
                    item.setIcon(R.drawable.visibility_off_white);
                } else {
                    mActivity.toggleLogsView(false);
                    item.setTitle(R.string.show_logs);
                    item.setIcon(R.drawable.visibility_white);
                }
                return true;
            }
            default:
                return false;
        }
    }
}
