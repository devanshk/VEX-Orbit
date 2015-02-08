package com.devanshkukreja.navdrawertest3.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.devanshkukreja.navdrawertest3.R;
import com.devanshkukreja.navdrawertest3.Helpers.Team;
import com.devanshkukreja.navdrawertest3.Fragments.TeamFragment;
import com.devanshkukreja.navdrawertest3.Fragments.TeamListFragment;
import com.devanshkukreja.navdrawertest3.Helpers.TeamOrganizer;

import java.util.UUID;

/**
 * Created by devanshk on 7/2/2014.
 */
public class TeamListActivity extends Activity {
    public static UUID teamID;
    public static Integer iteration = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 0, 0)));
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_team_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_team:
                Team team = new Team();
                TeamOrganizer.get(this).addTeam(team);
                Intent i = new Intent(this,ScoutingActivity.class);
                i.putExtra(TeamFragment.EXTRA_TEAM_ID,team.getId());
                startActivityForResult(i, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected Fragment createFragment(){
        return new TeamListFragment();
    }
}
