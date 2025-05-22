package com.andriodcourse.andriodfinalapp.model;

public class Task {
    private int id;
    private int userId; // 新增，便于多用户管理
    private String title;
    private int type; // 1=日常, 2=阶段, 3=最终
    private int exp;
    private boolean isCompleted;

    public Task(int id, int userId, String title, int type, int exp, boolean isCompleted) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.exp = exp;
        this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public boolean isCompleted() { return isCompleted; }
    public void setIsCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
}
