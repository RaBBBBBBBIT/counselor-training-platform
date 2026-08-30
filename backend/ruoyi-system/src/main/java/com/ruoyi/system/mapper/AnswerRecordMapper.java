package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.AnswerRecord;
import com.ruoyi.system.domain.Competition;
import com.ruoyi.system.domain.PracticeBatch;

public interface AnswerRecordMapper
{
    public int insertAnswerRecord(AnswerRecord record);
    public int updateSubjectiveScore(@Param("userId") Long userId, @Param("questionId") Long questionId, @Param("subjectiveScore") Integer subjectiveScore, @Param("scoredBy") Long scoredBy);
    public List<AnswerRecord> selectByUserAndPaper(@Param("userId") Long userId, @Param("paperId") Long paperId);
    public List<AnswerRecord> selectByUserAndStage(@Param("userId") Long userId, @Param("competitionId") Long competitionId, @Param("stageId") Long stageId);
    public List<PracticeBatch> selectPracticeBatchByUser(Long userId);
    public List<Competition> selectCompetitionByUser(Long userId);
}
