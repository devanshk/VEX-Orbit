package com.devanshkukreja.navdrawertest3.Helpers;

import android.content.Context;
import android.util.Log;

import com.devanshkukreja.navdrawertest3.Helpers.Idea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by devanshkukreja on 7/30/14.
 */
public class IdeaJSONSerializer {
    private static final String TAG = "IdeaJSONSerializer";

    private Context mContext;
    private String mFilename;

    public IdeaJSONSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
    }

    public ArrayList<Idea> loadIdeas() throws IOException, JSONException {
        ArrayList<Idea> ideas = new ArrayList<Idea>();
        BufferedReader reader = null;
        try {
            // open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // build the array of ideas from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                ideas.add(new Idea(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // we will ignore this one, since it happens when we start fresh
        } finally {
            if (reader != null)
                reader.close();
        }
        return ideas;
    }

    public void saveIdeas(ArrayList<Idea> ideas) throws JSONException, IOException {
        // build an array in JSON
        JSONArray array = new JSONArray();
        //Log.d(TAG, "Created JSONArray.");
        for (Idea i : ideas) {
            array.put(i.toJSON());
            //Log.d(TAG, "Put Idea "+i+" in JSON");
        }

        // write the file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            //Log.d(TAG, "Created OutputStream "+out);
            writer = new OutputStreamWriter(out);
            //Log.d(TAG, "Created OutputStreamWriter "+writer);
            writer.write(array.toString());
            //Log.d(TAG, "Wrote array to string.");
        } finally {
            if (writer != null) {
                //Log.d(TAG,"Closing Writer.");
                writer.close();
            }
        }
    }
}
