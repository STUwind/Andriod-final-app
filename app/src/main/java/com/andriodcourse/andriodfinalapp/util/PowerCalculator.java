package com.andriodcourse.andriodfinalapp.util;

/**
 * 战斗力计算工具类
 * 负责处理角色等级提升时的战斗力增长计算
 * 确保100级时总战斗力不超过9999
 */
public class PowerCalculator {
    
    // 等级段定义
    private static final int LEVEL_TIER_1_MAX = 20;    // 基础增长期
    private static final int LEVEL_TIER_2_MAX = 50;    // 快速增长期
    private static final int LEVEL_TIER_3_MAX = 80;    // 稳定增长期
    private static final int LEVEL_TIER_4_MAX = 100;   // 缓慢增长期
    
    // 各等级段的基础战斗力增长
    private static final int TIER_1_BASE = 60;         // 1-20级基础值
    private static final int TIER_1_INCREMENT = 1;     // 1-20级每级增长
    
    private static final int TIER_2_BASE = 80;         // 21-50级基础值  
    private static final int TIER_2_INCREMENT = 1;     // 21-50级每级增长
    
    private static final int TIER_3_BASE = 70;         // 51-80级基础值
    private static final int TIER_3_INCREMENT_DIV = 2; // 51-80级增长除数
    
    private static final int TIER_4_BASE = 50;         // 81-100级基础值
    private static final double TIER_4_INCREMENT_DIV = 1.3; // 81-100级增长除数
    
    private static final int RANDOM_BONUS_MAX = 21;    // 随机奖励最大值(0-20)
    private static final int AVERAGE_RANDOM_BONUS = 10; // 平均随机奖励
    
    /**
     * 计算指定等级的战斗力增长值
     * @param level 目标等级
     * @return 该等级应获得的战斗力
     */
    public static int calculatePowerGainForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        
        int basePower = calculateBasePowerForLevel(level);
        int randomBonus = (int)(Math.random() * RANDOM_BONUS_MAX); // 0-20的随机奖励
        
        return basePower + randomBonus;
    }
    
    /**
     * 计算指定等级的基础战斗力增长值（不含随机因素）
     * @param level 目标等级
     * @return 基础战斗力增长值
     */
    private static int calculateBasePowerForLevel(int level) {
        if (level <= LEVEL_TIER_1_MAX) {
            // 1-20级：60-79线性增长
            return TIER_1_BASE + (level - 1) * TIER_1_INCREMENT;
        } else if (level <= LEVEL_TIER_2_MAX) {
            // 21-50级：80-109线性增长
            return TIER_2_BASE + (level - LEVEL_TIER_1_MAX - 1) * TIER_2_INCREMENT;
        } else if (level <= LEVEL_TIER_3_MAX) {
            // 51-80级：70-84缓慢增长
            return TIER_3_BASE + (level - LEVEL_TIER_2_MAX) / TIER_3_INCREMENT_DIV;
        } else if (level <= LEVEL_TIER_4_MAX) {
            // 81-100级：50-65缓慢增长
            return TIER_4_BASE + (int)((level - LEVEL_TIER_3_MAX) / TIER_4_INCREMENT_DIV);
        } else {
            // 100级以上，固定增长
            return 30 + (int)(Math.random() * 11); // 30-40随机
        }
    }
    
    /**
     * 计算从1级到指定等级的总战斗力（平均值，不含随机因素）
     * @param targetLevel 目标等级
     * @return 预期的总战斗力
     */
    public static int calculateTotalPowerAtLevel(int targetLevel) {
        if (targetLevel <= 0) {
            return 0;
        }
        
        int totalPower = 0;
        for (int level = 1; level <= targetLevel; level++) {
            int basePower = calculateBasePowerForLevel(level);
            totalPower += basePower + AVERAGE_RANDOM_BONUS; // 加上平均随机奖励
        }
        return totalPower;
    }
    
    /**
     * 获取等级段描述
     * @param level 等级
     * @return 等级段描述
     */
    public static String getLevelTierDescription(int level) {
        if (level <= LEVEL_TIER_1_MAX) {
            return "基础成长期";
        } else if (level <= LEVEL_TIER_2_MAX) {
            return "快速成长期";
        } else if (level <= LEVEL_TIER_3_MAX) {
            return "稳定成长期";
        } else if (level <= LEVEL_TIER_4_MAX) {
            return "缓慢成长期";
        } else {
            return "传说境界";
        }
    }
    
    /**
     * 验证战斗力增长系统是否合理
     * @return 是否通过验证
     */
    public static boolean validatePowerGrowthSystem() {
        int totalPowerAt100 = calculateTotalPowerAtLevel(100);
        return totalPowerAt100 <= 9999;
    }
    
    /**
     * 获取系统详细信息（用于调试）
     * @return 系统信息字符串
     */
    public static String getSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== 战斗力增长系统信息 ===\n");
        info.append(String.format("1-20级: %d基础 + %d/级 + 随机0-20\n", 
                TIER_1_BASE, TIER_1_INCREMENT));
        info.append(String.format("21-50级: %d基础 + %d/级 + 随机0-20\n", 
                TIER_2_BASE, TIER_2_INCREMENT));
        info.append(String.format("51-80级: %d基础 + 等级/%d + 随机0-20\n", 
                TIER_3_BASE, TIER_3_INCREMENT_DIV));
        info.append(String.format("81-100级: %d基础 + 等级/%.1f + 随机0-20\n", 
                TIER_4_BASE, TIER_4_INCREMENT_DIV));
        
        int[] testLevels = {20, 50, 80, 100};
        for (int level : testLevels) {
            int power = calculateTotalPowerAtLevel(level);
            info.append(String.format("%d级预期战斗力: %d\n", level, power));
        }
        
        boolean isValid = validatePowerGrowthSystem();
        info.append(String.format("系统验证: %s\n", isValid ? "✅ 通过" : "❌ 失败"));
        
        return info.toString();
    }
} 