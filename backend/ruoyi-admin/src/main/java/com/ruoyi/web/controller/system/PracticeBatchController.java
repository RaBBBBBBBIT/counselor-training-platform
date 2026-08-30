package com.ruoyi.web.controller.system;

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
import com.ruoyi.system.domain.PracticeBatch;
import com.ruoyi.system.service.IPracticeBatchService;

@RestController
@RequestMapping("/api/v1/practice-batches")
public class PracticeBatchController extends BaseController
{
    @Autowired private IPracticeBatchService practiceService;

    @PreAuthorize("@ss.hasPermi('practice:list')")
    @GetMapping("/list")
    public TableDataInfo list(PracticeBatch batch) { startPage(); return getDataTable(practiceService.selectPracticeBatchList(batch)); }

    @PreAuthorize("@ss.hasPermi('practice:add')")
    @Log(title = "日常练习", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PracticeBatch batch) { return toAjax(practiceService.insertPracticeBatch(batch)); }

    @PreAuthorize("@ss.hasPermi('practice:edit')")
    @Log(title = "日常练习", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PracticeBatch batch) { return toAjax(practiceService.updatePracticeBatch(batch)); }

    @PreAuthorize("@ss.hasPermi('practice:remove')")
    @Log(title = "日常练习", businessType = BusinessType.DELETE)
    @DeleteMapping("/{batchId}")
    public AjaxResult remove(@PathVariable Long batchId) { return toAjax(practiceService.deletePracticeBatchById(batchId)); }
}
