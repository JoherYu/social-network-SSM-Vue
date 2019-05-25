package com.sharephoto.controller;

import com.sharephoto.entity.Photo;
import com.sharephoto.service.MainService;
import com.sharephoto.utils.CSRFProtect;
import com.sharephoto.utils.confirmRequired;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @PostMapping("/photo")
    @CSRFProtect
    @RequiresUser
    @confirmRequired
    @RequiresRoles(value = {"Administrator","Moderator","User"}, logical = Logical.OR)
    public ResponseEntity upload(MultipartFile file) {
        try {
            mainService.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/uploads/{filename}")
    public String getPhoto(@PathVariable(value = "filename") String filename) {
        return System.getProperty("joher") + "/uploads/" + filename;
    }

    @GetMapping("/avatars/{avatar}")
    public String getAvatar(@PathVariable("avatar") String avatar) {
        return System.getProperty("joher") + "/uploads/avatars/" + avatar;
    }

    @GetMapping("/photos/random")
    public List<Photo> explore() {
        return mainService.getRandomPhoto();
    }

    @GetMapping("/search/{category}/{q}")
    public Map<String, ?> search(@PathVariable("category") String category, @PathVariable("q") String q, @RequestParam(value = "page", defaultValue = "1") Integer page) {
        return mainService.search(category, q, page);
    }

    @GetMapping("/user/CenterData")
    public Map<String, ?> getSelfCenter(@RequestParam(value = "page", defaultValue = "1") Integer page) {
        return mainService.getSelfCenter(page);
    }

    @PostMapping("/collection/{id}")
    @CSRFProtect
    public Map<String, String> collectPhoto(@PathVariable("id") Integer id) {
        return mainService.collectPhoto(id);
    }

    @DeleteMapping("/collection/{id}")
    @CSRFProtect
    public Map<String, String> uncollectPhoto(@PathVariable("id") Integer id) {
        return mainService.uncollectPhoto(id);
    }

    @GetMapping("/user/popup/{id}")
    public Object getPopupData(@PathVariable("id") Integer userId) {
        return mainService.getPopupData(userId)[0];
    }

    @PostMapping("/follow/{username}")
    @CSRFProtect
    public Map<String, String> follow(@PathVariable("username") String username) {
        return mainService.followUser(username);
    }

    @DeleteMapping("/follow/{username}")
    @CSRFProtect
    public Map<String, String> unfollow(@PathVariable("username") String username) {
        return mainService.unfollowUser(username);
    }

    @GetMapping("/photo/{id}")
    public Map<String, ?> getPhotoIndex(@PathVariable("id") Integer photoId, @RequestParam(value = "page", defaultValue = "1") Integer page) {
        return mainService.getPhotoIndex(photoId, page);
    }

    @DeleteMapping("/photo/{id}")
    @CSRFProtect
    @RequiresUser
    public Map<String, ?> deletePhoto(@PathVariable("id") Integer photoId){
        return mainService.deletePhoto(photoId);
    }

    @GetMapping("/photo/{id}/next")
    public Map<String, ?> nextPhoto(@PathVariable("id") Integer photoId){
        return mainService.nextPhoto(photoId);
    }

    @GetMapping("/photo/{id}/pre")
    public Map<String, ?> prePhoto(@PathVariable("id") Integer photoId){
        return mainService.prePhoto(photoId);
    }

    @PatchMapping("/photo/{id}/report")
    @CSRFProtect
    @RequiresUser
    @confirmRequired
    public Map<String, String> reportPhoto(@PathVariable("id") Integer photoId){
        return mainService.reportPhoto(photoId);
    }

    @PatchMapping("/photo/{id}/description")
    @CSRFProtect
    @RequiresUser
    public Map<String, String> editPhotoDescription(@PathVariable("id") Integer photoId, @RequestBody Map<String ,String> description){
        return mainService.editPhotoDescription(photoId, description.get("description"));
    }

    @PostMapping("photo/{id}/tag")
    @CSRFProtect
    @RequiresUser
    public Map<String, Object> addTags(@PathVariable("id") Integer photoId, @RequestBody Map<String ,String> tags){
        return mainService.addTags(photoId, tags.get("tags"));
    }

    @DeleteMapping("photo/{photoId}/tag/{tagId}")
    @CSRFProtect
    @RequiresUser
    public Map<String, Object> deleteTag(@PathVariable("photoId") Integer photoId, @PathVariable("tagId") Integer tagId){
        return mainService.deleteTags(photoId, tagId);
    }

    @PatchMapping("photo/{photoId}/canComment")
    @CSRFProtect
    @RequiresUser
    public Map<String, String> setComment(@PathVariable("photoId") Integer photoId){
        return mainService.setComment(photoId);
    }

    @PostMapping("photo/{id}/comment")
    @CSRFProtect
    @RequiresUser
    @RequiresRoles(value = {"Administrator","Moderator","User"}, logical=Logical.OR)
    public Map<String, String> addComment(@PathVariable("id") Integer photoId, @RequestBody Map<String, Object> data){
        return mainService.addComment(photoId, (Integer) data.get("reply"), (String) data.get("body"));
    }

    @PatchMapping("/comment/{id}/report")
    @CSRFProtect
    @RequiresUser
    @confirmRequired
    public Map<String, String> reportComment(@PathVariable("id") Integer id){
        return mainService.reportComment(id);
    }

    @DeleteMapping("/comment/{id}")
    @CSRFProtect
    @RequiresUser
    public Map<String, String> deleteComment(@PathVariable("id") Integer id){
        return mainService.deleteComment(id);
    }

    @GetMapping("/notifications")
    @RequiresUser
    public Map<String, Object> getNotification(@RequestParam(value = "page", required = true, defaultValue = "1") Integer page, @RequestParam(value = "filterRule", required = true, defaultValue = "all") String filterRule){
        return mainService.getNotification(page, filterRule);
    }

    @PatchMapping("/notification/{id}/read")
    @CSRFProtect
    @RequiresUser
    public Map<String, String> readNotification(@PathVariable("id") Integer id){
        return mainService.readNotification(id);
    }

    @PatchMapping("/notifications/read")
    @CSRFProtect
    @RequiresUser
    public Map<String, String> readAllNotification(){
        return mainService.readAllNotification();
    }

    @GetMapping("/tag/{id}/photos")
    public Map<String, Object> getTagPhotos(@PathVariable("id") Integer id, @RequestParam(value = "page", required = true, defaultValue = "1") Integer page, @RequestParam(value = "filterRule", required = true, defaultValue = "time") String filterRule){
        return mainService.getTagPhotos(id, page, filterRule);
    }

    @GetMapping("/photo/{id}/collectors")
    public Map<String, Object> getPhotoCollectors(@PathVariable("id") Integer id, @RequestParam(value = "page", required = true, defaultValue = "1") Integer page){
        return mainService.getPhotoCollectors(id, page);
    }
}
