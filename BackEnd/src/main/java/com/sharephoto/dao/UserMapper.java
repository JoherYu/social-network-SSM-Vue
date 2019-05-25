package com.sharephoto.dao;

import com.sharephoto.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    // 根据用户ID锁定用户
    Integer updateByUserIdToLock(Integer UserId);

    // 根据用户ID解锁用户
    Integer updateByUserIdToUnlock(Integer UserId);

    // 根据用户ID封禁用户
    Integer updateByUserIdToBlock(Integer UserId);

    // 根据用户ID解封用户
    Integer updateByUserIdToUnblock(Integer UserId);

    // 查询用户总数
    Integer selectAllCount();

    // 查询锁定用户数
    Integer selectLockedCount();

    // 查询封禁用户数
    Integer selectBlockedCount();

    // 查询所有用户
    List<User> selectAll();

    // 查询所有被锁定用户
    List<User> selectAllLocked();

    // 查询所有被封禁用户
    List<User> selectAllBlocked();

    // 查询所有管理员
    List<User> selectAllAdministrator();

    // 查询所有协管员
    List<User> selectAllModerator();

    // 查询用户上传图片数
    Integer selectCountByUserId();

    // （管理员）查询用户信息
    User selectAdminInfoByUserId(Integer userId);

    // （管理员）更新用户信息
    Integer updateAdminInfoByUserId(User user);

    // 根据电子邮箱查询用户名
    String selectUsernameByEmail(String email);

    // 查询用户（登录验证）信息
    User selectLoginVerificationByUsername(String username);

    // 新增用户
    Integer insertUser(User user);

    // 查询用户（登录返回）信息
    User selectLoginUserInfoByEmail(String email);

    // 查询当前用户Id
    Integer selectUserIdByUsername(String username);

    // 查询用户未读消息数量
    Integer selectNotificationById(Integer userId);

    // 查询用户是否具有moderate权限
    boolean selectCanModerate(Integer userId);

    // 查询用户是否具有comment权限
    boolean selectCanComment(Integer userId);

    // 查询用户是否是Admin权限
    boolean selectIsAdmin(Integer userId);

    // 根据关键字之查询用户
    List<User> selectUserByKey(@Param("q") String q, @Param("currentUserId") Integer currentUserId);

    // 根据用户图片作者查询用户
    User selectUserById(@Param("author_id") Integer author_id, @Param("currentUserId") Integer currentUserId);

    // 当前用户是否关注此用户
    boolean selectUserIsFollowBy(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);

    // 当前用户是否被此用户关注
    boolean selectUserIsFollowing(@Param("currentUserId") Integer currentUserId, @Param("id") Integer id);

    // 查询用户角色
    String selectRoleByUsername(String username);

    // 查询角色权限
    List<String> selectPermissions(String roleName);

    // 当前用户是否confirm
    boolean selectConfirmed(Integer currentUserId);

    // 查询弹窗信息
    User selectPopupData(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);

    // 根据评论查询弹窗信息
    User selectPopupDataByComment(@Param("author_id") Integer author_id, @Param("currentUserId") Integer currentUserId);

    // 查询用户关注通知设置信息
    boolean selectFollowNotificationSettings(Integer id);

    // 查询用户评论通知设置信息
    boolean selectCommentNotificationSettings(Integer id);

    // 根据用户id查询用户name
    String selectNameById(Integer id);

    // 根据用户id查询用户邮件
    String selectEmailById(Integer id);

    // 根据图片id查询收藏者
    List<User> selectUserByPhotoId(@Param("currentUserId") Integer currentUserId, @Param("photoId") Integer PhotoId);

    // 更新头像
    Integer updateAvatar(@Param("avatarRaw") String avatarRaw, @Param("id") Integer id);

    // 获取原始头像
    String selectRaw(Integer id);

    // 更新头像缩略图
    Integer updateCropAvatar(@Param("id") Integer id, @Param("avatarS") String avatarS, @Param("avatarM") String avatarM, @Param("avatarL") String avatarL);

    // 更新用户确认信息
    Integer updateConfirmById(Integer id);

    // 删除用户
    Integer deleteUserByUsername(String username);

    // 获取用户详细信息
    User selectByUserusername(@Param("username") String username, @Param("currentUserId") Integer currentUserId);

    // 查询用户所有粉丝
    List<User> selectFollowers(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);

    // 查询用户所有关注
    List<User> selectFollowing(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);

    // 查询用户是否被封禁
    boolean selectActive(String username);

    // 查询用户是否被锁定
    boolean selectLocked(String username);

    // 查询当前用户简介
    User selectProfileSetting(String username);

    // 更新当前用户简介
    Integer updateProfileSetting(User user);

    // 查询当前用户消息设置
    User selectNotificationSetting(String username);

    // 更新当前用户消息设置
    Integer updateNotificationSetting(User user);

    // 查询隐私设置
    boolean selectPrivacySetting(String username);

    // 更新隐私设置
    Integer updatePrivacySetting(@Param("username") String username, @Param("publicCollections") boolean publicCollections);

    // 更新密码
    Integer updatePassword(@Param("username") String username, @Param("password") String password);

    // 查询用户密码
    String selectPassword(String username);

    // 更换用户邮箱
    Integer updateEmail(@Param("id") Integer id, @Param("email") String email);

    // 邮箱是否已被占用
    boolean selectEmail(String email);

    // 查询初始用户名
    String selectOriUsernaem(String username);

    // 查询用户所有尺寸头像
    User selectAvatars(Integer id);

    // 生成虚拟用户
    Integer insertFake(User user);

    // 为虚拟用户添加RAW头像
    Integer updateAvatarRaw(@Param("avatarRaw") String avatarRaw, @Param("id") Integer id);

}