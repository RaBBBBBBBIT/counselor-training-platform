# api.md —— 辅导员训练平台 接口文档（若依 RuoYi-Vue3）

> 本文档是**接口契约的权威来源**，与 specs/plan.md 第 8 节、sql/schema.sql 完全一致。业务接口前缀统一为 **/api/v1**。
>
> 角色与数据权限：ADMIN（业务管理人员，data_scope=1 全部）、ORGANIZER（现场组织者，data_scope=1）、COUNSELOR（辅导员，data_scope=5 仅本人）、DEPT_HEAD（院系学工负责人，data_scope=3 本部门）。

## 0. 通用约定

### 0.1 统一返回体 AjaxResult（若依）

成功：

~~~json
{ "code": 200, "msg": "操作成功", "data": { } }
~~~

失败：

~~~json
{ "code": 500, "msg": "错误信息" }
~~~

### 0.2 分页返回体 TableDataInfo

~~~json
{ "total": 20, "rows": [ { } ], "code": 200, "msg": "查询成功" }
~~~

### 0.3 认证方式（若依标准）

- 登录接口返回 token，前端请求头携带：Authorization: Bearer {token}。
- token 存 Redis（key 前缀 login_tokens:），退出登录时删除。

### 0.4 权限控制

- 接口方法用 @PreAuthorize("@ss.hasPermi('xxx')") 鉴权，本文在每个接口标注所需 perms。
- 成绩查询接口额外用 @DataScope 按角色数据权限过滤数据范围。

---

## 1. 认证接口（若依标准，公开）

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| GET | /captchaImage | 获取验证码（含 uuid 与图片） | 公开 |
| POST | /login | 登录，返回 token | 公开 |
| GET | /getInfo | 获取当前用户信息、角色、权限 | 已登录 |
| GET | /getRouters | 获取动态路由（菜单） | 已登录 |
| POST | /logout | 退出登录 | 已登录 |

登录请求体：

~~~json
{ "username": "admin", "password": "admin123", "code": "1234", "uuid": "captcha-uuid" }
~~~

登录响应 data：

~~~json
{ "token": "eyJhbGciOi..." }
~~~

---

## 2. 题库管理

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| GET | /api/v1/question-banks/list | 题库列表（分页） | bank:list |
| POST | /api/v1/question-banks | 新增题库 | bank:add |
| PUT | /api/v1/question-banks | 修改题库 | bank:edit |
| DELETE | /api/v1/question-banks/{bankId} | 删除自建题库（仅当无题目） | bank:remove |
| GET | /api/v1/questions/list | 题目分页查询（bankId/type/keyword） | question:list |
| GET | /api/v1/questions/{questionId} | 题目详情 | question:list |
| POST | /api/v1/questions | 新增题目（仅自建题库） | question:add |
| PUT | /api/v1/questions | 修改题目（仅自建题库） | question:edit |
| DELETE | /api/v1/questions/{questionId} | 删除题目（BR-QB-1 前置校验） | question:remove |
| GET | /api/v1/papers/list | 试卷列表 | paper:list |
| GET | /api/v1/papers/{paperId} | 试卷详情（含题目快照） | paper:list |
| POST | /api/v1/papers/random | 生成随机试卷 | paper:add |
| POST | /api/v1/papers/fixed | 生成固定试卷 | paper:add |

- 共享题库（shared=1）及其题目对所有登录角色只读；写接口仅 ADMIN（data_scope 不适用于题库，写操作直接校验 perms）。

### 2.1 题目新增请求体

~~~json
{
  "bankId": 1,
  "questionType": "SINGLE",
  "stem": "辅导员的职责不包括以下哪一项？",
  "options": [ {"key": "A", "text": "思想引领"}, {"key": "B", "text": "学业指导"}, {"key": "C", "text": "代替学生做决定"}, {"key": "D", "text": "心理关怀"} ],
  "answer": ["C"],
  "score": 5,
  "difficulty": "EASY"
}
~~~

### 2.2 生成随机试卷请求体

~~~json
{
  "paperName": "随机试卷B",
  "bankId": 1,
  "rules": [
    { "questionType": "SINGLE", "count": 2, "score": 5 },
    { "questionType": "JUDGE", "count": 1, "score": 5 }
  ]
}
~~~

- 抽题数量超过题库可用数量时返回 AjaxResult(500)（对应 E-3）。

---

## 3. 正式比赛

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| GET | /api/v1/competitions/list | 比赛列表 | competition:list |
| POST | /api/v1/competitions | 创建比赛（默认 2 笔试 + 3 面试） | competition:add |
| GET | /api/v1/competitions/{competitionId} | 比赛详情（含环节、参赛人员） | competition:list |
| PUT | /api/v1/competitions | 修改比赛（名称、内容） | competition:edit |
| PUT | /api/v1/competitions/{competitionId}/status | 状态流转（NOT_STARTED→IN_PROGRESS→FINISHED） | competition:status |
| POST | /api/v1/competitions/stages | 新增环节 | competition:edit |
| PUT | /api/v1/competitions/stages | 修改环节 | competition:edit |
| DELETE | /api/v1/competitions/stages/{stageId} | 删除环节 | competition:edit |
| POST | /api/v1/competitions/participants | 添加参赛人员 | competition:edit |
| DELETE | /api/v1/competitions/participants/{participantId} | 移除参赛人员 | competition:edit |

创建比赛请求体：

~~~json
{
  "competitionName": "2026年辅导员基本功大赛",
  "content": "以赛促学、以练促能。",
  "participantIds": [101, 102, 103]
}
~~~

- 环节默认生成：2 个 WRITTEN（顺序 1、2）+ 3 个 INTERVIEW（顺序 3、4、5），符合 BR-COMP-1。
- 状态仅允许单向流转，非法流转返回 AjaxResult(500)。

---

## 4. 日常练习

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| GET | /api/v1/practice-batches/list | 批次列表（可按 mode 筛选） | practice:list |
| POST | /api/v1/practice-batches | 创建批次 | practice:add |
| PUT | /api/v1/practice-batches | 修改批次 | practice:edit |
| DELETE | /api/v1/practice-batches/{batchId} | 删除批次 | practice:remove |
| POST | /api/v1/practice-batches/participants | 添加参与人员 | practice:edit |
| DELETE | /api/v1/practice-batches/participants/{id} | 移除参与人员 | practice:edit |

---

## 5. 成绩查询（@DataScope 数据权限）

> 通过 @DataScope 自动按角色过滤：ADMIN 全量、DEPT_HEAD 本部门（本院系辅导员）、COUNSELOR 仅本人。等价于 spec.md §3.1。

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| GET | /api/v1/scores/list | 成绩列表（基础知识成绩、阶段成绩、总成绩、排名） | score:list |
| POST | /api/v1/scores/subjective/import | 导入主观成绩（文件上传） | score:import |
| GET | /api/v1/scores/subjective/export | 导出主观成绩（文件下载） | score:export |

### 5.1 成绩列表响应 data（rows 元素）

~~~json
{
  "userId": 101,
  "userName": "倪明明",
  "stageScores": [ { "stageId": 1, "objectiveScore": 15, "subjectiveScore": 8, "stageScore": 23 } ],
  "objectiveScore": 15,
  "subjectiveScore": 8,
  "totalScore": 23,
  "rank": 1,
  "scoreStatus": "SCORED"
}
~~~

- 存在待评分主观题的选手 scoreStatus=PENDING，不计入最终排名（对应 E-11）。

### 5.2 主观成绩导入

- 上传文件字段名：file（CSV/XLSX）。表头：userId, questionId, subjectiveScore。
- 覆盖式更新 answer_record.subjective_score 并置 score_status=SCORED。
- 越界行（主观分 < 0 或 > 该题分值）被拒绝并逐行反馈（对应 E-7）。

---

## 6. 现场比赛

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| GET | /api/v1/live-matches/xueqing/draw | 学情辨析：随机抽取 1 名学生（编号、照片、姓名，不含答案） | live:draw |
| GET | /api/v1/live-matches/xueqing/{studentId}/answer | 查看该学生学情参考答案 | live:answer |
| POST | /api/v1/live-matches/case-analysis/draw | 案例分析：按分组抽题并展示题目 | live:draw |
| POST | /api/v1/live-matches/talk/draw | 谈心谈话：抽题并展示题目 | live:draw |
| GET | /api/v1/live-matches/questions/{questionId} | 展示题目详情 | live:list |

### 6.1 学情辨析抽取响应 data

~~~json
{ "studentId": 1, "serialNo": "001", "studentName": "张同学", "photoUrl": "/static/student/1.jpg" }
~~~

### 6.2 案例分析分组抽题

请求体：

~~~json
{ "groupNo": 1, "participantSerialNo": "001" }
~~~

响应 data：

~~~json
{ "groupNo": 1, "serialNo": "001", "question": { "questionId": 5, "stem": "请结合案例提出处理建议。" } }
~~~

---

## 7. 比赛练习

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| GET | /api/v1/exams/practices | 我可参加的练习/模拟/学情辨析批次列表 | exam:list |
| GET | /api/v1/exams/competitions | 我可参加的正式比赛列表 | exam:list |
| GET | /api/v1/exams/papers/{paperId} | 获取试卷题目（不含答案） | exam:list |
| POST | /api/v1/exams/answers | 提交单题答案（练习模式，答一题看一题） | exam:submit |
| POST | /api/v1/exams/submissions | 一次性提交全部答案（模拟模式） | exam:submit |
| GET | /api/v1/exams/answers/{recordId} | 查看单题答案与解析 | exam:list |
| GET | /api/v1/exams/competitions/{competitionId}/stages/{stageId}/paper | 获取正式比赛笔试试卷 | exam:list |
| POST | /api/v1/exams/competitions/{competitionId}/stages/{stageId}/submit | 提交正式比赛笔试答案 | exam:submit |

### 7.1 提交单题答案（练习模式）

请求体：

~~~json
{ "paperId": 1, "questionId": 1, "answer": "C", "batchId": 1 }
~~~

- 响应 data 返回该题判分结果与答案（答一题看一题，BR-EXAM-1）。

### 7.2 一次性提交（模拟模式）

请求体：

~~~json
{
  "batchId": 2,
  "answers": [
    { "questionId": 1, "answer": "C" },
    { "questionId": 2, "answer": ["A","B"] }
  ]
}
~~~

- 未全部作答时不允许提交（返回 AjaxResult(500)，对应 E-9）；全部提交后才可查看答案（BR-EXAM-2）。

### 7.3 提交正式比赛笔试答案

- 客观题提交后立即自动判分（写 objective_score），主观题进入 PENDING（FR-EXAM-3、FR-EXAM-4）。
