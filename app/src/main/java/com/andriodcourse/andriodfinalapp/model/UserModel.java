package com.andriodcourse.andriodfinalapp.model;

/**
 * 用户模型，包含用户 ID、用户名和密码哈希值
 */
public class UserModel {
    private int id;
    private String username;
    private String passwordHash;

    public UserModel(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
