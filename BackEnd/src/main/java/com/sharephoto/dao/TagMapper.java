package com.sharephoto.dao;

import com.sharephoto.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TagMapper {
//    int deleteByPrimaryKey(Integer id);

    int insert(String name);
//
//    int insertSelective(Tag record);
//
//    Tag selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(Tag record);
//
//    int updateByPrimaryKey(Tag record);

    // 根据标签ID删除标签
    int deleteTagById(Integer tagId);

    // 查询标签总数
    Integer selectAllCount();

    // 查询所有标签
    List<Map> selectAll();

    // 根据Id查询图片数
    Integer selectCountByTagId(Integer tagId);

    // 根据关键字之查询标签
    List<Tag> selectTagByKey(String q);

    // 查询图片数最多的10个标签
    List<Tag> selectHotTags();

    // 根据图片id查询标签
    List<Tag> selectTagByPhotoId(Integer photoId);

    // 根据标签名查询标签id
    Integer selectIdByName(String name);

    // 根据id查询标签标签名
    String selectNameByid(Integer id);

    Integer insertFake(@Param("id") Integer id, @Param("name") String name);

}