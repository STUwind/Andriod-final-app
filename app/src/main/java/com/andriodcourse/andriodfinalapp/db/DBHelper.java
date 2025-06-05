package com.andriodcourse.andriodfinalapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库帮助类，管理游戏相关的三张表：用户、任务和角色
 * 
 * 版本历史：
 * v1: 初始版本
 * v2: 添加基础功能
 * v3: character表的image字段改为INTEGER类型
 * v4: 添加索引和约束优化
 * v5: 添加升级选择记录字段
 */
public class DBHelper extends SQLiteOpenHelper {
    
    // 数据库配置
    private static final String DB_NAME = "game.db";
    private static final int DB_VERSION = 5;  // 升级到版本5
    private static final String TAG = "DBHelper";
    
    // 表名常量
    public static final String TABLE_USERS = "users";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_CHARACTER = "character";
    
    // 用户表字段
    public static final String USERS_ID = "id";
    public static final String USERS_USERNAME = "username";
    public static final String USERS_PASSWORD = "password";
    public static final String USERS_CREATED_AT = "created_at";
    
    // 任务表字段
    public static final String TASKS_ID = "id";
    public static final String TASKS_USER_ID = "user_id";
    public static final String TASKS_TITLE = "title";
    public static final String TASKS_TYPE = "type";
    public static final String TASKS_EXP = "exp";
    public static final String TASKS_IS_COMPLETED = "is_completed";
    public static final String TASKS_CREATED_AT = "created_at";
    public static final String TASKS_COMPLETED_AT = "completed_at";
    
    // 角色表字段
    public static final String CHARACTER_USER_ID = "user_id";
    public static final String CHARACTER_NAME = "name";
    public static final String CHARACTER_LEVEL = "level";
    public static final String CHARACTER_EXP = "exp";
    public static final String CHARACTER_IMAGE = "image";
    public static final String CHARACTER_COMBAT_POWER = "combat_power";
    public static final String CHARACTER_UPDATED_AT = "updated_at";
    public static final String CHARACTER_LAST_UPGRADE_CHOICE_LEVEL = "last_upgrade_choice_level";  // 记录上次升级选择的等级
    
    // 任务类型常量
    public static final int TASK_TYPE_DAILY = 1;     // 日常任务
    public static final int TASK_TYPE_STAGE = 2;     // 阶段任务
    public static final int TASK_TYPE_FINAL = 3;     // 最终任务
    
    // 任务经验值常量
    public static final int EXP_DAILY = 1;
    public static final int EXP_STAGE = 10;
    public static final int EXP_FINAL = 100;
    
    // 升级选择常量
    public static final int UPGRADE_OPTION_SAFE = 1;      // 100%概率升1级
    public static final int UPGRADE_OPTION_MEDIUM = 2;    // 50%概率升2级
    public static final int UPGRADE_OPTION_RISKY = 3;     // 30%概率升3级

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createUsersTable(db);
            createTasksTable(db);
            createCharacterTable(db);
            createIndexes(db);
            Log.i(TAG, "数据库创建成功");
        } catch (Exception e) {
            Log.e(TAG, "数据库创建失败", e);
            throw e;
        }
    }
    
    /**
     * 创建用户表
     */
    private void createUsersTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_USERS + " (" +
                USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USERS_USERNAME + " TEXT NOT NULL UNIQUE COLLATE NOCASE," +
                USERS_PASSWORD + " TEXT NOT NULL," +
                USERS_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "CHECK(LENGTH(" + USERS_USERNAME + ") >= 3)," +
                "CHECK(LENGTH(" + USERS_PASSWORD + ") >= 6)" +
                ")";
        db.execSQL(sql);
    }
    
    /**
     * 创建任务表
     */
    private void createTasksTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_TASKS + " (" +
                TASKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TASKS_USER_ID + " INTEGER NOT NULL," +
                TASKS_TITLE + " TEXT NOT NULL," +
                TASKS_TYPE + " INTEGER NOT NULL," +
                TASKS_EXP + " INTEGER NOT NULL," +
                TASKS_IS_COMPLETED + " INTEGER DEFAULT 0," +
                TASKS_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                TASKS_COMPLETED_AT + " DATETIME," +
                "CHECK(" + TASKS_TYPE + " IN (1, 2, 3))," +
                "CHECK(" + TASKS_EXP + " > 0)," +
                "CHECK(" + TASKS_IS_COMPLETED + " IN (0, 1))," +
                "FOREIGN KEY(" + TASKS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(sql);
    }
    
    /**
     * 创建角色表
     */
    private void createCharacterTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_CHARACTER + " (" +
                CHARACTER_USER_ID + " INTEGER PRIMARY KEY," +
                CHARACTER_NAME + " TEXT NOT NULL," +
                CHARACTER_LEVEL + " INTEGER NOT NULL DEFAULT 1," +
                CHARACTER_EXP + " INTEGER NOT NULL DEFAULT 0," +
                CHARACTER_IMAGE + " INTEGER NOT NULL," +
                CHARACTER_COMBAT_POWER + " INTEGER NOT NULL DEFAULT 0," +
                CHARACTER_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                CHARACTER_LAST_UPGRADE_CHOICE_LEVEL + " INTEGER NOT NULL DEFAULT 0," +
                "CHECK(" + CHARACTER_LEVEL + " >= 1)," +
                "CHECK(" + CHARACTER_EXP + " >= 0)," +
                "CHECK(" + CHARACTER_COMBAT_POWER + " >= 0)," +
                "CHECK(" + CHARACTER_LAST_UPGRADE_CHOICE_LEVEL + " >= 0)," +
                "FOREIGN KEY(" + CHARACTER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(sql);
    }
    
    /**
     * 创建索引以提高查询性能
     */
    private void createIndexes(SQLiteDatabase db) {
        // 用户名索引（已有UNIQUE约束，会自动创建）
        
        // 任务相关索引
        db.execSQL("CREATE INDEX idx_tasks_user_id ON " + TABLE_TASKS + "(" + TASKS_USER_ID + ")");
        db.execSQL("CREATE INDEX idx_tasks_type ON " + TABLE_TASKS + "(" + TASKS_TYPE + ")");
        db.execSQL("CREATE INDEX idx_tasks_completed ON " + TABLE_TASKS + "(" + TASKS_IS_COMPLETED + ")");
        db.execSQL("CREATE INDEX idx_tasks_user_completed ON " + TABLE_TASKS + "(" + TASKS_USER_ID + ", " + TASKS_IS_COMPLETED + ")");
        
        // 角色表索引（PRIMARY KEY会自动创建）
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "数据库从版本 " + oldVersion + " 升级到 " + newVersion);
        
        try {
            if (oldVersion < 3) {
                upgradeToVersion3(db);
            }
            if (oldVersion < 4) {
                upgradeToVersion4(db);
            }
            if (oldVersion < 5) {
                upgradeToVersion5(db);
            }
            Log.i(TAG, "数据库升级成功");
        } catch (Exception e) {
            Log.e(TAG, "数据库升级失败", e);
            // 如果升级失败，重新创建所有表
            recreateAllTables(db);
        }
    }
    
    /**
     * 升级到版本3：修改character表的image字段为INTEGER
     */
    private void upgradeToVersion3(SQLiteDatabase db) {
        Log.i(TAG, "升级到版本3：修改character表结构");
        
        // 备份现有数据
        db.execSQL("CREATE TEMPORARY TABLE character_backup AS SELECT * FROM " + TABLE_CHARACTER);
        
        // 删除旧表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHARACTER);
        
        // 重新创建表
        createCharacterTable(db);
        
        // 恢复数据（假设旧版本的image字段可以转换为整数）
        db.execSQL("INSERT INTO " + TABLE_CHARACTER + " SELECT * FROM character_backup");
        
        // 删除备份表
        db.execSQL("DROP TABLE character_backup");
    }
    
    /**
     * 升级到版本4：添加新字段和索引
     */
    private void upgradeToVersion4(SQLiteDatabase db) {
        Log.i(TAG, "升级到版本4：添加新字段和优化");
        
        // 为tasks表添加新字段
        try {
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + TASKS_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP");
        } catch (Exception e) {
            Log.w(TAG, "字段 " + TASKS_CREATED_AT + " 可能已存在");
        }
        
        try {
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + TASKS_COMPLETED_AT + " DATETIME");
        } catch (Exception e) {
            Log.w(TAG, "字段 " + TASKS_COMPLETED_AT + " 可能已存在");
        }
        
        // 为character表添加updated_at字段
        try {
            db.execSQL("ALTER TABLE " + TABLE_CHARACTER + " ADD COLUMN " + CHARACTER_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP");
        } catch (Exception e) {
            Log.w(TAG, "字段 " + CHARACTER_UPDATED_AT + " 可能已存在");
        }
        
        // 创建索引
        createIndexes(db);
    }
    
    /**
     * 升级到版本5：添加升级选择记录字段
     */
    private void upgradeToVersion5(SQLiteDatabase db) {
        Log.i(TAG, "升级到版本5：添加升级选择功能");
        
        // 为character表添加last_upgrade_choice_level字段
        try {
            db.execSQL("ALTER TABLE " + TABLE_CHARACTER + " ADD COLUMN " + CHARACTER_LAST_UPGRADE_CHOICE_LEVEL + " INTEGER NOT NULL DEFAULT 0");
        } catch (Exception e) {
            Log.w(TAG, "字段 " + CHARACTER_LAST_UPGRADE_CHOICE_LEVEL + " 可能已存在");
        }
    }
    
    /**
     * 重新创建所有表（当升级失败时使用）
     */
    private void recreateAllTables(SQLiteDatabase db) {
        Log.w(TAG, "重新创建所有表");
        
        // 删除所有表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHARACTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        
        // 重新创建
        onCreate(db);
    }
    
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // 启用外键约束
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    
    /**
     * 获取经验值根据任务类型
     */
    public static int getExpByTaskType(int taskType) {
        switch (taskType) {
            case TASK_TYPE_DAILY:
                return EXP_DAILY;
            case TASK_TYPE_STAGE:
                return EXP_STAGE;
            case TASK_TYPE_FINAL:
                return EXP_FINAL;
            default:
                return 0;
        }
    }
    
    /**
     * 获取任务类型描述
     */
    public static String getTaskTypeDescription(int taskType) {
        switch (taskType) {
            case TASK_TYPE_DAILY:
                return "日常任务";
            case TASK_TYPE_STAGE:
                return "阶段任务";
            case TASK_TYPE_FINAL:
                return "最终任务";
            default:
                return "未知类型";
        }
    }
    
    /**
     * 获取升级选择描述
     */
    public static String getUpgradeOptionDescription(int option) {
        switch (option) {
            case UPGRADE_OPTION_SAFE:
                return "稳健选择：100%概率升1级";
            case UPGRADE_OPTION_MEDIUM:
                return "平衡选择：50%概率升2级";
            case UPGRADE_OPTION_RISKY:
                return "冒险选择：30%概率升3级";
            default:
                return "未知选择";
        }
    }
}
