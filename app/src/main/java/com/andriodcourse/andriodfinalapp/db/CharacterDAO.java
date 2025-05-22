package com.andriodcourse.andriodfinalapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.andriodcourse.andriodfinalapp.R;
import com.andriodcourse.andriodfinalapp.model.CharacterModel;

public class CharacterDAO {
    private DBHelper helper;

    public CharacterDAO(Context context) {
        helper = new DBHelper(context);
    }

    public void createDefault(int userId, String name, int imageResId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("name", name);
        values.put("level", 1);
        values.put("exp", 0);
        values.put("image", imageResId);
        values.put("combat_power", 0);
        db.insert("character", null, values);
        db.close();
    }

    public CharacterModel getCharacter(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("character", null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return null;
        }
        CharacterModel cm = new CharacterModel(
                userId,
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getInt(cursor.getColumnIndexOrThrow("level")),
                cursor.getInt(cursor.getColumnIndexOrThrow("exp")),
                cursor.getInt(cursor.getColumnIndexOrThrow("image")),
                cursor.getInt(cursor.getColumnIndexOrThrow("combat_power"))
        );
        cursor.close();
        db.close();
        return cm;
    }

    public void addExp(int userId, int addExp) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("character", null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            createDefault(userId, "角色", R.drawable.avatar_default);
            cursor = db.query("character", null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (!cursor.moveToFirst()) {
                cursor.close();
                db.close();
                return;
            }
        }
        int currentExp = cursor.getInt(cursor.getColumnIndexOrThrow("exp"));
        int level = cursor.getInt(cursor.getColumnIndexOrThrow("level"));
        int power = cursor.getInt(cursor.getColumnIndexOrThrow("combat_power"));
        cursor.close();

        int totalExp = currentExp + addExp;
        while (totalExp >= 10) {
            totalExp -= 10;
            level++;
            power += Math.abs(Math.pow(2, level) - (int)(Math.random() * 10 + 1));
        }
        ContentValues values = new ContentValues();
        values.put("exp", totalExp);
        values.put("level", level);
        values.put("combat_power", power);
        db.update("character", values, "user_id=?", new String[]{String.valueOf(userId)});
        db.close();
    }
}
