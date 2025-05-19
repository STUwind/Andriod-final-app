package com.andriodcourse.andriodfinalapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.andriodcourse.andriodfinalapp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private DBHelper helper;

    public UserDAO(Context ctx) {
        helper = new DBHelper(ctx);
    }

    /** 根据用户名查询用户（不校验密码） */
    public UserModel getByName(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(
                "users",
                new String[]{"id", "username", "password"},
                "username = ?",
                new String[]{username},
                null, null, null
        );
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        int id   = c.getInt(c.getColumnIndexOrThrow("id"));
        String name = c.getString(c.getColumnIndexOrThrow("username"));
        String pwd  = c.getString(c.getColumnIndexOrThrow("password"));
        c.close();
        return new UserModel(id, name, pwd);
    }

    /** 创建新用户，返回新插入的用户ID */
    public long add(String username, String passwordHash) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", passwordHash);
        return db.insert("users", null, cv);
    }

    /**
     * 用户登录验证：根据用户名和密码哈希查询用户
     * @return 登录成功时返回对应 UserModel，失败返回 null
     */
    public UserModel login(String username, String passwordHash) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(
                "users",
                new String[]{"id", "username"},
                "username = ? AND password = ?",
                new String[]{username, passwordHash},
                null, null, null
        );
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        int id   = c.getInt(c.getColumnIndexOrThrow("id"));
        String name = c.getString(c.getColumnIndexOrThrow("username"));
        c.close();
        return new UserModel(id, name, passwordHash);
    }

    /** 查询所有用户数据 */
    public List<UserModel> getAllUsers() {
        List<UserModel> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, username, password FROM users", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String username = c.getString(1);
            String pwdHash = c.getString(2);
            list.add(new UserModel(id, username, pwdHash));
        }
        c.close();
        return list;
    }
}
