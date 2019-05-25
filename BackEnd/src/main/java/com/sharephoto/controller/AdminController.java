package com.sharephoto.controller;

import com.sharephoto.service.AdminService;
import com.sharephoto.utils.CSRFProtect;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PatchMapping("/user/{userId}/lock")
    @CSRFProtect
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, String> lockUser(@PathVariable("userId") Integer userId){
        return adminService.lockUser(userId);

    }

    @PatchMapping("/user/{userId}/unlock")
    @CSRFProtect
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, String> unlockUser(@PathVariable("userId") Integer userId){
        return adminService.unlockUser(userId);
    }

    @PatchMapping("/user/{userId}/block")
    @CSRFProtect
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, String> blockUser(@PathVariable("userId") Integer userId){
        return adminService.blockUser(userId);
    }

    @PatchMapping("/user/{userId}/unblock")
    @CSRFProtect
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, String> unblockUser(@PathVariable("userId") Integer userId){
        return adminService.unblockUser(userId);
    }

    @DeleteMapping("/tag/{tagId}")
    @CSRFProtect
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, String> deleteTag(@PathVariable("tagId") Integer tagId) {
        return adminService.deleteTag(tagId);
    }

    @GetMapping("/management/Info")
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, ?> getManageInfo(){
        return adminService.getInfo();
    }

    @GetMapping("/management/tags")
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, ?> manageTag(@RequestParam(value = "page", defaultValue = "1") Integer page){
        return adminService.manageTag(page);
    }

    @GetMapping("/management/users")
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, ?> manageUser(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "filterRule", defaultValue = "") String filterRule){
        return adminService.manageUser(page, filterRule);
    }

    @GetMapping("/management/photos")
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, ?> managePhoto(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "orderRule", defaultValue = "") String orderRule){
        return adminService.managePhoto(page, orderRule);
    }

    @GetMapping("/management/comments")
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator","Moderator"},logical=Logical.OR)
    public Map<String, ?> manageComment(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "orderRule", defaultValue = "") String orderRule){
        return adminService.manageComment(page, orderRule);
    }

    @GetMapping("/user/{userId}/adminSettings")
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator"})
    public Map<String, ?> getManageUserInfo(@PathVariable("userId") Integer userId) {
        Map<String, Object> result = null;
        try {
            result = adminService.getManageUserInfo(userId);
        } catch (NullPointerException e) {
            e.printStackTrace();
            result = new HashMap<>();
            result.put("message","用户不存在");
            result.put("type","info");
        }
        return result;
    }

    @PutMapping("/user/{userId}/adminSettings")
    @CSRFProtect
    @ResponseBody
    @RequiresUser
    @RequiresRoles(value={"Administrator"})
    Map<String, String> updateManageuUserInfo(@PathVariable("userId") Integer userId, @RequestBody Map<String ,Object> data){
        Map<String, String> result = null;
        try {
            result = adminService.updateManageUserInfo(userId, data);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            result = new HashMap<>();
            result.put("message", "用户名或邮件地址重复");
            result.put("type", "info");
        }

        return result;
    }
}
