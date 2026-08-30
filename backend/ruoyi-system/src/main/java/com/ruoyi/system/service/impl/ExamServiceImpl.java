package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.AnswerRecord;
import com.ruoyi.system.domain.Competition;
import com.ruoyi.system.domain.CompetitionStage;
import com.ruoyi.system.domain.PaperQuestion;
import com.ruoyi.system.domain.PracticeBatch;
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.mapper.AnswerRecordMapper;
import com.ruoyi.system.mapper.CompetitionStageMapper;
import com.ruoyi.system.mapper.PaperMapper;
import com.ruoyi.system.mapper.QuestionMapper;
import com.ruoyi.system.service.IExamService;

@Service
public class ExamServiceImpl implements IExamService
{
    @Autowired private AnswerRecordMapper answerRecordMapper;
    @Autowired private PaperMapper paperMapper;
    @Autowired private QuestionMapper questionMapper;
    @Autowired private CompetitionStageMapper stageMapper;

    @Override public List<PracticeBatch> myPractices(Long userId) { return answerRecordMapper.selectPracticeBatchByUser(userId); }
    @Override public List<Competition> myCompetitions(Long userId) { return answerRecordMapper.selectCompetitionByUser(userId); }

    @Override
    public List<Map<String, Object>> paperWithoutAnswers(Long paperId)
    {
        List<PaperQuestion> pqs = paperMapper.selectPaperQuestionByPaperId(paperId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (PaperQuestion pq : pqs)
        {
            JSONObject snap = JSON.parseObject(pq.getSnapshot());
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("questionId", pq.getQuestionId());
            m.put("stem", snap.getString("stem"));
            m.put("options", snap.getString("options"));
            m.put("score", pq.getScore());
            result.add(m);
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> answerOne(AnswerRecord record)
    {
        Question q = questionMapper.selectQuestionById(record.getQuestionId());
        if (q == null) { throw new ServiceException("题目不存在"); }
        judge(q, record);
        record.setAnsweredAt(new Date());
        answerRecordMapper.insertAnswerRecord(record);
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("objectiveScore", record.getObjectiveScore());
        m.put("isCorrect", record.getIsCorrect());
        m.put("correctAnswer", q.getAnswer());
        return m;
    }

    @Override
    @Transactional
    public int submitAll(Long userId, Long batchId, List<AnswerRecord> answers)
    {
        for (AnswerRecord a : answers)
        {
            Question q = questionMapper.selectQuestionById(a.getQuestionId());
            judge(q, a);
            a.setUserId(userId); a.setBatchId(batchId); a.setAnsweredAt(new Date());
            answerRecordMapper.insertAnswerRecord(a);
        }
        return 1;
    }

    @Override
    public List<Map<String, Object>> competitionStagePaper(Long competitionId, Long stageId)
    {
        CompetitionStage st = stageMapper.selectStageById(stageId);
        if (st == null) { throw new ServiceException("环节不存在"); }
        return paperWithoutAnswers(st.getPaperId());
    }

    @Override
    @Transactional
    public int submitCompetition(Long userId, Long competitionId, Long stageId, List<AnswerRecord> answers)
    {
        for (AnswerRecord a : answers)
        {
            Question q = questionMapper.selectQuestionById(a.getQuestionId());
            judge(q, a);
            a.setUserId(userId); a.setCompetitionId(competitionId); a.setStageId(stageId); a.setAnsweredAt(new Date());
            answerRecordMapper.insertAnswerRecord(a);
        }
        return 1;
    }

    private void judge(Question q, AnswerRecord rec)
    {
        String type = q.getQuestionType();
        if ("SUBJECTIVE".equals(type))
        {
            rec.setObjectiveScore(null);
            rec.setSubjectiveScore(null);
            rec.setIsCorrect(null);
            rec.setScoreStatus("PENDING");
            return;
        }
        String correct = q.getAnswer();
        String user = rec.getUserAnswer();
        boolean ok = equalsAnswer(correct, user);
        rec.setObjectiveScore(ok ? q.getScore() : 0);
        rec.setIsCorrect(ok ? "1" : "0");
        rec.setScoreStatus("SCORED");
    }

    private boolean equalsAnswer(String correctAns, String userAns)
    {
        Set<String> correct = parseSet(correctAns);
        Set<String> user = parseSet(userAns);
        return correct != null && user != null && correct.equals(user);
    }

    private Set<String> parseSet(String ans)
    {
        if (ans == null) { return null; }
        Set<String> set = new HashSet<>();
        String t = ans.trim();
        if (t.startsWith("["))
        {
            JSONArray arr = JSON.parseArray(t);
            for (Object o : arr) { set.add(String.valueOf(o)); }
        }
        else
        {
            set.add(t);
        }
        return set;
    }
}
