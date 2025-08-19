package com.app.fyra.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fyra.R;
import com.app.fyra.model.AppUser;
import com.bumptech.glide.Glide;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<AppUser> userList;
    private Context context;
    private OnUserClickListener listener;

    // Define interface
    public interface OnUserClickListener {
        void onUserClick(AppUser user);
    }

    public UsersAdapter(Context context, List<AppUser> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AppUser user = userList.get(position);

        holder.textID.setText(context.getString(R.string.user_id, user.getId()));

        // Load image with Glide/Picasso
        Glide.with(context)
                .load(user.getProfilePhoto())
                .placeholder(R.drawable.ic_user)
                .circleCrop()
                .into(holder.imageProfile);

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView textID;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textID = itemView.findViewById(R.id.textID);
        }
    }
}