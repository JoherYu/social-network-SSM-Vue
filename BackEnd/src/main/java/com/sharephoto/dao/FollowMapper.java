package com.sharephoto.dao;

import org.apache.ibatis.annotations.Param;

public interface FollowMapper {
    // 删除关注记录
    int delete(@Param("userId") Integer userId, @Param("currentUserId") Integer currentUserId);
    // 添加关注记录
    int insert(@Param("userId") Integer userId, @Param("currentUserId") Integer currentUserId);
//
//    int insertSelective(Follow record);
//
//    Follow selectByPrimaryKey(FollowKey key);
//
//    int updateByPrimaryKeySelective(Follow record);
//
//    int updateByPrimaryKey(Follow record);

    // 查询用户粉丝数
    int selectFollowerCount(Integer id);

    // 查询用户关注数
    int selectFollowingCount(Integer id);

}