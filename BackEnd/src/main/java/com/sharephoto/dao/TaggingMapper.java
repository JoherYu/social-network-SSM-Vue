package com.sharephoto.dao;

import com.sharephoto.entity.Tagging;

public interface TaggingMapper {
    // 添加图片标签
    int insert(Tagging record);

    // 查询标签记录是否存在
    boolean selectRecord(Tagging record);

    // 删除标签记录
    Integer delete(Tagging record);

    // 查询标签时候含有记录
    boolean selectTaggingIsEmpty(Integer tagId);
//
//    int insertSelective(Tagging record);
}