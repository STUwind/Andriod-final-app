package com.andriodcourse.andriodfinalapp.fragment;

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

public class CharacterFragment extends Fragment {

    private ImageView ivAvatar;
    private ImageView ivMiddleImage;
    private TextView tvName;
    private TextView tvLevel;
    private ProgressBar progressExp;
    private TextView tvPower;

    public CharacterFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_character, container, false);
        initViews(root);
        loadDummyCharacter();
        return root;
    }

    /**
     * 绑定布局中的控件
     */
    private void initViews(View root) {
        ivAvatar    = root.findViewById(R.id.iv_avatar);
        ivMiddleImage  = root.findViewById(R.id.iv_middle_image);
        tvName      = root.findViewById(R.id.tv_name);
        tvLevel     = root.findViewById(R.id.tv_level);
        progressExp = root.findViewById(R.id.progress_exp);
        tvPower     = root.findViewById(R.id.tv_power);
    }

    /**
     * 临时加载一组假数据，后续替换成从数据库获取真实角色数据
     */
    private void loadDummyCharacter() {
        // 设置头像占位图
        ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
        ivMiddleImage.setImageResource(R.drawable.ic_middle_placeholder);

        // 示例角色信息
        tvName.setText("疾风剑豪");
        tvLevel.setText("等级：6");
        progressExp.setMax(100);
        progressExp.setProgress(45);  // 当前经验进度：45%
        tvPower.setText("战斗力：1580");
    }
}
