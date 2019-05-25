package com.sharephoto.service;

import com.github.javafaker.Faker;
import com.sharephoto.dao.*;
import com.sharephoto.entity.Comment;
import com.sharephoto.entity.Photo;
import com.sharephoto.entity.Tagging;
import com.sharephoto.entity.User;
import com.sharephoto.utils.GenerateAvatar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.sharephoto.shiro.MyRealm.generateHash;
import static com.sharephoto.utils.GenerateImage.generateImage;

/**
 * @author Joher
 * @data 2019/5/23
 **/
@Service("faker")
public class GenerateFakeDataImpl implements GenerateFakeData{

    @Autowired
    UserMapper userMapper;

    @Autowired
    FollowMapper followMapper;

    @Autowired
    TagMapper tagMapper;

    @Autowired
    TaggingMapper taggingMapper;

    @Autowired
    CollectMapper collectMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    NotificationMapper notificationMapper;

    @Autowired
    PhotoMapper photoMapper;

    @Override
    @Transactional
    public List<String> fakingIt(){

        List<String> message = new ArrayList<>();

        Faker faker = new Faker();


        User admin = new User();
        admin.setId(1);
        admin.setUsername("admin");
        admin.setOriUsername("admin");
        Integer roleId = 4;
        admin.setRoleId(roleId.byteValue());
        String adminPasswordHash = generateHash("admin", "1234567890");
        admin.setPasswordHash(adminPasswordHash);
        admin.setName("admin");
        admin.setConfirmed(true);
        admin.setWebsite("http://" + faker.internet().domainName());
        admin.setBio(faker.shakespeare().romeoAndJulietQuote());
        admin.setLocation(faker.address().city());
        admin.setEmail("joheryu@163.com");
        try {
            userMapper.insertFake(admin);
            Integer adminUserId = userMapper.selectUserIdByUsername("admin");
            notificationMapper.insert("欢迎加入分相", adminUserId);
            String[] adminAvatars = GenerateAvatar.generateAvatar("admin");
            userMapper.updateCropAvatar(adminUserId, adminAvatars[0], adminAvatars[1], adminAvatars[2]);
            userMapper.updateAvatarRaw("admin_l.jpg" ,adminUserId);
        } catch (Exception e) {
            e.printStackTrace();
            message.add("管理员生成失败");
        }

        for (int i = 0; i < 10; i++){
            User user = new User();
            user.setId(i + 2);
            String username = faker.name().firstName();
            String passwordHash = generateHash(username, "123456");

            user.setUsername(username);
            user.setOriUsername(username);
            user.setPasswordHash(passwordHash);
            user.setName(faker.name().name());
            user.setConfirmed(true);
            user.setWebsite("http://" + faker.internet().domainName());
            user.setBio(faker.shakespeare().romeoAndJulietQuote());
            user.setLocation(faker.address().city());
            user.setEmail(faker.internet().emailAddress());
            try {
                userMapper.insertFake(user);
                Integer currentUserId = userMapper.selectUserIdByUsername(username);
                notificationMapper.insert("欢迎加入分相", currentUserId);
                String[] avatars = GenerateAvatar.generateAvatar(username);
                userMapper.updateCropAvatar(currentUserId, avatars[0], avatars[1], avatars[2]);
                userMapper.updateAvatarRaw(username + "_l.jpg", currentUserId);
            } catch (Exception e) {
                e.printStackTrace();
                message.add((i + 1) + "号用户生成失败");
            }
        }

        for (int i = 0; i < 30; i++){
            try {
                followMapper.insert((int)(1+Math.random()*11), (int)(1+Math.random()*11));
            } catch (Exception e) {
                e.printStackTrace();
                message.add("第" + (i + 1) + "条关注生成失败");
            }
        }

        for (int i = 0; i < 20; i++){
            try {
                tagMapper.insertFake(i + 1, faker.food().ingredient());
            } catch (Exception e) {
                e.printStackTrace();
                message.add((i + 1) + "号标签生成失败");
            }
        }

        for (int i = 0; i < 30; i++){
            try {
                String filename = "filename_" + i + ".jpg";
                generateImage(filename);
                Photo photo = new Photo();
                photo.setId(i + 1);
                photo.setFilenameS(filename);
                photo.setFilename(filename);
                photo.setFilenameM(filename);
                photo.setDescription(faker.lorem().paragraph());
                photo.setAuthorId((int)(1+Math.random()*11));
                photoMapper.insertFake(photo);
                for (int j = 0; j < 5; j++){
                    try {
                        Tagging tagging = new Tagging();
                        tagging.setPhotoId(i + 1);
                        tagging.setTagId((int)(1+Math.random()* 20));
                        taggingMapper.insert(tagging);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.add((i + 1) + "号图片生成失败");
            }

        }

        for (int i = 0; i < 50; i++){
            try {
                collectMapper.insert((int)(1+Math.random()* 30), (int)(1+Math.random()* 11));
            } catch (Exception e) {
                e.printStackTrace();
                message.add("第" + (i + 1) + "条收藏生成失败");
            }
        }

        for (int i = 0; i < 100; i++){
            Comment comment = new Comment();
            comment.setId(i + 1);
            comment.setPhotoId((int)(1+Math.random()* 30));
            comment.setAuthorId((int)(1+Math.random()* 11));
            comment.setBody(faker.lorem().paragraph());
            try {
                commentMapper.insertSelective(comment);
            } catch (Exception e) {
                e.printStackTrace();
                message.add((i + 1) + "号评论生成失败");
            }
        }
        return message;
    }



}
