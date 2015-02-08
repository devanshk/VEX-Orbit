package com.devanshkukreja.navdrawertest3.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.devanshkukreja.navdrawertest3.Activities.MainActivity;
import com.devanshkukreja.navdrawertest3.Helpers.Idea;
import com.devanshkukreja.navdrawertest3.Helpers.IdeaOrganizer;
import com.devanshkukreja.navdrawertest3.Helpers.LoadImage;
import com.devanshkukreja.navdrawertest3.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by devanshk on 8/15/14.
 */
public class DesignDrawFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "[DesignDrawFragment]";

    private static Idea mIdea;
    private LinearLayout basicLayout,gearLayout,extraLayout;
    private Button basicButton,gearButton,extraButton, clearButton;
    private ImageView background, drawingSurface, basicArrow,gearArrow,extraArrow,basicToolLine,basicToolCircle,basicToolRectangle,basicToolTriangle,gearTool12,gearTool36,gearTool60,gearTool84;
    private HorizontalScrollView basicTools,gearTools,extraTools;
    private static Bitmap baseBg,bg;
    private Canvas canvas;
    private Paint preview,paint;
    private Path mPath = new Path();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private ArrayList<Path> paths = new ArrayList<Path>();
    private float downX,downY;
    private ImageButton undoButton, redoButton;

    private enum Tool{Line,Circle,Rectangle,Triangle,Gear12,Gear36,Gear60,Gear84};
    private Tool selectedTool = Tool.Line;
    private ImageView[] tools;

    public DesignDrawFragment(){
        if (MainActivity.ideaId!=null)
            mIdea = IdeaOrganizer.getIdea(MainActivity.ideaId);
        else
            Log.d(TAG, "MainActivity ideaID = null.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_design_draw,container,false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        loadContent(v);
        loadListeners();
        loadData();


        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG, "item Id is: "+item.getItemId());
        switch (item.getItemId()){
            case android.R.id.home: //Returns to TeamListActivity
                Log.d(TAG,"Navigating back.");
                if (NavUtils.getParentActivityName(getActivity())!=null) //Checks if this has a parent activity
                    NavUtils.navigateUpFromSameTask(getActivity()); //Navigates back to parent activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void savePicture(){
        mIdea.setPictureUri(saveBitmap(bg));
        mIdea.setUsesDefault(false);
    }

    private static Uri saveBitmap(Bitmap bitmap){
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

    private void loadData(){
        switch (mIdea.getType()){
            case Custom:
                background.setImageDrawable(getResources().getDrawable(R.drawable.blueprint));
                break;
            case Drive:
                background.setImageDrawable(getResources().getDrawable(R.drawable.redprint));
                break;
            case Intake:
                background.setImageDrawable(getResources().getDrawable(R.drawable.purpleprint));
                break;
            case Lift:
                background.setImageDrawable(getResources().getDrawable(R.drawable.orangeprint));
                break;
            case Gear:
                background.setImageDrawable(getResources().getDrawable(R.drawable.greenprint));
                break;
        }
        if (!mIdea.getUsesDefault() && mIdea.getPictureUri()!=null)
            new LoadImage(drawingSurface, mIdea.getPictureUri(), null, 500, 500).execute(); //Sets picture
    }

    private void loadListeners(){
        drawingSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Integer action = event.getAction();

                if (action==MotionEvent.ACTION_DOWN)
                    undonePaths.clear();

                float X;
                float Y;
                float radius;

                switch (selectedTool){
                    case Line:
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getX();
                                downY = event.getY();
                                mPath= new Path(); //Resets path that the user is drawing from last time
                                break;
                            case MotionEvent.ACTION_UP:
                                X = event.getX(); //Current (X,Y) coordinates
                                Y = event.getY();
                                bg = baseBg.copy(baseBg.getConfig(),true); //Repaves drawing bitmap
                                canvas = new Canvas(bg);

                                mPath.moveTo(downX, downY); mPath.lineTo(X, Y);
                                paths.add(mPath);
                                drawPaths();
                                drawingSurface.setImageBitmap(bg);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                X = event.getX(); //Current (X,Y) coordinates
                                Y = event.getY();
                                bg = baseBg.copy(baseBg.getConfig(),true); //Repaves drawing bitmap
                                canvas = new Canvas(bg);

                                drawPaths();
                                canvas.drawLine(downX,downY,X,Y,preview); //Draws preview line
                                drawingSurface.setImageBitmap(bg);
                        }
                        break;

                    case Circle:
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getX();
                                downY = event.getY();
                                mPath= new Path(); //Resets path that the user is drawing from last time
                                break;
                            case MotionEvent.ACTION_UP:
                                X = event.getX(); //Current (X,Y) coordinates
                                Y = event.getY();
                                bg = baseBg.copy(baseBg.getConfig(),true); //Repaves drawing bitmap
                                canvas = new Canvas(bg);

                                radius = (float)(Math.sqrt(((downX-X)*(downX-X))+((downY-Y)*(downY-Y))));

                                mPath.addCircle(downX, downY, radius, Path.Direction.CW); //Adds circle to path
                                //mPath.moveTo(downX, downY); mPath.lineTo(X, Y); //Adds Line to path
                                paths.add(mPath);
                                drawPaths();
                                drawingSurface.setImageBitmap(bg);

                                break;
                            case MotionEvent.ACTION_MOVE:
                                X = event.getX(); //Current (X,Y) coordinates
                                Y = event.getY();
                                bg = baseBg.copy(baseBg.getConfig(),true); //Repaves drawing bitmap
                                canvas = new Canvas(bg);
                                drawPaths();
                                radius = (float)(Math.sqrt(((downX-X)*(downX-X))+((downY-Y)*(downY-Y))));
                                canvas.drawCircle(downX, downY, radius, preview); //Draws preview Circle
                                canvas.drawLine(downX,downY,X,Y,preview); //Draws preview line
                                drawingSurface.setImageBitmap(bg);
                        }
                        break;

                        case Rectangle:
                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    downX = event.getX();
                                    downY = event.getY();
                                    mPath= new Path(); //Resets path that the user is drawing from last time
                                    break;
                                case MotionEvent.ACTION_UP:
                                    X = event.getX(); //Current (X,Y) coordinates
                                    Y = event.getY();
                                    bg = baseBg.copy(baseBg.getConfig(),true); //Repaves drawing bitmap
                                    canvas = new Canvas(bg);

                                    //Adds Rectangle to path
                                    mPath.moveTo(downX, downY); mPath.lineTo(downX, Y); mPath.lineTo(X,Y); mPath.lineTo(X,downY);mPath.lineTo(downX,downY);
                                    paths.add(mPath);
                                    drawPaths();
                                    drawingSurface.setImageBitmap(bg);

                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    X = event.getX(); //Current (X,Y) coordinates
                                    Y = event.getY();
                                    bg = baseBg.copy(baseBg.getConfig(),true); //Repaves drawing bitmap
                                    canvas = new Canvas(bg);

                                    drawPaths();

                                    //Draws Preview Rectangle
                                    canvas.drawLine(downX, downY,downX,Y,preview);
                                    canvas.drawLine(downX,Y,X,Y,preview);
                                    canvas.drawLine(X,Y,X,downY,preview);
                                    canvas.drawLine(X,downY,downX,downY,preview);
                                    drawingSurface.setImageBitmap(bg);
                            }
                            break;

                    case Triangle:
//                      //TODO Set Triangle Shape
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getX();
                                downY = event.getY();
                                mPath= new Path(); //Resets path that the user is drawing from last time
                                break;
                            case MotionEvent.ACTION_UP:
                                X = event.getX(); //Current (X,Y) coordinates
                                Y = event.getY();
                                bg = baseBg.copy(baseBg.getConfig(),true); //Repaves drawing bitmap
                                canvas = new Canvas(bg);

                                //Adds Triangle to path
                                mPath.moveTo(downX, downY); mPath.lineTo(downX, Y);
                                paths.add(mPath);
                                drawPaths();
                                drawingSurface.setImageBitmap(bg);

                                break;
                            case MotionEvent.ACTION_MOVE:
                                X = event.getX(); //Current (X,Y) coordinates
                                Y = event.getY();
                                bg = baseBg.copy(baseBg.getConfig(),true); //Repaves drawing bitmap
                                canvas = new Canvas(bg);

                                drawPaths();

                                //Draws Preview Triangle
                                canvas.drawLine(downX, downY,downX,Y,preview);
                                drawingSurface.setImageBitmap(bg);
                        }
                        break;
                }

                return true;
            }
        });

        //ImageButton Listeners
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paths.size() > 0) {
                    undonePaths.add(paths.remove(paths.size() - 1));
                    bg = baseBg.copy(baseBg.getConfig(),true);
                    canvas = new Canvas(bg);
                    drawPaths();
                    drawingSurface.setImageBitmap(bg);
                }
            }
        });
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (undonePaths.size()>0){
                    paths.add(undonePaths.remove(undonePaths.size()-1));
                    bg = baseBg.copy(baseBg.getConfig(),true);
                    canvas = new Canvas(bg);
                    drawPaths();
                    drawingSurface.setImageBitmap(bg);
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paths.clear();
                undonePaths.clear();
                bg = baseBg.copy(baseBg.getConfig(),true);
                canvas = new Canvas(bg);
                drawingSurface.setImageBitmap(bg);
            }
        });

        //ImageView Listeners
        basicButton.setOnClickListener(new View.OnClickListener() { //If user selects basic toolkit
            @Override
            public void onClick(View v) {
                gearTools.setVisibility(View.INVISIBLE); //Makes sure other toolkits are invisible
                extraTools.setVisibility(View.INVISIBLE);
                basicTools.setVisibility(View.VISIBLE);
                setSelected(true, basicButton, basicArrow); //Makes basic button selected, and other invisible
                setSelected(false,gearButton,gearArrow);
                setSelected(false,extraButton,extraArrow);
            }
        });

        gearButton.setOnClickListener(new View.OnClickListener() { //If user selects basic toolkit
            @Override
            public void onClick(View v) {
                basicTools.setVisibility(View.INVISIBLE); //Makes sure other toolkits are invisible
                extraTools.setVisibility(View.INVISIBLE);
                gearTools.setVisibility(View.VISIBLE);
                setSelected(false, basicButton, basicArrow); //Makes gear button selected, and other invisible
                setSelected(true,gearButton,gearArrow);
                setSelected(false,extraButton,extraArrow);
            }
        });

        extraButton.setOnClickListener(new View.OnClickListener() { //If user selects basic toolkit
            @Override
            public void onClick(View v) {
                basicTools.setVisibility(View.INVISIBLE); //Makes sure other toolkits are invisible
                extraTools.setVisibility(View.VISIBLE);
                gearTools.setVisibility(View.INVISIBLE);
                setSelected(false,basicButton,basicArrow); //Makes extra button selected, and other invisible
                setSelected(false,gearButton,gearArrow);
                setSelected(true,extraButton,extraArrow);
            }
        });

        //Toolkit Tools Listeners
        basicToolLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView i : tools) { //Clears out all tool icon borders
                    i.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                basicToolLine.setBackgroundColor(getResources().getColor(R.color.white));
                selectedTool = Tool.Line;
            }
        });
        basicToolCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView i : tools) { //Clears out all tool icon borders
                    i.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                basicToolCircle.setBackgroundColor(getResources().getColor(R.color.white));
                selectedTool = Tool.Circle;
            }
        });
        basicToolRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView i : tools) { //Clears out all tool icon borders
                    i.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                basicToolRectangle.setBackgroundColor(getResources().getColor(R.color.white));
                selectedTool = Tool.Rectangle;
            }
        });
        basicToolTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView i : tools) { //Clears out all tool icon borders
                    i.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                basicToolTriangle.setBackgroundColor(getResources().getColor(R.color.white));
                selectedTool = Tool.Triangle;
            }
        });
        gearTool12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView i : tools) { //Clears out all tool icon borders
                    i.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                gearTool12.setBackgroundColor(getResources().getColor(R.color.white));
                selectedTool = Tool.Gear12;
            }
        });
        gearTool36.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView i : tools) { //Clears out all tool icon borders
                    i.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                gearTool36.setBackgroundColor(getResources().getColor(R.color.white));
                selectedTool = Tool.Gear36;
            }
        });
        gearTool60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView i : tools) { //Clears out all tool icon borders
                    i.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                gearTool60.setBackgroundColor(getResources().getColor(R.color.white));
                selectedTool = Tool.Gear60;
            }
        });
        gearTool84.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView i : tools) { //Clears out all tool icon borders
                    i.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                gearTool84.setBackgroundColor(getResources().getColor(R.color.white));
                selectedTool = Tool.Gear84;
            }
        });
    }

    private void loadContent(View v){
        basicLayout = (LinearLayout)v.findViewById(R.id.design_toolbox_basic);
        gearLayout = (LinearLayout)v.findViewById(R.id.design_toolbox_gear);
        extraLayout = (LinearLayout)v.findViewById(R.id.design_toolbox_extra);
        basicButton = (Button)v.findViewById(R.id.design_toolbox_button_basic);
        gearButton = (Button)v.findViewById(R.id.design_toolbox_button_gear);
        extraButton = (Button)v.findViewById(R.id.design_toolbox_button_extra);
        clearButton = (Button)v.findViewById(R.id.design_clear_button);
        drawingSurface = (ImageView)v.findViewById(R.id.design_canvas);
        drawingSurface.setTag("drawingSurface_"+mIdea.getId());
        basicToolLine = (ImageView)v.findViewById(R.id.design_toolbox_basic_line);
        basicToolCircle = (ImageView)v.findViewById(R.id.design_toolbox_basic_circle);
        basicToolRectangle = (ImageView)v.findViewById(R.id.design_toolbox_basic_rectangle);
        basicToolTriangle = (ImageView)v.findViewById(R.id.design_toolbox_basic_triangle);
        gearTool12 =  (ImageView)v.findViewById(R.id.design_toolbox_gear_12);
        gearTool36 =  (ImageView)v.findViewById(R.id.design_toolbox_gear_36);
        gearTool60 =  (ImageView)v.findViewById(R.id.design_toolbox_gear_60);
        gearTool84 =  (ImageView)v.findViewById(R.id.design_toolbox_gear_84);
        background = (ImageView)v.findViewById(R.id.design_background);
        basicArrow = (ImageView)v.findViewById(R.id.design_toolbox_arrow_basic);
        gearArrow = (ImageView)v.findViewById(R.id.design_toolbox_arrow_gear);
        extraArrow = (ImageView)v.findViewById(R.id.design_toolbox_arrow_extra);
        basicTools = (HorizontalScrollView)v.findViewById(R.id.design_toolbox_basic_tools);
        gearTools = (HorizontalScrollView)v.findViewById(R.id.design_toolbox_gear_tools);
        extraTools = (HorizontalScrollView)v.findViewById(R.id.design_toolbox_extra_tools);
        undoButton = (ImageButton)v.findViewById(R.id.design_undo_button);
        redoButton = (ImageButton)v.findViewById(R.id.design_redo_button);

        //Sets up paints for drawing
        preview = new Paint(); preview.setColor(getResources().getColor(R.color.white));
        preview.setStyle(Paint.Style.STROKE); preview.setStrokeWidth(4f);preview.setStrokeCap(Paint.Cap.ROUND);
        preview.setAlpha(75); preview.setAntiAlias(true);

        paint = new Paint(); paint.setColor(getResources().getColor(R.color.white));
        paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(4f);paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        bg = BitmapFactory.decodeResource(getResources(), R.drawable.transparent_square);
        bg = bg.copy(bg.getConfig(), true);
        baseBg = BitmapFactory.decodeResource(getResources(), R.drawable.transparent_square);
        baseBg = baseBg.copy(baseBg.getConfig(),true);
        canvas = new Canvas(bg);
        drawingSurface.setImageBitmap(bg);
        //[Add any extra tools to this list.]
        tools = new ImageView[]{basicToolLine,basicToolCircle,basicToolRectangle,basicToolTriangle,gearTool12,gearTool36,gearTool60,gearTool84};
    }

    private Button setSelected(boolean isEnabled, Button btn, ImageView arrow){
        if (isEnabled){
            btn.setBackgroundColor(getResources().getColor(R.color.light_gray));
            btn.setTextColor(getResources().getColor(R.color.black));
            arrow.setVisibility(View.VISIBLE);
        }
        else{
            btn.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            btn.setTextColor(getResources().getColor(R.color.white));
            arrow.setVisibility(View.INVISIBLE);
        }
        return btn;
    }

    private void drawPaths(){
        for (int i = 0;i<paths.size();i++) {
            Path p = paths.get(i);
            canvas.drawPath(p, paint);
        }
    }
}
