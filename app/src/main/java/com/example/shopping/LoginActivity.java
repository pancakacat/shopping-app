package com.example.shopping;

import android.arch.persistence.room.Room;
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
    public static LoginActivity instance = null;

//    private SharedPreferences preferences;
//    private SharedPreferences.Editor editor;
    public AppDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        cancel = findViewById(R.id.cancel);
        register = findViewById(R.id.register);

        inputName = findViewById(R.id.input_name);
        inputPassword = findViewById(R.id.input_pw);

//        db = Room.databaseBuilder(this, AppDatabase.class,
//                "itemDatabase").build();
        db = ScrollingActivity.instance.db;

//        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
//        editor = preferences.edit();

//        editor.putString("admin", "123");
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        final String name = inputName.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
//        UserInfo userInfo_ = db.userInfoDao().findByName(name);
        switch (v.getId()) {

            case R.id.login:
                Thread verify = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo userInfo_ = db.userInfoDao().findByName(name);
                        if (userInfo_ != null && password.equals(userInfo_.password)) {
//                            Toast.makeText(LoginActivity.instance, "Logged In", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(LoginActivity.instance, ScrollingActivity.class);
//                            intent.putExtra("signed", true);
//                            startActivity(intent);
                        } else {
//                            Toast.makeText(LoginActivity.instance, "Login Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                verify.start();
                break;

            case R.id.register:
                Thread register = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo userInfo_ = db.userInfoDao().findByName(name);
                        if (userInfo_ != null) {
//                            Toast.makeText(LoginActivity.instance, "Account already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            final UserInfo tmp = new UserInfo();
                            tmp.username = name;
                            tmp.password = password;
                            db.userInfoDao().insertAll(tmp);

//                            Toast.makeText(LoginActivity.instance, "Register success", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LoginActivity.this, ScrollingActivity.class);
//                            intent.putExtra("signedIn",true);
//                            startActivity(intent);
//                            finish();
                        }
                    }
                });
                register.start();
                break;

            case R.id.cancel:
                finish();
        }
    }
}