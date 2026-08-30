package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.QuestionBank;
import com.ruoyi.system.mapper.QuestionBankMapper;
import com.ruoyi.system.service.IQuestionBankService;

/**
 * 题库 服务实现
 */
@Service
public class QuestionBankServiceImpl implements IQuestionBankService
{
    @Autowired
    private QuestionBankMapper bankMapper;

    @Override
    public List<QuestionBank> selectQuestionBankList(QuestionBank bank)
    {
        return bankMapper.selectQuestionBankList(bank);
    }

    @Override
    public QuestionBank selectQuestionBankById(Long bankId)
    {
        return bankMapper.selectQuestionBankById(bankId);
    }

    @Override
    @Transactional
    public int insertQuestionBank(QuestionBank bank)
    {
        bank.setCreateBy(SecurityUtils.getUsername());
        return bankMapper.insertQuestionBank(bank);
    }

    @Override
    @Transactional
    public int updateQuestionBank(QuestionBank bank)
    {
        long bankId = bank.getBankId();
        QuestionBank old = bankMapper.selectQuestionBankById(bankId);
        if (old != null && "1".equals(old.getShared()))
        {
            throw new ServiceException("共享题库不允许修改");
        }
        bank.setUpdateBy(SecurityUtils.getUsername());
        return bankMapper.updateQuestionBank(bank);
    }

    @Override
    @Transactional
    public int deleteQuestionBankByIds(Long[] bankIds)
    {
        for (Long id : bankIds)
        {
            QuestionBank old = bankMapper.selectQuestionBankById(id);
            if (old != null && "1".equals(old.getShared()))
            {
                throw new ServiceException("共享题库不允许删除");
            }
            if (bankMapper.countQuestionByBankId(id) > 0)
            {
                throw new ServiceException("题库下存在题目，不允许删除");
            }
        }
        return bankMapper.deleteQuestionBankByIds(bankIds);
    }

    @Override
    @Transactional
    public int deleteQuestionBankById(Long bankId)
    {
        return deleteQuestionBankByIds(new Long[] { bankId });
    }
}
