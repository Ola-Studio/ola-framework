package io.ola.rbac.service;

import cn.hutool.core.thread.ThreadUtil;
import io.ola.rbac.mode.UserOnlineInfo;

import java.util.function.Supplier;

/**
 * @author yiuman
 * @date 2023/9/5
 */
public interface UserOnlineInfoProvider extends Supplier<UserOnlineInfo> {
}