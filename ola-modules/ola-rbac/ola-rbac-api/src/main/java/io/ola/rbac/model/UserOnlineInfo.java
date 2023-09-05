package io.ola.rbac.mode;

import io.ola.rbac.entity.Resource;
import io.ola.rbac.entity.Role;
import io.ola.rbac.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.util.List;

/**
 * @author yiuman
 * @date 2023/9/5
 */
@Data
public class UserOnlineInfo {
    private HttpServletRequest currentRequest;
    private User user;
    private Resource currentResource;
    private List<Role> roles;
    private List<Resource> userResources;
}
