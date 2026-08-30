package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Question;

public interface IQuestionService
{
    public List<Question> selectQuestionList(Question question);
    public Question selectQuestionById(Long questionId);
    public int insertQuestion(Question question);
    public int updateQuestion(Question question);
    public int deleteQuestionByIds(Long[] questionIds);
    public int deleteQuestionById(Long questionId);
}
