package com.devanshkukreja.navdrawertest3.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.devanshkukreja.navdrawertest3.R;
import com.devanshkukreja.navdrawertest3.Fragments.TeamFragment;


public class ScoutingActivity extends FragmentActivity{
    private String TAG= "ScoutingActivity";

    private Fragment fragment;
    private TeamFragment teamFragment = new TeamFragment();

    static MenuItem cancel,save,edit,share,settings;

    @Override
    public void onCreate(Bundle savedInstanceState){
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 0, 0)));
        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,teamFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scouting,menu);
        cancel=menu.findItem(R.id.cancel_team_changes);
        save=menu.findItem(R.id.save_team_changes);
        edit=menu.findItem(R.id.edit_settings);
        share=menu.findItem(R.id.share_button);
        settings=menu.findItem(R.id.settings_button);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG,"item ID: "+item.getItemId());
        switch (item.getItemId()){
            case 16908332: //ID for Home Button
                Log.d(TAG,"Attempting to navigate up from activity.");
                NavUtils.navigateUpFromSameTask(this); //Navigates back to parent activity
                return true;
            case R.id.edit_settings:
                teamFragment.setTeamEditable(true);
                edit.setVisible(false);
                share.setVisible(false);
                settings.setVisible(false);
                //cancel.setVisible(true);
                save.setVisible(true);
                return true;
            case R.id.save_team_changes:
                teamFragment.setTeamEditable(false);
                edit.setVisible(true);
                share.setVisible(true);
                settings.setVisible(true);
                //cancel.setVisible(false);
                save.setVisible(false);
                return true;
            case R.id.cancel_team_changes:
                teamFragment.setTeamEditable(false);
                edit.setVisible(true);
                share.setVisible(true);
                settings.setVisible(true);
                //cancel.setVisible(false);
                save.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected Fragment createFragment(){
        return new TeamFragment();
    }
}
