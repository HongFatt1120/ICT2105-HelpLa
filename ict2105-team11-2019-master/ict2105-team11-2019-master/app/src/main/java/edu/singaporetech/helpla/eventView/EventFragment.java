
package edu.singaporetech.helpla.eventView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.homeView.HomeFragment;
import edu.singaporetech.helpla.models.Event;

import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.utils.Utils;


public class EventFragment extends Fragment implements View.OnClickListener , OnTaskCompletedListener {

    private EditText editTextEventName;
    private EditText editTextStartTime;
    private EditText editTextEndTime;
    private EditText editTextLocation;
    private EditText editTextDate;
    private Button buttonCreateEvent;
    private Button buttonSkip;

    private String value;
    private String eventDate;
    private String eventStartTime;
    private String eventEndTime;
    private String randomID;
    private String key;
    private String case_id;

    private FirebaseDatabaseHelper databaseHelper;
    private SharedPreferences sharedPref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event, container, false);

        editTextEventName = view.findViewById(R.id.editTextEventName);
        editTextStartTime = view.findViewById(R.id.editTextStartTime);
        editTextEndTime = view.findViewById(R.id.editTextEndTime);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        editTextDate = view.findViewById(R.id.editTextDate);

        buttonCreateEvent = view.findViewById(R.id.buttonCreate);
        buttonSkip = view.findViewById(R.id.buttonSkip);
        databaseHelper = new FirebaseDatabaseHelper(this);
        value = getArguments().getString("ItemKey");
        if(value == null){
            case_id = getArguments().getString("caseID");
            databaseHelper.searchKey("Post","case_id",case_id);
        }
        buttonCreateEvent.setOnClickListener(this);
        editTextDate.setOnClickListener(this);
        editTextStartTime.setOnClickListener(this);
        editTextEndTime.setOnClickListener(this);
        buttonSkip.setOnClickListener(this);
        randomID = Utils.genNum();

        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonCreate:
                createEvent();
                break;
            case R.id.buttonSkip:
                transitHome();
                break;
            case R.id.editTextDate:
                selectDate();
                break;
            case R.id.editTextStartTime:
                selectStartTime("start");
                break;
            case R.id.editTextEndTime:
                selectStartTime("end");
                break;
        }

    }

    /**
     * Transition to viewpost screen after creating event.
     */
    public void transitViewPost(){
        Fragment frag = new EventDetails();
        FragmentManager fm = getFragmentManager();
        Bundle args = new Bundle();
        args.putString("eid", randomID);
        frag.setArguments(args);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(((ViewGroup) (getView().getParent())).getId(), frag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    /**
     * Transition to home fragment if user press on skip
     */
    public void transitHome(){
        Fragment frag = new HomeFragment();
        FragmentManager fm = getFragmentManager();
        Bundle args = new Bundle();
        args.putString("eventID", randomID);
        frag.setArguments(args);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(((ViewGroup) (getView().getParent())).getId(), frag);
        fragmentTransaction.commit();

    }

    /**
     * date picker dialog for choose date for event
     */
    public void selectDate(){
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                eventDate = i+"-"+(i1+1)+"-"+i2;
                editTextDate.setText(eventDate);

            }
        },year,month,day);
        datePickerDialog.show();

    }

    /**
     * Time picker to select start and end time
     * @param startend
     */
    public void selectStartTime(final String startend){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String result = selectedHour + ":" + selectedMinute;
                if(startend.equals("start")){

                    eventStartTime = result;
                    editTextStartTime.setText(eventStartTime);
                }
                else if(startend.equals("end")){
                    eventEndTime = result;
                    editTextEndTime.setText(eventEndTime);
                }

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    /**
     * Create event and stored to firebase
     */
    public void createEvent(){
        Event event = new Event();
        Log.d("HELLO","Create Event " + randomID);
        event.setEvent_id(randomID);
        event.setEvent_name(editTextEventName.getText().toString());
        event.setDate_time(editTextDate.getText().toString());
        event.setLocation(editTextLocation.getText().toString());
        event.setStart_time(editTextStartTime.getText().toString());
        event.setEnd_time(editTextEndTime.getText().toString());
        event.setStatus("open");
        sharedPref = getActivity().getSharedPreferences("data",getActivity().MODE_PRIVATE);
        String uid = sharedPref.getString("UID","");
        event.setOrganizer_id(uid);
        databaseHelper.addKey("Event");
        databaseHelper.addData("Event/"+ databaseHelper.getKey() ,event);

        String eventKey = databaseHelper.getKey();
        HashMap<String,Object> participants = new HashMap<>();
        participants.put("id",Utils.getUID(getActivity()));
        databaseHelper.updateData("Event/" + eventKey  + "/participants/" + Utils.getUID(getContext()), participants);

        if(value !=null){
            updateDB(value);
        }
        else {
            updateDB(key);
        }

        transitViewPost();

    }

    /**
     * update post collection with event id
     * @param value
     */
    public void updateDB(String value){
        HashMap<String,String> user = new HashMap<>();
        user.put("eid", randomID );
        databaseHelper.updateData("Post/" + value ,user);

        databaseHelper.addKey("Users/" + Utils.getUID(getActivity()));
        String chatPath = "Chat/" + databaseHelper.getKey();
        HashMap<String,Object> chat = new HashMap<>();
        chat.put("id",randomID);
        chat.put("chat_name",editTextEventName.getText().toString() + " Chat Room");
        databaseHelper.updateData(chatPath,chat);

        String path1 = "Users/" + Utils.getUID(getActivity()) + "/chat/" + databaseHelper.getKey();
        chat.clear();

        chat.put("chat_name",editTextEventName.getText().toString() + " Chat Room");
        chat.put("status","group");
        databaseHelper.updateData(path1, chat);

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

    }
}