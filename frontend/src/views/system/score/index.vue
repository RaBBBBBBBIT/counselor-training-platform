<template>
<div class="app-container">
  <el-form :inline="true">
    <el-form-item label="比赛"><el-select v-model="cid" clearable placeholder="选择比赛" style="width:200px" @change="load"><el-option v-for="c in comps" :key="c.competitionId" :label="c.competitionName" :value="c.competitionId"/></el-select></el-form-item>
    <el-form-item label="练习批次"><el-select v-model="bid" clearable placeholder="选择批次" style="width:200px" @change="load"><el-option v-for="b in batches" :key="b.batchId" :label="b.batchName" :value="b.batchId"/></el-select></el-form-item>
    <el-form-item><el-button type="primary" @click="load">查询</el-button><el-button type="warning" @click="importDlg=true">导入主观分</el-button><el-button @click="doExport">导出</el-button></el-form-item>
  </el-form>
  <el-table :data="rows" border>
    <el-table-column prop="rank" label="排名" width="60"/><el-table-column prop="userName" label="姓名"/>
    <el-table-column prop="deptName" label="院系"/><el-table-column prop="objectiveScore" label="客观分"/>
    <el-table-column prop="subjectiveScore" label="主观分"/><el-table-column prop="totalScore" label="总成绩"/>
    <el-table-column prop="scoreStatus" label="评分状态"/>
  </el-table>
  <el-dialog v-model="importDlg" title="导入主观分（JSON）" width="520">
    <el-input type="textarea" v-model="importJson" :rows="8" placeholder='[{ "userId":101, "questionId":4, "subjectiveScore":8 }]'/>
    <template #footer><el-button @click="importDlg=false">取消</el-button><el-button type="primary" @click="doImport">导入</el-button></template>
  </el-dialog>
</div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listScore, importSubjective, exportSubjective } from '@/api/score'
import { listCompetition } from '@/api/competition'
import { listPractice } from '@/api/practice'
const rows=ref([]),comps=ref([]),batches=ref([]),cid=ref(null),bid=ref(null),importDlg=ref(false),importJson=ref('')
async function load(){const r=await listScore({competitionId:cid.value||undefined,batchId:bid.value||undefined});rows.value=r.data||[]}
async function init(){const c=await listCompetition({pageNum:1,pageSize:100});comps.value=c.rows||[];const p=await listPractice({pageNum:1,pageSize:100});batches.value=p.rows||[];load()}
async function doImport(){try{await importSubjective(JSON.parse(importJson.value));ElMessage.success('导入成功');load();importDlg.value=false}catch(e){ElMessage.error('导入失败:'+e.message)}}
async function doExport(){const r=await exportSubjective();const blob=new Blob([JSON.stringify(r.data||[],null,2)],{type:'application/json'});const a=document.createElement('a');a.href=URL.createObjectURL(blob);a.download='subjective.json';a.click()}
onMounted(init)
</script>
