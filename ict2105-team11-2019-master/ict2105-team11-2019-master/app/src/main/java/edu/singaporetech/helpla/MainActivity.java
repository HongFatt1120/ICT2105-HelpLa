package edu.singaporetech.helpla;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import edu.singaporetech.helpla.utils.Utils;
//import edu.singaporetech.helpla.chat.ChatsActivity;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main_Activity";
    /** Declaration*/
    private ImageView icon;
    private LinearLayout linearLayoutLogin;
    private EditText username;
    private EditText password;
    private FirebaseAuth mAuth;
    private Boolean isChecked = false;
    private RelativeLayout loading;
    public static SharedPreferences sharedPref ;
    Context context;

    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**Animation*/
        icon = (ImageView) findViewById(R.id.icon);
        linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayoutLogin);
        loading = (RelativeLayout) findViewById(R.id.loadingRL);
        AnimatorSet bouncer = new AnimatorSet();

        ObjectAnimator anim3 = ObjectAnimator.ofFloat(icon, "translationY", 300f, 0f);
        anim3.setDuration(2000);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(linearLayoutLogin, View.ALPHA, 0,1);
        anim4.setDuration(2000);
        ObjectAnimator anim5 = ObjectAnimator.ofFloat(linearLayoutLogin, View.ALPHA, 1,0);
        anim5.setDuration(0);

        bouncer.play(anim4).after(anim3);
        bouncer.play(anim5).before(anim3);
        bouncer.start();


        /**Initialization*/
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        context = getApplicationContext();

        /** Check for Auto login*/
        sharedPref = context.getSharedPreferences("data",context.MODE_PRIVATE);
        boolean checked = sharedPref.getBoolean("RM",false);
        if(checked){
            if (mAuth.getCurrentUser() != null) {

                Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                setUID(mAuth.getCurrentUser().getUid());
                startActivity(intent);

                finish();
            }
        }

    }


    public static void setUID(String value) {
        sharedPref.edit().putString("UID", value ).commit();
    }
    public static String getUID() {
        return sharedPref.getString("UID","");
    }

    public static void setCurrentUserName(String value) {
        sharedPref.edit().putString("NAME", value ).commit();
    }
    public static String getCurrentUserName() {
        return MainActivity.sharedPref.getString("NAME","");
    }
    public static void setName(String value) {
        MainActivity.sharedPref.edit().putString("UID", value ).commit();
    }



    /**
     * Validate user credential
     * @param view
     */
    public void onLoginPressed(View view) {
        loading.setVisibility(View.VISIBLE);
        if (username.length() != 0 && password.length() != 0) {
            if (Utils.validateEmail(username.getText().toString())) {
                mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    sharedPref = context.getSharedPreferences("data",context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("RM",isChecked);
                                    editor.putString("UID",user.getUid());
                                    editor.putString("NAME",user.getDisplayName());

                                    editor.commit();
//                                    Log.d(TAG, "whyyy+: " + user.getDisplayName());
                                    setUID(user.getUid());
//                                    setCurrentUserName(user.getDisplayName());
                                    setCurrentUserName("atat");
                                    Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid login", duration);
                                    toast.show();
                                    loading.setVisibility(View.INVISIBLE);



                                }
                            }
                        });
            } else {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, "Invalid email", duration);
                toast.show();
                loading.setVisibility(View.INVISIBLE);
            }

        } else {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, "Empty fields detected", duration);
            toast.show();
            loading.setVisibility(View.INVISIBLE);

        }

//        Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
//                                    startActivity(intent);

    }

    /**
     * Intent to Registration activity
     * @param v
     */
    public void onRegisterPressed(View v){
        Intent intent = new Intent(getApplicationContext(), RegisterationActivity.class);
        startActivity(intent);
    }

    /**
     * save checkbox state
     * @param v
     */
    public void onRememberMePressed(View v){
        CheckBox checkBox =(CheckBox)v;
        if(checkBox.isChecked())
            isChecked = true;
        else
            isChecked = false;

    }

    /**
     * Intent to forgetpassword activity
     * @param v
     */
    public void onForgetPasswordPressed(View v){
        Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
        startActivity(intent);
    }


}