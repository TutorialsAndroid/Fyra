package com.app.fyra.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.app.fyra.R;
import com.app.fyra.fragment.ChatFragment;
import com.app.fyra.fragment.HomeFragment;
import com.app.fyra.utility.Utils;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (Utils.isTablet(this)) {
            loadFragmentChatList(new HomeFragment());
            loadFragmentChat(new ChatFragment());
        } else {
            loadFragmentChat(new ChatFragment());
        }
    }

    private void loadFragmentChat(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.chatViewFragment, fragment).commit();
    }

    private void loadFragmentChatList(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.chatListFragment, fragment).commit();
    }
}