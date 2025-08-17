package com.app.fyra.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fyra.LoadingDialogFragment;
import com.app.fyra.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    private TextInputLayout emailIL, passwordIL;
    private TextInputEditText emailIF, passwordIF;
    private MaterialButton createAccountBtn;

    private TextWatcher emailInputTextWatcher, passwordInputTextWatcher;

    private FirebaseAuth mAuth;

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
        loadingDialog();

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
                        clearInputField();
                        dismissLoadingDialog();
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

    private void loadingDialog() {
        loadingDialog = new LoadingDialogFragment();
        loadingDialog.show(getSupportFragmentManager(), "loading");
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
