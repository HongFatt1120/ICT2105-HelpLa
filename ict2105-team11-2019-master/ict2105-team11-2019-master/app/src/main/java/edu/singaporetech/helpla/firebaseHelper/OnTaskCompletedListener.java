package edu.singaporetech.helpla.firebaseHelper;

import java.util.ArrayList;
import java.util.Map;

public interface OnTaskCompletedListener<T> {
    void onTaskStart();

    void onTaskComplete(ArrayList<Object> data);

    void onTaskCompleteMap(Map<String, ArrayList<String>> data);
    void onKeyCompleted(String key);
}

