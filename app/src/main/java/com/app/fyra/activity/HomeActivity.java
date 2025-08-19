package com.app.fyra.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fyra.R;
import com.app.fyra.adapter.UsersAdapter;
import com.app.fyra.model.AppUser;
import com.app.fyra.model.UserSession;
import com.app.fyra.utility.Constants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private MaterialButton logoutBtn = null;
    private String email;
    private int id;
    private String photo;

    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private List<AppUser> userList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListenerRegistration usersListener;
    private ListenerRegistration incomingCallReg;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usersListener != null) {
            usersListener.remove();
        }
        if (incomingCallReg != null) incomingCallReg.remove();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        AppUser user = UserSession.getInstance().getUser();
        if (user != null) {
            email = user.getEmail();
            id = user.getId();
            photo = user.getProfilePhoto();
        }

        initializeViews();
    }

    private void initializeViews() {
        MaterialToolbar toolbar = findViewById(R.id.home_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                if (mAuth.getCurrentUser() == null) {
                    // ✅ Sign-out successful
                    Log.d("Auth", "User signed out successfully");
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                } else {
                    // ❌ Sign-out failed (very rare, usually if signOut() wasn't called correctly)
                    Log.d("Auth", "Sign-out failed");
                }

            }
        });
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(this, userList, user -> {
            // Open chat when user is clicked
            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            intent.putExtra("id", user.getId());
            intent.putExtra("email", user.getEmail());
            intent.putExtra("profilePhoto", user.getProfilePhoto());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        fetchAllUsers();
    }

    private void fetchAllUsers() {
        usersListener = db.collection(Constants.USERS)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        userList.clear();
                        for (DocumentSnapshot doc : snapshots) {
                            AppUser user = doc.toObject(AppUser.class);

                            // Don’t show the current logged-in user
                            if (!user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                userList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}