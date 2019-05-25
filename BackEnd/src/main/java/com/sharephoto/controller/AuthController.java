package com.sharephoto.controller;

import com.sharephoto.service.AuthService;
import com.sharephoto.utils.CSRFProtect;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@ResponseBody
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/user")
    @CSRFProtect
    public Map<String, String> register(@RequestBody Map<String ,Object> data, HttpServletRequest request){
        try {
            return authService.addUser(data, request);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> message = new HashMap<>();
            message.put("message","注册失败，请稍后再试");
            message.put("type","warning");
            return message;
        }
    }

    @PostMapping("/session")
    @CSRFProtect
    public Map<String, ?> login(@RequestBody Map<String ,Object> data) {
        return authService.login(data);
    }

    @DeleteMapping("/session")
    @CSRFProtect
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        Map<String, String> message = new HashMap<>();
        message.put("message","您已成功退出");
        message.put("type","success");
        return message;
    }

    @GetMapping(value = "/confirm/{token:.+}", produces = "text/html;charset=UTF-8")
    @RequiresUser
    public String confirm(@PathVariable("token") String token, HttpServletRequest request){
        try {
            return authService.confirm(token, request);
        } catch (Exception e) {
            e.printStackTrace();
            return "您没有通过验证";
        }
    }

    @PatchMapping("/password")
    @CSRFProtect
    public Map<String, String> forgetPassword(@RequestBody Map<String, String> data, HttpServletRequest request){
        try {
            return authService.forgetPassword(data, request);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> message = new HashMap<>();
            message.put("message","邮件发送失败，请稍后再试");
            message.put("type", "info");
            return message;
        }
    }

    @PostMapping("/password")
    @CSRFProtect
    public Map<String, String> resetPassword(@RequestBody Map<String, Object> data){
        try {
            return authService.resetPassword(data);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> message = new HashMap<>();
            message.put("message","认证失败，请稍后再试");
            message.put("type", "info");
            return message;
        }
    }

}
