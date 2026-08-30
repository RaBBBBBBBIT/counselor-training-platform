# tasks.md —— 辅导员训练平台 任务清单（若依 RuoYi-Vue3）

> 本文档将 specs/plan.md 的 RuoYi 技术方案拆解为**原子化、有依赖关系、可被 AI 直接执行**的任务列表。
>
> **约定**：
> - 任务粒度：每个任务只创建/修改**一个主要文件**，禁止「实现所有功能」式大任务。
> - **TDD 铁律**：每个功能点**先写测试任务，再写实现任务**（测试任务排在实现任务之前）。
> - **[P]**：无依赖关系、可并行执行的任务。
> - 任务编号 Tx.y，其中 x 为 Phase 编号。
> - 每个任务末尾标注所满足的需求编号（与 spec.md 追踪矩阵一致）。
> - 后端包名 com.ruoyi.system，Mapper XML 在 ruoyi-system/src/main/resources/mapper/system/。

---

## Phase 1: Foundation & Skeleton（集成若依骨架）

> 集成 RuoYi 前后端骨架与配置，不实现具体业务功能。

| 任务 | 内容 | 产出文件 | 依赖 | 需求 |
| --- | --- | --- | --- | --- |
| T1.1 | 集成 RuoYi 后端多模块骨架（ruoyi-admin/common/framework/system/quartz/generator） | backend/ 多模块 pom | — | — |
| T1.2 | 配置 application.yml（数据源、Redis、JWT 密钥走环境变量、端口 8080） | ruoyi-admin/src/main/resources/application.yml | T1.1 | NFR-3 |
| T1.3 | [P] 校验并执行 sql/schema.sql（业务表 + 角色/菜单/字典/演示数据） | sql/schema.sql | — | plan §6 |
| T1.4 | [P] 业务枚举常量类（与字典值一致：题型/难度/生成方式/比赛状态/环节类型/练习模式/评分状态） | com.ruoyi.system.constant.BizEnums | T1.1 | plan §7 |
| T1.5 | [P] 集成 RuoYi-Vue3 前端骨架（Vue3+Vite+Element Plus+Pinia+Vue Router+Axios） | frontend/ | — | — |
| T1.6 | [P] 前端依赖安装与启动配置 | frontend/package.json | T1.5 | — |
| T1.7 | [P] 前端 request.js 封装（token 注入、AjaxResult/TableDataInfo 解包、错误提示） | frontend/src/utils/request.js | T1.5 | NFR-1 |
| T1.8 | [P] 前端布局 Layout（侧边栏+顶栏，遵循 prototype.html 设计语言） | frontend/src/layout/ | T1.5 | — |
| T1.9 | [P] 前端动态路由 + 路由守卫 + Pinia user store（getInfo/getRouters） | frontend/src/router/、store/、permission.js | T1.7 | NFR-1 |
| T1.10 | 扩展全局异常处理（业务异常 → AjaxResult(500)） | com.ruoyi.framework.web.exception.GlobalExceptionHandler | T1.1 | §0.1 |
| T1.11 | [P] 校验 7 个业务字典可从接口读取（前端下拉） | 字典种子 | T1.3 | plan §7 |
| T1.12 | 校验 RuoYi 官方 SQL 与业务 SQL 同库执行无冲突 | sql/ | T1.3 | plan §6 |

---

## Phase 2: 业务实体与领域规则（TDD）

> 业务实体 + 核心规则类（成绩计算、权限范围、删除前置等），**必须先测试后实现**。

| 任务 | 内容 | 产出文件 | 依赖 | 需求 |
| --- | --- | --- | --- | --- |
| T2.1 | [P] 测试：客观题判分规则 ObjectiveScoringRule | ObjectiveScoringRuleTest | T1.4 | BR-SCORE-2 |
| T2.2 | [P] 实现：ObjectiveScoringRule | ObjectiveScoringRule | T2.1 | BR-SCORE-2 |
| T2.3 | [P] 测试：ScoreCalculator（基础知识成绩=客观+主观，待评分不计入） | ScoreCalculatorTest | T1.4 | BR-SCORE-1/3 |
| T2.4 | [P] 实现：ScoreCalculator | ScoreCalculator | T2.3 | BR-SCORE-1/3 |
| T2.5 | [P] 测试：RankingRule（降序+姓名拼音升序+并列连续编号） | RankingRuleTest | T1.4 | BR-SCORE-6 |
| T2.6 | [P] 实现：RankingRule | RankingRule | T2.5 | BR-SCORE-6 |
| T2.7 | [P] 测试：QuestionDeletionRule（未被引用且非共享才可删） | QuestionDeletionRuleTest | T1.4 | BR-QB-1 |
| T2.8 | [P] 实现：QuestionDeletionRule | QuestionDeletionRule | T2.7 | BR-QB-1 |
| T2.9 | [P] 测试：CompetitionStageRule（默认 2 笔试+3 面试、状态单向流转） | CompetitionStageRuleTest | T1.4 | BR-COMP-1, FR-COMP-3 |
| T2.10 | [P] 实现：CompetitionStageRule | CompetitionStageRule | T2.9 | BR-COMP-1, FR-COMP-3 |
| T2.11 | [P] 测试：PracticeModeRule（练习答一题看一题/模拟全答完/时间有效） | PracticeModeRuleTest | T1.4 | BR-EXAM-1/2, FR-PRAC-4 |
| T2.12 | [P] 实现：PracticeModeRule | PracticeModeRule | T2.11 | BR-EXAM-1/2, FR-PRAC-4 |
| T2.13 | [P] 实现：实体 QuestionBank/Question/Paper/PaperQuestion | domain/*.java | T1.4 | plan §6.1 |
| T2.14 | [P] 实现：实体 Competition/CompetitionStage/CompetitionParticipant | domain/*.java | T1.4 | plan §6.1 |
| T2.15 | [P] 实现：实体 PracticeBatch/PracticeParticipant/Student/AnswerRecord | domain/*.java | T1.4 | plan §6.1 |

---

## Phase 3: Service 业务层（TDD）

> 业务接口 + 实现，规则在 service 层单一实现。**必须先测试后实现**。

| 任务 | 内容 | 产出文件 | 依赖 | 需求 |
| --- | --- | --- | --- | --- |
| T3.1 | [P] 测试：QuestionBankService（题库 CRUD、共享只读） | QuestionBankServiceTest | T2.13 | FR-QB-1/2/6 |
| T3.2 | [P] 实现：QuestionBankService | QuestionBankServiceImpl | T3.1 | FR-QB-1/2/6 |
| T3.3 | [P] 测试：QuestionService（题目 CRUD、删除前置、共享只读） | QuestionServiceTest | T2.8,T2.13 | FR-QB-2/3/5 |
| T3.4 | [P] 实现：QuestionService | QuestionServiceImpl | T3.3 | FR-QB-2/3/5 |
| T3.5 | [P] 测试：PaperService（随机/固定生成、快照、抽题不足报错） | PaperServiceTest | T2.13 | FR-QB-4, E-3 |
| T3.6 | [P] 实现：PaperService | PaperServiceImpl | T3.5 | FR-QB-4 |
| T3.7 | [P] 测试：CompetitionService（默认环节、状态流转、环节/参赛人员） | CompetitionServiceTest | T2.10,T2.14 | FR-COMP-1~5 |
| T3.8 | [P] 实现：CompetitionService | CompetitionServiceImpl | T3.7 | FR-COMP-1~5 |
| T3.9 | [P] 测试：PracticeBatchService（批次 CRUD、参与人员） | PracticeBatchServiceTest | T2.15 | FR-PRAC-1 |
| T3.10 | [P] 实现：PracticeBatchService | PracticeBatchServiceImpl | T3.9 | FR-PRAC-1 |
| T3.11 | 测试：ScoreService（@DataScope 范围、阶段/总/排名、导入导出、越界拒绝） | ScoreServiceTest | T2.4,T2.6,T2.15 | FR-SCORE-1~10 |
| T3.12 | 实现：ScoreService | ScoreServiceImpl | T3.11 | FR-SCORE-1~10 |
| T3.13 | [P] 测试：LiveMatchService（学情辨析抽学生、案例分析分组抽题、谈心谈话抽题） | LiveMatchServiceTest | T2.15 | FR-LIVE-1~4 |
| T3.14 | [P] 实现：LiveMatchService | LiveMatchServiceImpl | T3.13 | FR-LIVE-1~4 |
| T3.15 | [P] 测试：ExamService（练习答一题看一题、模拟全答完、正式笔试提交判分） | ExamServiceTest | T2.2,T2.12,T2.15 | FR-EXAM-1~4, BR-EXAM-1/2 |
| T3.16 | [P] 实现：ExamService | ExamServiceImpl | T3.15 | FR-EXAM-1~4, BR-EXAM-1/2 |

---

## Phase 4: Controller & Web API（TDD）

> 控制器 + AjaxResult 返回 + @PreAuthorize 鉴权 + 参数校验。**必须先接口测试后实现**。

| 任务 | 内容 | 产出文件 | 依赖 | 需求 |
| --- | --- | --- | --- | --- |
| T4.1 | [P] 接口测试：QuestionBankController | QuestionBankControllerTest | T3.2 | api §2 |
| T4.2 | [P] 实现：QuestionBankController | QuestionBankController | T4.1 | api §2 |
| T4.3 | [P] 接口测试：QuestionController（含删除前置 409/500） | QuestionControllerTest | T3.4 | api §2 |
| T4.4 | [P] 实现：QuestionController | QuestionController | T4.3 | api §2 |
| T4.5 | [P] 接口测试：PaperController（随机/固定生成） | PaperControllerTest | T3.6 | api §2 |
| T4.6 | [P] 实现：PaperController | PaperController | T4.5 | api §2 |
| T4.7 | [P] 接口测试：CompetitionController（状态流转、环节、参赛人员） | CompetitionControllerTest | T3.8 | api §3 |
| T4.8 | [P] 实现：CompetitionController | CompetitionController | T4.7 | api §3 |
| T4.9 | [P] 接口测试：PracticeBatchController | PracticeBatchControllerTest | T3.10 | api §4 |
| T4.10 | [P] 实现：PracticeBatchController | PracticeBatchController | T4.9 | api §4 |
| T4.11 | 接口测试：ScoreController（@DataScope 范围、导入导出） | ScoreControllerTest | T3.12 | api §5 |
| T4.12 | 实现：ScoreController | ScoreController | T4.11 | api §5 |
| T4.13 | [P] 接口测试：LiveMatchController | LiveMatchControllerTest | T3.14 | api §6 |
| T4.14 | [P] 实现：LiveMatchController | LiveMatchController | T4.13 | api §6 |
| T4.15 | [P] 接口测试：ExamController（练习/模拟/正式笔试） | ExamControllerTest | T3.16 | api §7 |
| T4.16 | [P] 实现：ExamController | ExamController | T4.15 | api §7 |

---

## Phase 5: Mapper 与数据权限（集成）

> Mapper 接口 + XML（参数化 SQL）+ @DataScope 数据权限 + 集成测试。

| 任务 | 内容 | 产出文件 | 依赖 | 需求 |
| --- | --- | --- | --- | --- |
| T5.1 | [P] Mapper 接口：QuestionBank/Question/Paper | mapper/ | T2.13 | NFR-2 |
| T5.2 | [P] Mapper 接口：Competition/Stage/Participant | mapper/ | T2.14 | NFR-2 |
| T5.3 | [P] Mapper 接口：PracticeBatch/Participant/Student/AnswerRecord | mapper/ | T2.15 | NFR-2 |
| T5.4 | Mapper XML（全部 #{} 参数化，与接口对应） | resources/mapper/system/*.xml | T5.1,T5.2,T5.3 | NFR-2 |
| T5.5 | Score Mapper 加 @DataScope（deptAlias）实现数据权限过滤 | ScoreMapper | T3.12,T5.4 | §3.1 |
| T5.6 | 集成测试：建表、种子、基础 CRUD、数据权限、成绩聚合 | integration/*Test | T5.4,T5.5 | plan §6 |

---

## Phase 6: Frontend UI & Interaction（RuoYi Vue3）

> 页面、组件、API Service、表单、状态、鉴权态、联调。

| 任务 | 内容 | 产出文件 | 依赖 | 需求 |
| --- | --- | --- | --- | --- |
| T6.1 | 登录页 + 认证联调（captchaImage/login/getInfo/getRouters） | views/login.vue | T1.7,T1.9 | api §1 |
| T6.2 | [P] 工作台（仪表盘） | views/dashboard.vue | T1.8 | — |
| T6.3 | 题库管理页（题库/题目/试卷三个 Tab） | views/system/question/ | T4.2,T4.4,T4.6 | FR-QB-* |
| T6.4 | 正式比赛页（比赛/环节/参赛人员/状态） | views/system/competition/ | T4.8 | FR-COMP-* |
| T6.5 | 日常练习页（批次/参与人员） | views/system/practice/ | T4.10 | FR-PRAC-* |
| T6.6 | 成绩查询页（成绩/排名/导入导出，按角色范围） | views/system/score/ | T4.12 | FR-SCORE-* |
| T6.7 | 现场比赛页（学情辨析/案例分析/谈心谈话） | views/system/live/ | T4.14 | FR-LIVE-* |
| T6.8 | 比赛练习页（练习/模拟/正式比赛答题） | views/system/exam/ | T4.16 | FR-EXAM-* |
| T6.9 | API Service 层封装（与 docs/api.md 一一对应） | src/api/ | T1.7 | api 全文 |
| T6.10 | 端到端联调 + 角色权限验证（各角色菜单与数据范围正确） | frontend 全量 | T6.1~T6.9 | §3.1 |

---

## 执行顺序总览

1. Phase 1（T1.1~T1.12）：集成若依前后端骨架。
2. Phase 2（T2.1~T2.15）：业务实体与领域规则（TDD）。
3. Phase 3（T3.1~T3.16）：Service 业务层（TDD）。
4. Phase 4（T4.1~T4.16）：Controller 与接口（TDD，先接口测试）。
5. Phase 5（T5.1~T5.6）：Mapper + @DataScope 数据权限 + 集成。
6. Phase 6（T6.1~T6.10）：前端 UI 与联调。

> 每个 Phase 内按表格顺序执行；标注 [P] 的任务可并行。全部完成后按 specs/test-report.md 执行闭环验收。
