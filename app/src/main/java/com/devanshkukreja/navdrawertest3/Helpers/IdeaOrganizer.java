package com.devanshkukreja.navdrawertest3.Helpers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by devanshkukreja on 7/30/14.
 */
public class IdeaOrganizer {
    private static final String TAG="IdeaOrganizer";
    private static final String FILENAME="ideas.json";

    private static ArrayList<Idea> mIdeas;
    private static IdeaJSONSerializer mSerializer;

    private static IdeaOrganizer sIdeaOrganizer;

    private IdeaOrganizer(Context mAppContext){
        mSerializer=new IdeaJSONSerializer(mAppContext,FILENAME);
        mIdeas=new ArrayList<Idea>();

        try {
            Log.d(TAG, "[Loading Ideas]");
            mIdeas = mSerializer.loadIdeas();
        } catch (Exception e) {
            mIdeas = new ArrayList<Idea>();
            Log.e(TAG, "[Error loading ideas: "+ e+"]");
        }
    }

    public static IdeaOrganizer get(Context c){
        if (sIdeaOrganizer == null){
            sIdeaOrganizer=new IdeaOrganizer(c.getApplicationContext());
        }
        return sIdeaOrganizer;
    }

    public static void addIdea(Idea t){
        mIdeas.add(t);
    }

    public static void deleteIdea(Idea t){mIdeas.remove(t);}

    public static boolean saveIdeas() {
        try {
            //Log.d(TAG, "mIdeas size = "+mIdeas.size());
            mSerializer.saveIdeas(mIdeas);
            Log.d(TAG, "Ideas saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving ideas: " + e);
            return false;
        }
    }

    public static ArrayList<Idea> getmIdeas(){
        /*for (Integer i = 0;i<20;i++){ Auto-generates Design Units for testing
            Idea idea = new Idea();
            if (i%3==0)
                idea.setType(Idea.Type.Drive);
            else if (i%3==1)
                idea.setType(Idea.Type.Gear);
            else
                idea.setType(Idea.Type.Intake);
            if (i%2==0)
                idea.setName("Groot");
            else
                idea.setName("Drax");
            mIdeas.add(idea);
        }*/
        return mIdeas;
    }

    public static Idea getIdea(UUID id){
        for (Idea t:mIdeas){
            if (t.getId().equals(id))
                return t;
        }
        return null;
    }
}
