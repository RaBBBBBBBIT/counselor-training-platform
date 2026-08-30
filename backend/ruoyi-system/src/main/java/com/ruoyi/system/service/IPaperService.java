package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Paper;
import com.ruoyi.system.domain.PaperQuestion;

public interface IPaperService
{
    public List<Paper> selectPaperList(Paper paper);
    public Paper selectPaperById(Long paperId);
    public List<PaperQuestion> selectPaperQuestionByPaperId(Long paperId);
    public Long generateRandomPaper(Paper paper);
    public Long generateFixedPaper(Paper paper);
    public int updatePaper(Paper paper);
    public int deletePaperById(Long paperId);
}
