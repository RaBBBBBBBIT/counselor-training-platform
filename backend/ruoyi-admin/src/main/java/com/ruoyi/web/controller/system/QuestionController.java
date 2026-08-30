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
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.service.IQuestionService;

/**
 * 题目管理
 */
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController extends BaseController
{
    @Autowired
    private IQuestionService questionService;

    @PreAuthorize("@ss.hasPermi('question:list')")
    @GetMapping("/list")
    public TableDataInfo list(Question question)
    {
        startPage();
        List<Question> list = questionService.selectQuestionList(question);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('question:list')")
    @GetMapping("/{questionId}")
    public AjaxResult getInfo(@PathVariable Long questionId)
    {
        return success(questionService.selectQuestionById(questionId));
    }

    @PreAuthorize("@ss.hasPermi('question:add')")
    @Log(title = "题目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Question question)
    {
        return toAjax(questionService.insertQuestion(question));
    }

    @PreAuthorize("@ss.hasPermi('question:edit')")
    @Log(title = "题目管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Question question)
    {
        return toAjax(questionService.updateQuestion(question));
    }

    @PreAuthorize("@ss.hasPermi('question:remove')")
    @Log(title = "题目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{questionId}")
    public AjaxResult remove(@PathVariable Long questionId)
    {
        return toAjax(questionService.deleteQuestionById(questionId));
    }
}
