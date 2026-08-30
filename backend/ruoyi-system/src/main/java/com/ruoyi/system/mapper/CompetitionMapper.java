package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.Competition;

public interface CompetitionMapper
{
    public List<Competition> selectCompetitionList(Competition competition);
    public Competition selectCompetitionById(Long competitionId);
    public int insertCompetition(Competition competition);
    public int updateCompetition(Competition competition);
    public int deleteCompetitionById(Long competitionId);
    public int updateCompetitionStatus(@Param("competitionId") Long competitionId, @Param("status") String status);
}
