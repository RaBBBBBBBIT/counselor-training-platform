package com.ruoyi.system.domain;

/**
 * 随机试卷抽题规则
 */
public class PaperRule
{
    private String questionType;
    private Integer count;
    private Integer score;

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
