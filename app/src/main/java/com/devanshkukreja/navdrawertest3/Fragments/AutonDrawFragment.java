package com.devanshkukreja.navdrawertest3.Fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.devanshkukreja.navdrawertest3.Activities.MainActivity;
import com.devanshkukreja.navdrawertest3.Activities.TeamListActivity;
import com.devanshkukreja.navdrawertest3.Helpers.Idea;
import com.devanshkukreja.navdrawertest3.Helpers.IdeaOrganizer;
import com.devanshkukreja.navdrawertest3.Helpers.VaporVars;
import com.devanshkukreja.navdrawertest3.R;
import com.devanshkukreja.navdrawertest3.Helpers.Team;
import com.devanshkukreja.navdrawertest3.Helpers.TeamOrganizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by devanshk on 7/11/2014.
 */
public class AutonDrawFragment extends Fragment {
    public static final String EXTRA_TEAM_ID="com.bignerdranch.android.myapplication2.team_id";
    public static final String EXTRA_AUTON_NUMBER="com.bignerdranch.android.myapplication2.team_auton_number";
    private static final String TAG = "AutonDrawFragment";

    private String fileName;

    private Idea mIdea;
    private Team mTeam;
    private Integer mAutonNumber;
    private Paint borderPaint =new Paint(); private Paint ringPaint = new Paint();
    private Path mPath = new Path();
    private Bitmap baseBg,bg;
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Integer> undoneColors = new ArrayList<Integer>();
    private ArrayList<Integer> colors = new ArrayList<Integer>();
    private Canvas canvas;
    private ImageView fieldView,select1,select2,select3,select4,select5,select6,select7;
    private ImageButton undoButton,redoButton,colorButton1,colorButton2,colorButton3,colorButton4,colorButton5,colorButton6,colorButton7;
    private TextView clearButton;
    private Button mCancelButton, mSaveButton;
    private boolean userIsDrawingLine=false, fresh = false;
    private float downX,downY,upX,upY;

    public static AutonDrawFragment newInstance(UUID teamId, Integer autonNumber){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TEAM_ID,teamId);
        args.putSerializable(EXTRA_AUTON_NUMBER,autonNumber);

        AutonDrawFragment fragment = new AutonDrawFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public AutonDrawFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (MainActivity.fromPosition==2) {
            UUID teamId = (UUID) getActivity().getIntent().getSerializableExtra(EXTRA_TEAM_ID);
            Integer autonNumber = (Integer) getActivity().getIntent().getSerializableExtra(EXTRA_AUTON_NUMBER);
            //mTeam = TeamOrganizer.get(getActivity()).getTeam(teamId);
            mTeam = VaporVars.currentTeam;
            //mAutonNumber = autonNumber;
            mAutonNumber = VaporVars.currentAutonNumber;
        }
        else
            mIdea= IdeaOrganizer.get(getActivity()).getIdea(MainActivity.ideaId); //Loads current mIdea
        mTeam = VaporVars.currentTeam;
        mAutonNumber = VaporVars.currentAutonNumber;
        loadContent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_auton_draw, container, false);
        fieldView = (ImageView)rootView.findViewById(R.id.field_image_view);
        fieldView.setImageBitmap(bg);
        undoButton=(ImageButton)rootView.findViewById(R.id.undo_button);
        redoButton=(ImageButton)rootView.findViewById(R.id.redo_button);
        clearButton=(TextView)rootView.findViewById(R.id.clear_button);
        colorButton1=(ImageButton)rootView.findViewById(R.id.color1);
        colorButton2=(ImageButton)rootView.findViewById(R.id.color2);
        colorButton3=(ImageButton)rootView.findViewById(R.id.color3);
        colorButton4=(ImageButton)rootView.findViewById(R.id.color4);
        colorButton5=(ImageButton)rootView.findViewById(R.id.color5);
        colorButton6=(ImageButton)rootView.findViewById(R.id.color6);
        colorButton7=(ImageButton)rootView.findViewById(R.id.color7);
        mCancelButton=(Button)rootView.findViewById(R.id.drawing_cancel_button);
        mSaveButton=(Button)rootView.findViewById(R.id.drawing_save_button);

        select1=(ImageView)rootView.findViewById(R.id.select1);
        select2=(ImageView)rootView.findViewById(R.id.select2);
        select3=(ImageView)rootView.findViewById(R.id.select3);
        select4=(ImageView)rootView.findViewById(R.id.select4);
        select5=(ImageView)rootView.findViewById(R.id.select5);
        select6=(ImageView)rootView.findViewById(R.id.select6);
        select7=(ImageView)rootView.findViewById(R.id.select7);

        fieldView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == motionEvent.ACTION_DOWN) {
                    undoneColors.clear();
                    undonePaths.clear();
                    mPath= new Path();
                    userIsDrawingLine=false;
                    downX=motionEvent.getX(); downY=motionEvent.getY();
                }
                if (getDistance(downX,downY,motionEvent.getX(),motionEvent.getY())>50){
                    //Log.d(TAG, "Distance is "+ getDistance(downX,downY,motionEvent.getX(),motionEvent.getY()));
                    userIsDrawingLine=true;
                }
                if (motionEvent.getAction() == motionEvent.ACTION_UP) {
                    upX = motionEvent.getX();
                    upY = motionEvent.getY();
                        /*if (getDistance(downX,downY,upX,upY)>50){
                            userIsDrawingLine=true;
                        }*/
                    if (!userIsDrawingLine) {
                        mPath.moveTo(downX,downY);
                        mPath.addCircle(downX, downY, 40f, Path.Direction.CW);
                        bg = baseBg.copy(baseBg.getConfig(),true);
                        canvas = new Canvas(bg);
                        fieldView.setImageBitmap(bg);
                    } else {
                        //Draws Arrow
                        Float angleChange = (float) (Math.atan2((upY - downY), (upX - downX)));
                        mPath.moveTo(downX, downY); mPath.lineTo(upX, upY);

                        drawArrowHead(upX, upY, 25, angleChange); //ArrowHead
                    }
                    colors.add(ringPaint.getColor());
                    paths.add(mPath);
                    drawPaint(canvas);
                    fieldView.setImageBitmap(bg);
                }
                if (userIsDrawingLine) {
                    float X = motionEvent.getX();
                    float Y = motionEvent.getY();
                    float angleChange = (float) (Math.atan2((Y - downY), (X - downX)));
                    Integer oldColor = ringPaint.getColor();
                    //if (borderPaint.getColor()!=)

                    //Reset the screen
                    bg = baseBg.copy(baseBg.getConfig(),true);
                    canvas = new Canvas(bg);
                    fieldView.setImageBitmap(bg);

                    //Draw the old paths
                    for (int i = 0;i<paths.size();i++) {
                        Path p = paths.get(i);
                        Integer c = colors.get(i);
                        ringPaint.setColor(c);
                        canvas.drawPath(p, borderPaint);
                        canvas.drawPath(p, ringPaint);
                    }
                    //Draw the arrow preview
                    ringPaint.setColor(oldColor);
                    canvas.drawLine(downX,downY,X,Y,borderPaint);
                    drawRawArrowHead(X, Y, 25, angleChange,canvas,borderPaint);
                    canvas.drawLine(downX,downY,X,Y,ringPaint);
                    drawRawArrowHead(X,Y, 25, angleChange,canvas,ringPaint);
                }
                return true;
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener() { //If click undo
            @Override
            public void onClick(View view) {
                if (paths.size() > 0) {
                    undonePaths.add(paths.remove(paths.size() - 1));
                    undoneColors.add(colors.remove(colors.size()-1));
                    bg = baseBg.copy(baseBg.getConfig(),true);
                    canvas = new Canvas(bg);
                    drawPaint(canvas);
                    fieldView.setImageBitmap(bg);
                }
            }
        });
        redoButton.setOnClickListener(new View.OnClickListener(){ //If click redo
            @Override
            public void onClick(View view){
                if (undonePaths.size()>0){
                    paths.add(undonePaths.remove(undonePaths.size()-1));
                    colors.add(undoneColors.remove(undoneColors.size()-1));
                    bg = baseBg.copy(baseBg.getConfig(),true);
                    canvas = new Canvas(bg);
                    drawPaint(canvas);
                    fieldView.setImageBitmap(bg);
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                clearScreen();
                fresh=false;
            }
        });
        colorButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ringPaint.setColor(getResources().getColor(R.color.color1));
                resetMargins();
                setMargins(colorButton1,30,0,60,0);
                setMargins(select1,30,0,60,0);
                select1.setVisibility(View.VISIBLE);
            }
        });
        colorButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ringPaint.setColor(getResources().getColor(R.color.color2));
                resetMargins();
                setMargins(colorButton2, 30, 0, 60, 0);
                setMargins(select2,30,0,60,0);
                select2.setVisibility(View.VISIBLE);
            }
        });
        colorButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ringPaint.setColor(getResources().getColor(R.color.color3));
                resetMargins();
                setMargins(colorButton3,30,0,60,0);
                setMargins(select3,30,0,60,0);
                select3.setVisibility(View.VISIBLE);
            }
        });
        colorButton4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ringPaint.setColor(getResources().getColor(R.color.color4));
                resetMargins();
                setMargins(colorButton4,30,0,60,0);
                setMargins(select4,30,0,60,0);
                select4.setVisibility(View.VISIBLE);
            }
        });
        colorButton5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ringPaint.setColor(getResources().getColor(R.color.color5));
                resetMargins();
                setMargins(colorButton5,30,0,60,0);
                setMargins(select5,30,0,60,0);
                select5.setVisibility(View.VISIBLE);
            }
        });
        colorButton6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ringPaint.setColor(getResources().getColor(R.color.color6));
                resetMargins();
                setMargins(colorButton6,30,0,60,0);
                setMargins(select6,30,0,60,0);
                select6.setVisibility(View.VISIBLE);
            }
        });
        colorButton7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ringPaint.setColor(getResources().getColor(R.color.color7));
                resetMargins();
                setMargins(colorButton7,30,0,60,0);
                setMargins(select7,30,0,60,0);
                select7.setVisibility(View.VISIBLE);
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (NavUtils.getParentActivityName(getActivity())!=null)
                    NavUtils.navigateUpFromSameTask(getActivity());
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (mTeam!=null) {
                    if (mAutonNumber == 1) {
                        if (paths.size() > 0) {
                            String bgName = "auton1-" + mTeam.getId();
                            mTeam.setmAuton1ImagePath(saveToInternalStorage(bg, bgName));
                            mTeam.setmAuton1ImageName(fileName);
                        } else if (!fresh)
                            mTeam.setmAuton1ImagePath("");
                    } else {
                        if (paths.size() > 0) {
                            String bgName = "auton2-" + mTeam.getId();
                            mTeam.setmAuton2ImagePath(saveToInternalStorage(bg, bgName));
                            mTeam.setmAuton2ImageName(fileName);
                        } else if (!fresh)
                            mTeam.setmAuton2ImagePath("");
                    }
                }
                else { //If this came from an mIdea
                    mIdea.setPictureUri(saveBitmap(bg));
                    mIdea.setUsesDefault(false);
                }
                if (NavUtils.getParentActivityName(getActivity())!=null)
                    NavUtils.navigateUpFromSameTask(getActivity());
            }
        });

        fieldView.setImageBitmap(bg);
        return rootView;
    }

    public double getDistance(float x1, float y1, float x2, float y2){
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
    public void resetMargins(){
        setMargins(colorButton1,0,0,30,0);
        setMargins(colorButton2,0,0,30,0);
        setMargins(colorButton3,0,0,30,0);
        setMargins(colorButton4,0,0,30,0);
        setMargins(colorButton5,0,0,30,0);
        setMargins(colorButton6,0,0,30,0);
        setMargins(colorButton7,0,0,30,0);

        setMargins(select1,0,0,30,0);
        setMargins(select2,0,0,30,0);
        setMargins(select3,0,0,30,0);
        setMargins(select4,0,0,30,0);
        setMargins(select5,0,0,30,0);
        setMargins(select6,0,0,30,0);
        setMargins(select7,0,0,30,0);

        select1.setVisibility(View.INVISIBLE);
        select2.setVisibility(View.INVISIBLE);
        select3.setVisibility(View.INVISIBLE);
        select4.setVisibility(View.INVISIBLE);
        select5.setVisibility(View.INVISIBLE);
        select6.setVisibility(View.INVISIBLE);
        select7.setVisibility(View.INVISIBLE);
    }
    public static void setMargins(View v, int l, int t, int r, int b){
        if (v.getLayoutParams()instanceof
                ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void setSize(View v, int w, int h){
        if (v.getLayoutParams()instanceof
                ViewGroup.LayoutParams) {
            ViewGroup.LayoutParams p = v.getLayoutParams();
            p.width=w;
            p.height=h;
            v.requestLayout();
        }
    }

    private void drawArrowHead(float mBaseX, float mBaseY, float length, float mAngleChange)
    {
        Float dotOneX = (float)(Math.cos(mAngleChange - 3.926990817) * length)+mBaseX;
        Float dotOneY = (float)(Math.sin(mAngleChange - 3.926990817) * length)+mBaseY;
        Float dotTwoX = (float)(Math.cos(mAngleChange + 3.926990817) * length)+mBaseX;
        Float dotTwoY = (float)(Math.sin(mAngleChange + 3.926990817) * length)+mBaseY;
        mPath.moveTo(mBaseX, mBaseY); mPath.lineTo(dotOneX, dotOneY);
        mPath.moveTo(mBaseX, mBaseY); mPath.lineTo(dotTwoX, dotTwoY);
        //mCanvas.drawLine(dotOneX,dotOneY,mBaseX,mBaseY,mPaint);
        //mCanvas.drawLine(dotTwoX,dotTwoY,mBaseX,mBaseY,mPaint);
        //Log.d(TAG, "angle is "+ mAngleChange+". Dot One("+dotOneX+", "+dotOneY+"). Dot Two("+dotTwoX+", "+dotTwoY+")");
    }

    private void drawRawArrowHead(float mBaseX, float mBaseY, float length, float mAngleChange, Canvas mCanvas, Paint mPaint)
    {
        Float dotOneX = (float)(Math.cos(mAngleChange - 3.926990817) * length)+mBaseX;
        Float dotOneY = (float)(Math.sin(mAngleChange - 3.926990817) * length)+mBaseY;
        Float dotTwoX = (float)(Math.cos(mAngleChange + 3.926990817) * length)+mBaseX;
        Float dotTwoY = (float)(Math.sin(mAngleChange + 3.926990817) * length)+mBaseY;
        mCanvas.drawLine(dotOneX,dotOneY,mBaseX,mBaseY,mPaint);
        mCanvas.drawLine(dotTwoX,dotTwoY,mBaseX,mBaseY,mPaint);
        //Log.d(TAG, "angle is "+ mAngleChange+". Dot One("+dotOneX+", "+dotOneY+"). Dot Two("+dotTwoX+", "+dotTwoY+")");
    }

    private void loadContent(){
        if (mTeam!=null)
            TeamListActivity.teamID = mTeam.getId();

        borderPaint.setColor(Color.WHITE); //Outside Border
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(12f);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setAntiAlias(true);

        ringPaint.setColor(getResources().getColor(R.color.color1)); //Ring color
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(6f);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setAntiAlias(true);
        if (mTeam!=null) { //Loads initial background image
            if (mAutonNumber == 1) {
                if (mTeam.getmAuton1ImagePath().length() <= 1) {
                    bg = BitmapFactory.decodeResource(getResources(), R.drawable.skyrise_field);
                    bg = bg.copy(bg.getConfig(), true);
                } else {
                    bg = loadImageFromStorage(mTeam.getmAuton1ImagePath(), mTeam.getmAuton1ImageName());
                    bg = bg.copy(bg.getConfig(), true);
                    fresh = true;
                }
            } else {
                if (mTeam.getmAuton2ImagePath().length() <= 1) {
                    bg = BitmapFactory.decodeResource(getResources(), R.drawable.skyrise_field);
                    bg = bg.copy(bg.getConfig(), true);
                } else {
                    bg = loadImageFromStorage(mTeam.getmAuton2ImagePath(), mTeam.getmAuton2ImageName());
                    bg = bg.copy(bg.getConfig(), true);
                    fresh = true;
                }
            }
        }
        else{
            if (mIdea.getPictureUri()!=null){ //Loads initial background image
                try {bg = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mIdea.getPictureUri());}
                catch (Exception e){e.printStackTrace();}
            }
        }
        baseBg = BitmapFactory.decodeResource(getResources(), R.drawable.skyrise_field);
        canvas = new Canvas();
    }

    private void clearScreen(){
        bg = baseBg.copy(baseBg.getConfig(),true);
        paths.clear();
        undonePaths.clear();
        colors.clear();
        undoneColors.clear();
        canvas = new Canvas(bg);
        fieldView.setImageBitmap(bg);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name){
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        fileName = name;
        File mypath=new File(directory,fileName);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String path, String name)
    {

        try {
            File f=new File(path, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.d(TAG, "File not found.");
            return null;
        }

    }

    private void drawPaint(Canvas mCanvas){
        for (int i = 0;i<paths.size();i++) {
            Path p = paths.get(i);
            Integer c = colors.get(i);
            ringPaint.setColor(c);
            mCanvas.drawPath(p, borderPaint);
            mCanvas.drawPath(p, ringPaint);
        }
    }

    private Uri saveBitmap(Bitmap bitmap){
        File bmap = new File(Environment.getExternalStorageDirectory(), "Picture_" + mIdea.getId() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(bmap);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return Uri.fromFile(bmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Unable to save bitmap.");
        return null;
    }
}
