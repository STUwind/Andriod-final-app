package com.andriodcourse.andriodfinalapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andriodcourse.andriodfinalapp.R;
import com.andriodcourse.andriodfinalapp.db.CharacterDAO;
import com.andriodcourse.andriodfinalapp.db.DBHelper;
import com.andriodcourse.andriodfinalapp.model.CharacterModel;

import java.util.Random;

/**
 * 升级选择管理器
 * 处理每5级时的特殊升级选择功能
 */
public class UpgradeChoiceManager {
    
    private Context context;
    private CharacterDAO characterDAO;
    private Random random;
    
    // 升级选择接口
    public interface OnUpgradeCompleteListener {
        void onUpgradeComplete(int levelsGained, boolean success);
    }

    public UpgradeChoiceManager(Context context) {
        this.context = context;
        this.characterDAO = new CharacterDAO(context);
        this.random = new Random();
    }

    /**
     * 检查是否需要显示升级选择对话框
     * @param userId 用户ID
     * @param currentLevel 当前等级
     * @param previousLevel 之前等级
     * @return 是否需要显示升级选择
     */
    public boolean shouldShowUpgradeChoice(int userId, int currentLevel, int previousLevel) {
        // 检查是否跨越了5的倍数
        int previousMilestone = (previousLevel / 5) * 5;
        int currentMilestone = (currentLevel / 5) * 5;
        
        if (currentMilestone > previousMilestone && currentLevel >= 5) {
            CharacterModel character = characterDAO.getCharacter(userId);
            if (character != null) {
                // 检查是否已经在这个里程碑等级进行过选择
                return character.getLastUpgradeChoiceLevel() < currentMilestone;
            }
        }
        
        return false;
    }

    /**
     * 显示升级选择对话框
     * @param userId 用户ID
     * @param currentLevel 当前等级
     * @param listener 升级完成监听器
     */
    public void showUpgradeChoiceDialog(int userId, int currentLevel, OnUpgradeCompleteListener listener) {
        if (context == null) {
            // 如果上下文为空，直接回调失败
            if (listener != null) {
                listener.onUpgradeComplete(0, false);
            }
            return;
        }
        
        CharacterModel character = characterDAO.getCharacter(userId);
        if (character == null) {
            if (listener != null) {
                listener.onUpgradeComplete(0, false);
            }
            return;
        }

        try {
            // 创建对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_upgrade_choice, null);
            builder.setView(dialogView);
            builder.setCancelable(false); // 必须选择一个选项

            // 更新对话框信息
            TextView tvLevelInfo = dialogView.findViewById(R.id.tv_level_info);
            int milestone = (currentLevel / 5) * 5;
            tvLevelInfo.setText(String.format("恭喜达到%d级里程碑！选择你的升级方式：", milestone));

            AlertDialog dialog = builder.create();

            // 绑定按钮事件
            Button btnSafe = dialogView.findViewById(R.id.btn_safe_choice);
            Button btnMedium = dialogView.findViewById(R.id.btn_medium_choice);
            Button btnRisky = dialogView.findViewById(R.id.btn_risky_choice);

            btnSafe.setOnClickListener(v -> {
                dialog.dismiss();
                performUpgradeChoice(userId, milestone, DBHelper.UPGRADE_OPTION_SAFE, listener);
            });

            btnMedium.setOnClickListener(v -> {
                dialog.dismiss();
                performUpgradeChoice(userId, milestone, DBHelper.UPGRADE_OPTION_MEDIUM, listener);
            });

            btnRisky.setOnClickListener(v -> {
                dialog.dismiss();
                performUpgradeChoice(userId, milestone, DBHelper.UPGRADE_OPTION_RISKY, listener);
            });

            dialog.show();
        } catch (Exception e) {
            // 如果对话框显示失败，回调失败结果
            e.printStackTrace();
            if (listener != null) {
                listener.onUpgradeComplete(0, false);
            }
        }
    }

    /**
     * 执行升级选择
     * @param userId 用户ID
     * @param milestoneLevel 里程碑等级
     * @param choice 选择类型
     * @param listener 升级完成监听器
     */
    private void performUpgradeChoice(int userId, int milestoneLevel, int choice, OnUpgradeCompleteListener listener) {
        boolean success = false;
        int levelsToGain = 0;
        String resultMessage = "";
        
        try {
            switch (choice) {
                case DBHelper.UPGRADE_OPTION_SAFE:
                    // 100%概率升1级
                    success = true;
                    levelsToGain = 1;
                    resultMessage = "🎉 稳健选择成功！升级 1 级！";
                    break;
                    
                case DBHelper.UPGRADE_OPTION_MEDIUM:
                    // 50%概率升2级
                    success = random.nextFloat() < 0.5f;
                    if (success) {
                        levelsToGain = 2;
                        resultMessage = "🎉 平衡选择成功！升级 2 级！";
                    } else {
                        resultMessage = "😔 平衡选择失败...没有获得升级";
                    }
                    break;
                    
                case DBHelper.UPGRADE_OPTION_RISKY:
                    // 30%概率升3级
                    success = random.nextFloat() < 0.3f;
                    if (success) {
                        levelsToGain = 3;
                        resultMessage = "🎊 赌狗选择大成功！升级 3 级！";
                    } else {
                        resultMessage = "💔 赌狗选择失败...败者什么都得不到！";
                    }
                    break;
                    
                default:
                    resultMessage = "❌ 未知选择类型";
                    success = false;
                    break;
            }

            // 记录这次升级选择（无论成功失败都要记录）
            boolean updateSuccess = characterDAO.updateLastUpgradeChoiceLevel(userId, milestoneLevel);
            if (!updateSuccess) {
                android.util.Log.e("UpgradeChoiceManager", "Failed to update upgrade choice level");
            }

            // 如果抽奖成功，执行升级
            if (success && levelsToGain > 0) {
                boolean levelUpSuccess = characterDAO.addLevelsDirectly(userId, levelsToGain);
                if (!levelUpSuccess) {
                    // 如果等级提升失败，修改结果
                    success = false;
                    levelsToGain = 0;
                    resultMessage = "❌ 升级过程出现错误，请稍后重试";
                    android.util.Log.e("UpgradeChoiceManager", "Failed to add levels directly");
                }
            }

            // 创建final变量供lambda表达式使用
            final boolean finalSuccess = success;
            final int finalLevelsToGain = levelsToGain;
            final String finalResultMessage = resultMessage;

            // 显示结果（在主线程中）
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() -> {
                    showUpgradeResult(finalResultMessage, finalSuccess);
                });
            } else {
                showUpgradeResult(finalResultMessage, finalSuccess);
            }

            // 回调监听器（在主线程中）
            if (listener != null) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        listener.onUpgradeComplete(finalLevelsToGain, finalSuccess);
                    });
                } else {
                    listener.onUpgradeComplete(finalLevelsToGain, finalSuccess);
                }
            }
            
        } catch (Exception e) {
            android.util.Log.e("UpgradeChoiceManager", "Error in performUpgradeChoice", e);
            
            // 发生异常时的处理
            final String errorMessage = "❌ 升级过程发生错误，请重试";
            
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() -> {
                    showUpgradeResult(errorMessage, false);
                    if (listener != null) {
                        listener.onUpgradeComplete(0, false);
                    }
                });
            } else {
                showUpgradeResult(errorMessage, false);
                if (listener != null) {
                    listener.onUpgradeComplete(0, false);
                }
            }
        }
    }

    /**
     * 显示升级结果
     * @param message 结果消息
     * @param success 是否成功
     */
    private void showUpgradeResult(String message, boolean success) {
        if (context == null) {
            return;
        }
        
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            
            if (success) {
                builder.setTitle("🎉 升级成功！");
                builder.setIcon(android.R.drawable.ic_dialog_info);
            } else {
                builder.setTitle("😔 升级失败");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
            }
            
            builder.setMessage(message);
            builder.setPositiveButton("确定", null);
            builder.show();
        } catch (Exception e) {
            // 如果对话框显示失败，静默处理
            e.printStackTrace();
        }
    }

    /**
     * 计算角色需要的5级里程碑
     * @param level 当前等级
     * @return 下一个5级里程碑
     */
    public static int getNextMilestone(int level) {
        return ((level / 5) + 1) * 5;
    }

    /**
     * 检查等级是否为5的倍数
     * @param level 等级
     * @return 是否为里程碑等级
     */
    public static boolean isMilestoneLevel(int level) {
        return level > 0 && level % 5 == 0;
    }
} 