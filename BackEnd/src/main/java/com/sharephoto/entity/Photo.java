package com.sharephoto.entity;

import java.util.Date;
import java.util.List;

public class Photo {
    private Integer id;

//    @Max(value = 500, message = "密码字符数超过限制，最多可输入500个字符")
    private String description;

    private String filename;

    private String filenameS;

    private String filenameM;

    private Date timestamp;

    private Short flag;

    private boolean canComment;

    private Integer authorId;

    private User author;

    private String authorName;

    private String authorUsername;

    private List<Tag> tags;

    private Integer collectorCount;

    private Integer tagCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilenameS() {
        return filenameS;
    }

    public void setFilenameS(String filenameS) {
        this.filenameS = filenameS;
    }

    public String getFilenameM() {
        return filenameM;
    }

    public void setFilenameM(String filenameM) {
        this.filenameM = filenameM;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Short getFlag() {
        return flag;
    }

    public void setFlag(Short flag) {
        this.flag = flag;
    }

    public boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Integer getCollectorCount() {
        return collectorCount;
    }

    public void setCollectorCount(Integer collectorCount) {
        this.collectorCount = collectorCount;
    }

    public Integer getTagCount() {
        return tagCount;
    }

    public void setTagCount(Integer tagCount) {
        this.tagCount = tagCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    private Integer commentCount;

    private boolean collected;


}