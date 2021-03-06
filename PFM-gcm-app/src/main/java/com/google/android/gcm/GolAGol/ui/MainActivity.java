package com.google.android.gcm.GolAGol.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gcm.GolAGol.model.Constants;

import com.google.android.gcm.GolAGol.R;
import com.google.android.gcm.GolAGol.service.SportEventHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.List;

/**
 * Tha app's main activity that contains the different views or fragments
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String PREF_LAST_SCREEN_ID = "selected_screen_id";
    private static final String PREF_OPEN_DRAWER_AT_STARTUP = "open_drawer_at_startup";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mDrawerView;
    private ListView mDrawerMenu;
    private View mDrawerScrim;
    private TextView mLogsUI;
    private BroadcastReceiver mLoggerCallback;
    private MainMenu mMainMenu;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);
        mLogsUI = (TextView) findViewById(R.id.logs);
        // Broadcast receiver to handle common action such as refresh the UI or open/hide the match log
        mLoggerCallback = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Constants.ACTION_REFRESH_UI:
                        List<Fragment> fragments = getSupportFragmentManager().getFragments();
                        for (Fragment fragment : fragments) {
                            if (fragment instanceof RefreshableFragment && fragment.isVisible()) {
                                ((RefreshableFragment) fragment).refresh();
                            }
                        }
                        break;
                    case Constants.ACTION_SHOW_LOG:
                        String aux = intent.getStringExtra(SportEventHandler.EXTRA_COMMENT);
                        if (aux == null) {
                            aux = "";
                        }
                        mLogsUI.setText(aux);
                        break;
                }
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerView = (FrameLayout) findViewById(R.id.navigation_drawer);
        mDrawerMenu = (ListView) findViewById(R.id.navigation_drawer_menu);
        mDrawerScrim = findViewById(R.id.navigation_drawer_scrim);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TypedArray colorPrimaryDark =
                getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimaryDark});
        mDrawerLayout.setStatusBarBackgroundColor(colorPrimaryDark.getColor(0, 0xFF000000));
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        colorPrimaryDark.recycle();

        ImageView drawerHeader = new ImageView(this);
        drawerHeader.setImageResource(R.drawable.drawer_gcm_logo);
        mDrawerMenu.addHeaderView(drawerHeader);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Set the drawer width accordingly with the guidelines: window_width - toolbar_height.
            toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                        return;
                    }
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    float logicalDensity = metrics.density;
                    int maxWidth = (int) Math.ceil(320 * logicalDensity);
                    DrawerLayout.LayoutParams params =
                            (DrawerLayout.LayoutParams) mDrawerView.getLayoutParams();
                    int newWidth = view.getWidth() - view.getHeight();
                    params.width = (newWidth > maxWidth ? maxWidth : newWidth);
                    mDrawerView.setLayoutParams(params);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mDrawerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    // Set scrim height to match status bar height.
                    mDrawerScrim.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            insets.getSystemWindowInsetTop()));
                    return insets;
                }
            });
        }

        int activeItemIndicator = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) ?
                android.R.layout.simple_list_item_activated_1 :
                android.R.layout.simple_list_item_checked;

        mMainMenu = new MainMenu(this);
        mDrawerMenu.setOnItemClickListener(this);
        mDrawerMenu.setAdapter(new ArrayAdapter<>(getSupportActionBar().getThemedContext(),
                activeItemIndicator, android.R.id.text1, mMainMenu.getEntries()));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                // The user learned how to open the drawer. Do not open it for him anymore.
                getAppPreferences().edit()
                        .putBoolean(PREF_OPEN_DRAWER_AT_STARTUP, false).apply();
                super.onDrawerOpened(drawerView);
            }
        };

        boolean activityResumed = (savedState != null);
        boolean openDrawer = getAppPreferences().getBoolean(PREF_OPEN_DRAWER_AT_STARTUP, true);
        int lastScreenId = getAppPreferences().getInt(PREF_LAST_SCREEN_ID, 0);
        selectItem(lastScreenId);
        if (!activityResumed && openDrawer) {
            mDrawerLayout.openDrawer(mDrawerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /*
         * Here we check if the Activity was created by the user clicking on one of our GCM
         * notifications:
         * 1. Check if the action of the intent used to launch the Activity.
         * 2. Print out any additional data sent with the notification. This is included as extras
         *  on the intent.
         */
        Intent launchIntent = getIntent();
        if (Constants.NOTIFICATION_OPEN_ACTION.equals(launchIntent.getAction())) {
            Bundle data = launchIntent.getExtras();
            //data.isEmpty(); // Force the bundle to unparcel so that toString() works
            String Local = data.getString(SportEventHandler.EXTRA_LOCAL);
            String Away = data.getString(SportEventHandler.EXTRA_AWAY);
            String LocalScore = data.getString(SportEventHandler.EXTRA_LOCAL_SCORE);
            String AwayScore = data.getString(SportEventHandler.EXTRA_AWAY_SCORE);
            String Status = data.getString(SportEventHandler.EXTRA_STATUS);
            String comment = data.getString(SportEventHandler.EXTRA_COMMENT);
            String matchId = data.getString(SportEventHandler.EXTRA_MATCHID);
            SportEventHandler.startActionScore(this, matchId, Local, Away, LocalScore, AwayScore, Status, comment);
            //String format = getResources().getString(R.string.notification_intent_received);
            //mLogger.log(Log.INFO, String.format(format, data));
        }

        //Check if Google Play Services APK
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        checkPlayServices();

    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Set a status bar color while in action mode (text copy&paste)
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_900));
        }
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Reset status bar to transparent when leaving action mode (text copy&paste)
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
        //Check if Google Play Services APK
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        checkPlayServices();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLoggerCallback);
        //mLogger.unregisterCallback(mLoggerCallback);
        super.onPause();
    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_REFRESH_UI);
        filter.addAction(Constants.ACTION_SHOW_LOG);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLoggerCallback, filter);
    }


    /**
     * Toggle the Logs View visibility with a nice animation.
     */
    public void toggleLogsView(boolean showView) {
        final View logsView = findViewById(R.id.logs_layout);
        final View bodyView = findViewById(R.id.container);
        final FrameLayout.LayoutParams logsLayoutParams =
                (FrameLayout.LayoutParams) logsView.getLayoutParams();
        final int startLogsY, endLogsY, startBodyY, endBodyY;

        if (showView) {
            // The logsView height set in XML is a placeholder, we need to compute at runtime
            // how much is 0.4 of the screen height.
            int height = (int) (0.4 * mDrawerLayout.getHeight());

            // The LogsView is hidden being placed off-screen with a negative bottomMargin.
            // We need to update its height and bottomMargin to the correct runtime values.
            logsLayoutParams.bottomMargin = -logsLayoutParams.height;
            logsView.setLayoutParams(logsLayoutParams);
            logsLayoutParams.height = height;

            // Prepare the value for the Show animation.
            startLogsY = logsLayoutParams.bottomMargin;
            endLogsY = 0;
            startBodyY = 0;
            endBodyY = logsLayoutParams.height;
        } else {
            // Prepare the value for the Hide animation.
            startLogsY = 0;
            endLogsY = -logsLayoutParams.height;
            startBodyY = logsLayoutParams.height;
            endBodyY = 0;
        }
        final int deltaLogsY = endLogsY - startLogsY;
        final int deltaBodyY = endBodyY - startBodyY;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                logsLayoutParams.bottomMargin = (int) (startLogsY + deltaLogsY * interpolatedTime);
                logsView.setLayoutParams(logsLayoutParams);
                bodyView.setPadding(0, 0, 0, (int) (startBodyY + deltaBodyY * interpolatedTime));
            }
        };
        a.setDuration(500);
        logsView.startAnimation(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);
        return true;
    }

    private void selectItem(int pos) {
        if (pos < 0 || pos >= mMainMenu.getEntries().length) {
            pos = 0;
        }
        String titlePrefix = getString(R.string.main_activity_title_prefix);
        getSupportActionBar().setTitle(titlePrefix + mMainMenu.getEntries()[pos]);
        String nextFragmentTag = "FRAGMENT_TAG_" + Integer.toString(pos);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment != null && nextFragmentTag.equals(currentFragment.getTag())) {
            return;
        }
        Fragment recycledFragment = getSupportFragmentManager().findFragmentByTag(nextFragmentTag);
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (currentFragment != null) {
                transaction.detach(currentFragment);
            }
            if (recycledFragment != null) {
                transaction.attach(recycledFragment);
            } else {
                transaction.add(R.id.container, mMainMenu.createFragment(pos), nextFragmentTag);
            }
            transaction.commit();
            getSupportFragmentManager().executePendingTransactions();
            // The header takes the first position.
            mDrawerMenu.setItemChecked(pos + 1, true);
            getAppPreferences().edit().putInt(PREF_LAST_SCREEN_ID, pos).apply();
        } catch (InstantiationException e) {
            Log.wtf(TAG, "Error while instantiating the selected fragment", e);
        } catch (IllegalAccessException e) {
            Log.wtf(TAG, "Error while instantiating the selected fragment", e);
        }
    }

    private SharedPreferences getAppPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        selectItem(pos - 1);
        mDrawerLayout.closeDrawer(mDrawerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (mDrawerToggle.onOptionsItemSelected(item)
                || mMainMenu.onOverflowMenuItemSelected(item)
                || super.onOptionsItemSelected(item));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLoggerCallback);
        super.onDestroy();
    }

    public interface RefreshableFragment {
        void refresh();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
