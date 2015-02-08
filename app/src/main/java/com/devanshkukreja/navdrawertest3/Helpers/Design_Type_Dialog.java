package com.devanshkukreja.navdrawertest3.Helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.devanshkukreja.navdrawertest3.Activities.DesignActivity;
import com.devanshkukreja.navdrawertest3.Activities.MainActivity;
import com.devanshkukreja.navdrawertest3.R;

/**
 * Created by devanshk on 8/12/14.
 */
public class Design_Type_Dialog extends DialogFragment {
    private static final String TAG = "DesignTypesDialog";

    String[] mIdeaTypesArray;
    private ImageButton custom, gears, intake, drive, lift, route;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mIdeaTypesArray=getResources().getStringArray(R.array.idea_types);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View v = layoutInflater.inflate(R.layout.dialog_idea_types,null);
        builder.setTitle("Pick a Design Type")
                .setView(v);

        LoadContent(v);
        LoadListeners();

        return builder.create();
    }

    private void LoadContent(View v){
        custom=(ImageButton)v.findViewById(R.id.custom_button);
        gears=(ImageButton)v.findViewById(R.id.gear_button);
        intake=(ImageButton)v.findViewById(R.id.intake_button);
        drive=(ImageButton)v.findViewById(R.id.drive_button);
        lift=(ImageButton)v.findViewById(R.id.lift_button);
        route=(ImageButton)v.findViewById(R.id.route_button);
    }

    private void LoadListeners(){
        //Setting design type and launching the appropriate activity
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Idea idea = new Idea();
                IdeaOrganizer.get(getActivity()).addIdea(idea);
                MainActivity.ideaId = idea.getId();
                idea.setType(Idea.Type.Custom);
                //Start ScoutingActivity
                Intent i = new Intent(getActivity(),DesignActivity.class);
                startActivity(i);
            }
        });
        gears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Idea idea = new Idea();
                IdeaOrganizer.get(getActivity()).addIdea(idea);
                MainActivity.ideaId = idea.getId();
                idea.setType(Idea.Type.Gear);
                //Start ScoutingActivity
                Intent i = new Intent(getActivity(),DesignActivity.class);
                startActivity(i);
            }
        });
        intake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Idea idea = new Idea();
                IdeaOrganizer.get(getActivity()).addIdea(idea);
                MainActivity.ideaId = idea.getId();
                idea.setType(Idea.Type.Intake);
                //Start ScoutingActivity
                Intent i = new Intent(getActivity(),DesignActivity.class);
                startActivity(i);
            }
        });
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Idea idea = new Idea();
                IdeaOrganizer.get(getActivity()).addIdea(idea);
                MainActivity.ideaId = idea.getId();
                idea.setType(Idea.Type.Drive);
                //Start ScoutingActivity
                Intent i = new Intent(getActivity(),DesignActivity.class);
                startActivity(i);
            }
        });
        lift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Idea idea = new Idea();
                IdeaOrganizer.get(getActivity()).addIdea(idea);
                MainActivity.ideaId = idea.getId();
                idea.setType(Idea.Type.Lift);
                //Start ScoutingActivity
                Intent i = new Intent(getActivity(),DesignActivity.class);
                startActivity(i);
            }
        });
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Idea idea = new Idea();
                IdeaOrganizer.get(getActivity()).addIdea(idea);
                MainActivity.ideaId = idea.getId();
                idea.setType(Idea.Type.Route);
                //Start ScoutingActivity
                Intent i = new Intent(getActivity(),DesignActivity.class);
                startActivity(i);
            }
        });
    }
}
