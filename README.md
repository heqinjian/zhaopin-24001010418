# 招聘系统（Recruitment System）

基于 Spring Boot + MyBatis Plus 的招聘网站后端，提供职位浏览、用户注册登录、简历管理、职位收藏与投递、图片上传等接口。数据持久化到 MySQL，通过 MyBatis Plus 的 BaseMapper 完成 CRUD；集成 SpringDoc 自动生成在线接口文档，并使用 JWT 实现登录认证。

## 技术栈

- Java 17
- Spring Boot 3.2.2（spring-boot-starter-web）
- MyBatis Plus 3.5.5（mybatis-plus-spring-boot3-starter）
- MySQL 8.x / 9.x（mysql-connector-j）
- SpringDoc OpenAPI 2.3.0（接口文档 + Swagger UI）
- JJWT 0.12.3（JWT 生成与校验）
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
        │   ├── config/          # MyBatisPlusConfig、SwaggerConfig、FileUploadConfig、WebMvcConfig
        │   ├── context/         # UserContext（ThreadLocal 保存当前用户ID）
        │   ├── interceptor/     # JwtInterceptor（登录拦截）
        │   ├── controller/      # 控制层（职位/用户/简历/收藏/投递/文件）
        │   ├── service/         # 业务接口
        │   │   └── impl/        # 业务实现（基于 Mapper）
        │   ├── mapper/          # 5 个 Mapper（extends BaseMapper）
        │   ├── model/           # 实体类（@TableName/@TableId/@Schema）
        │   ├── util/            # JwtUtil
        │   └── vo/              # LoginVO（token + user）
        └── resources/
            ├── application.yml
            └── db/init.sql      # 建库建表 + 预置数据
```

## 快速开始

1. 先准备数据库：启动 MySQL，执行 `src/main/resources/db/init.sql`（自动创建 `recruitment_db` 及全部表、预置数据）
2. 按需修改 `application.yml` 中的数据源用户名/密码，以及 `file.upload-path` 文件上传目录
3. 启动工程：

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
