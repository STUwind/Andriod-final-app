package com.andriodcourse.andriodfinalapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 安全工具类，提供密码哈希功能
 */
public class SecurityUtil {
    /**
     * 对输入字符串进行 SHA-256 哈希，并返回十六进制字符串
     * @param input 明文字符串
     * @return 哈希后的十六进制字符串
     */
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("SHA-256 加密失败", e);
        }
    }
}
