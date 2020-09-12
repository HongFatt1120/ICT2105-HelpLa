package edu.singaporetech.helpla.homeView;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseStorageHelper;
import edu.singaporetech.helpla.models.Post;
import edu.singaporetech.helpla.postView.ViewPostFragment;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemViewHolder> {
    private static final String TAG = "HomeAdapter";
    private LayoutInflater mInflater;
    ArrayList<Post> mPost;
    private String lostItemImg;
    Context mContext;


    public HomeAdapter(Context mContext, ArrayList<Post> mPost) {
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mPost = mPost;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder()");
        View mItemView = mInflater.inflate(R.layout.recyclerview_lostitem, parent, false);
        return new ItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        Post post = mPost.get(position);
        holder.bind(position);

        final Bundle bundle = new Bundle();
        bundle.putSerializable("POST",post);

        holder.lostItemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AppCompatActivity activity = (AppCompatActivity) mContext;
                ViewPostFragment viewPostFragment = new ViewPostFragment();
                viewPostFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.indexfc, viewPostFragment, "viewPostFragment")
                        .addToBackStack(null)
                        .commit();


            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return mPost.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements FirebaseStorageHelper.OnDownloadImage {

        public TextView lostItemTitle;
        public ImageView lostItemImg;
        final HomeAdapter mHomeAdapter;

        FirebaseStorageHelper storageHelper = new FirebaseStorageHelper(this);
        public ItemViewHolder(@NonNull View itemView, HomeAdapter mHomeAdapter) {
            super(itemView);
            lostItemImg = itemView.findViewById(R.id.lostItemImg);
            lostItemTitle = itemView.findViewById(R.id.lostItemTitle);
            this.mHomeAdapter = mHomeAdapter;
        }

        void bind(int position) {
            String mTitle = mPost.get(position).getTitle();
            String mImage = mPost.get(position).getImage_url();
            storageHelper.getImage(mImage);
            lostItemTitle.setText(mTitle);
        }

        @Override
        public void onCompleted(Uri uri) {
            // Picasso.get().load(uri).into(lostItemImg);
            Picasso.get()
                    .load(uri)
                    .fit()
                    .centerCrop()
                    .into(lostItemImg);
        }
    }
}