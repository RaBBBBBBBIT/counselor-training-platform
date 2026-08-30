package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.PracticeBatch;

public interface PracticeBatchMapper
{
    public List<PracticeBatch> selectPracticeBatchList(PracticeBatch batch);
    public PracticeBatch selectPracticeBatchById(Long batchId);
    public int insertPracticeBatch(PracticeBatch batch);
    public int updatePracticeBatch(PracticeBatch batch);
    public int deletePracticeBatchById(Long batchId);
}
