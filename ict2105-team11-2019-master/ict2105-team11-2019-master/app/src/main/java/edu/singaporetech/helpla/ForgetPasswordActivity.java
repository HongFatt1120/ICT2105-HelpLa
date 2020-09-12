package edu.singaporetech.helpla;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import edu.singaporetech.helpla.firebaseHelper.FirebaseAuthHelper;
import edu.singaporetech.helpla.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;


public class ForgetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgetPassword_Fragment";
    /**Declaration*/
    private EditText editTextEmail;
    private FirebaseAuthHelper authHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**Initialization*/
        setContentView(R.layout.activity_forget_password);
        editTextEmail = findViewById(R.id.editTextUsername);
        authHelper = new FirebaseAuthHelper();
    }

    /**
     * Send an email to the user to reset password
     * @param v
     */
    public void forgetPwSubmit(View v) {
        authHelper.sendResetEmail(editTextEmail.getText().toString(),getApplicationContext());
    }

}
