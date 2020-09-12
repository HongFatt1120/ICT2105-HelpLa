package edu.singaporetech.helpla.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.helpla.MainActivity;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.User;
import edu.singaporetech.helpla.utils.Utils;
import okhttp3.internal.Util;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DiscussionActivity extends AppCompatActivity implements OnTaskCompletedListener<User>, View.OnClickListener {

    Button btnSendMsg;
    EditText etMsg;
    ListView lvDiscussion;
    ArrayList<String> listConversation = new ArrayList<String>();
    ArrayAdapter arrayAdpt;
    String UserName, SelectedTopic, user_msg_key;

    private DatabaseReference dbr;
    private static final String TAG = "DiscussionActivity";

    ArrayList<Integer> idList = new ArrayList<>();

    LinearLayout linearLayoutButtons;

    TextView chatTitleName;
    String chatName;
    String currentUserName;
    String roles;

    FirebaseDatabaseHelper dbHelper;
    OnTaskCompletedListener lstr;

    boolean chatExist = false;

    String getChatName;
    String getOtherPartyUID;
    String existingChatKey;

    ArrayList<User.chat> chat = new ArrayList<>();
    String oppUID;

    private Button buttonAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        currentUserName = Utils.getUserObj(getApplicationContext()).getName();

        btnSendMsg =  findViewById(R.id.btnSendMsg);
        etMsg =  findViewById(R.id.etMessage);
        lvDiscussion = findViewById(R.id.lvConversation);
        buttonAccept = findViewById(R.id.buttonAccept);
        buttonAccept.setOnClickListener(this);
        arrayAdpt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listConversation);
        lvDiscussion.setAdapter(arrayAdpt);

        linearLayoutButtons = findViewById(R.id.linearLayoutButtons);

        chatTitleName = findViewById(R.id.textViewNameOfChatPerson);

        getChatName = getIntent().getExtras().get("chat_name").toString();
        if(getIntent().getExtras().get("uid_of_other_party") != null)
        {
            oppUID = getIntent().getExtras().get("uid_of_other_party").toString();
        }

        chatName = getChatName;
        //.d(TAG, "UID :  " + oppUID);

        chatTitleName.setText(getChatName);
        dbHelper = new FirebaseDatabaseHelper(this);

       // dbHelper.searchKey("Chat","chat_id");

        final Query q = FirebaseDatabase.getInstance().getReference().child("Chat");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "zxc: " + itemSnapshot.getKey());
                    String chat_name_id = itemSnapshot.getKey();
                    if (Utils.getUserObj(getApplicationContext()).getChat() != null) {
                        for (Map.Entry d : Utils.getUserObj(getApplicationContext()).getChat().entrySet()) {
                            existingChatKey = (String) d.getKey();
                            Log.d("AAA",existingChatKey);
                            Log.d("SSS",chat_name_id);
                            if (chat_name_id.equals(existingChatKey)) {
                                chatExist = true;
                                break;
                            }
                        }
                    }

                }

                //if chat dont exist, create new
                if (chatExist == false) {

                    /* CREATE A CHAT START */
                    //with starting empty chat msg
                    dbHelper = new FirebaseDatabaseHelper(null);
                    HashMap<String, Object> abc = new HashMap<>();
                    dbHelper.addKey("Chat");
                    // abc.put("chat_name",dbHelper.getKey());
                    existingChatKey = dbHelper.getKey();
                    Log.d("HELLO123", existingChatKey);
                    abc.put("chat_name", getChatName);
                    dbHelper.addData("Chat/" + existingChatKey, abc);
                    abc.clear();
//                     abc.put("id",0);
                    abc.put("msg", " joined");
                    abc.put("user", currentUserName);
                    dbHelper.addData("Chat/" + existingChatKey + "/message/0", abc);
                    /* CREATE A CHAT END */
                    /* UPDATE USER DATA  */
                    updateUsersDataWithChatName();

                    Log.d(TAG, "onDataChange: ddd chat dont exist");
                } else {
                    //else dont create new chat
                    Log.d(TAG, "ddd chat existed ");
                    Log.d(TAG, "ssd  " + Utils.getUserObj(getApplicationContext()).getUid());
                    dbHelper.requestAllData("Users", "uid", Utils.getUserObj(getApplicationContext()).getUid());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        dbr = FirebaseDatabase.getInstance().getReference().child("Chat/" +
                getChatName + "/message");

//        dbr.child("id").orderByValue().equalTo(true); // to sort the ids of each chat
//        dbr.c


        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(Utils.getUserObj(getApplicationContext()).getName(),
                        etMsg.getText().toString());
            }
        });


        dbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //
    public void updateConversation(DataSnapshot dataSnapshot) {
        String msg, user, conversation, id;
        String keyNo = dataSnapshot.getKey();
        Iterator i = dataSnapshot.getChildren().iterator();
        Log.d(TAG, "updateConversation: fff " + i.hasNext());

        //Loop through each message and update
        while (i.hasNext()) {
            Log.d(TAG, "fff: keyNo" + keyNo);

//            id = String.valueOf(((DataSnapshot) i.next()).getValue());
            msg = (String) ((DataSnapshot) i.next()).getValue();
            user = (String) ((DataSnapshot) i.next()).getValue();


//            Log.d(TAG, "updateConversation: " + id);
            idList.add(Integer.parseInt(keyNo));

            conversation = user + ": " + msg;
            arrayAdpt.insert(conversation, arrayAdpt.getCount());
            arrayAdpt.notifyDataSetChanged();
        }
        Log.d(TAG, "idlist: " + idList);
    }

    //
    private void updateUsersDataWithChatName() {
        /**
         * Update current user's chat object
         * */
        String path1 = "Users/" + MainActivity.getUID() + "/chat/" + existingChatKey;
        HashMap<String, Object> chatDetails1 = new HashMap<>();
        chatDetails1.put("chat_name", getChatName);
        chatDetails1.put("status", "normal");
        dbHelper.updateData(path1, chatDetails1);

        /**
         * Update the other user's chat object
         * Update the other user's chat status to "request" because he is helping current user with a favour
         * */
//        getOtherPartyUID = getIntent().getExtras().get("uid_of_other_party").toString();

        String path2 = "Users/" + getOtherPartyUID + "/chat/" + existingChatKey;
        HashMap<String, Object> chatDetails2 = new HashMap<>();
        chatDetails2.put("chat_name", currentUserName);
        chatDetails2.put("status", "request");
        dbHelper.updateData(path2, chatDetails2);

    }

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {
        final ObjectMapper mapper = new ObjectMapper();
        for (Object x : data) {
            User u = mapper.convertValue(x, User.class);
            /**
             * When this activity is entered, check if the current user's chat status is
             * "normal" or "request".
             * if "normal", dont show buttons.
             * if "request", show buttons
             * */
            for (Map.Entry<String, User.chat> entry : u.getChat().entrySet()) {
                entry.getValue().setKey(entry.getKey());
                Log.d(TAG, "onTaskComplete: dfg" + existingChatKey);
                Log.d(TAG, "onTaskComplete: dfg" + entry.getValue().getKey());
                if (entry.getValue().equals(existingChatKey)) {
                    if (entry.getValue().getStatus().equals("request")) {
                        linearLayoutButtons.setVisibility(View.VISIBLE);
                        roles = "request";

                    } else {
                        linearLayoutButtons.setVisibility(View.GONE);
                        roles = "owner";

                    }

                }
            }
        }


    }

    @Override
    public void onTaskCompleteMap(Map<String, ArrayList<String>> data) {

    }

    @Override
    public void onKeyCompleted(String key) {

    }


    //
    public void sendMessage(String username, String msg) {
        Map<String, Object> map = new HashMap<String, Object>();
        user_msg_key = dbr.push().getKey();
        dbr.updateChildren(map);
        DatabaseReference dbr2 = dbr.child(user_msg_key);
        Map<String, Object> map2 = new HashMap<String, Object>();

        String checkMsg = etMsg.getText().toString();
        // if it is not a blank message then proceed to add in database
        if (checkMsg.trim().length() != 0) {
            map2.put("msg", msg);
            map2.put("user", username);
            Integer nextKeyNo = 0;
            if(idList.size() != 0){
                nextKeyNo = Collections.max(idList) + 1;
                DatabaseReference dbr3 = dbr.child(String.valueOf(nextKeyNo));
                dbr3.updateChildren(map2);
            }
            else
            {
                DatabaseReference dbr3 = dbr.child(String.valueOf(nextKeyNo));
                dbr3.updateChildren(map2);
            }



            // Clear send editText
            etMsg.setText("");
//            finish();
//            startActivity(getIntent());
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonAccept:
                accept();
                break;
        }

    }
    public void accept(){

//        Log.d("HELLO1234",oppUID);
        linearLayoutButtons.setVisibility(View.INVISIBLE);
        String requestPath = "Points/" + Utils.getUID(getApplicationContext()) + "/claim/" +  oppUID + "/points";
        dbHelper.reducePoint(requestPath);

        String onwerPath = "Points/" + oppUID + "/owe/ " +  Utils.getUID(getApplicationContext())+ "/points";
        dbHelper.reducePoint(onwerPath);

    }
}