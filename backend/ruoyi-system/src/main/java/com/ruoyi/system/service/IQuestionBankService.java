package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.QuestionBank;

public interface IQuestionBankService
{
    public List<QuestionBank> selectQuestionBankList(QuestionBank bank);
    public QuestionBank selectQuestionBankById(Long bankId);
    public int insertQuestionBank(QuestionBank bank);
    public int updateQuestionBank(QuestionBank bank);
    public int deleteQuestionBankByIds(Long[] bankIds);
    public int deleteQuestionBankById(Long bankId);
}
