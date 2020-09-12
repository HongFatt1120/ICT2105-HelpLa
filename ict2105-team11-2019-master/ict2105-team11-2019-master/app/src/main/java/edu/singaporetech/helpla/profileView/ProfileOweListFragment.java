package edu.singaporetech.helpla.profileView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Points;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileOweListFragment extends Fragment {


    private RecyclerView recyclerView;
    ProfileOweListAdapter claimAdapter;
    ArrayList<String[]> map;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_owe, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewOweList);
        map = new ArrayList<>();
        Points point = (Points) getArguments().getSerializable("points");
        for (Map.Entry<String, Points.owe> entry : point.getOwe().entrySet()) {
//            Points.claim c = new Points.claim();
            String array[] = new String[] {entry.getValue().getid(),entry.getValue().getPoints() + ""};
            map.add(array);
        }
        initView();
        //Toast.makeText(this.getContext(),userid,Toast.LENGTH_SHORT).show();


        return view;
    }

    public void initView(){

        claimAdapter = new ProfileOweListAdapter(getActivity(),map);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);  //this is needed
        recyclerView.setLayoutManager(linearLayoutManager1);
        recyclerView.setAdapter(claimAdapter);
    }


}
