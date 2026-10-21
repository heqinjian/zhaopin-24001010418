# 招聘系统（Recruitment System）

基于 Spring Boot 的招聘网站后端，提供职位浏览、用户注册登录、简历管理、职位收藏与投递等接口。使用内存仓库（MockDataStore）存储数据，无需数据库即可运行。

## 技术栈

- Java 17
- Spring Boot 3.2.2（spring-boot-starter-web）
- Maven 3.9+
- Lombok

## 目录结构

```
.
├── 第02步-数据绑定与接口实现(下).postman_collection.json   # 接口测试集合（Postman 导入）
└── recruitment-system/                                     # Maven 工程
    ├── pom.xml
    └── src/main/java/com/recruitment/
        ├── common/          # 统一响应 Result
        ├── controller/      # 控制层（职位/用户/简历/收藏/投递）
        ├── service/         # 业务接口
        │   └── impl/        # 业务实现
        ├── model/           # 实体类
        └── data/            # MockDataStore 内存仓库 + 预置数据
```

## 快速开始

```bash
cd recruitment-system
# 方式一：Maven
mvn spring-boot:run
# 方式二：Maven Wrapper
./mvnw spring-boot-run
```

启动后访问基础地址：`http://localhost:8080/api`

## 接口列表

| 模块 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 职位 | GET | `/jobs/list` | 职位列表，支持 `city`、`keyword` 条件（可组合） |
| 职位 | GET | `/jobs/{id}` | 职位详情，每次访问 `viewCount + 1` |
| 用户 | POST | `/users/register` | 注册（@RequestBody JSON），返回数据不含密码 |
| 用户 | POST | `/users/login` | 登录，用户名/密码错误统一提示 |
| 用户 | GET | `/users/{id}` | 按 id 查询用户信息 |
| 简历 | POST | `/resumes` | 新建简历 |
| 简历 | GET | `/resumes/{id}` | 简历详情 |
| 简历 | PUT | `/resumes/{id}` | 部分更新（仅覆盖请求体中非 null 字段） |
| 简历 | DELETE | `/resumes/{id}` | 删除简历 |
| 简历 | GET | `/resumes/list?userId=` | 某用户的简历列表 |
| 收藏 | POST | `/favorites/add` | 收藏职位，Body：`{"userId":1,"jobId":1}` |
| 收藏 | DELETE | `/favorites/remove?userId=&jobId=` | 取消收藏（物理删除） |
| 收藏 | GET | `/favorites/list?userId=` | 收藏的职位列表（返回 Job） |
| 投递 | POST | `/applications/apply` | 投递职位，Body：`{"userId":1,"jobId":2}` |
| 投递 | PUT | `/applications/cancel?userId=&jobId=` | 取消投递（只改状态，不删记录） |
| 投递 | GET | `/applications/list?userId=` | 投递列表（含职位信息、状态、时间） |

## 预置数据

- 预置账号：`demo` / `123456`
- 预置职位 3 条：
  - id=1：阿里巴巴 · Java开发工程师（杭州）
  - id=2：腾讯科技 · 前端开发工程师（深圳）
  - id=3：字节跳动 · Go开发工程师（北京）

## 接口测试

1. 启动 `RecruitmentApplication`
2. 打开 Postman / Apifox，导入根目录的 `第02步-数据绑定与接口实现(下).postman_collection.json`
3. 按文件夹顺序（职位 → 用户 → 简历 → 收藏 → 投递）逐项测试

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
