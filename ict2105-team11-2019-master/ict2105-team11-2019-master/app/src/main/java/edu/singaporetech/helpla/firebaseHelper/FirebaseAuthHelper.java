package edu.singaporetech.helpla.firebaseHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import edu.singaporetech.helpla.MainActivity;
import edu.singaporetech.helpla.utils.Utils;

public class FirebaseAuthHelper {
    private FirebaseAuth mAuth;
    private OnAuthCompleted authListener;

    public FirebaseAuthHelper(OnAuthCompleted auth) {
        mAuth = FirebaseAuth.getInstance();
        authListener = auth;
    }

    public FirebaseAuthHelper() {

    }

    public void sendResetEmail(String email, final Context context) {
        if (Utils.validateEmail(email)) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Please kindly check your email to reset", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);

                                context.startActivity(intent);
                            } else
                                Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show();
                        }

                    });
        } else {
            Toast.makeText(context, "Invalid Input", Toast.LENGTH_SHORT).show();
        }
    }

    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    public void updatePassword(String password){

        getCurrentUser().updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            authListener.success();
                        }
                        else
                        {
                            authListener.failure();
                        }
                    }
                });
    }
    public void updateEmail(String email){
        getCurrentUser().updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            authListener.success();

                        }
                        else
                        {
                            authListener.failure();
                        }
                    }
                });
    }

    public interface OnAuthCompleted{
        void success();
        void failure();
    }
}
