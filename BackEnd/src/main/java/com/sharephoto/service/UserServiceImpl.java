package com.sharephoto.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sharephoto.dao.PhotoMapper;
import com.sharephoto.dao.UserMapper;
import com.sharephoto.entity.Photo;
import com.sharephoto.entity.User;
import com.sharephoto.shiro.MyRealm;
import com.sharephoto.utils.JWTToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.*;

import static com.sharephoto.utils.EmailSender.sendEmail;
import static com.sharephoto.utils.JWTToken.getClaims;
import static com.sharephoto.utils.JWTToken.parseJWT;

/**
 * @author Joher
 * @data 2019/5/20
 **/
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PhotoMapper photoMapper;

    @Override
    @Transactional
    public Map<String, String> uploadAvatar(MultipartFile file, HttpSession session) {
//        new File(System.getProperty("joher") + "/uploads/avatars/" ).mkdirs();

        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        String fileName = null;

        if (file != null){
            // 原始文件名
            String originalFileName = file.getOriginalFilename();
            // 获取图片后缀
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 生成图片存储的名称，UUID 避免相同图片名冲突，并加上图片后缀
            fileName =  UUID.randomUUID().toString() + suffix;
            File saveFile = new File(System.getProperty("joher") + "/uploads/avatars/" + fileName);
            try {
                file.transferTo(saveFile);
            } catch (IOException e) {
                e.printStackTrace();
                message.put("message", "图片上传失败");
                message.put("type", "warning");
                return message;
            }

            User user = userMapper.selectAvatars(currentUserId);
            List<String> avatars = new ArrayList<>();
            avatars.add(user.getAvatarRaw());
            avatars.add(user.getAvatarS());
            avatars.add(user.getAvatarM());
            avatars.add(user.getAvatarL());

            session.setAttribute("avatars", avatars);
            userMapper.updateAvatar(fileName, currentUserId);
        }
        message.put("message", "图片已上传，请进行剪裁");
        message.put("type", "success");
        message.put("url", "/avatars/" + fileName);
        return message;
    }

    @Override
    @Transactional
    public Map<String, String> cropAvatar(Map<String, Object> data, HttpSession session) throws IOException {
        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        String avatarName = userMapper.selectRaw(currentUserId);
        String suffix = avatarName.substring(avatarName.lastIndexOf("."));
        int index = avatarName.lastIndexOf(".");
        String ext = avatarName.substring(index + 1);
        String body = avatarName.substring(0, index - 1);

        String avatarS = body + "_s" + suffix;
        String avatarM = body + "_m" + suffix;
        String avatarL = body + "_l" + suffix;

        BufferedImage bufImg = ImageIO.read(new File(System.getProperty("joher") + "/uploads/avatars/" + avatarName));
        Integer x = Integer.parseInt((String) data.get("x"));
        Integer y = Integer.parseInt((String) data.get("y"));
        Integer w = Integer.parseInt((String) data.get("w"));
        Integer h = Integer.parseInt((String) data.get("h"));
        BufferedImage subImg = bufImg.getSubimage(x, y, w, h);
        int width = subImg.getWidth();

        if (width > 30){
            Image img = subImg.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
            //新建一个和Image对象相同大小的画布
            BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
            //获取画笔
            Graphics2D graphics = image.createGraphics();
            //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image 信息通知的异步更新接口,没用到直接传空
            graphics.drawImage(img, 0, 0, null);
            //释放资源
            graphics.dispose();
            //使用ImageIO的方法进行输出,记得关闭资源
            OutputStream out = new FileOutputStream(System.getProperty("joher") + "/uploads/avatars/" + avatarS);
            ImageIO.write(image, ext, out);
            out.close();
        }else{
            avatarS = avatarName;
        }

        if (width > 100){
            Image img = subImg.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            //新建一个和Image对象相同大小的画布
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            //获取画笔
            Graphics2D graphics = image.createGraphics();
            //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image 信息通知的异步更新接口,没用到直接传空
            graphics.drawImage(img, 0, 0, null);
            //释放资源
            graphics.dispose();
            //使用ImageIO的方法进行输出,记得关闭资源
            OutputStream out = new FileOutputStream(System.getProperty("joher") + "/uploads/avatars/" + avatarM);
            ImageIO.write(image, ext, out);
            out.close();
        }else{
            avatarM = avatarName;
        }

        if (width > 200){
            Image img = subImg.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            //新建一个和Image对象相同大小的画布
            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            //获取画笔
            Graphics2D graphics = image.createGraphics();
            //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image 信息通知的异步更新接口,没用到直接传空
            graphics.drawImage(img, 0, 0, null);
            //释放资源
            graphics.dispose();
            //使用ImageIO的方法进行输出,记得关闭资源
            OutputStream out = new FileOutputStream(System.getProperty("joher") + "/uploads/avatars/" + avatarL);
            ImageIO.write(image, ext, out);
            out.close();
        }else{
            avatarL = avatarName;
        }

        userMapper.updateCropAvatar(currentUserId, avatarS, avatarM, avatarL);

        List<String> avatars = (List<String>) session.getAttribute("avatars");
        for (String avatar : avatars) {
            File file = new File(System.getProperty("joher") + "/uploads/avatars/" + avatar);
            if (!file.exists()) {
                System.out.println("删除文件失败:" + avatar + "不存在！");
            } else {
                file.delete();
                System.out.println("成功删除文件" + avatar);
            }
        }

        session.removeAttribute("avatars");
        message.put("message", "头像上传成功");
        message.put("type", "success");
        message.put("avatarS", "/avatars/" + avatarS);
        message.put("avatarM", "/avatars/" + avatarM);
        return message;
    }

    @Override
    @Transactional
    public Map<String, String> deleteAccount(String username) {
        Map<String, String> message = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();

        if (username.equals(currentUsername)){
            Integer result = userMapper.deleteUserByUsername(username);
            if (result.equals(0)){
                message.put("message", "用户不存在");
                message.put("type", "warning");
                return message;
            }
            message.put("message", "帐户已成功删除");
            message.put("type", "success");
            return message;
        }

        message.put("message", "用户名错误");
        message.put("type", "warning");
        return message;
    }

    @Override
    public Map<String, Object> getUserIndex(String username, Integer page, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            currentUsername = "";
        }
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);


        if (currentUsername.equals(username) && !userMapper.selectActive(username)){
            session.invalidate();
            result.put("message","您的帐户已被封禁");
            result.put("type","warning");
            return result;
        }

        if (currentUsername.equals(username) && userMapper.selectLocked(username)){
            session.invalidate();
            result.put("message","您的帐户已被锁定");
            result.put("type","warning");
            return result;
        }

        PageHelper.startPage(page, 12);
        List<Photo> photos = photoMapper.selectAllByUsername(username);
        for (Photo photo:photos){
            photo.setFilenameS("/uploads/" + photo.getFilenameS());
        }
        PageInfo<Photo> photosContent = new PageInfo<>(photos);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("count", photosContent.getTotal());
        pagination.put("currentPage", photosContent.getPageNum());
        pagination.put("perPage", photosContent.getPageSize());

        User user = userMapper.selectByUserusername(username, currentUserId);
        user.setAvatarL("/avatars/" + user.getAvatarL());
        user.setFollowerCount(user.getFollowerCount() - 1);
        user.setFollowingCount(user.getFollowingCount() - 1);

        Map<String, String> message = new HashMap<>();
        message.put("message", "");
        message.put("type", "");

        result.put("user", user);
        result.put("photos", photosContent.getList());
        result.put("pagination", pagination);
        result.put("message", message);

        return result;
    }
    @Override
    public Map<String, Object> getUserIndexCollection(String username, Integer page, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            currentUsername = "";
        }
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer userId = userMapper.selectUserIdByUsername(username);


        if (currentUsername.equals(username) && !userMapper.selectActive(username)){
            session.invalidate();
            result.put("message","您的帐户已被封禁");
            result.put("type","warning");
            return result;
        }

        if (currentUsername.equals(username) && userMapper.selectLocked(username)){
            session.invalidate();
            result.put("message","您的帐户已被锁定");
            result.put("type","warning");
            return result;
        }

        PageHelper.startPage(page, 12);
        List<Photo> photos = photoMapper.selectAllCollectionByUserId(userId);
        for (Photo photo:photos){
            photo.setFilenameS("/uploads/" + photo.getFilenameS());
        }
        PageInfo<Photo> photosContent = new PageInfo<>(photos);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("count", photosContent.getTotal());
        pagination.put("currentPage", photosContent.getPageNum());
        pagination.put("perPage", photosContent.getPageSize());

        User user = userMapper.selectByUserusername(username, currentUserId);
        user.setAvatarL("/avatars/" + user.getAvatarL());
        user.setFollowerCount(user.getFollowerCount() - 1);
        user.setFollowingCount(user.getFollowingCount() - 1);

        Map<String, String> message = new HashMap<>();
        message.put("message", "");
        message.put("type", "");

        result.put("user", user);
        result.put("collects", photosContent.getList());
        result.put("pagination", pagination);
        result.put("message", message);

        return result;
    }

    @Override
    public Map<String, Object> getUserIndexFollowers(String username, Integer page, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            currentUsername = "";
        }
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer userId = userMapper.selectUserIdByUsername(username);


        if (currentUsername.equals(username) && !userMapper.selectActive(username)){
            session.invalidate();
            result.put("message","您的帐户已被封禁");
            result.put("type","warning");
            return result;
        }

        if (currentUsername.equals(username) && userMapper.selectLocked(username)){
            session.invalidate();
            result.put("message","您的帐户已被锁定");
            result.put("type","warning");
            return result;
        }

        PageHelper.startPage(page, 20);
        List<User> users = userMapper.selectFollowers(userId, currentUserId);
        for (User user:users){
            user.setAvatarM("/avatars/" + user.getAvatarM());
        }
        PageInfo<User> usersContent = new PageInfo<>(users);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("count", usersContent.getTotal());
        pagination.put("currentPage", usersContent.getPageNum());
        pagination.put("perPage", usersContent.getPageSize());

        User user = userMapper.selectByUserusername(username, currentUserId);
        user.setAvatarL("/avatars/" + user.getAvatarL());
        user.setFollowerCount(user.getFollowerCount() - 1);
        user.setFollowingCount(user.getFollowingCount() - 1);

        Map<String, String> message = new HashMap<>();
        message.put("message", "");
        message.put("type", "");

        result.put("user", user);
        result.put("follows", usersContent.getList());
        result.put("pagination", pagination);
        result.put("message", message);

        return result;
    }

    @Override
    public Map<String, Object> getUserIndexFollowings(String username, Integer page, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            currentUsername = "";
        }
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        Integer userId = userMapper.selectUserIdByUsername(username);


        if (currentUsername.equals(username) && !userMapper.selectActive(username)){
            session.invalidate();
            result.put("message","您的帐户已被封禁");
            result.put("type","warning");
            return result;
        }

        if (currentUsername.equals(username) && userMapper.selectLocked(username)){
            session.invalidate();
            result.put("message","您的帐户已被锁定");
            result.put("type","warning");
            return result;
        }

        PageHelper.startPage(page, 20);
        List<User> users = userMapper.selectFollowing(userId, currentUserId);
        for (User user:users){
            user.setAvatarM("/avatars/" + user.getAvatarM());
        }
        PageInfo<User> usersContent = new PageInfo<>(users);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("count", usersContent.getTotal());
        pagination.put("currentPage", usersContent.getPageNum());
        pagination.put("perPage", usersContent.getPageSize());

        User user = userMapper.selectByUserusername(username, currentUserId);
        user.setAvatarL("/avatars/" + user.getAvatarL());
        user.setFollowerCount(user.getFollowerCount() - 1);
        user.setFollowingCount(user.getFollowingCount() - 1);

        Map<String, String> message = new HashMap<>();
        message.put("message", "");
        message.put("type", "");

        result.put("user", user);
        result.put("follows", usersContent.getList());
        result.put("pagination", pagination);
        result.put("message", message);

        return result;
    }

    @Override
    public Map<String, String> getEditProfile() {
        Map<String, String> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }

        User user = userMapper.selectProfileSetting(currentUsername);
        result.put("username", user.getUsername());
        result.put("name", user.getName());
        result.put("bio", user.getBio());
        result.put("website", user.getWebsite());
        result.put("city", user.getLocation());
        return result;
    }

    @Override
    @Transactional
    public Map<String, String> updateEditProfile(Map<String, String> data, HttpSession session) {
        Map<String, String> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);

        User user = new User();
        user.setUsername(data.get("username"));
        user.setName(data.get("name"));
        user.setLocation(data.get("city"));
        user.setBio(data.get("bio"));
        user.setWebsite(data.get("website"));
        user.setId(currentUserId);
        userMapper.updateProfileSetting(user);

        if (!currentUsername.equals(data.get("username"))){
            session.invalidate();
            result.put("message", "您的用户名已更改，请重新登录");
            result.put("type", "info");
            return result;
        }

        result.put("message", "资料更新成功");
        result.put("type", "success");
        return result;
    }

    @Override
    public Map<String, Object> getNotificationSetting() {
        Map<String, Object> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }

        User user = userMapper.selectNotificationSetting(currentUsername);
        List<String> checkedNotification = new ArrayList<>();

        if (user.isReceiveCollectNotification()){
            checkedNotification.add("newCollector");
        }

        if (user.isReceiveCommentNotification()){
            checkedNotification.add("newComment");
        }

        if (user.isReceiveFollowNotification()){
            checkedNotification.add("newFollower");
        }

        result.put("checkedNotification", checkedNotification);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updateNotificationSetting(Map<String, Boolean> data) {
        Map<String, Object> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }

        User user = new User();
        user.setUsername(currentUsername);
        user.setReceiveCollectNotification(data.get("newCollector"));
        user.setReceiveCommentNotification(data.get("newComment"));
        user.setReceiveFollowNotification(data.get("newFollower"));

        userMapper.updateNotificationSetting(user);
        result.put("message", "消息设置更新成功");
        result.put("type", "success");
        return result;
    }

    @Override
    public Map<String, Object> getPrivacySetting() {
        Map<String, Object> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }

        boolean publicMyCollection = userMapper.selectPrivacySetting(currentUsername);
        List<String> checkedPrivacy = new ArrayList<>();

        if (publicMyCollection){
            checkedPrivacy.add("publicMyCollection");
        }

        result.put("checkedPrivacy", checkedPrivacy);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updatePrivacySetting(Map<String, Boolean> data) {
        Map<String, Object> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }

        userMapper.updatePrivacySetting(currentUsername, data.get("publicMyCollection"));
        result.put("message", "隐私设置更新成功");
        result.put("type", "success");
        return result;
    }

    @Override
    @Transactional
    public Map<String, String> updatePassword(Map<String, String> data, HttpSession session) throws UnauthenticatedException, UnauthorizedException {
        Map<String, String> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }
        String oriUsername = userMapper.selectOriUsernaem(currentUsername);
        String oldPasswordHash = MyRealm.generateHash(oriUsername, data.get("oldPassword"));
        if (!userMapper.selectPassword(currentUsername).equals(oldPasswordHash)){
            result.put("message", "旧密码错误");
            result.put("type", "warning");
            return result;
        }else {

            String newPasswordHash = MyRealm.generateHash(oriUsername, data.get("password"));
            userMapper.updatePassword(currentUsername, newPasswordHash);
            session.invalidate();
            result.put("message", "密码已更新，请重新登录");
            result.put("type", "success");
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, String> updateEmail(Map<String, String> data, HttpServletRequest request) throws Exception {
        Map<String, String> result = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            result.put("message", "请先登录");
            result.put("type", "info");
            return result;
        }
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        if (currentUserId == null){
            result.put("message", "用户不存在");
            result.put("type", "info");
            return result;
        }

        if (userMapper.selectEmail(data.get("email").toLowerCase())){
            result.put("message", "邮箱已经注册过");
            result.put("type", "info");
            return result;
        }

        Map<String, Object> payLoad = new HashMap<>();
        payLoad.put("key", "newEmail");
        payLoad.put("value", data.get("email").toLowerCase());
        List<Map<String, Object>> payLoads = new ArrayList<>();
        payLoads.add(payLoad);
        String token = JWTToken.createJWT("changeEmail", currentUserId, payLoads);

        String requestUrl = request.getScheme() + "://" + request.getServerName();
        String content = "<p>" + currentUsername + ", 你好</p>\n" +
                "<p>请访问下方链接以确认电子邮箱地址:<br>\n" +
                "    <a href=\"" + requestUrl + "/emailConfirm/" + token + "\">" + requestUrl + "/emailConfirm/" + token + "</a>\n" +
                "</p>\n" +
                "<small>(请勿回复此邮件)</small>";
        new Thread(new Runnable() {
            public void run() {
                try {
                    sendEmail(content, "[分相]更换电子信箱", data.get("email").toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        result.put("message", "邮件已发送，请查看邮箱");
        result.put("type", "info");
        return result;
    }

    @Override
    public String emailConfirm(String token, HttpServletRequest request){
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        if (currentUsername == null){
            return "请先登录";
        }
        Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        if (currentUserId == null){
            return "用户不存在";
        }

        try {
            parseJWT(token, "changeEmail" ,currentUserId);
            Claims claims = getClaims(token, "changeEmail" ,currentUserId);
            userMapper.updateEmail(currentUserId, claims.get("newEmail", String.class));
            return "<!DOCTYPE HTML>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<title>倒计时自动关闭页面</title>\n" +
                    "<meta charset=\"utf-8\" />\n" +
                    "<script>\n" +
                    "    function myClose(){\n" +
                    "        var n=parseInt(time.innerHTML);\n" +
                    "        n--;\n" +
                    "        if(n>0){\n" +
                    "            time.innerHTML=n+\"秒钟后自动关闭\";\n" +
                    "            timer=setTimeout(myClose,1000);\n" +
                    "        }else{\n" +
                    "            close();\n" +
                    "        }\n" +
                    "    }\n" +
                    "    var timer=null;\n" +
                    "    window.onload=function(){\n" +
                    "        timer=setTimeout(myClose,1000);\n" +
                    "    }\n" +
                    "</script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h3>邮箱更改成功</h3>\n" +
                    "    <span id=\"time\">5秒钟后自动关闭</span><br>\n" +
                    "</body>\n" +
                    "</html>";
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return "<!DOCTYPE HTML>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<title>倒计时自动关闭页面</title>\n" +
                    "<meta charset=\"utf-8\" />\n" +
                    "<script>\n" +
                    "    function myClose(){\n" +
                    "        var n=parseInt(time.innerHTML);\n" +
                    "        n--;\n" +
                    "        if(n>0){\n" +
                    "            time.innerHTML=n+\"秒钟后自动关闭\";\n" +
                    "            timer=setTimeout(myClose,1000);\n" +
                    "        }else{\n" +
                    "            close();\n" +
                    "        }\n" +
                    "    }\n" +
                    "    var timer=null;\n" +
                    "    window.onload=function(){\n" +
                    "        timer=setTimeout(myClose,1000);\n" +
                    "    }\n" +
                    "</script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h3>令牌已过期，请重新操作</h3>\n" +
                    "    <span id=\"time\">5秒钟后自动关闭</span><br>\n" +
                    "</body>\n" +
                    "</html>";
        } catch (Exception e){
            e.printStackTrace();
            return "<!DOCTYPE HTML>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<title>倒计时自动关闭页面</title>\n" +
                    "<meta charset=\"utf-8\" />\n" +
                    "<script>\n" +
                    "    function myClose(){\n" +
                    "        var n=parseInt(time.innerHTML);\n" +
                    "        n--;\n" +
                    "        if(n>0){\n" +
                    "            time.innerHTML=n+\"秒钟后自动关闭\";\n" +
                    "            timer=setTimeout(myClose,1000);\n" +
                    "        }else{\n" +
                    "            close();\n" +
                    "        }\n" +
                    "    }\n" +
                    "    var timer=null;\n" +
                    "    window.onload=function(){\n" +
                    "        timer=setTimeout(myClose,1000);\n" +
                    "    }\n" +
                    "</script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h3>验证失败，请重新操作</h3>\n" +
                    "    <span id=\"time\">5秒钟后自动关闭</span><br>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }

}
