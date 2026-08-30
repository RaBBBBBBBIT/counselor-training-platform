package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class CompetitionParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long participantId;
    private Long competitionId;
    private Long userId;
    private String userName;

    public Long getParticipantId() { return participantId; } public void setParticipantId(Long v) { this.participantId = v; }
    public Long getCompetitionId() { return competitionId; } public void setCompetitionId(Long v) { this.competitionId = v; }
    public Long getUserId() { return userId; } public void setUserId(Long v) { this.userId = v; }
    public String getUserName() { return userName; } public void setUserName(String v) { this.userName = v; }
}
