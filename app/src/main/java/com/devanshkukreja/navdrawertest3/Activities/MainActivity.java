package com.devanshkukreja.navdrawertest3.Activities;

import android.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.devanshkukreja.navdrawertest3.Fragments.HomeFragment;
import com.devanshkukreja.navdrawertest3.Fragments.IdeaGridFragment;
import com.devanshkukreja.navdrawertest3.Fragments.NavigationDrawerFragment;
import com.devanshkukreja.navdrawertest3.Helpers.Design_Type_Dialog;
import com.devanshkukreja.navdrawertest3.Helpers.Idea;
import com.devanshkukreja.navdrawertest3.Helpers.IdeaOrganizer;
import com.devanshkukreja.navdrawertest3.R;
import com.devanshkukreja.navdrawertest3.Fragments.SettingsFragment;
import com.devanshkukreja.navdrawertest3.Helpers.Team;
import com.devanshkukreja.navdrawertest3.Fragments.TeamFragment;
import com.devanshkukreja.navdrawertest3.Fragments.TeamListFragment;
import com.devanshkukreja.navdrawertest3.Helpers.TeamOrganizer;

import java.util.UUID;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static UUID ideaId;
    public static Integer fromPosition;
    private static final String TAG = "MainActivity";

    private static ConnectivityManager cm;
    private static NetworkInfo activeNetwork;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> ideaAdapter = new ArrayAdapter<String>(
                getBaseContext(),android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.idea_types));
        ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                Toast.makeText(getBaseContext(), "Item selected: "+itemPosition, Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        getActionBar().setListNavigationCallbacks(ideaAdapter, navigationListener);

        cm=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE); //Prepares to get network status
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        if (position == 1){
            fromPosition=2;
            Log.d(TAG, "fromPosition = "+fromPosition);
            fragmentManager.beginTransaction()
                    .replace(R.id.container,new HomeFragment())
                    .commit();
        }
        if (position == 3) {
            fromPosition = 3;
            Log.d(TAG, "fromPosition = " + fromPosition);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new TeamListFragment())
                    .commit();
        }
        if (position == 2) {
            fromPosition = 2;
            Log.d(TAG, "fromPosition = " + fromPosition);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new IdeaGridFragment())
                    .commit();
        }
        if (position == 4){
            fromPosition=4;
            Log.d(TAG, "fromPosition = "+fromPosition);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "Resuming and fromPosition = "+ fromPosition);
        if (fromPosition!=null){
            mNavigationDrawerFragment.mCurrentSelectedPosition = fromPosition;
            if (mNavigationDrawerFragment.mDrawerListView != null) {
                mNavigationDrawerFragment.mDrawerListView.setItemChecked(fromPosition, true);
            }
            if (mNavigationDrawerFragment.mDrawerLayout != null) {
                mNavigationDrawerFragment.mDrawerLayout.closeDrawer(mNavigationDrawerFragment.mFragmentContainerView);
            }
            if (mNavigationDrawerFragment.mCallbacks != null) {
                mNavigationDrawerFragment.mCallbacks.onNavigationDrawerItemSelected(fromPosition);
            }
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section0);
                break;
            case 3:
                mTitle = getString(R.string.title_section1);
                break;
            case 4:
                mTitle = getString(R.string.title_section2);
                break;
            case 5:
                mTitle=getString(R.string.title_section3);
                break;
            case 6:
                mTitle=getString(R.string.title_section4);
                break;
            case 7:
                mTitle=getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            if (mNavigationDrawerFragment.mCurrentSelectedPosition==2)//If Idea Grid Fragment is selected
                getMenuInflater().inflate(R.menu.fragment_idea_grid,menu);
            else if (mNavigationDrawerFragment.mCurrentSelectedPosition==3) //If Team List Fragment is selected
                getMenuInflater().inflate(R.menu.fragment_team_list,menu);
            else if (mNavigationDrawerFragment.mCurrentSelectedPosition==4) //If Settings Fragment is selected
                getMenuInflater().inflate(R.menu.settings,menu);
            else
                getMenuInflater().inflate(R.menu.main, menu);

            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(TAG, "ActionBar Item "+ id + " clicked.");
        if (id == R.id.action_settings) {
            return true;
        }
        if (id==R.id.menu_item_new_idea){ //If new design icon is selected
            //SpinnerAdapter ideaAdapter = ArrayAdapter.createFromResource(this, R.array.idea_types, android.R.layout.simple_spinner_item);
            new Design_Type_Dialog().show(getFragmentManager(),"design_type");
        }
        if (id==R.id.menu_item_new_team){ //If new team icon is selected
            Team team = new Team();
            TeamOrganizer.get(this).addTeam(team);
            Intent i = new Intent(this,ScoutingActivity.class);
            i.putExtra(TeamFragment.EXTRA_TEAM_ID,team.getId());
            startActivityForResult(i,0);
            return true;
        }
        if (id==R.id.menu_item_new_idea) {//If new idea icon is selected

            /*Intent i = new Intent(this,DesignActivity.class);
            startActivity(i);*/
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isConnectedToInternet(){
        activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null &&
                              activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
