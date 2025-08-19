package com.app.fyra.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fyra.R;
import com.app.fyra.adapter.MessagesAdapter;
import com.app.fyra.model.Message;
import com.app.fyra.model.UserSession;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private int receiverId;
    private String receiverEmail, receiverPhoto;
    private int currentUserId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private MessagesAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    private EditText editMessage;
    private RecyclerView recyclerView;

    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get data from intent
        receiverId = getIntent().getIntExtra("id", -1);
        receiverEmail = getIntent().getStringExtra("email");
        receiverPhoto = getIntent().getStringExtra("profilePhoto");

        // Current logged-in user (from UserSession)
        currentUserId = UserSession.getInstance().getUser().getId();

        // Chat room
        chatRoomId = getChatRoomId(currentUserId, receiverId);

        // Views
        TextView chatUserEmail = findViewById(R.id.chatUserEmail);
        ImageView chatUserPhoto = findViewById(R.id.chatUserPhoto);
        recyclerView = findViewById(R.id.chatRecyclerView);
        editMessage = findViewById(R.id.editMessage);
        ImageButton buttonSend = findViewById(R.id.buttonSend);

        chatUserEmail.setText(receiverEmail);
        Glide.with(this).load(receiverPhoto).placeholder(R.drawable.ic_user).circleCrop().into(chatUserPhoto);

        // Recycler setup
        adapter = new MessagesAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Send message
        buttonSend.setOnClickListener(v -> sendMessage());

        // Listen for messages
        listenForMessages();
    }

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        Message message = new Message(
                String.valueOf(currentUserId),
                String.valueOf(receiverId),
                text,
                System.currentTimeMillis()
        );

        db.collection("chats")
                .document(chatRoomId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(doc -> {
                    editMessage.setText("");
                });
    }

    private void listenForMessages() {
        db.collection("chats")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("CHAT", "Listen failed.", e);
                        return;
                    }

                    messageList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Message msg = doc.toObject(Message.class);
                        messageList.add(msg);
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                });
    }

    private String getChatRoomId(int user1, int user2) {
        return (user1 < user2) ? user1 + "_" + user2 : user2 + "_" + user1;
    }
}

