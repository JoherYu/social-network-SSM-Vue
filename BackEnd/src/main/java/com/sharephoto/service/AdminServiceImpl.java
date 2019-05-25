package com.sharephoto.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sharephoto.dao.CommentMapper;
import com.sharephoto.dao.PhotoMapper;
import com.sharephoto.dao.TagMapper;
import com.sharephoto.dao.UserMapper;
import com.sharephoto.entity.Comment;
import com.sharephoto.entity.Photo;
import com.sharephoto.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PhotoMapper photoMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> lockUser(Integer userId){
        Integer result = userMapper.updateByUserIdToLock(userId);

        Map<String, String> message = new HashMap<String, String>();
        if (result <= 0){
            message.put("message", "用户不存在");
            message.put("type", "info");
        }else {
            message.put("message", "用户已锁定");
            message.put("type", "info");
        }

        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> unlockUser(Integer userId){
        Integer result = userMapper.updateByUserIdToUnlock(userId);

        Map<String, String> message = new HashMap<String, String>();
        if (result <= 0){
            message.put("message", "用户不存在");
            message.put("type", "info");
        }else {
            message.put("message", "用户已解锁");
            message.put("type", "info");
        }

        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> blockUser(Integer userId){
        Integer result = userMapper.updateByUserIdToBlock(userId);

        Map<String, String> message = new HashMap<String, String>();
        if (result <= 0){
            message.put("message", "用户不存在");
            message.put("type", "info");
        }else {
            message.put("message", "用户已封禁");
            message.put("type", "info");
        }

        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> unblockUser(Integer userId){
        Integer result = userMapper.updateByUserIdToUnblock(userId);

        Map<String, String> message = new HashMap<String, String>();
        if (result <= 0){
            message.put("message", "用户不存在");
            message.put("type", "info");
        }else {
            message.put("message", "用户已解封");
            message.put("type", "info");
        }

        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> deleteTag(Integer tagId){
        Integer result = tagMapper.deleteTagById(tagId);

        Map<String, String> message = new HashMap<String, String>();
        if (result <= 0){
            message.put("message", "标签不存在");
            message.put("type", "info");
        }else {
            message.put("message", "标签已删除");
            message.put("type", "info");
        }

        return message;
    }

    @Override
    public Map<String, Integer> getInfo(){

        Map<String, Integer> info = new HashMap<String, Integer>();
        info.put("userCount", userMapper.selectAllCount());
        info.put("photoCount", photoMapper.selectAllCount());
        info.put("tagCount", tagMapper.selectAllCount());
        info.put("commentCount", commentMapper.selectAllCount());
        info.put("lockedUserCount", userMapper.selectLockedCount());
        info.put("blockedUserCount", userMapper.selectBlockedCount());
        info.put("reportedPhotosCount", photoMapper.selectReportedCount());
        info.put("reportedCommentsCount", commentMapper.selectReportedCount());

        return info;
    }

    @Override
    public Map<String, Object> manageTag(Integer page) {

        PageHelper.startPage(page, 50);
        List<Map> tagsInfo = tagMapper.selectAll();
        PageInfo<Map> tagsContent = new PageInfo<Map>(tagsInfo);

        Map<String, Object> pagination = new HashMap<String, Object>();
        pagination.put("count", tagsContent.getTotal());
        pagination.put("currentPag", tagsContent.getPageNum());
        pagination.put("perPage", tagsContent.getPageSize());

        Map<String, Object> tags = new HashMap<String, Object>();
        tags.put("tags", tagsContent.getList());
        tags.put("pagination", pagination);
        tags.put("tagCount",tagsContent.getTotal());

        return tags;
    }

    @Override
    public Map<String, Object> manageUser(Integer page, String filterRule){
        PageHelper.startPage(page, 20);
        List<User> usersInfo = null;

        if (filterRule.equals("locked")){
            usersInfo = userMapper.selectAllLocked();
        }else if (filterRule.equals("blocked")){
            usersInfo = userMapper.selectAllBlocked();
        }else if (filterRule.equals("administrator")){
            usersInfo = userMapper.selectAllAdministrator();
        }else if (filterRule.equals("moderator")){
            usersInfo = userMapper.selectAllModerator();
        }else {
            usersInfo = userMapper.selectAll();
        }

        for (User user : usersInfo){
            user.setAvatarS("/avatars/" + user.getAvatarS());
        }

        PageInfo<User> usersContent = new PageInfo<User>(usersInfo);

        Map<String, Object> pagination = new HashMap<String, Object>();
        pagination.put("count", usersContent.getTotal());
        pagination.put("currentPag", usersContent.getPageNum());
        pagination.put("perPage", usersContent.getPageSize());

        Map<String, Object> users = new HashMap<String, Object>();
        users.put("users", usersContent.getList());
        users.put("pagination", pagination);
        users.put("userCount",usersContent.getTotal());

        return users;
    }

    @Override
    public Map<String, Object> managePhoto(Integer page, String orderRule){
        PageHelper.startPage(page, 12);
        List<Photo> photosInfo = null;

        if (orderRule.equals("byTime")){
            photosInfo = photoMapper.selectAllByTime();
        }else {
            photosInfo = photoMapper.selectAllByFlag();
        }

        for (Photo photo : photosInfo){
            photo.setFilenameS("/uploads/" + photo.getFilenameS());
        }

        PageInfo<Photo> photosContent = new PageInfo<Photo>(photosInfo);

        Map<String, Object> pagination = new HashMap<String, Object>();
        pagination.put("count", photosContent.getTotal());
        pagination.put("currentPag", photosContent.getPageNum());
        pagination.put("perPage", photosContent.getPageSize());

        Map<String, Object> photos = new HashMap<String, Object>();
        photos.put("photos", photosContent.getList());
        photos.put("pagination", pagination);
        photos.put("photoCount",photosContent.getTotal());

        return photos;
    }

    @Override
    public Map<String, Object> manageComment(Integer page, String orderRule){
        PageHelper.startPage(page, 15);
        List<Comment> commentsInfo = null;

        if (orderRule.equals("byTime")){
            commentsInfo = commentMapper.selectAll();
        }else {
            commentsInfo = commentMapper.selectAllByFlag();
        }

        PageInfo<Comment> commentsContent = new PageInfo<Comment>(commentsInfo);

        Map<String, Object> pagination = new HashMap<String, Object>();
        pagination.put("count", commentsContent.getTotal());
        pagination.put("currentPag", commentsContent.getPageNum());
        pagination.put("perPage", commentsContent.getPageSize());

        Map<String, Object> comments = new HashMap<String, Object>();
        comments.put("comments", commentsContent.getList());
        comments.put("pagination", pagination);
        comments.put("commentCount",commentsContent.getTotal());

        return comments;
    }

    @Override
    public Map<String, Object> getManageUserInfo(Integer userId) {
        User user = userMapper.selectAdminInfoByUserId(userId);

        Map<String, String> profileData = new HashMap<>();
        profileData.put("bio",user.getBio());
        profileData.put("website",user.getWebsite());
        profileData.put("name",user.getName());
        profileData.put("username",user.getUsername());
        profileData.put("location",user.getLocation());
        profileData.put("email",user.getEmail());

        List<String> check = new ArrayList<>();
        if (user.isConfirmed()){ check.add("confirmed"); }
        if (user.isActive()){ check.add("active"); }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("profileData", profileData);
        userInfo.put("check", check);
        userInfo.put("value", user.getRoleId());

        return userInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> updateManageUserInfo(Integer userId, Map<String ,Object> data){
        User user = new User();

        user.setId(userId);

        Map<String, String> profileData = (Map<String, String>) data.get("profileData");
        user.setBio(profileData.get("bio"));
        user.setWebsite(profileData.get("website"));
        user.setName(profileData.get("name"));
        user.setUsername(profileData.get("username"));
        user.setLocation(profileData.get("location"));
        user.setEmail(profileData.get("email"));

        Map<String, Boolean> check = (Map<String, Boolean>) data.get("check");
        user.setConfirmed(check.get("confirmed"));
        user.setActive(check.get("active"));

        Integer value = (Integer) data.get("role");
        user.setRoleId(value.byteValue());

        Integer result = userMapper.updateAdminInfoByUserId(user);
        Map<String, String> message = new HashMap<String, String>();
        if (result <= 0){
            message.put("message", "用户不存在");
            message.put("type", "info");
        }else {
            message.put("message", "资料已更新");
            message.put("type", "info");
        }

        return message;
    }
}
