package edu.singaporetech.helpla.chat;

import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.helpla.MainActivity;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Points;
import edu.singaporetech.helpla.models.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

        dbHelper = new FirebaseDatabaseHelper(this);
        dbHelper.requestAllData("Users", "uid");
        dbHelper.requestUserChatData("Users");

        final String currentUserName = MainActivity.getCurrentUserName();

        lvDiscussionTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedChatName = String.valueOf(parent.getItemAtPosition(position));
                Intent i = new Intent(getApplicationContext(), DiscussionActivity.class);
                i.putExtra("selected_topic", ((TextView)view).getText().toString());
                i.putExtra("SESSION_CHATNAME", ((TextView)view).getText().toString());

                i.putExtra("SESSION_USERID_OFCHATPARTY", ((TextView)view).getText().toString());
                i.putExtra("SESSION_ACTIVITY", "ChatsActivity");
                i.putExtra("SESSION_USERNAME", MainActivity.getCurrentUserName());


                startActivity(i);
            }
        });

    }

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {

        final ObjectMapper mapper = new ObjectMapper();

        for(Object x : data){
            User item = mapper.convertValue(x, User.class);
            namesList.add(item.getName());
            usersList.add(item);
        }

    }
    @Override
    public void onTaskCompleteMap(Map<String, ArrayList<String>> data) {


    }

    @Override
    public void onKeyCompleted(String key) {

    }
}
