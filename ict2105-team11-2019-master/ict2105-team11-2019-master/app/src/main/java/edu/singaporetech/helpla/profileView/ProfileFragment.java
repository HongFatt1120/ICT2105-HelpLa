package edu.singaporetech.helpla.profileView;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import edu.singaporetech.helpla.MainActivity;
//import edu.singaporetech.helpla.chat.ChatsActivity;
import edu.singaporetech.helpla.chat.ChatsActivityFragment;
import edu.singaporetech.helpla.chat.DiscussionActivity;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.FirebaseStorageHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.homeView.HomeAdapter;
import edu.singaporetech.helpla.models.Points;
import edu.singaporetech.helpla.models.Post;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.models.User;
import edu.singaporetech.helpla.utils.Utils;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements OnTaskCompletedListener<Post> , FirebaseStorageHelper.OnDownloadImage {

    private ProfileAdapter mProfileAdapter;
    private HomeAdapter mHomeAdapter;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;

    private ImageView profileImageView;


    Button claimBtn;
    Button giveBtn;
    Points point;
    RecyclerView recyclerView;
    ProfileListingAdapter mAdapter;
    private FirebaseDatabaseHelper dbHelper;
    private FirebaseStorageHelper storageHelper;
    private RelativeLayout loading;
    int secondReq = 0;

    int doneFlag = 0;
    int doneLoadingPostsFlag = 0;
    TextView userid;

    ImageButton buttonChat_ActionBar;
    ImageButton buttonSetting_ActionBar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        mProfileAdapter = new ProfileAdapter(getChildFragmentManager());
        // mViewPager = (ViewPager) view.findViewById(R.id.container);
        userid = (TextView) view.findViewById(R.id.textViewName);
        profileImageView = view.findViewById(R.id.profileImageView);
        loading = view.findViewById(R.id.loadingRL);
        mRecyclerView = view.findViewById(R.id.myListing_rv);

        mRecyclerView.setNestedScrollingEnabled(false);
        storageHelper = new FirebaseStorageHelper(this);
        buttonChat_ActionBar =  view.findViewById(R.id.imageViewChat1);
        buttonChat_ActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new ChatsActivityFragment();
                FragmentManager fm = getFragmentManager();
                // create a FragmentTransaction to begin the transaction and replace the Fragment
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                // replace the FrameLayout with new Fragment
                //fragmentTransaction.replace(R.id.profile_fragment, frag);
                fragmentTransaction.replace(((ViewGroup)(getView().getParent())).getId(), frag);
                fragmentTransaction.commit();
            }
        });

        buttonSetting_ActionBar = view.findViewById(R.id.imageButtonSetting);
        buttonSetting_ActionBar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment frag = new ProfileSettingFragment();
                FragmentManager fm = getFragmentManager();
                // create a FragmentTransaction to begin the transaction and replace the Fragment
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                // replace the FrameLayout with new Fragment
                //fragmentTransaction.replace(R.id.profile_fragment, frag);
                fragmentTransaction.replace(((ViewGroup)(getView().getParent())).getId(), frag);
                fragmentTransaction.commit();
            }
        });

        claimBtn = (Button) view.findViewById(R.id.claimBtn);
        giveBtn = (Button) view.findViewById(R.id.giveBtn);

        /**
         * claim btn is clicked
         */
        claimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new ProfileClaimListFragment();
                FragmentManager fm = getFragmentManager();
                final Bundle bundle = new Bundle();
                bundle.putSerializable("points",point);
                frag.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(((ViewGroup)(getView().getParent())).getId(), frag);
                fragmentTransaction.commit();
            }
        });

        /**
         * owe btn is clicked
         */
        giveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new ProfileOweListFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(((ViewGroup)(getView().getParent())).getId(), frag);
                //pass data from this fragment to the profile_othersowe_fragment
                final Bundle bundle = new Bundle();
                bundle.putSerializable("points",point);
                frag.setArguments(bundle);
                fragmentTransaction.commit();

            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.myListing_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbHelper = new FirebaseDatabaseHelper(this);
        //dbHelper.requestAllData("Post","timestamp");
//        dbHelper.requestClaimData("Points");
        if(secondReq == 0){
            dbHelper.requestAllData("Points","id", Utils.getUserObj(getContext()).getUid());

        }
        saveInfo();
        initProfileView();
        return view;
    }
    public void initProfileView(){
        SharedPreferences sharedPref = getContext().getSharedPreferences("userObj",MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPref.getString("userObject", "");
        User obj = gson.fromJson(json, User.class);
        userid.setText(obj.getName());
        storageHelper.getImage(obj.getImage_url());


    }



    /**
     *   save the user id using textview into shared preference
     */
    public void saveInfo(){
        SharedPreferences mypref = getContext().getSharedPreferences("mypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = mypref.edit();
        editor.putString("userid", userid.getText().toString());
        editor.apply();
    }


    @Override
    public void onTaskStart() {
        loading.setVisibility(View.VISIBLE);
    }

    public void onTaskComplete(ArrayList<Object> data) {
        if (secondReq == 0){
            final ObjectMapper mapper = new ObjectMapper();
            for(Object x:data){
                point = mapper.convertValue(x,Points.class);
            }

            if(point.getClaim() == null){
                claimBtn.setText("0 Points");

            }
            else
            {
                claimBtn.setText(computeTotal(point.getClaim()) + " Points");
            }
            if(point.getOwe() == null){
                giveBtn.setText("0 Points");

            }
            else
            {giveBtn.setText(computeOweTotal(point.getOwe()) + " Points");

            }
            /**GETTING USER POSTS*/
            dbHelper.requestAllData("Post","uid", Utils.getUserObj(getContext()).getUid());
            secondReq++;
        }
        else if (secondReq == 1){
            ArrayList<Post> mLostItemsList = new ArrayList<>();
            final ObjectMapper mapper2 = new ObjectMapper();


            if (getActivity() != null) {
                for (Object x : data) {
                    Post item = mapper2.convertValue(x, Post.class);
                    mLostItemsList.add(item);
                }

                mHomeAdapter = new HomeAdapter(getActivity(), mLostItemsList);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mHomeAdapter);
                mRecyclerView.setHasFixedSize(true);
                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
                mRecyclerView.setLayoutManager(mGridLayoutManager);
            }
            loading.setVisibility(View.INVISIBLE);
        }



    }

    public int computeOweTotal(Map<String,Points.owe> point){
        int total = 0;
        for (Map.Entry<String, Points.owe> entry : point.entrySet()) {

            total += entry.getValue().getPoints();
        }
        return total;
    }

    public int computeTotal(Map<String,Points.claim> point){
        int total = 0;
        for (Map.Entry<String, Points.claim> entry : point.entrySet()) {

            total += entry.getValue().getPoints();
        }
        return total;
    }
    /**
     *
     * Load the points data to display in the 2 buttons
     */
    @Override
    public void onTaskCompleteMap(Map<String,  ArrayList<String>> data) {

    }

    @Override
    public void onKeyCompleted(String key) {

    }


    @Override
    public void onCompleted(Uri uri) {
        Picasso.get().load(uri).into(profileImageView);
    }
}