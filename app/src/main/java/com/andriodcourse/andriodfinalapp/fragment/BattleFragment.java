// app/src/main/java/com/andriodcourse/andriodfinalapp/fragment/BattleFragment.java
package com.andriodcourse.andriodfinalapp.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.andriodcourse.andriodfinalapp.R;

public class BattleFragment extends Fragment {

    public BattleFragment() {

    }

    public static BattleFragment newInstance() {
        return new BattleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_battle, container, false);
    }
}
