package com.sharephoto.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * @author Joher
 * @data 2019/5/20
 **/

public interface UserService {

    // 上传头像
    Map<String, String> uploadAvatar(MultipartFile file, HttpSession session);

    // 剪裁头像
    Map<String, String> cropAvatar(Map<String, Object> data, HttpSession session) throws IOException;

    // 注销账户
    Map<String, String> deleteAccount(String username);

    // 获取用户主页信息
    Map<String, Object> getUserIndex(String username, Integer page, HttpSession session);

    // 获取用户主页收藏信息
    Map<String, Object> getUserIndexCollection(String username, Integer page, HttpSession session);

    // 获取用户主页粉丝信息
    Map<String, Object> getUserIndexFollowers(String username, Integer page, HttpSession session);

    // 获取用户主页关注信息
    Map<String, Object> getUserIndexFollowings(String username, Integer page, HttpSession session);

    // 获取用户简介设置
    Map<String, String> getEditProfile();

    // 更新用户简介设置
    Map<String, String> updateEditProfile(Map<String, String> data, HttpSession session);

    // 获取用户消息设置
    Map<String, Object> getNotificationSetting();

    // 更新用户消息设置
    Map<String, Object> updateNotificationSetting(Map<String, Boolean> data);

    // 获取用户隐私设置
    Map<String, Object> getPrivacySetting();

    // 更新用户隐私设置
    Map<String, Object> updatePrivacySetting(Map<String, Boolean> data);

    // 更新密码
    Map<String, String> updatePassword(Map<String, String> data, HttpSession session);

    // 更改邮箱地址
    Map<String, String> updateEmail(Map<String, String> data, HttpServletRequest request) throws Exception;

    // 更新邮箱地址
    String emailConfirm(String token, HttpServletRequest request) throws Exception;
}
