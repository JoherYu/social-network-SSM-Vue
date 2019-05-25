package com.sharephoto.service;

import com.sharephoto.dao.UserMapper;
import com.sharephoto.entity.User;
import com.sharephoto.utils.GenerateAvatar;
import com.sharephoto.utils.JWTToken;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.sharephoto.shiro.MyRealm.generateHash;
import static com.sharephoto.utils.EmailSender.sendEmail;
import static com.sharephoto.utils.JWTToken.parseJWT;

@Service("authService")
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, ?> login (Map<String ,Object> data){
        Map<String, String> message = new HashMap<>();

        String email = (String) data.get("email");
        String username = null;
        try {
            username = userMapper.selectUsernameByEmail(email.toLowerCase());
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "用户名不存在");
            message.put("type", "warning");
            return message;
        }
        if (username == null){
            message.put("message", "用户名不存在");
            message.put("type", "warning");
            return message;
        }

        User user = new User();
        user.setUsername(username);
        String oriUsername = userMapper.selectOriUsernaem(username);
        String passwordHash = generateHash(oriUsername, (String)data.get("password"));
        user.setPasswordHash(passwordHash);
        boolean rememberMe = (boolean) data.get("rememberMe");

        try {
            this.login(user, rememberMe);
        }catch (UnknownAccountException e) {
            message.put("message", "用户名不存在");
            message.put("type", "warning");
            return message;
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            message.put("message", "密码错误");
            message.put("type", "warning");
            return message;
        } catch (LockedAccountException e) {
            e.printStackTrace();
            message.put("message", "您的帐户以被封禁");
            message.put("type", "warning");
            return message;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            message.put("message", "登录失败，请稍后再试");
            message.put("type", "warning");
            return message;
        }

        Map<String, Object> result = new HashMap<>();
        User userInfo = userMapper.selectLoginUserInfoByEmail(email);
        result.put("username",userInfo.getUsername());
        result.put("notificationCount",userInfo.getNotificationCount());
        result.put("avatarS","/avatars/" + userInfo.getAvatarS());
        result.put("avatarM","/avatars/" + userInfo.getAvatarM());
        result.put("name",userInfo.getName());
        result.put("canModerate",userInfo.isCanModerate());
        result.put("canComment",userInfo.isCanComment());
        result.put("isAdmin",userInfo.isAdmin());
        result.put("message","恭喜您登录成功！");
        result.put("type", "success");
        result.put("rememberMe", rememberMe);
        return result;

    }

    @Override
    @Transactional
    public String confirm(String token, HttpServletRequest request) throws Exception {
        Subject currentUser = SecurityUtils.getSubject();
        String currentUsername = (String) currentUser.getPrincipal();
        Integer currentUserId = null;
        try {
            currentUserId = userMapper.selectUserIdByUsername(currentUsername);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "请先登录";
        }

        String currentEmail = userMapper.selectEmailById(currentUserId);

        try {
            parseJWT(token, "confirm" ,currentUserId);
            userMapper.updateConfirmById(currentUserId);
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
                    "    <h3>帐号确认成功</h3>\n" +
                    "    <span id=\"time\">5秒钟后自动关闭</span><br>\n" +
                    "</body>\n" +
                    "</html>";
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            String newToken = JWTToken.createJWT("confirm", currentUserId, null);
            String requestUrl = request.getScheme() + "://" + request.getServerName();
            String content = "<p>" + currentUsername + ", 你好</p>\n" +
                    "<p>欢迎您加入<b>分相</b>！</p>\n" +
                    "<p>请访问下面链接进行邮箱地址的确认操作:<br>\n" +
                    "    <a href=\"" + requestUrl + "/confirm/" + newToken + "\">" + requestUrl + "/confirm/" + token + "</a>\n" +
                    "</p>\n" +
                    "<small>(请勿回复此邮件)</small>";
            new Thread(new Runnable() {
                public void run() {
                    try {
                        sendEmail(content, "[分相]电子邮件确认", currentEmail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

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
                    "    <h3>令牌已过期，已自动向您的邮箱重新发送确认邮件</h3>\n" +
                    "    <span id=\"time\">5秒钟后自动关闭</span><br>\n" +
                    "</body>\n" +
                    "</html>";
        } catch (Exception e){
            e.printStackTrace();
            String newToken = JWTToken.createJWT("confirm", currentUserId, null);
            String requestUrl = request.getScheme() + "://" + request.getServerName();
            String content = "<p>" + currentUsername + ", 你好</p>\n" +
                    "<p>欢迎您加入<b>分相</b>！</p>\n" +
                    "<p>请访问下面链接进行邮箱地址的确认操作:<br>\n" +
                    "    <a href=\"" + requestUrl + "/confirm/" + newToken + "\">" + requestUrl + "/confirm/" + token + "</a>\n" +
                    "</p>\n" +
                    "<small>(请勿回复此邮件)</small>";
            new Thread(new Runnable() {
                public void run() {
                    try {
                        sendEmail(content, "[分相]电子邮件确认", currentEmail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
                    "    <h3>令牌无效，已自动向您的邮箱重新发送确认邮件</h3>\n" +
                    "    <span id=\"time\">5秒钟后自动关闭</span><br>\n" +
                    "</body>\n" +
                    "</html>";
        }

    }

    @Override
    public Map<String, String> forgetPassword(Map<String, String> data, HttpServletRequest request) throws Exception {
        Map<String, String> message = new HashMap<>();

        String email = data.get("email").toLowerCase();
        String username = userMapper.selectUsernameByEmail(email);
        if (username == null){
            message.put("message", "帐户不存在");
            message.put("type", "warning");
            return message;
        }
        Integer id = userMapper.selectUserIdByUsername(username);
        if (id == null){
            message.put("message", "帐户不存在");
            message.put("type", "warning");
            return message;
        }

        String token = JWTToken.createJWT("resetPassword", id, null);
        String requestUrl = request.getScheme() + "://" + request.getServerName();
        String content = "<p>" + username + ", 你好</p>\n" +
                "<p>点击下面的链接来重置密码:<br>\n" +
                "    <a href=\"" + requestUrl + "/main/reset/" + token + "\">" + requestUrl + "/main/reset/" + token + "</a>\n" +
                "</p>\n" +
                "<small>(请勿回复此邮件)</small>";
        new Thread(new Runnable() {
            public void run() {
                try {
                    sendEmail(content, "[分相]重置密码", email);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        message.put("message", "邮件已发送，请查看邮箱");
        message.put("type", "info");
        return message;
    }

    @Override
    public Map<String, String> resetPassword(Map<String, Object> data) throws Exception {
        Map<String, String> message = new HashMap<>();

        String token = (String) data.get("token");
        Map<String, String> authData = (Map<String, String>) data.get("passwordData");
        String password = authData.get("confirmPassword");
        String username = userMapper.selectUsernameByEmail(authData.get("email").toLowerCase());
        Integer id = userMapper.selectUserIdByUsername(username);

        try {
            parseJWT(token, "resetPassword" ,id);
            String oriUsername = userMapper.selectOriUsernaem(username);
            String passwordHash = generateHash(oriUsername, password);
            userMapper.updatePassword(username, passwordHash);
            message.put("message", "密码已更新");
            message.put("type", "success");
            return message;
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            message.put("message", "令牌已过期，请重新进行操作");
            message.put("type", "warning");
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            message.put("message", "令牌无效，请重新进行操作");
            message.put("type", "warning");
            return message;
        }
    }

    @Override
    public void login(User user, boolean rememberMe){
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPasswordHash());
            token.setRememberMe(rememberMe);
            currentUser.login(token);
        }
    }

    @Override
    @Transactional
    public Map<String, String> addUser(Map<String, Object> authData, HttpServletRequest request) throws Exception {

        Map<String, String> data = (Map<String, String>) authData.get("authData");

        User user = new User();
        user.setEmail(data.get("email").toLowerCase());
        user.setUsername(data.get("username"));
        user.setOriUsername(data.get("username"));
        user.setName(data.get("name"));
        String passwordHash = generateHash(data.get("username"), data.get("confirmPassword"));
        user.setPasswordHash(passwordHash);

        Map<String, String> message = new HashMap<>();
        try {
            userMapper.insertUser(user);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            message.put("message", "用户名或邮件地址已存在");
            message.put("type", "info");
            return message;
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            message.put("message", "请输入必选项");
            message.put("type", "info");
            return message;
        }


        Integer currentUserId = null;
        try {
            currentUserId = userMapper.selectUserIdByUsername(data.get("username"));
            String[] avatars = GenerateAvatar.generateAvatar(data.get("username"));
            userMapper.updateCropAvatar(currentUserId, avatars[0], avatars[1], avatars[2]);
        } catch (IOException e) {
            e.printStackTrace();
            message.put("message", "注册成功，请上传自定义头像");
            message.put("type", "info");
            return message;
        } catch (NullPointerException e) {
            e.printStackTrace();
            message.put("message", "注册失败，请稍后再试");
            message.put("type", "warning");
            return message;
        }

        String token = JWTToken.createJWT("confirm", currentUserId, null);
        String requestUrl = request.getScheme() + "://" + request.getServerName();
        String content = "<p>" + data.get("username") + ", 你好</p>\n" +
                "<p>欢迎您加入<b>分相</b>！</p>\n" +
                "<p>请访问下面链接进行邮箱地址的确认操作:<br>\n" +
                "    <a href=\"" + requestUrl + "/confirm/" + token + "\">" + requestUrl + "/confirm/" + token + "</a>\n" +
                "</p>\n" +
                "<small>(请勿回复此邮件)</small>";
        new Thread(new Runnable() {
            public void run() {
                try {
                    sendEmail(content, "[分相]电子邮件确认", data.get("email"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



        message.put("message", "注册成功，请登录邮箱进行激活以获取更多权限");
        message.put("type", "success");
        return message;

    }
}
