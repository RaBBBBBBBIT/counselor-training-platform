<template>
<div class="app-container">
  <el-form :inline="true" :model="query">
    <el-form-item label="比赛名称"><el-input v-model="query.competitionName" clearable/></el-form-item>
    <el-form-item><el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="open()">新增比赛</el-button></el-form-item>
  </el-form>
  <el-table :data="list" v-loading="loading" border>
    <el-table-column prop="competitionId" label="ID" width="60"/>
    <el-table-column prop="competitionName" label="比赛名称"/>
    <el-table-column prop="content" label="内容" show-overflow-tooltip/>
    <el-table-column prop="organizerName" label="组织人"/>
    <el-table-column label="状态" width="90"><template #default="s"><el-tag :type="s.row.status==='FINISHED'?'success':(s.row.status==='IN_PROGRESS'?'warning':'info')">{{ {NOT_STARTED:'未开始',IN_PROGRESS:'进行中',FINISHED:'已结束'}[s.row.status] }}</el-tag></template></el-table-column>
    <el-table-column label="操作" width="230"><template #default="s">
      <el-button link type="primary" @click="view(s.row)">详情</el-button>
      <el-button link type="warning" @click="toStatus(s.row,'IN_PROGRESS')">开始</el-button>
      <el-button link type="success" @click="toStatus(s.row,'FINISHED')">结束</el-button>
      <el-button link type="danger" @click="del(s.row)">删除</el-button>
    </template></el-table-column>
  </el-table>
  <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total,prev,pager,next" @current-change="load"/>

  <el-dialog v-model="dialog" title="新增比赛" width="640">
    <el-form :model="form" label-width="90">
      <el-form-item label="比赛名称"><el-input v-model="form.competitionName"/></el-form-item>
      <el-form-item label="比赛内容"><el-input type="textarea" v-model="form.content"/></el-form-item>
      <el-form-item label="参赛人员"><el-select v-model="form.participantIds" multiple filterable placeholder="选择辅导员" style="width:100%"><el-option v-for="u in counselors" :key="u.userId" :label="u.nickName" :value="u.userId"/></el-select></el-form-item>
      <el-form-item label="环节"><div v-for="(st,i) in form.stages" :key="i" style="display:flex;gap:6px;margin-bottom:4px">
        <el-select v-model="st.stageType" style="width:110px"><el-option label="笔试" value="WRITTEN"/><el-option label="面试" value="INTERVIEW"/></el-select>
        <el-input v-model="st.stageName" placeholder="环节名" style="flex:1"/><el-button link type="danger" @click="form.stages.splice(i,1)">删</el-button>
      </div><el-button link type="primary" @click="form.stages.push({stageName:'',stageType:'WRITTEN',orderNo:form.stages.length+1,paperId:null})">+ 环节</el-button></el-form-item>
    </el-form>
    <template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" @click="save">确定</el-button></template>
  </el-dialog>

  <el-dialog v-model="detailDialog" title="比赛详情" width="600">
    <p>名称：{{ detail.competitionName }}　状态：{{ detail.status }}</p>
    <el-table :data="detail.stages||[]" size="small" border><el-table-column prop="orderNo" label="顺序" width="60"/><el-table-column prop="stageName" label="环节"/><el-table-column prop="stageType" label="类型" width="90"/></el-table>
  </el-dialog>
</div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCompetition, getCompetition, addCompetition, changeStatus, delCompetition } from '@/api/competition'
import { listUser } from '@/api/system/user'
const loading=ref(false), list=ref([]), total=ref(0), query=reactive({pageNum:1,pageSize:10,competitionName:''})
const dialog=ref(false), form=reactive({competitionName:'',content:'',participantIds:[],stages:[{stageName:'笔试环节一',stageType:'WRITTEN',paperId:null},{stageName:'笔试环节二',stageType:'WRITTEN',paperId:null},{stageName:'面试环节一',stageType:'INTERVIEW',paperId:null},{stageName:'面试环节二',stageType:'INTERVIEW',paperId:null},{stageName:'面试环节三',stageType:'INTERVIEW',paperId:null}]})
const detailDialog=ref(false), detail=ref({}); const counselors=ref([])
async function load(){loading.value=true;try{const r=await listCompetition(query);list.value=r.rows;total.value=r.total}finally{loading.value=false}}
async function loadCounselors(){const r=await listUser({pageNum:1,pageSize:100,params:{}});counselors.value=r.rows||[]}
function open(){Object.assign(form,{competitionName:'',content:'',participantIds:[],stages:[{stageName:'笔试环节一',stageType:'WRITTEN',paperId:null},{stageName:'笔试环节二',stageType:'WRITTEN',paperId:null},{stageName:'面试环节一',stageType:'INTERVIEW',paperId:null},{stageName:'面试环节二',stageType:'INTERVIEW',paperId:null},{stageName:'面试环节三',stageType:'INTERVIEW',paperId:null}]});dialog.value=true}
async function save(){if(!form.competitionName){ElMessage.warning('请输入名称');return}await addCompetition(form);dialog.value=false;load();ElMessage.success('成功')}
async function view(row){detail.value=(await getCompetition(row.competitionId)).data;detailDialog.value=true}
async function toStatus(row,status){await ElMessageBox.confirm('确认将状态改为'+status+'?','提示',{type:'warning'});await changeStatus(row.competitionId,status);load();ElMessage.success('成功')}
async function del(row){await ElMessageBox.confirm('确认删除?','提示',{type:'warning'});await delCompetition(row.competitionId);load();ElMessage.success('删除成功')}
onMounted(()=>{load();loadCounselors()})
</script>
