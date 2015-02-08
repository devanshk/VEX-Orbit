package com.devanshkukreja.navdrawertest3.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devanshkukreja.navdrawertest3.Activities.DesignActivity;
import com.devanshkukreja.navdrawertest3.Activities.MainActivity;
import com.devanshkukreja.navdrawertest3.Helpers.Idea;
import com.devanshkukreja.navdrawertest3.Helpers.IdeaOrganizer;
import com.devanshkukreja.navdrawertest3.Helpers.LoadImage;
import com.devanshkukreja.navdrawertest3.Helpers.TeamOrganizer;
import com.devanshkukreja.navdrawertest3.R;

import java.util.ArrayList;

/**
 * Created by devanshkukreja on 7/30/14.
 */
public class IdeaGridFragment extends Fragment {
    private static final String TAG="IdeaGridFragment";
    private Button addIdeaButton,secondaryAddItemButton;

    private IdeaAdapter ideaAdapter;
    private ArrayList<Idea> mIdeas;
    private GridView gridView;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Log.d(TAG, "Creating Idea Fragment.");

        getActivity().setTitle(getResources().getString(R.string.idea_grid_title));
        mIdeas = IdeaOrganizer.get(getActivity()).getmIdeas();
        //Log.d(TAG, "mIdeas size = "+mIdeas.size());
        ideaAdapter = new IdeaAdapter(mIdeas);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"[Saving Ideas]");
        IdeaOrganizer.get(getActivity()).saveIdeas(); //Saves teams whenever the fragment is paused
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.grid_fragment_idea,parent,false);
        if (gridView == null) {
            //Log.d(TAG, "GridView is equal to null. Finding...");
            gridView = (GridView) v.findViewById(R.id.idea_gridview);
            //gridView.setAdapter(new ArrayAdapter<Idea>(getActivity(),R.layout.grid_item_idea,mIdeas));

            gridView.setAdapter(ideaAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Idea idea = ideaAdapter.getItem(position);
                    MainActivity.ideaId=idea.getId();
                    Intent i = new Intent(getActivity(),DesignActivity.class);
                    startActivity(i);
                }
            });
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(gridView);
        } else {
            gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
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
                            IdeaOrganizer ideaOrganizer = IdeaOrganizer.get(getActivity());
                            for (int i = ideaAdapter.getCount() - 1; i >= 0; i--) {
                                if (gridView.isItemChecked(i)) {
                                    ideaOrganizer.deleteIdea(ideaAdapter.getItem(i));
                                }
                            }
                            mode.finish();
                            ideaAdapter.notifyDataSetChanged();
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

    private class IdeaAdapter extends ArrayAdapter<Idea> {
        public IdeaAdapter(ArrayList<Idea> ideas) {
            super(getActivity(), 0, ideas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.grid_item_idea, null);
            }
            Idea i = getItem(position);
            ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.idea_progress_bar);
            ImageView imgView = (ImageView)convertView.findViewById(R.id.idea_image);
            ImageView background = (ImageView)convertView.findViewById(R.id.idea_background);
            TextView nameView=(TextView)convertView.findViewById(R.id.idea_name);
            TextView typeView=(TextView)convertView.findViewById(R.id.idea_type);
            imgView.setTag("imgView_"+i.getId()); //Allows Async tasks to identify this imageview to prevent duplicate async tasks
            background.setTag("background_"+i.getId()); //Allows Async tasks to identify this imageview to prevent duplicate async tasks

            if (!i.getUsesDefault()) {
                switch (i.getType()) { //Sets background paper design
                    case Custom:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.blueprint);
                        new LoadImage(background, bitmap, null, i, 150, 150).execute();
                        break;
                    case Route:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.skyrise_field);
                        new LoadImage(background, bitmap, null, i, 150, 150).execute();
                        break;
                    case Drive:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.redprint);
                        new LoadImage(background, bitmap, null, i, 150, 150).execute();
                        break;
                    case Intake:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.purpleprint);
                        new LoadImage(background, bitmap, progressBar, i, 150, 150).execute();
                        break;
                    case Lift:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.orangeprint);
                        new LoadImage(background, bitmap, progressBar, i, 150, 150).execute();
                        break;
                    case Gear:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.greenprint);
                        new LoadImage(background, bitmap, progressBar, i, 150, 150).execute();
                        break;
                }
                new LoadImage(imgView, i.getPictureUri(), progressBar,150,150).execute(); //Sets drawing picture
            }

            else{
                switch (i.getType()){
                    case Custom:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.blueprint_preview);
                        new LoadImage(imgView,bitmap,null,i,150,150).execute();
                        break;
                    case Route:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.skyrise_field);
                        new LoadImage(imgView,bitmap,null,i,150,150).execute();
                        break;
                    case Drive:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.redprint_preview);
                        new LoadImage(imgView,bitmap,null,i,150,150).execute();
                        break;
                    case Intake:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.purpleprint_preview);
                        new LoadImage(imgView,bitmap,progressBar,i,150,150).execute();
                        break;
                    case Lift:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.orangeprint_preview);
                        new LoadImage(imgView,bitmap,progressBar,i,150,150).execute();
                        break;
                    case Gear:
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.greenprint_preview);
                        new LoadImage(imgView,bitmap,progressBar,i,150,150).execute();
                        break;
                }
            }
            nameView.setText(i.getName());
            typeView.setText(i.getType().toString());

            //Log.d(TAG, "Idea List Size = "+mIdeas.size());

            return convertView;
        }
    }
}
