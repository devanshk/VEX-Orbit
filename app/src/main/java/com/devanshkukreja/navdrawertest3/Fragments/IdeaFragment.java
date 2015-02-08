package com.devanshkukreja.navdrawertest3.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.devanshkukreja.navdrawertest3.Activities.AutonDrawActivity;
import com.devanshkukreja.navdrawertest3.Activities.DesignActivity;
import com.devanshkukreja.navdrawertest3.Activities.DrawDesignActivity;
import com.devanshkukreja.navdrawertest3.Activities.MainActivity;
import com.devanshkukreja.navdrawertest3.Helpers.Idea;
import com.devanshkukreja.navdrawertest3.Helpers.IdeaOrganizer;
import com.devanshkukreja.navdrawertest3.Helpers.LoadImage;
import com.devanshkukreja.navdrawertest3.Helpers.TeamOrganizer;
import com.devanshkukreja.navdrawertest3.R;

import org.w3c.dom.Text;

import java.lang.reflect.Field;

/**
 * Created by devanshk on 8/12/14.
 */
public class IdeaFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "IdeaFragment";

    private Idea mIdea;

    private ArrayAdapter<CharSequence> designTypeAdapter;
    private String[] gearOptions = new String[]{"12", "36", "60", "84"};
    private ImageView mainBackground, designDisplay;
    private TextView dynamicPowerText, dynamicSpeedText, deleteButton;
    private EditText nameField, pointsField;
    private Spinner typeSpinner;
    private NumberPicker motorsField, pistonsField, gearBefore, gearAfter;
    private SeekBar powerSeekBar, speedSeekBar;
    private LinearLayout motorPistonLayout, powerSpeedLayout, gearRatioLayout, routeScoreLayout;
    private View motorPistonDivider, powerSpeedDivider, gearRatioDivider, routeScoreDivider;
    private ProgressBar mainProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIdea = IdeaOrganizer.getIdea(MainActivity.ideaId);
        Log.d(TAG, "IdeaFragment Uri = "+mIdea.getPictureUri());
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"[Saving Ideas]");
        IdeaOrganizer.get(getActivity()).saveIdeas(); //Saves teams whenever the fragment is paused
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "IdeaFragment Uri = "+mIdea.getPictureUri());
        if (!mIdea.getUsesDefault())
            new LoadImage(designDisplay,mIdea.getPictureUri(),null,500,500).execute(); //Sets picture//
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.idea_landing, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        loadContent(v); //Loads the control widgets and sets them to their proper values
        loadData();
        loadListeners(); //Initializes all control widget listeners
        return v;
    }

    private void loadData() {
        //Populate Idea Type Spinner
        //Create an ArrayAdapter using the string array and a default spinner layout
        designTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.idea_types, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        designTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        typeSpinner.setAdapter(designTypeAdapter);

        //Set Min/Max for Motor/Piston Number Pickers
        motorsField.setMinValue(0);
        pistonsField.setMinValue(0);
        motorsField.setMaxValue(10);
        pistonsField.setMaxValue(10);
        motorsField.setWrapSelectorWheel(true);
        pistonsField.setWrapSelectorWheel(true);

        //Set data values from mIdea
        typeSpinner.setSelection(designTypeAdapter.getPosition(mIdea.getType().toString()));
        nameField.setText(mIdea.getName());
        pointsField.setText("" + mIdea.getPoints());
        motorsField.setValue(mIdea.getMotors());
        pistonsField.setValue(mIdea.getPistons());
        powerSeekBar.setProgress(mIdea.getPower());
        speedSeekBar.setProgress(mIdea.getSpeed());
        dynamicPowerText.setText("" + String.format("%.1f", (double) mIdea.getPower() / 10));
        dynamicSpeedText.setText("" + String.format("%.1f", (double) mIdea.getSpeed() / 10));
        gearBefore.setValue(mIdea.getGearBefore());
        gearAfter.setValue(mIdea.getGearAfter());
    }

    private void refreshView(Idea.Type type) {
        clearViews(); //Makes all units gone
        if (mIdea.getUsesDefault()) {
            switch (type) { //Sets main image in case there is no saved
                case Custom:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.blueprint_preview));
                    loadDefaultEngineeringView();
                    break;
                case Lift:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.orangeprint_preview));
                    loadDefaultEngineeringView();
                    break;
                case Drive:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.redprint_preview));
                    loadDefaultEngineeringView();
                    break;
                case Intake:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.purpleprint_preview));
                    loadDefaultEngineeringView();
                    break;
                case Gear:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.greenprint_preview));
                    gearRatioLayout.setVisibility(View.VISIBLE);
                    gearRatioDivider.setVisibility(View.VISIBLE);
                    break;
                case Route:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.skyrise_field));
                    routeScoreDivider.setVisibility(View.VISIBLE);
                    routeScoreLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
        else {
            new LoadImage(designDisplay, mIdea.getPictureUri(), mainProgressBar, 500, 500).execute(); //Sets picture
            switch (type) { //Sets main image when it's not using default
                case Custom:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.blueprint));
                    loadDefaultEngineeringView();
                    break;
                case Lift:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.orangeprint));
                    loadDefaultEngineeringView();
                    break;
                case Drive:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.redprint));
                    loadDefaultEngineeringView();
                    break;
                case Intake:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.purpleprint));
                    loadDefaultEngineeringView();
                    break;
                case Gear:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.greenprint));
                    gearRatioLayout.setVisibility(View.VISIBLE);
                    gearRatioDivider.setVisibility(View.VISIBLE);
                    break;
                case Route:
                    mainBackground.setImageDrawable(getResources().getDrawable(R.drawable.skyrise_field));
                    routeScoreDivider.setVisibility(View.VISIBLE);
                    routeScoreLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void loadDefaultEngineeringView(){
        motorPistonLayout.setVisibility(View.VISIBLE);
        motorPistonDivider.setVisibility(View.VISIBLE);
        powerSpeedDivider.setVisibility(View.VISIBLE);
        powerSpeedLayout.setVisibility(View.VISIBLE);
    }

    private void clearViews(){
        motorPistonDivider.setVisibility(View.GONE);
        motorPistonLayout.setVisibility(View.GONE);
        powerSpeedLayout.setVisibility(View.GONE);
        powerSpeedDivider.setVisibility(View.GONE);
        gearRatioDivider.setVisibility(View.GONE);
        gearRatioLayout.setVisibility(View.GONE);
        routeScoreDivider.setVisibility(View.GONE);
        routeScoreLayout.setVisibility(View.GONE);
    }

    private void loadContent(View v){
        mainBackground = (ImageButton)v.findViewById(R.id.idea_image_view);
        mainBackground.setTag("mainImage_"+mIdea.getId());
        designDisplay = (ImageView)v.findViewById(R.id.idea_design_display);
        designDisplay.setTag("designDisplay_"+mIdea.getId());
        dynamicPowerText = (TextView)v.findViewById(R.id.idea_dynamic_power);
        dynamicSpeedText = (TextView)v.findViewById(R.id.idea_dynamic_speed);
        deleteButton = (TextView)v.findViewById(R.id.idea_delete_button);
        nameField = (EditText)v.findViewById(R.id.idea_name_field);
        pointsField = (EditText)v.findViewById(R.id.idea_route_score_field);
        typeSpinner = (Spinner)v.findViewById(R.id.idea_type_spinner);
        motorsField = (NumberPicker)v.findViewById(R.id.idea_motors_number_picker);
        pistonsField = (NumberPicker)v.findViewById(R.id.idea_pistons_number_picker);
        gearBefore = (NumberPicker)v.findViewById(R.id.idea_gear_ratio_before);
        gearAfter = (NumberPicker)v.findViewById(R.id.idea_gear_ratio_after);
        powerSeekBar = (SeekBar)v.findViewById(R.id.idea_power_seekbar);
        speedSeekBar = (SeekBar)v.findViewById(R.id.idea_speed_seekbar);
        motorPistonLayout = (LinearLayout)v.findViewById(R.id.idea_motor_piston_layout);
        powerSpeedLayout = (LinearLayout)v.findViewById(R.id.idea_power_speed_layout);
        gearRatioLayout = (LinearLayout)v.findViewById(R.id.idea_gear_ratio_layout);
        routeScoreLayout = (LinearLayout)v.findViewById(R.id.idea_route_score_layout);
        motorPistonDivider = v.findViewById(R.id.idea_motor_piston_separator);
        powerSpeedDivider = v.findViewById(R.id.idea_power_speed_separator);
        gearRatioDivider = v.findViewById(R.id.idea_gear_ratio_separator);
        routeScoreDivider = v.findViewById(R.id.idea_route_score_separator);
        mainProgressBar = (ProgressBar) v.findViewById(R.id.idea_progress_bar);

        gearBefore.setMinValue(0);
        gearAfter.setMinValue(0);
        gearBefore.setMaxValue(gearOptions.length-1);
        gearAfter.setMaxValue(gearOptions.length-1);
        gearBefore.setDisplayedValues(gearOptions);
        gearAfter.setDisplayedValues(gearOptions);
        gearBefore.setWrapSelectorWheel(true);
        gearAfter.setWrapSelectorWheel(true);

        setNumberPickerTextColor(gearBefore, getResources().getColor(R.color.header_color));
        setNumberPickerTextColor(gearAfter, getResources().getColor(R.color.header_color));
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

    private void loadListeners(){
        //EditText Listeners
        nameField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIdea.setName(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });
        pointsField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {mIdea.setPoints(Integer.parseInt(s.toString()));} catch (NumberFormatException e){Log.d(TAG, "Invalid number format.");}
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        //NumberPicker Listeners
        motorsField.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mIdea.setMotors(newVal);
            }
        });
        pistonsField.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                mIdea.setPistons(newVal);
            }
        });

        //SeekBar Listeners
        powerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dynamicPowerText.setText(""+String.format("%.1f",(double)progress/10));
                mIdea.setPower(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dynamicSpeedText.setText(""+String.format("%.1f",(double)progress/10));
                mIdea.setSpeed(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Spinner Listeners
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = getResources().getStringArray(R.array.idea_types)[position];
                refreshView(Idea.parseType(selected)); //Refreshes view based on type enum found from string
                Log.d(TAG, "selected type = " + Idea.parseType(selected));
                Log.d(TAG, "mIdea = "+mIdea.toString());
                mIdea.setType(Idea.parseType(selected));
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        //ImageView Listeners
        mainBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdea.getType()== Idea.Type.Route) {
                    Intent i = new Intent(getActivity(), AutonDrawActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(getActivity(), DrawDesignActivity.class);
                    startActivity(i);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity()); //Dialog to confirm user wants to delete team
                builder1.setMessage("Are you sure you want to delete your design?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                IdeaOrganizer.get(getActivity()).deleteIdea(mIdea); //Deletes current team
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

        //NumberPicker Listeners
        gearBefore.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mIdea.setGearBefore(newVal);
            }
        });
        gearAfter.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mIdea.setGearAfter(newVal);
            }
        });
    }
}
