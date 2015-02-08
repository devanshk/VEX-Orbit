package com.devanshkukreja.navdrawertest3.Helpers;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.devanshkukreja.navdrawertest3.Activities.MainActivity;
import com.devanshkukreja.navdrawertest3.Fragments.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by devanshkukreja on 8/7/14.
 */
public class VexAPI {
    private static final String TAG = "VexAPI";

    private static String apiQueryRequest="";
    private static Boolean firstArg=true;

    public VexAPI(){}

    public static Integer getRankStatistic(String statistic,String season, String team, String rank, String sku){
        try {
            Integer cumulative = 0;

            JSONArray jsonArray = getRankings(season, team, rank, sku);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                cumulative= cumulative+jsonObject.getInt(statistic);
            }
            return cumulative;
        } catch (Exception e){e.printStackTrace();}

        return 0;
    }

    public static Integer getSkillStatistic(String statistic,Integer type, String season, String team, Boolean global, String rank, String sku){
        try {
            Integer best=null;

            JSONArray jsonArray = getSkills(season, team, type, global, rank, sku);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (best!=null) {
                    if (statistic == "score" && best < jsonObject.getInt(statistic))
                        best = jsonObject.getInt(statistic);
                    else if (statistic == "rank" && best > jsonObject.getInt(statistic))
                        best = jsonObject.getInt(statistic);
                }
                else
                    best=jsonObject.getInt(statistic);
            }
            if (best!=null)return best; else return 0;
        } catch (Exception e){e.printStackTrace();}

        return 0;
    }

    public static JSONArray getSkills(String season, String team, Integer type, Boolean global, String rank, String sku){
        apiQueryRequest = "http://api.vex.us.nallen.me/get_skills";
        firstArg=true;

        addParameter("sku",sku); //Event
        addParameter("rank",rank); //Filter by rank
        addParameter("team",team); //Filter by team
        addParameter("type",type.toString()); //0 = Robot Skills, 1 = Programming Skills
        addParameter("season",season); //Filter by season
        addParameter("season_rank",global.toString()); //Show ranks relative to all events, or just that one

        Log.d(TAG, "queryRequest = "+apiQueryRequest);

        try{ //Sends a web request and gets back a JSONObject response
            JSONObject jsonObject = new JSONObject(getWebResponse(apiQueryRequest));

            JSONArray jsonArray = jsonObject.getJSONArray("result");
            Log.d(TAG, "Received jsonObject = "+jsonArray);
            return jsonArray;} catch (Exception e){e.printStackTrace();}

        return null;
    }

    public static JSONArray getRankings(String season, String team, String rank, String sku){
        apiQueryRequest="http://api.vex.us.nallen.me/get_rankings";
        firstArg=true;

        addParameter("sku",sku);
        addParameter("rank",rank);
        addParameter("team",team);
        addParameter("season",season);

        Log.d(TAG, "queryRequest = "+apiQueryRequest);

        try{ //Sends a web request and gets back a JSONObject response
        JSONObject jsonObject = new JSONObject(getWebResponse(apiQueryRequest));

        JSONArray jsonArray = jsonObject.getJSONArray("result");
        Log.d(TAG, "Received jsonObject = "+jsonArray);
        return jsonArray;} catch (Exception e){e.printStackTrace();}

        return null;
    }

    private static void addParameter(String key, String value){
        if (value!=null){
            if (!firstArg){
                apiQueryRequest = apiQueryRequest+"&"; //Adds an & if it's not the first parameter
            }
            else{
                apiQueryRequest = apiQueryRequest+"?"; //Adds a ? if it is the first parameter
                firstArg=false;
            }
            apiQueryRequest=apiQueryRequest+key+"="+value;
        }
    }

    private static String getWebResponse(String urlString){

        try{URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url
                    .openConnection();
            return (readStream(con.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.d(TAG, "got back " + line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static class refreshTeamData extends AsyncTask<String, Void, String>{
        private String response1,response2,response3,response4,response5,response6,response7;

        @Override
        protected String doInBackground(String... params) {
            String team = HomeFragment.centerButton.getText().toString();

            response1 = ""+VexAPI.getRankStatistic("wins", "Skyrise", team, null, null);

            response2=""+VexAPI.getRankStatistic("ties","Skyrise",team,null,null);

            response3=""+VexAPI.getRankStatistic("losses","Skyrise",team,null,null);

            response4=""+VexAPI.getSkillStatistic("season_rank",0,"Skyrise",team,true,null,null);

            response5=""+VexAPI.getSkillStatistic("score",0,"Skyrise",team,true,null,null);

            response6=""+VexAPI.getSkillStatistic("season_rank",1,"Skyrise",team,true,null,null);

            response7 = ""+VexAPI.getSkillStatistic("score",1,"Skyrise",team,true,null,null);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HomeFragment.setRotationSpeed("fast");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            HomeFragment.setWinsCircleText(response1);
            HomeFragment.setTiesCircleText(response2);
            HomeFragment.setLossesCircleText(response3);
            HomeFragment.setRobotSkillsRankCircle(response4); //0 = robot skills
            HomeFragment.setRobotSkillsScoreCircleS(response5);
            HomeFragment.setProgSkillsRankCircle(response6); //1 = prog skills
            HomeFragment.setProgSkillsScoreCircle(response7);
            HomeFragment.setRotationSpeed("slow");
        }
    }
}
