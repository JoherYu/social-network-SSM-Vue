package com.sharephoto.dao;

import com.sharephoto.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    // 删除评论
    int deleteByPrimaryKey(Integer id);
//    // 添加新回复
//    int insertWithReply(Comment record);
    // 添加新评论
//    int insert(Comment record);
    int insertSelective(Comment record);

    // 查询评论信息
    Comment selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(Comment record);
//
//    int updateByPrimaryKeyWithBLOBs(Comment record);
//
//    int updateByPrimaryKey(Comment record);

    // 查询评论总数
    Integer selectAllCount();

    // 查询被举报评论总数
    Integer selectReportedCount();

    // 查询所有评论并按时间排序
    List<Comment> selectAll();

    // 查询所有评论并按被举报数排序
    List<Comment>  selectAllByFlag();

    // 根据图片id查询评论
    List<Comment> selectCommentByPhotoId(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);

    // 举报评论
    Integer updateFlag(Integer id);

    // 查询评论作者
    Integer selectAuthorIdById(Integer id);
}