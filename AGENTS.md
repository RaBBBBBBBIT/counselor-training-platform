# AGENTS.md —— 辅导员训练平台 项目根上下文与工程规则（若依 RuoYi-Vue3）

> 本文件是项目根目录的「长期记忆」（Long-term Memory）。任何 AI 编码代理在会话开始时必须自动加载本文件，并严格遵守其中的约束。本文件与 specs/spec.md、specs/plan.md、specs/tasks.md 共同构成项目的唯一事实来源（Single Source of Truth）。

## 1. 项目概述

- **项目名称**：辅导员训练平台（Counselor Training Platform）
- **技术基座**：若依 RuoYi-Vue（前后端分离版，Vue3）
- **业务目标**：搭建辅导员训练平台，支撑辅导员日常学习、模拟演练和正式比赛全流程，实现「以赛促学、以练促能」。

## 2. 全局角色（唯一枚举，全文统一，不得新增/改名）

| 角色代码 | 角色名称 | RuoYi role_key | 数据权限 data_scope |
| --- | --- | --- | --- |
| ADMIN | 业务管理人员 | admin（超级管理员） | 1 全部 |
| ORGANIZER | 现场组织者 | organizer | 1 全部 |
| COUNSELOR | 辅导员 | counselor | 5 仅本人 |
| DEPT_HEAD | 院系学工负责人 | dept_head | 3 本部门 |

> 上述四种角色为**闭集**。权限控制用 @PreAuthorize 注解（perms）；数据权限用 @DataScope 注解。

## 3. 全局功能模块（唯一枚举）

1. 题库管理（question-bank）
2. 正式比赛（competition）
3. 日常练习（practice）
4. 成绩查询（score）
5. 现场比赛（live-match）
6. 比赛练习（exam）

## 4. 必须遵守的工程铁律

### 4.1 数据库与 SQL 安全（强制）

- **禁止**生成 DROP TABLE、DROP DATABASE、TRUNCATE、无条件全表 DELETE（不带 WHERE 的 DELETE）语句。
- **所有** SQL 必须使用**参数化查询**（MyBatis 的 #{} 占位符 / PreparedStatement），**禁止**字符串拼接 SQL（含 MyBatis 取参拼接），杜绝 SQL 注入。
- 数据库/接口变更必须**向后兼容**：不得删除已有字段、不得破坏已有接口签名；如确需变更，必须提供对应的 migration 脚本。
- 任何写操作（增/删/改）都必须进行 @PreAuthorize 权限校验 + 入参校验。

### 4.2 系统与命令安全（强制）

- **禁止**生成或执行 rm -rf、rm -r *、sudo、以及任何系统高危命令。
- 生产/测试/开发环境必须通过配置区分，危险操作必须加二次确认机制。

### 4.3 密钥与敏感信息（强制）

- **禁止**硬编码数据库密码、Redis 密码、JWT 密钥等敏感信息；一律通过环境变量或配置文件（不进入版本库）注入。
- 对外接口必须做数据脱敏；访问目录/文件必须做权限限制。

### 4.4 架构分层（强制，若依 MVC）

代码严格遵循若依 MVC 分层，依赖方向只能自上而下，禁止反向依赖：

    controller（表现层：参数校验、AjaxResult 返回、@PreAuthorize 鉴权）
            ↓ 依赖
    service → service.impl（业务层：编排、事务边界、核心规则）
            ↓ 依赖
    mapper（持久层：接口 + Mapper XML，全部参数化 SQL）
            ↓ 依赖
    domain（实体对象）

- 核心业务规则（成绩计算、权限范围、删除前置条件）**只收敛在 service 层且仅实现一次**，不得散落或重复（对应 spec.md NFR-5）。
- 数据权限统一用 @DataScope；权限校验统一用 @PreAuthorize。
- 每个类/方法保持单一职责；方法长度可控；命名清晰明确。

### 4.5 开发流程（TDD 铁律）

- 依据 specs/tasks.md 的六个 Phase 顺序执行。
- 每个 Phase **必须先写测试，再写实现**（TDD）；接口测试先于接口实现。
- 每个任务只涉及**一个主要文件的创建或修改**；禁止「实现所有功能」式的大任务。

## 5. 唯一事实来源

- 需求 → specs/spec.md
- 技术方案 → specs/plan.md
- 任务清单 → specs/tasks.md
- 验收标准 → specs/test-report.md
- 数据库结构 → sql/schema.sql（业务表 + 种子；RuoYi 基础表由若依官方 SQL 提供）
- API 契约 → docs/api.md

> 当代码与文档冲突时，以文档为准；当文档之间冲突时，以 spec.md 为准（spec 是唯一真理）。若发现文档自身矛盾，**先修正文档，再改代码**。
