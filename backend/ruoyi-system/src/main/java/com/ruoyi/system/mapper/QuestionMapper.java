package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.Question;

/**
 * 题目 数据层
 */
public interface QuestionMapper
{
    public List<Question> selectQuestionList(Question question);
    public Question selectQuestionById(Long questionId);
    public int insertQuestion(Question question);
    public int updateQuestion(Question question);
    public int deleteQuestionById(Long questionId);
    public int deleteQuestionByIds(Long[] questionIds);
    public int countPaperRefById(Long questionId);
    public List<Question> selectRandomQuestionList(@Param("bankId") Long bankId, @Param("questionType") String questionType, @Param("limit") Integer limit);
}
