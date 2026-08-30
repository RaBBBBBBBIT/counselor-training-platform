package com.ruoyi.web.controller.system;

import java.util.Map;
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
import com.ruoyi.system.service.ILiveMatchService;
import com.ruoyi.system.service.IQuestionService;

@RestController
@RequestMapping("/api/v1/live-matches")
public class LiveMatchController extends BaseController
{
    @Autowired private ILiveMatchService liveMatchService;
    @Autowired private IQuestionService questionService;

    @PreAuthorize("@ss.hasPermi('live:draw')")
    @GetMapping("/xueqing/draw")
    public AjaxResult xueqingDraw() { return success(liveMatchService.drawStudent()); }

    @PreAuthorize("@ss.hasPermi('live:answer')")
    @GetMapping("/xueqing/{studentId}")
    public AjaxResult xueqingAnswer(@PathVariable Long studentId) { return success(liveMatchService.studentAnswer(studentId)); }

    @PreAuthorize("@ss.hasPermi('live:draw')")
    @PostMapping("/case-analysis/draw")
    public AjaxResult caseDraw(@RequestBody Map<String, String> body)
    {
        return success(liveMatchService.drawCaseAnalysis(body.get("groupNo"), body.get("serialNo")));
    }

    @PreAuthorize("@ss.hasPermi('live:draw')")
    @PostMapping("/talk/draw")
    public AjaxResult talkDraw() { return success(liveMatchService.drawTalk()); }

    @PreAuthorize("@ss.hasPermi('live:list')")
    @GetMapping("/questions/{questionId}")
    public AjaxResult question(@PathVariable Long questionId) { return success(questionService.selectQuestionById(questionId)); }
}
