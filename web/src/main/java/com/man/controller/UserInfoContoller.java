package com.man.controller;

import com.man.dto.request.AddUserRequest;
import com.man.entity.RoleInfo;
import com.man.entity.UserInfo;
import com.man.service.UserInfoService;
import com.man.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserInfoContoller {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserRoleService userRoleService;

    @GetMapping("/getall")
    public List<UserInfo> selectAll() {
        return userInfoService.selectAllWithRedisHash();
    }

    @GetMapping("/getall1")
    public List<UserInfo> selectAll1() {
        return userInfoService.selectAll();
    }

    @GetMapping("/getidname")
    public List<UserInfo> getIdName() {
        return userInfoService.getIdName();
    }

    @PreAuthorize("hasAuthority('caiji')")
    @GetMapping("/kankanquanxian")
    public List<RoleInfo> getRoleById(@RequestParam Long userId) {
        return userRoleService.getRoleListByUserId(userId);
    }

    @PostMapping("/add")
    public int addUserInfoWithLock(@Validated @RequestBody AddUserRequest user) {
        return userInfoService.addUserInfoWithLock(user);
    }

    @PostMapping("/update")
    public int minusUserScore(@Validated @RequestParam AddUserRequest user) {
        return userInfoService.update(user);
    }

    @PostMapping("/minusScore")
    public int minusUserScoreByRedis(@RequestParam String userId, @RequestParam Integer score) {
        return userInfoService.minusUserScoreByRedis(userId, score);
    }

    @PostMapping("/delete/{userId}")
    public int deleteById(@PathVariable String userId) {
        return userInfoService.deleteById(userId);
    }

}
