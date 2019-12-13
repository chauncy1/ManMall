package com.man.service;

import com.man.entity.RoleInfo;
import com.man.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public List<RoleInfo> getRoleListByUserId(Long userId) {
        List<RoleInfo> s = userRoleMapper.getRoleListByUserId(userId);
        return s;
    }

}
