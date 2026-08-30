package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class PracticeParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long practiceParticipantId;
    private Long batchId;
    private Long userId;
    private String userName;

    public Long getPracticeParticipantId() { return practiceParticipantId; } public void setPracticeParticipantId(Long v) { this.practiceParticipantId = v; }
    public Long getBatchId() { return batchId; } public void setBatchId(Long v) { this.batchId = v; }
    public Long getUserId() { return userId; } public void setUserId(Long v) { this.userId = v; }
    public String getUserName() { return userName; } public void setUserName(String v) { this.userName = v; }
}
