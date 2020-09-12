package edu.singaporetech.helpla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Points;
import edu.singaporetech.helpla.models.User;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TempChatActivity extends AppCompatActivity implements OnTaskCompletedListener<User>{

    TextView textViewName;
    TextView textViewCurrentUserName;
    Button buttonAccept;
    Button buttonReject;
    Button buttonAcceptCurrentUser;
    Button buttonRejectCurrentUser;
    private static final String TAG = "TempChatActivity";

    FirebaseDatabaseHelper dbHelper;
    OnTaskCompletedListener lstr;

    int uid_currentOwePoints;
    String uid_ToDeductPointsFrom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_chat);

        final String session_userid= getIntent().getStringExtra("SESSION_USERID");
        final String session_username= getIntent().getStringExtra("SESSION_USERNAME");
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewName.setText(session_username); // name of person who owe u pts
        textViewCurrentUserName = (TextView) findViewById(R.id.textViewCurrentUserId);
        textViewCurrentUserName.setText(MainActivity.getUID());
//        textViewCurrentUserName.setText(MainActivity.getCurrentUserName());

        // if user u are claiming from press accept/reject
        buttonAccept = (Button) findViewById(R.id.buttonAccept);
        buttonReject = (Button) findViewById(R.id.buttonReject);

        //set btns to invisible or visible based on previous activity
        //means whether is from claimlist or owelist
        final String session_buttonAccept= getIntent().getStringExtra("buttonAccept");
        final String session_buttonReject= getIntent().getStringExtra("buttonReject");
        if(session_buttonAccept.equals("visible")){
            buttonAccept.setVisibility(View.VISIBLE);
        }
        else{
            buttonAccept.setVisibility(View.GONE);
        }
        if(session_buttonReject.equals("visible")){
            buttonReject.setVisibility(View.VISIBLE);
        }
        else{
            buttonReject.setVisibility(View.GONE);
        }


        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Query q = FirebaseDatabase.getInstance().getReference().child("Points");
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            if (MainActivity.getUID().equals(itemSnapshot.getKey())) {
                                DataSnapshot contentSnapshot = itemSnapshot.child("/claim");
                                Iterable<DataSnapshot> matchSnapShot = contentSnapshot.getChildren();
                                for (DataSnapshot match : matchSnapShot){
                                    Points p = match.getValue(Points.class);
                                    Log.d(TAG, "qwe^ " + p.getId());
                                    // must check if id equals, if not it will loop thru all the ids and deduct their pts
                                    if(p.getId().equals(session_userid)){
                                        uid_ToDeductPointsFrom = p.getId();
                                        //then u get his/her pts
                                       // uid_currentOwePoints = p.getPoints();
                                        String userId = itemSnapshot.getKey();
                                        String type = contentSnapshot.getKey();
                                        String objectId = match.getKey();
                                        // path e.g. Points/uyVfBg9IjTQA0lIslsBLlJLUQLh1/claim/0
                                        String path = "Points/" + userId + "/" + type + "/" + objectId;
                                        //on once click accept, deduct the point that this user owed you
                                        uid_currentOwePoints -=1;

                                        dbHelper = new FirebaseDatabaseHelper(lstr);
                                        //if deduct until the points == 0, then remove the entire data
                                        if(String.valueOf(uid_currentOwePoints).equals("0")){
                                            itemSnapshot.getRef().child("/claim/" + objectId).removeValue();
                                        }
                                        else {
                                            //else then update data accordingly
                                            HashMap<String, Object> obj = new HashMap<>();
                                            obj.put("id_status", "accept");
                                            obj.put("points", String.valueOf(uid_currentOwePoints));
                                            dbHelper.updateData(path, obj);
                                        }
                                    }

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

//        RegisterationActivity registerationActivity = new RegisterationActivity();
//        registerationActivity.createNewUserPointsData(MainActivity.getUID(),databaseHelper,lstr);


        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Query q = FirebaseDatabase.getInstance().getReference().child("Points");
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            if (MainActivity.getUID().equals(itemSnapshot.getKey())) {
                                DataSnapshot contentSnapshot = itemSnapshot.child("/claim");
                                Iterable<DataSnapshot> matchSnapShot = contentSnapshot.getChildren();
                                for (DataSnapshot match : matchSnapShot){
                                    Points p = match.getValue(Points.class);
                                    Log.d(TAG, "qwe^ " + p.getId());
                                    // must check if id equals, if not it will loop thru all the ids and deduct their pts
                                    if(p.getId().equals(session_userid)){
                                        uid_ToDeductPointsFrom = p.getId();
                                        //then u get his/her pts
                                        //uid_currentOwePoints = p.getPoints();
                                        // path e.g. Points/uyVfBg9IjTQA0lIslsBLlJLUQLh1/claim/0
                                        String path = "Points/" + itemSnapshot.getKey() +
                                                "/" + contentSnapshot.getKey() +
                                                "/" + match.getKey();
                                        //on once click accept, deduct the point that this user owed you
                                        HashMap<String, Object> obj = new HashMap<>();
                                        obj.put("id_status", "reject");
                                        dbHelper = new FirebaseDatabaseHelper(lstr);
                                        dbHelper.updateData(path,obj);
                                    }

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {
        ArrayList<User> newItems = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper

        for(Object x : data){
            User item = mapper.convertValue(x, User.class);
            newItems.add(item);
//           if(item.getId().equals(uidToDeductPointsFrom)){
//               Log.d(TAG, "qwe!! " + item.getPoints());
//           }
              // Log.d(TAG, "qwe!! " + item.getId());

        }


    }

    @Override
    public void onTaskCompleteMap(Map<String, ArrayList<String>> data) {

    }

    @Override
    public void onKeyCompleted(String key) {

    }


}
