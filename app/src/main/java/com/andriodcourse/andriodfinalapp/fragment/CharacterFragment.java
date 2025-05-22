package com.andriodcourse.andriodfinalapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.andriodcourse.andriodfinalapp.R;
import com.andriodcourse.andriodfinalapp.db.CharacterDAO;
import com.andriodcourse.andriodfinalapp.model.CharacterModel;

/**
 * 角色界面 Fragment，展示当前用户的角色信息，并播放中部动画
 */
public class CharacterFragment extends Fragment {

    private ImageView ivAvatar;
    private ImageView ivMiddleImage;
    private TextView tvName;
    private TextView tvLevel;
    private ProgressBar progressExp;
    private TextView tvExpPercent;
    private TextView tvPower;

    public CharacterFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_character, container, false);
        initViews(root);
        loadCharacterFromDb();  // 初次加载角色数据
        startMiddleAnimation(); // 开始中部动画
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次 Fragment 可见时刷新角色数据（例如任务完成后更新经验和战力）
        loadCharacterFromDb();
    }

    /**
     * 绑定视图控件
     */
    private void initViews(View root) {
        ivAvatar      = root.findViewById(R.id.iv_avatar);
        ivMiddleImage = root.findViewById(R.id.iv_middle_image);
        tvName        = root.findViewById(R.id.tv_name);
        tvLevel       = root.findViewById(R.id.tv_level);
        progressExp   = root.findViewById(R.id.progress_exp);
        tvExpPercent  = root.findViewById(R.id.tv_exp_percent);
        tvPower       = root.findViewById(R.id.tv_power);
    }

    /**
     * 从数据库获取角色信息并展示，包括头像、等级、经验进度和战斗力
     */
    private void loadCharacterFromDb() {
        Context ctx = requireContext();
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        if (userId < 0) return;

        CharacterDAO dao = new CharacterDAO(ctx);
        CharacterModel cm = dao.getCharacter(userId);
        if (cm == null) return;

        ivAvatar.setImageResource(cm.getImageRes());
        tvName.setText(cm.getName());
        tvLevel.setText("等级：" + cm.getLevel());
        // 经验进度条最大值 10，代表满级经验
        progressExp.setMax(10);
        progressExp.setProgress(cm.getExp());
        tvExpPercent.setText("经验：" + (cm.getExp() * 10) + "%");
        tvPower.setText("战斗力：" + cm.getCombatPower());
    }

    /**
     * 启动并循环播放中部的帧动画
     */
    private void startMiddleAnimation() {
        ivMiddleImage.setImageResource(R.drawable.anim_character_middle);
        AnimationDrawable anim = (AnimationDrawable) ivMiddleImage.getDrawable();
        anim.start();
    }
}
