package com.devanshkukreja.navdrawertest3.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by devanshkukreja on 7/26/14.
 */
public class LoadImage extends AsyncTask<Object, Void, Bitmap> {
    private String TAG="LoadImage AsyncTask";

    private ImageView imv;
    private String path;
    private Uri mUri;
    private ProgressBar mProgressBar;
    private Bitmap mBitmap;
    private Idea mIdea;
    private Team mTeam;
    private Integer targetX, targetY;
    public LoadImage(ImageView imv, Uri uri) {
        this.imv = imv;
        this.path = imv.getTag().toString();
        mUri = uri;
    }

    public LoadImage(ImageView imv, Uri uri, ProgressBar progressBar) {
        this.imv = imv;
        this.path = imv.getTag().toString();
        mUri = uri;
        mProgressBar=progressBar;
    }

    public LoadImage(ImageView imv, Bitmap rawBitmap, ProgressBar progressBar,Team team){
        this.imv=imv;
        this.path=imv.getTag().toString();
        this.mBitmap=rawBitmap;
        this.mProgressBar=progressBar;
        this.mTeam=team;
    }

    public LoadImage(ImageView imv, Bitmap rawBitmap, ProgressBar progressBar, Idea idea){
        this.imv=imv;
        this.path=imv.getTag().toString();
        this.mBitmap=rawBitmap;
        this.mProgressBar=progressBar;
        this.mIdea = idea;
    }


    public LoadImage(ImageView imv, Bitmap rawBitmap, ProgressBar progressBar, Idea idea,Integer x, Integer y){
        this.imv=imv;
        this.path=imv.getTag().toString();
        this.mBitmap=rawBitmap;
        this.mProgressBar=progressBar;
        this.mIdea = idea;
        this.targetX = x;
        this.targetY = y;
    }

    public LoadImage(ImageView imv, Uri uri, ProgressBar progressBar,Integer x, Integer y) {
        this.imv = imv;
        this.path = imv.getTag().toString();
        this.mUri = uri;
        this.mProgressBar=progressBar;
        this.targetX=x;
        this.targetY=y;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        if (mProgressBar!=null) {
            Log.d(TAG, "mIdea = ");
            if (mIdea==null||!mIdea.getImageLoaded()); //If there is no mIdea or if mIdea's image has not loaded
                mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        try{
            Log.d(TAG, "Loading picture");
            if (mBitmap!=null) { //Saves bitmap to a new location with a new URI if necessary
                mUri = saveBitmap(mBitmap);
                mBitmap.recycle();
                mBitmap = null;
                System.gc();
                if (mTeam!=null)
                    mTeam.setmPictureUri(mUri);
                else
                    mIdea.setPictureUri(mUri);
            }
                return setPic(imv, mUri.getPath()); //Calls setPic to optimize and return appropriate bitmap
        } catch (NullPointerException e){
            Log.d(TAG, "List couldn't find picture.");}
        return null;
    }

    private Uri saveBitmap(Bitmap bitmap){
        try {
            File bmap;
        if (mTeam!=null){
            bmap = new File(Environment.getExternalStorageDirectory(), "mainPic" + mTeam.getId() + ".jpg");}
        else{
            bmap = new File(Environment.getExternalStorageDirectory(), "Picture" + imv.getTag() + ".jpg");}
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

    private Bitmap setPic(ImageView mImageView, String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        //Not Working Work Around code in case this is called too soon
        /*while (targetW==0) {
            targetW = mImageView.getWidth();
            targetH = mImageView.getHeight();
            //Log.d(TAG, "Re-Set Width");
        }*/
            if (targetW == 0) {
                Log.d(TAG,"Wimped out.");
                targetW = 500;
            }
            if (targetH == 0)
                targetH = 500;

        if (targetX!=null)
            targetW=targetX;
        if (targetY!=null)
            targetH=targetY;

        Log.d(TAG, "picturePath  = "+mCurrentPhotoPath);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return(BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions));//Decodes the bitmap and returns it
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (!imv.getTag().toString().equals(path)) {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
            Log.d(TAG, "Duplicate Async Tasks. Returning.");
            return;
        }

        if(result != null && imv != null){
            if (mIdea!=null)
                mIdea.setImageLoaded(true);
            if (mProgressBar!=null)
                mProgressBar.setVisibility(View.GONE);
            imv.setVisibility(View.VISIBLE);
            imv.setImageBitmap(result);
            Log.d(TAG, "Set bitmap image.");
        }else{
            //imv.setVisibility(View.GONE);
            Log.d(TAG, "Could not find image or imageview.");
        }
    }

}
