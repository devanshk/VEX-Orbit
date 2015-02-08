package com.devanshkukreja.navdrawertest3.Helpers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by devanshk on 7/2/2014.
 */
public class TeamOrganizer {
    private static final String TAG="TeamOrganizer";
    private static final String FILENAME="teams.json";

    private ArrayList<Team> mTeams;
    private TeamJSONSerializer mSerializer;

    private static TeamOrganizer sTeamOrganizer;

    private TeamOrganizer(Context mAppContext){
        mSerializer=new TeamJSONSerializer(mAppContext,FILENAME);
        mTeams=new ArrayList<Team>();

        try {
            mTeams = mSerializer.loadTeams();
        } catch (Exception e) {
            mTeams = new ArrayList<Team>();
            Log.e(TAG, "Error loading teams: ", e);
        }
    }

    public static TeamOrganizer get(Context c){
        if (sTeamOrganizer == null){
            sTeamOrganizer=new TeamOrganizer(c.getApplicationContext());
        }
        return sTeamOrganizer;
    }

    public void addTeam(Team t){
        mTeams.add(t);
    }

    public void deleteTeam(Team t){mTeams.remove(t);}

    public boolean saveTeams() {
        try {
            mSerializer.saveTeams(mTeams);
            Log.d(TAG, "Teams saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving teams: " + e);
            return false;
        }
    }

    public ArrayList<Team> getTeams(){
        return mTeams;
    }

    public Team getTeam(UUID id){
        for (Team t:mTeams){
            if (t.getId().equals(id))
                return t;
        }
        return null;
    }
}

