package com.andriodcourse.andriodfinalapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.andriodcourse.andriodfinalapp.R;
import com.andriodcourse.andriodfinalapp.db.CharacterDAO;
import com.andriodcourse.andriodfinalapp.model.CharacterModel;

/**
 * 战斗界面 Fragment，展示boss血量和战斗功能
 */
public class BattleFragment extends Fragment {

    private TextView tvBossTitle;
    private ProgressBar progressBossHp;
    private TextView tvBossHp;
    private ImageView ivCharacter;
    private ImageView ivBattlePlaceholder;
    private ImageView ivBoss;
    private Button btnAttack;
    private Button btnReset;

    private static final int MAX_BOSS_HP = 99999;
    private int currentBossHp = MAX_BOSS_HP;
    private boolean isAttacking = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    public BattleFragment() {
        // Required empty public constructor
    }

    public static BattleFragment newInstance() {
        return new BattleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_battle, container, false);
        initViews(root);
        initBossHp();
        loadCharacterImage();
        setupAttackButton();
        return root;
    }

    /**
     * 绑定视图控件
     */
    private void initViews(View root) {
        tvBossTitle = root.findViewById(R.id.tv_boss_title);
        progressBossHp = root.findViewById(R.id.progress_boss_hp);
        tvBossHp = root.findViewById(R.id.tv_boss_hp);
        ivCharacter = root.findViewById(R.id.iv_character);
        ivBattlePlaceholder = root.findViewById(R.id.iv_battle_placeholder);
        ivBoss = root.findViewById(R.id.iv_boss);
        btnAttack = root.findViewById(R.id.btn_attack);
        btnReset = root.findViewById(R.id.btn_reset);
    }

    /**
     * 初始化Boss血量显示
     */
    private void initBossHp() {
        // 从SharedPreferences读取保存的Boss血量
        Context ctx = requireContext();
        SharedPreferences sp = ctx.getSharedPreferences("battle_data", Context.MODE_PRIVATE);
        currentBossHp = sp.getInt("boss_hp", MAX_BOSS_HP);
        
        progressBossHp.setMax(MAX_BOSS_HP);
        progressBossHp.setProgress(currentBossHp);
        updateBossHpText();
        
        // 如果Boss已被击败，更新按钮状态
        if (currentBossHp <= 0) {
            btnAttack.setEnabled(false);
            btnAttack.setText("Boss已被击败");
        }
    }

    /**
     * 更新Boss血量文本显示
     */
    private void updateBossHpText() {
        tvBossHp.setText(currentBossHp + " / " + MAX_BOSS_HP);
    }

    /**
     * 加载角色图片（使用角色动画的第一帧）
     */
    private void loadCharacterImage() {
        // 始终使用 char_middle_frame1 作为默认显示
        ivCharacter.setImageResource(R.drawable.char_middle_frame1);
    }

    /**
     * 设置攻击按钮点击事件
     */
    private void setupAttackButton() {
        btnAttack.setOnClickListener(v -> {
            if (!isAttacking && currentBossHp > 0) {
                performAttack();
            }
        });
        
        btnReset.setOnClickListener(v -> {
            if (!isAttacking) {
                resetBoss();
            }
        });
    }

    /**
     * 执行攻击逻辑
     */
    private void performAttack() {
        isAttacking = true;
        btnAttack.setEnabled(false);
        btnAttack.setText("攻击中...");

        // 播放角色攻击动画（三次循环）
        playCharacterAttackAnimation();

        // 计算伤害并更新Boss血量
        // 动画总时间：12帧 × 200ms = 2400ms
        handler.postDelayed(() -> {
            int damage = calculateDamage();
            dealDamageToBoss(damage);
            
            // 恢复攻击状态
            isAttacking = false;
            
            // 只有当Boss还活着时才恢复按钮状态
            if (currentBossHp > 0) {
                btnAttack.setEnabled(true);
                btnAttack.setText("攻击");
            }
            
            // 恢复角色默认图片（char_middle_frame1）
            ivCharacter.setImageResource(R.drawable.char_middle_frame1);
            
        }, 2400); // 动画播放时间（12帧 * 200ms）
    }

    /**
     * 播放角色攻击动画（三次循环）
     */
    private void playCharacterAttackAnimation() {
        ivCharacter.setImageResource(R.drawable.anim_character_battle);
        AnimationDrawable anim = (AnimationDrawable) ivCharacter.getDrawable();
        if (anim != null) {
            anim.start();
        }
    }

    /**
     * 计算攻击伤害
     */
    private int calculateDamage() {
        Context ctx = requireContext();
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        
        if (userId < 0) {
            return 100; // 默认伤害
        }

        CharacterDAO dao = new CharacterDAO(ctx);
        CharacterModel cm = dao.getCharacter(userId);
        if (cm != null) {
            // 基于角色战斗力计算伤害，添加一些随机性
            int baseDamage = cm.getCombatPower();
            int randomFactor = (int) (Math.random() * 50) + 75; // 75-125%的随机系数
            return baseDamage * randomFactor / 100;
        }
        
        return 100; // 默认伤害
    }

    /**
     * 对Boss造成伤害
     */
    private void dealDamageToBoss(int damage) {
        currentBossHp = Math.max(0, currentBossHp - damage);
        
        // 保存Boss血量到SharedPreferences
        Context ctx = requireContext();
        SharedPreferences sp = ctx.getSharedPreferences("battle_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("boss_hp", currentBossHp);
        editor.apply();
        
        // 更新UI
        progressBossHp.setProgress(currentBossHp);
        updateBossHpText();
        
        // 显示伤害提示
        Toast.makeText(getContext(), "造成伤害: " + damage, Toast.LENGTH_SHORT).show();
        
        // 检查Boss是否被击败
        if (currentBossHp <= 0) {
            onBossDefeated();
        }
    }

    /**
     * Boss被击败时的处理
     */
    private void onBossDefeated() {
        btnAttack.setEnabled(false);
        btnAttack.setText("Boss已被击败");
        Toast.makeText(getContext(), "恭喜！你击败了Boss！", Toast.LENGTH_LONG).show();
        
        // 可以在这里添加奖励逻辑，比如增加经验值等
        giveVictoryReward();
    }

    /**
     * 给予胜利奖励
     */
    private void giveVictoryReward() {
        Context ctx = requireContext();
        SharedPreferences sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        
        if (userId >= 0) {
            CharacterDAO dao = new CharacterDAO(ctx);
            // 给予大量经验奖励
            dao.addExp(userId, 50);
            Toast.makeText(getContext(), "获得经验奖励: 50", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 重置Boss血量（可以添加重置按钮调用此方法）
     */
    public void resetBoss() {
        currentBossHp = MAX_BOSS_HP;
        
        // 保存重置后的血量到SharedPreferences
        Context ctx = requireContext();
        SharedPreferences sp = ctx.getSharedPreferences("battle_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("boss_hp", currentBossHp);
        editor.apply();
        
        progressBossHp.setProgress(currentBossHp);
        updateBossHpText();
        btnAttack.setEnabled(true);
        btnAttack.setText("攻击");
        Toast.makeText(getContext(), "Boss已重置", Toast.LENGTH_SHORT).show();
    }
}
