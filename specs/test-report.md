# test-report.md —— 辅导员训练平台 验收测试方案与报告

> 本文档是**验收测试的权威标准**。上半部分为可执行的测试用例与检查清单；下半部分为验收报告模板。代码实现完成后，按本文执行测试并将结果填入报告模板，最终结论以此为准。
>
> 说明：本文在代码实现前生成，属「验收标准 + 用例 + 报告模板」。执行阶段逐条勾选 Pass / Fail 并记录证据（截图、日志、接口返回）。

---

## 1. 测试概述

- **测试范围**：辅导员训练平台六大模块 + 认证鉴权 + 安全合规。
- **测试依据**：specs/spec.md（需求与验收标准 AC）、specs/plan.md（架构）、docs/api.md（接口契约）、sql/schema.sql（数据结构）。
- **测试方法**：单元测试（Domain/Application）、接口测试（Controller）、集成测试（DB）、安全专项审计、工程构建检测。
- **测试环境**：Java 17 + Spring Boot 4.1（若依 v3.9.2）+ Redis + MySQL 8；前端 Vue 3.5 + Vite 6。
- **通过标准**：所有 AC 验收项全部 Pass，安全专项零高危，工程可编译可运行。

---

## 2. 任务完成度核对表（对照 specs/tasks.md）

| Phase | 任务数 | 完成数 | 完成率 | 遗漏项 |
| --- | --- | --- | --- | --- |
| Phase 1 Foundation & Skeleton | 12 | _ | _% | _ |
| Phase 2 业务实体与规则 & Tests | 15 | _ | _% | _ |
| Phase 3 Service 业务层 & Tests | 16 | _ | _% | _ |
| Phase 4 Controller & API | 16 | _ | _% | _ |
| Phase 5 Mapper 与数据权限 | 6 | _ | _% | _ |
| Phase 6 Frontend UI & Interaction | 10 | _ | _% | _ |
| **合计** | **75** | _ | _% | _ |

> 逐条核对 tasks.md 中每个 Tx.y 任务，统计完成率、遗漏项、未实现功能。

---

## 3. 功能测试用例（可执行）

### 3.1 认证与权限

| 用例 | 前置 | 步骤 | 预期结果 | 对应 |
| --- | --- | --- | --- | --- |
| TC-AUTH-01 | 用户存在 | 正确账号密码登录 | code=0，返回 JWT 与用户信息 | api §1 |
| TC-AUTH-02 | — | 错误密码登录 | code=401 或 400，无 token | api §1 |
| TC-AUTH-03 | 无 token | 访问受保护接口 | 返回 401 | NFR-1 |
| TC-AUTH-04 | 以 COUNSELOR 登录 | 访问题库创建接口 | 返回 403 | §3.1 |

### 3.2 题库管理

| 用例 | 前置 | 步骤 | 预期结果 | 对应 |
| --- | --- | --- | --- | --- |
| TC-QB-01 | ADMIN | 创建自建题库并新增题目 | 成功，题目入自建题库 | FR-QB-1/2 |
| TC-QB-02 | 任意角色 | 查看共享题库题目 | 只读，无编辑入口 | BR-QB-2, FR-QB-6 |
| TC-QB-03 | 题目已被试卷引用 | 删除该题目 | 409，提示「题目已被试卷使用」 | BR-QB-1, E-1 |
| TC-QB-04 | 题目在共享题库 | 删除该题目 | 409，提示「共享题库题目只读」 | BR-QB-1, E-2 |
| TC-QB-05 | ADMIN | 生成随机试卷（题型/数量/分值） | 成功，题目快照固化 | FR-QB-4, AC-QB-3 |
| TC-QB-06 | 抽题数 > 可用数 | 生成随机试卷 | 409，提示可用数量不足 | E-3 |
| TC-QB-07 | ADMIN | 生成固定试卷（指定题目） | 成功，顺序固定 | FR-QB-4 |

### 3.3 正式比赛

| 用例 | 前置 | 步骤 | 预期结果 | 对应 |
| --- | --- | --- | --- | --- |
| TC-COMP-01 | ADMIN | 新建比赛（不配环节） | 默认生成 2 笔试 + 3 面试环节 | BR-COMP-1, AC-COMP-1 |
| TC-COMP-02 | 比赛未开始 | 开始比赛 | 状态 = IN_PROGRESS | FR-COMP-3 |
| TC-COMP-03 | 比赛进行中 | 结束比赛 | 状态 = FINISHED | FR-COMP-3 |
| TC-COMP-04 | 比赛已结束 | 再次开始 | 409，非法流转 | FR-COMP-3, api §4.2 |
| TC-COMP-05 | ADMIN | 增删环节、调整顺序 | 环节顺序号连续更新 | FR-COMP-2 |
| TC-COMP-06 | ADMIN | 添加/移除参赛人员 | 成功，已产生成绩保留 | FR-COMP-4 |

### 3.4 日常练习 / 比赛练习

| 用例 | 前置 | 步骤 | 预期结果 | 对应 |
| --- | --- | --- | --- | --- |
| TC-EXAM-01 | 练习批次有效 | 辅导员答一题提交 | 立即返回该题答案 | BR-EXAM-1, AC-EXAM-1 |
| TC-EXAM-02 | 模拟批次未答完 | 尝试提交/看答案 | 409，不允许 | BR-EXAM-2, E-9, AC-EXAM-2 |
| TC-EXAM-03 | 模拟批次全答完 | 一次性提交 | 成功，可查看全部答案 | BR-EXAM-2, AC-EXAM-2 |
| TC-EXAM-04 | 批次时间外 | 提交答案 | 409，提示不在有效时间 | FR-PRAC-4, E-8 |
| TC-EXAM-05 | 正式比赛笔试 | 提交客观+主观题 | 客观题立即判分，主观题 PENDING | FR-EXAM-3/4, AC-EXAM-3 |

### 3.5 成绩查询

| 用例 | 前置 | 步骤 | 预期结果 | 对应 |
| --- | --- | --- | --- | --- |
| TC-SCORE-01 | 已答客观+主观 | 查询基础知识成绩 | = 客观分 + 主观分 | BR-SCORE-1, AC-SCORE-1 |
| TC-SCORE-02 | 比赛已评分 | 查询阶段/总成绩/排名 | 阶段成绩按环节求和，总成绩各环节求和，排名降序 | BR-SCORE-4/5/6, AC-SCORE-2 |
| TC-SCORE-03 | 两人总成绩相同 | 查询排名 | 按姓名拼音升序，并列名次连续（1,2,2,4） | BR-SCORE-6 |
| TC-SCORE-04 | COUNSELOR | 查询成绩 | 仅返回本人 | §3.1, AC-SCORE-3 |
| TC-SCORE-05 | DEPT_HEAD | 查询成绩 | 仅返回本院系辅导员 | §3.1, AC-SCORE-3 |
| TC-SCORE-06 | ADMIN | 查询成绩 | 返回全量 | §3.1 |
| TC-SCORE-07 | ADMIN | 导入主观成绩 | 覆盖更新对应主观分 | FR-SCORE-8, AC-SCORE-4 |
| TC-SCORE-08 | 主观分越界 | 导入/评分 | 拒绝该行并逐行反馈 | BR-SCORE-3, E-7 |
| TC-SCORE-09 | 存在待评分主观题 | 查询排名 | 该选手标 PENDING，不计入最终排名 | E-11 |
| TC-SCORE-10 | 多选题部分选对 | 判分 | 0 分 | BR-SCORE-2, E-12 |

### 3.6 现场比赛

| 用例 | 前置 | 步骤 | 预期结果 | 对应 |
| --- | --- | --- | --- | --- |
| TC-LIVE-01 | ORGANIZER | 学情辨析抽取学生 | 返回编号+照片+姓名，不含答案 | FR-LIVE-1 |
| TC-LIVE-02 | ORGANIZER | 查看学情参考答案 | 返回参考答案 | FR-LIVE-1 |
| TC-LIVE-03 | ORGANIZER | 案例分析分组抽题展示 | 返回分组+题目 | FR-LIVE-2 |
| TC-LIVE-04 | ORGANIZER | 谈心谈话抽题展示 | 返回题目 | FR-LIVE-3 |

---

## 4. 架构验收清单

| 检查项 | 标准 | 结果 |
| --- | --- | --- |
| 分层 | controller → service → mapper → domain（若依 MVC），无反向依赖 | Pass/Fail |
| 依赖方向 | domain 不依赖 MyBatis/Spring 框架细节 | Pass/Fail |
| 规则收敛 | 成绩计算、权限范围、删除前置条件仅在 service 层实现一次（NFR-5） | Pass/Fail |
| 包结构 | 与 plan.md 第 3 节一致（ruoyi-system 下 domain/controller/service/impl/mapper） | Pass/Fail |
| 技术栈 | 与 plan.md 第 1 节一致（RuoYi-Vue3：SpringBoot4.1/MyBatis/Redis/MySQL8/Vue3.5） | Pass/Fail |

---

## 5. 代码质量检查清单

| 检查项 | 标准 | 结果 |
| --- | --- | --- |
| 可编译 | mvn clean package 成功，前端 npm run build 成功 | Pass/Fail |
| 单一职责 | 类/方法职责清晰，方法长度可控 | Pass/Fail |
| 命名 | 类/方法/变量命名清晰明确 | Pass/Fail |
| 测试覆盖 | Domain/Application 核心规则有单元测试 | Pass/Fail |
| 无重复 | 核心规则无重复实现 | Pass/Fail |

---

## 6. 安全专项审计报告（强制重点）

| 审计项 | 检索范围 | 结果 |
| --- | --- | --- |
| 禁用 DROP TABLE / DROP DATABASE / TRUNCATE / 无条件全表 DELETE | 全仓库 SQL 与代码 | 出现/未出现 |
| 禁用 rm -rf / rm -r * / sudo / 系统高危命令 | 全仓库脚本 | 出现/未出现 |
| 硬编码密码/密钥/明文账号 | 全仓库配置与代码 | 出现/未出现 |
| SQL 参数化（#{} / PreparedStatement） | Mapper/Service SQL | 全部参数化/存在拼接 |
| 接口权限校验 | Controller 层 | 全部鉴权/存在越权 |
| 数据脱敏 | 成绩/用户接口 | 已脱敏/未脱敏 |
| 密码哈希存储 | sys_user.password | BCrypt/明文 |

> 结论：安全专项须「零高危」方可通过。任何一项命中禁用清单即为高危，需修复后复测。

---

## 7. 工程构建检测清单

| 检查项 | 命令/标准 | 结果 |
| --- | --- | --- |
| 后端编译 | cd backend && mvn -q clean package | Pass/Fail |
| 前端构建 | cd frontend && npm run build | Pass/Fail |
| 数据库初始化 | 先导入若依官方 SQL，再执行 sql/schema.sql 无报错 | Pass/Fail |
| 配置完整 | application.yml 环境变量注入，无硬编码敏感项 | Pass/Fail |
| 目录符合 SDD | 根目录含 AGENTS.md、README.md、specs/、docs/、sql/ | Pass/Fail |

---

## 8. 问题汇总

| 编号 | 问题描述 | 严重级别 | 关联用例/需求 | 状态 |
| --- | --- | --- | --- | --- |
| _ | _ | 高/中/低 | _ | 待修复/已修复 |

---

## 9. 风险分级

| 风险 | 级别 | 说明 | 应对 |
| --- | --- | --- | --- |
| 越权访问成绩 | 高 | 权限范围实现错误导致数据泄露 | ScoreScopeResolver 单一实现 + 接口测试覆盖 |
| SQL 注入 | 高 | 字符串拼接 SQL | 强制参数化 + 安全审计 |
| 删除被引用题目 | 中 | 破坏试卷完整性 | QuestionDeletionRule 前置校验 |
| 排名计算错误 | 中 | 影响公平 | RankingRule 单测覆盖并列场景 |

---

## 10. 最终交付结论

**测试报告 = 任务完成度 + 功能合规 + 安全合规 + 代码可运行性，是项目交付的最终凭证。**

- 任务完成度：_%
- 功能合规：全部 AC Pass / Fail
- 架构验收：Pass / Fail
- 安全合规：零高危 / 存在高危
- 工程构建：Pass / Fail

**交付结论**：□ 可交付　□ 有条件交付（修复后复测）　□ 不可交付
