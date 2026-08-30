package com.ruoyi.system.domain;

public class ScoreVO
{
    private Long userId;
    private String userName;
    private Long deptId;
    private String deptName;
    private Long competitionId;
    private Long batchId;
    private Integer objectiveScore;
    private Integer subjectiveScore;
    private Integer totalScore;
    private Integer rank;
    private String scoreStatus;

    public Long getUserId() { return userId; } public void setUserId(Long v) { this.userId = v; }
    public String getUserName() { return userName; } public void setUserName(String v) { this.userName = v; }
    public Long getDeptId() { return deptId; } public void setDeptId(Long v) { this.deptId = v; }
    public String getDeptName() { return deptName; } public void setDeptName(String v) { this.deptName = v; }
    public Long getCompetitionId() { return competitionId; } public void setCompetitionId(Long v) { this.competitionId = v; }
    public Long getBatchId() { return batchId; } public void setBatchId(Long v) { this.batchId = v; }
    public Integer getObjectiveScore() { return objectiveScore; } public void setObjectiveScore(Integer v) { this.objectiveScore = v; }
    public Integer getSubjectiveScore() { return subjectiveScore; } public void setSubjectiveScore(Integer v) { this.subjectiveScore = v; }
    public Integer getTotalScore() { return totalScore; } public void setTotalScore(Integer v) { this.totalScore = v; }
    public Integer getRank() { return rank; } public void setRank(Integer v) { this.rank = v; }
    public String getScoreStatus() { return scoreStatus; } public void setScoreStatus(String v) { this.scoreStatus = v; }
}
