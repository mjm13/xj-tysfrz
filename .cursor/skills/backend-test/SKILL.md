---
name: backend-test
description: 为后端 Spring Boot 代码生成并验证测试。在实现任何业务 Controller/Service 前调用，确保每个 AC 有可运行的测试作为验收门禁。
---

# 目标

以 **红-绿-重构** 循环驱动本项目后端实现：先写测试（RED），看它失败，再写最小实现（GREEN），最后清理（REFACTOR）。**禁止先写实现代码再补测试。**

---

# 项目测试基础设施（固定约束，不可绕过）

| 项 | 值 |
| --- | --- |
| 框架 | JUnit 5 + Spring Boot Test + Mockito（均由 `spring-boot-starter-test` 引入） |
| 测试数据库 | H2 in-memory，`MODE=MySQL`（`application-test.yml`） |
| Flyway | 每次测试启动自动执行全部迁移脚本，**不用 `@Sql` 重置** |
| 集成测试 profile | `@ActiveProfiles("test")`，启动类 `ZbptApplication` |
| 运行命令 | `JAVA_HOME=C:\Users\mjm13\.jdks\ms-21.0.10 mvn test`（本机 JDK 21 路径） |
| 测试类命名 | `<被测类>Test`，不加 `IT` 后缀 |

---

# 分层选型决策树

```
Q1: 是否需要 Spring 容器？
├── 否 → 纯 JUnit + Mockito（最快，用于 Service 单元测试、工具类）
└── 是 → Q2: 是否只测 Web 层（Controller 映射/响应格式/CORS）？
           ├── 是 → @WebMvcTest(XxxController.class)
           │         + @Import({GlobalExceptionHandler.class, CorsConfig.class})
           │         + @ActiveProfiles("dev")          ← CORS 配置是 dev profile
           │         + 用 @MockBean 隔离 Service 依赖
           └── 否 → @SpringBootTest + @AutoConfigureMockMvc
                     + @ActiveProfiles("test")          ← 走 H2 + Flyway
                     + 直接注入真实 Bean（不 Mock Service）
```

**何时用哪种：**

| 场景 | 注解 | 速度 | 示例 |
| --- | --- | --- | --- |
| Controller 路由/响应/CORS | `@WebMvcTest` | 快（<2s） | `PingControllerTest` |
| Controller 含业务 Service | `@WebMvcTest` + `@MockBean` | 快 | 见下方示例 |
| 含 DB 的链路验收 | `@SpringBootTest` | 慢（~5s） | `SystemInfoControllerTest` |
| Service 逻辑/不变量 | 纯 JUnit + Mockito | 极快 | 见下方示例 |
| Mapper（MyBatis-Plus） | **不测**，`@MockBean` 替代 | — | BaseMapper 方法视为叶节点 |

> **Mapper 规则**：继承 `BaseMapper`、方法名含 select/insert/update/delete、或标注 `@Mapper` 的方法——一律 `@MockBean`，不写 Mapper 层测试，不要求用户提供 DB 数据。

---

# TDD 执行步骤

## STEP 1 — 写测试（RED）

1. 读取 AC（验收标准），每条 AC 对应一个或多个 `@Test` 方法
2. 按分层决策树选注解
3. 测试方法命名：`<场景>_should<预期结果>`，例如：
   - `getInfo_shouldReturnRelease()`
   - `createUser_whenEmailBlank_shouldReturnBizError()`
4. 写完测试，**运行一次确认 RED**：

```powershell
$env:JAVA_HOME='C:\Users\mjm13\.jdks\ms-21.0.10'; mvn test -pl backend
```

预期：编译失败（类不存在）或断言失败——**任意一种都算合格的 RED**。  
如果测试直接 GREEN → 测试写错了，重写。

## STEP 2 — 写实现（GREEN）

- 只写让测试通过的最小代码
- 禁止在实现阶段加"备用"逻辑或未被测试覆盖的分支
- 实现完成后运行测试确认 GREEN：

```powershell
$env:JAVA_HOME='C:\Users\mjm13\.jdks\ms-21.0.10'; mvn test -pl backend
```

所有测试必须通过，**输出中不得有编译警告或 Spring 上下文错误**。

## STEP 3 — 重构（REFACTOR）

- 消除重复代码、改善命名
- 重构完再跑一次测试，保持 GREEN

---

# 典型模板

## Controller 切片测试（有 Service 依赖）

```java
@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class, CorsConfig.class})
@ActiveProfiles("dev")
class UserControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean UserService userService;    // Service 全部 Mock

    @Test
    void getUser_shouldReturnUserDto() throws Exception {
        given(userService.getById(1L)).willReturn(new UserDto(1L, "张三"));

        mockMvc.perform(get("/api/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value(0))
               .andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    void getUser_whenNotFound_shouldReturnBizError() throws Exception {
        given(userService.getById(99L)).willThrow(new BizException("用户不存在"));

        mockMvc.perform(get("/api/users/99"))
               .andExpect(jsonPath("$.code").value(1000))
               .andExpect(jsonPath("$.message").value("用户不存在"));
    }
}
```

## Service 单元测试（纯 JUnit + Mockito）

```java
class UserServiceTest {

    @Mock UserMapper userMapper;
    UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UserServiceImpl(userMapper);
    }

    @Test
    void create_whenEmailDuplicate_shouldThrowBizException() {
        given(userMapper.selectOne(any())).willReturn(new User());   // 模拟已存在

        assertThatThrownBy(() -> service.create(new CreateUserCmd("test@example.com")))
            .isInstanceOf(BizException.class)
            .hasMessageContaining("邮箱已存在");
    }
}
```

## 集成测试（DB + Flyway）

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderFlowTest {

    @Autowired MockMvc mockMvc;

    @Test
    void createOrder_shouldPersistAndReturn() throws Exception {
        mockMvc.perform(post("/api/orders")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content("""{"productId":1,"qty":2}"""))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.id").isNumber());
    }
}
```

---

# 验收门禁（与 openspec-superpowers-apply 联动）

每个 AC 勾选前必须满足：

- [ ] 有对应测试方法（命名可追溯到 AC）
- [ ] 看到该测试 RED（失败日志截图或输出）
- [ ] `mvn test` 全绿，无 `ERROR`、无 Spring 上下文失败
- [ ] 测试不依赖外部 MySQL（只用 H2 + Flyway）
- [ ] Mapper 相关依赖已 `@MockBean`，不询问用户提供 DB 数据

---

# 禁止行为

- **先写实现再补测试**（哪怕"就这一次"）
- 为通过测试修改 H2/Flyway 配置文件（测试应适配基础设施，不是反过来）
- 在 `@WebMvcTest` 中直接注入真实 Service（会导致 Spring 上下文缺依赖）
- 使用 `@Disabled` 跳过失败测试而不修复
- 在 `application-test.yml` 里设置 `spring.flyway.enabled=false`（破坏 DB 迁移验证）

---

# 常见错误速查

| 错误 | 原因 | 修法 |
| --- | --- | --- |
| `NoSuchBeanDefinitionException: XxxService` | `@WebMvcTest` 没有 `@MockBean` Service | 加 `@MockBean XxxService` |
| `Flyway: Found non-empty schema without schema history table` | H2 模式问题 | 确认 `application-test.yml` 有 `baseline-on-migrate: true` |
| `H2 does not support ... function` | 迁移脚本用了 MySQL 专有函数 | 改写为 H2 兼容 SQL 或用条件迁移 |
| `java.lang.UnsupportedClassVersionError` | JDK 不是 21 | 确认 `JAVA_HOME=C:\Users\mjm13\.jdks\ms-21.0.10` |
| 测试直接 GREEN（未见 RED） | 测试写错，可能在测 Mock 而非真实逻辑 | 删除实现代码，重新看 RED |
