<template>
<div class="app-container">
  <el-form :inline="true" :model="query">
    <el-form-item label="批次名称"><el-input v-model="query.batchName" clearable/></el-form-item>
    <el-form-item label="模式"><el-select v-model="query.mode" clearable placeholder="全部"><el-option label="练习" value="PRACTICE"/><el-option label="模拟" value="SIMULATION"/><el-option label="学情辨析" value="XUEQING"/></el-select></el-form-item>
    <el-form-item><el-button type="primary" @click="load">查询</el-button><el-button type="success" @click="openAdd">新增批次</el-button></el-form-item>
  </el-form>
  <el-table :data="list" v-loading="loading" border>
    <el-table-column prop="batchId" label="ID" width="60"/><el-table-column prop="batchName" label="批次名称"/>
    <el-table-column label="模式" width="90"><template #default="s">{{ {PRACTICE:'练习',SIMULATION:'模拟',XUEQING:'学情辨析'}[s.row.mode] }}</template></el-table-column>
    <el-table-column prop="paperName" label="试卷"/>
    <el-table-column prop="startTime" label="开始"/><el-table-column prop="endTime" label="结束"/>
    <el-table-column label="操作" width="90"><template #default="s"><el-button link type="danger" @click="del(s.row)">删除</el-button></template></el-table-column>
  </el-table>
  <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total,prev,pager,next" @current-change="load"/>

  <el-dialog v-model="dialog" title="新增练习批次" width="500">
    <el-form :model="form" label-width="90">
      <el-form-item label="批次名称"><el-input v-model="form.batchName"/></el-form-item>
      <el-form-item label="模式"><el-select v-model="form.mode"><el-option label="练习" value="PRACTICE"/><el-option label="模拟" value="SIMULATION"/><el-option label="学情辨析" value="XUEQING"/></el-select></el-form-item>
      <el-form-item label="试卷"><el-select v-model="form.paperId"><el-option v-for="p in papers" :key="p.paperId" :label="p.paperName" :value="p.paperId"/></el-select></el-form-item>
      <el-form-item label="参与人员"><el-select v-model="form.participantIds" multiple filterable style="width:100%"><el-option v-for="u in counselors" :key="u.userId" :label="u.nickName" :value="u.userId"/></el-select></el-form-item>
      <el-form-item label="时间范围"><el-date-picker v-model="range" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%"/></el-form-item>
    </el-form>
    <template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" @click="save">确定</el-button></template>
  </el-dialog>
</div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listPractice, addPractice, delPractice } from '@/api/practice'
import { listPaper } from '@/api/question'
import { listUser } from '@/api/system/user'
const loading=ref(false),list=ref([]),total=ref(0),query=reactive({pageNum:1,pageSize:10,batchName:'',mode:''})
const dialog=ref(false), form=reactive({batchName:'',mode:'PRACTICE',paperId:null,participantIds:[],startTime:'',endTime:''})
const range=ref([]), papers=ref([]), counselors=ref([])
async function load(){loading.value=true;try{const r=await listPractice(query);list.value=r.rows;total.value=r.total}finally{loading.value=false}}
async function loadOpts(){const p=await listPaper({pageNum:1,pageSize:100});papers.value=p.rows||[];const u=await listUser({pageNum:1,pageSize:100});counselors.value=u.rows||[]}
function openAdd(){Object.assign(form,{batchName:'',mode:'PRACTICE',paperId:null,participantIds:[],startTime:'',endTime:''});range.value=[];dialog.value=true}
async function save(){if(!form.batchName){ElMessage.warning('请输入名称');return}if(range.value&&range.value.length===2){form.startTime=range.value[0];form.endTime=range.value[1]}await addPractice(form);dialog.value=false;load();ElMessage.success('成功')}
async function del(row){await ElMessageBox.confirm('确认删除?','提示',{type:'warning'});await delPractice(row.batchId);load();ElMessage.success('删除成功')}
onMounted(()=>{load();loadOpts()})
</script>
