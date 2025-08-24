package com.app.fyra.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration listener;

    private int currentUserId;
    private int receiverId;
    private String chatRoomId;

    public void init(int currentUserId, int receiverId) {
        if (chatRoomId != null) return; // Already initialized

        this.currentUserId = currentUserId;
        this.receiverId = receiverId;
        this.chatRoomId = getChatRoomId(currentUserId, receiverId);

        listenForMessages();
    }

    public LiveData<List<Message>> getMessages() {
        return messagesLiveData;
    }

    private void listenForMessages() {
        listener = db.collection("chats")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("CHAT", "Listen failed.", e);
                        return;
                    }

                    List<Message> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Message msg = doc.toObject(Message.class);
                        if (msg != null) {
                            // Mark as seen if current user is receiver
                            if (msg.getReceiverId().equals(String.valueOf(currentUserId)) &&
                                    !"seen".equals(msg.getStatus())) {
                                doc.getReference().update("status", "seen");
                            }
                            list.add(msg);
                        }
                    }
                    messagesLiveData.setValue(list);
                });
    }

    public void sendMessage(String text) {
        if (text.trim().isEmpty()) return;

        Message message = new Message(
                String.valueOf(currentUserId),
                String.valueOf(receiverId),
                text,
                System.currentTimeMillis(),
                "sent"
        );

        db.collection("chats")
                .document(chatRoomId)
                .collection("messages")
                .add(message);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listener != null) {
            listener.remove();
        }
    }

    private String getChatRoomId(int user1, int user2) {
        return (user1 < user2) ? user1 + "_" + user2 : user2 + "_" + user1;
    }
}

