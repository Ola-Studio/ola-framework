package io.ola.crud.model;

import com.mybatisflex.annotation.Table;

/**
 * SortHelper测试用的实体类
 *
 * @author yiuman
 * @date 2026/4/10
 */
@Table("test_entity")
public class TestEntity {

    private Long id;
    private String username;
    private String createdAt;
    private String updatedAt;
}