package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;
import com.ruoyi.common.core.domain.BaseEntity;

public class PracticeBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long batchId;
    private String batchName;
    private String mode;
    private Long paperId;
    private Date startTime;
    private Date endTime;
    private String paperName;
    private List<Long> participantIds;

    public Long getBatchId() { return batchId; } public void setBatchId(Long v) { this.batchId = v; }
    public String getBatchName() { return batchName; } public void setBatchName(String v) { this.batchName = v; }
    public String getMode() { return mode; } public void setMode(String v) { this.mode = v; }
    public Long getPaperId() { return paperId; } public void setPaperId(Long v) { this.paperId = v; }
    public Date getStartTime() { return startTime; } public void setStartTime(Date v) { this.startTime = v; }
    public Date getEndTime() { return endTime; } public void setEndTime(Date v) { this.endTime = v; }
    public String getPaperName() { return paperName; } public void setPaperName(String v) { this.paperName = v; }
    public List<Long> getParticipantIds() { return participantIds; } public void setParticipantIds(List<Long> v) { this.participantIds = v; }
}
