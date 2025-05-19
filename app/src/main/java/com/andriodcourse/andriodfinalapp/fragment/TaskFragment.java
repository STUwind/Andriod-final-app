// app/src/main/java/com/andriodcourse/andriodfinalapp/fragment/TaskFragment.java
package com.andriodcourse.andriodfinalapp.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.andriodcourse.andriodfinalapp.R;

public class TaskFragment extends Fragment {

    public TaskFragment() {

    }

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_task, container, false);
    }
}
