package com.app.fyra.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fyra.LoadingDialogFragment;
import com.app.fyra.R;
import com.app.fyra.model.CreateUser;
import com.app.fyra.utility.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    private TextInputLayout emailIL, passwordIL;
    private TextInputEditText emailIF, passwordIF;
    private MaterialButton createAccountBtn;

    private TextWatcher emailInputTextWatcher, passwordInputTextWatcher;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private LoadingDialogFragment loadingDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        emailIF.removeTextChangedListener(emailInputTextWatcher);
        passwordIF.removeTextChangedListener(passwordInputTextWatcher);

        emailIL = null;
        passwordIL = null;
        emailIF = null;
        passwordIF = null;

        createAccountBtn = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_create_account);

        initializeViews();
    }

    private void initializeViews() {
        emailIL = findViewById(R.id.create_acc_email_input_layout);
        passwordIL = findViewById(R.id.create_acc_password_input_layout);
        emailIF = findViewById(R.id.create_acc_email_edit_text);
        passwordIF = findViewById(R.id.create_acc_password_edit_text);

        createAccountBtn = findViewById(R.id.create_acc_create_account_btn);
        createAccountBtn.setOnClickListener(view -> createAccount());

        emailTextWatcher();
        passwordTextWatcher();
    }

    private void emailTextWatcher() {
        emailInputTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 0) {
                    emailInputError(false);
                }
            }
        };
        emailIF.addTextChangedListener(emailInputTextWatcher);
    }

    private void passwordTextWatcher() {
        passwordInputTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 0) {
                    passwordInputError(false);
                }
            }
        };
        passwordIF.addTextChangedListener(passwordInputTextWatcher);
    }

    /**
     * @noinspection DataFlowIssue
     */
    private void createAccount() {
        showLoadingDialog();

        String email = emailIF.getText().toString();
        String password = passwordIF.getText().toString();

        if (email.isBlank()) {
            emailInputError(true);
            dismissLoadingDialog();
            return;
        }

        if (password.isBlank()) {
            passwordInputError(true);
            dismissLoadingDialog();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        createUser(email, password);
                        clearInputField();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        dismissLoadingDialog();
                    }
                });
    }

    private void emailInputError(boolean email) {
        if (email) {
            emailIL.setErrorEnabled(true);
            emailIL.setError("Email Required");
        } else {
            emailIL.setErrorEnabled(false);
            emailIL.setError(null);
        }
    }

    private void passwordInputError(boolean password) {
        if (password) {
            passwordIL.setErrorEnabled(true);
            passwordIL.setError("Password Required");
        } else {
            passwordIL.setErrorEnabled(false);
            passwordIL.setError(null);
        }
    }

    private void clearInputField() {
        emailIF.setText(null);
        passwordIF.setText(null);
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

    private void createUser(String email, String password) {
        generateUniqueId(id -> {
            String profilePhoto = "https://api.dicebear.com/9.x/adventurer/png?seed="+id;
            // Now we are sure id is unique
            CreateUser createUser = new CreateUser(id, email, password, profilePhoto);

            db.collection(Constants.USERS)
                    .document()
                    .set(createUser)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FIRESTORE", "User created with id: " + id);

                        dismissLoadingDialog();

                        startActivity(new Intent(CreateAccountActivity.this, WelcomeScreen.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FIRESTORE", "Error creating user", e);
                    });
        });
    }

    /**
     * Generates a unique 4-digit ID and passes it to the callback.
     */
    private void generateUniqueId(OnIdGeneratedListener listener) {
        Random random = new Random();
        int id = 1000 + random.nextInt(9000);

        // Check if ID already exists in Firestore
        db.collection(Constants.USERS)
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // ID exists → try again recursively
                            generateUniqueId(listener);
                        } else {
                            // ID is unique → return it
                            listener.onIdGenerated(id);
                        }
                    } else {
                        Log.e("FIRESTORE", "Error checking ID", task.getException());
                    }
                });
    }

    /**
     * Simple callback interface for async ID generation
     */
    interface OnIdGeneratedListener {
        void onIdGenerated(int id);
    }
}
