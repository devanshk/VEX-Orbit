package com.devanshkukreja.navdrawertest3.Activities;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.devanshkukreja.navdrawertest3.Fragments.AutonDrawFragment;
import com.devanshkukreja.navdrawertest3.Fragments.DesignDrawFragment;
import com.devanshkukreja.navdrawertest3.R;

/**
 * Created by devanshk on 7/11/2014.
 */
public class DrawDesignActivity extends SingleFragmentActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.design_fragment,menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer id = item.getItemId();
        switch (id){
            case R.id.design_menu_cancel:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.design_menu_accept:
                DesignDrawFragment.savePicture();
                NavUtils.navigateUpFromSameTask(this);
                break;
            case android.R.id.home:
                DesignDrawFragment.savePicture();
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    @Override
    protected Fragment createFragment(){
        return new DesignDrawFragment();
    }

    public void restoreActionBar()  {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Design");
    }
}
