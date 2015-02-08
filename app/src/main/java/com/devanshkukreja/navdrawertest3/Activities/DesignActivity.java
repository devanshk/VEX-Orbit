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

import com.devanshkukreja.navdrawertest3.Fragments.IdeaFragment;
import com.devanshkukreja.navdrawertest3.Fragments.TeamFragment;
import com.devanshkukreja.navdrawertest3.R;


public class DesignActivity extends FragmentActivity{
    private String TAG= "DesignActivity";

    private Fragment fragment;
    private IdeaFragment ideaFragment = new IdeaFragment();

    static MenuItem cancel,save,edit,share,settings;

    @Override
    public void onCreate(Bundle savedInstanceState){
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 0, 0)));
        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.design_fragment_container);

        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.design_fragment_container,ideaFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG,"item ID: "+item.getItemId());
        if (item.getItemId()==16908332) { //If the back arrow item is selected
            NavUtils.navigateUpFromSameTask(this);
        }
                return super.onOptionsItemSelected(item);

    }

    protected Fragment createFragment(){
        return new IdeaFragment();
    }
}
