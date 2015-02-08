package com.devanshkukreja.navdrawertest3.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.devanshkukreja.navdrawertest3.R;
import com.devanshkukreja.navdrawertest3.Helpers.Team;
import com.devanshkukreja.navdrawertest3.Fragments.TeamFragment;
import com.devanshkukreja.navdrawertest3.Helpers.TeamOrganizer;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Devansh on 7/2/2014.
 */
public class TeamPagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ArrayList<Team> mTeams;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mTeams= TeamOrganizer.get(this).getTeams();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Team team = mTeams.get(position);
                return TeamFragment.newInstance(team.getId());
            }

            @Override
            public int getCount() {
                return mTeams.size();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                Team team = mTeams.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        UUID teamId = (UUID)getIntent()
                .getSerializableExtra(TeamFragment.EXTRA_TEAM_ID);
        for (int i=0;i<mTeams.size();i++){
            if (mTeams.get(i).getId().equals(teamId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
