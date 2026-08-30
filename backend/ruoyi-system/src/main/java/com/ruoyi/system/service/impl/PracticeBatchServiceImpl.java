package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.PracticeBatch;
import com.ruoyi.system.domain.PracticeParticipant;
import com.ruoyi.system.mapper.PracticeBatchMapper;
import com.ruoyi.system.mapper.PracticeParticipantMapper;
import com.ruoyi.system.service.IPracticeBatchService;

@Service
public class PracticeBatchServiceImpl implements IPracticeBatchService
{
    @Autowired private PracticeBatchMapper batchMapper;
    @Autowired private PracticeParticipantMapper participantMapper;

    @Override public List<PracticeBatch> selectPracticeBatchList(PracticeBatch b) { return batchMapper.selectPracticeBatchList(b); }
    @Override public PracticeBatch selectPracticeBatchById(Long id)
    {
        PracticeBatch b = batchMapper.selectPracticeBatchById(id);
        if (b != null)
        {
            b.setParticipantIds(participantMapper.selectByBatchId(id).stream().map(PracticeParticipant::getUserId).collect(java.util.stream.Collectors.toList()));
        }
        return b;
    }
    @Override @Transactional
    public int insertPracticeBatch(PracticeBatch b)
    {
        if (b.getStartTime() == null || b.getEndTime() == null) { throw new ServiceException("请设置时间范围"); }
        if (b.getEndTime().before(b.getStartTime())) { throw new ServiceException("结束时间不能早于开始时间"); }
        b.setCreateBy(SecurityUtils.getUsername());
        batchMapper.insertPracticeBatch(b);
        if (b.getParticipantIds() != null)
        {
            for (Long uid : b.getParticipantIds())
            {
                PracticeParticipant p = new PracticeParticipant(); p.setBatchId(b.getBatchId()); p.setUserId(uid);
                participantMapper.insertParticipant(p);
            }
        }
        return 1;
    }
    @Override @Transactional public int updatePracticeBatch(PracticeBatch b) { b.setUpdateBy(SecurityUtils.getUsername()); return batchMapper.updatePracticeBatch(b); }
    @Override @Transactional public int deletePracticeBatchById(Long id) { participantMapper.deleteParticipantByBatchId(id); return batchMapper.deletePracticeBatchById(id); }
}
