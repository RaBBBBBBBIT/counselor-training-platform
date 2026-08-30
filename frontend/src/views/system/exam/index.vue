<template>
<div class="app-container">
  <el-tabs v-model="mode">
    <el-tab-pane label="练习模式" name="PRACTICE">
      <p><el-select v-model="practiceId" placeholder="选择练习批次" @change="loadPractice" style="width:300px"><el-option v-for="p in practices" :key="p.batchId" :label="p.batchName" :value="p.batchId"/></el-select></p>
      <div v-if="qIndex < questions.length">
        <p>{{ qIndex+1 }}. {{ questions[qIndex].stem }}</p>
        <el-input v-model="curAnswer" placeholder="请输入答案（客观题填选项key，主观题填内容）"/>
        <el-button type="primary" @click="submitOne">提交本题</el-button>
        <p v-if="lastResult">得分：{{ lastResult.objectiveScore }}　{{ lastResult.isCorrect==='1'?'正确':'错误' }}　正确答案：{{ lastResult.correctAnswer }}</p>
        <el-button @click="qIndex++">下一题</el-button>
      </div>
    </el-tab-pane>
    <el-tab-pane label="模拟模式" name="SIMULATION">
      <p><el-select v-model="simId" placeholder="选择模拟/学情辨析批次" @change="loadSim" style="width:300px"><el-option v-for="p in practices.filter(x=>x.mode!=='PRACTICE')" :key="p.batchId" :label="p.batchName" :value="p.batchId"/></el-select></p>
      <div v-for="(q,i) in simQuestions" :key="q.questionId" style="margin-bottom:10px"><p>{{ i+1 }}. {{ q.stem }}</p><el-input v-model="simAnswers[q.questionId]" placeholder="答案"/></div>
      <el-button type="primary" @click="submitSim">全部提交</el-button>
    </el-tab-pane>
    <el-tab-pane label="正式比赛" name="FORMAL">
      <p><el-select v-model="compId" placeholder="选择比赛" @change="loadStages" style="width:240px"><el-option v-for="c in comps" :key="c.competitionId" :label="c.competitionName" :value="c.competitionId"/></el-select>
      <el-select v-model="stageId" placeholder="选择环节" @change="loadStagePaper" style="width:240px"><el-option v-for="s in stages" :key="s.stageId" :label="s.stageName" :value="s.stageId"/></el-select></p>
      <div v-for="(q,i) in stageQuestions" :key="q.questionId" style="margin-bottom:10px"><p>{{ i+1 }}. {{ q.stem }}</p><el-input v-model="stageAnswers[q.questionId]" placeholder="答案"/></div>
      <el-button type="primary" @click="submitFormal">提交</el-button>
    </el-tab-pane>
  </el-tabs>
</div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { myPractices, myCompetitions, getPaper, answerOne, submitAll, getStagePaper, submitCompetition } from '@/api/exam'
const mode=ref('PRACTICE');const practices=ref([]),comps=ref([]);const practiceId=ref(null),questions=ref([]),qIndex=ref(0),curAnswer=ref(''),lastResult=ref(null)
const simId=ref(null),simQuestions=ref([]),simAnswers=ref({});const compId=ref(null),stages=ref([]),stageId=ref(null),stageQuestions=ref([]),stageAnswers=ref({})
async function init(){practices.value=await myPractices();comps.value=await myCompetitions()}
async function loadPractice(){questions.value=await getPaper(practices.value.find(p=>p.batchId===practiceId.value).paperId);qIndex.value=0;curAnswer.value='';lastResult.value=null}
async function submitOne(){const q=questions.value[qIndex.value];lastResult.value=await answerOne({paperId:q.paperId||null,questionId:q.questionId,userAnswer:curAnswer.value,batchId:practiceId.value})}
async function loadSim(){simQuestions.value=await getPaper(practices.value.find(p=>p.batchId===simId.value).paperId);simAnswers.value={}}
async function submitSim(){const answers=Object.keys(simAnswers.value).map(qid=>({questionId:+qid,userAnswer:simAnswers.value[qid]}));await submitAll({batchId:simId.value,answers});ElMessage.success('已提交')}
async function loadStages(){stages.value=(await myCompetitions()).find(c=>c.competitionId===compId.value).stages||[]}
async function loadStagePaper(){stageQuestions.value=await getStagePaper(compId.value,stageId.value);stageAnswers.value={}}
async function submitFormal(){const answers=Object.keys(stageAnswers.value).map(qid=>({questionId:+qid,userAnswer:stageAnswers.value[qid]}));await submitCompetition(compId.value,stageId.value,answers);ElMessage.success('已提交')}
onMounted(init)
</script>
