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
    `user_id`       varchar(32) NOT NULL COMMENT '用户ID',
    `role_id`       varchar(32) NOT NULL COMMENT '角色ID',
    `create_time`   datetime    DEFAULT NULL COMMENT '创建时间',
    `creator`       bigint      DEFAULT NULL COMMENT '创建人',
    `update_time`   datetime    DEFAULT NULL COMMENT '最后的更新时间',
    `last_modifier` VARCHAR(32) DEFAULT NULL COMMENT '最后的更新人',
    PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='用户角色关系表';

CREATE TABLE IF NOT EXISTS `sys_organ`
(
    `id`            varchar(32) NOT NULL COMMENT '主键ID',
    `name`          varchar(500)  DEFAULT NULL COMMENT '组织名称',
    `parent_id`     varchar(32)   DEFAULT NULL COMMENT '父级ID',
    `remark`        varchar(1000) DEFAULT NULL COMMENT '备注',
    `create_time`   datetime      DEFAULT NULL COMMENT '创建时间',
    `creator`       bigint        DEFAULT NULL COMMENT '创建人',
    `update_time`   datetime      DEFAULT NULL COMMENT '最后的更新时间',
    `last_modifier` VARCHAR(32)   DEFAULT NULL COMMENT '最后的更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='组织机构表';
CREATE INDEX INDEX_SYS_ORGAN_NAME on sys_organ (name);
CREATE INDEX INDEX_SYS_ORGAN_PARENT_ID on sys_organ (parent_id);

CREATE TABLE IF NOT EXISTS `sys_user_organ`
(
    `user_id`  varchar(32) NOT NULL COMMENT '用户ID',
    `organ_id` varchar(32) NOT NULL COMMENT '组织ID',
    PRIMARY KEY (`user_id`, `organ_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='用户组织关系表';

CREATE TABLE IF NOT EXISTS `sys_resource`
(
    `id`            varchar(32) NOT NULL COMMENT '主键ID',
    `name`          varchar(500)  DEFAULT NULL COMMENT '资源名称',
    `parent_id`     varchar(32)   DEFAULT NULL COMMENT '父级ID',
    `type`          varchar(10)   DEFAULT NULL COMMENT '类型',
    `code`          varchar(128)  DEFAULT NULL COMMENT '编码',
    `icon`          varchar(1000) DEFAULT NULL COMMENT '图标',
    `uri`           varchar(1000) DEFAULT NULL COMMENT '请求地址',
    `method`        varchar(8)    DEFAULT NULL COMMENT '请求方式',
    `expression`    varchar(1000) DEFAULT NULL COMMENT '表达式',
    `remark`        varchar(1000) DEFAULT NULL COMMENT '备注',
    `create_time`   datetime      DEFAULT NULL COMMENT '创建时间',
    `creator`       bigint        DEFAULT NULL COMMENT '创建人',
    `update_time`   datetime      DEFAULT NULL COMMENT '最后的更新时间',
    `last_modifier` VARCHAR(32)   DEFAULT NULL COMMENT '最后的更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='资源表';
CREATE INDEX INDEX_SYS_RESOURCE_NAME on sys_resource (name);
CREATE INDEX INDEX_SYS_RESOURCE_PARENT_ID on sys_resource (parent_id);
CREATE INDEX INDEX_SYS_RESOURCE_URI on sys_resource (uri);
CREATE INDEX INDEX_SYS_RESOURCE_CODE on sys_resource (code);

CREATE TABLE IF NOT EXISTS `sys_resource_owner`
(
    `owner_id`    varchar(32) NOT NULL COMMENT '拥有者ID（用户/角色）',
    `resource_id` varchar(32) NOT NULL COMMENT '组织ID',
    `owner_type`  varchar(8)  NOT NULL COMMENT '拥有者的类型',
    PRIMARY KEY (`owner_id`, `resource_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='资源拥有者关系表';

CREATE TABLE IF NOT EXISTS `sys_user_data`
(
    `id`            varchar(32)  NOT NULL COMMENT '主键ID',
    `user_id`       varchar(500) NOT NULL COMMENT '用户ID',
    `data_id`       varchar(32)  DEFAULT NULL COMMENT '数据ID',
    `data_code`     varchar(100) DEFAULT NULL COMMENT '数据编码',
    `source`        varchar(32)  DEFAULT NULL COMMENT '来源',
    `operations`    varchar(64)  DEFAULT NULL COMMENT '操作类型',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `creator`       bigint       DEFAULT NULL COMMENT '创建人',
    `update_time`   datetime     DEFAULT NULL COMMENT '最后的更新时间',
    `last_modifier` VARCHAR(32)  DEFAULT NULL COMMENT '最后的更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='用户数据表（一般用于数据分享，数据协作）';