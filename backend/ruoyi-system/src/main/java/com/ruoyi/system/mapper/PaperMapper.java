package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Paper;
import com.ruoyi.system.domain.PaperQuestion;

/**
 * 试卷 数据层
 */
public interface PaperMapper
{
    public List<Paper> selectPaperList(Paper paper);
    public Paper selectPaperById(Long paperId);
    public int insertPaper(Paper paper);
    public int updatePaper(Paper paper);
    public int deletePaperById(Long paperId);
    public List<PaperQuestion> selectPaperQuestionByPaperId(Long paperId);
    public int insertPaperQuestion(PaperQuestion pq);
    public int deletePaperQuestionByPaperId(Long paperId);
}
