package com.sharephoto.dao;

import org.apache.ibatis.annotations.Param;

public interface CollectMapper {
//    int deleteByPrimaryKey(CollectKey key);
//
    // 添加收藏记录
    int insert(@Param("photoId") Integer photoId, @Param("currentUserId") Integer currentUserId);
//
//    int insertSelective(Collect record);
//
//    Collect selectByPrimaryKey(CollectKey key);
//
//    int updateByPrimaryKeySelective(Collect record);
//
//    int updateByPrimaryKey(Collect record);
    // 查询当前用户是否收藏此图片
    boolean selectcollected(@Param("photoId") Integer photoId, @Param("currentUserId") Integer currentUserId);

    // 删除收藏记录
    int delete(@Param("photoId") Integer photoId, @Param("currentUserId") Integer currentUserId);

    // 查询用户收藏总数
    Integer selectCollectCountByUserId(Integer userId);
}