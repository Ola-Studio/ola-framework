CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id`            varchar(32) NOT NULL COMMENT '主键ID',
    `password`      varchar(100) DEFAULT NULL COMMENT '密码',
    `username`      varchar(45)  DEFAULT NULL COMMENT '用户名',
    `mobile`        varchar(45)  DEFAULT NULL COMMENT '手机',
    `avatar`        mediumtext   DEFAULT NULL COMMENT '头像',
    `status`        bigint       DEFAULT NULL COMMENT '状态',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `creator`       bigint       DEFAULT NULL COMMENT '创建人',
    `update_time`   datetime     DEFAULT NULL COMMENT '最后的更新时间',
    `last_modifier` VARCHAR(32)  DEFAULT NULL COMMENT '最后的更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='用户表';

CREATE INDEX INDEX_SYS_USER_USERNAME on sys_user (username);
CREATE INDEX INDEX_SYS_USER_MOBILE on sys_user (mobile);

CREATE TABLE IF NOT EXISTS `sys_role`
(
    `id`            varchar(32) NOT NULL COMMENT '主键ID',
    `name`          varchar(500)  DEFAULT NULL COMMENT '角色名',
    `remark`        varchar(1000) DEFAULT NULL COMMENT '备注',
    `create_time`   datetime      DEFAULT NULL COMMENT '创建时间',
    `creator`       bigint        DEFAULT NULL COMMENT '创建人',
    `update_time`   datetime      DEFAULT NULL COMMENT '最后的更新时间',
    `last_modifier` VARCHAR(32)   DEFAULT NULL COMMENT '最后的更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='角色表';

CREATE INDEX INDEX_SYS_ROLE_NAME on sys_role (name);

CREATE TABLE IF NOT EXISTS `sys_user_role`
(
    `user_id` varchar(32) NOT NULL COMMENT '用户ID',
    `role_id` varchar(32) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='用户角色关系表';

