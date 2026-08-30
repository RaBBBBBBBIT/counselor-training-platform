package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 试卷题目快照对象 paper_question
 */
public class PaperQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long paperQuestionId;
    private Long paperId;
    private Long questionId;
    private Integer orderNo;
    private Integer score;
    private String snapshot;

    public Long getPaperQuestionId() { return paperQuestionId; }
    public void setPaperQuestionId(Long paperQuestionId) { this.paperQuestionId = paperQuestionId; }
    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Integer getOrderNo() { return orderNo; }
    public void setOrderNo(Integer orderNo) { this.orderNo = orderNo; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getSnapshot() { return snapshot; }
    public void setSnapshot(String snapshot) { this.snapshot = snapshot; }
}
