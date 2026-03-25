# AGENTS.md - OLA Framework Development Guide

This document provides guidance for AI agents working on the OLA Framework codebase.

## Project Overview

**OLA Framework** is a multi-module Java 17 Spring Boot framework for building REST APIs with CRUD operations, security, and RBAC modules.

**Key Technologies:**
- Java 17
- Spring Boot 3.2.5
- Maven (multi-module)
- MyBatis-Flex for ORM
- Hutool for utilities
- Lombok for boilerplate reduction

**Modules:**
- `ola-common` - Shared utilities and HTTP response types
- `ola-crud` - Generic CRUD service and REST API infrastructure
- `ola-security` - Authentication/authorization with JWT
- `ola-modules/ola-rbac` - Role-based access control
- `ola-starter` - Auto-configuration starters
- `ola-server` - Application entry point

---

## Build, Lint, and Test Commands

### Full Build
```bash
# Build all modules without tests
mvn clean package -DskipTests

# Build with checkstyle validation
mvn clean package

# Build specific module
mvn clean package -pl ola-crud -am
```

### Checkstyle (Code Style)
```bash
# Run checkstyle only
mvn checkstyle:check

# Generate checkstyle report
mvn checkstyle:checkstyle
```

### Running Tests
```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run a single test method
mvn test -Dtest=ClassName#testMethodName

# Run tests in specific module
mvn test -pl ola-crud

# Skip tests during build
mvn package -DskipTests
```

### Maven Options
```bash
# Run with parallel threads
mvn test -T 1C

# Show detailed output
mvn test -X

# Update snapshots
mvn clean package -U
```

---

## Code Style Guidelines

### Checkstyle Rules (Enforced)

The project uses **Checkstyle** with `checkstyle.xml` in the root. Key rules:

| Rule | Limit |
|------|-------|
| Max line length | 200 characters |
| Max method length | 80 lines |
| Max method parameters | 8 |
| Max nested for loops | 3 |
| Max nested if depth | 3 |
| Max nested try depth | 3 |
| Max file length | 1500 lines |

**Checkstyle runs automatically during the `validate` phase.** All violations must be fixed before build succeeds.

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Package names | lowercase, dot-separated | `io.ola.crud.service` |
| Class names | PascalCase | `BaseCrudService`, `EntityMeta` |
| Interface names | PascalCase | `CrudService`, `Authorize` |
| Method names | camelCase | `save()`, `getById()` |
| Variable names | camelCase | `entityClass`, `idSet` |
| Constant names | UPPER_SNAKE_CASE | `DEFAULT_BATCH_SIZE` |
| Generic type params | PascalCase | `<ENTITY>`, `<ID extends Serializable>` |
| Annotation names | PascalCase | `@DeleteTag`, `@BeforeSave` |

### Java Type Rules
- `TypeName` (classes/interfaces): `^[A-Z][a-zA-Z0-9]*$`
- `PackageName`: `^[a-z]+(\.[a-z][a-z0-9]*)*$` (lowercase only)
- `ConstantName`: `^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$`

### Import Organization

Imports should follow this order (no blank lines between groups):
1. `java.*` standard library
2. `javax.*` packages
3. Third-party (`cn.hutool.*`, `com.mybatisflex.*`, `org.springframework.*`)
4. Project imports (`io.ola.*`)

**No unused imports allowed.** Checkstyle will flag them.

### Code Block Rules

- **Braces required** for all `if`, `for`, `while`, `do`, `switch` statements (even single-line bodies)
- **Left curly brace** on same line as declaration
- **Modifier order**: `public` → `protected` → `private` → `abstract` → `static` → `final` → `transient` → `volatile` → `synchronized` → `native` → `strictfp`
- **Avoid nested blocks** - extract to methods when possible
- **No empty blocks** - use comments or simplify design

### Javadoc Requirements

- **All public classes** must have Javadoc with `@author` and `@date`
- Use Chinese for inline comments (project convention)
- Include `@param` for generic type parameters

```java
/**
 * CRUD基本接口
 *
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/7/25
 */
public interface BaseRESTAPI<ENTITY> {
```

---

## Error Handling Patterns

### Response Wrapping

Always use the `R<T>` class for API responses:
```java
// Success
return R.ok();
return R.ok(data);

// Error
return R.badRequest();
return R.badRequest("Custom message");
return R.error();
return R.error(statusCode, "Message");
```

### Validation

Use Hutool's validation utilities:
```java
import cn.hutool.extra.validation.ValidationUtil;

// With groups for save vs update
BeanValidationResult result = ValidationUtil.warpValidate(entity, validateGroups);
Assert.isTrue(result.isSuccess(), () -> new ValidateException(...));
```

### Exception Types

- `AuthenticationException` - Security/authentication failures
- `NoPermissionException` - Authorization failures
- `ValidateException` (Hutool) - Input validation failures

---

## Project Conventions

### Interface/Implementation Pattern

- **API interfaces** define REST endpoints with Spring annotations
- **Service interfaces** define business logic contracts
- **Abstract base classes** provide shared implementations
- Implementation suffix: `Impl` (e.g., `BaseCrudService`)

### Directory Structure
```
src/main/java/io/ola/{module}/
├── rest/          # REST API interfaces
├── service/        # Service interfaces
│   └── impl/      # Service implementations
├── model/         # Domain models and entities
├── annotation/    # Custom annotations
├── enums/         # Enumerations
├── properties/    # Configuration properties
├── inject/        # Dependency injection helpers
├── query/         # Query building utilities
└── serializer/    # JSON serializers
```

### Generic Type Parameters

Common conventions:
- `<ENTITY>` - Domain entity type
- `<ID extends Serializable>` - Entity identifier type
- `<T>` - General purpose type parameter
- `<DAO>` - Data access object type
- `<S>` - Service type

### Lombok Usage

Lombok is used extensively:
- `@Data` - Generates getters, setters, equals, hashCode, toString
- `@SuppressWarnings` - Suppress specific warnings when needed
- `@Retention(RetentionPolicy.RUNTIME)` - For annotations

---

## Git Workflow

### Pre-commit Hook
The project has a pre-commit hook (`pre-commit`) that runs `mvn checkstyle:check` before allowing commits. **Commits will fail if checkstyle violations exist.**

### Commit Message Format
```
type(scope): description

Examples:
feat(crud): add batch save functionality
fix(security): resolve token refresh issue
chore(checkstyle): update line length limit
```

### Branch Naming
```
feature/module-name        # New features
fix/module-name           # Bug fixes
refactor/module-name      # Code refactoring
docs/module-name          # Documentation
```

---

## Common Tasks

### Adding a New Module
1. Create module directory with `pom.xml`
2. Add module to root `pom.xml` `<modules>` list
3. Add dependencies to `ola-crud` or `ola-security` as appropriate
4. Follow existing module structure

### Adding a New CRUD Entity
1. Create entity class with `@Table` annotation (MyBatis-Flex)
2. Extend `BaseCrudService` for service layer
3. Extend `BaseRESTAPI` for REST endpoints
4. Use `@BeforeSave`/`@BeforeUpdate` for lifecycle hooks

### Adding a New Security Endpoint
1. Add endpoint method to `AuthenticateAPI` interface
2. Implement business logic in corresponding service
3. Register in `SecurityProperties` if path needs configuration

---

## Quick Reference

- **Build command**: `mvn clean package`
- **Style check**: `mvn checkstyle:check`
- **Run single test**: `mvn test -Dtest=ClassName#methodName`
- **Response wrapper**: `R<T>` from `io.ola.common.http`
- **Logging**: Use appropriate logging framework
- **Config**: Use `@ConfigurationProperties` for typed config
