package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.AnswerRecord;
import com.ruoyi.system.domain.Competition;
import com.ruoyi.system.domain.PracticeBatch;

public interface IExamService
{
    public List<PracticeBatch> myPractices(Long userId);
    public List<Competition> myCompetitions(Long userId);
    public List<Map<String, Object>> paperWithoutAnswers(Long paperId);
    public Map<String, Object> answerOne(AnswerRecord record);
    public int submitAll(Long userId, Long batchId, List<AnswerRecord> answers);
    public List<Map<String, Object>> competitionStagePaper(Long competitionId, Long stageId);
    public int submitCompetition(Long userId, Long competitionId, Long stageId, List<AnswerRecord> answers);
}
