package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.domain.Student;
import com.ruoyi.system.mapper.QuestionMapper;
import com.ruoyi.system.mapper.StudentMapper;
import com.ruoyi.system.service.ILiveMatchService;

@Service
public class LiveMatchServiceImpl implements ILiveMatchService
{
    @Autowired private StudentMapper studentMapper;
    @Autowired private QuestionMapper questionMapper;

    @Override public Map<String, Object> drawStudent()
    {
        Student s = studentMapper.selectRandomStudent();
        if (s == null) { throw new ServiceException("暂无学生素材"); }
        java.util.Map<String,Object> m = new java.util.LinkedHashMap<>();
        m.put("studentId", s.getStudentId()); m.put("studentName", s.getStudentName()); m.put("photoUrl", s.getPhotoUrl());
        return m;
    }

    @Override public Map<String, Object> studentAnswer(Long studentId)
    {
        Student s = studentMapper.selectStudentById(studentId);
        if (s == null) { throw new ServiceException("学生不存在"); }
        java.util.Map<String,Object> m = new java.util.LinkedHashMap<>();
        m.put("studentId", s.getStudentId()); m.put("studentName", s.getStudentName()); m.put("info", s.getInfo());
        return m;
    }

    @Override public Map<String, Object> drawCaseAnalysis(String groupNo, String serialNo)
    {
        Question q = randomSubjective();
        java.util.Map<String,Object> m = new java.util.LinkedHashMap<>();
        m.put("groupNo", groupNo); m.put("serialNo", serialNo); m.put("questionId", q.getQuestionId()); m.put("stem", q.getStem());
        return m;
    }

    @Override public Map<String, Object> drawTalk()
    {
        Question q = randomSubjective();
        java.util.Map<String,Object> m = new java.util.LinkedHashMap<>();
        m.put("questionId", q.getQuestionId()); m.put("stem", q.getStem());
        return m;
    }

    private Question randomSubjective()
    {
        Question query = new Question();
        query.setQuestionType("SUBJECTIVE");
        List<Question> list = questionMapper.selectQuestionList(query);
        if (list == null || list.isEmpty()) { throw new ServiceException("暂无主观题，请先在题库添加"); }
        return list.get(new Random().nextInt(list.size()));
    }
}
