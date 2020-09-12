package edu.singaporetech.helpla.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;

import java.util.Random;

import edu.singaporetech.helpla.models.User;

public class Utils {

    private Utils() {
        throw new AssertionError();
    }

    public static boolean validateEmail(String email){

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(email.length() != 0){
            if(email.matches(emailPattern)){
                return true;
            }
        }
        return false;
    }

    public static String getFileExtension(Uri uri, Context context){
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public static String genNum(){
        Random r = new Random();
        return String.format("%04d", r.nextInt(10000));

    }

    public static User getUserObj(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences("userObj",context.MODE_PRIVATE);
        String sharedPref1 =  sharedPref.getString("userObject","");
        User user= gson.fromJson(sharedPref1, User.class);
        return user;

    }

    public static String getUID(Context context) {
        String id = "";
        if(context.getSharedPreferences("data",context.MODE_PRIVATE) != null) {
            SharedPreferences sharedPref = context.getSharedPreferences("data",context.MODE_PRIVATE);
            id = sharedPref.getString("UID","");
        }
        return id;


    }
}
