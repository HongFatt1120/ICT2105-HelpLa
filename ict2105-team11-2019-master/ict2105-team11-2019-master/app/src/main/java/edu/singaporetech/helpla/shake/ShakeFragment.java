package edu.singaporetech.helpla.shake;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.chat.ChatsActivityFragment;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Post;
import edu.singaporetech.helpla.postView.ViewPostFragment;
import edu.singaporetech.helpla.profileView.ProfileSettingFragment;
import edu.singaporetech.helpla.services.NotificationService;

import static android.content.Context.SENSOR_SERVICE;

public class ShakeFragment extends Fragment implements OnTaskCompletedListener<Post>, ShakeDetector.Listener {
    private static final String TAG = "ShakeFragment";
    private View view;
    private ImageView imgViewShake;
    private OnFragmentInteractionListener mListener;
    private FirebaseDatabaseHelper dbHelper;
    ArrayList<Post> mLostItemsList;

    ImageButton buttonChat_ActionBar;
    ImageButton buttonSetting_ActionBar;


    public ShakeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        ShakeDetector shakeDetector = new ShakeDetector(this);

        shakeDetector.start(sensorManager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shake, container, false);
        dbHelper = new FirebaseDatabaseHelper(this);
        dbHelper.requestAllData("Post","");
        mLostItemsList = new ArrayList<>();

        imgViewShake = view.findViewById(R.id.imgViewShake);
        Glide.with(this).asGif().load(getResources().getIdentifier("img_shake_phone",
                "drawable", getActivity().getPackageName())).into(imgViewShake);

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {
        Log.d(TAG, "onTaskComplete()");
        final ObjectMapper mapper = new ObjectMapper();

        for (Object x : data) {
            Post item = mapper.convertValue(x, Post.class);
            mLostItemsList.add(item);
        }
        Log.d(TAG, "mLostItemsList size = " + mLostItemsList.size());
    }

    @Override
    public void onKeyCompleted(String key) {

    }

    @Override
    public void onTaskCompleteMap(Map<String, ArrayList<String>> data) {

    }

    @Override
    public void hearShake() {
        Log.d(TAG, "hearShake()");
        Random rand = new Random();
        int r = rand.nextInt(mLostItemsList.size());
        Log.d(TAG, "random" + r);

        Post post = mLostItemsList.get(r);

        final Bundle bundle = new Bundle();
        bundle.putSerializable("POST",post);


        ViewPostFragment viewPostFragment = new ViewPostFragment();
        viewPostFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.indexfc, viewPostFragment);
        transaction.addToBackStack(null);

        transaction.commit();

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}