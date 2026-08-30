package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.domain.QuestionBank;
import com.ruoyi.system.mapper.QuestionBankMapper;
import com.ruoyi.system.mapper.QuestionMapper;
import com.ruoyi.system.service.IQuestionService;

/**
 * 题目 服务实现
 */
@Service
public class QuestionServiceImpl implements IQuestionService
{
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionBankMapper bankMapper;

    @Override
    public List<Question> selectQuestionList(Question question)
    {
        return questionMapper.selectQuestionList(question);
    }

    @Override
    public Question selectQuestionById(Long questionId)
    {
        return questionMapper.selectQuestionById(questionId);
    }

    private QuestionBank getBank(Long bankId)
    {
        return bankMapper.selectQuestionBankById(bankId);
    }

    @Override
    @Transactional
    public int insertQuestion(Question question)
    {
        QuestionBank bank = getBank(question.getBankId());
        if (bank != null && "1".equals(bank.getShared()))
        {
            throw new ServiceException("共享题库不允许新增题目");
        }
        if (bank == null)
        {
            throw new ServiceException("所属题库不存在");
        }
        question.setCreateBy(SecurityUtils.getUsername());
        return questionMapper.insertQuestion(question);
    }

    @Override
    @Transactional
    public int updateQuestion(Question question)
    {
        Question old = questionMapper.selectQuestionById(question.getQuestionId());
        if (old != null)
        {
            QuestionBank bank = getBank(old.getBankId());
            if (bank != null && "1".equals(bank.getShared()))
            {
                throw new ServiceException("共享题库题目不允许修改");
            }
        }
        question.setUpdateBy(SecurityUtils.getUsername());
        return questionMapper.updateQuestion(question);
    }

    @Override
    @Transactional
    public int deleteQuestionByIds(Long[] questionIds)
    {
        for (Long id : questionIds)
        {
            Question q = questionMapper.selectQuestionById(id);
            if (q != null)
            {
                QuestionBank bank = getBank(q.getBankId());
                if (bank != null && "1".equals(bank.getShared()))
                {
                    throw new ServiceException("共享题库题目只读，不允许删除");
                }
                if (questionMapper.countPaperRefById(id) > 0)
                {
                    throw new ServiceException("题目已被试卷使用，不允许删除");
                }
            }
        }
        return questionMapper.deleteQuestionByIds(questionIds);
    }

    @Override
    @Transactional
    public int deleteQuestionById(Long questionId)
    {
        return deleteQuestionByIds(new Long[] { questionId });
    }
}
