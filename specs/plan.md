# plan.md —— 辅导员训练平台 技术方案（若依 RuoYi-Vue3）

> 本文档是**技术方案（HOW）**，基于 specs/spec.md 生成，需求保持不变，仅技术实现改用**若依 RuoYi-Vue（前后端分离，Vue3 版）**作为基础框架。目标是在 RuoYi 之上落地六大业务模块，且与 specs/spec.md、docs/api.md、sql/schema.sql 强追踪、无矛盾。
>
> 本文与 sql/schema.sql（数据库结构）、docs/api.md（接口契约）共同构成实现的唯一依据。若三者冲突，以本文为准；本文与 spec.md 冲突时，以 spec.md 为准。

---

## 1. 技术栈选型（基于 RuoYi-Vue3）

| 层 | 技术 | 说明 |
| --- | --- | --- |
| 后端框架 | RuoYi-Vue（若依前后端分离版） | Maven 多模块工程，版本 3.9.2 |
| 后端语言 | Java 17 | — |
| Spring Boot | 4.1.0（若依 v3.9.2） | 内嵌 Tomcat |
| 安全 | Spring Security + JWT（jjwt） | 无状态认证，token 存 Redis |
| ORM | MyBatis + PageHelper | Mapper XML 手写 SQL，分页插件 |
| 缓存 | Redis | 验证码、token、字典缓存 |
| 数据库 | MySQL 8.0 | Druid 连接池 |
| 构建 | Maven 3.6+ | 多模块 |
| 前端框架 | Vue 3.5 + Vite 6 | RuoYi-Vue3 前端（v3.9.2） |
| UI 组件 | Element Plus | 遵循 prototype.html 设计语言 |
| 前端状态 | Pinia | 用户态、权限态 |
| 前端路由 | Vue Router | 动态路由 + 路由守卫 |
| HTTP 客户端 | Axios | 统一拦截器（token、错误提示） |

> RuoYi 后端默认模块：ruoyi-admin（启动）、ruoyi-common（通用工具）、ruoyi-framework（框架核心）、ruoyi-system（系统管理）、ruoyi-quartz（定时任务）、ruoyi-generator（代码生成）。

---

## 2. 总体架构（若依 MVC 分层）

采用**前后端分离 + 若依 MVC 分层**，依赖方向自上而下，禁止反向依赖：

~~~
web/controller（表现层：参数校验、AjaxResult 返回、@PreAuthorize 鉴权）
        ↓ 依赖
service（业务接口） → service.impl（业务实现：编排、事务边界）
        ↓ 依赖
mapper（持久层接口 + Mapper XML，全部参数化 SQL）
        ↓ 依赖
domain（实体对象）
~~~

- 统一返回：**AjaxResult**（code=200 成功 / 500 失败，msg，data）。
- 分页返回：**TableDataInfo**（total、rows）+ PageHelper 的 startPage()。
- 核心业务规则（成绩计算、权限范围、删除前置条件）**只收敛在 service 层且仅实现一次**（对应 spec.md NFR-5）。

---

## 3. 业务代码落点（包结构）

业务代码扩展在 **ruoyi-system** 模块，包名 com.ruoyi.system：

~~~
com.ruoyi.system
├── domain           # 业务实体（QuestionBank、Question、Paper、Competition、PracticeBatch、AnswerRecord、Student 等）
├── controller       # 业务控制器
├── service          # 业务接口
│   └── impl         # 业务实现（成绩计算、权限范围、删除前置等规则在此）
└── mapper           # MyBatis Mapper 接口
~~~

Mapper XML 落点：ruoyi-system/src/main/resources/mapper/system/*.xml（与 mapper 接口对应，全部 #{} 参数化）。

---

## 4. 角色与数据权限映射（关键，消除歧义）

spec.md 的四个业务角色映射为 RuoYi 的角色（sys_role.role_key）与数据权限（sys_role.data_scope）：

| 业务角色 | role_key | data_scope | 说明 |
| --- | --- | --- | --- |
| 业务管理人员（ADMIN） | admin（若依超级管理员） | 1 全部数据权限 | 题库/比赛/练习/成绩全量管理 |
| 现场组织者（ORGANIZER） | organizer | 1 全部数据权限 | 仅现场比赛，**不授予**成绩查询权限 |
| 辅导员（COUNSELOR） | counselor | 5 仅本人数据权限 | 比赛练习 + 本人成绩 |
| 院系学工负责人（DEPT_HEAD） | dept_head | 3 本部门数据权限 | 本院系辅导员成绩 |

- **权限控制**：接口方法用 @PreAuthorize("@ss.hasPermi('xxx')") 按权限字符串（perms）鉴权。
- **数据权限**：成绩查询 Mapper 用 @DataScope(deptAlias = "d") 自动按角色 data_scope 过滤（本部门/仅本人/全部）。
- **等价性说明**：answer_record 仅由 COUNSELOR 作答产生，故 dept_head 的「本部门数据权限」天然等价于 spec.md 的「本院系辅导员成绩」，二者无矛盾。

> RuoYi data_scope 取值：1 全部、2 自定义（sys_role_dept）、3 本部门、4 本部门及以下、5 仅本人。

---

## 5. 权限字符串清单（sys_menu.perms，与 docs/api.md 一致）

| 模块 | perms |
| --- | --- |
| 题库管理 | question:list、question:add、question:edit、question:remove、bank:list、bank:add、bank:edit、bank:remove、paper:list、paper:add |
| 正式比赛 | competition:list、competition:add、competition:edit、competition:remove、competition:status |
| 日常练习 | practice:list、practice:add、practice:edit、practice:remove |
| 成绩查询 | score:list、score:import、score:export |
| 现场比赛 | live:list、live:draw、live:answer |
| 比赛练习 | exam:list、exam:submit |

---

## 6. 数据库设计

> 业务表 DDL 与种子数据见 **sql/schema.sql**。RuoYi 官方基础表（sys_user、sys_dept、sys_role、sys_menu、sys_user_role、sys_role_menu、sys_role_dept、sys_dict_type、sys_dict_data、sys_config、sys_logininfor、sys_oper_log、sys_post、sys_user_post、sys_notice、sys_job 等）由若依官方 SQL 提供，先导入官方脚本，再执行本项目的 sql/schema.sql。

### 6.1 业务表清单（字段对齐 RuoYi）

| 表名 | 用途 | 关键字段 |
| --- | --- | --- |
| question_bank | 题库 | bank_id, bank_name, owner_id, shared |
| question | 题目 | question_id, bank_id, question_type, stem, options, answer, score, difficulty |
| paper | 试卷 | paper_id, paper_name, generate_mode, bank_id, total_score |
| paper_question | 试卷题目快照 | paper_question_id, paper_id, question_id, order_no, score, snapshot |
| competition | 比赛 | competition_id, competition_name, content, organizer_id, status |
| competition_stage | 比赛环节 | stage_id, competition_id, stage_name, stage_type, order_no, paper_id |
| competition_participant | 比赛参赛人员 | participant_id, competition_id, user_id |
| practice_batch | 练习批次 | batch_id, batch_name, mode, paper_id, start_time, end_time |
| practice_participant | 练习参与人员 | practice_participant_id, batch_id, user_id |
| student | 学生素材 | student_id, student_name, photo_url, dept_id, info |
| answer_record | 答题记录（成绩唯一来源） | record_id, user_id, paper_id, question_id, competition_id, stage_id, batch_id, user_answer, objective_score, subjective_score, score_status |

### 6.2 字段命名映射（spec → RuoYi）

| spec.md 业务术语 | RuoYi 字段 | 说明 |
| --- | --- | --- |
| 用户 | sys_user.user_id | 用户主键 |
| 院系（department_id） | sys_dept.dept_id | 院系即若依部门表 |
| 所属院系 | sys_user.dept_id | 用户归属院系 |

> spec.md 中的「院系/department_id」在 RuoYi 中统一对应 sys_dept.dept_id，本文与 schema.sql 一致。

### 6.3 成绩计算落点（对应 spec.md 模块四）

- 客观分：客观题提交时判分，写 answer_record.objective_score 与 is_correct。
- 主观分：主观题提交后 score_status=PENDING；ADMIN 评分/导入后写 subjective_score 并置 SCORED。
- 基础知识成绩 = SUM(objective_score) + SUM(subjective_score)；阶段成绩按环节求和；总成绩各环节求和；排名降序+姓名拼音升序。
- 规则收敛在 service 层（ScoreService、RankingService 等单一实现）。

---

## 7. 枚举与字典映射（唯一，与 spec/schema/api 一致）

| 枚举 | 取值 | 若依字典 dict_type |
| --- | --- | --- |
| 题型 QuestionType | SINGLE, MULTIPLE, JUDGE, SUBJECTIVE | question_type |
| 难度 Difficulty | EASY, MEDIUM, HARD | question_difficulty |
| 试卷生成方式 PaperGenerateMode | RANDOM, FIXED | paper_generate_mode |
| 比赛状态 CompetitionStatus | NOT_STARTED, IN_PROGRESS, FINISHED | competition_status |
| 环节类型 StageType | WRITTEN, INTERVIEW | stage_type |
| 练习模式 PracticeMode | PRACTICE, SIMULATION, XUEQING | practice_mode |
| 评分状态 ScoreStatus | PENDING, SCORED | score_status |

> 客观题 = SINGLE/MULTIPLE/JUDGE；主观题 = SUBJECTIVE。此映射唯一，全文统一。

---

## 8. API 契约

> 完整接口见 **docs/api.md**。统一约定：返回 AjaxResult（code/msg/data）；分页 TableDataInfo；鉴权 @PreAuthorize + @DataScope。

- 认证接口（若依标准）：GET /captchaImage、POST /login、GET /getInfo、GET /getRouters、POST /logout。
- 业务接口前缀 /api/v1 沿用 RuoYi 风格，分为题库/比赛/练习/成绩/现场比赛/比赛练习六组。
- 登录后 token 存 Redis，请求头 Authorization: Bearer {token}。

---

## 9. 安全设计（对应 AGENTS.md / spec.md NFR）

1. SQL 一律 #{} 参数化，禁止字符串拼接，禁止 DROP/TRUNCATE/无条件全表 DELETE。
2. 密码 BCrypt 哈希（若依 BCryptPasswordEncoder）；JWT 密钥、Redis/DB 密码走 application.yml 环境变量，不进入版本库。
3. 对外成绩/用户接口对手机号脱敏（中间 4 位打码）。
4. 写操作统一 @PreAuthorize 鉴权 + 参数校验（若依 @Validated / Bean Validation）。
5. 数据权限统一 @DataScope，删除题目二次校验前置条件（BR-QB-1）。

---

## 10. 模块实现映射（需求追踪）

| 模块 | 后端（ruoyi-system） | 前端页面 | 需求 |
| --- | --- | --- | --- |
| 题库管理 | QuestionBank/Question/Paper 的 controller+service+mapper | 题库管理页 | FR-QB-1 ~ FR-QB-6 |
| 正式比赛 | Competition 相关 controller+service+mapper | 正式比赛页 | FR-COMP-1 ~ FR-COMP-5 |
| 日常练习 | PracticeBatch 相关 | 日常练习页 | FR-PRAC-1 ~ FR-PRAC-4 |
| 成绩查询 | ScoreService + @DataScope | 成绩查询页 | FR-SCORE-1 ~ FR-SCORE-10 |
| 现场比赛 | LiveMatch 相关 | 现场比赛页 | FR-LIVE-1 ~ FR-LIVE-4 |
| 比赛练习 | Exam 相关 | 比赛练习页 | FR-EXAM-1 ~ FR-EXAM-4 |

---

## 11. 目录结构（工程落点）

~~~
counselor-training-platform/
├── AGENTS.md
├── README.md
├── docs/
│   ├── api.md
│   ├── 流程图.md
│   └── ai-conversation.md
├── specs/
│   ├── spec.md
│   ├── plan.md
│   ├── tasks.md
│   └── test-report.md
├── sql/
│   └── schema.sql            # 业务表 + 角色/菜单/字典/演示数据种子
├── backend/                  # 若依后端（ruoyi-admin/common/framework/system/quartz/generator）
└── frontend/                 # 若依 Vue3 前端
~~~
