package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.Paper;
import com.ruoyi.system.domain.PaperQuestion;
import com.ruoyi.system.domain.PaperRule;
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.mapper.PaperMapper;
import com.ruoyi.system.mapper.QuestionMapper;
import com.ruoyi.system.service.IPaperService;

/**
 * 试卷 服务实现
 */
@Service
public class PaperServiceImpl implements IPaperService
{
    @Autowired
    private PaperMapper paperMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<Paper> selectPaperList(Paper paper)
    {
        return paperMapper.selectPaperList(paper);
    }

    @Override
    public Paper selectPaperById(Long paperId)
    {
        return paperMapper.selectPaperById(paperId);
    }

    @Override
    public List<PaperQuestion> selectPaperQuestionByPaperId(Long paperId)
    {
        return paperMapper.selectPaperQuestionByPaperId(paperId);
    }

    private String buildSnapshot(Question q, Integer score)
    {
        JSONObject o = new JSONObject();
        o.put("stem", q.getStem());
        o.put("options", q.getOptions());
        o.put("answer", q.getAnswer());
        o.put("score", score == null ? q.getScore() : score);
        return o.toJSONString();
    }

    @Override
    @Transactional
    public Long generateRandomPaper(Paper paper)
    {
        List<PaperRule> rules = paper.getRules();
        if (rules == null || rules.isEmpty())
        {
            throw new ServiceException("请设置随机抽题规则");
        }
        paper.setGenerateMode("RANDOM");
        paper.setTotalScore(0);
        paper.setCreateBy(SecurityUtils.getUsername());
        paperMapper.insertPaper(paper);
        Long paperId = paper.getPaperId();
        int orderNo = 1;
        int total = 0;
        for (PaperRule rule : rules)
        {
            if (rule.getCount() == null || rule.getCount() <= 0)
            {
                continue;
            }
            List<Question> qs = questionMapper.selectRandomQuestionList(paper.getBankId(), rule.getQuestionType(), rule.getCount());
            if (qs.size() < rule.getCount())
            {
                throw new ServiceException("题库中 " + rule.getQuestionType() + " 题目数量不足");
            }
            Integer score = rule.getScore() == null ? 5 : rule.getScore();
            for (Question q : qs)
            {
                PaperQuestion pq = new PaperQuestion();
                pq.setPaperId(paperId);
                pq.setQuestionId(q.getQuestionId());
                pq.setOrderNo(orderNo++);
                pq.setScore(score);
                pq.setSnapshot(buildSnapshot(q, score));
                paperMapper.insertPaperQuestion(pq);
                total += score;
            }
        }
        paper.setTotalScore(total);
        paperMapper.updatePaper(paper);
        return paperId;
    }

    @Override
    @Transactional
    public Long generateFixedPaper(Paper paper)
    {
        List<Long> questionIds = paper.getQuestionIds();
        if (questionIds == null || questionIds.isEmpty())
        {
            throw new ServiceException("请选择固定题目");
        }
        paper.setGenerateMode("FIXED");
        paper.setTotalScore(0);
        paper.setCreateBy(SecurityUtils.getUsername());
        paperMapper.insertPaper(paper);
        Long paperId = paper.getPaperId();
        int orderNo = 1;
        int total = 0;
        for (Long qid : questionIds)
        {
            Question q = questionMapper.selectQuestionById(qid);
            if (q == null)
            {
                throw new ServiceException("题目不存在：" + qid);
            }
            PaperQuestion pq = new PaperQuestion();
            pq.setPaperId(paperId);
            pq.setQuestionId(qid);
            pq.setOrderNo(orderNo++);
            pq.setScore(q.getScore());
            pq.setSnapshot(buildSnapshot(q, q.getScore()));
            paperMapper.insertPaperQuestion(pq);
            total += q.getScore();
        }
        paper.setTotalScore(total);
        paperMapper.updatePaper(paper);
        return paperId;
    }

    @Override
    @Transactional
    public int updatePaper(Paper paper)
    {
        paper.setUpdateBy(SecurityUtils.getUsername());
        return paperMapper.updatePaper(paper);
    }

    @Override
    @Transactional
    public int deletePaperById(Long paperId)
    {
        paperMapper.deletePaperQuestionByPaperId(paperId);
        return paperMapper.deletePaperById(paperId);
    }
}
