
package edu.singaporetech.helpla.postView;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.eventView.EventDetails;
import edu.singaporetech.helpla.eventView.EventFragment;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.FirebaseStorageHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Post;
import edu.singaporetech.helpla.utils.Utils;


public class ViewPostFragment extends Fragment implements FirebaseStorageHelper.OnDownloadImage, View.OnClickListener, OnTaskCompletedListener<Post> {
    private ImageView postItemImage;
    private TextView postItemTitle;
    private TextView postItemDescription;
    private TextView postItemLocation;
    private View view;
    private Button buttonJoin;
    private Button buttonCreate;
    private Button buttonView;
    Post post;
    private String key;
    private String eventid;
    private FirebaseDatabaseHelper databaseHelper;

    private FirebaseStorageHelper storageHelper;


    public ViewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_post, container, false);
        postItemImage = view.findViewById(R.id.postItemImage);
        postItemTitle = view.findViewById(R.id.postItemTitle);
        postItemDescription = view.findViewById(R.id.postItemDescription);
        postItemLocation = view.findViewById(R.id.postItemLocation);
        buttonCreate = view.findViewById(R.id.buttonCreateEvent);
        buttonView = view.findViewById(R.id.buttonView);
        databaseHelper = new FirebaseDatabaseHelper(this);
        storageHelper = new FirebaseStorageHelper(this);

        buttonView.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable("POST");
            eventid =  getArguments().getString("eventID");
            initButtonState();
        }
    }

    public void initButtonState(){
        //Check if coming from Post view
        if(post != null){
            initPostView(post);
            databaseHelper.searchKey("Event", "event_id", post.getEid());
            //check if its my post
            if (post.getUid().equals(Utils.getUID(getActivity())))
            {
                //if its my post check if event is created
                if (post.getEid() == null && eventid == null) {
                    buttonCreate.setVisibility(View.VISIBLE);
                    buttonView.setVisibility(View.INVISIBLE);
                }
                else
                {
                    buttonView.setVisibility(View.VISIBLE);
                    buttonCreate.setVisibility(View.INVISIBLE);
                }

            } else {
                if (post.getEid() == null ) {
                    buttonCreate.setVisibility(View.INVISIBLE);
                    buttonView.setVisibility(View.INVISIBLE);
                } else {
                    buttonView.setVisibility(View.VISIBLE);
                    buttonCreate.setVisibility(View.INVISIBLE);
                }

            }
        }
        else
        {
            databaseHelper.requestAllData("Post","eid",eventid);

        }
    }

    public void initPostView(Post post){
        postItemTitle.setText(post.getTitle());
        postItemDescription.setText(post.getDescription());
        postItemLocation.setText(post.getLast_seen_location());

        storageHelper.getImage(post.getImage_url());
    }



    @Override
    public void onCompleted(Uri uri) {
        Picasso.get().load(uri).into(postItemImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCreateEvent:
                createEvent();
                break;
            case R.id.buttonView:
                rediectEvent();
        }
    }

    public void rediectEvent(){
        Fragment frag = new EventDetails();
        FragmentManager fm = getFragmentManager();
        final Bundle bundle = new Bundle();
        bundle.putSerializable("POST",post);
        bundle.putString("eventKey",key);

        bundle.putString("eid",post.getEid());
        frag.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(((ViewGroup) (getView().getParent())).getId(), frag);
        fragmentTransaction.commit();
    }
    public void createEvent(){
        Fragment frag = new EventFragment();
        FragmentManager fm = getFragmentManager();
        Bundle arg = new Bundle();
        arg.putString("caseID",post.getCase_id());
        frag.setArguments(arg);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(((ViewGroup) (getView().getParent())).getId(), frag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void updateParticipants() {

        updateDB();
        Fragment frag = new EventDetails();
        Bundle arg = new Bundle();
        arg.putString("eid",post.getEid());
        frag.setArguments(arg);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(((ViewGroup) (getView().getParent())).getId(), frag);
        fragmentTransaction.commit();

    }

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {
        final ObjectMapper mapper = new ObjectMapper();
        if(data.size() == 1){
             post = mapper.convertValue(data.get(0),Post.class);
            initPostView(post);
        }
    }

    @Override
    public void onKeyCompleted(String key) {
        this.key = key;


    }

    @Override
    public void onTaskCompleteMap(Map data) {

    }

    public void updateDB() {


        HashMap<String, String> user = new HashMap<>();
        user.put("id", Utils.getUID(getActivity()));
        databaseHelper.addKey("Event" + key + "participants");
        databaseHelper.addData("Event/" + key + "/participants/" + databaseHelper.getKey() , user);


    }

}