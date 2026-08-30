package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.AnswerRecord;
import com.ruoyi.system.service.IScoreService;

@RestController
@RequestMapping("/api/v1/scores")
public class ScoreController extends BaseController
{
    @Autowired private IScoreService scoreService;

    @PreAuthorize("@ss.hasPermi('score:list')")
    @GetMapping("/list")
    public AjaxResult list(Long competitionId, Long batchId) { return success(scoreService.listScores(competitionId, batchId)); }

    @PreAuthorize("@ss.hasPermi('score:import')")
    @Log(title = "导入主观成绩", businessType = BusinessType.IMPORT)
    @PostMapping("/subjective/import")
    public AjaxResult importSubjective(@RequestBody List<AnswerRecord> records) { return toAjax(scoreService.importSubjective(records)); }

    @PreAuthorize("@ss.hasPermi('score:export')")
    @Log(title = "导出主观成绩", businessType = BusinessType.EXPORT)
    @GetMapping("/subjective/export")
    public AjaxResult export() { return success(scoreService.listScores(null, null)); }
}
