# OLA Framework 使用指南

OLA Framework 是一个基于 Spring Boot 的 CRUD 快速开发框架，封装了通用的增删改查接口和常用工具，助你快速构建 REST API。

---

## 1. 快速开始

### 1.1 Maven 依赖

```xml
<dependencies>
    <!-- 引入 ola-crud 模块 -->
    <dependency>
        <groupId>io.ola</groupId>
        <artifactId>ola-crud</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    
    <!-- 如果需要 HTTP 响应封装 -->
    <dependency>
        <groupId>io.ola</groupId>
        <artifactId>ola-common</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 1.2 响应格式

所有 API 返回统一封装 `R<T>`：

| 方法 | 说明 |
|------|------|
| `R.ok()` | 成功，无返回数据 |
| `R.ok(data)` | 成功，带返回数据 |
| `R.badRequest()` | 请求参数错误 |
| `R.error()` | 服务器错误 |

---

## 2. 定义实体

### 2.1 基础实体

```java
package com.example.entity;

import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_user")
public class User extends BaseAudit {
    
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer status;
}
```

### 2.2 审计字段

继承 `BaseAudit` 自动获得：
- `creator` - 创建人（自动填充）
- `createTime` - 创建时间（自动填充）
- `lastModifier` - 最后修改人（自动填充）
- `updateTime` - 更新时间（自动填充）

### 2.3 逻辑删除

使用 `@DeleteTag` 标记逻辑删除字段：

```java
@DeleteTag
private Integer deleted;
```

带有 `@DeleteTag` 的实体调用 `delete()` 时会执行 UPDATE 而非 DELETE。

### 2.4 排序字段

使用 `@Sort` 标记排序字段：

```java
@Sort
private Integer sortOrder;
```

### 2.5 数据脱敏

使用 `@Sensitive` 注解标记敏感字段：

```java
@Sensitive(strategy = SensitiveStrategy.PHONE)
private String phone;

@Sensitive(strategy = SensitiveStrategy.USERNAME)
private String username;

@Sensitive(strategy = SensitiveStrategy.ID_CARD)
private String idCard;

@Sensitive(strategy = SensitiveStrategy.ADDRESS)
private String address;
```

内置策略：`USERNAME`、`ID_CARD`、`PHONE`、`ADDRESS`、`UUID`

---

## 3. 定义 REST API

### 3.1 基础 CRUD API

让接口继承 `BaseRESTAPI<ENTITY>`，自动获得完整 CRUD 能力：

```java
package com.example.api;

import com.example.entity.User;
import io.ola.crud.rest.BaseRESTAPI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/users")
public class UserApi implements BaseRESTAPI<User> {
    
}
```

**自动获得的接口：**

| 方法 | URL | 说明 |
|------|-----|------|
| POST | `/users` | 新增实体 |
| POST | `/users` (form) | 表单新增/修改 |
| DELETE | `/users/{id}` | 删除实体 |
| GET | `/users` | 分页查询 |
| GET | `/users/list` | 列表查询 |
| GET | `/users/{id}` | 根据ID查询 |

### 3.2 自定义查询接口

如果只需要查询功能，继承 `BaseQueryAPI<ENTITY>`：

```java
@RestController
@RequestMapping("/users")
public class UserApi implements BaseQueryAPI<User> {
    // 只有查询接口，没有增删改
}
```

---

## 4. 查询构建

### 4.1 定义查询对象

创建查询类，使用查询注解标记字段：

```java
package com.example.query;

import io.ola.crud.query.annotation.*;
import lombok.Data;

/**
 * 用户查询条件
 */
@Data
public class UserQuery {
    
    @Equals
    private String username;
    
    @Like
    private String email;
    
    @GT
    private Integer status;
    
    @LE
    private Integer status;
    
    @In("status")
    private List<Integer> statusList;
    
    @NotEquals
    private Integer deleted;
}
```

### 4.2 查询注解说明

| 注解 | 说明 | 示例 |
|------|------|------|
| `@Equals` | 等于 | `status = 1` |
| `@NotEquals` | 不等于 | `status != 1` |
| `@Like` | 模糊查询 | `name LIKE '%keyword%'` |
| `@NotLike` | 模糊查询取反 | `name NOT LIKE '%keyword%'` |
| `@GT` | 大于 | `id > 1` |
| `@GE` | 大于等于 | `id >= 1` |
| `@LT` | 小于 | `id < 1` |
| `@LE` | 小于等于 | `id <= 1` |
| `@In` | IN 查询 | `status IN (1,2,3)` |
| `@IsNull` | 为空 | `field IS NULL` |
| `@IsNotNull` | 不为空 | `field IS NOT NULL` |

所有注解支持参数：
- `mapping` - 映射到实体字段名
- `clauses` - 连接条件 (`AND`/`OR`)
- `require` - 是否必填
- `handler` - 自定义条件处理器

### 4.3 关联查询类

在 API 接口上使用 `@Query` 注解指定查询类：

```java
@Query(UserQuery.class)
@RestController
@RequestMapping("/users")
public class UserApi implements BaseRESTAPI<User> {
    
}
```

---

## 5. 字段注入

### 5.1 @BeforeSave

在保存时自动注入字段值：

```java
public class User extends BaseAudit {
    
    @BeforeSave(UserIdInjector.class)
    private String creator;
    
    @BeforeSave(NowInjector.class)
    private LocalDateTime createTime;
}
```

### 5.2 @BeforeUpdate

在更新时自动注入字段值：

```java
public class User extends BaseAudit {
    
    @BeforeUpdate(UserIdInjector.class)
    private String lastModifier;
    
    @BeforeUpdate(NowInjector.class)
    private LocalDateTime updateTime;
}
```

### 5.3 内置Injector

| Injector | 说明 |
|----------|------|
| `UserIdInjector` | 注入当前用户ID |
| `NowInjector` | 注入当前时间（支持 Date、long、LocalDateTime） |

### 5.4 自定义Injector

实现 `Injector` 接口：

```java
package com.example.inject;

import io.ola.crud.inject.Injector;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.UUID;

@Component
public class UuidInjector implements Injector {
    
    @Override
    public Object getInjectValue(Field field) {
        return UUID.randomUUID().toString();
    }
}

// 使用
@BeforeSave(UuidInjector.class)
private String uuid;
```

---

## 6. 校验

### 6.1 Bean Validation

实体字段使用标准校验注解：

```java
public class User {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为3-20")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

### 6.2 分组校验

框架自动根据新增/修改选择校验分组：

| 操作 | 校验分组 |
|------|----------|
| 新增 | `Default` + `Save` |
| 修改 | `Default` + `Modify` |

```java
public class User {
    
    @NotBlank(groups = Save.class, message = "用户名不能为空")
    private String username;
    
    @NotNull(groups = Modify.class, message = "ID不能为空")
    private Long id;
}
```

---

## 7. Service 层

### 7.1 获取Service

通过 `CRUD` 工具类获取 Service 实例：

```java
// 通过 API 类获取
CrudService<User> service = CRUD.getService(UserApi.class);

// 直接通过实体类获取
CrudService<User> service = CRUD.getCrudService(UserEntity.class);
```

### 7.2 Service 方法

`CrudService<ENTITY>` 继承自 `QueryService`、`EditableService`、`DeletableService`：

**查询方法：**
```java
// 根据ID查询
User user = service.get(id);

// 条件查询
User user = service.get(queryWrapper);

// 查询列表
List<User> list = service.list();

// 批量查询
List<User> list = service.list(ids);

// 分页查询
Page<User> page = service.page(page, queryWrapper);

// 统计数量
long count = service.count(queryWrapper);

// 判断是否为新增
boolean isNew = service.isNew(entity);
```

**保存方法：**
```java
// 保存（新增或修改自动判断）
User saved = service.save(entity);

// 批量保存
Iterable<User> saved = service.saveAll(entities);

// 新增前回调
void beforeSave(T entity);

// 保存后回调
void afterSave(T entity);
```

**删除方法：**
```java
// 根据ID删除
service.delete(id);

// 根据实体删除
service.delete(entity);

// 批量删除
service.deleteByIds(ids);

// 条件删除
service.deleteByQuery(queryWrapper);

// 删除前回调
void beforeDelete(ENTITY entity);
```

---

## 8. 条件注入器

实现 `ConditionInjector` 接口，为所有查询自动添加条件：

```java
package com.example.inject;

import com.mybatisflex.core.query.QueryWrapper;
import io.ola.crud.inject.ConditionInjector;
import org.springframework.stereotype.Component;

@Component
public class TenantConditionInjector implements ConditionInjector {
    
    @Override
    public void inject(QueryWrapper queryWrapper, Class<?> entityClass) {
        // 自动添加租户条件
        queryWrapper.where("tenant_id = #{currentTenantId}");
    }
}
```

在 API 上使用 `@ConditionInject` 启用：

```java
@ConditionInject
public class UserApi implements BaseRESTAPI<User> {
    
}
```

---

## 9. 完整示例

### 9.1 实体类

```java
package com.example.entity;

import com.mybatisflex.annotation.Table;
import io.ola.crud.annotation.BeforeSave;
import io.ola.crud.annotation.BeforeUpdate;
import io.ola.crud.annotation.DeleteTag;
import io.ola.crud.annotation.Sensitive;
import io.ola.crud.enums.SensitiveStrategy;
import io.ola.crud.inject.impl.NowInjector;
import io.ola.crud.inject.impl.UserIdInjector;
import io.ola.crud.model.BaseAudit;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_user")
public class User extends BaseAudit {
    
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20)
    private String username;
    
    @NotBlank
    private String password;
    
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;
    
    @Sensitive(strategy = SensitiveStrategy.EMAIL)
    @Email
    private String email;
    
    @DeleteTag
    private Integer deleted;
}
```

### 9.2 查询类

```java
package com.example.query;

import io.ola.crud.query.annotation.*;
import lombok.Data;

@Data
public class UserQuery {
    
    @Like
    private String username;
    
    @Like
    private String email;
    
    @Equals
    private Integer status;
    
    @In
    private List<Integer> statusList;
}
```

### 9.3 REST API

```java
package com.example.api;

import com.example.entity.User;
import com.example.query.UserQuery;
import io.ola.crud.rest.BaseRESTAPI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Query(UserQuery.class)
public class UserApi implements BaseRESTAPI<User> {
    
}
```

---

## 10. API 一览

### 10.1 REST 接口

| 方法 | URL | 说明 | 请求体 |
|------|-----|------|--------|
| POST | `/users` | 新增 | JSON |
| POST | `/users` | 表单新增/修改 | Form |
| DELETE | `/users/{id}` | 删除 | - |
| GET | `/users` | 分页查询 | Query参数 |
| GET | `/users/list` | 列表查询 | Query参数 |
| GET | `/users/{id}` | 根据ID查询 | - |

### 10.2 Service 接口

**QueryService:**
- `getId(entity)` - 获取实体ID
- `get(id)` - 根据ID查询
- `get(queryWrapper)` - 条件查询
- `list()` - 查询全部
- `list(ids)` - 批量查询
- `list(queryWrapper)` - 条件查询列表
- `page(page, queryWrapper)` - 分页查询
- `count(queryWrapper)` - 统计数量

**EditableService:**
- `save(entity)` - 保存（新增/修改）
- `saveAll(entities)` - 批量保存
- `isNew(entity)` - 判断是否新增
- `beforeSave(entity)` - 保存前回调
- `afterSave(entity)` - 保存后回调

**DeletableService:**
- `delete(id)` - 根据ID删除
- `delete(entity)` - 根据实体删除
- `deleteByIds(ids)` - 批量删除
- `deleteByQuery(queryWrapper)` - 条件删除
- `beforeDelete(entity)` - 删除前回调

### 10.3 查询注解

`@Equals`, `@NotEquals`, `@Like`, `@NotLike`, `@GT`, `@GE`, `@LT`, `@LE`, `@In`, `@IsNull`, `@IsNotNull`

### 10.4 字段注解

`@BeforeSave`, `@BeforeUpdate`, `@DeleteTag`, `@Sort`, `@Sensitive`

---

## 11. 常见问题

**Q: 如何自定义主键生成策略？**
A: 使用 MyBatis-Flex 的 `@Id` 注解配置主键策略。

**Q: 如何实现软删除？**
A: 在实体字段上添加 `@DeleteTag` 注解，调用 `delete()` 时会自动执行 UPDATE。

**Q: 如何添加租户隔离？**
A: 实现 `ConditionInjector` 接口，并通过 `@ConditionInject` 启用。

**Q: 如何处理关联查询？**
A: MyBatis-Flex 支持关联查询配置，具体参考 MyBatis-Flex 文档。
