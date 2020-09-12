package edu.singaporetech.helpla.leadershipBoard;

import android.content.Context;
import 	androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseStorageHelper;
import edu.singaporetech.helpla.models.User;

public class LeadershipBoardAdapter extends RecyclerView.Adapter<LeadershipBoardAdapter.ItemViewHolder> {



    /**Declaration*/
            ArrayList<User> usersList;
    private LayoutInflater mInflater;
    private static final String TAG = "ChatAdapter";

    public LeadershipBoardAdapter(Context context, ArrayList<User> usersList) {
        mInflater = LayoutInflater.from(context);
        this.usersList = usersList;
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
        return usersList.size();

    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements FirebaseStorageHelper.OnDownloadImage {

        public TextView txt_item_text;
        public ImageView profile_img;
        public TextView textScore;

        final edu.singaporetech.helpla.leadershipBoard.LeadershipBoardAdapter adapter;

        private FirebaseStorageHelper storageHelper;
        public ItemViewHolder(@NonNull View itemView, edu.singaporetech.helpla.leadershipBoard.LeadershipBoardAdapter adapter) {
            super(itemView);
            txt_item_text = itemView.findViewById(R.id.txt_item_text);
            profile_img = itemView.findViewById(R.id.profile_img);
            textScore = itemView.findViewById(R.id.textViewScore);
            this.adapter = adapter;
            storageHelper = new FirebaseStorageHelper(this);
        }

        void bind( int position) {
            String mCurrent = usersList.get(position).getName();
            String image =  usersList.get(position).getImage_url();

            storageHelper.getImage(image);
            txt_item_text.setText(mCurrent);
            textScore.setText(usersList.get(position).getPoints() + "");
        }

        @Override
        public void onCompleted(Uri uri) {
            Picasso.get().load(uri).into(profile_img);
        }
    }

}
