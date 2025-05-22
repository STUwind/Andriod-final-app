package com.andriodcourse.andriodfinalapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andriodcourse.andriodfinalapp.R;
import com.andriodcourse.andriodfinalapp.model.CharacterModel;
import com.andriodcourse.andriodfinalapp.db.DBHelper;

/**
 * 角色数据访问对象，负责 character 表的增删查改操作
 */
public class CharacterDAO {
    private DBHelper helper;  // 数据库帮助类实例

    public CharacterDAO(Context ctx) {
        helper = new DBHelper(ctx);
    }

    /**
     * 初始化新用户角色，确保用户首次使用时有初始数据
     */
    public void createDefault(int userId, String characterName, int imageResId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("user_id", userId);
        v.put("name", characterName);
        v.put("level", 1);
        v.put("exp", 0);
        v.put("image", imageResId);
        v.put("combat_power", 0);
        db.insert("character", null, v);
    }

    /**
     * 查询角色信息
     */
    public CharacterModel getCharacter(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("character", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        if (!c.moveToFirst()) {
            c.close();  // 若无记录则关闭并返回null
            return null;
        }
        CharacterModel cm = new CharacterModel(
                userId,
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getInt(c.getColumnIndexOrThrow("level")),
                c.getInt(c.getColumnIndexOrThrow("exp")),
                c.getInt(c.getColumnIndexOrThrow("image")),
                c.getInt(c.getColumnIndexOrThrow("combat_power"))
        );
        c.close();
        return cm;
    }

    /**
     * 为角色增加经验，若无旧记录则先插入默认记录后再退出
     */
    public void addExp(int userId, int addExp) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query("character", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        if (!c.moveToFirst()) {
            c.close();
            // 若无记录，则创建默认角色（默认名从资源取）
            createDefault(userId, "角色", R.drawable.avatar_default);
            return;
        }
        // 已有记录则累加经验并可能升级
        int currentExp = c.getInt(c.getColumnIndexOrThrow("exp"));
        int level = c.getInt(c.getColumnIndexOrThrow("level"));
        int power = c.getInt(c.getColumnIndexOrThrow("combat_power"));
        c.close();

        int totalExp = currentExp + addExp;  // 累积经验
        while (totalExp >= 10) {
            totalExp -= 10;
            level++;
            power += Math.pow(2, level) - (int)(Math.random() * 10 + 1);
        }
        // 更新数据库
        ContentValues v = new ContentValues();
        v.put("exp", totalExp);
        v.put("level", level);
        v.put("combat_power", power);
        db.update("character", v, "user_id=?", new String[]{String.valueOf(userId)});
    }
}


