package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Paper;
import com.ruoyi.system.service.IPaperService;

/**
 * 试卷管理
 */
@RestController
@RequestMapping("/api/v1/papers")
public class PaperController extends BaseController
{
    @Autowired
    private IPaperService paperService;

    @PreAuthorize("@ss.hasPermi('paper:list')")
    @GetMapping("/list")
    public TableDataInfo list(Paper paper)
    {
        startPage();
        List<Paper> list = paperService.selectPaperList(paper);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('paper:list')")
    @GetMapping("/{paperId}")
    public AjaxResult getInfo(@PathVariable Long paperId)
    {
        return success(paperService.selectPaperById(paperId));
    }

    @PreAuthorize("@ss.hasPermi('paper:add')")
    @Log(title = "生成随机试卷", businessType = BusinessType.INSERT)
    @PostMapping("/random")
    public AjaxResult random(@RequestBody Paper paper)
    {
        Long id = paperService.generateRandomPaper(paper);
        return success(id);
    }

    @PreAuthorize("@ss.hasPermi('paper:add')")
    @Log(title = "生成固定试卷", businessType = BusinessType.INSERT)
    @PostMapping("/fixed")
    public AjaxResult fixed(@RequestBody Paper paper)
    {
        Long id = paperService.generateFixedPaper(paper);
        return success(id);
    }

    @PreAuthorize("@ss.hasPermi('paper:add')")
    @Log(title = "删除试卷", businessType = BusinessType.DELETE)
    @DeleteMapping("/{paperId}")
    public AjaxResult remove(@PathVariable Long paperId)
    {
        return toAjax(paperService.deletePaperById(paperId));
    }
}
