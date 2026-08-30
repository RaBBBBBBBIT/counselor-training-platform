package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Competition;
import com.ruoyi.system.domain.CompetitionParticipant;
import com.ruoyi.system.domain.CompetitionStage;
import com.ruoyi.system.service.ICompetitionService;

/**
 * 正式比赛管理
 */
@RestController
@RequestMapping("/api/v1/competitions")
public class CompetitionController extends BaseController
{
    @Autowired private ICompetitionService competitionService;

    @PreAuthorize("@ss.hasPermi('competition:list')")
    @GetMapping("/list")
    public TableDataInfo list(Competition competition) { startPage(); return getDataTable(competitionService.selectCompetitionList(competition)); }

    @PreAuthorize("@ss.hasPermi('competition:add')")
    @Log(title = "正式比赛", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Competition competition) { return toAjax(competitionService.insertCompetition(competition)); }

    @PreAuthorize("@ss.hasPermi('competition:list')")
    @GetMapping("/{competitionId}")
    public AjaxResult getInfo(@PathVariable Long competitionId) { return success(competitionService.selectCompetitionById(competitionId)); }

    @PreAuthorize("@ss.hasPermi('competition:edit')")
    @Log(title = "正式比赛", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Competition competition) { return toAjax(competitionService.updateCompetition(competition)); }

    @PreAuthorize("@ss.hasPermi('competition:status')")
    @Log(title = "比赛状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{competitionId}/status")
    public AjaxResult changeStatus(@PathVariable Long competitionId, @RequestBody Competition competition)
    {
        return toAjax(competitionService.changeStatus(competitionId, competition.getStatus()));
    }

    @PreAuthorize("@ss.hasPermi('competition:edit')")
    @Log(title = "新增环节", businessType = BusinessType.INSERT)
    @PostMapping("/stages")
    public AjaxResult addStage(@RequestBody CompetitionStage stage) { return toAjax(competitionService.addStage(stage)); }

    @PreAuthorize("@ss.hasPermi('competition:edit')")
    @Log(title = "修改环节", businessType = BusinessType.UPDATE)
    @PutMapping("/stages")
    public AjaxResult editStage(@RequestBody CompetitionStage stage) { return toAjax(competitionService.updateStage(stage)); }

    @PreAuthorize("@ss.hasPermi('competition:edit')")
    @Log(title = "删除环节", businessType = BusinessType.DELETE)
    @DeleteMapping("/stages/{stageId}")
    public AjaxResult removeStage(@PathVariable Long stageId) { return toAjax(competitionService.deleteStage(stageId)); }

    @PreAuthorize("@ss.hasPermi('competition:edit')")
    @Log(title = "添加参赛人员", businessType = BusinessType.INSERT)
    @PostMapping("/participants")
    public AjaxResult addParticipants(@RequestBody Competition competition)
    {
        return toAjax(competitionService.addParticipant(competition.getCompetitionId(), competition.getParticipantIds()));
    }

    @PreAuthorize("@ss.hasPermi('competition:edit')")
    @Log(title = "移除参赛人员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{competitionId}/participants/{userId}")
    public AjaxResult removeParticipant(@PathVariable Long competitionId, @PathVariable Long userId)
    {
        return toAjax(competitionService.removeParticipant(competitionId, userId));
    }

    @PreAuthorize("@ss.hasPermi('competition:edit')")
    @GetMapping("/{competitionId}/participants")
    public AjaxResult participants(@PathVariable Long competitionId) { return success(competitionService.selectParticipants(competitionId)); }

    @PreAuthorize("@ss.hasPermi('competition:remove')")
    @Log(title = "正式比赛", businessType = BusinessType.DELETE)
    @DeleteMapping("/{competitionId}")
    public AjaxResult remove(@PathVariable Long competitionId) { return toAjax(competitionService.deleteCompetitionById(competitionId)); }
}
