package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.PracticeParticipant;

public interface PracticeParticipantMapper
{
    public List<PracticeParticipant> selectByBatchId(Long batchId);
    public int insertParticipant(PracticeParticipant p);
    public int deleteParticipant(@Param("batchId") Long batchId, @Param("userId") Long userId);
    public int deleteParticipantByBatchId(Long batchId);
}
