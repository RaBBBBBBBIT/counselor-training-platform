package com.ruoyi.system.domain;

import java.util.List;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 试卷对象 paper
 */
public class Paper extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long paperId;
    private String paperName;
    private String generateMode;
    private Long bankId;
    private Integer totalScore;
    private String bankName;
    private List<Long> questionIds;
    private List<PaperRule> rules;

    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }
    public String getPaperName() { return paperName; }
    public void setPaperName(String paperName) { this.paperName = paperName; }
    public String getGenerateMode() { return generateMode; }
    public void setGenerateMode(String generateMode) { this.generateMode = generateMode; }
    public Long getBankId() { return bankId; }
    public void setBankId(Long bankId) { this.bankId = bankId; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public List<Long> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }
    public List<PaperRule> getRules() { return rules; }
    public void setRules(List<PaperRule> rules) { this.rules = rules; }
}
