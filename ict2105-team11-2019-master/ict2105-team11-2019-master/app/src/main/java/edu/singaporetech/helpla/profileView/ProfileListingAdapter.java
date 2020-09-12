package edu.singaporetech.helpla.profileView;

import android.content.Context;
import 	androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.homeView.HomeAdapter;
import edu.singaporetech.helpla.models.Post;

public class ProfileListingAdapter extends RecyclerView.Adapter<ProfileListingAdapter.ViewHolder> {
    private static final String TAG = "ProfileListingAdapter";
    private Context mContext;
    private List<Post> mPost;

    ProfileListingAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ProfileListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder()");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_listing, parent, false);
        ProfileListingAdapter.ViewHolder holder = new ProfileListingAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileListingAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder()");
//        holder.lostItemImg.setImageResource(Integer.valueOf((mPost.get(position).getImage_url())));
        Picasso.get().load(mPost.get(position).getImage_url()).into(holder.lostItemImg);

        holder.lostItemTitle.setText(mPost.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView lostItemImg = itemView.findViewById(R.id.lostItemImg);
        TextView lostItemTitle = itemView.findViewById(R.id.lostItemTitle);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }
}
