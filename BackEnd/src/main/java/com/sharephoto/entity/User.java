package com.sharephoto.entity;

import java.util.Date;

public class User {
    private Integer id;

    //    @NotEmpty(message = "邮箱地址不能为空")
//    @Email(message = "您的邮箱地址格式不正确")
//    @Max(value = 254, message = "邮箱地址字符数超过限制，最多可输入254个字符")
    private String email;

    //    @NotEmpty(message = "用户名不能为空")
//    @Max(value = 20, message = "用户名字符数超过限制，最多可输入20个字符")
    private String username;

    public String getOriUsername() {
        return oriUsername;
    }

    public void setOriUsername(String oriUsername) {
        this.oriUsername = oriUsername;
    }

    private String oriUsername;

    //    @NotEmpty(message = "密码不能为空")
//    @Max(value = 128, message = "密码字符数超过限制，最多可输入128个字符")
    private String passwordHash;

    //    @Max(value = 30, message = "密码字符数超过限制，最多可输入30个字符")
    private String name;

    //    @Max(value = 255, message = "密码字符数超过限制，最多可输入255个字符")
    private String website;

    //    @Max(value = 120, message = "密码字符数超过限制，最多可输入120个字符")
    private String bio;

    //    @Max(value = 150, message = "密码字符数超过限制，最多可输入150个字符")
    private String location;

    private Date memberSince;

    private String avatarS;

    private String avatarM;

    private String avatarL;

    private String avatarRaw;

    private boolean confirmed;

    private boolean locked;

    private boolean active;

    private boolean publicCollections;

    private boolean receiveCommentNotification;

    private boolean receiveFollowNotification;

    private boolean receiveCollectNotification;

    private Byte roleId;

    private Integer notificationCount;

    private boolean admin;

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    private boolean canModerate;

    private boolean canComment;

    private boolean following;

    private boolean followed;

    private Integer followerCount;

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(Integer collectionCount) {
        this.collectionCount = collectionCount;
    }

    private Integer followingCount;

    private Integer photoCount;

    private Integer collectionCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(Date memberSince) {
        this.memberSince = memberSince;
    }

    public String getAvatarS() {
        return avatarS;
    }

    public void setAvatarS(String avatarS) {
        this.avatarS = avatarS;
    }

    public String getAvatarM() {
        return avatarM;
    }

    public void setAvatarM(String avatarM) {
        this.avatarM = avatarM;
    }

    public String getAvatarL() {
        return avatarL;
    }

    public void setAvatarL(String avatarL) {
        this.avatarL = avatarL;
    }

    public String getAvatarRaw() {
        return avatarRaw;
    }

    public void setAvatarRaw(String avatarRaw) {
        this.avatarRaw = avatarRaw;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPublicCollections() {
        return publicCollections;
    }

    public void setPublicCollections(boolean publicCollections) {
        this.publicCollections = publicCollections;
    }

    public boolean isReceiveCommentNotification() {
        return receiveCommentNotification;
    }

    public void setReceiveCommentNotification(boolean receiveCommentNotification) {
        this.receiveCommentNotification = receiveCommentNotification;
    }

    public boolean isReceiveFollowNotification() {
        return receiveFollowNotification;
    }

    public void setReceiveFollowNotification(boolean receiveFollowNotification) {
        this.receiveFollowNotification = receiveFollowNotification;
    }

    public boolean isReceiveCollectNotification() {
        return receiveCollectNotification;
    }

    public void setReceiveCollectNotification(boolean receiveCollectNotification) {
        this.receiveCollectNotification = receiveCollectNotification;
    }

    public Byte getRoleId() {
        return roleId;
    }

    public void setRoleId(Byte roleId) {
        this.roleId = roleId;
    }

    public Integer getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isCanModerate() {
        return canModerate;
    }

    public void setCanModerate(boolean canModerate) {
        this.canModerate = canModerate;
    }

    public boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }
}