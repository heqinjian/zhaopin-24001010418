# 招聘系统（Recruitment System）

基于 Spring Boot + MyBatis Plus 的招聘网站后端，提供职位浏览、用户注册登录、简历管理、职位收藏与投递、图片上传等接口。数据持久化到 MySQL，通过 MyBatis Plus 的 BaseMapper 完成 CRUD；集成 SpringDoc 自动生成在线接口文档，使用 JWT 实现登录认证，用 Redis 做列表/详情查询缓存，并用 Bean Validation + 全局异常处理器统一校验参数与错误响应。

## 技术栈

- Java 17
- Spring Boot 3.2.2（spring-boot-starter-web）
- MyBatis Plus 3.5.5（mybatis-plus-spring-boot3-starter）
- MySQL 8.x / 9.x（mysql-connector-j）
- Spring Data Redis（spring-boot-starter-data-redis）
- Bean Validation（spring-boot-starter-validation）
- SpringDoc OpenAPI 2.3.0（接口文档 + Swagger UI）
- JJWT 0.12.3（JWT 生成与校验）
- Logback（Spring Boot 内置，`logback-spring.xml` 分级输出到文件）
- Maven 3.9+
- Lombok

## 目录结构

```
.
├── 第02步-数据绑定与接口实现(下).postman_collection.json   # 接口测试集合（Postman 导入）
├── 第05步-文件上传.postman_collection.json                # 文件上传测试集合
└── recruitment-system/                                     # Maven 工程
    ├── pom.xml
    └── src/main/
        ├── java/com/recruitment/
        │   ├── common/          # 统一响应 Result
        │   ├── config/          # MyBatisPlusConfig、SwaggerConfig、FileUploadConfig、WebMvcConfig、RedisConfig
        │   ├── context/         # UserContext（ThreadLocal 保存当前用户ID）
        │   ├── interceptor/     # JwtInterceptor（登录拦截）、LoggingInterceptor（请求日志）
        │   ├── controller/      # 控制层（职位/用户/简历/收藏/投递/文件）
        │   ├── exception/       # GlobalExceptionHandler（校验异常统一返回 Result）
        │   ├── service/         # 业务接口
        │   │   └── impl/        # 业务实现（基于 Mapper + Redis 缓存，含业务日志）
        │   ├── mapper/          # 5 个 Mapper（extends BaseMapper）
        │   ├── model/           # 实体类（@TableName/@TableId/@Schema）
        │   ├── util/            # JwtUtil、RedisUtil
        │   └── vo/              # LoginVO（token + user）
        └── resources/
            ├── application.yml
            ├── logback-spring.xml  # 日志级别与输出文件配置
            └── db/init.sql      # 建库建表 + 预置数据
```

## 快速开始

1. 先准备数据库：启动 MySQL，执行 `src/main/resources/db/init.sql`（自动创建 `recruitment_db` 及全部表、预置数据）
2. 启动 Redis（默认 `localhost:6379`，无密码；本机可执行 `D:\redis\redis-server.exe D:\redis\redis.windows.conf`，并用 `redis-cli ping` 确认返回 `PONG`）
3. 按需修改 `application.yml` 中的数据源用户名/密码、`file.upload-path` 文件上传目录，以及 `spring.data.redis` 连接信息
4. 启动工程：

```bash
cd recruitment-system
mvn spring-boot:run
```

启动后访问基础地址：`http://localhost:8080/api`

## 接口文档（Swagger UI）

启动后浏览器访问：

- Swagger UI：`http://localhost:8080/api/swagger-ui/index.html`
- OpenAPI JSON：`http://localhost:8080/api/v3/api-docs`

在 Swagger UI 中点击右上角 **Authorize**，输入登录接口返回的 token（无需手动加 `Bearer ` 前缀），即可在线调试受保护接口。

## 认证说明

除白名单外，所有接口都需要在请求头携带 JWT：

```
Authorization: Bearer <token>
```

未携带或 token 无效时返回 HTTP 401：

```json
{ "code": 401, "message": "请先登录", "data": null }
```

白名单（无需登录）：`/users/login`、`/users/register`、`/jobs/**`、`/hello/**`、`/files/upload`、`/uploads/**`、Swagger 相关路径、`/error`。

## 接口列表

| 模块 | 方法 | 路径 | 认证 | 说明 |
| --- | --- | --- | --- | --- |
| 职位 | GET | `/jobs/list` | 否 | 职位列表，支持 `city`、`keyword` 条件（可组合） |
| 职位 | GET | `/jobs/{id}` | 否 | 职位详情，每次访问 `viewCount + 1` |
| 用户 | POST | `/users/register` | 否 | 注册（@RequestBody JSON），返回数据不含密码 |
| 用户 | POST | `/users/login` | 否 | 登录，成功返回 `token` 与脱敏用户信息 |
| 用户 | GET | `/users/{id}` | 是 | 按 id 查询用户信息 |
| 用户 | GET | `/users/info` | 是 | 根据 token 获取当前登录用户信息 |
| 简历 | POST | `/resumes` | 是 | 新建简历 |
| 简历 | GET | `/resumes/{id}` | 是 | 简历详情 |
| 简历 | PUT | `/resumes/{id}` | 是 | 部分更新（仅覆盖请求体中非 null 字段） |
| 简历 | DELETE | `/resumes/{id}` | 是 | 删除简历 |
| 简历 | GET | `/resumes/list?userId=` | 是 | 某用户的简历列表 |
| 收藏 | POST | `/favorites/add` | 是 | 收藏职位，Body：`{"userId":1,"jobId":1}` |
| 收藏 | DELETE | `/favorites/remove?userId=&jobId=` | 是 | 取消收藏（物理删除） |
| 收藏 | GET | `/favorites/list?userId=` | 是 | 收藏的职位列表（返回 Job） |
| 投递 | POST | `/applications/apply` | 是 | 投递职位，Body：`{"userId":1,"jobId":2}` |
| 投递 | PUT | `/applications/cancel?userId=&jobId=` | 是 | 取消投递（只改状态，不删记录） |
| 投递 | GET | `/applications/list?userId=` | 是 | 投递列表（含职位信息、状态、时间） |
| 文件 | POST | `/files/upload` | 否 | 上传图片（multipart 字段名 `file`），返回 `url`、`filename` |
| 文件 | GET | `/uploads/{filename}` | 否 | 访问已上传的图片 |

## 文件上传说明

- 上传目录通过 `file.upload-path` 配置（默认 `D:/work/uploads/`），目录不存在时自动创建
- 仅支持 `jpg`、`jpeg`、`png`、`gif` 格式，其他格式返回错误提示
- 文件使用去横线的 UUID 重命名，避免重名
- 单文件大小上限 5MB，单次请求上限 10MB

## 参数校验与异常处理

校验注解写在实体字段上，Controller 参数前加 `@Valid` 才会在请求进入方法体之前执行：

| 接口 | 校验字段 | 规则 |
| --- | --- | --- |
| `POST /users/register` | `userName` | `@NotBlank` + `@Size(3,20)` |
| | `passWord` | `@NotBlank` + `@Size(6,20)` |
| | `email` | `@Email`（为 null 时不校验） |
| | `phone` | `@Pattern(^1[3-9]\\d{9}$)`（为 null 时不校验） |
| `POST /resumes` | `userId` | `@NotNull` |
| | `realName` | `@NotBlank` |

`@Valid` 不通过时抛出 `MethodArgumentNotValidException`，由 `exception/GlobalExceptionHandler` 接住，把所有字段错误拼成 `字段名: 提示; 字段名: 提示`，用 `Result.error` 返回（`code=500`，HTTP 200）：

```json
{
  "code": 500,
  "message": "userName: 用户名长度必须在3-20之间; passWord: 密码长度必须在6-20之间; email: 邮箱格式不正确",
  "data": null
}
```

- 校验失败发生在 Controller 之前，Service 不会执行，因此数据库不会有 INSERT
- `PUT /resumes/{id}` 是部分更新，**不加** `@Valid`；`POST /users/login` 也不加，登录只判断用户名是否存在、密码是否匹配
- 业务错误（如"用户名已存在"）仍由 Service 直接 `return Result.error(...)`，不抛异常，也就不需要自定义异常类
- 兜底的 `@ExceptionHandler(Exception.class)` 统一返回"系统异常，请稍后重试"，避免把堆栈细节暴露给前端

## 日志系统

用 Logback 把日志按级别分流，不再依赖 `System.out.println`（控制台一关就没了）。

### 级别约定

| 级别 | 用在哪 | 本项目例子 |
| --- | --- | --- |
| `log.info` | 正常做完的事 | 用户登录成功、简历创建成功、请求开始/结束 |
| `log.warn` | 请求到了，但业务没通过 | 用户名或密码错误、参数校验失败、创建简历失败 |
| `log.error` | 系统出错，需要堆栈 | 全局异常处理器兜底的系统异常 |

写法统一用 `{}` 占位符，不做字符串拼接：`log.info("用户登录成功: userId={}, userName={}", id, name)`。

### 输出文件

配置在 `src/main/resources/logback-spring.xml`，写到**运行目录**下的 `logs/` 文件夹（`mvn spring-boot:run` 时即 `recruitment-system/logs/`）：

| 文件 | 内容 |
| --- | --- |
| `logs/recruitment-info.log` | 只有 `INFO`（WARN、ERROR 不进来） |
| `logs/recruitment-error.log` | 只有 `ERROR`，系统异常的堆栈在这里 |
| `logs/recruitment.log` | `INFO`、`WARN`、`ERROR` 全都有 |

- 按天滚动，文件名带日期，保留 30 天（`maxHistory=30`）
- `WARN` 只在控制台和 `recruitment.log` 里，不会进 `recruitment-info.log`，也不会进 `recruitment-error.log`
- MyBatis 的 SQL 仍由 `application.yml` 的 `StdOutImpl` 打到控制台，不写入上述三个文件

### 记录位置

| 位置 | 级别 | 内容 |
| --- | --- | --- |
| `LoggingInterceptor`（preHandle） | INFO | `请求开始: GET /users/login` |
| `LoggingInterceptor`（afterCompletion） | INFO | `请求结束: POST /users/login \| 状态: 200 \| 耗时: 15ms` |
| `UserServiceImpl.login` | INFO / WARN | 登录请求、登录成功；用户名密码为空、用户名或密码错误 |
| `ResumeServiceImpl.create` | INFO / WARN | 简历创建成功（`resumeId`、`userId`）；真实姓名为空、用户不存在（带 `userId`） |
| `GlobalExceptionHandler` | WARN / ERROR | 参数校验失败（拼好的错误文本）；系统异常（第二个参数传异常本身，堆栈进日志） |

- 请求日志拦截器注册在 JWT 拦截器**前面**，先注册的 `preHandle` 先执行，所以没带 Token 被 401 拦下的请求也会留下「开始」「结束」两行
- 日志里只记 userId、userName、resumeId 这类标识，**不打印简历正文和密码**
- 日志文件不提交：`.gitignore` 已排除 `*.log` 与 `logs/`
- Windows 命令行窗口默认代码页是 936（GBK），而 CONSOLE 按 UTF-8 输出，直接 `mvn spring-boot:run` 会让控制台中文显示成乱码（`鑱熺儴…`）。启动前先执行 `chcp 65001` 把窗口切到 UTF-8（或用 IDEA 运行）即可；日志文件本身是 UTF-8，不受影响

## Redis 缓存说明

采用 Cache Aside（旁路缓存）模式：先查 Redis，命中直接返回；未命中再查 MySQL 并写回缓存，缓存 3 分钟（180 秒）过期。

| 缓存 Key | 内容 | 写入时机 | 删除时机 |
| --- | --- | --- | --- |
| `resume:list:{userId}` | 某用户简历列表 JSON | `listByUserId` 未命中后 | 创建/更新/删除简历成功后 |
| `resume:detail:{id}` | 单条简历 JSON | `getById` 未命中且记录存在时 | 更新/删除简历成功后 |
| `favorite:list:{userId}` | 某用户收藏的职位列表 JSON | `listByUser` 未命中后 | 收藏/取消收藏成功后 |

- 序列化：Key 用 `StringRedisSerializer`（可读文本），Value 用 `GenericJackson2JsonRedisSerializer`（JSON，并保存类型信息，支持 `List<Job>` 等还原）
- `Job` 因带 `@Builder` 会丢失无参构造方法，已补 `@NoArgsConstructor`、`@AllArgsConstructor`，否则从 Redis 还原 JSON 会失败
- 控制台会打印 `命中Redis缓存：<key>` / `Redis缓存未命中，查询MySQL：<key>`，便于对照 SQL 日志确认缓存生效

## 预置数据

- 预置账号：`demo` / `123456`
- 预置职位 3 条：
  - id=1：阿里巴巴 · Java开发工程师（杭州）
  - id=2：腾讯科技 · 前端开发工程师（深圳）
  - id=3：字节跳动 · Go开发工程师（北京）

## 接口测试

1. 启动 `RecruitmentApplication`
2. 打开 Postman / Apifox，导入根目录的 Postman 测试集合
3. 先调用登录接口获取 token，再在受保护接口的 Header 中配置 `Authorization: Bearer <token>`

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## License

MIT
