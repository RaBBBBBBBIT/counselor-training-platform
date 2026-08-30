package com.ruoyi.system.domain;

import java.util.List;
import com.ruoyi.common.core.domain.BaseEntity;

public class Competition extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long competitionId;
    private String competitionName;
    private String content;
    private Long organizerId;
    private String status;
    private String organizerName;
    private List<CompetitionStage> stages;
    private List<Long> participantIds;

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long v) { this.competitionId = v; }
    public String getCompetitionName() { return competitionName; }
    public void setCompetitionName(String v) { this.competitionName = v; }
    public String getContent() { return content; }
    public void setContent(String v) { this.content = v; }
    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long v) { this.organizerId = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String v) { this.organizerName = v; }
    public List<CompetitionStage> getStages() { return stages; }
    public void setStages(List<CompetitionStage> v) { this.stages = v; }
    public List<Long> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Long> v) { this.participantIds = v; }
}
