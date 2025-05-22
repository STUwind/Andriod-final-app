package com.andriodcourse.andriodfinalapp.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.andriodcourse.andriodfinalapp.R;
import com.andriodcourse.andriodfinalapp.db.CharacterDAO;
import com.andriodcourse.andriodfinalapp.db.TaskDAO;
import com.andriodcourse.andriodfinalapp.model.Task;
import java.util.List;

public class TaskFragment extends Fragment {
    private LinearLayout layoutTaskContainer;
    private TaskDAO taskDAO;
    private CharacterDAO characterDAO;
    private int userId;

    private static final int[] TASK_TYPES = {1, 2, 3};
    private static final String[] TASK_TYPE_NAMES = {"日常任务", "阶段任务", "最终任务"};
    private static final int[] TASK_TYPE_EXP = {1, 10, 100};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        layoutTaskContainer = view.findViewById(R.id.layout_task_container);
        taskDAO = new TaskDAO(getContext());
        characterDAO = new CharacterDAO(getContext());

        // 获取传入的 userId
        Bundle args = getArguments();
        userId = (args != null) ? args.getInt("userId", 0) : 0;

        renderAllTasks(); // 加载并显示任务列表
        return view;
    }

    /**
     * 按类型渲染任务：先添加标题，再显示该类型下所有未完成任务，最后添加“+”按钮
     */
    private void renderAllTasks() {
        layoutTaskContainer.removeAllViews();
        List<Task> allTasks = taskDAO.getAll(userId);
        for (int i = 0; i < TASK_TYPES.length; i++) {
            int type = TASK_TYPES[i];
            String typeName = TASK_TYPE_NAMES[i];
            int typeExp = TASK_TYPE_EXP[i];

            // 任务类型标题
            TextView tvTypeTitle = new TextView(getContext());
            tvTypeTitle.setText(typeName);
            tvTypeTitle.setTextSize(20f);
            tvTypeTitle.setPadding(0, 30, 0, 10);
            layoutTaskContainer.addView(tvTypeTitle);

            // 显示所有未完成任务
            for (Task task : allTasks) {
                if (task.getType() == type && !task.isCompleted()) {
                    View itemView = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_task, layoutTaskContainer, false);
                    TextView tvTitle = itemView.findViewById(R.id.tv_task_title);
                    ImageButton btnComplete = itemView.findViewById(R.id.btn_task_complete);
                    tvTitle.setText(task.getTitle());
                    // 完成按钮逻辑：标记完成，增经验，刷新列表
                    btnComplete.setOnClickListener(v -> {
                        taskDAO.complete(task.getId());
                        characterDAO.addExp(userId, task.getExp());
                        renderAllTasks();
                    });
                    layoutTaskContainer.addView(itemView);
                }
            }

            // 添加“+”按钮保持在底部
            View addView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_add_task, layoutTaskContainer, false);
            Button btnAdd = addView.findViewById(R.id.btn_add_task);
            btnAdd.setOnClickListener(v -> showAddTaskDialog(type));
            layoutTaskContainer.addView(addView);
        }
    }

    /**
     * 弹出对话框输入任务名称并添加任务
     */
    private void showAddTaskDialog(int type) {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_task, null);
        EditText etTitle = dialogView.findViewById(R.id.et_task_title);
        new AlertDialog.Builder(getContext())
                .setTitle("新增任务")
                .setView(dialogView)
                .setPositiveButton("添加", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    if (!TextUtils.isEmpty(title)) {
                        taskDAO.add(userId, title, type);
                        renderAllTasks();
                    } else {
                        Toast.makeText(getContext(), "任务名称不能为空", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
