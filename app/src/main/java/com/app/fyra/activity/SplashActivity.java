package com.app.fyra.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fyra.model.AppUser;
import com.app.fyra.model.UserSession;
import com.app.fyra.utility.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.getViewTreeObserver().addOnPreDrawListener(() -> false);
        }

        checkAuthentication();
    }

    private void checkAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            fetchUserData(user.getEmail());
            // User is logged in
        } else {
            // User is not logged in
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void fetchUserData(String userEmail) {
        db.collection(Constants.USERS)
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        int id = document.getLong("id").intValue();
                        String email = document.getString("email");
                        String profilePhoto = document.getString("profilePhoto");

                        AppUser user = new AppUser(id, email, profilePhoto);
                        UserSession.getInstance().setUser(user); // ✅ Global session

                        // Move to HomeActivity
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();

                    } else {
                        // Error or user not found → clear session and go to login
                        UserSession.getInstance().clearSession();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                });
    }
}
