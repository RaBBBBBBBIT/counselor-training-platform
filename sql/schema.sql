-- ============================================================
-- 辅导员训练平台 业务数据库脚本（基于若依 RuoYi-Vue）
-- 数据库：counselor_training（业务表可与 RuoYi 基础库同库）
--
-- 重要：本脚本**不包含** RuoYi 官方基础表（sys_user/sys_dept/sys_role/
--       sys_menu/sys_dict_type/sys_dict_data/sys_user_role/sys_role_menu/
--       sys_role_dept/sys_config/sys_logininfor/sys_oper_log/sys_post/
--       sys_user_post/sys_notice/sys_job/gen_table 等）。
--       请先导入若依官方 SQL（ry_xxx.sql 与 quartz.sql），再执行本脚本。
--
-- 说明：本脚本不包含任何 DROP / TRUNCATE / 无条件全表 DELETE 语句，
--       符合 AGENTS.md 安全铁律。字段命名对齐 RuoYi 约定。
-- 与 specs/plan.md 6.1 业务表清单一一对应。
-- ============================================================

USE counselor_training;

-- ------------------------------------------------------------
-- 1. 题库
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS question_bank (
  bank_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '题库主键',
  bank_name   VARCHAR(100) NOT NULL COMMENT '题库名称',
  owner_id    BIGINT       NOT NULL COMMENT '所属人（sys_user.user_id）',
  shared      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '是否共享：0自建 1共享（只读）',
  create_by   VARCHAR(64)  DEFAULT '' COMMENT '创建者（用户名）',
  create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
  remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (bank_id),
  KEY idx_bank_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库';

-- ------------------------------------------------------------
-- 2. 题目
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS question (
  question_id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '题目主键',
  bank_id       BIGINT       NOT NULL COMMENT '所属题库（question_bank.bank_id）',
  question_type VARCHAR(20)  NOT NULL COMMENT '题型：SINGLE/MULTIPLE/JUDGE/SUBJECTIVE',
  stem          TEXT         NOT NULL COMMENT '题干',
  options       TEXT         NULL COMMENT '选项JSON：[{"key":"A","text":"..."}]；主观题为空',
  answer        TEXT         NOT NULL COMMENT '标准答案：客观题为key集合JSON；判断题为["TRUE"/"FALSE"]；主观题为参考答案文本',
  score         INT          NOT NULL COMMENT '分值（正整数）',
  difficulty    VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM' COMMENT '难度：EASY/MEDIUM/HARD',
  create_by     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time   DATETIME     DEFAULT NULL COMMENT '创建时间',
  update_by     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  update_time   DATETIME     DEFAULT NULL COMMENT '更新时间',
  remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (question_id),
  KEY idx_question_bank (bank_id),
  KEY idx_question_type (question_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目';

-- ------------------------------------------------------------
-- 3. 试卷
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS paper (
  paper_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '试卷主键',
  paper_name    VARCHAR(100) NOT NULL COMMENT '试卷名称',
  generate_mode VARCHAR(20)  NOT NULL COMMENT '生成方式：RANDOM/FIXED',
  bank_id       BIGINT       NULL COMMENT '来源题库（随机试卷使用）',
  total_score   INT          NULL COMMENT '总分',
  create_by     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time   DATETIME     DEFAULT NULL COMMENT '创建时间',
  remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (paper_id),
  KEY idx_paper_bank (bank_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷';

-- ------------------------------------------------------------
-- 4. 试卷题目快照
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS paper_question (
  paper_question_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  paper_id          BIGINT NOT NULL COMMENT '试卷（paper.paper_id）',
  question_id       BIGINT NOT NULL COMMENT '题目（question.question_id）',
  order_no          INT    NOT NULL COMMENT '顺序号（从1递增）',
  score             INT    NOT NULL COMMENT '该题分值（快照）',
  snapshot          TEXT   NOT NULL COMMENT '题目内容快照JSON（生成时固化）',
  PRIMARY KEY (paper_question_id),
  KEY idx_pq_paper (paper_id),
  KEY idx_pq_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目快照';

-- ------------------------------------------------------------
-- 5. 正式比赛
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS competition (
  competition_id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '比赛主键',
  competition_name VARCHAR(100) NOT NULL COMMENT '比赛名称',
  content          TEXT         NOT NULL COMMENT '比赛内容/说明',
  organizer_id     BIGINT       NOT NULL COMMENT '组织人（sys_user.user_id）',
  status           VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED' COMMENT 'NOT_STARTED/IN_PROGRESS/FINISHED',
  create_by        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time      DATETIME     DEFAULT NULL COMMENT '创建时间',
  remark           VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (competition_id),
  KEY idx_comp_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='正式比赛';

-- ------------------------------------------------------------
-- 6. 比赛环节
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS competition_stage (
  stage_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '环节主键',
  competition_id BIGINT       NOT NULL COMMENT '比赛（competition.competition_id）',
  stage_name     VARCHAR(100) NOT NULL COMMENT '环节名称',
  stage_type     VARCHAR(20)  NOT NULL COMMENT '环节类型：WRITTEN/INTERVIEW',
  order_no       INT          NOT NULL COMMENT '环节顺序号（从1递增）',
  paper_id       BIGINT       NOT NULL COMMENT '关联试卷（笔试环节试卷；面试环节面试题组）',
  PRIMARY KEY (stage_id),
  KEY idx_stage_competition (competition_id),
  KEY idx_stage_paper (paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛环节';

-- ------------------------------------------------------------
-- 7. 比赛参赛人员
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS competition_participant (
  participant_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  competition_id BIGINT NOT NULL COMMENT '比赛（competition.competition_id）',
  user_id        BIGINT NOT NULL COMMENT '参赛辅导员（sys_user.user_id）',
  PRIMARY KEY (participant_id),
  UNIQUE KEY uk_comp_user (competition_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛参赛人员';

-- ------------------------------------------------------------
-- 8. 练习批次
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS practice_batch (
  batch_id    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '批次主键',
  batch_name  VARCHAR(100) NOT NULL COMMENT '批次名称',
  mode        VARCHAR(20)  NOT NULL COMMENT '模式：PRACTICE/SIMULATION/XUEQING',
  paper_id    BIGINT       NOT NULL COMMENT '试卷（paper.paper_id）',
  start_time  DATETIME     NOT NULL COMMENT '开始时间',
  end_time    DATETIME     NOT NULL COMMENT '结束时间',
  create_by   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
  remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (batch_id),
  KEY idx_batch_paper (paper_id),
  KEY idx_batch_time (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='练习批次';

-- ------------------------------------------------------------
-- 9. 练习批次参与人员
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS practice_participant (
  practice_participant_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  batch_id                BIGINT NOT NULL COMMENT '练习批次（practice_batch.batch_id）',
  user_id                 BIGINT NOT NULL COMMENT '参与辅导员（sys_user.user_id）',
  PRIMARY KEY (practice_participant_id),
  UNIQUE KEY uk_batch_user (batch_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='练习批次参与人员';

-- ------------------------------------------------------------
-- 10. 学生素材（学情辨析用）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student (
  student_id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '学生主键',
  student_name VARCHAR(50)  NOT NULL COMMENT '学生姓名',
  photo_url    VARCHAR(255) NULL COMMENT '照片地址',
  dept_id      BIGINT       NULL COMMENT '院系（sys_dept.dept_id）',
  info         TEXT         NULL COMMENT '学情信息（学业/心理/困难等）',
  create_by    VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time  DATETIME     DEFAULT NULL COMMENT '创建时间',
  remark       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (student_id),
  KEY idx_student_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生素材';

-- ------------------------------------------------------------
-- 11. 答题记录（成绩唯一来源）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS answer_record (
  record_id        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id          BIGINT      NOT NULL COMMENT '作答人（sys_user.user_id）',
  paper_id         BIGINT      NOT NULL COMMENT '试卷（paper.paper_id）',
  question_id      BIGINT      NOT NULL COMMENT '题目（question.question_id）',
  competition_id   BIGINT      NULL COMMENT '正式比赛（competition.competition_id），练习时为NULL',
  stage_id         BIGINT      NULL COMMENT '比赛环节（competition_stage.stage_id），练习时为NULL',
  batch_id         BIGINT      NULL COMMENT '练习批次（practice_batch.batch_id），比赛时为NULL',
  user_answer      TEXT        NULL COMMENT '用户答案',
  objective_score  INT         NULL COMMENT '客观分（客观题判分后填充）',
  subjective_score INT         NULL COMMENT '主观分（人工评分后填充；NULL=待评分）',
  is_correct       CHAR(1)     NULL COMMENT '客观题是否答对：1对 0错 NULL非客观题',
  score_status     VARCHAR(20) NOT NULL DEFAULT 'SCORED' COMMENT '评分状态：PENDING待评分/SCORED已评分',
  answered_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作答时间',
  scored_at        DATETIME    NULL COMMENT '评分时间',
  scored_by        BIGINT      NULL COMMENT '评分人（sys_user.user_id）',
  PRIMARY KEY (record_id),
  KEY idx_ar_user (user_id),
  KEY idx_ar_competition_stage (competition_id, stage_id),
  KEY idx_ar_batch (batch_id),
  KEY idx_ar_paper (paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录（成绩唯一来源）';

-- ============================================================
-- 种子数据（Seed Data）
-- 说明：password 为 BCrypt 哈希。演示口令统一为 123456，
--       部署时用 RuoYi 用户管理或 BCrypt 工具生成真实哈希后替换占位符，
--       禁止在生产环境使用明文。
-- ============================================================

-- 院系（写入若依 sys_dept；parent_id=0 表示顶级部门）
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time) VALUES
  (200, 0, '0', '计算机学院', 1, '王主任', '13800000001', 'cs@univ.edu', '0', '0', 'admin', NOW()),
  (201, 0, '0', '经济管理学院', 2, '李主任', '13800000002', 'em@univ.edu', '0', '0', 'admin', NOW());

-- 业务角色（写入若依 sys_role；admin 超级管理员由若依官方 SQL 提供，role_id=1）
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark) VALUES
  (100, '现场组织者', 'organizer', 2, '1', 1, 1, '0', '0', 'admin', NOW(), '现场比赛组织与展示'),
  (101, '辅导员', 'counselor', 3, '5', 1, 1, '0', '0', 'admin', NOW(), '比赛练习与本人成绩'),
  (102, '院系学工负责人', 'dept_head', 4, '3', 1, 1, '0', '0', 'admin', NOW(), '本院系辅导员成绩');

-- 演示用户（写入若依 sys_user；dept_id 归属院系）
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, create_by, create_time, remark) VALUES
  (100, NULL, 'organizer', '现场组织者', '00', 'organizer@univ.edu', '13800000003', '2', '', '__BCRYPT_123456__', '0', '0', 'admin', NOW(), NULL),
  (101, 200, 'counselor1', '倪明明', '00', 'nimingming@univ.edu', '13800000004', '2', '', '__BCRYPT_123456__', '0', '0', 'admin', NOW(), NULL),
  (102, 200, 'counselor2', '王丽', '00', 'wangli@univ.edu', '13800000005', '2', '', '__BCRYPT_123456__', '0', '0', 'admin', NOW(), NULL),
  (103, 201, 'counselor3', '李强', '00', 'liqiang@univ.edu', '13800000006', '1', '', '__BCRYPT_123456__', '0', '0', 'admin', NOW(), NULL),
  (104, 200, 'depthead', '学工负责人', '00', 'depthead@univ.edu', '13800000007', '2', '', '__BCRYPT_123456__', '0', '0', 'admin', NOW(), NULL);

-- 用户角色关联（写入若依 sys_user_role）
INSERT INTO sys_user_role (user_id, role_id) VALUES
  (100, 100),
  (101, 101),
  (102, 101),
  (103, 101),
  (104, 102);

-- 业务菜单与按钮权限（写入若依 sys_menu；menu_type: M目录 C菜单 F按钮）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
  (3000, '辅导员训练', 0, 5, 'platform', NULL, 1, 0, 'M', '0', '0', NULL, 'education', 'admin', NOW()),
  (3001, '题库管理', 3000, 1, 'question', 'system/question/index', 1, 0, 'C', '0', '0', 'question:list', 'list', 'admin', NOW()),
  (3002, '正式比赛', 3000, 2, 'competition', 'system/competition/index', 1, 0, 'C', '0', '0', 'competition:list', 'chart', 'admin', NOW()),
  (3003, '日常练习', 3000, 3, 'practice', 'system/practice/index', 1, 0, 'C', '0', '0', 'practice:list', 'form', 'admin', NOW()),
  (3004, '成绩查询', 3000, 4, 'score', 'system/score/index', 1, 0, 'C', '0', '0', 'score:list', 'table', 'admin', NOW()),
  (3005, '现场比赛', 3000, 5, 'live', 'system/live/index', 1, 0, 'C', '0', '0', 'live:list', 'monitor', 'admin', NOW()),
  (3006, '比赛练习', 3000, 6, 'exam', 'system/exam/index', 1, 0, 'C', '0', '0', 'exam:list', 'edit', 'admin', NOW()),
  (3010, '题库查询', 3001, 1, '', '', 1, 0, 'F', '0', '0', 'bank:list', '#', 'admin', NOW()),
  (3011, '题库新增', 3001, 2, '', '', 1, 0, 'F', '0', '0', 'bank:add', '#', 'admin', NOW()),
  (3012, '题库修改', 3001, 3, '', '', 1, 0, 'F', '0', '0', 'bank:edit', '#', 'admin', NOW()),
  (3013, '题库删除', 3001, 4, '', '', 1, 0, 'F', '0', '0', 'bank:remove', '#', 'admin', NOW()),
  (3014, '题目新增', 3001, 5, '', '', 1, 0, 'F', '0', '0', 'question:add', '#', 'admin', NOW()),
  (3015, '题目修改', 3001, 6, '', '', 1, 0, 'F', '0', '0', 'question:edit', '#', 'admin', NOW()),
  (3016, '题目删除', 3001, 7, '', '', 1, 0, 'F', '0', '0', 'question:remove', '#', 'admin', NOW()),
  (3017, '试卷新增', 3001, 8, '', '', 1, 0, 'F', '0', '0', 'paper:add', '#', 'admin', NOW()),
  (3020, '比赛新增', 3002, 1, '', '', 1, 0, 'F', '0', '0', 'competition:add', '#', 'admin', NOW()),
  (3021, '比赛修改', 3002, 2, '', '', 1, 0, 'F', '0', '0', 'competition:edit', '#', 'admin', NOW()),
  (3022, '比赛删除', 3002, 3, '', '', 1, 0, 'F', '0', '0', 'competition:remove', '#', 'admin', NOW()),
  (3023, '比赛状态', 3002, 4, '', '', 1, 0, 'F', '0', '0', 'competition:status', '#', 'admin', NOW()),
  (3024, '练习新增', 3003, 1, '', '', 1, 0, 'F', '0', '0', 'practice:add', '#', 'admin', NOW()),
  (3025, '练习修改', 3003, 2, '', '', 1, 0, 'F', '0', '0', 'practice:edit', '#', 'admin', NOW()),
  (3026, '练习删除', 3003, 3, '', '', 1, 0, 'F', '0', '0', 'practice:remove', '#', 'admin', NOW()),
  (3027, '成绩导入', 3004, 1, '', '', 1, 0, 'F', '0', '0', 'score:import', '#', 'admin', NOW()),
  (3028, '成绩导出', 3004, 2, '', '', 1, 0, 'F', '0', '0', 'score:export', '#', 'admin', NOW()),
  (3029, '现场抽题', 3005, 1, '', '', 1, 0, 'F', '0', '0', 'live:draw', '#', 'admin', NOW()),
  (3030, '现场看答案', 3005, 2, '', '', 1, 0, 'F', '0', '0', 'live:answer', '#', 'admin', NOW()),
  (3031, '提交答案', 3006, 1, '', '', 1, 0, 'F', '0', '0', 'exam:submit', '#', 'admin', NOW());

-- 角色菜单关联（写入若依 sys_role_menu；admin 超级管理员自动拥有全部权限，无需关联）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
  (100, 3000), (100, 3005), (100, 3029), (100, 3030),
  (101, 3000), (101, 3004), (101, 3006), (101, 3031),
  (102, 3000), (102, 3004);

-- 字典类型与字典数据（写入若依 sys_dict_type / sys_dict_data）
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
  (100, '题型', 'question_type', '0', 'admin', NOW(), NULL),
  (101, '题目难度', 'question_difficulty', '0', 'admin', NOW(), NULL),
  (102, '试卷生成方式', 'paper_generate_mode', '0', 'admin', NOW(), NULL),
  (103, '比赛状态', 'competition_status', '0', 'admin', NOW(), NULL),
  (104, '环节类型', 'stage_type', '0', 'admin', NOW(), NULL),
  (105, '练习模式', 'practice_mode', '0', 'admin', NOW(), NULL),
  (106, '评分状态', 'score_status', '0', 'admin', NOW(), NULL);

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
  (100, 1, '单选题', 'SINGLE', 'question_type', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (101, 2, '多选题', 'MULTIPLE', 'question_type', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (102, 3, '判断题', 'JUDGE', 'question_type', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (103, 4, '主观题', 'SUBJECTIVE', 'question_type', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (104, 1, '简单', 'EASY', 'question_difficulty', '', 'success', 'N', '0', 'admin', NOW(), NULL),
  (105, 2, '中等', 'MEDIUM', 'question_difficulty', '', 'warning', 'N', '0', 'admin', NOW(), NULL),
  (106, 3, '困难', 'HARD', 'question_difficulty', '', 'danger', 'N', '0', 'admin', NOW(), NULL),
  (107, 1, '随机', 'RANDOM', 'paper_generate_mode', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (108, 2, '固定', 'FIXED', 'paper_generate_mode', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (109, 1, '未开始', 'NOT_STARTED', 'competition_status', '', 'info', 'N', '0', 'admin', NOW(), NULL),
  (110, 2, '进行中', 'IN_PROGRESS', 'competition_status', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (111, 3, '已结束', 'FINISHED', 'competition_status', '', 'success', 'N', '0', 'admin', NOW(), NULL),
  (112, 1, '笔试环节', 'WRITTEN', 'stage_type', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (113, 2, '面试环节', 'INTERVIEW', 'stage_type', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (114, 1, '练习模式', 'PRACTICE', 'practice_mode', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (115, 2, '模拟模式', 'SIMULATION', 'practice_mode', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (116, 3, '学情辨析', 'XUEQING', 'practice_mode', '', 'primary', 'N', '0', 'admin', NOW(), NULL),
  (117, 1, '待评分', 'PENDING', 'score_status', '', 'warning', 'N', '0', 'admin', NOW(), NULL),
  (118, 2, '已评分', 'SCORED', 'score_status', '', 'success', 'N', '0', 'admin', NOW(), NULL);

-- ============================================================
-- 业务示例数据
-- ============================================================

INSERT INTO question_bank (bank_id, bank_name, owner_id, shared, create_by, create_time) VALUES
  (1, '自建题库-基础知识', 1, '0', 'admin', NOW()),
  (2, '共享题库-学情辨析', 1, '1', 'admin', NOW());

INSERT INTO question (question_id, bank_id, question_type, stem, options, answer, score, difficulty, create_by, create_time) VALUES
  (1, 1, 'SINGLE', '辅导员的职责不包括以下哪一项？', '[{"key":"A","text":"思想引领"},{"key":"B","text":"学业指导"},{"key":"C","text":"代替学生做决定"},{"key":"D","text":"心理关怀"}]', '["C"]', 5, 'EASY', 'admin', NOW()),
  (2, 1, 'MULTIPLE', '下列属于大学生常见心理问题的是？', '[{"key":"A","text":"学业压力"},{"key":"B","text":"人际困扰"},{"key":"C","text":"饮食偏好"},{"key":"D","text":"作息规律"}]', '["A","B"]', 5, 'MEDIUM', 'admin', NOW()),
  (3, 1, 'JUDGE', '谈心谈话应当以批评教育为主。', '[{"key":"TRUE","text":"正确"},{"key":"FALSE","text":"错误"}]', '["FALSE"]', 5, 'EASY', 'admin', NOW()),
  (4, 1, 'SUBJECTIVE', '请简述辅导员开展学情辨析的基本步骤。', NULL, '参考答案：1.采集基础信息；2.识别异常信号；3.分类处置；4.跟踪反馈。', 10, 'MEDIUM', 'admin', NOW()),
  (5, 2, 'SUBJECTIVE', '根据学生照片与姓名，辨析其可能的学情并说明依据。', NULL, '参考答案：结合学业表现与日常表现综合判断。', 10, 'HARD', 'admin', NOW());

INSERT INTO paper (paper_id, paper_name, generate_mode, bank_id, total_score, create_by, create_time) VALUES
  (1, '基础知识固定试卷A', 'FIXED', 1, 25, 'admin', NOW());

INSERT INTO paper_question (paper_question_id, paper_id, question_id, order_no, score, snapshot) VALUES
  (1, 1, 1, 1, 5, '{"stem":"辅导员的职责不包括以下哪一项？","options":[{"key":"A","text":"思想引领"},{"key":"B","text":"学业指导"},{"key":"C","text":"代替学生做决定"},{"key":"D","text":"心理关怀"}],"answer":["C"],"score":5}'),
  (2, 1, 2, 2, 5, '{"stem":"下列属于大学生常见心理问题的是？","options":[{"key":"A","text":"学业压力"},{"key":"B","text":"人际困扰"},{"key":"C","text":"饮食偏好"},{"key":"D","text":"作息规律"}],"answer":["A","B"],"score":5}'),
  (3, 1, 3, 3, 5, '{"stem":"谈心谈话应当以批评教育为主。","options":[{"key":"TRUE","text":"正确"},{"key":"FALSE","text":"错误"}],"answer":["FALSE"],"score":5}'),
  (4, 1, 4, 4, 10, '{"stem":"请简述辅导员开展学情辨析的基本步骤。","answer":"参考答案文本","score":10}');

INSERT INTO competition (competition_id, competition_name, content, organizer_id, status, create_by, create_time) VALUES
  (1, '2026年辅导员基本功大赛', '以赛促学、以练促能，覆盖笔试与面试环节。', 1, 'NOT_STARTED', 'admin', NOW());

INSERT INTO competition_stage (stage_id, competition_id, stage_name, stage_type, order_no, paper_id) VALUES
  (1, 1, '笔试环节一', 'WRITTEN', 1, 1),
  (2, 1, '笔试环节二', 'WRITTEN', 2, 1),
  (3, 1, '面试环节一', 'INTERVIEW', 3, 1),
  (4, 1, '面试环节二', 'INTERVIEW', 4, 1),
  (5, 1, '面试环节三', 'INTERVIEW', 5, 1);

INSERT INTO competition_participant (participant_id, competition_id, user_id) VALUES
  (1, 1, 101),
  (2, 1, 102),
  (3, 1, 103);

INSERT INTO practice_batch (batch_id, batch_name, mode, paper_id, start_time, end_time, create_by, create_time) VALUES
  (1, '日常练习批次-基础知识', 'PRACTICE', 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'admin', NOW()),
  (2, '模拟批次-基础知识', 'SIMULATION', 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'admin', NOW()),
  (3, '学情辨析批次', 'XUEQING', 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'admin', NOW());

INSERT INTO practice_participant (practice_participant_id, batch_id, user_id) VALUES
  (1, 1, 101),
  (2, 2, 101),
  (3, 3, 101);

INSERT INTO student (student_id, student_name, photo_url, dept_id, info, create_by, create_time) VALUES
  (1, '张同学', '/static/student/1.jpg', 200, '学业预警，需关注。', 'admin', NOW()),
  (2, '刘同学', '/static/student/2.jpg', 200, '心理波动，需谈心。', 'admin', NOW());

-- 隐藏若依官方菜单(若依官网)以去除品牌痕迹
USE counselor_training;
UPDATE sys_menu SET visible='1' WHERE menu_id=4;

-- ============================================================
-- 去若依品牌：管理员昵称/演示账号/部门树/岗位/公告 中性化
-- （在若依基础表导入后执行，覆盖 RuoYi 演示数据）
-- ============================================================
USE counselor_training;
UPDATE sys_user SET nick_name='系统管理员' WHERE user_id=1;
UPDATE sys_user SET nick_name='管理员', status='1' WHERE user_id=2;
UPDATE sys_dept SET dept_name='学校总部' WHERE dept_id=100;
UPDATE sys_dept SET dept_name='教学单位' WHERE dept_id=101;
UPDATE sys_dept SET dept_name='学生工作单位' WHERE dept_id=102;
UPDATE sys_dept SET dept_name='院系一' WHERE dept_id=103;
UPDATE sys_dept SET dept_name='院系二' WHERE dept_id=104;
UPDATE sys_dept SET dept_name='院系三' WHERE dept_id=105;
UPDATE sys_dept SET dept_name='院系四' WHERE dept_id=106;
UPDATE sys_dept SET dept_name='院系五' WHERE dept_id=107;
UPDATE sys_dept SET dept_name='院系六' WHERE dept_id=108;
UPDATE sys_dept SET dept_name='院系七' WHERE dept_id=109;
UPDATE sys_post SET post_name='党委书记' WHERE post_id=1;
UPDATE sys_post SET post_name='院长' WHERE post_id=2;
UPDATE sys_post SET post_name='教研室主任' WHERE post_id=3;
UPDATE sys_post SET post_name='辅导员' WHERE post_id=4;
UPDATE sys_notice SET notice_title='欢迎使用辅导员训练平台', notice_content='以赛促学、以练促能，助力辅导员专业成长。' WHERE notice_id=1;
UPDATE sys_notice SET notice_title='系统维护通知：每周一凌晨例行维护' WHERE notice_id=2;
UPDATE sys_notice SET notice_title='辅导员训练平台介绍' WHERE notice_id=3;

-- ============================================================
-- 部门/院系：清理若依演示组织树，只保留真实院系
--   学校(100) → 计算机学院(200) / 经济管理学院(201)
-- ============================================================
UPDATE sys_dept SET dept_name='学校', parent_id=0, ancestors='0' WHERE dept_id=100;
UPDATE sys_dept SET parent_id=100, ancestors='0,100', order_num=1 WHERE dept_id=200;
UPDATE sys_dept SET parent_id=100, ancestors='0,100', order_num=2 WHERE dept_id=201;
UPDATE sys_user SET dept_id=100 WHERE user_id=1 AND user_name='admin';
UPDATE sys_user SET dept_id=100 WHERE user_id=2 AND user_name='ry';
DELETE FROM sys_dept WHERE dept_id IN (101,102,103,104,105,106,107,108,109);
DELETE FROM sys_role_dept WHERE dept_id NOT IN (SELECT dept_id FROM sys_dept);
