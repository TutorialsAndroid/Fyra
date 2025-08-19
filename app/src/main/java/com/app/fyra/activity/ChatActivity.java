package com.app.fyra.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fyra.R;
import com.app.fyra.adapter.MessagesAdapter;
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

public class ChatActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<Message> messageList = new ArrayList<>();
    private int receiverId;
    private int currentUserId;
    private String chatRoomId;
    private String receiverPhoto;
    private MessagesAdapter adapter;
    private TextInputEditText editMessage;
    private RecyclerView recyclerView;
    private ImageView chatUserPhoto;
    private TextView chatUserID;
    private ShapeableImageView buttonSend;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Clear Views to prevent any leaks
        adapter = null;
        editMessage = null;
        recyclerView = null;
        chatUserPhoto = null;
        chatUserID = null;
        buttonSend = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        db.collection("chats").document(chatRoomId).collection("messages").whereEqualTo("receiverId", String.valueOf(currentUserId)).whereIn("status", Arrays.asList("sent", "delivered")).get().addOnSuccessListener(query -> {
            for (DocumentSnapshot doc : query.getDocuments()) {
                doc.getReference().update("status", "seen");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get data from intent
        receiverId = getIntent().getIntExtra("id", -1);
        receiverPhoto = getIntent().getStringExtra("profilePhoto");

        // Current logged-in user (from UserSession)
        currentUserId = UserSession.getInstance().getUser().getId();

        // Chat room
        chatRoomId = getChatRoomId(currentUserId, receiverId);

        initializeViews();
    }

    private void initializeViews() {
        chatUserID = findViewById(R.id.chatUserID);
        chatUserPhoto = findViewById(R.id.chatUserPhoto);
        recyclerView = findViewById(R.id.chatRecyclerView);
        editMessage = findViewById(R.id.editMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Send message
        buttonSend.setOnClickListener(v -> sendMessage());

        fetchData();
    }

    private void fetchData() {
        chatUserID.setText(getString(R.string.user_id, receiverId));
        Glide.with(this).load(receiverPhoto).placeholder(R.drawable.ic_user).circleCrop().into(chatUserPhoto);

        // Recycler setup
        adapter = new MessagesAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Listen for messages
        listenForMessages();
    }

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        Message message = new Message(String.valueOf(currentUserId), String.valueOf(receiverId), text, System.currentTimeMillis(), "sent" // initially sent
        );

        db.collection("chats").document(chatRoomId).collection("messages").add(message).addOnSuccessListener(doc -> {
            editMessage.setText("");
        });
    }

    private void listenForMessages() {
        db.collection("chats").document(chatRoomId).collection("messages").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e("CHAT", "Listen failed.", e);
                return;
            }

            messageList.clear();
            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                Message msg = doc.toObject(Message.class);
                if (msg == null) continue;

                // If current user is the receiver
                if (msg.getReceiverId().equals(String.valueOf(currentUserId))) {
                    String status = msg.getStatus();

                    // If it’s not already seen → mark as seen
                    if (!"seen".equals(status)) {
                        doc.getReference().update("status", "seen");
                    }
                }

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