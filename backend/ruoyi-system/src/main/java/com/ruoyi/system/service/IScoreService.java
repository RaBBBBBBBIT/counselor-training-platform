package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AnswerRecord;
import com.ruoyi.system.domain.ScoreVO;

public interface IScoreService
{
    public List<ScoreVO> listScores(Long competitionId, Long batchId);
    public int importSubjective(List<AnswerRecord> records);
}
