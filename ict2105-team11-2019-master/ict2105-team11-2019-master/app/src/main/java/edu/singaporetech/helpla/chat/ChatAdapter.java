package edu.singaporetech.helpla.chat;

import android.content.Context;
import 	androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.singaporetech.helpla.R;
import edu.singaporetech.helpla.firebaseHelper.FirebaseStorageHelper;
import edu.singaporetech.helpla.models.User;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemViewHolder> {



    /**Declaration*/
    ArrayList<User.chat> usersList;
    private LayoutInflater mInflater;
    private static final String TAG = "ChatAdapter";
    private String chat_name;

    public ChatAdapter(Context context, ArrayList<User.chat> usersList) {
        if (context != null) {
            mInflater = LayoutInflater.from(context);
            this.usersList = usersList;
        }

    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mItemView = mInflater.inflate(R.layout.chat_list_content, parent, false);
        return new ItemViewHolder(mItemView,this);
    }
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.bind(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),
                        usersList.get(position).getStatus(),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(holder.itemView.getContext(), DiscussionActivity.class);
                intent.putExtra("chat_name", usersList.get(position).getChat_name());
                intent.putExtra("status", usersList.get(position).getStatus());
                intent.putExtra("name_of_chat_party", usersList.get(position).getChat_name());
                holder.itemView.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override public int getItemCount() {
        return usersList.size();

    }

    class ItemViewHolder extends RecyclerView.ViewHolder   {

        public TextView txt_item_text;
        public ImageView profile_img;

        final ChatAdapter adapter;

        private FirebaseStorageHelper storageHelper;
        public ItemViewHolder(@NonNull View itemView, ChatAdapter adapter) {
            super(itemView);
            txt_item_text = itemView.findViewById(R.id.txt_item_text);
            this.adapter = adapter;
        }

        void bind( int position) {
            String mCurrent = usersList.get(position).getChat_name();

            txt_item_text.setText(mCurrent);

        }

    }

}
