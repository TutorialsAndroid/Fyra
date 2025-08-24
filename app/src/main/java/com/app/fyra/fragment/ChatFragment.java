package com.app.fyra.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fyra.R;
import com.app.fyra.adapter.MessagesAdapter;
import com.app.fyra.model.ChatViewModel;
import com.app.fyra.model.Message;
import com.app.fyra.model.UserSession;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatViewModel viewModel;
    private MessagesAdapter adapter;

    private RecyclerView recyclerView;
    private TextInputEditText editMessage;
    private ImageView chatUserPhoto;
    private TextView chatUserID;
    private ShapeableImageView buttonSend;

    private int receiverId;
    private String receiverPhoto;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Get data from intent
        Activity activity = requireActivity();
        receiverId = activity.getIntent().getIntExtra("id", -1);
        receiverPhoto = activity.getIntent().getStringExtra("profilePhoto");
        currentUserId = UserSession.getInstance().getUser().getId();

        initializeViews(view);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.init(currentUserId, receiverId);

        observeMessages();

        return view;
    }

    private void initializeViews(View view) {
        chatUserID = view.findViewById(R.id.chatUserID);
        chatUserPhoto = view.findViewById(R.id.chatUserPhoto);
        recyclerView = view.findViewById(R.id.chatRecyclerView);
        editMessage = view.findViewById(R.id.editMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        chatUserID.setText(getString(R.string.user_id, receiverId));
        Glide.with(this).load(receiverPhoto).placeholder(R.drawable.ic_user).circleCrop().into(chatUserPhoto);

        adapter = new MessagesAdapter(new ArrayList<>(), currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> {
            String text = editMessage.getText().toString().trim();
            viewModel.sendMessage(text);
            editMessage.setText("");
        });
    }

    private void observeMessages() {
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messages.size() - 1);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        editMessage = null;
        chatUserPhoto = null;
        chatUserID = null;
        buttonSend = null;
    }
}

