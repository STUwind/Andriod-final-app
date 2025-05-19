package com.andriodcourse.andriodfinalapp.model;

/**
 * 任务模型，封装了任务的基本属性：id、标题、类型、经验和完成状态
 */
public class Task {
    // 任务唯一标识
    private int id;
    // 任务标题
    private String title;
    // 任务类型：1=日常, 2=阶段, 3=最终
    private int type;
    // 完成任务可获得的经验值
    private int exp;
    // 任务是否完成
    private boolean completed;

    /**
     * 构造函数
     * @param id 任务 ID
     * @param title 任务标题
     * @param type 任务类型
     * @param exp 完成获得经验
     * @param completed 完成状态
     */
    public Task(int id, String title, int type, int exp, boolean completed) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.exp = exp;
        this.completed = completed;
    }

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
