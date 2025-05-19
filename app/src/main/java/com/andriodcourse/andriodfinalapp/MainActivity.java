package com.andriodcourse.andriodfinalapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.andriodcourse.andriodfinalapp.fragment.CharacterFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载上面新建的 activity_main.xml
        setContentView(R.layout.activity_main);

        // 只有在第一次创建时才添加 Fragment，防止切屏或重建时重复叠加
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CharacterFragment())
                    .commit();
        }
    }
}