package com.sharephoto.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sharephoto.dao.*;
import com.sharephoto.entity.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

@Service("mainService")
public class MainServiceImpl implements MainService {

    @Autowired
    private PhotoMapper photoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TaggingMapper taggingMapper;

    @Override
    public List<Photo> getRandomPhoto(){
        List<Photo> photos = photoMapper.selectRandom();
        for (Photo photo:photos){
            photo.setFilenameS("/uploads/" + photo.getFilenameS());
        }
        return photos;
    }

    @Override
    public Map<String, ?> search(String category, String q, Integer page) {
        if (q.equals("")){
            Map<String,String> message = new HashMap<>();
            message.put("message", "请输入相片、用户名或标签关键词");
            message.put("type", "warning");
            return message;
        }

        List<?> result = null;
        if (category.equals("user")){
            String currentUsername =(String)  SecurityUtils.getSubject().getPrincipal();
            Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
            if (currentUserId == null){
                Map<String,String> message = new HashMap<>();
                message.put("message", "请先登录");
                message.put("type", "warning");
                return message;
            }
            PageHelper.startPage(page,20);
            List<User> users = userMapper.selectUserByKey(q, currentUserId);
            for (User user : users){
                user.setAvatarM("/avatars/" + user.getAvatarM());
            }
            result = users;
        }else if (category.equals("tag")){
            PageHelper.startPage(page,20);
            result = tagMapper.selectTagByKey(q);
        }else {
            PageHelper.startPage(page,20);
            List<Photo> photos = photoMapper.selectPhotoByKey(q);
            for (Photo photo : photos){
                photo.setFilenameS("/uploads/" + photo.getFilenameS());
            }
            result = photos;
        }
        PageInfo<?> resultContent = new PageInfo<>(result);

        Map<String, Object> pagination = new HashMap<String, Object>();
        pagination.put("count", resultContent.getTotal());
        pagination.put("currentPage", resultContent.getPageNum());
        pagination.put("perPage", resultContent.getPageSize());
        pagination.put("items", result);

        return pagination;
    }

    @Override
    public Map<String, ?> getSelfCenter(Integer page) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isRemembered() || currentUser.isAuthenticated()) {
            String currentUsername =(String)  currentUser.getPrincipal();
            Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

            PageHelper.startPage(page,12);
            List<Photo> photos = photoMapper.selectCenterPhotos(currentUserId);
            for (Photo photo : photos) {
                photo.setFilenameM("/uploads/" + photo.getFilenameM());
                if (photo.getAuthor().getAvatarM().indexOf("/avatars/") != 0){
                    photo.getAuthor().setAvatarM("/avatars/" + photo.getAuthor().getAvatarM());
                }
            }
            PageInfo<Photo> photosContent = new PageInfo<>(photos);

            List<Tag> tags = tagMapper.selectHotTags();

            Map<String, Object> pagination = new HashMap<>();
            pagination.put("count", photosContent.getTotal());
            pagination.put("currentPage", photosContent.getPageNum());
            pagination.put("perPage", photosContent.getPageSize());

            Map<String, Object> result = new HashMap<>();
            result.put("photos", photosContent.getList());
            result.put("photoCount", photosContent.getTotal());
            result.put("tags", tags);
            result.put("pagination", pagination);

            return result;
        }else {
            Map<String, String> result = new HashMap<>();
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }

    }

    @Override
    @Transactional
    public Map<String, String> collectPhoto(Integer photoId) {
        Map<String, String> message = new HashMap<>();
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        if (!(currentUser.isRemembered() || currentUser.isAuthenticated())) {
            message.put("message", "请先登录");
            message.put("type", "info");
            return message;
        }
        if (!userMapper.selectConfirmed(currentUserId)) {
            message.put("message", "请先登录邮箱，激活帐户");
            message.put("type", "info");
            return message;
        }
        if (!currentUser.isPermitted("COLLECT")){
            message.put("message", "对不起，您没有此权限");
            message.put("type", "warning");
            return message;
        }
        if (collectMapper.selectcollected(photoId, currentUserId)){
            message.put("message", "您已收藏过此图片");
            message.put("type", "info");
            return message;
        }

        try {
            collectMapper.insert(photoId, currentUserId);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            message.put("message", "缺少必要的参数");
            message.put("type", "info");
            return message;
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "该图片不存在");
            message.put("type", "info");
            return message;
        }

        User user = photoMapper.selectAuthorInfoByPhotoId(photoId);

        if (!currentUsername.equals(user.getUsername()) && user.isReceiveCollectNotification()){
            String messageContent = String.format("用户 <a href=\"%s\">%s</a> 收藏了你的 <a href=\"%s\">相片</a>", "/main/user/" + user.getUsername(), user.getUsername(), "/main/photo" + photoId);
            notificationMapper.insert(messageContent, user.getId());
        }

        message.put("message", "图片收藏成功");
        message.put("type", "success");
        return message;
    }

    @Override
    @Transactional
    public Map<String, String> uncollectPhoto(Integer photoId) {
        Map<String, String> message = new HashMap<>();
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        if (!(currentUser.isRemembered() || currentUser.isAuthenticated())) {
            message.put("message", "请先登录");
            message.put("type", "info");
            return message;
        }
        if (!collectMapper.selectcollected(photoId, currentUserId)){
            message.put("message", "您还未收藏过此图片");
            message.put("type", "info");
            return message;
        }

        try {
            collectMapper.delete(photoId, currentUserId);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            message.put("message", "缺少必要的参数");
            message.put("type", "info");
            return message;
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "该图片不存在");
            message.put("type", "info");
            return message;
        }

        message.put("message", "成功取消收藏");
        message.put("type", "success");
        return message;
    }

    @Override
    public Object[] getPopupData(Integer userId) {
        Object[] result = new Object[1];
        Map<String, String> message = new HashMap<>();
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        if (!(currentUser.isRemembered() || currentUser.isAuthenticated())) {
            message.put("message", "请先登录");
            message.put("type", "info");
            result[0] = message;
            return result;
        }
        try {
            User user = userMapper.selectPopupData(userId, currentUserId);
            user.setAvatarM("/avatars/" + user.getAvatarM());
            user.setFollowerCount(user.getFollowerCount() - 1);
            result[0] = user;
            return result;
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            message.put("message", "缺少必要的参数");
            message.put("type", "info");
            result[0] = message;
            return result;
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "该用户不存在");
            message.put("type", "info");
            result[0] = message;
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, String> followUser(String username) {
        Map<String, String> message = new HashMap<>();
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        if (!(currentUser.isRemembered() || currentUser.isAuthenticated())) {
            message.put("message", "请先登录");
            message.put("type", "info");
            return message;
        }
        if (!userMapper.selectConfirmed(currentUserId)) {
            message.put("message", "请先登录邮箱，激活帐户");
            message.put("type", "info");
            return message;
        }
        if (!currentUser.isPermitted("FOLLOW")){
            message.put("message", "对不起，您没有此权限");
            message.put("type", "warning");
            return message;
        }

        Integer userId = userMapper.selectUserIdByUsername(username);

        if (userMapper.selectUserIsFollowBy(userId, currentUserId)){
            message.put("message", "您已关注过此用户");
            message.put("type", "info");
            return message;
        }

        try {
            followMapper.insert(userId, currentUserId);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            message.put("message", "缺少必要的参数");
            message.put("type", "info");
            return message;
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "该用户不存在");
            message.put("type", "info");
            return message;
        }

        if ( userMapper.selectFollowNotificationSettings(userId)){
            String messageContent = String.format("用户 <a href=\"%s\">%s</a> 关注了你", "/main/user/" + currentUsername, currentUsername);
            notificationMapper.insert(messageContent, userId);
        }

        message.put("message", "关注成功");
        message.put("type", "success");
        return message;
    }

    @Override
    @Transactional
    public Map<String, String> unfollowUser(String username) {
        Map<String, String> message = new HashMap<>();
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        Integer userId = userMapper.selectUserIdByUsername(username);

        if (!(currentUser.isRemembered() || currentUser.isAuthenticated())) {
            message.put("message", "请先登录");
            message.put("type", "info");
            return message;
        }
        if (!userMapper.selectUserIsFollowBy(userId, currentUserId)){
            message.put("message", "您还未关注过此用户");
            message.put("type", "info");
            return message;
        }

        try {
            followMapper.delete(userId, currentUserId);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            message.put("message", "缺少必要的参数");
            message.put("type", "info");
            return message;
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "该用户不存在");
            message.put("type", "info");
            return message;
        }

        message.put("message", "成功取消关注");
        message.put("type", "success");
        return message;
    }

    @Override
    public Map<String, ?> getPhotoIndex(Integer id, Integer page) {
        Map<String, String> message = new HashMap<>();
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        if (!(currentUser.isRemembered() || currentUser.isAuthenticated())) {
            message.put("message", "请先登录");
            message.put("type", "info");
            return message;
        }

        Photo photo = photoMapper.selectPhotoDetail(id, currentUserId);
        photo.setFilename("/uploads/" + photo.getFilename());
        photo.setFilenameM("/uploads/" + photo.getFilenameM());
        photo.getAuthor().setAvatarM("/avatars/" + photo.getAuthor().getAvatarM());

        PageHelper.startPage(page, 15);
        List<Comment> comments = commentMapper.selectCommentByPhotoId(id, currentUserId);
        for (Comment comment : comments) {
            if (comment.getAuthor().getAvatarM().indexOf("/avatars/") != 0) {
                comment.getAuthor().setAvatarM("/avatars/" + comment.getAuthor().getAvatarM());
                comment.getAuthor().setFollowerCount(comment.getAuthor().getFollowerCount() - 1);
            }
            if (!(comment.getRepliedId() == null)){
                Integer authorId = commentMapper.selectAuthorIdById(comment.getRepliedId());
                comment.setRepliedName(userMapper.selectNameById(authorId));
            }
        }
        PageInfo<Comment> commentsContent = new PageInfo<>(comments);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("count", commentsContent.getTotal());
        pagination.put("currentPage", commentsContent.getPageNum());
        pagination.put("perPage", commentsContent.getPageSize());
        pagination.put("lastPage", commentsContent.getLastPage());


        Map<String, Object> result = new HashMap<>();
        result.put("photo", photo);
        result.put("comments", commentsContent.getList());
        result.put("pagination", pagination);

        return result;
    }

    @Override
    @Transactional
    public Map<String, ?> deletePhoto(Integer photoId) {
        Map<String, Object> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer photoAuthorId = photoMapper.selectAuthorId(photoId);

        if (!currentUserId.equals(photoAuthorId) && !currentUser.isPermitted("MODERATE")){
            message.put("message", "对不起，您没有此操作权限");
            message.put("type", "warning");
            return message;
        }

        Photo photo = photoMapper.selectFilenamesById(photoId);
        photoMapper.deleteByPrimaryKey(photoId);
        List<String> filenames = new ArrayList<>();
        filenames.add(photo.getFilename());
        filenames.add(photo.getFilenameS());
        filenames.add(photo.getFilenameM());

        for (String filename : filenames) {
            File file = new File(System.getProperty("joher") + "/uploads/" + filename);
            if (!file.exists()) {
                System.out.println("删除文件失败:" + filename + "不存在！");
            } else {
                file.delete();
                System.out.println("成功删除文件" + filename);
            }
        }

        Integer next = photoMapper.selectNextPhotoId(photoId, photoAuthorId);
        if (next == null){
            Integer pre = photoMapper.selectPrePhotoId(photoId, photoAuthorId);
            if (pre == null){
                message.put("photoId", 0);
                return message;
            }
            message.put("message", "相片已删除");
            message.put("type", "success");
            message.put("photoId", pre);
            return message;
        }
        message.put("message", "相片已删除");
        message.put("type", "success");
        message.put("photoId", next);
        return message;
    }

    @Override
    public Map<String, ?> nextPhoto(Integer photoId) {
        Map<String, Object> message = new HashMap<>();
        Integer photoAuthorId = photoMapper.selectAuthorId(photoId);
        Integer next = photoMapper.selectNextPhotoId(photoId, photoAuthorId);
        if (next == null){
            message.put("message", "已经是最后一张");
            message.put("type", "info");
            return message;
        }
        message.put("photoId", next);
        return message;
    }

    @Override
    public Map<String, ?> prePhoto(Integer photoId) {
        Map<String, Object> message = new HashMap<>();
        Integer photoAuthorId = photoMapper.selectAuthorId(photoId);
        Integer pre = photoMapper.selectPrePhotoId(photoId, photoAuthorId);
        if (pre == null){
            message.put("message", "已经是第一张");
            message.put("type", "info");
            return message;
        }
        message.put("photoId", pre);
        return message;
    }

    @Override
    @Transactional
    public Map<String, String> reportPhoto(Integer photoId) {
        Map<String, String> message = new HashMap<>();
        Integer result = photoMapper.updateReport(photoId);
        if (result.equals(0)){
            message.put("message", "图片不存在");
            message.put("type", "info");
            return message;
        }
        message.put("message", "您的举报请求已受理");
        message.put("type", "info");
        return message;
    }

    @Override
    @Transactional
    public Map<String, String> editPhotoDescription(Integer photoId, String description) {
        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer photoAuthorId = photoMapper.selectAuthorId(photoId);

        if (!currentUserId.equals(photoAuthorId) && !currentUser.isPermitted("MODERATE")){
            message.put("message", "对不起，您没有此操作权限");
            message.put("type", "warning");
            return message;
        }

        Integer result = photoMapper.updateDescription(photoId, description);
        if (result.equals(0)){
            message.put("message", "图片不存在");
            message.put("type", "info");
            return message;
        }

        message.put("message", "图片描述已更新");
        message.put("type", "success");
        message.put("description", description);
        return message;
    }

    @Override
    @Transactional
    public Map<String, Object> addTags(Integer photoId, String tagString) {
        Map<String, Object> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer photoAuthorId = photoMapper.selectAuthorId(photoId);

        if (!currentUserId.equals(photoAuthorId) && !currentUser.isPermitted("MODERATE")){
            message.put("message", "对不起，您没有此操作权限");
            message.put("type", "warning");
            return message;
        }

        String[] tags = tagString.split(" ");
        for (String tag : tags) {
            Integer tagId = tagMapper.selectIdByName(tag);
            if (tagId == null){
                try {
                    tagMapper.insert(tag);
                } catch (DuplicateKeyException e) {
                    e.printStackTrace();
                    message.put("message", "该标签已存在");
                    message.put("type", "info");
                    return message;
                } catch (DataIntegrityViolationException e) {
                    e.printStackTrace();
                    message.put("message", "请输入必选项");
                    message.put("type", "info");
                    return message;
                }
            }
            Integer id = tagMapper.selectIdByName(tag);
            Tagging tagging = new Tagging();
            tagging.setPhotoId(photoId);
            tagging.setTagId(id);
            if (!taggingMapper.selectRecord(tagging)){
                try {
                    taggingMapper.insert(tagging);
                } catch (DuplicateKeyException e) {
                    e.printStackTrace();
                    message.put("message", "该标签已存在");
                    message.put("type", "info");
                    return message;
                } catch (DataIntegrityViolationException e) {
                    e.printStackTrace();
                    message.put("message", "请输入必选项");
                    message.put("type", "info");
                    return message;
                }
            }
        }
        List<Tag> photoTags = tagMapper.selectTagByPhotoId(photoId);
        message.put("tags", photoTags);
        return message;


    }

    @Override
    @Transactional
    public Map<String, Object> deleteTags(Integer photoId, Integer tagId) {
        Map<String, Object> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer photoAuthorId = photoMapper.selectAuthorId(photoId);

        if (!currentUserId.equals(photoAuthorId) && !currentUser.isPermitted("MODERATE")){
            message.put("message", "对不起，您没有此操作权限");
            message.put("type", "warning");
            return message;
        }

        Tagging tagging = new Tagging();
        tagging.setPhotoId(photoId);
        tagging.setTagId(tagId);
        Integer result = null;
        try {
            result = taggingMapper.delete(tagging);
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "图片或标签不存在");
            message.put("type", "info");
            return message;
        }
        if (result.equals(0)){
            message.put("message", "该图片不含有该标签");
            message.put("type", "warning");
            return message;
        }

        if (!taggingMapper.selectTaggingIsEmpty(tagId)){
            try {
                tagMapper.deleteTagById(tagId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new NullPointerException();
            }
        }
        List<Tag> photoTags = tagMapper.selectTagByPhotoId(photoId);
        message.put("tags", photoTags);
        message.put("message", "标签删除成功");
        message.put("type", "success");
        return message;
    }

    @Override
    @Transactional
    public Map<String, String> setComment(Integer photoId) {
        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer photoAuthorId = photoMapper.selectAuthorId(photoId);

        if (!currentUserId.equals(photoAuthorId)){
            message.put("message", "对不起，您没有此操作权限");
            message.put("type", "warning");
            return message;
        }

        if (photoMapper.selectCanComment(photoId)){
            Integer result = photoMapper.updateCanComment(photoId);
            if (result.equals(0)){
                message.put("message", "图片不存在");
                message.put("type", "warning");
                return message;
            }
            message.put("message", "评论已禁止");
            message.put("type", "success");
            return message;
        }else {
            Integer result = photoMapper.updateCanComment(photoId);
            if (result.equals(0)){
                message.put("message", "图片不存在");
                message.put("type", "warning");
                return message;
            }
            message.put("message", "评论已开启");
            message.put("type", "success");
            return message;
        }
    }

    @Override
    @Transactional
    public Map<String, String> addComment(Integer photoId, Integer reply, String body) {
        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer photoAuthorId = photoMapper.selectAuthorId(photoId);

        Comment comment = new Comment();
        comment.setAuthorId(currentUserId);
        comment.setPhotoId(photoId);
        comment.setBody(body);

        if (!reply.equals(0)){
            comment.setRepliedId(reply);
            Integer authorId = commentMapper.selectAuthorIdById(reply);
            if (userMapper.selectCommentNotificationSettings(authorId)){
                String messageContent = String.format("您在<a href=\"%s#comments\">图片</a> 下的评论有新回复", "/main/photo/" + photoId);
                try {
                    notificationMapper.insert(messageContent, authorId);
                } catch (DataIntegrityViolationException e) {
                    e.printStackTrace();
                    message.put("message", "缺少必要的参数");
                    message.put("type", "info");
                    return message;
                }
            }
        }

        if (!currentUserId.equals(photoAuthorId) && userMapper.selectCommentNotificationSettings(photoAuthorId)){
            String messageContent = String.format("您的<a href=\"%s#comments\">图片</a> 下有新评论", "/main/photo/" + photoId);
            try {
                notificationMapper.insert(messageContent, photoAuthorId);
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                message.put("message", "缺少必要的参数");
                message.put("type", "info");
                return message;
            }
        }

        try {
            commentMapper.insertSelective(comment);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            message.put("message", "缺少必要的参数");
            message.put("type", "info");
            return message;
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "图片或用户不存在");
            message.put("type", "info");
            return message;
        }
        message.put("message", "评论发布成功");
        message.put("type", "success");
        return message;

    }

    @Override
    @Transactional
    public Map<String, String> reportComment(Integer id) {
        Map<String, String> message = new HashMap<>();
        Integer result = commentMapper.updateFlag(id);
        if (result.equals(0)){
            message.put("message", "评论不存在");
            message.put("type", "info");
            return message;
        }
        message.put("message", "您的举报请求已受理");
        message.put("type", "info");
        return message;
    }

    @Override
    public Map<String, String> deleteComment(Integer id) {
        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Comment comment = commentMapper.selectByPrimaryKey(id);
        Integer photoAuthorId = photoMapper.selectAuthorId(comment.getPhotoId());

        if (!currentUserId.equals(comment.getAuthorId()) && !currentUserId.equals(photoAuthorId) && !currentUser.isPermitted("MODERATE")){
            message.put("message", "对不起，您没有此操作权限");
            message.put("type", "warning");
            return message;
        }

        Integer result = commentMapper.deleteByPrimaryKey(id);
        if (result.equals(0)){
            message.put("message", "评论不存在");
            message.put("type", "info");
            return message;
        }
        message.put("message", "评论已删除");
        message.put("type", "success");
        return message;
    }

    @Override
    @Transactional
    public void upload(MultipartFile file) throws IOException {
//        new File(System.getProperty("joher") + "/uploads/" ).mkdirs();
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        String suffix = "";
        String ext = "";
        String fileName = "";
        String fileNameS = "";
        String fileNameM = "";
        String filePath = "";

        if (file != null){
            // 原始文件名
            String originalFileName = file.getOriginalFilename();
            String random = UUID.randomUUID().toString();
            // 获取图片后缀
            suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            int index = suffix.lastIndexOf(".");
            ext = suffix.substring(index + 1);
            // 生成图片存储的名称，UUID 避免相同图片名冲突，并加上图片后缀
            fileName =  random + suffix;
            fileNameS = random + "_s" + suffix;
            fileNameM = random + "_m" +  suffix;
            File saveFile = new File(System.getProperty("joher") + "/uploads/" + fileName);
            file.transferTo(saveFile);
        }
        BufferedImage bufImg = ImageIO.read(new File(System.getProperty("joher") + "/uploads/" + fileName));
        float width = bufImg.getWidth();
        int height = bufImg.getHeight();

        if (width > 400){
            double scale = ( width / 400 );
            int newHeight= (int) ( height / scale );
            int newWidth = (int) ( width / scale );
            Image img = bufImg.getScaledInstance((int)newWidth, newHeight, Image.SCALE_DEFAULT);
            //新建一个和Image对象相同大小的画布
            BufferedImage image = new BufferedImage((int)newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            //获取画笔
            Graphics2D graphics = image.createGraphics();
            //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image 信息通知的异步更新接口,没用到直接传空
            graphics.drawImage(img, 0, 0, null);
            //释放资源
            graphics.dispose();
            //使用ImageIO的方法进行输出,记得关闭资源
            OutputStream out = new FileOutputStream(System.getProperty("joher") + "/uploads/" + fileNameS);
            ImageIO.write(image, ext, out);
            out.close();
        }else{
            fileNameS = fileName;
        }

        if (width > 800){
            double scale = ( width / 800 );
            int newHeight= (int) ( height / scale );
            int newWidth = (int) ( width / scale );
            Image img = bufImg.getScaledInstance((int)newWidth, newHeight, Image.SCALE_DEFAULT);
            //新建一个和Image对象相同大小的画布
            BufferedImage image = new BufferedImage((int)newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            //获取画笔
            Graphics2D graphics = image.createGraphics();
            //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image 信息通知的异步更新接口,没用到直接传空
            graphics.drawImage(img, 0, 0, null);
            //释放资源
            graphics.dispose();
            //使用ImageIO的方法进行输出,记得关闭资源
            OutputStream out = new FileOutputStream(System.getProperty("joher") + "/uploads/" + fileNameM);
            ImageIO.write(image, ext, out);
            out.close();
        }else {
            fileNameM = fileName;
        }

        Photo photo = new Photo();
        photo.setFilenameS(fileNameS);
        photo.setFilenameM(fileNameM);
        photo.setFilename(fileName);
        photo.setAuthorId(currentUserId);

        try {
            photoMapper.insert(photo);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getNotification(Integer page, String filterRule) {
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        List<Notification> notifications = null;
        if ( filterRule.equals("unread") ){
            PageHelper.startPage(page, 20);
            notifications = notificationMapper.selectAllUnread(currentUserId);
        }else {
            PageHelper.startPage(page, 20);
            notifications = notificationMapper.selectAll(currentUserId);
        }

        PageInfo<Notification> notificationContent = new PageInfo<>(notifications);
        Integer notificationCount = userMapper.selectNotificationById(currentUserId);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("count", notificationContent.getTotal());
        pagination.put("currentPage", notificationContent.getPageNum());
        pagination.put("perPage", notificationContent.getPageSize());

        Map<String, Object> result = new HashMap<>();
        result.put("notifications", notificationContent.getList());
        result.put("notificationCount", notificationCount);
        result.put("pagination", pagination);

        return result;
    }

    @Override
    @Transactional
    public Map<String, String> readNotification(Integer id) {
        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        if (!currentUserId.equals(notificationMapper.selectReceiverId(id))){
            message.put("message", "对不起，您没有此操作权限");
            message.put("type", "warning");
            return message;
        }

        Integer result = notificationMapper.updateReadById(id);
        if (result.equals(0)){
            message.put("message", "此消息不存在");
            message.put("type", "warning");
            return message;
        }

        message.put("message", "成功将该提醒标记为已读");
        message.put("type", "success");
        return message;
    }

    @Override
    @Transactional
    public Map<String, String> readAllNotification() {
        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        Integer result = notificationMapper.updateReadByUserid(currentUserId);
        if (result.equals(0)){
            message.put("message", "不存在未读消息");
            message.put("type", "warning");
            return message;
        }

        message.put("message", "成功将所有提醒标记为已读");
        message.put("type", "success");
        return message;
    }

    @Override
    public Map<String, Object> getTagPhotos(Integer tagId, Integer page, String filterRule) {
        PageInfo<Photo> photosContent = null;
        if (filterRule.equals("byCollects")){
            PageHelper.startPage(page, 12);
            List<Photo> photos = photoMapper.selectPhotoByOrder(tagId);
            for (Photo photo : photos) {
                photo.setFilenameS("/uploads/" + photo.getFilenameS());
            }
            photosContent = new PageInfo<>(photos);
        }else {
            PageHelper.startPage(page, 12);
            List<Photo> photos = photoMapper.selectPhotoByTagId(tagId);
            for (Photo photo : photos) {
                photo.setFilenameS("/uploads/" + photo.getFilenameS());
            }
            photosContent = new PageInfo<>(photos);
        }

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("count", photosContent.getTotal());
        pagination.put("currentPage", photosContent.getPageNum());
        pagination.put("perPage", photosContent.getPageSize());

        Map<String, Object> tag = new HashMap<>();
        tag.put("name", tagMapper.selectNameByid(tagId));

        Map<String, Object> result = new HashMap<>();
        result.put("photos", photosContent.getList());
        result.put("photoCount", photosContent.getTotal());
        result.put("pagination", pagination);
        result.put("tag", tag);

        return result;
    }

    @Override
    public Map<String, Object> getPhotoCollectors(Integer photoId, Integer page) {
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        PageHelper.startPage(page, 20);
        List<User> users = userMapper.selectUserByPhotoId(currentUserId, photoId);
        for (User user : users) {
            user.setAvatarM("/avatars/" + user.getAvatarM());
        }
        PageInfo<User> usersContent = new PageInfo<>(users);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("count", usersContent.getTotal());
        pagination.put("currentPage", usersContent.getPageNum());
        pagination.put("perPage", usersContent.getPageSize());

        Map<String, Object> result = new HashMap<>();
        result.put("collectorCount", usersContent.getTotal());
        result.put("collects", usersContent.getList());
        result.put("pagination", pagination);
        return result;
    }

}
