package com.andriodcourse.andriodfinalapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类，包含 users、tasks 和 character 三张表。
 * image 字段改为整数类型，onUpgrade 支持版本升级到3。
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "game.db";
    private static final int    DB_VERSION = 3;  // 升级到 3

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 用户表：支持密码登录
        db.execSQL(
                "CREATE TABLE users (" +
                        "  id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  username    TEXT    NOT NULL UNIQUE," +
                        "  password    TEXT    NOT NULL," +
                        "  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ")"
        );

        // 任务表：三种类型 & 对应经验
        db.execSQL(
                "CREATE TABLE tasks (" +
                        "  id           INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "  user_id      INTEGER NOT NULL," +
                        "  title        TEXT    NOT NULL," +
                        "  type         INTEGER NOT NULL," +  // 1=日常,2=阶段,3=最终
                        "  exp          INTEGER NOT NULL," +  // 完成时获得经验：1/10/100
                        "  is_completed INTEGER DEFAULT 0," +
                        "  FOREIGN KEY(user_id) REFERENCES users(id)" +
                        ")"
        );

        // 角色表：绑定 user_id，image 字段为整型，预留图片和战斗力
        db.execSQL(
                "CREATE TABLE character (" +
                        "  user_id      INTEGER PRIMARY KEY," +
                        "  name         TEXT    NOT NULL," +
                        "  level        INTEGER NOT NULL," +
                        "  exp          INTEGER NOT NULL," +
                        "  image        INTEGER NOT NULL," +  // 存储 drawable 资源ID
                        "  combat_power INTEGER NOT NULL," +
                        "  FOREIGN KEY(user_id) REFERENCES users(id)" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // 升级到版本3：重新创建 character 表，image字段改为 INTEGER
            db.execSQL("DROP TABLE IF EXISTS character");
            db.execSQL(
                    "CREATE TABLE character (" +
                            "  user_id      INTEGER PRIMARY KEY," +
                            "  name         TEXT    NOT NULL," +
                            "  level        INTEGER NOT NULL," +
                            "  exp          INTEGER NOT NULL," +
                            "  image        INTEGER NOT NULL," +
                            "  combat_power INTEGER NOT NULL," +
                            "  FOREIGN KEY(user_id) REFERENCES users(id)" +
                            ")"
            );
        }
    }
}
