package com.sharephoto.dao;

import com.sharephoto.entity.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotificationMapper {
//    int deleteByPrimaryKey(Integer id);
//
    // 增加新消息
    int insert(@Param("message") String message, @Param("receiverId") Integer receiverId);
//
//    int insertSelective(Notification record);
//
//    Notification selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(Notification record);
//
//    int updateByPrimaryKeyWithBLOBs(Notification record);
//
//    int updateByPrimaryKey(Notification record);
    // 查询所有消息
    List<Notification> selectAll(Integer receiverId);

    // 查询所有未读消息
    List<Notification> selectAllUnread(Integer receiverId);

    // 根据消息Id更新消息为已读
    Integer updateReadById(Integer id);

    // 根据用户Id更新消息为已读
    Integer updateReadByUserid(Integer userId);

    // 根据消息Id查询消息收件人
    Integer selectReceiverId(Integer id);
}