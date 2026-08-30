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
import com.ruoyi.system.domain.QuestionBank;
import com.ruoyi.system.service.IQuestionBankService;

/**
 * 题库管理
 */
@RestController
@RequestMapping("/api/v1/question-banks")
public class QuestionBankController extends BaseController
{
    @Autowired
    private IQuestionBankService bankService;

    @PreAuthorize("@ss.hasPermi('bank:list')")
    @GetMapping("/list")
    public TableDataInfo list(QuestionBank bank)
    {
        startPage();
        List<QuestionBank> list = bankService.selectQuestionBankList(bank);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('bank:add')")
    @Log(title = "题库管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QuestionBank bank)
    {
        bank.setOwnerId(getUserId());
        return toAjax(bankService.insertQuestionBank(bank));
    }

    @PreAuthorize("@ss.hasPermi('bank:edit')")
    @Log(title = "题库管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QuestionBank bank)
    {
        return toAjax(bankService.updateQuestionBank(bank));
    }

    @PreAuthorize("@ss.hasPermi('bank:remove')")
    @Log(title = "题库管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bankId}")
    public AjaxResult remove(@PathVariable Long bankId)
    {
        return toAjax(bankService.deleteQuestionBankById(bankId));
    }
}
