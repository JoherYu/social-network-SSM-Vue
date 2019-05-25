package com.sharephoto.entity;

public class Tag {
    private Integer id;

//    @Max(value = 64, message = "密码字符数超过限制，最多可输入64个字符")
    private String name;

    private Integer photoCount;

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim(); }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photoCount=" + photoCount +
                '}';
    }
}