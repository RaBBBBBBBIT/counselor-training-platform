<template>
<div class="app-container">
  <el-tabs>
    <el-tab-pane label="学情辨析">
      <el-button type="primary" @click="drawXueqing">随机抽取学生</el-button>
      <el-card v-if="xueqing" style="margin-top:12px;width:300px">
        <p>学生：{{ xueqing.studentName }}</p>
        <p><el-image v-if="xueqing.photoUrl" :src="xueqing.photoUrl" preview-src-list="[xueqing.photoUrl]" style="width:120px;height:120px"/></p>
        <el-button @click="showXueqingAnswer">查看答案</el-button>
        <p v-if="ansInfo">{{ ansInfo.info }}</p>
      </el-card>
    </el-tab-pane>
    <el-tab-pane label="案例分析">
      <el-input v-model="groupNo" placeholder="分组" style="width:120px"/><el-input v-model="serialNo" placeholder="编号" style="width:120px"/>
      <el-button type="primary" @click="drawCase">分组抽题</el-button>
      <el-card v-if="caseQ" style="margin-top:12px"><p>分组{{ caseQ.groupNo }} / {{ caseQ.serialNo }}：{{ caseQ.stem }}</p></el-card>
    </el-tab-pane>
    <el-tab-pane label="谈心谈话">
      <el-button type="primary" @click="drawTalk">抽题</el-button>
      <el-card v-if="talkQ" style="margin-top:12px"><p>{{ talkQ.stem }}</p></el-card>
    </el-tab-pane>
  </el-tabs>
</div>
</template>
<script setup>
import { ref } from 'vue'
import { xueqingDraw, xueqingAnswer, caseDraw, talkDraw } from '@/api/live'
const xueqing=ref(null),ansInfo=ref(null),groupNo=ref(''),serialNo=ref(''),caseQ=ref(null),talkQ=ref(null)
async function drawXueqing(){xueqing.value=await xueqingDraw();ansInfo.value=null}
async function showXueqingAnswer(){ansInfo.value=await xueqingAnswer(xueqing.value.studentId)}
async function drawCase(){caseQ.value=await caseDraw({groupNo:groupNo.value,serialNo:serialNo.value})}
async function drawTalk(){talkQ.value=await talkDraw()}
</script>
