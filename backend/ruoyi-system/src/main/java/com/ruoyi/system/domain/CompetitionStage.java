package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class CompetitionStage extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long stageId;
    private Long competitionId;
    private String stageName;
    private String stageType;
    private Integer orderNo;
    private Long paperId;
    private String paperName;

    public Long getStageId() { return stageId; } public void setStageId(Long v) { this.stageId = v; }
    public Long getCompetitionId() { return competitionId; } public void setCompetitionId(Long v) { this.competitionId = v; }
    public String getStageName() { return stageName; } public void setStageName(String v) { this.stageName = v; }
    public String getStageType() { return stageType; } public void setStageType(String v) { this.stageType = v; }
    public Integer getOrderNo() { return orderNo; } public void setOrderNo(Integer v) { this.orderNo = v; }
    public Long getPaperId() { return paperId; } public void setPaperId(Long v) { this.paperId = v; }
    public String getPaperName() { return paperName; } public void setPaperName(String v) { this.paperName = v; }
}
