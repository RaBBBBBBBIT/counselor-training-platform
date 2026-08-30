# 辅导员训练平台（Counselor Training Platform）

> 一个基于 **若依 RuoYi-Vue（前后端分离版 · Vue3）** 与 SDD 规范驱动开发方法论构建的高校辅导员训练平台，支撑辅导员「日常学习 → 模拟演练 → 正式比赛」全流程，实现以赛促学、以练促能。

## 1. 项目简介（Overview）

本平台面向高校辅导员队伍的专业能力培养与考核，覆盖题库管理、正式比赛、日常练习、成绩查询、现场比赛、比赛练习六大功能模块，并内置四种角色的权限体系（基于若依 RBAC + 数据权限）：

| 角色 | role_key | 数据权限 data_scope | 职责 |
| --- | --- | --- | --- |
| 业务管理人员（ADMIN） | admin | 1 全部 | 题库/比赛/练习/成绩全量管理 |
| 现场组织者（ORGANIZER） | organizer | 1 全部 | 现场比赛组织与展示 |
| 辅导员（COUNSELOR） | counselor | 5 仅本人 | 比赛练习、本人成绩 |
| 院系学工负责人（DEPT_HEAD） | dept_head | 3 本部门 | 本院系辅导员成绩 |

## 2. 核心特性（Features）

- **题库管理**：题目增删改查；共享题库只读；随机/固定试卷生成；删除题目前置校验（未被试卷使用且非共享）。
- **正式比赛**：默认 2 笔试环节 + 3 面试环节，可增删改顺序；状态单向流转；参赛人员管理。
- **日常练习**：练习模式（答一题看一题答案）与模拟模式（全部答完才能看答案，含学情辨析批次）。
- **成绩查询**：客观分自动判分 + 主观分人工评分；基础知识成绩、阶段成绩、总成绩、排名；主观成绩导入导出；@DataScope 数据权限控制查询范围。
- **现场比赛**：学情辨析（抽学生照片/姓名）、案例分析（分组抽题）、谈心谈话（抽题）。
- **比赛练习**：练习模式、模拟模式、正式比赛笔试（客观+主观）。
- **安全合规**：SQL 全参数化、密码 BCrypt 哈希、JWT + Redis 无状态认证、@PreAuthorize 权限 + @DataScope 数据权限、敏感信息脱敏。

## 3. 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | 若依 RuoYi-Vue 3.9.2：Spring Boot 4.1 + MyBatis + Redis + MySQL 8 + Spring Security + JWT |
| 前端 | 若依 Vue3：Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios |

## 4. 目录结构

    counselor-training-platform/
    ├── AGENTS.md              # 项目根上下文与工程铁律
    ├── README.md
    ├── docs/
    │   ├── api.md             # API 接口文档
    │   ├── 流程图.md          # 流程图（Mermaid）
    │   └── ai-conversation.md # 与 AI 对话记录
    ├── specs/
    │   ├── spec.md            # 需求规范
    │   ├── plan.md            # 技术方案（RuoYi-Vue3）
    │   ├── tasks.md           # 任务清单
    │   └── test-report.md     # 验收测试方案与报告
    ├── sql/
    │   └── schema.sql         # 业务表 + 角色/菜单/字典/演示数据种子
    ├── backend/               # 若依后端（ruoyi-admin/common/framework/system/quartz/generator）
    └── frontend/              # 若依 Vue3 前端

## 5. 安装指南（Installation）

### 5.1 前置环境

- JDK 17（若依 v3.9.2）
- Maven 3.6+
- MySQL 8.0+
- Redis 5+（缓存/token/验证码）
- Node.js 16+（前端）

### 5.2 数据库初始化

1. 导入若依官方 SQL（RuoYi 仓库 sql 目录下的 ry_xxx.sql 与 quartz.sql），创建 RuoYi 基础表。
2. 再执行本项目业务脚本：

    mysql -u root -p counselor_training < sql/schema.sql

### 5.3 后端配置

在 ruoyi-admin/src/main/resources/application.yml 中通过环境变量注入数据库、Redis、JWT 等敏感配置（禁止硬编码）：

    spring:
      redis:
        host: localhost
        password: 环境变量 REDIS_PASSWORD
      datasource:
        druid:
          url: jdbc:mysql://localhost:3306/counselor_training
          username: 环境变量 DB_USERNAME
          password: 环境变量 DB_PASSWORD
    token:
      secret: 环境变量 JWT_SECRET

### 5.4 前端依赖安装

    cd frontend && npm install

## 6. 使用方法（Usage）

### 6.1 启动后端

    cd backend && mvn spring-boot:run

后端默认监听 http://localhost:8080，接口前缀 /api/v1（认证接口为 /login、/captchaImage、/getInfo、/getRouters、/logout）。

### 6.2 启动前端

    cd frontend && npm run dev

前端默认监听 http://localhost:80（若依默认）。

### 6.3 默认账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | admin123 | 业务管理人员（若依默认超级管理员） |
| organizer | 123456 | 现场组织者 |
| counselor1 | 123456 | 辅导员 |
| depthead | 123456 | 院系学工负责人 |

> 演示账号口令 123456，部署时用 RuoYi 用户管理或 BCrypt 工具生成真实哈希后替换 sql/schema.sql 中的占位符；生产环境务必修改默认口令。

### 6.4 接口文档

完整接口见 docs/api.md；权限控制为 @PreAuthorize + @DataScope。

## 7. 构建方法（Building from Source）

### 7.1 后端打包

    cd backend && mvn clean package

产物：backend/ruoyi-admin/target/ruoyi-admin.jar。

### 7.2 前端构建

    cd frontend && npm run build:prod

产物：frontend/dist/。

## 8. 验收与交付

- 按 specs/test-report.md 执行全维度闭环验收（任务完成度 + 功能合规 + 架构验收 + 安全合规 + 工程构建）。
- 交付物：SDD 文档（specs/）、API 文档（docs/api.md）、流程图（docs/流程图.md）、项目代码、运行截图、与 AI 对话记录（docs/ai-conversation.md）。

## 9. 文档导航

| 文档 | 说明 |
| --- | --- |
| specs/spec.md | 需求规范（真理之源） |
| specs/plan.md | 技术方案（RuoYi-Vue3） |
| specs/tasks.md | 任务清单（六阶段 TDD） |
| specs/test-report.md | 验收测试方案与报告 |
| docs/api.md | 接口文档 |
| docs/流程图.md | 核心流程图 |
| docs/ai-conversation.md | 与 AI 对话记录 |
| sql/schema.sql | 业务数据库脚本 + 种子 |
| AGENTS.md | 项目上下文与工程铁律 |
