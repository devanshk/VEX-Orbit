package com.devanshkukreja.navdrawertest3.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.devanshkukreja.navdrawertest3.Activities.AutonDrawActivity;
import com.devanshkukreja.navdrawertest3.Activities.MainActivity;
import com.devanshkukreja.navdrawertest3.Activities.TeamListActivity;
import com.devanshkukreja.navdrawertest3.Helpers.LoadImage;
import com.devanshkukreja.navdrawertest3.Helpers.Team;
import com.devanshkukreja.navdrawertest3.Helpers.TeamOrganizer;
import com.devanshkukreja.navdrawertest3.Helpers.VaporVars;
import com.devanshkukreja.navdrawertest3.R;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

/**
 * Created by devanshk on 7/1/2014.
 */
public class TeamFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "TeamFragment";
    public static final String EXTRA_TEAM_ID="com.bignerdranch.android.myapplication2.team_id";
    public static final String EXTRA_AUTON_NUMBER="com.bignerdranch.android.myapplication2.team_auton_number";

    private static final int TAKE_PICTURE = 2; //Used in onActivityResult() to identify intent
    private static final int PICK_PICTURE = 3; //Used in onActivityResult() to identify intent
    private Uri imageUri; //Used in redundant ways. Clean up.
    private Boolean fromAuton=false; //Indicates whether it came from the AutonDrawingFragment

    //Initializes control widgets to modify team properties
    private Team mTeam;
    private Spinner drive_type_spinner, lift_type_spinner;
    private ArrayAdapter<CharSequence> driveAdapter;
    private ArrayAdapter<CharSequence> liftAdapter;
    private SeekBar DriveSeekBar,Intake1SeekBar,Intake2SeekBar,LiftSeekBar,Auton1SeekBar,Auton2SeekBar;
    private EditText NumberField, NameField, ClubField,DriveNotesField,IntakeNotesField,LiftNotesField,AutonNotesField,Auton1ScoreField,Auton2ScoreField;
    private TextView MainPictureHelper,MainNumber, MainTeamName, MainClubName, MainScore, DynamicDriveScore, DynamicIntake1Score,DynamicIntake2Score,DynamicLiftScore,DynamicAuton1Reliability,DynamicAuton2Reliability, CustomDriveField, CustomLiftField, Auton1Helper, Auton2Helper, DeleteButton;
    private ToggleButton AutonSynchronizedButton;
    private Double mDriveScore,mLiftScore,mIntake1Score,mIntake2Score;
    private String mDriveType, mLiftType;
    private Integer mAuton1Reliability,mAuton2Reliability,mAuton1Score,mAuton2Score, mTimeVisited;
    private ImageView mAuton1Viewer,mAuton2Viewer, mTeamPicture;
    private ProgressBar mMainProgressBar;
    private NumberPicker mDriveMotorsField,mLiftMotorsField,mIntakeMotorsField;
    private ScrollView mScrollView; //Allows calling of a scroll method if needed

    public static TeamFragment newInstance(UUID teamId){ //Starts this fragment with a reference to the team it should display
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TEAM_ID,teamId);

        TeamFragment fragment = new TeamFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID teamId=(UUID)getActivity().getIntent().getSerializableExtra(EXTRA_TEAM_ID); //Gets team ID in question
        mTeam = TeamOrganizer.get(getActivity()).getTeam(teamId);
        if (mTeam == null) { //If this came from AutonDrawingFragment, access global variable teamID
            mTeam = TeamOrganizer.get(getActivity()).getTeam(TeamListActivity.teamID);
            fromAuton=true;
        }
        mTeam.setTimeVisited(mTeam.getTimeVisited()+1);
    }

    @Override
    public void onPause(){
        super.onPause();
        TeamOrganizer.get(getActivity()).saveTeams(); //Saves teams whenever the fragment is paused
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG, "item Id is: "+item.getItemId());
        switch (item.getItemId()){
            case android.R.id.home: //Returns to TeamListActivity
                Log.d(TAG,"Attempting to navigate up from activity.");
                if (NavUtils.getParentActivityName(getActivity())!=null) //Checks if this has a parent activity
                    NavUtils.navigateUpFromSameTask(getActivity()); //Navigates back to parent activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_team, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        loadContent(v); //Loads the control widgets and sets them to their proper values
        loadListeners(); //Initializes all control widget listeners
        return v;
    }

    private void updateMainScore(){ //Recalculates overall team score
        Double mAutonScore, mAutonReliability;
        if (mAuton1Score*mAuton1Reliability>mAuton2Score*mAuton2Reliability){ //Selects 'best' autonomous
            mAutonScore = mAuton1Score*.8;
            mAutonReliability = (double)mAuton1Reliability/10;
        }
        else{
            mAutonScore = mAuton2Score*.8;
            mAutonReliability=(double)mAuton2Reliability/10;
        }
        Double averageIntakeScore=(mIntake1Score+mIntake2Score)/2; //Weights each score differently to calculate a final score
        Double intelliScore = mDriveScore*.3 + mLiftScore* .32 + averageIntakeScore*.3 + mAutonScore*mAutonReliability*.010;
        mTeam.setScore(intelliScore);
        MainScore.setText(""+String.format("%.1f",intelliScore));
    }

    private void loadListeners(){ //Sets up all control widget impacts

        //TextView Watchers
        NumberField.addTextChangedListener(new TextWatcher() { //Updates Primary Team Number when changed
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mTeam.setNumber(charSequence.toString());
                MainNumber.setText(charSequence.toString());
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
        NameField.addTextChangedListener(new TextWatcher() { //Updates Primary Team Name when changed
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mTeam.setName(charSequence.toString());
                MainTeamName.setText(charSequence.toString());
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
        ClubField.addTextChangedListener(new TextWatcher() { //Updates Primary Team Club when changed
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mTeam.setClub(charSequence.toString());
                MainClubName.setText(charSequence.toString());
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
        Auton1ScoreField.addTextChangedListener(new TextWatcher() { //Updates mTeam's Auton1 score
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try{
                    mAuton1Score=Integer.parseInt(charSequence.toString());
                    mTeam.setAuton1Score(mAuton1Score);
                    updateMainScore();}
                catch (NumberFormatException e){}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
        Auton2ScoreField.addTextChangedListener(new TextWatcher() { //Updates mTeam's Auton2 score
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try{
                    mAuton2Score=Integer.parseInt(charSequence.toString());
                    mTeam.setAuton2Score(mAuton2Score);
                    updateMainScore();}
                catch (NumberFormatException e){}
            }
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override public void afterTextChanged(Editable editable) {}
        });
        CustomLiftField.addTextChangedListener(new TextWatcher() { //Updates lift type, assuming lift type is other
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mLiftType=charSequence.toString();
                mTeam.setLiftType(mLiftType);
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
        CustomDriveField.addTextChangedListener(new TextWatcher() { //Updates drive type, assuming drive type is other
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mDriveType=charSequence.toString();
                mTeam.setDriveType(mDriveType);
            }
            @Override public void afterTextChanged(Editable editable) {}
        });

        //SeekBar Watchers
        DriveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //Updates mTeam's drive score
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                DynamicDriveScore.setText(""+String.format("%.1f",(double)progress/10));
                mDriveScore=(double)progress/10;
                mTeam.setDriveScore(mDriveScore);
                updateMainScore();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Intake1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //Update mTeam's intake1 score
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                DynamicIntake1Score.setText(""+String.format("%.1f",(double)progress/10));
                mIntake1Score=(double)progress/10;
                mTeam.setIntake1Score(mIntake1Score);
                updateMainScore();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Intake2SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //Updates mTeam's Intake2 score
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                DynamicIntake2Score.setText(""+String.format("%.1f",(double)progress/10));
                mIntake2Score=(double)progress/10;
                mTeam.setIntake2Score(mIntake2Score);
                updateMainScore();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        LiftSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //Updates mTeam's Lift score
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                DynamicLiftScore.setText(""+String.format("%.1f",(double)progress/10));
                mLiftScore=(double)progress/10;
                mTeam.setLiftScore(mLiftScore);
                updateMainScore();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Auton1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //Updates mTeam's Auton1 score
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                DynamicAuton1Reliability.setText(progress + "%");
                mAuton1Reliability=(Integer)progress;
                mTeam.setAuton1Reliability(mAuton1Reliability);
                updateMainScore();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Auton2SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //Updates mTeam's Auton2 score
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                DynamicAuton2Reliability.setText(progress + "%");
                mAuton2Reliability=(Integer)progress;
                mTeam.setAuton2Reliability(mAuton2Reliability);
                updateMainScore();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //ToggleButton Watchers
        AutonSynchronizedButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //Updates mTeam's AutonSynchronized. Also changes button colors
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    compoundButton.setBackgroundColor(getResources().getColor(R.color.toggle_on_color));
                else
                    compoundButton.setBackgroundColor(getResources().getColor(R.color.toggle_off_color));
                mTeam.setSynchronized(b);
            }
        });
        DeleteButton.setOnClickListener(new View.OnClickListener() { //Deletes team if user gets past second check
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity()); //Dialog to confirm user wants to delete team
                builder1.setMessage("Are you sure you want to delete this team?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TeamOrganizer.get(getActivity()).deleteTeam(mTeam); //Deletes current team
                                if (NavUtils.getParentActivityName(getActivity())!=null) //Checks if this has a parent activity
                                    NavUtils.navigateUpFromSameTask(getActivity()); //Navigates back to parent activity
                                //dialog.cancel();
                            }
                        });
                builder1.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        //Spinner Watchers
        drive_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Updates mTeam's drive type
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDriveType=(String)adapterView.getItemAtPosition(i);
                try{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.header_color));
                    ((TextView) adapterView.getChildAt(0)).setTextSize(25);} catch(NullPointerException e){}
                if (mDriveType.equals("Other"))
                    CustomDriveField.setVisibility(View.VISIBLE);
                else
                    CustomDriveField.setVisibility(View.GONE);
                //Toast.makeText(getActivity(), mDriveType, Toast.LENGTH_SHORT).show();
                mTeam.setDriveType(mDriveType);
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        lift_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Updates mTeam's lift type
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mLiftType=(String)adapterView.getItemAtPosition(i);
                try{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.header_color));
                    ((TextView) adapterView.getChildAt(0)).setTextSize(25);} catch (NullPointerException e){}
                if (mLiftType.equals("Other"))
                    CustomLiftField.setVisibility(View.VISIBLE);
                else
                    CustomLiftField.setVisibility(View.GONE);
                //Toast.makeText(getActivity(), mLiftType, Toast.LENGTH_SHORT).show();
                mTeam.setLiftType(mLiftType);
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //ImageView Watchers
        mAuton1Viewer.setOnClickListener(new View.OnClickListener() { //Starts AutonDrawFragment for auton 1 when clicked
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),AutonDrawActivity.class);
                i.putExtra(EXTRA_TEAM_ID,mTeam.getId());
                i.putExtra(EXTRA_AUTON_NUMBER,1);
                VaporVars.currentTeam = mTeam;
                VaporVars.currentAutonNumber = 1;
                startActivityForResult(i,1);
            }
        });
        mAuton2Viewer.setOnClickListener(new View.OnClickListener() { //Starts AutonDrawFragment for auton 2 when clicked
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),AutonDrawActivity.class);
                i.putExtra(EXTRA_TEAM_ID,mTeam.getId());
                i.putExtra(EXTRA_AUTON_NUMBER,2);
                VaporVars.currentTeam = mTeam;
                VaporVars.currentAutonNumber = 2;
                startActivityForResult(i,1);
            }
        });
        mTeamPicture.setOnClickListener(new View.OnClickListener() { //Gives option to change team picture when clicked
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity()); //Asks where the pic should come from
                builder1.setMessage("What do you want to replace team picture with?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Pick from Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mTeam.setUsesDefaultPicture(false);
                                pickPhotoFromGallery(); //Sends intent to get a new picture from the gallery
                            }
                        }
                );
                builder1.setNeutralButton("Reset to Default", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //Replaces picture with default placeholder pic
                        Bitmap temp = BitmapFactory.decodeResource(getActivity().getBaseContext().getResources(),
                                R.drawable.picture_placeholder_tvdpi);
                        mTeam.setmPictureUri(saveBitmap(temp));
                        mTeamPicture.setImageResource(R.drawable.picture_placeholder_tvdpi);
                        mTeam.setUsesDefaultPicture(true);
                    }
                });
                builder1.setNegativeButton("Take New Pic",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mTeam.setUsesDefaultPicture(false);
                                takePhoto(); //Sends intent to get a new picture from the camera
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        //ImagePicker Listeners
        mDriveMotorsField.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTeam.setDriveMotors(newVal);
                mLiftMotorsField.setMaxValue(10-mDriveMotorsField.getValue()-mIntakeMotorsField.getValue());
                mIntakeMotorsField.setMaxValue(10-mDriveMotorsField.getValue()-mLiftMotorsField.getValue());
            }
        });
        mLiftMotorsField.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTeam.setLiftMotors(newVal);
                mDriveMotorsField.setMaxValue(10-mLiftMotorsField.getValue()-mIntakeMotorsField.getValue());
                mIntakeMotorsField.setMaxValue(10-mDriveMotorsField.getValue()-mLiftMotorsField.getValue());
            }
        });
        mIntakeMotorsField.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTeam.setIntakeMotors(newVal);
                mDriveMotorsField.setMaxValue(10-mLiftMotorsField.getValue()-mIntakeMotorsField.getValue());
                mLiftMotorsField.setMaxValue(10-mDriveMotorsField.getValue()-mIntakeMotorsField.getValue());
            }
        });
    }

    public void pickPhotoFromGallery(){ //Sends an intent to get a saved picture
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_PICTURE);
    }

    private Uri saveBitmap(Bitmap bitmap){
        File bmap = new File(Environment.getExternalStorageDirectory(), "TeamPicture" + mTeam.getId() + ".jpg");
        try {

            FileOutputStream fos = new FileOutputStream(bmap);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return Uri.fromFile(bmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG,"Unable to save bitmap.");
        return null;
    }

    private void addUriToGallery(Uri contentUri) { //Adds picture to gallery
        Log.d(TAG,"Adding Uri to Gallery");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public void takePhoto() { //Take a new picture with camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Date d = new Date();
        CharSequence s  = DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());
        File photo = new File(Environment.getExternalStorageDirectory(), "TeamPicture" + s + ".png");
        Log.d(TAG,"New Uri is " +Uri.fromFile(photo) + "and old Uri is "+mTeam.getmPictureUri());
        if (Uri.fromFile(photo).getPath().equals(mTeam.getmPictureUri().getPath())) { //Gives it a unique uri
            photo = new File(Environment.getExternalStorageDirectory(), "TeamPicture"+TeamListActivity.iteration + mTeam.getId() + ".jpg");
            TeamListActivity.iteration++;
            Log.d(TAG,"Attempted Workaround. New Uri is " +Uri.fromFile(photo) + "and old Uri is "+mTeam.getmPictureUri());
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE: //If a picture was just taken from the camera, save the Uri and set the team pic
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    //Beta Image Loading Code
                    Log.d(TAG, getView().toString());
                    addUriToGallery(imageUri);
                    mTeam.setmPictureUri(imageUri);
                    new LoadImage(mTeamPicture,imageUri,mMainProgressBar).execute();
                    //setPic(mTeamPicture,selectedImage.getPath()); //Deprecated Image Setting Code
                    //mTeam.setUsesDefaultPicture(false);
                }
            case PICK_PICTURE: //If a picture was just picked from the gallery, save the Uri and set the team pic
                if (resultCode==Activity.RESULT_OK){
                    try {
                        Bitmap bitmap = getBitmapFromCameraData(data, getActivity());
                        //Beta Image Loading Code
                        new LoadImage(mTeamPicture,bitmap,mMainProgressBar,mTeam).execute();
                        //setPic(mTeamPicture, selectedImage.getPath()); //Deprecated Image Setting Code
                    }
                    catch (Exception e){ //In case picture somehow isn't found
                        Log.d(TAG, "While loading pic from gallery, caught "+e);
                    }
                }
        }
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    public void setTeamEditable(Boolean v){
        mTeamPicture.setEnabled(v);
        NameField.setEnabled(v);
        NumberField.setEnabled(v);
        ClubField.setEnabled(v);
        drive_type_spinner.setEnabled(v);
        DriveSeekBar.setEnabled(v);
        DriveNotesField.setEnabled(v);
        CustomDriveField.setEnabled(v);
        Intake1SeekBar.setEnabled(v);
        Intake2SeekBar.setEnabled(v);
        IntakeNotesField.setEnabled(v);
        lift_type_spinner.setEnabled(v);
        LiftSeekBar.setEnabled(v);
        LiftNotesField.setEnabled(v);
        CustomLiftField.setEnabled(v);
        AutonSynchronizedButton.setEnabled(v);
        Auton1ScoreField.setEnabled(v);
        Auton1SeekBar.setEnabled(v);
        mAuton1Viewer.setEnabled(v);
        Auton2ScoreField.setEnabled(v);
        Auton2ScoreField.setEnabled(v);
        Auton2SeekBar.setEnabled(v);
        mAuton2Viewer.setEnabled(v);
        AutonNotesField.setEnabled(v);
        mDriveMotorsField.setEnabled(v);
        mLiftMotorsField.setEnabled(v);
        mIntakeMotorsField.setEnabled(v);
        if (v) {
            DeleteButton.setVisibility(View.VISIBLE);
            MainPictureHelper.setVisibility(View.VISIBLE);
            Animation rock = AnimationUtils.loadAnimation(getActivity(),R.anim.rock);
            if (mTeam.getmAuton1ImagePath().length()<=1){
                Auton1Helper.setVisibility(View.VISIBLE);
                mAuton1Viewer.startAnimation(rock);}
            if (mTeam.getmAuton2ImagePath().length()<=1){
                Auton2Helper.setVisibility(View.VISIBLE);
                mAuton2Viewer.startAnimation(rock);}

            NumberField.setHint(getResources().getString(R.string.team_number_hint));
            NameField.setHint(getResources().getString(R.string.team_name_hint));
            ClubField.setHint(getResources().getString(R.string.team_club_hint));
            CustomDriveField.setHint(getResources().getString(R.string.custom_drive_hint));
            DriveNotesField.setHint(getResources().getString(R.string.drive_notes_hint));
            IntakeNotesField.setHint(getResources().getString(R.string.intake_notes_hint));
            CustomLiftField.setHint(getResources().getString(R.string.custom_lift_hint));
            LiftNotesField.setHint(getResources().getString(R.string.lift_notes_hint));
            Auton1ScoreField.setHint(getResources().getString(R.string.auton_score_hint));
            Auton2ScoreField.setHint(getResources().getString(R.string.auton_score_hint));
            AutonNotesField.setHint(getResources().getString(R.string.auton_notes_hint));
        }
        else {
            DeleteButton.setVisibility(View.GONE);
            MainPictureHelper.setVisibility(View.GONE);
            Auton1Helper.setVisibility(View.GONE);
            Auton2Helper.setVisibility(View.GONE);

            mAuton1Viewer.clearAnimation();
            mAuton2Viewer.clearAnimation();

            NumberField.setHint("");
            NameField.setHint("");
            ClubField.setHint("");
            CustomDriveField.setHint("");
            DriveNotesField.setHint("");
            IntakeNotesField.setHint("");
            CustomLiftField.setHint("");
            LiftNotesField.setHint("");
            Auton1ScoreField.setHint("");
            Auton2ScoreField.setHint("");
            AutonNotesField.setHint("");
        }
    }

    private void reloadContent(){
        //Populate variables with team-specific variables
        Log.d(TAG,"mTeam is "+mTeam.toString());
        if (mTeam.getScore()!=null){MainScore.setText(String.format("%.1f",mTeam.getScore()));}
        if (mTeam.getName()!=null){NameField.setText(mTeam.getName());
            MainTeamName.setText(mTeam.getName());}
        if (mTeam.getNumber()!=null){NumberField.setText(mTeam.getNumber());
            MainNumber.setText(mTeam.getNumber());}
        if (mTeam.getClub()!=null){ClubField.setText(mTeam.getClub());
            MainClubName.setText(mTeam.getClub());}
        if (mTeam.getDriveType()!=null)
        {mDriveType=mTeam.getDriveType();
            drive_type_spinner.setSelection(driveAdapter.getPosition(mDriveType));
            if (driveAdapter.getPosition(mDriveType)==-1)
            {drive_type_spinner.setSelection(driveAdapter.getPosition("Other"));
                CustomDriveField.setVisibility(View.VISIBLE);
                CustomDriveField.setText(mDriveType);}}
        if (mTeam.getLiftType()!=null)
        {mLiftType=mTeam.getLiftType();
            lift_type_spinner.setSelection(liftAdapter.getPosition(mLiftType));
            if (liftAdapter.getPosition(mLiftType)==-1)
            {lift_type_spinner.setSelection(liftAdapter.getPosition("Other"));
                CustomLiftField.setVisibility(View.VISIBLE);
                CustomLiftField.setText(mLiftType);}}
        if (mTeam.getDriveScore()!=null){DriveSeekBar.setProgress((int)Math.round(mTeam.getDriveScore()*10));
            mDriveScore=mTeam.getDriveScore();
            DynamicDriveScore.setText(""+String.format("%.1f",(mDriveScore)));}
        if (mTeam.getDriveNotes()!=null){DriveNotesField.setText(mTeam.getDriveNotes());}
        if (mTeam.getIntake1Score()!=null){Intake1SeekBar.setProgress((int)Math.round(mTeam.getIntake1Score()*10));
            mIntake1Score=mTeam.getIntake1Score();
            DynamicIntake1Score.setText(""+String.format("%.1f",mIntake1Score));}
        if (mTeam.getIntake2Score()!=null){Intake2SeekBar.setProgress((int)Math.round(mTeam.getIntake2Score()*10));
            mIntake2Score=mTeam.getIntake2Score();
            DynamicIntake2Score.setText(""+String.format("%.1f",mIntake2Score));}
        if (mTeam.getIntakeNotes()!=null){IntakeNotesField.setText(mTeam.getIntakeNotes());}
        if (mTeam.getLiftScore()!=null){LiftSeekBar.setProgress((int)Math.round(mTeam.getLiftScore()*10));
            mLiftScore=(mTeam.getLiftScore());
            DynamicLiftScore.setText(""+String.format("%.1f",mLiftScore));}
        if (mTeam.getLiftNotes()!=null){LiftNotesField.setText(mTeam.getLiftNotes());}
        if (mTeam.getSynchronized()!=null){AutonSynchronizedButton.setChecked(mTeam.getSynchronized());
            if (mTeam.getSynchronized())
                AutonSynchronizedButton.setBackgroundColor(getResources().getColor(R.color.toggle_on_color));
            else
                AutonSynchronizedButton.setBackgroundColor(getResources().getColor(R.color.toggle_off_color));}
        if (mTeam.getAuton1Score()!=null&&mTeam.getAuton1Score()>0){Auton1ScoreField.setText(mTeam.getAuton1Score().toString());}
        if (mTeam.getAuton1Reliability()!=null){Auton1SeekBar.setProgress(mTeam.getAuton1Reliability());
            mAuton1Reliability=(Math.round(mTeam.getAuton1Reliability()));
            DynamicAuton1Reliability.setText(mAuton1Reliability+"%");}
        if (mTeam.getAuton2Score()!=null&&mTeam.getAuton2Score()>0){Auton2ScoreField.setText(mTeam.getAuton2Score().toString());}
        if (mTeam.getAuton2Reliability()!=null){Auton2SeekBar.setProgress(mTeam.getAuton2Reliability());
            mAuton2Reliability=(Math.round(mTeam.getAuton2Reliability()));
            DynamicAuton2Reliability.setText(mAuton2Reliability+"%");}
        if (mTeam.getAutonNotes()!=null){AutonNotesField.setText(mTeam.getAutonNotes());}

        //Setting NumberPickers
        if (mTeam.getDriveMotors()!=null){mDriveMotorsField.setValue(mTeam.getDriveMotors());}
        if (mTeam.getLiftMotors()!=null){mLiftMotorsField.setValue(mTeam.getLiftMotors());}
        if (mTeam.getIntakeMotors()!=null){mIntakeMotorsField.setValue(mTeam.getIntakeMotors());}

        mDriveMotorsField.setMaxValue(10-mLiftMotorsField.getValue()-mIntakeMotorsField.getValue());
        mLiftMotorsField.setMaxValue(10-mDriveMotorsField.getValue()-mIntakeMotorsField.getValue());
        mIntakeMotorsField.setMaxValue(10-mLiftMotorsField.getValue()-mDriveMotorsField.getValue());

        //Setting autonomous pictures
        if (mTeam.getmAuton1ImagePath().length()>1) {
            try {
                mAuton1Viewer.setImageBitmap(AutonDrawFragment.loadImageFromStorage(mTeam.getmAuton1ImagePath(), mTeam.getmAuton1ImageName()));
                AutonDrawFragment.setSize(mAuton1Viewer, 300, 300);
                mAuton1Viewer.setAlpha(1f);
            } catch (Exception e) {}
        }
        else {
            mAuton1Viewer.setImageResource(R.drawable.skyrise_field);
            AutonDrawFragment.setSize(mAuton1Viewer, 120, 120);
            mAuton1Viewer.setAlpha(.5f);
        }

        if (mTeam.getmAuton2ImagePath().length()>1) {
            try {
                mAuton2Viewer.setImageBitmap(AutonDrawFragment.loadImageFromStorage(mTeam.getmAuton2ImagePath(), mTeam.getmAuton2ImageName()));
                AutonDrawFragment.setSize(mAuton2Viewer,300,300);
                mAuton2Viewer.setAlpha(1f);
            } catch (Exception e) {
            }
        }
        else {
            mAuton2Viewer.setImageResource(R.drawable.skyrise_field);
            AutonDrawFragment.setSize(mAuton2Viewer, 120, 120);
            mAuton2Viewer.setAlpha(.5f);
        }

        //Setting Main Team Picture
        if (mTimeVisited>1) {
            //Beta Image Loading Code
            if (!mTeam.getUsesDefaultPicture())
                new LoadImage(mTeamPicture,mTeam.getmPictureUri(),mMainProgressBar).execute();
            //setPic(mTeamPicture, mTeam.getmPictureUri().getPath()); //Deprecated Image Setting Code
            setTeamEditable(false);
        }
        else
        {
            //ScoutingActivity.setMode("edit");
            Bitmap temp = BitmapFactory.decodeResource(getActivity().getBaseContext().getResources(),
                    R.drawable.picture_placeholder_tvdpi);
            mTeam.setmPictureUri(saveBitmap(temp));
        }

        //If it just came from the auton drawing fragment, make sure everything is still editable
        if (fromAuton)
            setTeamEditable(true);
    }

    private void loadContent(View v){
        //Reference all layout widgets
        drive_type_spinner=(Spinner)v.findViewById(R.id.drive_type_spinner);
        lift_type_spinner=(Spinner)v.findViewById(R.id.lift_type_spinner);
        DriveSeekBar=(SeekBar)v.findViewById(R.id.drive_seek_bar);
        Intake1SeekBar=(SeekBar)v.findViewById(R.id.intake1_seek_bar);
        Intake2SeekBar=(SeekBar)v.findViewById(R.id.intake2_seek_bar);
        LiftSeekBar=(SeekBar)v.findViewById(R.id.lift_seek_bar);
        Auton1SeekBar=(SeekBar)v.findViewById(R.id.auton1_seek_bar);
        Auton2SeekBar=(SeekBar)v.findViewById(R.id.auton2_seek_bar);
        DriveNotesField=(EditText)v.findViewById(R.id.drive_notes);
        IntakeNotesField=(EditText)v.findViewById(R.id.intake_notes);
        LiftNotesField=(EditText)v.findViewById(R.id.lift_notes);
        AutonNotesField=(EditText)v.findViewById(R.id.auton_notes);
        Auton1ScoreField=(EditText)v.findViewById(R.id.auton1_score_field);
        Auton2ScoreField=(EditText)v.findViewById(R.id.auton2_score_field);
        NumberField=(EditText)v.findViewById(R.id.team_number_field);
        NameField=(EditText)v.findViewById(R.id.team_name_field);
        ClubField=(EditText)v.findViewById(R.id.team_club_field);
        CustomDriveField=(EditText) v.findViewById(R.id.custom_drive_type_field);
        CustomLiftField=(EditText) v.findViewById(R.id.custom_lift_type_field);
        MainPictureHelper=(TextView)v.findViewById(R.id.main_picture_helper);
        MainNumber=(TextView)v.findViewById(R.id.main_team_number);
        MainTeamName=(TextView)v.findViewById(R.id.main_team_name);
        MainClubName=(TextView)v.findViewById(R.id.main_club_name);
        MainScore=(TextView)v.findViewById(R.id.main_score);
        DynamicDriveScore=(TextView)v.findViewById(R.id.drive_dynamic_score);
        DynamicIntake1Score=(TextView)v.findViewById(R.id.intake1_dynamic_score);
        DynamicIntake2Score=(TextView)v.findViewById(R.id.intake2_dynamic_score);
        DynamicLiftScore=(TextView)v.findViewById(R.id.lift_dynamic_score);
        DynamicAuton1Reliability=(TextView)v.findViewById(R.id.auton1_dynamic_reliability);
        DynamicAuton2Reliability=(TextView)v.findViewById(R.id.auton2_dynamic_reliability);
        Auton1Helper = (TextView)v.findViewById(R.id.auton1_helper);
        Auton2Helper= (TextView)v.findViewById(R.id.auton2_helper);
        DeleteButton=(TextView)v.findViewById(R.id.delete_button);
        AutonSynchronizedButton=(ToggleButton)v.findViewById(R.id.auton_synchronize_button);
        mAuton1Viewer=(ImageView)v.findViewById(R.id.auton1_viewer);
        mAuton2Viewer=(ImageView)v.findViewById(R.id.auton2_viewer);
        mTeamPicture = (ImageView)v.findViewById(R.id.main_picture);
        mTeamPicture.setTag(mTeam.toString());
        mMainProgressBar = (ProgressBar)v.findViewById(R.id.main_progress_bar);
        mDriveMotorsField=(NumberPicker)v.findViewById(R.id.drive_motors_field);
        mLiftMotorsField=(NumberPicker)v.findViewById(R.id.lift_motors_field);
        mIntakeMotorsField=(NumberPicker)v.findViewById(R.id.intake_motors_field);
        mScrollView = (ScrollView)v.findViewById(R.id.teamScrollView);
        mTimeVisited=mTeam.getTimeVisited();

        //Set up MotorField NumberPickers
        mDriveMotorsField.setMinValue(0);
        mLiftMotorsField.setMinValue(0);
        mIntakeMotorsField.setMinValue(0);

        mDriveMotorsField.setMaxValue(10);
        mLiftMotorsField.setMaxValue(10);
        mIntakeMotorsField.setMaxValue(10);

        mDriveMotorsField.setValue(0);
        mLiftMotorsField.setValue(0);
        mIntakeMotorsField.setValue(0);

        mDriveMotorsField.setWrapSelectorWheel(true);
        mLiftMotorsField.setWrapSelectorWheel(true);
        mIntakeMotorsField.setWrapSelectorWheel(true);

        setNumberPickerTextColor(mDriveMotorsField, getResources().getColor(R.color.header_color));
        setNumberPickerTextColor(mLiftMotorsField,getResources().getColor(R.color.header_color));
        setNumberPickerTextColor(mIntakeMotorsField,getResources().getColor(R.color.header_color));

        //Populate Drive Type Spinner
        //Create an ArrayAdapter using the string array and a default spinner layout
        driveAdapter = ArrayAdapter.createFromResource (getActivity(), R.array.drive_types, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        driveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        drive_type_spinner.setAdapter(driveAdapter);
        //Populate Lift Type Spinner
        liftAdapter= ArrayAdapter.createFromResource(getActivity(),R.array.lift_types,android.R.layout.simple_spinner_item);
        liftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lift_type_spinner.setAdapter(liftAdapter);

        mAuton1Reliability=0;mAuton1Score=0;mAuton2Reliability=0;mAuton2Score=0;mDriveScore=0.0;mLiftScore=0.0;mIntake1Score=0.0;mIntake2Score=0.0;
        reloadContent();
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) //Used to change the color of a NumberPicker
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }

    public boolean isEnabled(){
        return (NameField.isEnabled());
    }
}
