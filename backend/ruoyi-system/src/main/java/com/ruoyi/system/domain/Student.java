package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class Student extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long studentId;
    private String studentName;
    private String photoUrl;
    private Long deptId;
    private String info;

    public Long getStudentId() { return studentId; } public void setStudentId(Long v) { this.studentId = v; }
    public String getStudentName() { return studentName; } public void setStudentName(String v) { this.studentName = v; }
    public String getPhotoUrl() { return photoUrl; } public void setPhotoUrl(String v) { this.photoUrl = v; }
    public Long getDeptId() { return deptId; } public void setDeptId(Long v) { this.deptId = v; }
    public String getInfo() { return info; } public void setInfo(String v) { this.info = v; }
}
