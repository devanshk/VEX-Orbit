package com.devanshkukreja.navdrawertest3.Helpers;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by devanshk on 7/1/2014.
 */
public class Team {

    private static final String JSON_ID="id";
    private static final String JSON_Number="number";
    private static final String JSON_Name="name";
    private static final String JSON_Club="club";
    private static final String JSON_DriveType="drive_type";
    private static final String JSON_DriveScore="drive_score";
    private static final String JSON_DriveNotes="drive_notes";
    private static final String JSON_Intake1Score="intake1_score";
    private static final String JSON_Intake2Score="intake2_score";
    private static final String JSON_IntakeNotes="intake_notes";
    private static final String JSON_LiftType="lift_type";
    private static final String JSON_LiftScore="lift_score";
    private static final String JSON_LiftNotes="lift_notes";
    private static final String JSON_Auton1Score="auton1_score";
    private static final String JSON_Auton1Reliability="auton1_reliability";
    private static final String JSON_Auton2Score="auton2_score";
    private static final String JSON_Auton2Reliability="auton2_reliability";
    private static final String JSON_AutonNotes="auton_notes";
    private static final String JSON_AutonSynchronized="auton_synchronized";
    private static final String JSON_OverallScore="overall_score";
    private static final String JSON_Auton1ImagePath="auton1_image_path";
    private static final String JSON_Auton2ImagePath="auton2_image_path";
    private static final String JSON_Auton1ImageName="auton1_image_name";
    private static final String JSON_Auton2ImageName="auton2_image_name";
    private static final String JSON_MainPictureUri="picture_uri";
    private static final String JSON_UsesDefaultPic="uses_default_pic";
    private static final String JSON_TimeVisited="time_visited";
    private static final String JSON_DriveMotors="drive_motors";
    private static final String JSON_LiftMotors="lift_motors";
    private static final String JSON_IntakeMotors="intake_motors";

    private UUID mId;
    private String mNumber="";
    private String mName="";
    private String mClub="";
    private String mDriveType="Tap Here";
    private Double mDriveScore=0.0;
    private String mDriveNotes="";
    private Double mIntake1Score=0.0;
    private Double mIntake2Score=0.0;
    private String mIntakeNotes="";
    private String mLiftType="Tap Here";
    private Double mLiftScore=0.0;
    private String mLiftNotes="";
    private Integer mAuton1Score=0;
    private Integer mAuton1Reliability=0;
    private Integer mAuton2Score=0;
    private Integer mAuton2Reliability=0;
    private String mAutonNotes="";
    private Boolean mSynchronized=false;
    private Double mScore=0.0;
    private String mAuton1ImagePath="";
    private String mAuton2ImagePath="";
    private String mAuton1ImageName="";
    private String mAuton2ImageName="";
    private Uri mPictureUri=null;
    private Boolean usesDefaultPicture=true;
    private Integer timeVisited=0;
    private Integer driveMotors=0;
    private Integer liftMotors=0;
    private Integer intakeMotors=0;

    public Team(){
        mId=UUID.randomUUID();
    }

    public Team(JSONObject json) throws JSONException{
        mId=UUID.fromString(json.getString(JSON_ID));
        mNumber=json.getString(JSON_Number);
        mName=json.getString(JSON_Name);
        mClub=json.getString(JSON_Club);
        mDriveType=json.getString(JSON_DriveType);
        mDriveScore=json.getDouble(JSON_DriveScore);
        mDriveNotes=json.getString(JSON_DriveNotes);
        mIntake1Score=json.getDouble(JSON_Intake1Score);
        mIntake2Score=json.getDouble(JSON_Intake2Score);
        mIntakeNotes=json.getString(JSON_IntakeNotes);
        mLiftType=json.getString(JSON_LiftNotes);
        mLiftScore=json.getDouble(JSON_LiftScore);
        mLiftNotes=json.getString(JSON_LiftNotes);
        mAuton1Score=json.getInt(JSON_Auton1Score);
        mAuton1Reliability=json.getInt(JSON_Auton1Reliability);
        mAuton2Score=json.getInt(JSON_Auton2Score);
        mAuton2Reliability=json.getInt(JSON_Auton2Reliability);
        mAutonNotes=json.getString(JSON_AutonNotes);
        mSynchronized=json.getBoolean(JSON_AutonSynchronized);
        mScore=json.getDouble(JSON_OverallScore);
        mAuton1ImagePath=json.getString(JSON_Auton1ImagePath);
        mAuton2ImagePath=json.getString(JSON_Auton2ImagePath);
        mAuton1ImageName=json.getString(JSON_Auton1ImageName);
        mAuton2ImageName=json.getString(JSON_Auton2ImageName);
        try{mPictureUri = Uri.parse(json.getString(JSON_MainPictureUri));} catch (Exception e){e.printStackTrace();}
        usesDefaultPicture=json.getBoolean(JSON_UsesDefaultPic);
        timeVisited = json.getInt(JSON_TimeVisited);
        driveMotors=json.getInt(JSON_DriveMotors);
        liftMotors=json.getInt(JSON_LiftMotors);
        intakeMotors=json.getInt(JSON_IntakeMotors);
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_Number,mNumber);
        json.put(JSON_Name,mName);
        json.put(JSON_Club,mClub);
        json.put(JSON_DriveType,mDriveType);
        json.put(JSON_DriveScore,mDriveScore);
        json.put(JSON_DriveNotes,mDriveNotes);
        json.put(JSON_Intake1Score,mIntake1Score);
        json.put(JSON_Intake2Score,mIntake2Score);
        json.put(JSON_IntakeNotes,mIntakeNotes);
        json.put(JSON_LiftType,mLiftType);
        json.put(JSON_LiftScore,mLiftScore);
        json.put(JSON_LiftNotes,mLiftNotes);
        json.put(JSON_Auton1Score,mAuton1Score);
        json.put(JSON_Auton1Reliability,mAuton1Reliability);
        json.put(JSON_Auton2Score,mAuton2Score);
        json.put(JSON_Auton2Reliability,mAuton2Reliability);
        json.put(JSON_AutonNotes,mAutonNotes);
        json.put(JSON_AutonSynchronized,mSynchronized);
        json.put(JSON_OverallScore,mScore);
        json.put(JSON_Auton1ImagePath,mAuton1ImagePath);
        json.put(JSON_Auton2ImagePath,mAuton2ImagePath);
        json.put(JSON_Auton1ImageName,mAuton1ImageName);
        json.put(JSON_Auton2ImageName,mAuton2ImageName);
        if (mPictureUri!=null) json.put(JSON_MainPictureUri,mPictureUri);
        json.put(JSON_UsesDefaultPic,usesDefaultPicture);
        json.put(JSON_TimeVisited, timeVisited);
        json.put(JSON_DriveMotors,driveMotors);
        json.put(JSON_LiftMotors,liftMotors);
        json.put(JSON_IntakeMotors,intakeMotors);
        return json;
    }

    @Override
    public String toString(){
        return mNumber.toString();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getClub() {
        return mClub;
    }

    public void setClub(String mClub) {
        this.mClub = mClub;
    }

    public String getDriveType() {
        return mDriveType;
    }

    public void setDriveType(String mDriveType) {
        this.mDriveType = mDriveType;
    }

    public Double getDriveScore() {
        return mDriveScore;
    }

    public void setDriveScore(Double mDriveScore) {
        this.mDriveScore = mDriveScore;
    }

    public String getDriveNotes() {
        return mDriveNotes;
    }

    public void setDriveNotes(String mDriveNotes) {
        this.mDriveNotes = mDriveNotes;
    }

    public Double getIntake1Score() {
        return mIntake1Score;
    }

    public void setIntake1Score(Double mIntake1Score) {
        this.mIntake1Score = mIntake1Score;
    }

    public Double getIntake2Score() {
        return mIntake2Score;
    }

    public void setIntake2Score(Double mIntake2Score) {
        this.mIntake2Score = mIntake2Score;
    }

    public String getIntakeNotes() {
        return mIntakeNotes;
    }

    public void setIntakeNotes(String mIntakeNotes) {
        this.mIntakeNotes = mIntakeNotes;
    }

    public String getLiftType() {
        return mLiftType;
    }

    public void setLiftType(String mLiftType) {
        this.mLiftType = mLiftType;
    }

    public Double getLiftScore() {
        return mLiftScore;
    }

    public void setLiftScore(Double mLiftScore) {
        this.mLiftScore = mLiftScore;
    }

    public String getLiftNotes() {
        return mLiftNotes;
    }

    public void setLiftNotes(String mLiftNotes) {
        this.mLiftNotes = mLiftNotes;
    }

    public Integer getAuton1Score() {
        return mAuton1Score;
    }

    public void setAuton1Score(Integer mAuton1Score) {
        this.mAuton1Score = mAuton1Score;
    }

    public Integer getAuton1Reliability() {
        return mAuton1Reliability;
    }

    public void setAuton1Reliability(Integer mAuton1Reliability) {
        this.mAuton1Reliability = mAuton1Reliability;
    }

    public Integer getAuton2Score() {
        return mAuton2Score;
    }

    public void setAuton2Score(Integer mAuton2Score) {
        this.mAuton2Score = mAuton2Score;
    }

    public Integer getAuton2Reliability() {
        return mAuton2Reliability;
    }

    public void setAuton2Reliability(Integer mAuton2Reliability) {
        this.mAuton2Reliability = mAuton2Reliability;
    }

    public String getAutonNotes() {
        return mAutonNotes;
    }

    public void setAutonNotes(String mAutonNotes) {
        this.mAutonNotes = mAutonNotes;
    }

    public Boolean getSynchronized() {
        return mSynchronized;
    }

    public void setSynchronized(Boolean aSynchronized) {
        mSynchronized = aSynchronized;
    }

    public Double getScore() {
        return mScore;
    }
    public void setScore(Double mScore) {
        this.mScore = mScore;
    }

    public String getmAuton1ImagePath() {
        return mAuton1ImagePath;
    }

    public void setmAuton1ImagePath(String mAuton1ImagePath) {
        this.mAuton1ImagePath = mAuton1ImagePath;
    }

    public String getmAuton2ImagePath() {
        return mAuton2ImagePath;
    }

    public void setmAuton2ImagePath(String mAuton2ImagePath) {
        this.mAuton2ImagePath = mAuton2ImagePath;
    }

    public String getmAuton1ImageName() {
        return mAuton1ImageName;
    }

    public void setmAuton1ImageName(String mAuton1ImageName) {
        this.mAuton1ImageName = mAuton1ImageName;
    }

    public String getmAuton2ImageName() {
        return mAuton2ImageName;
    }

    public void setmAuton2ImageName(String mAuton2ImageName) {
        this.mAuton2ImageName = mAuton2ImageName;
    }

    public Uri getmPictureUri() {
        return mPictureUri;
    }

    public void setmPictureUri(Uri mPictureUri) {
        this.mPictureUri = mPictureUri;
    }

    public Boolean getUsesDefaultPicture() {
        return usesDefaultPicture;
    }

    public void setUsesDefaultPicture(Boolean usesDefaultPicture) {
        this.usesDefaultPicture = usesDefaultPicture;
    }

    public Integer getIntakeMotors() {
        return intakeMotors;
    }

    public void setIntakeMotors(Integer intakeMotors) {
        this.intakeMotors = intakeMotors;
    }

    public Integer getTimeVisited() {
        return timeVisited;
    }

    public void setTimeVisited(Integer timeVisited) {
        this.timeVisited = timeVisited;
    }

    public Integer getDriveMotors() {
        return driveMotors;
    }

    public void setDriveMotors(Integer driveMotors) {
        this.driveMotors = driveMotors;
    }

    public Integer getLiftMotors() {
        return liftMotors;
    }

    public void setLiftMotors(Integer liftMotors) {
        this.liftMotors = liftMotors;
    }
}

