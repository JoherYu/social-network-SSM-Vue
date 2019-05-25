package com.sharephoto.service;

import com.sharephoto.entity.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MainService {

    // 获取随机图片
    List<Photo> getRandomPhoto();

    // 搜索
    Map<String, ?> search(String category, String q, Integer page);

    // 用户个人中心数据
    Map<String, ?> getSelfCenter(Integer page);

    // 收藏图片
    Map<String, String> collectPhoto(Integer photoId);

    // 取消收藏
    Map<String, String> uncollectPhoto(Integer photoId);

    // 获取用户资料弹窗信息
    Object[] getPopupData(Integer userId);

    // 关注用户
    Map<String, String> followUser(String username);

    // 取消关注
    Map<String, String> unfollowUser(String username);

    // 获取相片主页信息
    Map<String, ?> getPhotoIndex(Integer photoId, Integer page);

    // 删除图片
    Map<String, ?> deletePhoto(Integer photoId);

    // 获取下一张图片
    Map<String, ?> nextPhoto(Integer photoId);

    // 获取上一张图片
    Map<String, ?> prePhoto(Integer photoId);

    // 举报图片
    Map<String, String> reportPhoto(Integer photoId);

    // 更新图片描述
    Map<String, String> editPhotoDescription(Integer photoId, String description);

    // 添加标签
    Map<String, Object> addTags(Integer photoId, String tagString);

    // 删除图片标签
    Map<String, Object> deleteTags(Integer photoId, Integer tagId);

    // 设置图片能否评论
    Map<String, String> setComment(Integer photoId);

    // 添加新评论
    Map<String, String> addComment(Integer photoId, Integer reply, String body);

    // 举报评论
    Map<String, String> reportComment(Integer id);

    // 删除评论
    Map<String, String> deleteComment(Integer id);

    // 图片上传
    void upload(MultipartFile file) throws IOException;

    // 获取消息
    Map<String, Object> getNotification(Integer page, String filterRule);

    // 设置消息为已读
    Map<String, String> readNotification(Integer id);

    // 设置所有消息为已读
    Map<String, String> readAllNotification();

    // 获取标签下所有图片
    Map<String, Object> getTagPhotos(Integer tagId, Integer page, String filterRule);

    // 获取图片所有收藏者
    Map<String, Object> getPhotoCollectors(Integer photoId, Integer page);
}
