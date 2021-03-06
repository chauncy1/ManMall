package com.man.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true) // 链式调用
public class RoleInfo implements GrantedAuthority {
    private static final long serialVersionUID = 7088351652237386029L;

    private Long roleId;

    private String roleName;

    private String roleDesc;

    @Override
    public String getAuthority() {
        return roleName;
    }
}
