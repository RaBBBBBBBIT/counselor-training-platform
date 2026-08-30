package com.ruoyi.system.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

public class AnswerRecord extends BaseEntity
{
    private java.util.List<AnswerRecord> answers;
    private static final long serialVersionUID = 1L;
    private Long recordId;
    private Long userId;
    private Long paperId;
    private Long questionId;
    private Long competitionId;
    private Long stageId;
    private Long batchId;
    private String userAnswer;
    private Integer objectiveScore;
    private Integer subjectiveScore;
    private String isCorrect;
    private String scoreStatus;
    private Date answeredAt;
    private Date scoredAt;

    public Long getRecordId() { return recordId; } public void setRecordId(Long v) { this.recordId = v; }
    public Long getUserId() { return userId; } public void setUserId(Long v) { this.userId = v; }
    public Long getPaperId() { return paperId; } public void setPaperId(Long v) { this.paperId = v; }
    public Long getQuestionId() { return questionId; } public void setQuestionId(Long v) { this.questionId = v; }
    public Long getCompetitionId() { return competitionId; } public void setCompetitionId(Long v) { this.competitionId = v; }
    public Long getStageId() { return stageId; } public void setStageId(Long v) { this.stageId = v; }
    public Long getBatchId() { return batchId; } public void setBatchId(Long v) { this.batchId = v; }
    public String getUserAnswer() { return userAnswer; } public void setUserAnswer(String v) { this.userAnswer = v; }
    public Integer getObjectiveScore() { return objectiveScore; } public void setObjectiveScore(Integer v) { this.objectiveScore = v; }
    public Integer getSubjectiveScore() { return subjectiveScore; } public void setSubjectiveScore(Integer v) { this.subjectiveScore = v; }
    public String getIsCorrect() { return isCorrect; } public void setIsCorrect(String v) { this.isCorrect = v; }
    public String getScoreStatus() { return scoreStatus; } public void setScoreStatus(String v) { this.scoreStatus = v; }
    public Date getAnsweredAt() { return answeredAt; } public void setAnsweredAt(Date v) { this.answeredAt = v; }
    public Date getScoredAt() { return scoredAt; } public void setScoredAt(Date v) { this.scoredAt = v; }
    public java.util.List<AnswerRecord> getAnswers() { return answers; } public void setAnswers(java.util.List<AnswerRecord> v) { this.answers = v; }
}
