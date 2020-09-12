package edu.singaporetech.helpla.chat;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.chat.ChatAdapter;
import edu.singaporetech.helpla.models.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ChatsActivityFragment extends Fragment implements OnTaskCompletedListener<User> {
    private static final String TAG = "ChatsActivityFragment";
    /**Declaration*/
    private RecyclerView recyclerView;
    ArrayList<User> usersList = new ArrayList<>();
    ArrayList<String> namesList = new ArrayList<>();
//    private ArrayList<User> newItems ;
    ArrayList<User.chat> chat = new ArrayList<>();

    private ChatAdapter chatAdapter;

    private FirebaseDatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chats_list, container, false);
        /** Initialization*/
        recyclerView = view.findViewById(R.id.recyclerViewChatList);
//        newItems = new ArrayList<>();

        SharedPreferences sharedPref = view.getContext().getSharedPreferences("userObj",MODE_PRIVATE);
        databaseHelper = new FirebaseDatabaseHelper(this);
        Gson gson = new Gson();
        String json = sharedPref.getString("userObject", "");
        User obj = gson.fromJson(json, User.class);
        databaseHelper.requestAllData("Users", "uid",obj.getUid());

        return view;
    }


    @Override
    public void onTaskStart() {

    }

    @Override
    public void onTaskComplete(ArrayList<Object> data) {

        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
        for(Object x : data) {
            User item = mapper.convertValue(x, User.class);

            if(item.getChat() != null) {
                for (Map.Entry<String, User.chat> entry : item.getChat().entrySet()) {
                    entry.getValue().setKey(entry.getKey());

                    chat.add(entry.getValue());
                }
            }
        }
        initAdapter();
    }



    @Override
    public void onTaskCompleteMap(Map<String, ArrayList<String>> data) {

    }

    @Override
    public void onKeyCompleted(String key) {

    }

    public void initAdapter(){
        chatAdapter = new ChatAdapter(getActivity(),chat);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);  //this is needed
        recyclerView.setLayoutManager(linearLayoutManager1);
        recyclerView.setAdapter(chatAdapter);

    }


}