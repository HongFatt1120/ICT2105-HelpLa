package edu.singaporetech.helpla.eventView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseDatabaseHelper;
import edu.singaporetech.helpla.firebaseHelper.FirebaseStorageHelper;
import edu.singaporetech.helpla.firebaseHelper.OnTaskCompletedListener;
import edu.singaporetech.helpla.models.Event;
import edu.singaporetech.helpla.models.User;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ItemViewHolder> {

    private static final String TAG = "EventAdapter";
    /**Declaration*/
    ArrayList<String> eventLists;
    private LayoutInflater mInflater;

    public EventAdapter(Context context, ArrayList<String> eventLists) {
        mInflater = LayoutInflater.from(context);
        this.eventLists = eventLists;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mItemView = mInflater.inflate(R.layout.score_board, parent, false);
        return new ItemViewHolder(mItemView,this);
    }
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override public int getItemCount() {
        return eventLists.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements FirebaseStorageHelper.OnDownloadImage , OnTaskCompletedListener {

        public TextView txt_item_text;
        public ImageView profile_img;

        final EventAdapter adapter;


        private FirebaseStorageHelper storageHelper;
        private FirebaseDatabaseHelper databaseHelper;
        public ItemViewHolder(@NonNull View itemView, EventAdapter adapter) {
            super(itemView);
            txt_item_text = itemView.findViewById(R.id.txt_item_text);
            profile_img = itemView.findViewById(R.id.profile_img);
            this.adapter = adapter;
            storageHelper = new FirebaseStorageHelper(this);
            databaseHelper = new FirebaseDatabaseHelper(this);
        }

        void bind( int position) {
            databaseHelper.requestAllData("Users","uid",eventLists.get(position));
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
            final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
            for(Object x : data){
                User item = mapper.convertValue(x, User.class);
                txt_item_text.setText(item.getName());

            }
        }
    }

}