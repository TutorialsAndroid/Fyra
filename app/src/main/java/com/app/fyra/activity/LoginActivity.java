package com.app.fyra.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fyra.R;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton createAccountBtn;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        createAccountBtn = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();
    }

    private void initializeViews() {
        createAccountBtn =  findViewById(R.id.login_create_account_btn);
        createAccountBtn.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));
    }
}
