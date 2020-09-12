package edu.singaporetech.helpla.profileView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.models.Points;

public class ProfileClaimListFragment extends Fragment{


    private RecyclerView recyclerView;
    ProfileClaimListAdapter claimAdapter;
    ArrayList<String[]> map;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_claim, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewClaimList);
        map = new ArrayList<>();
        Points point = (Points) getArguments().getSerializable("points");
        if(point.getClaim() != null){
            for (Map.Entry<String, Points.claim> entry : point.getClaim().entrySet()) {
                String array[] = new String[] {entry.getValue().getid(),entry.getValue().getPoints() + ""};
                map.add(array);
            }
            initView();
        }

        return view;
    }


    public void initView(){

        claimAdapter = new ProfileClaimListAdapter(getActivity(),map);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);  //this is needed
        recyclerView.setLayoutManager(linearLayoutManager1);
        recyclerView.setAdapter(claimAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
