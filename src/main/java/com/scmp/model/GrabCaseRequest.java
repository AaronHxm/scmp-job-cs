package com.scmp.model;

/**
 * 抢单请求实体类
 */
public class GrabCaseRequest {
    private Long syskey;
    private String contractNo;
    
    public Long getSyskey() {
        return syskey;
    }
    
    public void setSyskey(Long syskey) {
        this.syskey = syskey;
    }
    
    public String getContractNo() {
        return contractNo;
    }
    
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }
}