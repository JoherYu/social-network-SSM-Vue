package com.sharephoto.dao;

import com.sharephoto.entity.Photo;
import com.sharephoto.entity.Tag;
import com.sharephoto.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PhotoMapper {

    // 根据id删除图片
    int deleteByPrimaryKey(Integer id);
    // 上传图片
    int insert(Photo record);

    // 插入虚拟图片数据
    int insertFake(Photo record);

//    int insertSelective(Photo record);
//
//    Photo selectByPrimaryKey(Integer id);


//    int updateByPrimaryKeySelective(Photo record);
//
//    int updateByPrimaryKey(Photo record);

    // 根据Id查询图片
    List<Map> selectByTagId(Integer tagId);

    // 查询图片总数
    Integer selectAllCount();

    // 查询被举报图片数
    Integer selectReportedCount();

    // 查询所有图片并根据时间排序
    List<Photo> selectAllByTime();

    // 查询所有图片并根据举报数排序
    List<Photo> selectAllByFlag();

    // 查询图片便签
    List<Tag> selectAllTag();

    // 查询图片标签数
    Integer selectTagCount();

    // 查询随机图片
    List<Photo> selectRandom();

    // 查询收藏者数量
    Integer selectCollectorCount(Integer photoId);

    // 查询评论数量
    Integer selectCommentCount(Integer photoId);

    // 根据关键字之查询图片
    List<Photo> selectPhotoByKey(String q);

    // 查询当前用户是否收藏此图片
    boolean selectCollected(@Param("id") Integer id, @Param("userId") Integer userId);

    // 查询用户个人中心图片
    List<Photo> selectCenterPhotos(Integer currentUserId);

    // 查询图片作者信息
    List<User> selectAuthorByPhotoId(Integer id);

    // 查询图片具体作者信息
    User selectAuthorInfoByPhotoId(Integer id);

    // 查询图片详情
    Photo selectPhotoDetail(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);

    // 查询图片作者id
    Integer selectAuthorId(Integer id);

    // 查询下一张图片
    Integer selectNextPhotoId(@Param("id") Integer id, @Param("photoAuthorId") Integer photoAuthorId);

    // 查询下一张图片
    Integer selectPrePhotoId(@Param("id") Integer id, @Param("photoAuthorId") Integer photoAuthorId);

    // 更新举报信息
    Integer updateReport(Integer id);

    // 更新图片描述
    Integer updateDescription(@Param("id") Integer id, @Param("description") String description);

    // 查询图片评论属性
    boolean selectCanComment(Integer id);

    // 更新图片评论属性
    Integer updateCanComment(Integer id);

    // 查询标签下所有图片
    List<Photo> selectPhotoByTagId(Integer tagId);

    // 查询标签下所有图片并根据收藏数排序
    List<Photo> selectPhotoByOrder(Integer tagId);

    // 查询用户所有图片
    List<Photo> selectAllByUsername(String username);

    // 查询用户所有收藏
    List<Photo> selectAllCollectionByUserId(Integer id);

    // 根据相片ID查询所有尺寸图片
    Photo selectFilenamesById(Integer id);
}