package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ScoreVO;

public interface ScoreMapper
{
    public List<ScoreVO> selectScoreList(ScoreVO score);
}
