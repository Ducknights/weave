# weave (纹理)

基于 Spring Cloud 微服务架构的校园社交平台，提供用户认证、帖子发布、评论互动、社团管理、实时聊天、全文搜索与个性化推荐等功能。

## 技术栈

| 技术                  | 说明              |
| --------------------- | ----------------- |
| Spring Boot 3.3.4     | 基础框架            |
| Spring Cloud 2023.0.3 | 微服务治理          |
| Spring Cloud Alibaba  | Nacos 服务发现      |
| Spring Cloud Gateway  | API 网关           |
| Spring Security + JWT | 认证与鉴权          |
| MyBatis Plus 3.5.15   | ORM 框架           |
| MySQL 9.1             | 关系型数据库         |
| MongoDB               | 评论数据存储         |
| Elasticsearch         | 全文搜索引擎         |
| Redis                 | 缓存与排行榜         |
| RabbitMQ              | 消息队列            |
| MinIO                 | 对象存储            |
| Zipkin                | 分布式链路追踪       |
| Druid                 | 数据库连接池         |
| Hutool 5.8            | 工具类库            |

## 项目结构

```
weave-backend/
├── gateway/                               # API 网关
│   ├── filter/                            # JWT 认证过滤器
│   ├── config/                            # 白名单配置
│   └── exception/                         # 网关异常处理（WebFlux）
├── infrastructure/                        # 基础设施层
│   ├── common-model/                      # 公共模型（ApiResult、ApiStatus 接口、DTO、VO、常量、OpenAPI 配置）
│   ├── common-util/                       # 公共工具类（雪花 ID、JWT 工具）
│   ├── exception-spring-boot-starter/     # 全局异常处理器（AbstractBusinessException + GlobalExceptionHandler）
│   ├── redis-spring-boot-starter/         # Redis 自动配置与工具类
│   ├── rabbitmq-spring-boot-starter/      # RabbitMQ 自动配置与工具类
│   ├── mybatis-plus-spring-boot-starter/  # MyBatis Plus 分页插件自动配置
│   ├── security-spring-boot-starter/      # 微服务间请求头认证过滤器
│   └── minio-spring-boot-starter/         # MinIO 文件上传/下载/预签名 URL 自动配置
└── services/                              # 业务服务层
    ├── auth-service/                      # 认证服务（登录注册、验证码、Token 刷新）
    ├── user-service/                      # 用户服务（信息管理、关注、拉黑、头像上传）
    ├── post-service/                      # 帖子服务（发布、审核状态机、点赞收藏）
    ├── draft-service/                     # 草稿服务（草稿保存、提交审核状态机）
    ├── comment-service/                   # 评论服务（树形评论、点赞、MongoDB 存储）
    ├── club-service/                      # 社团服务（社团管理、成员管理、活动管理）
    ├── search-service/                    # 搜索服务（Elasticsearch 全文搜索、IK 分词）
    ├── recommend-service/                 # 推荐服务（协同过滤、每日相似度计算）
    ├── chat-service/                      # 聊天服务（私信、Netty-SocketIO 长轮询）
    ├── captcha-service/                   # 验证码服务（邮件发送、Redis 缓存）
    ├── rag-service/                       # RAG 服务（gRPC 调用 Python 端的 LLM 问答）
    └── rag-py-service/                    # RAG Python 端（文档加载、向量检索、LLM 生成）
```

## 模块说明

### Gateway 网关

- 统一入口，基于 Spring Cloud Gateway 进行路由转发
- 集成 JWT 认证过滤器，对请求进行 Token 校验
- 支持白名单路径免认证访问（如登录、注册接口）
- 通过 Nacos 实现服务发现与负载均衡

### 基础设施层 (infrastructure)

| 模块                          | 说明                                      |
| ----------------------------- | ----------------------------------------- |
| `common-model`                | 跨服务共享的 DTO、VO、ApiStatus 接口、OpenAPI 配置、枚举、MongoDB/ES 实体、MQ 常量 |
| `common-util`                 | 通用工具类（雪花 ID 生成器、JWT 工具类）     |
| `exception-spring-boot-starter` | 全局异常处理器：抽取各服务重复的异常处理逻辑，提供 AbstractBusinessException + GlobalExceptionHandler，各服务只需维护自己的 *ApiStatus 枚举 |
| `redis-spring-boot-starter`   | Redis 自动配置，封装 Set/ZSet 操作工具类      |
| `rabbitmq-spring-boot-starter`| RabbitMQ 自动配置与工具类                    |
| `mybatis-plus-spring-boot-starter` | MyBatis Plus 分页插件自动配置          |
| `security-spring-boot-starter`| 微服务间请求头认证过滤器                     |
| `minio-spring-boot-starter`   | MinIO 文件上传/下载/预签名 URL 自动配置       |

### 业务服务

#### auth-service（认证服务）

- 账号密码登录
- 发送注册验证码
- 验证码校验并注册
- 退出登录
- 刷新 Token

#### user-service（用户服务）

- 用户信息 CRUD（创建、查询、更新）
- 批量查询用户信息（支持 Feign 调用）
- 关注 / 取消关注
- 拉黑 / 解除拉黑
- 禁言管理
- 头像上传（MinIO）

#### post-service（帖子服务）

- 帖子发布、删除、查询（分页）
- 热门帖子、推荐帖子获取
- 点赞、收藏、浏览行为记录
- 文件上传与预签名 URL
- 通过 RabbitMQ 发布帖子行为消息（用于同步和推荐）

#### comment-service（评论服务）

- 基于 MongoDB 存储评论数据
- 发表评论、回复评论（树形结构）
- 分页查询、排序、点赞
- 支持评论隐藏/删除

#### club-service（社团服务）

- 社团创建、删除、查询
- 社团成员管理（加入、退出）
- 社团活动 CRUD

#### search-service（搜索服务）

- 基于 Elasticsearch 的全文搜索
- IK 中文分词器
- 监听 RabbitMQ 消息同步帖子数据到 ES（创建/更新/删除）
- 搜索结果与帖子详情聚合返回

#### recommend-service（推荐服务）

- 基于物品的协同过滤推荐算法（加权版）
- 权重比例：浏览(1) : 点赞(3) : 收藏(5)
- 每日凌晨 2 点定时计算帖子相似度矩阵
- 冷启动时返回热门帖子
- 通过 RabbitMQ 消费用户行为消息

#### chat-service（聊天服务）

- 用户间私信会话管理
- 消息发送与历史消息分页查询
- 长轮询（Long Polling）实现实时消息推送
- 支持文本消息类型

#### captcha-service（验证码服务）

- 监听 RabbitMQ 验证码队列
- 生成随机验证码并发送邮件
- 验证码 Redis 缓存（支持过期）

## 环境依赖

- JDK 22+
- MySQL 9.1+
- MongoDB
- Elasticsearch 7.x+（需安装 IK 分词器插件）
- Redis
- RabbitMQ
- Nacos
- MinIO
- Zipkin（可选，用于链路追踪）

## 快速开始

### 1. 准备环境

确保上述依赖服务均已启动，并在各服务 `application.yml` 中配置正确的连接地址。

### 2. 创建数据库

按照各服务配置创建对应的 MySQL 数据库：

- `weave-user`
- `weave-auth`
- `weave-post`
- `weave-club`
- `weave-chat`
- `weave-recommend`

### 3. 启动服务

按以下顺序启动各模块：

```bash
# 1. 启动基础设施
# 2. 启动网关
# 3. 启动业务服务（无依赖顺序，可并行）
mvn spring-boot:run
```

各服务默认端口：

| 服务                    | 端口   |
|:----------------------|:-----|
| gateway               | 80   |
| auth-service          | 4000 |
| captcha-service       | 4200 |
| post-service          | 4700 |
| draft-service         | 4701 |
| comment-service       | 4400 |
| club-service          | 4500 |
| search-service        | 4600 |
| recommend-service     | 4800 |
| chat-service          | 4300 |
| user-service          | 4100 |
| rag-service           | 4900 |

### 4. 访问

所有 API 通过网关统一入口访问：`http://localhost/api/**`

## API 响应规范

统一响应格式：

```json
{
  "code": 200,
  "message": "请求成功",
  "data": {}
}
```

`common-model` 提供 [ApiStatus](file:///d:/Graduation%20Design/weave-backend/infrastructure/common-model/src/main/java/com/weave/model/model/ApiStatus.java) 接口，各服务实现各自的 `*ApiStatus` 枚举。`exception-spring-boot-starter` 提供 [AbstractBusinessException](file:///d:/Graduation%20Design/weave-backend/infrastructure/exception-spring-boot-starter/src/main/java/com/weave/exception/AbstractBusinessException.java) 基类和统一的 [GlobalExceptionHandler](file:///d:/Graduation%20Design/weave-backend/infrastructure/exception-spring-boot-starter/src/main/java/com/weave/exception/GlobalExceptionHandler.java)，自动将业务异常转为上述 JSON 响应。
