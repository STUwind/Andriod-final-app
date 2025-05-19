package com.andriodcourse.andriodfinalapp.model;

/**
 * 角色模型，封装用户 ID、名称、等级、经验、头像资源 ID 和战斗力
 */
public class CharacterModel {
    private int userId;        // 关联的用户 ID
    private String name;       // 角色名称
    private int level;         // 当前等级
    private int exp;           // 剩余经验值（用于升级进度）
    private int imageRes;      // 头像资源 ID
    private int combatPower;   // 当前战斗力

    /**
     * 构造函数
     * @param userId 用户 ID
     * @param name 角色名称
     * @param level 当前等级
     * @param exp 当前剩余经验
     * @param imageRes 头像资源 ID
     * @param combatPower 当前战斗力
     */
    public CharacterModel(int userId, String name, int level, int exp, int imageRes, int combatPower) {
        this.userId = userId;
        this.name = name;
        this.level = level;
        this.exp = exp;
        this.imageRes = imageRes;
        this.combatPower = combatPower;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public int getCombatPower() {
        return combatPower;
    }

    public void setCombatPower(int combatPower) {
        this.combatPower = combatPower;
    }
}
