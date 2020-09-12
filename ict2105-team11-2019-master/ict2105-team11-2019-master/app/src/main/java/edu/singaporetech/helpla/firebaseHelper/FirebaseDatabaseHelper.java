package edu.singaporetech.helpla.firebaseHelper;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

import androidx.annotation.NonNull;
import edu.singaporetech.helpla.MainActivity;
import edu.singaporetech.helpla.models.Points;

import static androidx.constraintlayout.widget.Constraints.TAG;


import androidx.annotation.Nullable;

public class FirebaseDatabaseHelper<T> {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private OnTaskCompletedListener iListener;
    private String key;

    final ArrayList<Object> objList = new ArrayList<>();
    final Map<String, String> userChatMap = new HashMap<>();

    final Map<String, ArrayList<String>> claimsMap = new HashMap<>();
    final Map<String, ArrayList<String>> oweMap = new HashMap<>();

    private ValueEventListener insertData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            objList.clear();
            for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {
                objList.add(itemSnapShot.getValue());
                Log.d("FirebaseDB",itemSnapShot.getChildren().toString());
            }

            iListener.onTaskComplete(objList);

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    public FirebaseDatabaseHelper(@Nullable OnTaskCompletedListener lstr) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        this.iListener = lstr;
        this.key = "";

    }

    public String getName() {
        return MainActivity.sharedPref.getString("UID", "");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public void requestAllData(String collection, String oberby) {

        Query query;

        iListener.onTaskStart();
        if (oberby.equals("")) {
            query = FirebaseDatabase.getInstance().getReference().child(collection);
        } else {
            query = FirebaseDatabase.getInstance().getReference().child(collection).orderByChild(oberby);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {
                    objList.add(itemSnapShot.getValue());
                }

                iListener.onTaskComplete(objList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private DatabaseReference getRef(String ref) {
        myRef = database.getReference(ref);
        return myRef;
    }

    public void addKey(String ref){

        this.key = getRef(ref).push().getKey();
    }
    public void addData(String ref, T obj) {

        getRef(ref).setValue(obj);
    }

    public void updateData(String ref, HashMap<String, Object> obj) {
        getRef(ref).updateChildren(obj);
    }

    public void deleteData(String ref, String child) {
        getRef(ref).child(child).removeValue();
    }


    public void requestAllData(String collection, String oberby, String keyword) {

        Query query;

        if (oberby.equals("")){
            query = FirebaseDatabase.getInstance().getReference().child(collection).equalTo(keyword);

        } else {
            query = FirebaseDatabase.getInstance().getReference().child(collection).orderByChild(oberby).equalTo(keyword);
        }


        query.addValueEventListener(insertData);
        //myRef.child(collection).removeEventListener(insertData);
    }
    public void requestUserChatData(final String collection) {
        iListener.onTaskStart();
        Query query;
        query = FirebaseDatabase.getInstance().getReference().child(collection);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
//                    Log.d(TAG, "item: " + itemSnapshot.getKey());

                    // loop data, check if userid equals looped data's user id
                    // if yes then run the remaining code
                    if (itemSnapshot.getKey().equals(getName())) {
                        DataSnapshot contentSnapshot = itemSnapshot.child("/chat");
//                    Log.d(TAG, "qq" + contentSnapshot.getValue());
                        for (DataSnapshot match : contentSnapshot.getChildren()) {
                            userChatMap.put(match.getKey(), match.getValue().toString());
                        }

                        Log.d(TAG, "qq" + userChatMap);

                    } else {
//                        Log.d(TAG, "user has no claim data");
                    }
                }
                iListener.onTaskCompleteMap(userChatMap);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ABABABA", "" + databaseError.getMessage());
            }
        });
    }


    public void addPointsData(Points points, String uid) {
        String uuid = UUID.randomUUID().toString();
        this.myRef.child("Points/" + uid + "/claim/" + uuid + "/").setValue(points);
    }

    public void requestClaimData(final String collection) {
        Query query;
        query = FirebaseDatabase.getInstance().getReference().child(collection);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {

                    // loop data, check if userid equals looped data's user id
                    // if yes then run the remaining code
                    if (itemSnapshot.getKey().equals(MainActivity.getUID())) {
                        DataSnapshot contentSnapshot = itemSnapshot.child("/claim");
                        Iterable<DataSnapshot> matchSnapShot = contentSnapshot.getChildren();
                        for (DataSnapshot match : matchSnapShot) {

                            Points c = match.getValue(Points.class);
                            final ArrayList<String> list = new ArrayList<>();
                            //list.add(String.valueOf(c.getPoints()));
                            //  list.add(c.getImage_url());
                            claimsMap.put(c.getId(), list);
                        }
                    } else {
                        Log.d(TAG, "user has no claim data");
                    }
                }
                //below code is very impt, u need to tie the hashmap to ur listener
                iListener.onTaskCompleteMap(claimsMap);
                Log.d(TAG, "CMAP: " + claimsMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ABABABA", "" + databaseError.getMessage());
            }
        });

    }


    public void requestOweData(String collection) {
        Query query;
        query = FirebaseDatabase.getInstance().getReference().child(collection);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    if (itemSnapshot.getKey().equals(MainActivity.getUID())) {
                        DataSnapshot contentSnapshot = itemSnapshot.child("/owe");
                        Iterable<DataSnapshot> matchSnapShot = contentSnapshot.getChildren();
                        for (DataSnapshot match : matchSnapShot) {
                            Points c = match.getValue(Points.class);
                            final ArrayList<String> list = new ArrayList<>();
                            //   list.add(String.valueOf(c.getPoints()));
//                            list.add(c.getImage_url());
                            oweMap.put(c.getId(), list);
                        }
                    } else {
                        Log.d(TAG, "user has no claim data");
                    }
                }
                //below code is very impt, u need to tie the hashmap to ur listener
                iListener.onTaskCompleteMap(oweMap);
//                Log.d(TAG, "oweMap: " + oweMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ABABABA", "" + databaseError.getMessage());
            }
        });


    }

    public void searchKey(String ref,String orderBy,String searchkey) {
        getRef(ref).orderByChild(orderBy)
                .equalTo(searchkey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            iListener.onKeyCompleted(childSnapshot.getKey());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Log.d("DBHelper","")

                    }
                });
    }

    public void updatePoint(String ref){
        final String ref1 = ref;
        getRef(ref).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Long i;
                if(mutableData.getValue() == null)
                    i = 1l;
                else
                {
                    i = (Long) mutableData.getValue() + 1;
                }
                mutableData.setValue(i);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }


    public void reducePoint(String ref){
        final String ref1 = ref;
        getRef(ref).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Long i;
                if(mutableData.getValue() == null)
                    i = 1l;
                else
                {
                    i = (Long) mutableData.getValue() - 1;
                }
                mutableData.setValue(i);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void requestUsersData(String collection, String oberby) {

        Query query;
        if (oberby.equals("")) {
            query = FirebaseDatabase.getInstance().getReference().child(collection);
        } else {
            query = FirebaseDatabase.getInstance().getReference().child(collection).orderByChild(oberby);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {
                    objList.add(itemSnapShot.getValue());
                }

                iListener.onTaskComplete(objList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}