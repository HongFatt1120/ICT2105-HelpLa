package edu.singaporetech.helpla.notificationView;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.chat.ChatsActivityFragment;
import edu.singaporetech.helpla.profileView.ProfileSettingFragment;
//import edu.singaporetech.helpla.chat.;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificaitonFragment extends Fragment {


    public NotificaitonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notificaiton,container,false);

        return view;
    }


}
