package edu.singaporetech.helpla.profileView;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import edu.singaporetech.helpla.MainActivity;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseAuthHelper;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.utils.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSettingFragment extends Fragment implements FirebaseAuthHelper.OnAuthCompleted , View.OnClickListener {

    EditText editTextName;
    EditText editTextEmail;
    EditText editTextNewPassword;
    EditText editTextConfirmPassword;
    Button buttonSave;

    Button buttonlogout;

    FirebaseDatabaseHelper dbHelper;

    OnTaskCompletedListener lstr;

    boolean changesDetected = false;
    boolean validEmail = false;
    boolean validPassword = false;

    FirebaseAuthHelper authHelper;

    private static final String TAG = "ProfileSetting_Fragment";
    public ProfileSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        editTextName = (EditText) view.findViewById(R.id.editTextName);
        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextNewPassword = (EditText) view.findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = (EditText) view.findViewById(R.id.editTextConfirmPassword);
        buttonSave = (Button) view.findViewById(R.id.buttonSave);
        buttonlogout = view.findViewById(R.id.buttonlogout);

        buttonlogout.setOnClickListener(this);
        final String currentName = Utils.getUserObj(getContext()).getName();
        final String currentEmail = Utils.getUserObj(getContext()).getEmail();

        editTextName.setText(currentName);
        editTextEmail.setText(currentEmail);

        dbHelper = new FirebaseDatabaseHelper(lstr);

        authHelper = new FirebaseAuthHelper(this);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Check for changes then validate
                 * */
                //Check if NAME got difference
                //if not equal, then update. else, do nth
                /**
                 * NAME VALIDATION
                 * */
                if(!currentName.equals(editTextName.getText().toString())){
                    String path = "Users/" + Utils.getUID(getContext());
                    HashMap<String,Object> nameMap = new HashMap<>();
                    nameMap.put("name", editTextName.getText().toString());
                    dbHelper.updateData(path, nameMap);
                    changesDetected = true;
                }
                /**
                 * EMAIL VALIDATION
                 * */
                if(validate(editTextEmail.getText().toString()) == true){
                    String path = "Users/" + Utils.getUID(getContext());
                    HashMap<String,Object> emailMap = new HashMap<>();
                    emailMap.put("email", editTextEmail.getText().toString());
                    dbHelper.updateData(path, emailMap);
                    validEmail = true;
                    authHelper.updateEmail(editTextEmail.getText().toString());
                    changesDetected = true;

                }


                /**
                 * PASSWORD VALIDATION
                 * */

                if(!editTextNewPassword.equals(editTextNewPassword.getText().toString())){
                    if(editTextNewPassword.getText().toString().length() >= 6){
                        if(editTextNewPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())){
                            validPassword = true;
                            authHelper.updatePassword(editTextConfirmPassword.getText().toString());
                            changesDetected = true;
                        }
                    }
                }


                if((changesDetected == true) || (validEmail == true) || (validPassword == true)){
                    Toast.makeText(getContext(),"Changes Saved!", Toast.LENGTH_SHORT).show();
                }



            }
        });


            // Inflate the layout for this fragment
        return view;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    @Override
    public void success() {
        Toast.makeText(getContext(),"Success!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void failure() {
        Toast.makeText(getContext(),"Failure!", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonlogout:
            logout();
            break;
        }
    }
    public void logout(){

        FirebaseAuth.getInstance().signOut();
        SharedPreferences settings = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
