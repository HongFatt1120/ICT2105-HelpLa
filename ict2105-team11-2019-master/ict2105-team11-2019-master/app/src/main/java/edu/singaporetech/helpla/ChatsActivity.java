package edu.singaporetech.helpla;

import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Points;
import edu.singaporetech.helpla.models.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatsActivity extends AppCompatActivity implements OnTaskCompletedListener<User> {

    /* THIS JAVA FILE IS TO DISPLAY A LIST OF CHATS OF USER'S*/

    ListView lvDiscussionTopics;
    ArrayAdapter arrayAdpt;

    private FirebaseDatabaseHelper dbHelper;

    private static final String TAG = "ChatsActivity";
    ArrayList<User> usersList = new ArrayList<>();
    ArrayList<String> namesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        lvDiscussionTopics = (ListView) findViewById(R.id.lvDiscussionTopics);

        SharedPreferences sharedPref = getSharedPreferences("userObj",MODE_PRIVATE);
        dbHelper = new FirebaseDatabaseHelper(this);
        Gson gson = new Gson();
        String json = sharedPref.getString("userObject", "");
        User obj = gson.fromJson(json, User.class);
        dbHelper.requestAllData("Users", "uid",obj.getUid());
//        dbHelper.requestUserChatData("Users");
//
//        final String currentUserName = "atat";

//        lvDiscussionTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selectedChatName = String.valueOf(parent.getItemAtPosition(position));
//                Intent i = new Intent(getApplicationContext(), DiscussionActivity.class);
//                i.putExtra("selected_topic", ((TextView)view).getText().toString());
//                i.putExtra("user_name", dbHelper.getName());
//                i.putExtra("SESSION_ACTIVITY", "ChatsActivity");
//
//                //check if selected chat name is == to any user's name (e.g. kittycat)
//                //if yes means its a p2p chat. then we need to attach the name to
//                //this current user's name and pass it to the next screen (intent.putExtra)
//                // e.g. atat&kittycat
//                //else set the intent.putExtra to null
//
//
//
//                for(String a : namesList){
//                    Log.d("HELLO",a);
//                    if(selectedChatName.equals(a)){
////                        i.putExtra("SESSION_CHATNAME", currentUserName + "&" + a);
//                        //if
//                        String tempStr = currentUserName+"&"+selectedChatName;
//                        String split0 = tempStr.split("&")[0];
//                        String split1 = tempStr.split("&")[1];
//                        if(currentUserName.equals(split0)){
//                            i.putExtra("SESSION_CHATNAME", currentUserName + "&" + a);
//                        }
//                        else{
//                            i.putExtra("SESSION_CHATNAME", a + "&" + currentUserName);
//                        }
//                        break;
//                    }
//                    else{
//                        Log.d("HELLO   ",selectedChatName);
//                        i.putExtra("SESSION_CHATNAME", selectedChatName);
//                    }
////
////
//                }
//
//                startActivity(i);
//            }
//        });

    }

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {

//        final ObjectMapper mapper = new ObjectMapper();
//        ArrayList finalChatsList = new ArrayList();
//        for(Object x : data){
//            User item = mapper.convertValue(x, User.class);
//            for (String chat:item.getChat()){
//                finalChatsList.add(chat);
//            }
//            arrayAdpt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, finalChatsList);
//            lvDiscussionTopics.setAdapter(arrayAdpt);
//
//            //Log.d(TAG, "aaa: " + item.getName());
//           // namesList.add(item.getName());
//            // usersList.add(item);
//        }

    }
    @Override
    public void onTaskCompleteMap(Map<String, ArrayList<String>> data) {

//        String symbol = "&";
//        ArrayList allChatsList = new ArrayList();
//        ArrayList<String> p2p_chats = new ArrayList();
//        ArrayList finalChatsList = new ArrayList();
//
//        for(Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
//            String key = entry.getKey();
//            allChatsList.add(entry.getValue());
//        }
//
//
//        //since for p2p chats we need to extract out the other party's name, we need to create a hashmap
//        //specially for it e.g. [{"atat","kitty"}, {"atat","johndoe123"}]
//        //take out the chat topics with '&' inside and put in another array.
//        //means its p2p chat
//        for(int i=0; i<allChatsList.size(); i++){
//            if(String.valueOf(allChatsList.get(i)).contains(symbol)){
//                p2p_chats.add(String.valueOf(allChatsList.get(i)));
//            }
//            else{
//                //else then put in the final list first
//                finalChatsList.add(String.valueOf(allChatsList.get(i)));
//            }
//        }
//
//        //then split it and put in hashmap
//        Map<String,String> p2p_chatsMap = new HashMap<>();
//        for(int j=0; j<p2p_chats.size(); j++){
//            String[] words = p2p_chats.get(j).split(symbol);
//            p2p_chatsMap.put(words[0],words[1]);
//        }
//        //once in hashmap, extract out only the other partys' names(aka values) and
//        //add all in a separate list tgt with the other chats, to populate the lvDiscussionTopics.setAdapter()
//        Log.d(TAG, "ggg: " + p2p_chatsMap.toString());
//
//        for (Map.Entry<String, String> item : p2p_chatsMap.entrySet()) {
//            String key = item.getKey();
//            String value = item.getValue();
//            finalChatsList.add(value);
//        }
//
//        Log.d(TAG, "ggg: " + allChatsList.size());
//        Log.d(TAG, "ggg: " + finalChatsList.toString());
//
////        arrayAdpt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, allChatsList);
//        arrayAdpt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, finalChatsList);
//
//        lvDiscussionTopics.setAdapter(arrayAdpt);

    }

    @Override
    public void onKeyCompleted(String key) {

    }
}
