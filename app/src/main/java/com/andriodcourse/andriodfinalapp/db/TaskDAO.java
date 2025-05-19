package com.andriodcourse.andriodfinalapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.andriodcourse.andriodfinalapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private DBHelper helper;
    public TaskDAO(Context ctx) { helper = new DBHelper(ctx); }

    /** 查询当前用户所有任务 */
    public List<Task> getAll(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(
                "tasks", null,
                "user_id=?",
                new String[]{ String.valueOf(userId) },
                null, null, "id ASC"
        );
        List<Task> list = new ArrayList<>();
        while (c.moveToNext()) {
            int id    = c.getInt(c.getColumnIndexOrThrow("id"));
            String title = c.getString(c.getColumnIndexOrThrow("title"));
            int type = c.getInt(c.getColumnIndexOrThrow("type"));
            int exp  = c.getInt(c.getColumnIndexOrThrow("exp"));
            boolean done = c.getInt(c.getColumnIndexOrThrow("is_completed")) == 1;
            list.add(new Task(id, title, type, exp, done));
        }
        c.close();
        return list;
    }

    /** 新增任务，需要传类型，内部会根据 type 赋 exp */
    public void add(int userId, String title, int type) {
        int gainExp;
        switch (type) {
            case 1:
                gainExp = 1;   // 日常
                break;
            case 2:
                gainExp = 10;  // 阶段
                break;
            case 3:
                gainExp = 100; // 最终
                break;
            default:
                gainExp = 0;   // 测试时可用
        }

        ContentValues v = new ContentValues();
        v.put("user_id", userId);
        v.put("title",   title);
        v.put("type",    type);
        v.put("exp",     gainExp);
        helper.getWritableDatabase().insert("tasks", null, v);
    }


    /** 标记完成，不在这里做经验结算 */
    public void complete(int taskId) {
        ContentValues v = new ContentValues();
        v.put("is_completed", 1);
        helper.getWritableDatabase()
                .update("tasks", v, "id=?", new String[]{ String.valueOf(taskId) });
    }
}
