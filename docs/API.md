# Weave API 接口文档

## 通用说明

- **Base URL**: `http://localhost:80/api`
- **统一响应格式**:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {}
  }
  ```
- **认证方式**: JWT Token，请求头 `Authorization: Bearer <token>`（白名单路径除外）
- **白名单路径**（无需认证）: `/api/auth/login`、`/api/auth/register/**`、`/api/auth/logout`

---

## 一、认证服务 (auth-service)

### 1. 账号密码登录

> 白名单接口，无需 Token

**POST** `/api/auth/login`

| 参数     | 位置 | 类型   | 必填 | 说明     |
| -------- | ---- | ------ | ---- | -------- |
| email    | body | string | 是   | 邮箱     |
| password | body | string | 是   | 密码，≥6位 |

**成功响应** (200):
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "xxx",
    "refreshToken": "xxx"
  }
}
```

### 2. 发送注册验证码

> 白名单接口

**POST** `/api/auth/register/sendCode`

| 参数     | 位置 | 类型   | 必填 | 说明     |
| -------- | ---- | ------ | ---- | -------- |
| email    | body | string | 是   | 接收验证码的邮箱 |
| password | body | string | 是   | 用户密码，≥6位 |

**成功响应** (200):
```json
{ "code": 200, "message": "验证码发送成功", "data": {} }
```

### 3. 验证码校验并注册

> 白名单接口

**POST** `/api/auth/register/verifyCode`

| 参数     | 位置 | 类型   | 必填 | 说明       |
| -------- | ---- | ------ | ---- | ---------- |
| email    | body | string | 是   | 邮箱       |
| password | body | string | 是   | 密码，≥6位  |
| code     | body | string | 是   | 验证码，6位  |

**成功响应** (201):
```json
{ "code": 201, "message": "注册成功", "data": {} }
```

### 4. 退出登录

**POST** `/api/auth/logout`

无请求参数。

**成功响应** (200):
```json
{ "code": 200, "message": "退出登录成功", "data": {} }
```

### 5. 刷新 AccessToken

**POST** `/api/auth/access`

无请求参数。从请求头中的 refreshToken 生成新 Token。

**成功响应** (200):
```json
{ "code": 200, "message": "令牌刷新成功", "data": { "accessToken": "xxx" } }
```

### 6. 刷新 RefreshToken

**POST** `/api/auth/refresh`

**成功响应** (200):
```json
{ "code": 200, "message": "令牌刷新成功", "data": { "refreshToken": "xxx" } }
```

---

## 二、用户服务 (user-service)

### 2.1 用户信息

### 获取当前用户信息

**GET** `/api/user/info`

**成功响应**:
```json
{
  "id": 1,
  "username": "张三",
  "avatar": "/avatar/xxx.jpg",
  "email": "user@example.com",
  ...
}
```

### 根据ID获取用户信息

**GET** `/api/user/info/{id}`

| 参数 | 位置  | 类型   | 必填 | 说明    |
| ---- | ----- | ------ | ---- | ------- |
| id   | path  | long   | 是   | 用户ID  |

**成功响应**:
```json
{
  "id": 1,
  "username": "张三",
  "avatar": "/avatar/xxx.jpg"
}
```

### 批量获取用户信息

**POST** `/api/user/info/batch`

| 参数 | 位置 | 类型          | 必填 | 说明        |
| ---- | ---- | ------------- | ---- | ----------- |
| ids  | body | Set\<Long\>   | 是   | 用户ID集合   |

**成功响应**:
```json
{
  "1": { "id": 1, "username": "张三", ... },
  "2": { "id": 2, "username": "李四", ... }
}
```

### 更新用户信息

**PUT** `/api/user/info`

| 参数     | 位置 | 类型     | 必填 | 说明         |
| -------- | ---- | -------- | ---- | ------------ |
| (userInfo) | body | UserInfo | 是   | 需更新的字段  |

### 用户在线心跳

**POST** `/api/user/info/online`

刷新用户在线状态，返回是否成功。

### 2.2 头像管理

### 上传头像

**POST** `/api/users/avatar/upload`

`Content-Type: multipart/form-data`

| 参数 | 位置 | 类型         | 必填 | 说明     |
| ---- | ---- | ------------ | ---- | -------- |
| file | body | MultipartFile | 是   | 头像文件  |

### 获取文件预签名URL

**GET** `/api/users/avatar/url?path={path}&expiry={expiry}`

| 参数   | 类型   | 必填 | 默认值 | 说明          |
| ------ | ------ | ---- | ------ | ------------- |
| path   | string | 是   | -      | 文件存储路径   |
| expiry | int    | 否   | 3600   | 有效期(秒)    |

### 批量获取文件预签名URL

**GET** `/api/users/avatar/urls?paths={path1}&paths={path2}&expiry={expiry}`

| 参数   | 类型          | 必填 | 默认值 | 说明         |
| ------ | ------------- | ---- | ------ | ------------ |
| paths  | List\<String\> | 是   | -      | 文件路径列表  |
| expiry | int           | 否   | 3600   | 有效期(秒)   |

### 2.3 关注管理

### 关注用户

**POST** `/api/user/follow/{targetUserId}`

| 参数          | 位置 | 类型 | 必填 | 说明       |
| ------------- | ---- | ---- | ---- | ---------- |
| targetUserId  | path | long | 是   | 目标用户ID  |

### 取消关注

**DELETE** `/api/user/follow/{targetUserId}`

### 获取关注列表

**GET** `/api/user/follow?page={page}&size={size}`

| 参数  | 类型 | 必填 | 默认值 | 说明   |
| ----- | ---- | ---- | ------ | ------ |
| page  | int  | 否   | 0      | 页码   |
| size  | int  | 否   | 20     | 每页数  |

**成功响应**: `[1, 2, 3, ...]`（用户ID列表）

### 2.4 拉黑管理

### 拉黑用户

**POST** `/api/user/block/{targetUserId}`

### 解除拉黑

**DELETE** `/api/user/block/{targetUserId}`

### 获取拉黑列表

**GET** `/api/user/block?page={page}&size={size}`

### 2.5 禁言管理

### 禁言用户

**POST** `/api/user/mute/{targetUserId}`

### 解除禁言

**DELETE** `/api/user/mute/{targetUserId}`

### 获取禁言列表

**GET** `/api/user/mute?page={page}&size={size}`

### 2.6 用户行为记录

### 获取收藏列表

**GET** `/api/user/actions/collect?page={page}&size={size}`

### 获取点赞列表

**GET** `/api/user/actions/like?page={page}&size={size}`

### 获取浏览历史

**GET** `/api/user/actions/history?page={page}&size={size}`

---

## 三、帖子服务 (post-service)

### 3.1 帖子管理

### 创建帖子

**POST** `/api/post`

| 参数       | 位置 | 类型          | 必填 | 说明       |
| ---------- | ---- | ------------- | ---- | ---------- |
| clubId     | body | long          | 否   | 所属社团ID  |
| title      | body | string        | 是   | 帖子标题    |
| content    | body | string        | 是   | 帖子内容    |
| coverImage | body | List\<String\> | 否   | 封面图片URL |

### 获取推荐帖子

**GET** `/api/post/recommend`

返回个性化推荐的帖子列表（基于协同过滤算法）。

### 获取热门帖子

**GET** `/api/post/hot?pageNum={pageNum}&pageSize={pageSize}`

| 参数     | 类型 | 必填 | 说明   |
| -------- | ---- | ---- | ------ |
| pageNum  | int  | 是   | 页码   |
| pageSize | int  | 是   | 每页数  |

### 获取最新帖子

**GET** `/api/post/new?pageNum={pageNum}&pageSize={pageSize}`

### 根据ID获取帖子详情

**GET** `/api/post/{id}`

### 批量获取帖子

**POST** `/api/post/batch`

| 参数 | 位置 | 类型          | 必填 | 说明       |
| ---- | ---- | ------------- | ---- | ---------- |
| ids  | body | List\<Long\>  | 是   | 帖子ID列表  |

### 更新帖子

**PUT** `/api/post/{id}`

| 参数    | 位置 | 类型    | 必填 | 说明     |
| ------- | ---- | ------- | ---- | -------- |
| id      | path | long    | 是   | 帖子ID   |
| title   | body | string  | 否   | 新标题   |
| content | body | string  | 否   | 新内容   |

### 删除帖子

**DELETE** `/api/post/{id}`

### 3.2 帖子操作

### 点赞帖子

**POST** `/api/post/{id}/like`

### 取消点赞

**POST** `/api/post/{id}/unlike`

### 收藏帖子

**POST** `/api/post/{id}/collect`

### 取消收藏

**POST** `/api/post/{id}/uncollect`

### 3.3 帖子文件

### 上传帖子图片

**POST** `/api/post/files`

`Content-Type: multipart/form-data`

| 参数  | 位置 | 类型               | 必填 | 说明     |
| ----- | ---- | ------------------ | ---- | -------- |
| files | body | List\<MultipartFile\> | 是   | 图片文件列表 |

### 获取文件预签名URL

**GET** `/api/post/files/url?path={path}&expiry={expiry}`

### 批量获取文件预签名URL

**GET** `/api/post/files/url/batch?paths={path1}&paths={path2}&expiry={expiry}`

---

## 四、评论服务 (comment-service)

### 添加评论

**POST** `/api/comments`

| 参数       | 位置 | 类型   | 必填 | 说明       |
| ---------- | ---- | ------ | ---- | ---------- |
| resourceId | body | string | 是   | 资源ID（如帖子ID）|
| parentId   | body | string | 否   | 父评论ID（回复用） |
| content    | body | string | 是   | 评论内容    |

**成功响应** (201)

### 获取评论详情

**GET** `/api/comments/{commentId}`

### 获取资源评论（按时间排序，游标分页）

**GET** `/api/comments/resource/{resourceId}/time?cursorTime={cursorTime}&cursorId={cursorId}&limit={limit}`

| 参数       | 类型   | 必填 | 默认值 | 说明             |
| ---------- | ------ | ---- | ------ | ---------------- |
| resourceId | string | 是   | -      | 资源ID           |
| cursorTime | string | 否   | -      | 游标时间（首次为空）|
| cursorId   | string | 否   | -      | 游标ID（首次为空） |
| limit      | int    | 否   | 20     | 每页数量          |

### 获取资源评论（按热度排序）

**GET** `/api/comments/resource/{resourceId}/hot?cursorLikeCount={n}&cursorReplyCount={n}&cursorId={id}&limit={limit}`

### 获取用户评论

**GET** `/api/comments/user/{userId}?page={page}&limit={limit}`

### 获取评论回复

**GET** `/api/comments/replies/{commentId}?page={page}&limit={limit}`

### 点赞/取消点赞评论

**POST** `/api/comments/{commentId}/like`

| 参数   | 位置 | 类型   | 必填 | 说明   |
| ------ | ---- | ------ | ---- | ------ |
| userId | body | string | 是   | 用户ID  |

### 编辑评论

**PUT** `/api/comments/{commentId}`

| 参数    | 位置 | 类型   | 必填 | 说明     |
| ------- | ---- | ------ | ---- | -------- |
| content | body | string | 是   | 新内容   |
| userId  | body | string | 是   | 用户ID   |

### 删除评论（软删除）

**DELETE** `/api/comments/{commentId}`

| 参数    | 位置 | 类型    | 必填 | 说明            |
| ------- | ---- | ------- | ---- | --------------- |
| userId  | body | string  | 是   | 操作用户ID       |
| isAdmin | body | boolean | 否   | 是否为管理员操作  |

---

## 五、社团服务 (club-service)

### 5.1 社团管理

### 创建社团

**POST** `/api/club`

| 参数  | 位置 | 类型 | 必填 | 说明     |
| ----- | ---- | ---- | ---- | -------- |
| (club) | body | Club | 是   | 社团信息  |

### 删除社团

**DELETE** `/api/club`

| 参数    | 位置 | 类型    | 必填 | 说明    |
| ------- | ---- | ------- | ---- | ------- |
| clubId  | body | Integer | 是   | 社团ID  |

### 更新社团

**PUT** `/api/club`

| 参数  | 位置 | 类型 | 必填 | 说明       |
| ----- | ---- | ---- | ---- | ---------- |
| (club) | body | Club | 是   | 更新后的信息 |

### 获取所有社团

**GET** `/api/club/clubs`

返回社团卡片列表 `List<ClubCardVo>`。

### 根据ID获取社团

**GET** `/api/club/{clubId}`

### 5.2 成员管理

`Base: /api/club/{clubId}/members`

### 添加成员

**POST** `/api/club/{clubId}/members`

| 参数    | 位置 | 类型   | 必填 | 说明   |
| ------- | ---- | ------ | ---- | ------ |
| (member) | body | Member | 是   | 成员信息 |

### 删除成员

**DELETE** `/api/club/{clubId}/members`

| 参数     | 位置 | 类型    | 必填 | 说明   |
| -------- | ---- | ------- | ---- | ------ |
| memberId | body | Integer | 是   | 成员ID  |

### 更新成员

**PUT** `/api/club/{clubId}/members`

### 获取社团成员列表

**GET** `/api/club/{clubId}/members`

### 获取成员详情

**GET** `/api/club/{clubId}/members/{memberId}`

### 5.3 活动管理

### 创建活动

**POST** `/api/club/activities`

| 参数      | 位置 | 类型     | 必填 | 说明   |
| --------- | ---- | -------- | ---- | ------ |
| (activity) | body | Activity | 是   | 活动信息 |

### 删除活动

**DELETE** `/api/club/activities`

| 参数       | 位置 | 类型    | 必填 | 说明   |
| ---------- | ---- | ------- | ---- | ------ |
| activityId | body | Integer | 是   | 活动ID  |

### 更新活动

**PUT** `/api/club/activities`

### 按时间范围查询活动

**GET** `/api/club/activities/week?startDate={start}&endDate={end}`

| 参数      | 类型           | 必填 | 说明             |
| --------- | -------------- | ---- | ---------------- |
| startDate | LocalDateTime  | 是   | 开始日期，ISO格式  |
| endDate   | LocalDateTime  | 是   | 结束日期，ISO格式  |

### 根据ID获取活动

**GET** `/api/club/activities/{activityId}`

---

## 六、搜索服务 (search-service)

### 搜索帖子

**GET** `/api/search/post?keyword={keyword}&page={page}&size={size}`

| 参数    | 类型   | 必填 | 默认值 | 说明     |
| ------- | ------ | ---- | ------ | -------- |
| keyword | string | 是   | -      | 搜索关键词 |
| page    | int    | 否   | 1      | 页码     |
| size    | int    | 否   | 10     | 每页大小  |

**成功响应**:
```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "keyword": "关键词",
    "pageNum": 1,
    "pageSize": 10,
    "total": 25,
    "posts": [
      {
        "id": 1,
        "title": "帖子标题",
        "content": "...",
        "score": 3.5,
        ...
      }
    ]
  }
}
```

### 手动索引内容（管理用）

**POST** `/api/search/index`

| 参数     | 位置 | 类型           | 必填 | 说明     |
| -------- | ---- | -------------- | ---- | -------- |
| id       | body | long           | 是   | 目标ID   |
| title    | body | string         | 是   | 标题     |
| content  | body | string         | 是   | 内容     |
| isPublic | body | boolean        | 是   | 是否公开  |

---

## 七、推荐服务 (recommend-service)

### 获取推荐帖子

**GET** `/api/recommend/post?limit={limit}`

| 参数  | 类型 | 必填 | 默认值 | 说明       |
| ----- | ---- | ---- | ------ | ---------- |
| limit | int  | 否   | 10     | 推荐数量    |

**成功响应**: `[101, 203, 45, ...]`（推荐帖子ID列表，按相关性降序）

---

## 八、聊天服务 (chat-service)

### 获取会话列表

**GET** `/api/chat/conversations`

**成功响应**: `List<ConversationVo>`，包含会话信息及最新消息。

### 创建会话

**POST** `/api/chat/conversation?userB={userB}`

| 参数  | 类型 | 必填 | 说明           |
| ----- | ---- | ---- | -------------- |
| userB | long | 是   | 对方用户ID      |

### 获取历史消息

**GET** `/api/chat/messages?conversationId={id}&page={page}&size={size}`

| 参数           | 类型 | 必填 | 说明     |
| -------------- | ---- | ---- | -------- |
| conversationId | long | 是   | 会话ID   |
| page           | int  | 是   | 页码     |
| size           | int  | 是   | 每页数   |

### 长轮询获取新消息（实时通知）

**GET** `/api/chat/messages/poll?lastReceivedId={lastReceivedId}`

| 参数           | 类型 | 必填 | 默认值 | 说明                   |
| -------------- | ---- | ---- | ------ | ---------------------- |
| lastReceivedId | long | 否   | 0      | 客户端最后收到的消息ID   |

> 此接口为长轮询，服务端持有请求直到有新消息或超时返回。

### 发送消息

**POST** `/api/chat/message`

| 参数      | 位置 | 类型   | 必填 | 说明     |
| --------- | ---- | ------ | ---- | -------- |
| toUserId  | body | long   | 是   | 接收者ID  |
| content   | body | string | 是   | 消息内容  |

---

## 错误码说明

| 状态码 | 说明           | 场景                         |
| ------ | -------------- | ---------------------------- |
| 200    | 请求成功       | 查询/更新/删除操作成功         |
| 201    | 创建成功       | 注册、评论、帖子创建等         |
| 400    | 请求失败       | 参数校验失败                  |
| 401    | 未认证         | Token 缺失或无效              |
| 403    | 无权限         | 非本人资源操作                |
| 404    | 资源不存在     | 帖子/用户/社团不存在           |
| 500    | 服务器内部错误  | 服务异常                      |
