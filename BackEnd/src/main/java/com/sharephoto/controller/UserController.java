package com.sharephoto.controller;

import com.sharephoto.service.UserService;
import com.sharephoto.utils.CSRFProtect;
import com.sharephoto.utils.confirmRequired;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Joher
 * @data 2019/5/20
 **/

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/avatar")
    @CSRFProtect
    @RequiresUser
    @confirmRequired
    public Map<String, String> uploadAvatar(MultipartFile file, HttpSession session){
        return userService.uploadAvatar(file, session);
    }

    @PutMapping("/thumbnail")
    @CSRFProtect
    @RequiresUser
    @confirmRequired
    public Map<String, String> cropAvatar(@RequestBody Map<String, Object> data, HttpSession session){
        try {
            return userService.cropAvatar(data, session);
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, String> message = new HashMap<>();
            message.put("message","剪裁失败，请稍后再试");
            message.put("type", "info");
            return message;
        }
    }

    @DeleteMapping("/user/{username}")
    @CSRFProtect
    @RequiresAuthentication
    public Map<String, String> deleteAccount(@PathVariable("username") String username){
        return userService.deleteAccount(username);
    }

    @GetMapping("/user/{username}/photos")
    public Map<String, Object> getUserIndex(@PathVariable("username") String username, @RequestParam(value = "page", required = true, defaultValue = "1") Integer page, HttpSession session){
        return userService.getUserIndex(username, page, session);
    }

    @GetMapping("/user/{username}/collections")
    public Map<String, Object> getUserIndexCollection(@PathVariable("username") String username, @RequestParam(value = "page", required = true, defaultValue = "1") Integer page, HttpSession session){
        return userService.getUserIndexCollection(username, page, session);
    }

    @GetMapping("/user/{username}/followers")
    public Map<String, Object> getUserIndexFollowers(@PathVariable("username") String username, @RequestParam(value = "page", required = true, defaultValue = "1") Integer page, HttpSession session){
        return userService.getUserIndexFollowers(username, page, session);
    }

    @GetMapping("/user/{username}/followings")
    public Map<String, Object> getUserIndexFollowings(@PathVariable("username") String username, @RequestParam(value = "page", required = true, defaultValue = "1") Integer page, HttpSession session){
        return userService.getUserIndexFollowings(username, page, session);
    }

    @GetMapping("/user/profileSetting")
    @RequiresUser
    public Map<String, String> getEditProfile(){
        return userService.getEditProfile();
    }

    @PutMapping("/user/profileSetting")
    @CSRFProtect
    @RequiresUser
    public Map<String, String> updateEditProfile(@RequestBody Map<String, Object> data, HttpSession session){
        return userService.updateEditProfile( (Map<String, String>) data.get("profileData"), session);
    }

    @GetMapping("/user/notificationSetting")
    @RequiresUser
    public Map<String, Object> getNotificationSetting(){
        return userService.getNotificationSetting();
    }

    @PutMapping("/user/notificationSetting")
    @CSRFProtect
    @RequiresUser
    public Map<String, Object> updateNotificationSetting(@RequestBody Map<String, Boolean> data){
        return userService.updateNotificationSetting(data);
    }

    @GetMapping("/user/privacySetting")
    @RequiresUser
    public Map<String, Object> getPrivacySetting(){
        return userService.getPrivacySetting();
    }

    @PutMapping("/user/privacySetting")
    @CSRFProtect
    @RequiresUser
    public Map<String, Object> updatePrivacySetting(@RequestBody Map<String, Boolean> data){
        return userService.updatePrivacySetting(data);
    }

    @PutMapping("/user/password")
    @CSRFProtect
    @RequiresAuthentication
    Map<String, String> changePassword(@RequestBody Map<String, String> data, HttpSession session){
        return userService.updatePassword(data, session);
    }

    @PutMapping("/user/email")
    @CSRFProtect
    @RequiresAuthentication
    Map<String, String> changeEmail(@RequestBody Map<String, String> data, HttpServletRequest request){
        try {
            return userService.updateEmail(data, request);
        }  catch (Exception e) {
            e.printStackTrace();
            Map<String, String> message = new HashMap<>();
            message.put("message","邮件发送失败，请稍后再试");
            message.put("type", "info");
            return message;
        }
    }

    @GetMapping(value = "/emailConfirm/{token:.+}", produces = "text/html;charset=UTF-8")
    @RequiresUser
    public String emailConfirm(@PathVariable("token") String token, HttpServletRequest request){
        try {
            return userService.emailConfirm(token, request);
        } catch (UnauthenticatedException e) {
            e.printStackTrace();
            return "请先登录";
        } catch (Exception e) {
            e.printStackTrace();
            return "您没有通过验证";
        }
    }

    @GetMapping("/csrfToken")
    public Map<String, String> getCSRFToken(HttpServletRequest request){
        String token = UUID.randomUUID().toString();
        request.getSession().setAttribute("CSRFToken", token);
        Map<String, String> result = new HashMap<>();
        result.put("csrfToken", token);
        return result;
    }

}
