package edu.singaporetech.helpla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Points;

import edu.singaporetech.helpla.models.User;
import edu.singaporetech.helpla.utils.Utils;

//import com.google.firebase.auth.ActionCodeSettings;

public class RegisterationActivity extends AppCompatActivity  {
    private static final String TAG = "Register_Activity";

    /** Declaration */
    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextChangePassword;
    private EditText editTextName;
    FirebaseDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        /** Initialization */
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextName = findViewById(R.id.name);
        editTextChangePassword = findViewById(R.id.cpw);
        databaseHelper = new FirebaseDatabaseHelper(null);

    }

    /**
     * Create new user account
     * @param view
     */
    public void onSignUpPressed(View view){
        if(editTextName.getText().length() != 0 && editTextEmail.getText().length() != 0 && editTextPassword.getText().length() !=0 && editTextChangePassword.getText().length() != 0) {
            final String email1 = editTextEmail.getText().toString();
            String pw = editTextPassword.getText().toString();
            String confirmPw = editTextChangePassword.getText().toString();
            final String name1 = editTextName.getText().toString();
            if(Utils.validateEmail(email1)) {
                if (pw.equals(confirmPw)){
                    mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        User userobj = new User();
                                        userobj.setName(editTextName.getText().toString());
                                        userobj.setUid(user.getUid());
                                        userobj.setEmail(user.getEmail());
                                        userobj.setPoints(0);
                                        userobj.setImage_url("uploads/Default.jpg");
                                        databaseHelper.addData("Users/" + user.getUid() ,userobj);
                                        HashMap<String,String> userid = new HashMap<>();
                                        userid.put("id",user.getUid());
                                        databaseHelper.updateData("Points/" + user.getUid(),userid);

                                        Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                                        startActivity(intent);

                                    } else {
                                    }
                                }
                            });
                }
                else {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(this, "Password not match", duration);
                    toast.show();
                }

            }
            else {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, "Invalid editTextEmail", duration);
                toast.show();
            }
        }
        else
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, "Empty Fields detected", duration);
            toast.show();
        }


    }

}