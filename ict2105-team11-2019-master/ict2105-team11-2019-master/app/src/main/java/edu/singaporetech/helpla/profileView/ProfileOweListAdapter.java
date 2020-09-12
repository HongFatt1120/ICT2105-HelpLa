package edu.singaporetech.helpla.profileView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.MainActivity;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.chat.DiscussionActivity;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.FirebaseStorageHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Points;
import edu.singaporetech.helpla.models.User;

public class ProfileOweListAdapter extends RecyclerView.Adapter<ProfileOweListAdapter.ItemViewHolder> {


    private LayoutInflater mInflater;
    private static final String TAG = "IOWEADAPTER ";
    ArrayList<String[]> pointsMap;
    Object[] keys;
    ArrayList<String> values;

    public ProfileOweListAdapter(Context context, ArrayList<String[]> pointsMap) {
        try {
            mInflater = LayoutInflater.from(context);
            this.pointsMap = pointsMap;
        } catch (Exception e) {
            Log.d(TAG, "ProfileOweListAdapter: " + e);
        }

    }

    @NonNull
    @Override
    public ProfileOweListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.owe_list_content, parent, false);

        return new ProfileOweListAdapter.ItemViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileOweListAdapter.ItemViewHolder holder, final int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
//        holder.claimBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(),
//                        keys[position] + " owe you " +
//                                values.get(0),
//                        Toast.LENGTH_SHORT).show();
//
//                final String userid = keys[position].toString();
//                Intent intent = new Intent(holder.itemView.getContext(), DiscussionActivity.class);
//                intent.putExtra("SESSION_USERID_OFCHATPARTY", userid);
//                TextView username = (TextView) holder.itemView.findViewById(R.id.textViewName);
//                intent.putExtra("SESSION_USERNAME", username.getText());
//                intent.putExtra("SESSION_CHATNAME", username.getText());
//                intent.putExtra("SESSION_USERNAME_OFCHATPARTY", username.getText());
//
//                intent.putExtra("user_name", username.getText());
//                intent.putExtra("buttonAccept", "visible");
//                intent.putExtra("buttonReject", "visible");
//                intent.putExtra("SESSION_ACTIVITY", "ProfileClaimListAdapter");
//                holder.itemView.getContext().startActivity(intent);
//
//                //at the same time, change the id_status of the user to "reject"
//                final Query q = FirebaseDatabase.getInstance().getReference().child("Points");
//                q.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
//                            if (MainActivity.getUID().equals(itemSnapshot.getKey())) {
//                                DataSnapshot contentSnapshot = itemSnapshot.child("/claim");
//                                Iterable<DataSnapshot> matchSnapShot = contentSnapshot.getChildren();
//                                for (DataSnapshot match : matchSnapShot){
//                                    Points p = match.getValue(Points.class);
//                                    Log.d(TAG, "qwe^ " + p.getId());
//                                    // must check if id equals, if not it will loop thru all the ids and deduct their pts
//                                    if(p.getId().equals(userid)){
//                                        uid_ToChangeStatusToReject = p.getId();
//                                        // path e.g. Points/uyVfBg9IjTQA0lIslsBLlJLUQLh1/claim/0
//                                        String path = "Points/" + itemSnapshot.getKey() +
//                                                "/" + contentSnapshot.getKey() +
//                                                "/" + match.getKey();
//                                        HashMap<String, Object> obj = new HashMap<>();
//                                        obj.put("id_status", "reject");
//                                        dbHelper = new FirebaseDatabaseHelper(lstr);
//                                        dbHelper.updateData(path,obj);
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//        });




//    public ProfileClaimListAdapter(Context context, Map<String, Points.claim> profileImageList) {
        //mInflater = LayoutInflater.from(context);
        // this.profileImageList = profileImageList;
//    }

    }

    @Override
    public int getItemCount() {
        return pointsMap.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements FirebaseStorageHelper.OnDownloadImage, OnTaskCompletedListener {

        public ImageView profile_img;
        public TextView name;
        public TextView points;
        public Button claimBtn;
        final ProfileOweListAdapter adapter;
        FirebaseDatabaseHelper databaseHelper;
        FirebaseStorageHelper storageHelper;

        public ItemViewHolder(@NonNull View itemView, ProfileOweListAdapter adapter) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.imageViewProfile);
            name = itemView.findViewById(R.id.textViewName);
            points = itemView.findViewById(R.id.textViewPoints);
            claimBtn = itemView.findViewById(R.id.buttonClaim);
            databaseHelper = new FirebaseDatabaseHelper(this);
            storageHelper = new FirebaseStorageHelper(this);
            this.adapter = adapter;
        }

        void bind(final int position) {
            points.setText(pointsMap.get(position)[1]);
            databaseHelper.requestAllData("Users","uid",pointsMap.get(position)[0]);


//            Log.d("HELLO",pointsMap.get(0)[0]);
//            points.setText(values.get(0)); // set points first, from Points collection

        }

        @Override
        public void onCompleted(Uri uri) {

            Picasso.get().load(uri).into(profile_img);
        }

        @Override
        public void onTaskStart() {

        }

        @Override
        public void onKeyCompleted(String key) {

        }

        @Override
        public void onTaskCompleteMap(Map data) {

        }

        @Override
        public void onTaskComplete(ArrayList data) {
            final ObjectMapper mapper = new ObjectMapper();
            for(Object x:data){
                User u = mapper.convertValue(x,User.class);
                name.setText(u.getName());
                storageHelper.getImage(u.getImage_url());
            }
        }
    }

}
