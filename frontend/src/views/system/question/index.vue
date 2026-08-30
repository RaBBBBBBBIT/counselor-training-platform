<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <!-- 题库列表 -->
      <el-tab-pane label="题库列表" name="bank">
        <el-form :inline="true" :model="bankQuery">
          <el-form-item label="题库名称"><el-input v-model="bankQuery.bankName" placeholder="请输入" clearable /></el-form-item>
          <el-form-item label="类型">
            <el-select v-model="bankQuery.shared" placeholder="全部" clearable>
              <el-option label="自建" value="0"/><el-option label="共享" value="1"/>
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" @click="loadBanks">查询</el-button>
            <el-button type="success" @click="openBank()">新增</el-button></el-form-item>
        </el-form>
        <el-table :data="bankList" v-loading="loading" border>
          <el-table-column prop="bankId" label="ID" width="60"/>
          <el-table-column prop="bankName" label="题库名称"/>
          <el-table-column label="类型" width="80"><template #default="s">{{ s.row.shared==='1'?'共享':'自建' }}</template></el-table-column>
          <el-table-column prop="ownerName" label="所属人"/>
          <el-table-column prop="createTime" label="创建时间" width="170"/>
          <el-table-column label="操作" width="140"><template #default="s">
            <el-button link type="primary" @click="openBank(s.row)">编辑</el-button>
            <el-button link type="danger" @click="removeBank(s.row)">删除</el-button>
          </template></el-table-column>
        </el-table>
        <el-pagination v-model:current-page="bankQuery.pageNum" v-model:page-size="bankQuery.pageSize" :total="bankTotal"
          layout="total, prev, pager, next" @current-change="loadBanks"/>
      </el-tab-pane>

      <!-- 题目列表 -->
      <el-tab-pane label="题目列表" name="question">
        <el-form :inline="true" :model="qQuery">
          <el-form-item label="所属题库">
            <el-select v-model="qQuery.bankId" placeholder="全部" clearable><el-option v-for="b in bankList" :key="b.bankId" :label="b.bankName" :value="b.bankId"/></el-select>
          </el-form-item>
          <el-form-item label="题型">
            <el-select v-model="qQuery.questionType" placeholder="全部" clearable>
              <el-option label="单选" value="SINGLE"/><el-option label="多选" value="MULTIPLE"/>
              <el-option label="判断" value="JUDGE"/><el-option label="主观" value="SUBJECTIVE"/>
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" @click="loadQuestions">查询</el-button>
            <el-button type="success" @click="openQuestion()">新增</el-button></el-form-item>
        </el-form>
        <el-table :data="questionList" v-loading="loading" border>
          <el-table-column prop="questionId" label="ID" width="60"/>
          <el-table-column prop="stem" label="题干" show-overflow-tooltip/>
          <el-table-column label="题型" width="80"><template #default="s">{{ typeName(s.row.questionType) }}</template></el-table-column>
          <el-table-column prop="difficulty" label="难度" width="80"/>
          <el-table-column prop="score" label="分值" width="70"/>
          <el-table-column prop="bankName" label="所属题库"/>
          <el-table-column label="操作" width="140"><template #default="s">
            <el-button link type="primary" @click="openQuestion(s.row)">编辑</el-button>
            <el-button link type="danger" @click="removeQuestion(s.row)">删除</el-button>
          </template></el-table-column>
        </el-table>
        <el-pagination v-model:current-page="qQuery.pageNum" v-model:page-size="qQuery.pageSize" :total="questionTotal"
          layout="total, prev, pager, next" @current-change="loadQuestions"/>
      </el-tab-pane>

      <!-- 试卷管理 -->
      <el-tab-pane label="试卷管理" name="paper">
        <div style="margin-bottom:10px">
          <el-button type="success" @click="openPaper('RANDOM')">生成随机试卷</el-button>
          <el-button type="warning" @click="openPaper('FIXED')">生成固定试卷</el-button>
        </div>
        <el-table :data="paperList" v-loading="loading" border>
          <el-table-column prop="paperId" label="ID" width="60"/>
          <el-table-column prop="paperName" label="试卷名称"/>
          <el-table-column label="生成方式" width="90"><template #default="s">{{ s.row.generateMode==='RANDOM'?'随机':'固定' }}</template></el-table-column>
          <el-table-column prop="totalScore" label="总分" width="80"/>
          <el-table-column prop="bankName" label="来源题库"/>
          <el-table-column prop="createTime" label="创建时间" width="170"/>
          <el-table-column label="操作" width="90"><template #default="s">
            <el-button link type="danger" @click="removePaper(s.row)">删除</el-button>
          </template></el-table-column>
        </el-table>
        <el-pagination v-model:current-page="paperQuery.pageNum" v-model:page-size="paperQuery.pageSize" :total="paperTotal"
          layout="total, prev, pager, next" @current-change="loadPapers"/>
      </el-tab-pane>
    </el-tabs>

    <!-- 题库对话框 -->
    <el-dialog v-model="bankDialog" :title="bankForm.bankId?'编辑题库':'新增题库'" width="460">
      <el-form :model="bankForm" label-width="80">
        <el-form-item label="题库名称"><el-input v-model="bankForm.bankName"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="bankForm.shared"><el-option label="自建" value="0"/><el-option label="共享" value="1"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="bankDialog=false">取消</el-button><el-button type="primary" @click="saveBank">确定</el-button></template>
    </el-dialog>

    <!-- 题目对话框 -->
    <el-dialog v-model="questionDialog" :title="questionForm.questionId?'编辑题目':'新增题目'" width="620">
      <el-form :model="questionForm" label-width="80">
        <el-form-item label="所属题库"><el-select v-model="questionForm.bankId" style="width:100%"><el-option v-for="b in bankList" :key="b.bankId" :label="b.bankName" :value="b.bankId"/></el-select></el-form-item>
        <el-form-item label="题型"><el-select v-model="questionForm.questionType" @change="questionForm.options='';questionForm.answer=''">
          <el-option label="单选" value="SINGLE"/><el-option label="多选" value="MULTIPLE"/><el-option label="判断" value="JUDGE"/><el-option label="主观" value="SUBJECTIVE"/></el-select></el-form-item>
        <el-form-item label="题干"><el-input type="textarea" v-model="questionForm.stem"/></el-form-item>
        <el-form-item v-if="questionForm.questionType!=='SUBJECTIVE'" label="选项">
          <template v-if="questionForm.questionType==='JUDGE'">
            <el-radio-group v-model="questionForm.answer"><el-radio value="TRUE">正确</el-radio><el-radio value="FALSE">错误</el-radio></el-radio-group>
          </template>
          <template v-else>
            <div v-for="(opt,i) in options" :key="i" style="display:flex;gap:6px;margin-bottom:4px">
              <el-input v-model="opt.key" style="width:70px" placeholder="键"/><el-input v-model="opt.text" placeholder="选项文本"/>
              <el-button link type="danger" @click="options.splice(i,1)">删</el-button>
            </div>
            <el-button link type="primary" @click="options.push({key:'A',text:''})">+ 选项</el-button>
          </template>
        </el-form-item>
        <el-form-item v-if="!['SUBJECTIVE','JUDGE'].includes(questionForm.questionType)" label="正确答案">
          <el-checkbox-group v-model="ansKeys"><el-checkbox v-for="o in options" :key="o.key" :value="o.key">{{ o.key }}</el-checkbox></el-checkbox-group>
        </el-form-item>
        <el-form-item v-if="questionForm.questionType==='SUBJECTIVE'" label="参考答案"><el-input type="textarea" v-model="questionForm.answer"/></el-form-item>
        <el-form-item label="分值"><el-input-number v-model="questionForm.score" :min="1"/></el-form-item>
        <el-form-item label="难度"><el-select v-model="questionForm.difficulty"><el-option label="简单" value="EASY"/><el-option label="中等" value="MEDIUM"/><el-option label="困难" value="HARD"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="questionDialog=false">取消</el-button><el-button type="primary" @click="saveQuestion">确定</el-button></template>
    </el-dialog>

    <!-- 试卷生成对话框 -->
    <el-dialog v-model="paperDialog" :title="paperForm.generateMode==='RANDOM'?'生成随机试卷':'生成固定试卷'" width="520">
      <el-form :model="paperForm" label-width="90">
        <el-form-item label="试卷名称"><el-input v-model="paperForm.paperName"/></el-form-item>
        <template v-if="paperForm.generateMode==='RANDOM'">
          <el-form-item label="来源题库"><el-select v-model="paperForm.bankId"><el-option v-for="b in bankList" :key="b.bankId" :label="b.bankName" :value="b.bankId"/></el-select></el-form-item>
          <el-form-item label="抽题规则">
            <div v-for="(rule,i) in rules" :key="i" style="display:flex;gap:6px;margin-bottom:4px">
              <el-select v-model="rule.questionType" placeholder="题型" style="width:110px"><el-option label="单选" value="SINGLE"/><el-option label="多选" value="MULTIPLE"/><el-option label="判断" value="JUDGE"/><el-option label="主观" value="SUBJECTIVE"/></el-select>
              <el-input-number v-model="rule.count" :min="1" placeholder="数量" style="width:90px"/>
              <el-input-number v-model="rule.score" :min="1" placeholder="分值" style="width:90px"/>
              <el-button link type="danger" @click="rules.splice(i,1)">删</el-button>
            </div>
            <el-button link type="primary" @click="rules.push({questionType:'SINGLE',count:1,score:5})">+ 规则</el-button>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="选择题目"><el-select v-model="paperForm.questionIds" multiple filterable style="width:100%">
            <el-option v-for="q in questionList" :key="q.questionId" :label="q.stem" :value="q.questionId"/></el-select></el-form-item>
        </template>
      </el-form>
      <template #footer><el-button @click="paperDialog=false">取消</el-button><el-button type="primary" @click="savePaper">生成</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listBank, addBank, updateBank, delBank, listQuestion, addQuestion, updateQuestion, delQuestion, listPaper, genRandomPaper, genFixedPaper, delPaper } from '@/api/question'

const activeTab = ref('bank')
const loading = ref(false)
const bankList = ref([]); const bankTotal = ref(0)
const bankQuery = reactive({ pageNum: 1, pageSize: 10, bankName: '', shared: '' })
const questionList = ref([]); const questionTotal = ref(0)
const qQuery = reactive({ pageNum: 1, pageSize: 10, bankId: null, questionType: '' })
const paperList = ref([]); const paperTotal = ref(0)
const paperQuery = reactive({ pageNum: 1, pageSize: 10 })

const bankDialog = ref(false); const bankForm = reactive({ bankId: null, bankName: '', shared: '0' })
const questionDialog = ref(false); const questionForm = reactive({ questionId: null, bankId: null, questionType: 'SINGLE', stem: '', answer: '', score: 5, difficulty: 'MEDIUM' })
const options = ref([{ key: 'A', text: '' }, { key: 'B', text: '' }])
const ansKeys = ref([])
const paperDialog = ref(false); const paperForm = reactive({ generateMode: 'RANDOM', paperName: '', bankId: null, questionIds: [] })
const rules = ref([{ questionType: 'SINGLE', count: 1, score: 5 }])

function typeName(t){ return {SINGLE:'单选',MULTIPLE:'多选',JUDGE:'判断',SUBJECTIVE:'主观'}[t]||t }

async function loadBanks(){ loading.value=true; try { const r=await listBank(bankQuery); bankList.value=r.rows; bankTotal.value=r.total } finally { loading.value=false } }
async function loadQuestions(){ loading.value=true; try { const r=await listQuestion(qQuery); questionList.value=r.rows; questionTotal.value=r.total } finally { loading.value=false } }
async function loadPapers(){ loading.value=true; try { const r=await listPaper(paperQuery); paperList.value=r.rows; paperTotal.value=r.total } finally { loading.value=false } }

onMounted(()=>{ loadBanks(); loadQuestions(); loadPapers() })
watch(activeTab, t=>{ if(t==='bank')loadBanks(); else if(t==='question')loadQuestions(); else loadPapers() })

function openBank(row){ if(row){ Object.assign(bankForm,{bankId:row.bankId,bankName:row.bankName,shared:row.shared}) } else { Object.assign(bankForm,{bankId:null,bankName:'',shared:'0'}) } bankDialog.value=true }
async function saveBank(){ if(!bankForm.bankName){ElMessage.warning('请输入题库名称');return} if(bankForm.bankId){await updateBank(bankForm)}else{await addBank(bankForm)} bankDialog.value=false; loadBanks(); ElMessage.success('成功') }
async function removeBank(row){ await ElMessageBox.confirm('确认删除该题库?','提示',{type:'warning'}); await delBank(row.bankId); loadBanks(); ElMessage.success('删除成功') }

function openQuestion(row){ if(row){ Object.assign(questionForm,{questionId:row.questionId,bankId:row.bankId,questionType:row.questionType,stem:row.stem,answer:row.answer,score:row.score,difficulty:row.difficulty}); try{ options.value=JSON.parse(row.options||'[]'); }catch(e){ options.value=[] } ansKeys.value=JSON.parse(row.answer||'[]'); if(typeof ansKeys.value==='string'){ansKeys.value=[ansKeys.value]} } else { Object.assign(questionForm,{questionId:null,bankId:null,questionType:'SINGLE',stem:'',answer:'',score:5,difficulty:'MEDIUM'}); options.value=[{key:'A',text:''},{key:'B',text:''}]; ansKeys.value=[] } questionDialog.value=true }
async function saveQuestion(){
  const f=questionForm; if(!f.bankId||!f.stem){ElMessage.warning('请选择题库并填写题干');return}
  if(f.questionType==='SUBJECTIVE'){ f.answer=f.answer; f.options=null }
  else if(f.questionType==='JUDGE'){ f.options=JSON.stringify(options.value); f.answer=JSON.stringify([ansKeys.value[0]||'FALSE']) }
  else { f.options=JSON.stringify(options.value); f.answer=JSON.stringify(ansKeys.value) }
  if(f.questionId){await updateQuestion(f)}else{await addQuestion(f)} questionDialog.value=false; loadQuestions(); ElMessage.success('成功') }
async function removeQuestion(row){ await ElMessageBox.confirm('确认删除该题目?','提示',{type:'warning'}); await delQuestion(row.questionId); loadQuestions(); ElMessage.success('删除成功') }

function openPaper(mode){ Object.assign(paperForm,{generateMode:mode,paperName:'',bankId:null,questionIds:[]}); paperDialog.value=true; if(mode==='FIXED'){loadQuestions()} }
async function savePaper(){ if(!paperForm.paperName){ElMessage.warning('请输入试卷名称');return} if(paperForm.generateMode==='RANDOM'){ await genRandomPaper(paperForm) } else { if(!paperForm.questionIds.length){ElMessage.warning('请选择题目');return} await genFixedPaper(paperForm) } paperDialog.value=false; loadPapers(); ElMessage.success('生成成功') }
async function removePaper(row){ await ElMessageBox.confirm('确认删除该试卷?','提示',{type:'warning'}); await delPaper(row.paperId); loadPapers(); ElMessage.success('删除成功') }
</script>
