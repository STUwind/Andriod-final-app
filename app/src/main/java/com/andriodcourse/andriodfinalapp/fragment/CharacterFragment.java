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
 * 角色界面 Fragment，展示当前用户的角色信息，并播放中下位置的动画
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
        loadCharacterFromDb();
        startMiddleAnimation();
        return root;
    }

    private void initViews(View root) {
        ivAvatar       = root.findViewById(R.id.iv_avatar);
        ivMiddleImage  = root.findViewById(R.id.iv_middle_image);
        tvName         = root.findViewById(R.id.tv_name);
        tvLevel        = root.findViewById(R.id.tv_level);
        progressExp    = root.findViewById(R.id.progress_exp);
        tvExpPercent   = root.findViewById(R.id.tv_exp_percent);
        tvPower        = root.findViewById(R.id.tv_power);
    }

    private void loadCharacterFromDb() {
        Context ctx = requireContext();
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        if (userId < 0) return;

        CharacterDAO dao = new CharacterDAO(ctx);
        CharacterModel cm = dao.getCharacter(userId);
        if (cm == null) return;

        // 直接使用资源ID设置头像
        ivAvatar.setImageResource(cm.getImageRes());

        // 名称、等级、经验、战斗力显示
        tvName.setText(cm.getName());
        tvLevel.setText("等级：" + cm.getLevel());
        progressExp.setMax(10);
        progressExp.setProgress(cm.getExp());
        tvExpPercent.setText("经验：" + (cm.getExp() * 10) + "%");
        tvPower.setText("战斗力：" + cm.getCombatPower());
    }

    private void startMiddleAnimation() {
        ivMiddleImage.setImageResource(R.drawable.anim_character_middle);
        AnimationDrawable anim = (AnimationDrawable) ivMiddleImage.getDrawable();
        anim.start();
    }
}
