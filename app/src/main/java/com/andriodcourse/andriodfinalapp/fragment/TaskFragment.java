package com.andriodcourse.andriodfinalapp.fragment;

import android.app.AlertDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.andriodcourse.andriodfinalapp.model.CharacterModel;
import com.andriodcourse.andriodfinalapp.model.Task;
import com.andriodcourse.andriodfinalapp.util.UpgradeChoiceManager;

import java.util.List;

/**
 * 任务界面 Fragment，按类型分组展示任务，支持添加、完成等操作
 */
public class TaskFragment extends Fragment {
    private LinearLayout layoutTaskContainer;
    private TaskDAO taskDAO;
    private CharacterDAO characterDAO;
    private UpgradeChoiceManager upgradeChoiceManager;
    private int userId;

    private static final int[] TASK_TYPES = {1, 2, 3};
    private static final String[] TASK_TYPE_NAMES = {"日常任务", "阶段任务", "最终任务"};
    private static final int[] TASK_TYPE_EXP = {1, 10, 100};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        layoutTaskContainer = view.findViewById(R.id.layout_task_container);
        taskDAO = new TaskDAO(getContext());
        characterDAO = new CharacterDAO(getContext());
        upgradeChoiceManager = new UpgradeChoiceManager(getContext());

        // 从 SharedPreferences 中读取正确的 userId
        SharedPreferences sp = requireContext()
                .getSharedPreferences("config", Context.MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        renderAllTasks(); // 加载并显示任务列表
        return view;
    }

    private void renderAllTasks() {
        layoutTaskContainer.removeAllViews();
        List<Task> allTasks = taskDAO.getAll(userId);
        for (int i = 0; i < TASK_TYPES.length; i++) {
            int type = TASK_TYPES[i];
            String typeName = TASK_TYPE_NAMES[i];
            int typeExp = TASK_TYPE_EXP[i];

            TextView tvTypeTitle = new TextView(getContext());
            tvTypeTitle.setText(typeName);
            tvTypeTitle.setTextSize(20f);
            tvTypeTitle.setPadding(0, 30, 0, 10);
            layoutTaskContainer.addView(tvTypeTitle);

            for (Task task : allTasks) {
                if (task.getType() == type && !task.isCompleted()) {
                    View itemView = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_task, layoutTaskContainer, false);
                    TextView tvTitle = itemView.findViewById(R.id.tv_task_title);
                    ImageButton btnComplete = itemView.findViewById(R.id.btn_task_complete);
                    tvTitle.setText(task.getTitle());
                    btnComplete.setOnClickListener(v -> {
                        completeTask(task);
                    });
                    layoutTaskContainer.addView(itemView);
                }
            }

            View addView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_add_task, layoutTaskContainer, false);
            View btnAdd = addView.findViewById(R.id.btn_add_task);
            btnAdd.setOnClickListener(v -> showAddTaskDialog(type));
            layoutTaskContainer.addView(addView);
        }
    }

    /**
     * 完成任务（修复版本）
     * @param task 要完成的任务
     */
    private void completeTask(Task task) {
        // 标记任务完成
        taskDAO.complete(task.getId());
        
        // 直接使用简化版方法添加经验（更可靠）
        boolean expAdded = characterDAO.addExpSimple(userId, task.getExp());
        
        if (expAdded) {
            // 获取最新的角色信息检查升级
            CharacterModel character = characterDAO.getCharacter(userId);
            if (character != null) {
                // 检查是否需要升级选择（简化逻辑）
                int currentLevel = character.getLevel();
                int milestone = (currentLevel / 5) * 5;
                
                if (milestone >= 5 && character.getLastUpgradeChoiceLevel() < milestone) {
                    // 需要显示升级选择对话框
                    upgradeChoiceManager.showUpgradeChoiceDialog(userId, currentLevel, 
                            new UpgradeChoiceManager.OnUpgradeCompleteListener() {
                        @Override
                        public void onUpgradeComplete(int levelsGained, boolean success) {
                            // 刷新界面
                            renderAllTasks();
                            
                            // 显示消息
                            String message = "获得经验: " + task.getExp();
                            if (success && levelsGained > 0) {
                                message += "\n🎉 额外升级 " + levelsGained + " 级！";
                            }
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // 不需要升级选择，直接显示消息
                    Toast.makeText(getContext(), "获得经验: " + task.getExp(), Toast.LENGTH_SHORT).show();
                    renderAllTasks();
                }
            } else {
                Toast.makeText(getContext(), "获得经验: " + task.getExp(), Toast.LENGTH_SHORT).show();
                renderAllTasks();
            }
        } else {
            Toast.makeText(getContext(), "经验添加失败，请重试", Toast.LENGTH_SHORT).show();
            renderAllTasks();
        }
    }

    private void showAddTaskDialog(int type) {
        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);
        builder.setView(view);

        EditText editTaskTitle = view.findViewById(R.id.et_task_title);
        builder.setTitle("添加" + TASK_TYPE_NAMES[type - 1]);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String title = editTaskTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                taskDAO.add(userId, title, type);
                renderAllTasks();
                Toast.makeText(getContext(), "任务添加成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "请输入任务标题", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
