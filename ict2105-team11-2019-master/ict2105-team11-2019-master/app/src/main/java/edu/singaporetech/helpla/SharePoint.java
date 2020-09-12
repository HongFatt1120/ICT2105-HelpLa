package edu.singaporetech.helpla;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.eventView.EventAdapter;
import edu.singaporetech.helpla.eventView.EventFragment;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.homeView.HomeFragment;
import edu.singaporetech.helpla.models.Post;
import edu.singaporetech.helpla.models.User;
import edu.singaporetech.helpla.utils.Utils;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SharePoint extends Fragment implements View.OnClickListener , OnTaskCompletedListener {

    private ArrayList<String> users;
    private String event_id;
    private RecyclerView recyclerView;
    private EventAdapter adapter;

    private Button buttonSharePoint;

    private FirebaseDatabaseHelper databaseHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            users = (ArrayList<String>) getArguments().getSerializable("POST");
            event_id = getArguments().getString("event_id");
            initAdapter();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_point, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewParticipant);
        users = new ArrayList<>();
        databaseHelper = new FirebaseDatabaseHelper(this);
        buttonSharePoint = view.findViewById(R.id.buttonDistributePoint);

        buttonSharePoint.setOnClickListener(this);
        return view;
    }


    public void initAdapter(){
        if(getActivity() != null){

            adapter = new EventAdapter(getActivity(),users);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
            linearLayoutManager1.setReverseLayout(true);
            recyclerView.setLayoutManager(linearLayoutManager1);
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(View v) {

        for(String x:users){

            if(!x.equals(Utils.getUID(getContext()))){
                HashMap<String,String> id = new HashMap<>();
                id.put("id",x);
                databaseHelper.updateData("Points/" +  x + "/claim/" + Utils.getUID(getContext())  , id);
                databaseHelper.updateData("Points/"  + Utils.getUID(getContext()) + "/owe/" + x  , id);
                databaseHelper.updatePoint("Points/" +  x + "/claim/" + Utils.getUID(getContext()) + "/points");
                databaseHelper.updatePoint("Users/" + x + "/points");
                databaseHelper.updatePoint("Points/"  + Utils.getUID(getContext()) + "/owe/" + x + "/points");
            }

        }
        databaseHelper.searchKey("Event","event_id",event_id);

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getContext(), "Successfully Shared", duration);
        toast.show();

        Fragment frag = new HomeFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(((ViewGroup) (getView().getParent())).getId(), frag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onTaskStart() {

    }

    @Override
    public void onKeyCompleted(String key) {
        HashMap<String,String> status = new HashMap<>();
        status.put("status","closed");
        databaseHelper.updateData("Event/" + key ,status);
    }

    @Override
    public void onTaskCompleteMap(Map data) {

    }

    @Override
    public void onTaskComplete(ArrayList data) {

    }
}
