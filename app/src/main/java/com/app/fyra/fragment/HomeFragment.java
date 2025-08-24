package com.app.fyra.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fyra.R;
import com.app.fyra.activity.ChatActivity;
import com.app.fyra.adapter.UsersAdapter;
import com.app.fyra.model.AppUser;
import com.app.fyra.model.AppUser2;
import com.app.fyra.model.UserSession;
import com.app.fyra.utility.Constants;
import com.app.fyra.utility.Utils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final List<AppUser2> userList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email;
    private int id;
    private String photo;
    private Context context;
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private ListenerRegistration usersListener;
    private AppUser user;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (usersListener != null) {
            usersListener.remove();
        }
        recyclerView = null;
        adapter = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = requireContext();

        user = UserSession.getInstance().getUser();
        if (user != null) {
            email = user.getEmail();
            id = user.getId();
            photo = user.getProfilePhoto();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        if (Utils.isTablet(context)) {
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
            layoutManager.setJustifyContent(JustifyContent.CENTER); // centers items
            layoutManager.setFlexDirection(FlexDirection.ROW);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new UsersAdapter(context, userList, user2 -> {
            // Open chat when user is clicked
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("id", user2.getId());
            intent.putExtra("email", user2.getEmail());
            intent.putExtra("profilePhoto", user2.getProfilePhoto());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        fetchAllUsers();
    }

    private void fetchAllUsers() {
        usersListener = db.collection(Constants.USERS)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        userList.clear();
                        for (DocumentSnapshot doc : snapshots) {
                            //TODO app gets crash when data is changed
                            AppUser2 user2 = doc.toObject(AppUser2.class);
                            // Don’t show the current logged-in user
                            if (!user2.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                userList.add(user2);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
