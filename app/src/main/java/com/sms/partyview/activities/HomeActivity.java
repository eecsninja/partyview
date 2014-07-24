package com.sms.partyview.activities;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.adapters.NavDrawerListAdapter;
import com.sms.partyview.fragments.AcceptedEventDetailFragment;
import com.sms.partyview.fragments.EventListFragment;
import com.sms.partyview.fragments.EventTabsFragment;
import com.sms.partyview.fragments.ProfileFragment;
import com.sms.partyview.fragments.SignOutDialogFragment;
import com.sms.partyview.helpers.Utils;
import com.sms.partyview.models.LocalEvent;
import com.sms.partyview.models.NavDrawerItem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.sms.partyview.fragments.EventListFragment.EventListDataChangeListener;
import static com.sms.partyview.fragments.SignOutDialogFragment.SignOutDialogListener;

public class HomeActivity
        extends FragmentActivity
        implements SignOutDialogListener, EventListDataChangeListener {

    // Key used to store the user name in the installation info.
    public static final String INSTALLATION_USER_NAME_KEY = "username";
    private static final int HOME_DRAWER_POSITION = 0;

    // For passing in a new event from a notification.
    public static final String EVENT_NOTIFICATION_ACTION = "com.sms.partyview.NEW_EVENT";
    public static final String EVENT_NOTIFICATION_EVENT_KEY = "event";
    public static final String EVENT_NOTIFICATION_IS_NEW_KEY = "is_new";

    private static final String TAG = HomeActivity.class.getSimpleName() + "_DEBUG";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private EventTabsFragment mEventTabsFragment;
    private ProfileFragment mProfileFragment;
    private SignOutDialogFragment mSignOutDialogFragment;

    private BroadcastReceiver mBroadcastReceiver;

    // Indexes for nav drawer items.
    private static final int NAV_DRAWER_HOME = 0;
    private static final int NAV_DRAWER_PROFILE = 1;
    private static final int NAV_DRAWER_SIGN_OUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cacheAppUsers();

//        getActionBar().setTitle(
//                getString(R.string.title_activity_home) +
//                        " (" + ParseUser.getCurrentUser().getUsername() + ")"
//        );

        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTextColor(getResources().getColor(R.color.white));
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        yourTextView.setPadding(0,0,0,5);
        yourTextView.setTextSize(22);

        setUpFragments(savedInstanceState);

        setupNavDrawer(savedInstanceState);

        storeInstallationInfo();

        // Listen for event notifications.
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEventNotificationIntent(intent);
            }
        };
        registerReceiver(mBroadcastReceiver, new IntentFilter(EVENT_NOTIFICATION_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void setupNavDrawer(Bundle savedInstanceState) {
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Change password
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Log out
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
               // getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
               // getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            showSelectedDrawerItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_new_event:
                displayNewEventActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    private void signOutUser() {
        ParseUser.getCurrentUser().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == Utils.NEW_EVENT_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            mEventTabsFragment.respondToEventCreation(data);
        } else if ((requestCode == Utils.RESPOND_TO_INVITE_EVENT_REQUEST_CODE) && (resultCode
                == RESULT_OK)) {
            mEventTabsFragment.respondToInvite(data);

        } else if (requestCode == EventListFragment.EVENT_DETAIL_REQUEST &&
                resultCode == RESULT_OK) {
            mEventTabsFragment.respondToEventEdit(data);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void displayNewEventActivity() {
        Intent i = new Intent(this, NewEventActivity.class);
        startActivityForResult(i, Utils.NEW_EVENT_REQUEST_CODE);
    }

    // Registers the user with this installation's info.
    private void storeInstallationInfo() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String currentUserName = ParseUser.getCurrentUser().getUsername();
        if (currentUserName == installation.getString(INSTALLATION_USER_NAME_KEY)) {
            return;
        }
        installation.put(INSTALLATION_USER_NAME_KEY, currentUserName);
        installation.saveInBackground();
    }

    private void cacheAppUsers() {
        FindCallback<ParseUser> callback = new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> users, ParseException e) {

                Log.d(TAG, "got user info");
                Log.d(TAG, users.toString());

                // Remove the previously cached results.
                ParseObject.unpinAllInBackground("users", new DeleteCallback() {
                    public void done(ParseException e) {
                        // Cache the new results.
                        ParseObject.pinAllInBackground("users", users);
                        Log.d(TAG, "pin all user info");
                    }
                });
            }
        };

        Utils.cacheAppUsers(callback);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void showSelectedDrawerItem(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case NAV_DRAWER_HOME:
                displayEventTabsFragment();
                break;
            case NAV_DRAWER_PROFILE:
                displayProfileFragment();
                break;
            case NAV_DRAWER_SIGN_OUT:
                displaySignOutDialogFragment();
                break;
            default:
                break;
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        //setTitle(navMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onDialogPositiveClick() {
        signOutUser();
    }

    @Override
    public void onDialogNegativeClick() {
        // go back to home
        showSelectedDrawerItem(HOME_DRAWER_POSITION);
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected nav drawer item
            showSelectedDrawerItem(position);
        }
    }

    // Gets a new or updated event from an intent.
    private void handleEventNotificationIntent(Intent intent) {
        LocalEvent event = (LocalEvent) intent.getSerializableExtra(EVENT_NOTIFICATION_EVENT_KEY);
        if (event == null) {
            System.err.println("Intent does not contain an event.");
            return;
        }
        boolean isNewEvent = intent.getBooleanExtra(EVENT_NOTIFICATION_IS_NEW_KEY, true);

        // Pass the event along to the event tabs.
        if (isNewEvent) {
            mEventTabsFragment.addNewInvite(event);
        } else {
            Intent tabsIntent = new Intent();
            tabsIntent.putExtra(AcceptedEventDetailFragment.UDPATED_EVENT_INTENT_KEY, event);
            mEventTabsFragment.respondToEventEdit(tabsIntent);
        }
    }

    private void setUpFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mEventTabsFragment = EventTabsFragment.newInstance();
            mProfileFragment = ProfileFragment.newInstance();
            mSignOutDialogFragment = new SignOutDialogFragment();
        }
    }

    private void displayEventTabsFragment()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mEventTabsFragment.isAdded()) { // if the fragment is already in container
            ft.show(mEventTabsFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.frame_container, mEventTabsFragment, "EventTabsFragment");
        }

        // hide remaining fragments
        if (mProfileFragment.isAdded()) { ft.hide(mProfileFragment); }

        // Commit changes
        ft.commit();
    }

    private void displayProfileFragment()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (mProfileFragment.isAdded()) {
            ft.show(mProfileFragment);
        } else {
            ft.add(R.id.frame_container, mProfileFragment, "ProfileFrragment");
        }

        // hide remaining fragments
        if (mEventTabsFragment.isAdded()) { ft.hide(mEventTabsFragment); }

        // Commit changes
        ft.commit();
    }

    private void displaySignOutDialogFragment()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mSignOutDialogFragment.show(getSupportFragmentManager(), "SignoutFragment");

        // hide remaining fragments
        if (mEventTabsFragment.isAdded()) { ft.hide(mEventTabsFragment); }
        if (mProfileFragment.isAdded()) { ft.hide(mProfileFragment); }

        // Commit changes
        ft.commit();
    }

    @Override
    public void onEventListUpdate(int dataIndex, int size) {
        mEventTabsFragment.onEventListUpdate(dataIndex, size);
    }
}