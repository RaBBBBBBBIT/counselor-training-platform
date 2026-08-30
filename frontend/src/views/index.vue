<template>
  <div class="dashboard-editor-container">
    <div class="welcome-card">
      <div class="welcome-icon">🎓</div>
      <h2>欢迎使用辅导员训练平台</h2>
      <p>支撑高校辅导员「日常学习 · 模拟演练 · 正式比赛」全流程，以赛促学、以练促能。</p>
    </div>
    <el-row :gutter="20" class="module-row">
      <el-col :span="8" v-for="m in modules" :key="m.path">
        <el-card shadow="hover" class="module-card" @click="$router.push(m.path)">
          <div class="module-icon">{{ m.icon }}</div>
          <div class="module-title">{{ m.title }}</div>
          <div class="module-desc">{{ m.desc }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" class="info-card">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="系统名称">辅导员训练平台</el-descriptions-item>
        <el-descriptions-item label="系统版本">{{ version }}</el-descriptions-item>
        <el-descriptions-item label="技术基座">Spring Boot + Vue3 前后端分离</el-descriptions-item>
        <el-descriptions-item label="服务对象">高校辅导员队伍</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const version = ref('v1.0.0')
const modules = [
  { title: '题库管理', icon: '📚', desc: '题库/题目/试卷，随机与固定生成', path: '/question' },
  { title: '正式比赛', icon: '🏆', desc: '笔试/面试环节、参赛人员、状态流转', path: '/competition' },
  { title: '日常练习', icon: '✍️', desc: '练习/模拟/学情辨析批次', path: '/practice' },
  { title: '成绩查询', icon: '📊', desc: '阶段/总成绩/排名、主观分导入导出', path: '/score' },
  { title: '现场比赛', icon: '🎤', desc: '学情辨析/案例分析/谈心谈话', path: '/live' },
  { title: '比赛练习', icon: '📝', desc: '练习/模拟/正式比赛答题', path: '/exam' },
]
</script>
<style scoped>
.dashboard-editor-container { padding: 20px; }
.welcome-card { background: linear-gradient(135deg,#1a56db,#6366f1); color:#fff; border-radius:12px; padding:28px 32px; margin-bottom:20px; }
.welcome-icon { font-size:44px; margin-bottom:8px; }
.welcome-card h2 { margin:0 0 6px; font-size:22px; }
.welcome-card p { margin:0; opacity:.92; }
.module-row { margin-bottom:20px; }
.module-card { cursor:pointer; text-align:center; }
.module-icon { font-size:34px; margin-bottom:8px; }
.module-title { font-weight:600; font-size:16px; margin-bottom:6px; }
.module-desc { color:#888; font-size:13px; }
.info-card { margin-top:6px; }
</style>
