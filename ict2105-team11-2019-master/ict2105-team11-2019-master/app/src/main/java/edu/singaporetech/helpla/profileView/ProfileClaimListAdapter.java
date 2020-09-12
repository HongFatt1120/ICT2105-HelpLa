package edu.singaporetech.helpla.profileView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.chat.DiscussionActivity;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.User;

public class ProfileClaimListAdapter extends RecyclerView.Adapter<ProfileClaimListAdapter.ItemViewHolder> {

    private static final String TAG = "ProfileClaimListAdapter";
    private LayoutInflater mInflater;
    ArrayList<String[]> pointsMap;
    Object[] keys;
    ArrayList<String> values;

    String uid_of_other_party;

    public ProfileClaimListAdapter(Context context, ArrayList<String[]> pointsMap) {
        try {
            mInflater = LayoutInflater.from(context);
            this.pointsMap = pointsMap;
        } catch (Exception e) {
            Log.d(TAG, "ProfileClaimListAdapterError: " + e);
        }

    }

    @NonNull
    @Override
    public ProfileClaimListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.claim_list_content, parent, false);

        return new ProfileClaimListAdapter.ItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileClaimListAdapter.ItemViewHolder holder, final int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.claimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), DiscussionActivity.class);
                TextView username = holder.itemView.findViewById(R.id.textViewName);
                intent.putExtra("chat_name", username.getText().toString());
                intent.putExtra("status", "normal");
                Log.d("NS",pointsMap.get(position)[0]);
                intent.putExtra("uid_of_other_party", pointsMap.get(position)[0]);

//                holder.itemView.getContext().startActivity(intent);
                holder.itemView.getContext().startActivity(intent);


                /**
                 * Then update the other party's status to "request"
                 **/


            }
        });


    }

    @Override
    public int getItemCount() {
        return pointsMap.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements OnTaskCompletedListener {

        public ImageView profile_img;
        public TextView name;
        public TextView points;
        public Button claimBtn;
        final ProfileClaimListAdapter adapter;
        FirebaseDatabaseHelper databaseHelper;

        public ItemViewHolder(@NonNull View itemView, ProfileClaimListAdapter adapter) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.imageViewProfile);
            name = itemView.findViewById(R.id.textViewName);
            points = itemView.findViewById(R.id.textViewPoints);
            claimBtn = itemView.findViewById(R.id.buttonClaim);
            databaseHelper = new FirebaseDatabaseHelper(this);
            this.adapter = adapter;
        }

        void bind(final int position) {
            points.setText(pointsMap.get(position)[1]);
            databaseHelper.requestAllData("Users", "uid", pointsMap.get(position)[0]);

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
            for (Object x : data) {
                User u = mapper.convertValue(x, User.class);
                name.setText(u.getName());
                uid_of_other_party = u.getUid();
            }
        }
    }

}
