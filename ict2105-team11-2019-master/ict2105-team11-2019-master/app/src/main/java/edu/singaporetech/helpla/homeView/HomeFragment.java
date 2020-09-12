package edu.singaporetech.helpla.homeView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.chat.ChatsActivityFragment;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Post;
import edu.singaporetech.helpla.profileView.ProfileSettingFragment;
import edu.singaporetech.helpla.services.NotificationService;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements OnTaskCompletedListener<Post>, View.OnClickListener {
    private static final String TAG = "Home_Fragment";
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private FirebaseDatabaseHelper dbHelper;
    private ImageButton btnSpeak;
    private ImageView imageViewSetting;
    private View view;
    private EditText editTextSearch;
    private RelativeLayout loading;
    private ServiceConnection mConnection;
    private NotificationService mService;
    ImageButton buttonChat_ActionBar;
    ImageButton buttonSetting_ActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        loading = view.findViewById(R.id.loadingRL);
        dbHelper = new FirebaseDatabaseHelper(this);
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        mRecyclerView = view.findViewById(R.id.recyclerViewLostItem);
        dbHelper.requestAllData("Post", "");

        btnSpeak = view.findViewById(R.id.btnSpeak);
        editTextSearch = view.findViewById(R.id.editTextSearch);

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


        /**
         * Start Notification Service
         */



        //Speach to Text
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Hi! Say something!");
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {

                }
            }


        });



        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    Log.d(TAG, "onTextChange():" + s);
                    search(s.toString());
                } else {
                    dbHelper.requestAllData("Post", "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void search(String toString) {
        Log.d(TAG, "search()");
        dbHelper.requestAllData("Post", "title", toString);

    }

    @Override
    // Receiving speech input
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        HomeFragment.super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editTextSearch.setText(result.get(0));

                }
                break;
            }

        }
    }


    /**
     * Database
     */
    @Override
    public void onTaskStart() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {
        ArrayList<Post> mLostItemsList = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper();

        mLostItemsList.clear();
        for (Object x : data) {
            Post item = mapper.convertValue(x, Post.class);
            mLostItemsList.add(item);
        }

        if (getActivity() != null) {
            mHomeAdapter = new HomeAdapter(getActivity(), mLostItemsList);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mHomeAdapter);
            mRecyclerView.setHasFixedSize(true);
//            mRecyclerView.setItemViewCacheSize(40);
//            mRecyclerView.setDrawingCacheEnabled(true);
//            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
        }
        loading.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onDestroy() {
        Log.d("ABC", "ABC");
        super.onDestroy();
        Log.d("ABC", "AB");

    }

    @Override
    public void onKeyCompleted(String key) {

    }

    @Override
    public void onTaskCompleteMap(Map<String, ArrayList<String>> data) {

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "hello");
    }

}

