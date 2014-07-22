package com.sms.partyview.activities;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.adapters.NavDrawerListAdapter;
import com.sms.partyview.fragments.EventListFragment;
import com.sms.partyview.fragments.EventTabsFragment;
import com.sms.partyview.fragments.ProfileFragment;
import com.sms.partyview.fragments.SignOutDialogFragment;
import com.sms.partyview.helpers.Utils;
import com.sms.partyview.models.NavDrawerItem;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import static com.sms.partyview.fragments.SignOutDialogFragment.SignOutDialogListener;

public class HomeActivity
        extends FragmentActivity
implements SignOutDialogListener{

    // Key used to store the user name in the installation info.
    public static final String INSTALLATION_USER_NAME_KEY = "username";
    private static final String TAG = HomeActivity.class.getSimpleName() + "_DEBUG";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private EventTabsFragment mEventTabsFragment;

    private Fragment mAttachedFragment;

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

        mEventTabsFragment = EventTabsFragment.newInstance();

        setupNavDrawer(savedInstanceState);

        storeInstallationInfo();
    }

    private void setupNavDrawer(Bundle savedInstanceState) {
        mTitle = mDrawerTitle = getTitle();

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
            displayView(0);
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
    private void displayView(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                mEventTabsFragment = EventTabsFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, mEventTabsFragment).commit();
                break;
            case 1:
                Fragment fragment = ProfileFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();
                break;
            case 2:
                SignOutDialogFragment signOutDialogFragment = new SignOutDialogFragment();
                signOutDialogFragment.show(getSupportFragmentManager(), "SignOutFragment");
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
        // do nothing
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
            displayView(position);
        }
    }
}