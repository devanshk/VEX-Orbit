package com.devanshkukreja.navdrawertest3.Fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devanshkukreja.navdrawertest3.Activities.ScoutingActivity;
import com.devanshkukreja.navdrawertest3.Helpers.LoadImage;
import com.devanshkukreja.navdrawertest3.Helpers.Team;
import com.devanshkukreja.navdrawertest3.Helpers.TeamOrganizer;
import com.devanshkukreja.navdrawertest3.R;

import java.util.ArrayList;

/**
 * Created by devanshk on 7/2/2014.
 */
public class TeamListFragment extends ListFragment {
    private static final String TAG="TeamListFragment";
    private Button addTeamButton,secondaryAddTeamButton;
    private Integer iteration=1;

    private ArrayList<Team> mTeams;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.team_list_title));
        mTeams = TeamOrganizer.get(getActivity()).getTeams();

        TeamAdapter teamAdapter = new TeamAdapter(mTeams);
        setListAdapter(teamAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.list_fragment,parent,false);

        addTeamButton =(Button)v.findViewById(R.id.initial_add_team_button);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Team team = new Team();
                TeamOrganizer.get(getActivity()).addTeam(team);
                Intent i = new Intent(getActivity(),ScoutingActivity.class);
                i.putExtra(TeamFragment.EXTRA_TEAM_ID,team.getId());
                startActivityForResult(i, 0);
            }
        });

        secondaryAddTeamButton =(Button)v.findViewById(R.id.secondary_add_team_button);
        secondaryAddTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Team team = new Team();
                TeamOrganizer.get(getActivity()).addTeam(team);
                Intent i = new Intent(getActivity(),ScoutingActivity.class);
                i.putExtra(TeamFragment.EXTRA_TEAM_ID,team.getId());
                startActivityForResult(i, 0);
            }
        });

        ListView listView = (ListView)v.findViewById(android.R.id.list);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {

                }

                @Override
                public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.team_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_team:
                            TeamAdapter adapter = (TeamAdapter) getListAdapter();
                            TeamOrganizer teamOrganizer = TeamOrganizer.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    teamOrganizer.deleteTeam(adapter.getItem(i));
                                }
                            }
                            mode.finish();
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(android.view.ActionMode mode) {
                    //Do Something
                }
            });
        }

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Team t = ((TeamAdapter)getListAdapter()).getItem(position);

        //Start ScoutingActivity
        Intent i = new Intent(getActivity(),ScoutingActivity.class);
        i.putExtra(TeamFragment.EXTRA_TEAM_ID,t.getId());
        startActivity(i);
    }

    @Override
    public void onResume(){
        super.onResume();
        ((TeamAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        ((TeamAdapter)getListAdapter()).notifyDataSetChanged();
    }


    private class TeamAdapter extends ArrayAdapter<Team> {
        public TeamAdapter(ArrayList<Team> teams) {
            super(getActivity(), 0, teams);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_team, null);
            }
            //Configure the view for this Team
            Team t = getItem(position);
                //Beta Async Code
                ProgressBar tProgressBar = (ProgressBar)convertView.findViewById(R.id.list_item_progressbar);
                ImageView tImgView = (ImageView)convertView.findViewById(R.id.list_item_picture);
                tImgView.setTag("teamImage_"+t.getId());
            if (!t.getUsesDefaultPicture())
                new LoadImage(tImgView,t.getmPictureUri(),tProgressBar).execute();
            else
                new LoadImage(tImgView,t.getmPictureUri()).execute();

                //Deprecated Image Setting Code
                /*try{setPic((ImageView)convertView.findViewById(R.id.list_item_picture), t.getmPictureUri().getPath());} catch (NullPointerException e){Log.d(TAG,"List couldn't find picture.");}
                Log.d(TAG, "Saved Image " + iteration +" for team # "+ t.getNumber());
                iteration++;*/
            if (t.getNumber()!=null){
            TextView listNumber = (TextView) convertView.findViewById(R.id.list_item_number);
            listNumber.setText(t.getNumber().toString());}
            if (t.getClub()!=null){
            TextView clubName = (TextView) convertView.findViewById(R.id.list_item_club);
            clubName.setText(t.getClub().toString());}
            if (t.getName()!=null){
            TextView listName = (TextView) convertView.findViewById(R.id.list_item_name);
            listName.setText(t.getName().toString());}
            if (t.getScore()!=null){
            TextView listScore = (TextView) convertView.findViewById(R.id.list_item_score);
            listScore.setText(String.format("%.1f", t.getScore()));}

            return convertView;
        }

        private void setPic(ImageView mImageView, String mCurrentPhotoPath) {
            // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            //Work Around code in case this is called too soon
            DisplayMetrics displayMetrics = getActivity().getBaseContext().getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            if (targetW == 0)
                targetW= Math.round(dpWidth);
            if (targetH==0)
                targetH=300;

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

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            mImageView.setImageBitmap(bitmap);
        }
    }
}
