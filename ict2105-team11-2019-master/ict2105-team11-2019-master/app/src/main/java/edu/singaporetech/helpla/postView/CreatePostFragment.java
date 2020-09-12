package edu.singaporetech.helpla.postView;



import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import edu.singaporetech.helpla.MapActivity;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.chat.ChatsActivityFragment;
import edu.singaporetech.helpla.eventView.EventFragment;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.FirebaseStorageHelper;
import edu.singaporetech.helpla.models.Post;
import edu.singaporetech.helpla.profileView.ProfileSettingFragment;
import edu.singaporetech.helpla.utils.Utils;


import static android.app.Activity.RESULT_OK;


public class CreatePostFragment extends Fragment implements View.OnClickListener {

    /**
     * Declaration
     */
    private EditText editTextName;
    private EditText editTextLastSeen;
    private EditText editTextDescription;
    private EditText editTextCaseID;
    private ImageButton buttonMap;
    private Button buttonReport;

    private SharedPreferences sharedPref;
    private ImageView imageView1;
    private Uri imageUri;


    String longtitude;
    String latitude;

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseDatabaseHelper databaseHelper;
    private FirebaseStorageHelper storageHelper;

    ImageButton buttonChat_ActionBar;
    ImageButton buttonSetting_ActionBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        /** Initialization*/
        buttonMap = view.findViewById(R.id.buttonMap);
        buttonReport = view.findViewById(R.id.buttonReport);
        editTextName = view.findViewById(R.id.editTextItemName);
        editTextLastSeen = view.findViewById(R.id.editTextLastSeenLocation);
        editTextDescription = view.findViewById(R.id.editTextDetails);
        editTextCaseID = view.findViewById(R.id.editTextCaseID);

        imageView1 = view.findViewById(R.id.imageViewLostItem);

        databaseHelper = new FirebaseDatabaseHelper(null);
        storageHelper = new FirebaseStorageHelper(null);

        /** Set Onclick listener*/
        buttonMap.setOnClickListener(this);
        buttonReport.setOnClickListener(this);
        imageView1.setOnClickListener(this);

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

    /**
     * Intent to Map Activity
     */
    public void onMapPressed() {
        Intent intent = new Intent(getActivity(), MapActivity.class);
        SharedPreferences.Editor editor = getContext().getSharedPreferences("postData", getContext().MODE_PRIVATE).edit();
        editor.putString("caseID", editTextCaseID.getText().toString());
        editor.putString("itemName", editTextName.getText().toString());
        editor.putString("Description",editTextDescription.getText().toString());
        editor.apply();
        startActivityForResult(intent,1000);

    }

    /**
     * Insert data to firebase
     */
    public void onReportPressed() {


        if(editTextName.getText().length() >= 1 && editTextCaseID.getText().length() >= 1 || editTextDescription.getText().length() >= 1 || editTextLastSeen.getText().length() >= 1){
            Post post = new Post();
            post.setCase_id(editTextCaseID.getText().toString());
            post.setDescription(editTextDescription.getText().toString());
            post.setLast_seen_location(editTextLastSeen.getText().toString());
            post.setLat(latitude);
            post.setLongtitude(longtitude);
            post.setStatus("Not Found");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            post.setTimestamp(formatter.format(cal.getTime()));
            post.setTitle(editTextName.getText().toString());
            post.setUid(Utils.getUID(getContext()));
            databaseHelper.addKey("Post");
            databaseHelper.addData("Post/" + databaseHelper.getKey(),post);
            storageHelper.uploadFile(getActivity(),imageUri,"Post/" + databaseHelper.getKey());
            Toast toast = Toast.makeText(getContext(),
                    "Success", Toast.LENGTH_SHORT); toast.show();
            EventFragment newFragment = new EventFragment();
            Bundle args = new Bundle();
            args.putString("ItemKey", databaseHelper.getKey());
            newFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.indexfc, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }



    }


    /**
     * Open file chooser to select photo
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView1);
        }
        else if(requestCode == 1000 && resultCode == 1000){
            SharedPreferences prefs = getContext().getSharedPreferences("postData", getContext().MODE_PRIVATE);
            editTextCaseID.setText(prefs.getString("caseID", ""));
            editTextName.setText(prefs.getString("itemName", ""));
            editTextDescription.setText(prefs.getString("Description", ""));
            longtitude = data.getStringExtra("long");
            longtitude = data.getStringExtra("lat");
            Log.d("HELLO1",longtitude);
            editTextLastSeen.setText(data.getStringExtra("location"));



        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonMap:
                onMapPressed();
                break;
            case R.id.buttonReport:
                onReportPressed();
                break;
            case R.id.imageViewLostItem:
                openFileChooser();
                break;
        }

    }


}