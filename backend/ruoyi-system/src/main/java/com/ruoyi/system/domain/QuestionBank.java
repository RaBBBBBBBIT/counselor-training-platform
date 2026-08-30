package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 题库对象 question_bank
 */
public class QuestionBank extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long bankId;
    private String bankName;
    private Long ownerId;
    private String shared;
    private String ownerName;

    public Long getBankId() { return bankId; }
    public void setBankId(Long bankId) { this.bankId = bankId; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getShared() { return shared; }
    public void setShared(String shared) { this.shared = shared; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
