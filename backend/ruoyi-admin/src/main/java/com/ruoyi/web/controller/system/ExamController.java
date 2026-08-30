package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.AnswerRecord;
import com.ruoyi.system.service.IExamService;

@RestController
@RequestMapping("/api/v1/exams")
public class ExamController extends BaseController
{
    @Autowired private IExamService examService;

    @PreAuthorize("@ss.hasPermi('exam:list')")
    @GetMapping("/practices")
    public AjaxResult practices() { return success(examService.myPractices(getUserId())); }

    @PreAuthorize("@ss.hasPermi('exam:list')")
    @GetMapping("/competitions")
    public AjaxResult competitions() { return success(examService.myCompetitions(getUserId())); }

    @PreAuthorize("@ss.hasPermi('exam:list')")
    @GetMapping("/papers/{paperId}")
    public AjaxResult paper(@PathVariable Long paperId) { return success(examService.paperWithoutAnswers(paperId)); }

    @PreAuthorize("@ss.hasPermi('exam:submit')")
    @PostMapping("/answers")
    public AjaxResult answerOne(@RequestBody AnswerRecord record)
    {
        record.setUserId(getUserId());
        return success(examService.answerOne(record));
    }

    @PreAuthorize("@ss.hasPermi('exam:submit')")
    @PostMapping("/submissions")
    public AjaxResult submitAll(@RequestBody AnswerRecord req)
    {
        return toAjax(examService.submitAll(getUserId(), req.getBatchId(), req.getAnswers()));
    }

    @PreAuthorize("@ss.hasPermi('exam:list')")
    @GetMapping("/competitions/{competitionId}/stages/{stageId}/paper")
    public AjaxResult stagePaper(@PathVariable Long competitionId, @PathVariable Long stageId)
    {
        return success(examService.competitionStagePaper(competitionId, stageId));
    }

    @PreAuthorize("@ss.hasPermi('exam:submit')")
    @PostMapping("/competitions/{competitionId}/stages/{stageId}/submit")
    public AjaxResult submitCompetition(@PathVariable Long competitionId, @PathVariable Long stageId, @RequestBody AnswerRecord req)
    {
        return toAjax(examService.submitCompetition(getUserId(), competitionId, stageId, req.getAnswers()));
    }
}
