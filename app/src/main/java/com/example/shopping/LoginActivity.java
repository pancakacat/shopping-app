package com.example.shopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login, cancel,register;
    private EditText inputName, inputPassword;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        cancel = findViewById(R.id.cancel);
        register = findViewById(R.id.register);

        inputName = findViewById(R.id.input_name);
        inputPassword = findViewById(R.id.input_pw);

        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();

        editor.putString("admin", "123");
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String name = inputName.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        switch (v.getId()) {
            case R.id.login:
                if (preferences.contains(name) && password.equals(preferences.getString(name, null))) {

                    Toast.makeText(this, "logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ScrollingActivity.class);
                    intent.putExtra("signed",true);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "login failed", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register:
                if (preferences.contains(name)) {
                    Toast.makeText(this, "Account already exists", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString(name, password);
                    editor.commit();
                    Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ScrollingActivity.class);
                    intent.putExtra("signedIn",true);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.cancel:
                finish();
        }
    }
}