package com.devanshkukreja.navdrawertest3.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devanshkukreja.navdrawertest3.Activities.MainActivity;
import com.devanshkukreja.navdrawertest3.R;
import com.devanshkukreja.navdrawertest3.Helpers.VexAPI;

/**
 * Created by devanshkukreja on 8/4/14.
 */
public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";

    private static ImageView homeCenterBackdrop, homeCenterBlue1, homeCenterBlue2, homeCenterRed1, homeCenterRed2;
    private static ObjectAnimator rotationCenterBackdrop, rotationBlue1, rotationBlue2, rotationRed1, rotationRed2;
    public static TextView centerButton,winsCircle, tiesCircle, lossesCircle, robotSkillsRankCircle, robotSkillsScoreCircle, progSkillsRankCircle, progSkillsScoreCircle;
    private String speed,team;
    private static AnimatorSet animatorSet;
    private Boolean isInitialSetup=true;

    public HomeFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        LoadComponents(v); //Loads up ImageViews and animations
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Log.d(TAG, "Team in Settings = "+prefs.getString("team_number","2620A"));
        centerButton.setText(prefs.getString("team_number","XXXXX"));

        //Sample random duration: rotationBlue1.setDuration(scaleToDuration(Math.random()));
        speed = "slow";
        setRotationSpeed(speed);

        if (isInitialSetup){ //Queries for team data at start, but only once
            if (MainActivity.isConnectedToInternet()) {
                Log.d(TAG, "isConnected = "+MainActivity.isConnectedToInternet());
                isInitialSetup = false;
                new VexAPI.refreshTeamData().execute();
            }
            else
                Toast.makeText(getActivity(),"No Internet Connection.",Toast.LENGTH_SHORT);
        }

        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (speed.equals("slow")) {
                    speed = "fast";
                    setRotationSpeed(speed);
                } else {
                    speed = "slow";
                    setRotationSpeed(speed);
                }*/
                new VexAPI.refreshTeamData().execute();
            }
        });

        return v;
    }

    private void LoadComponents(View v){
        centerButton = (TextView)v.findViewById(R.id.home_center_text);
        winsCircle = (TextView)v.findViewById(R.id.home_wins);
        tiesCircle = (TextView)v.findViewById(R.id.home_ties);
        lossesCircle = (TextView)v.findViewById(R.id.home_losses);
        robotSkillsRankCircle = (TextView)v.findViewById(R.id.home_robot_skills_rank);
        robotSkillsScoreCircle = (TextView)v.findViewById(R.id.home_robot_skills_score);
        progSkillsRankCircle = (TextView)v.findViewById(R.id.home_prog_skills_rank);
        progSkillsScoreCircle = (TextView)v.findViewById(R.id.home_prog_skills_score);

        homeCenterBackdrop = (ImageView)v.findViewById(R.id.home_center_backdrop);
        homeCenterBlue1 = (ImageView)v.findViewById(R.id.home_center_blue1);
        homeCenterBlue2 = (ImageView)v.findViewById(R.id.home_center_blue2);
        homeCenterRed1 = (ImageView)v.findViewById(R.id.home_center_red1);
        homeCenterRed2 = (ImageView)v.findViewById(R.id.home_center_red2);

        animatorSet = new AnimatorSet();
        rotationCenterBackdrop = animateRotation(homeCenterBackdrop,(float)0,(long)10000);
        rotationBlue1 = animateRotation(homeCenterBlue1,(float)0,(long)13000);
        rotationBlue2 = animateRotation(homeCenterBlue2,(float)0,(long)15000);
        rotationRed1 = animateRotation(homeCenterRed1,(float)0,(long)12000);
        rotationRed2 = animateRotation(homeCenterRed2,(float)0,(long)14000);
        animatorSet.play(rotationCenterBackdrop);
        animatorSet.play(rotationBlue1);
        animatorSet.play(rotationBlue2);
        animatorSet.play(rotationRed1);
        animatorSet.play(rotationRed2);
        animatorSet.start();

        /*rotationCenterBackdrop = AnimationUtils.loadAnimation(getActivity(),R.anim.clockwise_rotation);
        rotationBlue1 = AnimationUtils.loadAnimation(getActivity(),R.anim.clockwise_rotation);
        rotationBlue2 = AnimationUtils.loadAnimation(getActivity(),R.anim.clockwise_rotation);
        rotationRed1 = AnimationUtils.loadAnimation(getActivity(),R.anim.clockwise_rotation);
        rotationRed2 = AnimationUtils.loadAnimation(getActivity(),R.anim.clockwise_rotation);*/
    }

    public static void setRotationSpeed(String speed){
        if (speed.equals("fast")){

            animatorSet = new AnimatorSet(); //Set ring rotation speed to fast
            rotationCenterBackdrop = animateRotation(homeCenterBackdrop,(Float)rotationCenterBackdrop.getAnimatedValue(),(long)750);
            rotationBlue1 = animateRotation(homeCenterBlue1,(Float)rotationBlue1.getAnimatedValue(),(long)2000);
            rotationBlue2 = animateRotation(homeCenterBlue2,(Float)rotationBlue2.getAnimatedValue(),(long)4000);
            rotationRed1 = animateRotation(homeCenterRed1,(Float)rotationRed1.getAnimatedValue(),(long)1000);
            rotationRed2 = animateRotation(homeCenterRed2,(Float)rotationRed2.getAnimatedValue(),(long)3000);
            animatorSet.play(rotationCenterBackdrop);
            animatorSet.play(rotationBlue1);
            animatorSet.play(rotationBlue2);
            animatorSet.play(rotationRed1);
            animatorSet.play(rotationRed2);
            animatorSet.start();

            /*rotationBlue2.setDuration(4000);
            homeCenterBlue2.startAnimation(rotationBlue2);

            rotationRed2.setDuration(3000);
            homeCenterRed2.startAnimation(rotationRed2);

            rotationBlue1.setDuration(2000);
            homeCenterBlue1.startAnimation(rotationBlue1);

            rotationRed1.setDuration(1000);
            homeCenterRed1.startAnimation(rotationRed1);

            rotationCenterBackdrop.setDuration(750);
            homeCenterBackdrop.startAnimation(rotationCenterBackdrop);*/
        }
        else if (speed.equals("slow")){

            animatorSet = new AnimatorSet(); //Set ring rotation speed to slow
            rotationCenterBackdrop = animateRotation(homeCenterBackdrop,(Float)rotationCenterBackdrop.getAnimatedValue(),(long)10000);
            rotationBlue1 = animateRotation(homeCenterBlue1,(Float)rotationBlue1.getAnimatedValue(),(long)13000);
            rotationBlue2 = animateRotation(homeCenterBlue2,(Float)rotationBlue2.getAnimatedValue(),(long)15000);
            rotationRed1 = animateRotation(homeCenterRed1,(Float)rotationRed1.getAnimatedValue(),(long)12000);
            rotationRed2 = animateRotation(homeCenterRed2,(Float)rotationRed2.getAnimatedValue(),(long)14000);
            animatorSet.play(rotationCenterBackdrop);
            animatorSet.play(rotationBlue1);
            animatorSet.play(rotationBlue2);
            animatorSet.play(rotationRed1);
            animatorSet.play(rotationRed2);
            animatorSet.start();

            /*rotationBlue2.setDuration(15000);
            homeCenterBlue2.startAnimation(rotationBlue2);

            rotationRed2.setDuration(14000);
            homeCenterRed2.startAnimation(rotationRed2);

            rotationBlue1.setDuration(13000);
            homeCenterBlue1.startAnimation(rotationBlue1);

            rotationRed1.setDuration(12000);
            homeCenterRed1.startAnimation(rotationRed1);

            rotationCenterBackdrop.setDuration(10000);
            homeCenterBackdrop.startAnimation(rotationCenterBackdrop);*/
        }
    }

    private static ObjectAnimator animateRotation(ImageView picture,Float save, Long duration){
        Float valueFrom=save; Float valueTo=save+360;
        ObjectAnimator temp = ObjectAnimator.ofFloat(picture,"rotation",valueFrom,valueTo);
        temp.setInterpolator(new LinearInterpolator());
        temp.setDuration(duration);
        temp.setRepeatCount(Animation.INFINITE);
        temp.setRepeatMode(Animation.RESTART);

        return temp;
    }

    private long scaleToDuration(double x){
        double duration;
        duration = (1.5*x/(x+.5))*4000+1000;
        Log.d(TAG, "duration = "+duration);
        return (long)duration;
    }

    public static void setWinsCircleText(String text){
        winsCircle.setText(text);
    }
    public static void setTiesCircleText(String text){
        tiesCircle.setText(text);
    }
    public static void setLossesCircleText(String text){
        lossesCircle.setText(text);
    }
    public static void setRobotSkillsRankCircle(String text){
        robotSkillsRankCircle.setText(text);
    }
    public static void setRobotSkillsScoreCircleS(String text){
        robotSkillsScoreCircle.setText(text);
    }
    public static void setProgSkillsRankCircle(String text){
        progSkillsRankCircle.setText(text);
    }
    public static void setProgSkillsScoreCircle(String text){
        progSkillsScoreCircle.setText(text);
    }
}
