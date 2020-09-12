package edu.singaporetech.helpla.eventView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.SharePoint;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Event;
import edu.singaporetech.helpla.models.Post;
import edu.singaporetech.helpla.utils.Utils;
import okhttp3.internal.Util;


public class EventDetails extends Fragment implements OnTaskCompletedListener, View.OnClickListener {

    private ArrayList<Event> newItems;
    private ArrayList<String> users;
    private RecyclerView recyclerView;
    private EventAdapter adapter;

    private TextView textViewEvent;
    private TextView textViewDate;
    private TextView textStartTime;
    private TextView textViewEndTime;
    private TextView textViewVenue;
    private TextView textViewStatus;

    private Button buttonEndEvent;
    private Button buttonJoinEvent;

    private String key;
    private String name;
    private FirebaseDatabaseHelper databaseHelper;

    String eid = "";
    String eventid = "";
    String eventkey = "";

    public EventDetails() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        textViewEvent = view.findViewById(R.id.textViewEventNameResult);
        textViewDate = view.findViewById(R.id.textViewStartDateResult);
        textStartTime = view.findViewById(R.id.textViewStartTimeResult);
        textViewEndTime = view.findViewById(R.id.textViewEndTimeResult);
        textViewVenue = view.findViewById(R.id.textViewVenueResult);
        recyclerView = view.findViewById(R.id.recyclerViewEvent);
        buttonEndEvent = view.findViewById(R.id.buttonEndEvent);
        buttonJoinEvent = view.findViewById(R.id.buttonJoinEvent);
        textViewStatus = view.findViewById(R.id.textViewStatusResult);


        buttonEndEvent.setOnClickListener(this);
        buttonJoinEvent.setOnClickListener(this);
        databaseHelper = new FirebaseDatabaseHelper(this);


        newItems = new ArrayList<>();
        users = new ArrayList<>();
        Post post = (Post) getArguments().getSerializable("POST");
        eventid = getArguments().getString("eid");


        eventkey = getArguments().getString("eventKey");


        if (post != null) {
            eid = post.getEid();
            databaseHelper.requestAllData("Event", "event_id", eid);
        } else if (eventid != null) {
            eid = eventid;
            databaseHelper.requestAllData("Event", "event_id", eid);

        }

        databaseHelper.searchKey("Chat" , "id", eid);
        return view;
    }


    @Override
    public void onTaskStart() {

    }

    @Override
    public void onKeyCompleted(String key) {
        this.key = key;

    }

    @Override
    public void onTaskCompleteMap(Map data) {

    }

    @Override
    public void onTaskComplete(ArrayList data) {

        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
        for (Object x : data) {
            Event item = mapper.convertValue(x, Event.class);
            name = item.getEvent_name();
            users.clear();
            if (item.getParticipants() != null) {

                for (Map.Entry<java.lang.String, Event.Participants> entry : item.getParticipants().entrySet()) {
                    users.add(entry.getValue().getId());
                }
                int flag = 0;

                for (String user : users) {
                    if (user.equals(Utils.getUID(getContext()))) {
                        buttonEndEvent.setVisibility(View.INVISIBLE);
                        buttonJoinEvent.setVisibility(View.INVISIBLE);
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0) {
                    buttonJoinEvent.setVisibility(View.VISIBLE);
                    buttonEndEvent.setVisibility(View.INVISIBLE);

                }
                if (item.getOrganizer_id().equals(Utils.getUID(getContext()))) {
                    buttonJoinEvent.setVisibility(View.INVISIBLE);
                    buttonEndEvent.setVisibility(View.VISIBLE);
                }
                if(item.getStatus().equals("closed")){
                    buttonJoinEvent.setVisibility(View.INVISIBLE);
                    buttonEndEvent.setVisibility(View.INVISIBLE);
                }
                newItems.add(item);
            }

        }
        initText();
        initAdapter();
    }

    public void initAdapter() {
        if (getActivity() != null) {
            adapter = new EventAdapter(getActivity(), users);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager1);
            recyclerView.setAdapter(adapter);
        }

    }

    public void initText() {
        for (Event item : newItems) {
            textViewEvent.setText(item.getEvent_name());
            textViewDate.setText(item.getDate_time());
            textStartTime.setText(item.getDate_time());
            textViewEndTime.setText(item.getDate_time());
            textViewVenue.setText(item.getLocation());
            textViewStatus.setText(item.getStatus());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonEndEvent:
                endEvent();
                break;
            case R.id.buttonJoinEvent:
                joinEvent();
                break;

        }
    }

    public void joinEvent() {


        HashMap<String,Object> chat = new HashMap<>();
        HashMap<String, String> user = new HashMap<>();
        user.put("id", Utils.getUID(getActivity()));
        databaseHelper.addKey("Event" + eventkey + "participants");
        databaseHelper.addData("Event/" + eventkey + "/participants/" + databaseHelper.getKey() , user);

        String path1 = "Users/" + Utils.getUID(getActivity()) + "/chat/" + key;
        chat.clear();
        chat.put("chat_name",name + " Chat Room");
        chat.put("status","group");
        databaseHelper.updateData(path1, chat);

    }
    public void endEvent() {
        HashMap<String,String> status = new HashMap<>();
        String path1 = "Event/" + eventkey ;
        status.put("status","closed");
        databaseHelper.updateData(path1,status);
        SharePoint newFragment = new SharePoint();
        Bundle args = new Bundle();
        args.putSerializable("POST", users);
        args.putString("event_id", eid);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.indexfc, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

