package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.AnswerRecord;
import com.ruoyi.system.domain.ScoreVO;
import com.ruoyi.system.mapper.AnswerRecordMapper;
import com.ruoyi.system.mapper.QuestionMapper;
import com.ruoyi.system.mapper.ScoreMapper;
import com.ruoyi.system.service.IScoreService;

@Service
public class ScoreServiceImpl implements IScoreService
{
    @Autowired private ScoreMapper scoreMapper;
    @Autowired private AnswerRecordMapper answerRecordMapper;
    @Autowired private QuestionMapper questionMapper;

    @Override
    public List<ScoreVO> listScores(Long competitionId, Long batchId)
    {
        ScoreVO query = new ScoreVO();
        query.setCompetitionId(competitionId); query.setBatchId(batchId);
        List<ScoreVO> list = scoreMapper.selectScoreList(query);
        int lastRank = 0;
        Integer lastTotal = null;
        for (int i = 0; i < list.size(); i++)
        {
            ScoreVO vo = list.get(i);
            Integer total = vo.getTotalScore();
            if (!Integer.valueOf(total).equals(lastTotal)) { lastRank = i + 1; lastTotal = total; }
            vo.setRank(lastRank);
        }
        return list;
    }

    @Override
    @Transactional
    public int importSubjective(List<AnswerRecord> records)
    {
        if (records == null || records.isEmpty()) { throw new ServiceException("导入数据为空"); }
        for (AnswerRecord r : records)
        {
            if (r.getSubjectiveScore() == null) { throw new ServiceException("主观分不能为空"); }
            Integer max = questionMapper.selectQuestionById(r.getQuestionId()) != null ? questionMapper.selectQuestionById(r.getQuestionId()).getScore() : null;
            if (max == null) { throw new ServiceException("题目不存在：" + r.getQuestionId()); }
            if (r.getSubjectiveScore() < 0 || r.getSubjectiveScore() > max) { throw new ServiceException("题" + r.getQuestionId() + " 主观分越界(0~" + max + ")"); }
            answerRecordMapper.updateSubjectiveScore(r.getUserId(), r.getQuestionId(), r.getSubjectiveScore(), SecurityUtils.getUserId());
        }
        return 1;
    }
}
