package com.andriodcourse.andriodfinalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andriodcourse.andriodfinalapp.db.CharacterDAO;
import com.andriodcourse.andriodfinalapp.db.UserDAO;
import com.andriodcourse.andriodfinalapp.model.UserModel;
import com.andriodcourse.andriodfinalapp.util.SecurityUtil;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private UserDAO userDao;
    private CharacterDAO characterDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername   = findViewById(R.id.et_username);
        etPassword   = findViewById(R.id.et_password);
        btnLogin     = findViewById(R.id.btn_login);

        userDao      = new UserDAO(this);
        characterDao = new CharacterDAO(this);

        // 调试输出：打印所有用户及密码哈希到 Logcat
        List<UserModel> users = userDao.getAllUsers();
        for (UserModel u : users) {
            Log.d("UserDB", "ID=" + u.getId() +
                    ", 用户名=" + u.getUsername() +
                    ", 密码哈希=" + u.getPasswordHash());
        }

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String plainPwd = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(plainPwd)) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String pwdHash = SecurityUtil.sha256(plainPwd);
        UserModel user = userDao.getByName(username);
        int userId;

        if (user == null) {
            long id = userDao.add(username, pwdHash);
            if (id < 0) {
                Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
                return;
            }
            userId = (int) id;
            characterDao.createDefault(userId, username, R.drawable.avatar_default);
            Toast.makeText(this, "注册成功，已为您创建角色", Toast.LENGTH_SHORT).show();
        } else {
            UserModel loginUser = userDao.login(username, pwdHash);
            if (loginUser == null) {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                return;
            }
            userId = loginUser.getId();
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        sp.edit()
                .putInt("user_id", userId)
                .apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
