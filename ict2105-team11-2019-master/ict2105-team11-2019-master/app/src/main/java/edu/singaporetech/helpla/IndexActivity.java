package edu.singaporetech.helpla;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import androidx.fragment.app.FragmentTransaction;
import edu.singaporetech.helpla.homeView.HomeFragment;
import edu.singaporetech.helpla.leadershipBoard.LeadershipBoardFragment;
import edu.singaporetech.helpla.models.User;
import edu.singaporetech.helpla.notificationView.NotificaitonFragment;
import edu.singaporetech.helpla.postView.CreatePostFragment;
import edu.singaporetech.helpla.profileView.ProfileFragment;
import edu.singaporetech.helpla.services.NotificationService;
import edu.singaporetech.helpla.shake.ShakeFragment;
import edu.singaporetech.helpla.utils.Utils;

public class IndexActivity extends AppCompatActivity implements OnTaskCompletedListener {

    private static final String TAG = "Index_Activity";
    private ServiceConnection mConnection;
    private NotificationService mService;
    private boolean mBound;
    private BottomNavigationView bottomNav;

    private FirebaseDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        bottomNav = findViewById(R.id.indexbn);
        databaseHelper = new FirebaseDatabaseHelper(this);

        Log.d("JELLO", Utils.getUID(getApplication()));
        databaseHelper.requestAllData("Users", "uid", Utils.getUID(getApplication()));


        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.indexfc, new HomeFragment()).commit();

        String type = getIntent().getStringExtra("From");

        if (type != null) {
            if (type.equals("notifyFrag")) {

                getSupportFragmentManager().beginTransaction().replace(R.id.indexfc, new ShakeFragment()).commit();
            }

        }


        Intent intent = new Intent(this, NotificationService.class);
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "Service Connected");
                NotificationService.LocalBinder binder = (NotificationService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "Service Disconnected");
                mBound = false;
            }
        };
        startService(intent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, NotificationService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            getApplicationContext().stopService(new Intent(this, IndexActivity.class));
            unbindService(mConnection);
            mBound = false;
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    String tag = "";
                    switch (item.getItemId()) {
                        case R.id.nav_index:
                            tag = "Index";
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_leadership:
                            tag = "Leadership";
                            selectedFragment = new LeadershipBoardFragment();
                            break;
                        case R.id.nav_create:
                            tag = "Create";
                            selectedFragment = new CreatePostFragment();
                            break;
                        case R.id.nav_notificaiton:
                            tag = "ShakeME";
                            selectedFragment = new ShakeFragment();
                            break;
                        case R.id.nav_profile:
                            tag = "Profile";
                            selectedFragment = new ProfileFragment();
                        break;


                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.indexfc,
                            selectedFragment, tag).commit();
                    return true;
                }
            };

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onKeyCompleted(String key) {

    }

    @Override
    public void onTaskCompleteMap(Map data) {

    }

    @Override
    public void onTaskComplete(ArrayList data) {
        final ObjectMapper mapper = new ObjectMapper();


        SharedPreferences mPrefs = getSharedPreferences("userObj", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        User user;
        String json = "";
        for (Object x : data) {
            user = mapper.convertValue(x, User.class);
            json = gson.toJson(user);
        }
        prefsEditor.putString("userObject", json);
        prefsEditor.commit();
    }

}
