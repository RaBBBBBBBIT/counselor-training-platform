package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Competition;
import com.ruoyi.system.domain.CompetitionParticipant;
import com.ruoyi.system.domain.CompetitionStage;

public interface ICompetitionService
{
    public List<Competition> selectCompetitionList(Competition competition);
    public Competition selectCompetitionById(Long competitionId);
    public int insertCompetition(Competition competition);
    public int updateCompetition(Competition competition);
    public int changeStatus(Long competitionId, String status);
    public int addStage(CompetitionStage stage);
    public int updateStage(CompetitionStage stage);
    public int deleteStage(Long stageId);
    public int addParticipant(Long competitionId, List<Long> userIds);
    public int removeParticipant(Long competitionId, Long userId);
    public List<CompetitionParticipant> selectParticipants(Long competitionId);
    public int deleteCompetitionById(Long competitionId);
}
