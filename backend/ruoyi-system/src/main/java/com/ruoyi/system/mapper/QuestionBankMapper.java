package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.QuestionBank;

/**
 * 题库 数据层
 */
public interface QuestionBankMapper
{
    public List<QuestionBank> selectQuestionBankList(QuestionBank bank);
    public QuestionBank selectQuestionBankById(Long bankId);
    public int insertQuestionBank(QuestionBank bank);
    public int updateQuestionBank(QuestionBank bank);
    public int deleteQuestionBankById(Long bankId);
    public int deleteQuestionBankByIds(Long[] bankIds);
    public int countQuestionByBankId(Long bankId);
}
