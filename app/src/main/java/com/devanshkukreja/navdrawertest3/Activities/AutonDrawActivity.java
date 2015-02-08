package com.devanshkukreja.navdrawertest3.Activities;

import android.support.v4.app.Fragment;

import com.devanshkukreja.navdrawertest3.Fragments.AutonDrawFragment;

/**
 * Created by devanshk on 7/11/2014.
 */
public class AutonDrawActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new AutonDrawFragment();
    }
}
