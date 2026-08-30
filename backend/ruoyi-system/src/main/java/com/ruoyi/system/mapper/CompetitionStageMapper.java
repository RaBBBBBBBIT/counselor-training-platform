package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CompetitionStage;

public interface CompetitionStageMapper
{
    public List<CompetitionStage> selectStageListByCompetitionId(Long competitionId);
    public CompetitionStage selectStageById(Long stageId);
    public int insertStage(CompetitionStage stage);
    public int updateStage(CompetitionStage stage);
    public int deleteStageById(Long stageId);
    public int deleteStageByCompetitionId(Long competitionId);
}
