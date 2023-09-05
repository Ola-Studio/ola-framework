package io.ola.rbac.utils;

import cn.hutool.core.thread.ThreadUtil;
import io.ola.common.utils.SpringUtils;
import io.ola.rbac.mode.UserOnlineInfo;
import io.ola.rbac.service.UserOnlineInfoProvider;

import java.util.Objects;

/**
 * @author yiuman
 * @date 2023/9/5
 */
public final class RbacUtils {
    public static final ThreadLocal<UserOnlineInfo> USER_ONLINE_INFO_THREAD_LOCAL = ThreadUtil.createThreadLocal(true);

    public static UserOnlineInfo getUserOnlineInfo() {
        UserOnlineInfo userOnlineInfo = USER_ONLINE_INFO_THREAD_LOCAL.get();
        if (Objects.isNull(userOnlineInfo)) {
            UserOnlineInfoProvider userOnlineInfoProvider = SpringUtils.getBean(UserOnlineInfoProvider.class);
            userOnlineInfo = userOnlineInfoProvider.get();
            put(userOnlineInfo);
        }

        return userOnlineInfo;
    }

    public static void put(UserOnlineInfo userOnlineInfo) {
        USER_ONLINE_INFO_THREAD_LOCAL.set(userOnlineInfo);
    }

    public static void clear() {
        USER_ONLINE_INFO_THREAD_LOCAL.remove();
    }

    private RbacUtils() {
    }
}
