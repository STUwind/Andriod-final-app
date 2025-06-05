package com.andriodcourse.andriodfinalapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.andriodcourse.andriodfinalapp.R;
import com.andriodcourse.andriodfinalapp.model.CharacterModel;
import com.andriodcourse.andriodfinalapp.util.PowerCalculator;

public class CharacterDAO {
    private DBHelper helper;

    public CharacterDAO(Context context) {
        helper = new DBHelper(context);
    }

    /**
     * 升级选择触发接口
     */
    public interface OnUpgradeChoiceNeededListener {
        void onUpgradeChoiceNeeded(int userId, int currentLevel, int previousLevel);
    }

    public void createDefault(int userId, String name, int imageResId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.CHARACTER_USER_ID, userId);
        values.put(DBHelper.CHARACTER_NAME, name);
        values.put(DBHelper.CHARACTER_LEVEL, 1);
        values.put(DBHelper.CHARACTER_EXP, 0);
        values.put(DBHelper.CHARACTER_IMAGE, imageResId);
        values.put(DBHelper.CHARACTER_COMBAT_POWER, 0);
        values.put(DBHelper.CHARACTER_LAST_UPGRADE_CHOICE_LEVEL, 0);
        db.insert(DBHelper.TABLE_CHARACTER, null, values);
        db.close();
    }

    public CharacterModel getCharacter(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_CHARACTER, null, 
                DBHelper.CHARACTER_USER_ID + "=?", 
                new String[]{String.valueOf(userId)}, null, null, null);
        
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return null;
        }
        
        // 获取字段值，考虑向后兼容性
        int lastUpgradeChoiceLevel = 0;
        try {
            int columnIndex = cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_LAST_UPGRADE_CHOICE_LEVEL);
            lastUpgradeChoiceLevel = cursor.getInt(columnIndex);
        } catch (IllegalArgumentException e) {
            // 如果字段不存在（旧版本数据库），使用默认值0
        }
        
        CharacterModel cm = new CharacterModel(
                userId,
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_LEVEL)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_EXP)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_IMAGE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_COMBAT_POWER)),
                lastUpgradeChoiceLevel
        );
        cursor.close();
        db.close();
        return cm;
    }

    /**
     * 计算单次升级获得的战斗力（已弃用，使用PowerCalculator代替）
     * @deprecated 使用 PowerCalculator.calculatePowerGainForLevel() 代替
     */
    @Deprecated
    private int calculatePowerGainForLevel(int level) {
        return PowerCalculator.calculatePowerGainForLevel(level);
    }
    
    /**
     * 计算从1级到指定等级的总战斗力（已弃用，使用PowerCalculator代替）
     * @deprecated 使用 PowerCalculator.calculateTotalPowerAtLevel() 代替
     */
    @Deprecated
    private int calculateTotalPowerAtLevel(int targetLevel) {
        return PowerCalculator.calculateTotalPowerAtLevel(targetLevel);
    }

    /**
     * 简化版添加经验值方法（用于调试）
     * @param userId 用户ID
     * @param addExp 要添加的经验值
     * @return 是否成功
     */
    public boolean addExpSimple(int userId, int addExp) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            android.util.Log.d("CharacterDAO", "Adding " + addExp + " exp to user " + userId);
            
            db = helper.getWritableDatabase();
            cursor = db.query(DBHelper.TABLE_CHARACTER, null, 
                    DBHelper.CHARACTER_USER_ID + "=?", 
                    new String[]{String.valueOf(userId)}, null, null, null);
            
            if (!cursor.moveToFirst()) {
                if (cursor != null) cursor.close();
                cursor = null;
                android.util.Log.d("CharacterDAO", "Character not found, creating default");
                createDefault(userId, "角色", R.drawable.avatar_default);
                cursor = db.query(DBHelper.TABLE_CHARACTER, null, 
                        DBHelper.CHARACTER_USER_ID + "=?", 
                        new String[]{String.valueOf(userId)}, null, null, null);
                if (!cursor.moveToFirst()) {
                    android.util.Log.e("CharacterDAO", "Failed to create default character");
                    return false;
                }
            }
            
            int currentExp = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_EXP));
            int originalLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_LEVEL));
            int power = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_COMBAT_POWER));
            
            android.util.Log.d("CharacterDAO", "Before: Level=" + originalLevel + ", Exp=" + currentExp + ", Power=" + power);

            int totalExp = currentExp + addExp;
            int level = originalLevel;
            
            // 计算升级和战斗力增长
            while (totalExp >= 10) {
                totalExp -= 10;
                level++;
                int powerGain = PowerCalculator.calculatePowerGainForLevel(level);
                power += powerGain;
                android.util.Log.d("CharacterDAO", "Level " + level + " gained " + powerGain + " power");
            }
            
            android.util.Log.d("CharacterDAO", "After: Level=" + level + ", Exp=" + totalExp + ", Power=" + power);
            android.util.Log.d("CharacterDAO", "Expected power at level " + level + ": " + PowerCalculator.calculateTotalPowerAtLevel(level));
            
            // 更新数据库
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHARACTER_EXP, totalExp);
            values.put(DBHelper.CHARACTER_LEVEL, level);
            values.put(DBHelper.CHARACTER_COMBAT_POWER, power);
            
            int rowsUpdated = db.update(DBHelper.TABLE_CHARACTER, values, 
                    DBHelper.CHARACTER_USER_ID + "=?", 
                    new String[]{String.valueOf(userId)});
            
            android.util.Log.d("CharacterDAO", "Rows updated: " + rowsUpdated);
            
            return rowsUpdated > 0;
            
        } catch (Exception e) {
            android.util.Log.e("CharacterDAO", "Error in addExpSimple", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    /**
     * 添加经验值（原方法，不触发升级选择）
     */
    public void addExp(int userId, int addExp) {
        addExp(userId, addExp, null);
    }

    /**
     * 添加经验值（支持升级选择回调）
     * @param userId 用户ID
     * @param addExp 要添加的经验值
     * @param listener 升级选择监听器（可为null）
     */
    public void addExp(int userId, int addExp, OnUpgradeChoiceNeededListener listener) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = helper.getWritableDatabase();
            cursor = db.query(DBHelper.TABLE_CHARACTER, null, 
                    DBHelper.CHARACTER_USER_ID + "=?", 
                    new String[]{String.valueOf(userId)}, null, null, null);
            
            if (!cursor.moveToFirst()) {
                if (cursor != null) cursor.close();
                cursor = null;
                createDefault(userId, "角色", R.drawable.avatar_default);
                cursor = db.query(DBHelper.TABLE_CHARACTER, null, 
                        DBHelper.CHARACTER_USER_ID + "=?", 
                        new String[]{String.valueOf(userId)}, null, null, null);
                if (!cursor.moveToFirst()) {
                    return;
                }
            }
            
            int currentExp = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_EXP));
            int originalLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_LEVEL));
            int power = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_COMBAT_POWER));

            int totalExp = currentExp + addExp;
            int level = originalLevel;
            
            // 计算升级和战斗力增长
            while (totalExp >= 10) {
                totalExp -= 10;
                level++;
                int powerGain = PowerCalculator.calculatePowerGainForLevel(level);
                power += powerGain;
            }
            
            // 更新数据库
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHARACTER_EXP, totalExp);
            values.put(DBHelper.CHARACTER_LEVEL, level);
            values.put(DBHelper.CHARACTER_COMBAT_POWER, power);
            
            // 检查是否有updated_at字段，如果有则更新
            try {
                cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_UPDATED_AT);
                // 字段存在，使用正确的SQLite时间函数
                values.put(DBHelper.CHARACTER_UPDATED_AT, "datetime('now')");
            } catch (IllegalArgumentException e) {
                // 字段不存在，跳过更新
                android.util.Log.d("CharacterDAO", "UPDATED_AT field not found, skipping timestamp update");
            }
            
            int rowsUpdated = db.update(DBHelper.TABLE_CHARACTER, values, 
                    DBHelper.CHARACTER_USER_ID + "=?", 
                    new String[]{String.valueOf(userId)});
            
            // 检查更新是否成功
            if (rowsUpdated > 0) {
                // 检查是否需要触发升级选择
                if (listener != null && level > originalLevel) {
                    listener.onUpgradeChoiceNeeded(userId, level, originalLevel);
                }
            } else {
                // 更新失败，记录日志
                android.util.Log.e("CharacterDAO", "Failed to update character exp for user: " + userId);
            }
            
        } catch (Exception e) {
            android.util.Log.e("CharacterDAO", "Error adding exp for user: " + userId, e);
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 直接增加指定数量的等级（用于升级选择功能）
     * @param userId 用户ID
     * @param levelsToAdd 要增加的等级数
     * @return 是否成功
     */
    public boolean addLevelsDirectly(int userId, int levelsToAdd) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            android.util.Log.d("CharacterDAO", "Adding " + levelsToAdd + " levels to user " + userId);
            
            db = helper.getWritableDatabase();
            cursor = db.query(DBHelper.TABLE_CHARACTER, null, 
                    DBHelper.CHARACTER_USER_ID + "=?", 
                    new String[]{String.valueOf(userId)}, null, null, null);
            
            if (!cursor.moveToFirst()) {
                android.util.Log.e("CharacterDAO", "Character not found for user: " + userId);
                return false;
            }
            
            int currentLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_LEVEL));
            int power = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_COMBAT_POWER));
            
            android.util.Log.d("CharacterDAO", "Current level: " + currentLevel + ", power: " + power);

            // 计算新等级和战斗力增长
            int newLevel = currentLevel + levelsToAdd;
            
            // 为每个等级增加战斗力
            for (int i = 0; i < levelsToAdd; i++) {
                int level = currentLevel + i + 1;
                int powerGain = PowerCalculator.calculatePowerGainForLevel(level);
                power += powerGain;
                android.util.Log.d("CharacterDAO", "Level " + level + " gained " + powerGain + " power");
            }
            
            android.util.Log.d("CharacterDAO", "New level: " + newLevel + ", new power: " + power);
            android.util.Log.d("CharacterDAO", "Expected total power at level " + newLevel + ": " + PowerCalculator.calculateTotalPowerAtLevel(newLevel));
            
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHARACTER_LEVEL, newLevel);
            values.put(DBHelper.CHARACTER_COMBAT_POWER, power);
            
            // 检查是否有updated_at字段，如果有则更新
            try {
                cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_UPDATED_AT);
                // 字段存在，使用正确的SQLite时间函数
                values.put(DBHelper.CHARACTER_UPDATED_AT, "datetime('now')");
            } catch (IllegalArgumentException e) {
                // 字段不存在，跳过更新
                android.util.Log.d("CharacterDAO", "UPDATED_AT field not found, skipping timestamp update");
            }
            
            int rowsUpdated = db.update(DBHelper.TABLE_CHARACTER, values, 
                    DBHelper.CHARACTER_USER_ID + "=?", 
                    new String[]{String.valueOf(userId)});
            
            android.util.Log.d("CharacterDAO", "Levels updated, rows affected: " + rowsUpdated);
            return rowsUpdated > 0;
            
        } catch (Exception e) {
            android.util.Log.e("CharacterDAO", "Error adding levels directly", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    /**
     * 更新角色的上次升级选择等级
     * @param userId 用户ID
     * @param milestoneLevel 里程碑等级
     * @return 是否成功
     */
    public boolean updateLastUpgradeChoiceLevel(int userId, int milestoneLevel) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            android.util.Log.d("CharacterDAO", "Updating upgrade choice level to " + milestoneLevel + " for user " + userId);
            
            db = helper.getWritableDatabase();
            
            // 先检查角色是否存在并获取字段信息
            cursor = db.query(DBHelper.TABLE_CHARACTER, null, 
                    DBHelper.CHARACTER_USER_ID + "=?", 
                    new String[]{String.valueOf(userId)}, null, null, null);
            
            if (!cursor.moveToFirst()) {
                android.util.Log.e("CharacterDAO", "Character not found for user: " + userId);
                return false;
            }
            
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHARACTER_LAST_UPGRADE_CHOICE_LEVEL, milestoneLevel);
            
            // 检查是否有updated_at字段，如果有则更新
            try {
                cursor.getColumnIndexOrThrow(DBHelper.CHARACTER_UPDATED_AT);
                // 字段存在，使用正确的SQLite时间函数
                values.put(DBHelper.CHARACTER_UPDATED_AT, "datetime('now')");
            } catch (IllegalArgumentException e) {
                // 字段不存在，跳过更新
                android.util.Log.d("CharacterDAO", "UPDATED_AT field not found, skipping timestamp update");
            }
            
            int rowsUpdated = db.update(DBHelper.TABLE_CHARACTER, values, 
                    DBHelper.CHARACTER_USER_ID + "=?", 
                    new String[]{String.valueOf(userId)});
            
            android.util.Log.d("CharacterDAO", "Upgrade choice level updated, rows affected: " + rowsUpdated);
            return rowsUpdated > 0;
            
        } catch (Exception e) {
            android.util.Log.e("CharacterDAO", "Error updating upgrade choice level", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    /**
     * 获取角色当前等级和之前等级，用于检测升级选择
     * @param userId 用户ID
     * @param beforeExp 添加经验前的经验值
     * @param afterExp 添加经验后的经验值
     * @return int数组：[之前等级, 当前等级]
     */
    public int[] getLevelChange(int userId, int beforeExp, int afterExp) {
        CharacterModel character = getCharacter(userId);
        if (character == null) return new int[]{1, 1};
        
        int currentLevel = character.getLevel();
        
        // 计算之前的等级（减去添加的经验）
        int expDiff = afterExp - beforeExp;
        int tempExp = character.getExp() - expDiff;
        int previousLevel = currentLevel;
        
        // 如果剩余经验不足，需要往回计算等级
        while (tempExp < 0 && previousLevel > 1) {
            tempExp += 10;
            previousLevel--;
        }
        
        return new int[]{previousLevel, currentLevel};
    }

    /**
     * 测试战斗力增长系统（开发和调试用）
     * 验证各个等级段的战斗力增长是否合理
     */
    public void testPowerGrowthSystem() {
        android.util.Log.i("CharacterDAO", PowerCalculator.getSystemInfo());
        
        // 额外的验证信息
        android.util.Log.i("CharacterDAO", "=== 升级选择功能验证 ===");
        
        // 模拟几次5级升级选择
        for (int baseLevel = 5; baseLevel <= 20; baseLevel += 5) {
            android.util.Log.i("CharacterDAO", String.format("等级%d里程碑升级选择:", baseLevel));
            
            // 稳健选择：+1级
            int safePower = PowerCalculator.calculatePowerGainForLevel(baseLevel + 1);
            android.util.Log.i("CharacterDAO", String.format("  稳健选择(+1级): +%d战斗力", safePower));
            
            // 平衡选择：+2级
            int mediumPower = PowerCalculator.calculatePowerGainForLevel(baseLevel + 1) + 
                             PowerCalculator.calculatePowerGainForLevel(baseLevel + 2);
            android.util.Log.i("CharacterDAO", String.format("  平衡选择(+2级): +%d战斗力", mediumPower));
            
            // 冒险选择：+3级
            int riskyPower = PowerCalculator.calculatePowerGainForLevel(baseLevel + 1) + 
                            PowerCalculator.calculatePowerGainForLevel(baseLevel + 2) +
                            PowerCalculator.calculatePowerGainForLevel(baseLevel + 3);
            android.util.Log.i("CharacterDAO", String.format("  冒险选择(+3级): +%d战斗力", riskyPower));
        }
        
        android.util.Log.i("CharacterDAO", "=== 测试完成 ===\n");
    }
}
