package com.andriodcourse.andriodfinalapp;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.andriodcourse.andriodfinalapp.fragment.TaskFragment;
import com.andriodcourse.andriodfinalapp.fragment.BattleFragment;
import com.andriodcourse.andriodfinalapp.fragment.CharacterFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);

        // 使用 if–else 代替 switch–case
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment frag = null;
                int id = item.getItemId();
                if (id == R.id.navigation_task) {
                    frag = new TaskFragment();
                } else if (id == R.id.navigation_battle) {
                    frag = new BattleFragment();
                } else if (id == R.id.navigation_character) {
                    frag = new CharacterFragment();
                }
                if (frag != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .commit();
                }
                return true;
            }
        });

        // 默认选中角色页
        bottomNav.setSelectedItemId(R.id.navigation_character);
    }
}
