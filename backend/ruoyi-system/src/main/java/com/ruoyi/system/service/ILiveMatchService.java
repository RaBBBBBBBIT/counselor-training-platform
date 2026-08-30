package com.ruoyi.system.service;

import java.util.Map;

public interface ILiveMatchService
{
    public Map<String, Object> drawStudent();
    public Map<String, Object> studentAnswer(Long studentId);
    public Map<String, Object> drawCaseAnalysis(String groupNo, String serialNo);
    public Map<String, Object> drawTalk();
}
