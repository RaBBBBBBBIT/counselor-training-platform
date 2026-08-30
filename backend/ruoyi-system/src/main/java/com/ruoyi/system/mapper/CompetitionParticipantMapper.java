package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.CompetitionParticipant;

public interface CompetitionParticipantMapper
{
    public List<CompetitionParticipant> selectParticipantListByCompetitionId(Long competitionId);
    public int insertParticipant(CompetitionParticipant participant);
    public int deleteParticipant(@Param("competitionId") Long competitionId, @Param("userId") Long userId);
    public int deleteParticipantByCompetitionId(Long competitionId);
}
