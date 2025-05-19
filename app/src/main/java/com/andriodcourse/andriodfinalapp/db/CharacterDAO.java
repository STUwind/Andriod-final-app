package com.andriodcourse.andriodfinalapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.andriodcourse.andriodfinalapp.model.CharacterModel;

/**
 * 角色数据访问对象，负责 character 表的增删查改操作
 */
public class CharacterDAO {
    private DBHelper helper;

    public CharacterDAO(Context ctx) {
        helper = new DBHelper(ctx);
    }

    /**
     * 初始化新用户角色，使用指定的角色名和头像资源
     * @param userId 用户 ID
     * @param characterName 角色名称
     * @param imageResId 头像资源 ID
     */
    public void createDefault(int userId, String characterName, int imageResId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("user_id",      userId);
        v.put("name",         characterName);
        v.put("level",        1);
        v.put("exp",          0);
        v.put("image",        imageResId);
        v.put("combat_power", 0);
        db.insert("character", null, v);
    }

    /**
     * 查询指定用户的角色信息
     * @param userId 用户 ID
     * @return 对应的 CharacterModel 或 null
     */
    public CharacterModel getCharacter(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(
                "character", null,
                "user_id=?", new String[]{ String.valueOf(userId) },
                null, null, null
        );
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        String name = c.getString(c.getColumnIndexOrThrow("name"));
        int level = c.getInt(c.getColumnIndexOrThrow("level"));
        int exp = c.getInt(c.getColumnIndexOrThrow("exp"));
        int imageRes = c.getInt(c.getColumnIndexOrThrow("image"));
        int combatPower = c.getInt(c.getColumnIndexOrThrow("combat_power"));
        c.close();
        return new CharacterModel(userId, name, level, exp, imageRes, combatPower);
    }

    /**
     * 更新角色的等级、经验、头像和战斗力
     * @param cm 包含最新状态的角色模型
     */
    public void update(CharacterModel cm) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("level",        cm.getLevel());
        v.put("exp",          cm.getExp());
        v.put("image",        cm.getImageRes());
        v.put("combat_power", cm.getCombatPower());
        db.update(
                "character", v,
                "user_id=?", new String[]{ String.valueOf(cm.getUserId()) }
        );
    }
}
