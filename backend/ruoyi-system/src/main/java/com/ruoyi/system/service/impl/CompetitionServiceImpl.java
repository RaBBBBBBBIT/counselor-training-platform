package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.Competition;
import com.ruoyi.system.domain.CompetitionParticipant;
import com.ruoyi.system.domain.CompetitionStage;
import com.ruoyi.system.mapper.CompetitionMapper;
import com.ruoyi.system.mapper.CompetitionParticipantMapper;
import com.ruoyi.system.mapper.CompetitionStageMapper;
import com.ruoyi.system.service.ICompetitionService;

@Service
public class CompetitionServiceImpl implements ICompetitionService
{
    @Autowired private CompetitionMapper competitionMapper;
    @Autowired private CompetitionStageMapper stageMapper;
    @Autowired private CompetitionParticipantMapper participantMapper;

    @Override public List<Competition> selectCompetitionList(Competition c) { return competitionMapper.selectCompetitionList(c); }

    @Override
    public Competition selectCompetitionById(Long id)
    {
        Competition c = competitionMapper.selectCompetitionById(id);
        if (c != null)
        {
            c.setStages(stageMapper.selectStageListByCompetitionId(id));
            c.setParticipantIds(participantMapper.selectParticipantListByCompetitionId(id).stream().map(CompetitionParticipant::getUserId).collect(java.util.stream.Collectors.toList()));
        }
        return c;
    }

    @Override
    @Transactional
    public int insertCompetition(Competition c)
    {
        c.setOrganizerId(SecurityUtils.getUserId());
        c.setStatus("NOT_STARTED");
        c.setCreateBy(SecurityUtils.getUsername());
        competitionMapper.insertCompetition(c);
        Long id = c.getCompetitionId();
        List<CompetitionStage> stages = c.getStages();
        if (stages != null && !stages.isEmpty())
        {
            int order = 1;
            for (CompetitionStage st : stages)
            {
                if (st.getOrderNo() == null) { st.setOrderNo(order); }
                st.setCompetitionId(id);
                stageMapper.insertStage(st);
                order++;
            }
        }
        if (c.getParticipantIds() != null && !c.getParticipantIds().isEmpty())
        {
            for (Long uid : c.getParticipantIds())
            {
                CompetitionParticipant p = new CompetitionParticipant();
                p.setCompetitionId(id); p.setUserId(uid);
                participantMapper.insertParticipant(p);
            }
        }
        return 1;
    }

    @Override @Transactional public int updateCompetition(Competition c) { c.setUpdateBy(SecurityUtils.getUsername()); return competitionMapper.updateCompetition(c); }

    @Override @Transactional
    public int changeStatus(Long id, String status)
    {
        Competition c = competitionMapper.selectCompetitionById(id);
        if (c == null) { throw new ServiceException("比赛不存在"); }
        String cur = c.getStatus();
        boolean ok = ("NOT_STARTED".equals(cur) && "IN_PROGRESS".equals(status))
                || ("IN_PROGRESS".equals(cur) && "FINISHED".equals(status));
        if (!ok) { throw new ServiceException("比赛状态不允许从 " + cur + " 流转到 " + status); }
        return competitionMapper.updateCompetitionStatus(id, status);
    }

    @Override @Transactional public int addStage(CompetitionStage st) { st.setCompetitionId(st.getCompetitionId()); return stageMapper.insertStage(st); }
    @Override @Transactional public int updateStage(CompetitionStage st) { return stageMapper.updateStage(st); }
    @Override @Transactional public int deleteStage(Long stageId) { return stageMapper.deleteStageById(stageId); }

    @Override @Transactional
    public int addParticipant(Long compId, List<Long> userIds)
    {
        if (userIds == null || userIds.isEmpty()) { throw new ServiceException("请选择参赛人员"); }
        for (Long uid : userIds)
        {
            CompetitionParticipant p = new CompetitionParticipant();
            p.setCompetitionId(compId); p.setUserId(uid);
            participantMapper.insertParticipant(p);
        }
        return 1;
    }

    @Override @Transactional public int removeParticipant(Long compId, Long uid) { return participantMapper.deleteParticipant(compId, uid); }
    @Override public List<CompetitionParticipant> selectParticipants(Long compId) { return participantMapper.selectParticipantListByCompetitionId(compId); }

    @Override @Transactional
    public int deleteCompetitionById(Long id)
    {
        stageMapper.deleteStageByCompetitionId(id);
        participantMapper.deleteParticipantByCompetitionId(id);
        return competitionMapper.deleteCompetitionById(id);
    }
}
