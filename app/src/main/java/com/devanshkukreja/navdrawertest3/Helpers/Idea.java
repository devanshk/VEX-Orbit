package com.devanshkukreja.navdrawertest3.Helpers;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by devanshkukreja on 7/30/14.
 */
public class Idea {
    private static final String TAG = "Idea Helper";

    private static final String JSON_ID="id";
    private static final String JSON_Name="name";
    private static final String JSON_Motors="motors";
    private static final String JSON_Pistons="pistons";
    private static final String JSON_Power="power";
    private static final String JSON_Speed="speed";
    private static final String JSON_Gear_Before = "gear_before";
    private static final String JSON_Gear_After="gear_after";
    private static final String JSON_Height="height";
    private static final String JSON_Notes="notes";
    private static final String JSON_Type="type";
    private static final String JSON_Picture_Uri="picture_uri";
    private static final String JSON_Route_Score="route_score";
    private static final String JSON_Uses_Default="uses_default";
    private static final String JSON_JUNK = "junk";

    public enum Type{Route,Drive,Lift,Intake,Gear,Custom};
    private UUID Id;
    private String name="";
    private Integer motors=0;
    private Integer pistons=0;
    private Integer power=0;
    private Integer speed=0;
    private Integer gearBefore=0;
    private Integer gearAfter=0;
    private Float height=0f;
    private String notes="";
    private Type type=Type.Custom;
    private Uri pictureUri=null;
    private Integer points=0;
    private Boolean usesDefault=true;
    private Boolean imageLoaded=false;
    private ArrayList<String> junk = new ArrayList<String>();

    public Idea(){Id = UUID.randomUUID();}

    public Idea (JSONObject json) throws JSONException{
        Id = UUID.fromString(json.getString(JSON_ID));
        name = json.getString(JSON_Name);
        motors= json.getInt(JSON_Motors);
        pistons= json.getInt(JSON_Pistons);
        power= json.getInt(JSON_Power);
        speed= json.getInt(JSON_Speed);
        gearBefore= json.getInt(JSON_Gear_Before);
        gearAfter= json.getInt(JSON_Gear_After);
        height= Float.parseFloat(json.getString(JSON_Height));
        notes = json.getString(JSON_Notes);
        type = parseType(json.getString(JSON_Type));
        try{pictureUri = Uri.parse(json.getString(JSON_Picture_Uri));} catch (Exception e){e.printStackTrace();}
        points = json.getInt(JSON_Route_Score);
        usesDefault = json.getBoolean(JSON_Uses_Default);
        JSONArray temp = json.getJSONArray(JSON_JUNK);
        ArrayList<String> hopper = new ArrayList<String>();
        if (temp!=null){
            for (int i=0;i<temp.length();i++){
                hopper.add(temp.get(i).toString());
            }
        }

    }

    public JSONObject toJSON() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID, Id);
        jsonObject.put(JSON_Name,name);
        jsonObject.put(JSON_Motors, motors);
        jsonObject.put(JSON_Pistons,pistons);
        jsonObject.put(JSON_Power, power);
        jsonObject.put(JSON_Speed,speed);
        jsonObject.put(JSON_Gear_Before, gearBefore);
        jsonObject.put(JSON_Gear_After,gearAfter);
        jsonObject.put(JSON_Height, height);
        jsonObject.put(JSON_Notes,notes);
        jsonObject.put(JSON_Type, type.toString());
        try{jsonObject.put(JSON_Picture_Uri,pictureUri.toString());} catch (Exception e){e.printStackTrace();}
        jsonObject.put(JSON_Route_Score,points);
        jsonObject.put(JSON_Uses_Default, usesDefault);
        return jsonObject;
    }

    public static Type parseType  (String sample){
        if (sample.equals("Route"))
            return Type.Route;
        else if (sample.equals("Drive"))
            return Type.Drive;
        else if (sample.equals("Intake"))
            return Type.Intake;
        else if (sample.equals("Lift"))
            return Type.Lift;
        else if (sample.equals("Gear"))
            return Type.Gear;
        else if (sample.equals("Custom"))
            return Type.Custom;

        //Clean and scalable, but not working enum parsing code
        /*Type[] typeEnums = Type.values();
        for (Type t : typeEnums){
            if (t.toString() == sample)
                return t;
        }*/
        return null;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMotors() {
        return motors;
    }

    public void setMotors(Integer motors) {
        this.motors = motors;
    }

    public Integer getPistons() {
        return pistons;
    }

    public void setPistons(Integer pistons) {
        this.pistons = pistons;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Uri getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(Uri pictureUri) {
        this.pictureUri = pictureUri;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Boolean getUsesDefault() {
        return usesDefault;
    }

    public void setUsesDefault(Boolean usesDefault) {
        this.usesDefault = usesDefault;
    }

    public Boolean getImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(Boolean imageLoaded) {
        this.imageLoaded = imageLoaded;
    }

    public Integer getGearBefore() {
        return gearBefore;
    }

    public void setGearBefore(Integer gearBefore) {
        this.gearBefore = gearBefore;
    }

    public Integer getGearAfter() {
        return gearAfter;
    }

    public void setGearAfter(Integer gearAfter) {
        this.gearAfter = gearAfter;
    }
}
