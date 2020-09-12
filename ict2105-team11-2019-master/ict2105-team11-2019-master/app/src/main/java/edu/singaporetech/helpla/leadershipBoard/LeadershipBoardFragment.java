package edu.singaporetech.helpla.leadershipBoard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.chat.ChatsActivityFragment;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.User;
import edu.singaporetech.helpla.profileView.ProfileSettingFragment;


public class LeadershipBoardFragment extends Fragment implements OnTaskCompletedListener<User> {
    private static final String TAG = "LeadershipBoard_Fragment";
    /**Declaration*/
    private RecyclerView recyclerView;
    private LeadershipBoardAdapter adapter;
    private ArrayList<User> newItems ;
    private RelativeLayout loading;
    private FirebaseDatabaseHelper databaseHelper;
    ImageButton buttonChat_ActionBar;
    ImageButton buttonSetting_ActionBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leadershiip_board, container, false);
        /** Initialization*/
        recyclerView = view.findViewById(R.id.recyclerViewParticipant);

        loading = view.findViewById(R.id.loadingRL);

        newItems = new ArrayList<>();
        adapter = new LeadershipBoardAdapter(getActivity(),newItems);

        databaseHelper = new FirebaseDatabaseHelper(this);
        /** Request Leadership board data from firebase */
        databaseHelper.requestAllData("Users","points");

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

        return view;
    }


    @Override
    public void onTaskStart() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {
        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        for(Object x : data){
            User item = mapper.convertValue(x, User.class);

            newItems.add(item);
        }
        initAdapter();
        loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTaskCompleteMap(Map<String, ArrayList<String>> data) {

    }

    @Override
    public void onKeyCompleted(String key) {

    }

    public void initAdapter(){

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);  //this is needed
        recyclerView.setLayoutManager(linearLayoutManager1);
        recyclerView.setAdapter(adapter);

    }


}
