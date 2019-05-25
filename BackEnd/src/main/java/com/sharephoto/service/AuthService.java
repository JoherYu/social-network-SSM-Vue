package com.sharephoto.service;

import com.sharephoto.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AuthService {

    // 添加新用户
    Map<String, String> addUser(Map<String, Object> data, HttpServletRequest request) throws Exception;

    // 内部登录方法
    void login(User user, boolean rememberMe);

    // 用户登录
    Map<String, ?> login(Map<String ,Object> data);

    // 邮箱确认
    String confirm(String token, HttpServletRequest request) throws Exception;

    // 发送密码重置邮件
    Map<String, String> forgetPassword(Map<String, String> data, HttpServletRequest request) throws Exception;

    // 重置密码
    Map<String, String> resetPassword(Map<String, Object> data) throws Exception;
}
