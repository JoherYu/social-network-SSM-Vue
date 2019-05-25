package com.sharephoto.service;

import java.util.Map;

public interface AdminService {

    // 锁定用户
    Map<String, String> lockUser(Integer userId);

    // 解锁用户
    Map<String, String>unlockUser(Integer userId);

    // 封禁用户
    Map<String, String> blockUser(Integer userId);

    // 解封用户
    Map<String, String> unblockUser(Integer userId);

    // 删除单个标签
    Map<String, String> deleteTag(Integer tagId);

    // 获取管理主页数据
    Map<String, Integer> getInfo();

    // 标签数据
    Map<String, Object> manageTag(Integer page);

    // 管理用户数据
    Map<String, Object> manageUser(Integer page, String filterRule);

    // 管理相片数据
    Map<String, Object> managePhoto(Integer page, String orderRule);

    // 管理评论数据
    Map<String, Object> manageComment(Integer page, String orderRule);

    // 获取（管理员）用户信息
    Map<String, Object> getManageUserInfo(Integer userId);

    // 更新（管理员）用户信息
    Map<String, String> updateManageUserInfo(Integer userId, Map<String ,Object> data);
}
