package com.app.fyra.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.fyra.LoadingDialogFragment;
import com.app.fyra.R;
import com.app.fyra.model.AppUser;
import com.app.fyra.model.UserSession;
import com.app.fyra.utility.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import nl.dionsegijn.konfetti.xml.image.ImageUtil;

public class WelcomeScreen extends AppCompatActivity {

    private KonfettiView konfettiView = null;
    private Shape.DrawableShape drawableShape = null;

    private ImageView profilePhotoImageView;
    private TextView idTextView, emailTextView;
    private MaterialButton continueBtn = null;

    private FirebaseFirestore db;

    private LoadingDialogFragment loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_welcome);

        initializeView();
    }

    private void initializeView() {
        konfettiView = findViewById(R.id.konfettiView);
        profilePhotoImageView = findViewById(R.id.welcome_screen_user_photo);
        idTextView = findViewById(R.id.welcome_screen_user_id);
        emailTextView = findViewById(R.id.welcome_screen_user_email);
        continueBtn = findViewById(R.id.welcome_screen_continue_btn);
        continueBtn.setOnClickListener(view -> {
            startActivity(new Intent(WelcomeScreen.this, HomeActivity.class));
            finish();
        });

        showLoadingDialog();
        fetchUserData();
    }

    private void showParticlesAnimation() {
        final Drawable drawable =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_thumb_up_24);
        if (drawable != null) {
            drawableShape = ImageUtil.loadDrawable(drawable, true, true);
        }

        EmitterConfig emitterConfig = new Emitter(50L, TimeUnit.SECONDS).perSecond(50);
        Party party =
                new PartyFactory(emitterConfig)
                        .angle(270)
                        .spread(90)
                        .setSpeedBetween(1f, 5f)
                        .timeToLive(2000L)
                        .shapes(new Shape.Rectangle(0.2f), drawableShape)
                        .sizes(new Size(12, 5f, 0.2f))
                        .position(0.0, 0.0, 1.0, 0.0)
                        .build();
        konfettiView.start(party);
    }

    private void fetchUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e("FIRESTORE", "No user is currently logged in.");
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();

        db.collection(Constants.USERS)
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            int id = document.getLong("id").intValue();
                            String email = document.getString("email");
                            String profilePhoto = document.getString("profilePhoto");

                            AppUser user = new AppUser(id, email, profilePhoto);
                            UserSession.getInstance().setUser(user); // ✅ Global session

                            //Update the UI
                            idTextView.setText(getString(R.string.user_id, id));
                            emailTextView.setText(email);
                            Glide.with(profilePhotoImageView)
                                    .load(profilePhoto)
                                    .listener(new RequestListener<>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                            dismissLoadingDialog();
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                            dismissLoadingDialog();
                                            showParticlesAnimation();
                                            return false;
                                        }
                                    })
                                    .error(R.drawable.ic_user)
                                    .placeholder(R.drawable.ic_user)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true).into(profilePhotoImageView);
                        } else {
                            Log.e("FIRESTORE", "No user found with email: " + userEmail);

                            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(WelcomeScreen.this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        Log.e("FIRESTORE", "Error fetching user", task.getException());
                        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoadingDialog() {
        loadingDialog = new LoadingDialogFragment();
        loadingDialog.show(getSupportFragmentManager(), "loading");
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
