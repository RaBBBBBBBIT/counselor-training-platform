package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Student;

public interface StudentMapper
{
    public List<Student> selectStudentList(Student student);
    public Student selectStudentById(Long studentId);
    public Student selectRandomStudent();
    public int insertStudent(Student student);
    public int deleteStudentById(Long studentId);
}
