package com.man.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.man.entity.RoleInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleMapper extends BaseMapper<RoleInfo> {

    List<RoleInfo> getRoleListByUserId(Long userId);
}
