package com.app.fyra.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fyra.LoadingDialogFragment;
import com.app.fyra.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private MaterialButton createAccountBtn, loginAccountBtn;
    private TextInputLayout emailIL, passwordIL;
    private TextInputEditText emailIF, passwordIF;
    private TextWatcher emailInputTextWatcher, passwordInputTextWatcher;
    private LoadingDialogFragment loadingDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        emailIF.removeTextChangedListener(emailInputTextWatcher);
        passwordIF.removeTextChangedListener(passwordInputTextWatcher);

        createAccountBtn = null;
        loginAccountBtn = null;
        emailIF = null;
        passwordIF = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);

        initializeViews();
    }

    private void initializeViews() {
        createAccountBtn = findViewById(R.id.login_create_account_btn);
        loginAccountBtn = findViewById(R.id.login_btn);

        emailIL = findViewById(R.id.login_email_input_layout);
        passwordIL = findViewById(R.id.login_password_input_layout);
        emailIF = findViewById(R.id.login_email_edit_text);
        passwordIF = findViewById(R.id.login_password_edit_text);

        createAccountBtn.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));
        loginAccountBtn.setOnClickListener(view -> signIn());

        emailTextWatcher();
        passwordTextWatcher();
    }

    private void signIn() {
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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("SignIN", "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    clearInputField();
                    dismissLoadingDialog();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w("SignIN", "signInWithEmail:failure", task.getException());
                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
        });
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
}
