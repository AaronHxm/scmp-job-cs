package com.scmp.model;

import java.util.Date;

/**
 * 合同信息类，包含合同的详细字段信息
 */
public class ContractInfo {
    private Long syskey;                        // 系统主键
    private String contractNo;                  // 合同编号
    private String accountType;                // 账户类型
    private String customerName;               // 客户姓名
    private String provinceName;               // 省份名称
    private String cityName;                   // 城市名称
    private String dlrSimpleName;              // 经销商简称
    private String dlrProvinceName;            // 经销商所在省
    private String areaName;                   // 区域名称
    private String brand;                      // 品牌
    private String assetDsc;                   // 资产描述
    private String businessType;               // 业务类型
    private Integer totalODDays;                // 总逾期天数
    private Integer delinquencyTerm;           // 逾期期数
    private String odrentalAmt;            // 逾期租金金额
    private String totalODAmt;             // 总逾期金额
    private Integer dcaTranTimes;              // 催收流转次数
    private Integer relatedContractNum;        // 关联合同数量
    private String loseContactFlag;            // 失联标志
    private String dcaType;                    // 催收类型
    private String customerStatus;             // 客户状态
    private String carStatus;                  // 车辆状态
    private String prevCompName;               // 前手公司名称
    private String compId;                     // 公司ID
    private String compName;                   // 公司名称
    private Date dcaPlanAllocDate;             // 计划分配日期
    private Date dcaRealAllocDate;             // 实际分配日期
    private Date dcaPlanBlDate;                // 计划BL日期
    private Date dcaRealBlDate;                // 实际BL日期
    private String cpyWriteoffFlag;            // 公司核销标志
    private Integer cpyTotalODDays;            // 公司总逾期天数
    private String cpyTotalODAmt;          // 公司总逾期金额
    private String writeoffFlag;               // 核销标志
    private String dcareserveflg;              // 催收预留标志
    private Date dcareservedate;               // 催收预留日期
    private String cpyHaveHomeRecord;          // 公司有无户籍记录
    private Date cpyHomeRecoedDate;            // 公司户籍记录日期
    private String sendtodcaId;                // 发送催收ID
    private String contractRate;           // 合同利率
    private String reportStatus;               // 报告状态
    private String currSector;                 // 当前阶段
    private String currSubsector;              // 当前子阶段
    private String currStates;                // 当前状态
    private String dcaStatus;                  // 催收状态
    private String disValid;                   // 是否有效
    private String putsigleUser;               // 推送用户
    private String cpyCurrSubsector;           // 公司当前子阶段
    private String cpyCurrsector;              // 公司当前阶段
    private String dispatchStatus;             // 派单状态
    private String paymentMsgStatus;           // 付款消息状态
    private String cpyTotalDebtAmountAll;  // 公司总债务金额
    private String receiveStatus;              // 接收状态
    private String caseTaskId;                 // 案件任务ID
    private String relativeContractNos;        // 关联合同编号
    private Integer accountAge;                // 账龄
    private String isPayoff;                   // 是否已结清
    private String isPayoffDesc;               // 结清描述
    private String outsrcIsPayoff;             // 委外是否结清
    private Date balanceDate;                  // 结算日期
    private String financialProduct;           // 金融产品
    private String isAppoint;                  // 是否指定
    private String freezeFlag;                 // 冻结标志
    private String provinceCodeHj;             // 户籍省份代码
    private String provinceNameHj;             // 户籍省份名称
    private String leftMoney;              // 剩余金额

    // 以下为描述字段（Str结尾）
    private String accountTypeStr;             // 账户类型描述
    private String loseContactFlagStr;         // 失联标志描述
    private String dcaTypeStr;                 // 催收类型描述
    private String customerStatusStr;          // 客户状态描述
    private String carStatusStr;               // 车辆状态描述
    private String cpyWriteoffFlagStr;         // 公司核销标志描述
    private String writeoffFlagStr;            // 核销标志描述
    private String dcareserveflgStr;           // 催收预留标志描述
    private String cpyHaveHomeRecordStr;       // 公司有无户籍记录描述
    private String reportStatusStr;            // 报告状态描述
    private String currSectorDesc;             // 当前阶段描述
    private String currStatesDesc;             // 当前状态描述
    private String dcaStatusDesc;              // 催收状态描述
    private String dispatchStatusDesc;         // 派单状态描述
    private String isAppointDesc;              // 是否指定描述

    private String userId;

    /**
     * 历史聊天信息
     */
    private String historyRemarks = "暂无客服联系记录";

    private boolean selected;
    public String getHistoryRemarks() {
        return historyRemarks;
    }

    public void setHistoryRemarks(String historyRemarks) {
        this.historyRemarks = historyRemarks;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // getter和setter方法
    public Long getSyskey() { return syskey; }
    public void setSyskey(Long syskey) { this.syskey = syskey; }

    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getDlrSimpleName() { return dlrSimpleName; }
    public void setDlrSimpleName(String dlrSimpleName) { this.dlrSimpleName = dlrSimpleName; }

    public String getDlrProvinceName() { return dlrProvinceName; }
    public void setDlrProvinceName(String dlrProvinceName) { this.dlrProvinceName = dlrProvinceName; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getAssetDsc() { return assetDsc; }
    public void setAssetDsc(String assetDsc) { this.assetDsc = assetDsc; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public Integer getTotalODDays() { return totalODDays; }
    public void setTotalODDays(Integer totalODDays) { this.totalODDays = totalODDays; }

    public Integer getDelinquencyTerm() { return delinquencyTerm; }
    public void setDelinquencyTerm(Integer delinquencyTerm) { this.delinquencyTerm = delinquencyTerm; }

    public String getOdrentalAmt() { return odrentalAmt; }
    public void setOdrentalAmt(String odrentalAmt) { this.odrentalAmt = odrentalAmt; }

    public String getTotalODAmt() { return totalODAmt; }
    public void setTotalODAmt(String totalODAmt) { this.totalODAmt = totalODAmt; }

    public Integer getDcaTranTimes() { return dcaTranTimes; }
    public void setDcaTranTimes(Integer dcaTranTimes) { this.dcaTranTimes = dcaTranTimes; }

    public Integer getRelatedContractNum() { return relatedContractNum; }
    public void setRelatedContractNum(Integer relatedContractNum) { this.relatedContractNum = relatedContractNum; }

    public String getLoseContactFlag() { return loseContactFlag; }
    public void setLoseContactFlag(String loseContactFlag) { this.loseContactFlag = loseContactFlag; }

    public String getDcaType() { return dcaType; }
    public void setDcaType(String dcaType) { this.dcaType = dcaType; }

    public String getCustomerStatus() { return customerStatus; }
    public void setCustomerStatus(String customerStatus) { this.customerStatus = customerStatus; }

    public String getCarStatus() { return carStatus; }
    public void setCarStatus(String carStatus) { this.carStatus = carStatus; }

    public String getPrevCompName() { return prevCompName; }
    public void setPrevCompName(String prevCompName) { this.prevCompName = prevCompName; }

    public String getCompId() { return compId; }
    public void setCompId(String compId) { this.compId = compId; }

    public String getCompName() { return compName; }
    public void setCompName(String compName) { this.compName = compName; }

    public Date getDcaPlanAllocDate() { return dcaPlanAllocDate; }
    public void setDcaPlanAllocDate(Date dcaPlanAllocDate) { this.dcaPlanAllocDate = dcaPlanAllocDate; }

    public Date getDcaRealAllocDate() { return dcaRealAllocDate; }
    public void setDcaRealAllocDate(Date dcaRealAllocDate) { this.dcaRealAllocDate = dcaRealAllocDate; }

    public Date getDcaPlanBlDate() { return dcaPlanBlDate; }
    public void setDcaPlanBlDate(Date dcaPlanBlDate) { this.dcaPlanBlDate = dcaPlanBlDate; }

    public Date getDcaRealBlDate() { return dcaRealBlDate; }
    public void setDcaRealBlDate(Date dcaRealBlDate) { this.dcaRealBlDate = dcaRealBlDate; }

    public String getCpyWriteoffFlag() { return cpyWriteoffFlag; }
    public void setCpyWriteoffFlag(String cpyWriteoffFlag) { this.cpyWriteoffFlag = cpyWriteoffFlag; }

    public Integer getCpyTotalODDays() { return cpyTotalODDays; }
    public void setCpyTotalODDays(Integer cpyTotalODDays) { this.cpyTotalODDays = cpyTotalODDays; }

    public String getCpyTotalODAmt() { return cpyTotalODAmt; }
    public void setCpyTotalODAmt(String cpyTotalODAmt) { this.cpyTotalODAmt = cpyTotalODAmt; }

    public String getWriteoffFlag() { return writeoffFlag; }
    public void setWriteoffFlag(String writeoffFlag) { this.writeoffFlag = writeoffFlag; }

    public String getDcareserveflg() { return dcareserveflg; }
    public void setDcareserveflg(String dcareserveflg) { this.dcareserveflg = dcareserveflg; }

    public Date getDcareservedate() { return dcareservedate; }
    public void setDcareservedate(Date dcareservedate) { this.dcareservedate = dcareservedate; }

    public String getCpyHaveHomeRecord() { return cpyHaveHomeRecord; }
    public void setCpyHaveHomeRecord(String cpyHaveHomeRecord) { this.cpyHaveHomeRecord = cpyHaveHomeRecord; }

    public Date getCpyHomeRecoedDate() { return cpyHomeRecoedDate; }
    public void setCpyHomeRecoedDate(Date cpyHomeRecoedDate) { this.cpyHomeRecoedDate = cpyHomeRecoedDate; }

    public String getSendtodcaId() { return sendtodcaId; }
    public void setSendtodcaId(String sendtodcaId) { this.sendtodcaId = sendtodcaId; }

    public String getContractRate() { return contractRate; }
    public void setContractRate(String contractRate) { this.contractRate = contractRate; }

    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }

    public String getCurrSector() { return currSector; }
    public void setCurrSector(String currSector) { this.currSector = currSector; }

    public String getCurrSubsector() { return currSubsector; }
    public void setCurrSubsector(String currSubsector) { this.currSubsector = currSubsector; }

    public String getCurrStates() { return currStates; }
    public void setCurrStates(String currStates) { this.currStates = currStates; }

    public String getDcaStatus() { return dcaStatus; }
    public void setDcaStatus(String dcaStatus) { this.dcaStatus = dcaStatus; }

    public String getDisValid() { return disValid; }
    public void setDisValid(String disValid) { this.disValid = disValid; }

    public String getPutsigleUser() { return putsigleUser; }
    public void setPutsigleUser(String putsigleUser) { this.putsigleUser = putsigleUser; }

    public String getCpyCurrSubsector() { return cpyCurrSubsector; }
    public void setCpyCurrSubsector(String cpyCurrSubsector) { this.cpyCurrSubsector = cpyCurrSubsector; }

    public String getCpyCurrsector() { return cpyCurrsector; }
    public void setCpyCurrsector(String cpyCurrsector) { this.cpyCurrsector = cpyCurrsector; }

    public String getDispatchStatus() { return dispatchStatus; }
    public void setDispatchStatus(String dispatchStatus) { this.dispatchStatus = dispatchStatus; }

    public String getPaymentMsgStatus() { return paymentMsgStatus; }
    public void setPaymentMsgStatus(String paymentMsgStatus) { this.paymentMsgStatus = paymentMsgStatus; }

    public String getCpyTotalDebtAmountAll() { return cpyTotalDebtAmountAll; }
    public void setCpyTotalDebtAmountAll(String cpyTotalDebtAmountAll) { this.cpyTotalDebtAmountAll = cpyTotalDebtAmountAll; }

    public String getReceiveStatus() { return receiveStatus; }
    public void setReceiveStatus(String receiveStatus) { this.receiveStatus = receiveStatus; }

    public String getCaseTaskId() { return caseTaskId; }
    public void setCaseTaskId(String caseTaskId) { this.caseTaskId = caseTaskId; }

    public String getRelativeContractNos() { return relativeContractNos; }
    public void setRelativeContractNos(String relativeContractNos) { this.relativeContractNos = relativeContractNos; }

    public Integer getAccountAge() { return accountAge; }
    public void setAccountAge(Integer accountAge) { this.accountAge = accountAge; }

    public String getIsPayoff() { return isPayoff; }
    public void setIsPayoff(String isPayoff) { this.isPayoff = isPayoff; }

    public String getIsPayoffDesc() { return isPayoffDesc; }
    public void setIsPayoffDesc(String isPayoffDesc) { this.isPayoffDesc = isPayoffDesc; }

    public String getOutsrcIsPayoff() { return outsrcIsPayoff; }
    public void setOutsrcIsPayoff(String outsrcIsPayoff) { this.outsrcIsPayoff = outsrcIsPayoff; }

    public Date getBalanceDate() { return balanceDate; }
    public void setBalanceDate(Date balanceDate) { this.balanceDate = balanceDate; }

    public String getFinancialProduct() { return financialProduct; }
    public void setFinancialProduct(String financialProduct) { this.financialProduct = financialProduct; }

    public String getIsAppoint() { return isAppoint; }
    public void setIsAppoint(String isAppoint) { this.isAppoint = isAppoint; }

    public String getFreezeFlag() { return freezeFlag; }
    public void setFreezeFlag(String freezeFlag) { this.freezeFlag = freezeFlag; }

    public String getProvinceCodeHj() { return provinceCodeHj; }
    public void setProvinceCodeHj(String provinceCodeHj) { this.provinceCodeHj = provinceCodeHj; }

    public String getProvinceNameHj() { return provinceNameHj; }
    public void setProvinceNameHj(String provinceNameHj) { this.provinceNameHj = provinceNameHj; }

    public String getLeftMoney() { return leftMoney; }
    public void setLeftMoney(String leftMoney) { this.leftMoney = leftMoney; }

    // 描述字段的getter和setter
    public String getAccountTypeStr() { return accountTypeStr; }
    public void setAccountTypeStr(String accountTypeStr) { this.accountTypeStr = accountTypeStr; }

    public String getLoseContactFlagStr() { return loseContactFlagStr; }
    public void setLoseContactFlagStr(String loseContactFlagStr) { this.loseContactFlagStr = loseContactFlagStr; }

    public String getDcaTypeStr() { return dcaTypeStr; }
    public void setDcaTypeStr(String dcaTypeStr) { this.dcaTypeStr = dcaTypeStr; }

    public String getCustomerStatusStr() { return customerStatusStr; }
    public void setCustomerStatusStr(String customerStatusStr) { this.customerStatusStr = customerStatusStr; }

    public String getCarStatusStr() { return carStatusStr; }
    public void setCarStatusStr(String carStatusStr) { this.carStatusStr = carStatusStr; }

    public String getCpyWriteoffFlagStr() { return cpyWriteoffFlagStr; }
    public void setCpyWriteoffFlagStr(String cpyWriteoffFlagStr) { this.cpyWriteoffFlagStr = cpyWriteoffFlagStr; }

    public String getWriteoffFlagStr() { return writeoffFlagStr; }
    public void setWriteoffFlagStr(String writeoffFlagStr) { this.writeoffFlagStr = writeoffFlagStr; }

    public String getDcareserveflgStr() { return dcareserveflgStr; }
    public void setDcareserveflgStr(String dcareserveflgStr) { this.dcareserveflgStr = dcareserveflgStr; }

    public String getCpyHaveHomeRecordStr() { return cpyHaveHomeRecordStr; }
    public void setCpyHaveHomeRecordStr(String cpyHaveHomeRecordStr) { this.cpyHaveHomeRecordStr = cpyHaveHomeRecordStr; }

    public String getReportStatusStr() { return reportStatusStr; }
    public void setReportStatusStr(String reportStatusStr) { this.reportStatusStr = reportStatusStr; }

    public String getCurrSectorDesc() { return currSectorDesc; }
    public void setCurrSectorDesc(String currSectorDesc) { this.currSectorDesc = currSectorDesc; }

    public String getCurrStatesDesc() { return currStatesDesc; }
    public void setCurrStatesDesc(String currStatesDesc) { this.currStatesDesc = currStatesDesc; }

    public String getDcaStatusDesc() { return dcaStatusDesc; }
    public void setDcaStatusDesc(String dcaStatusDesc) { this.dcaStatusDesc = dcaStatusDesc; }

    public String getDispatchStatusDesc() { return dispatchStatusDesc; }
    public void setDispatchStatusDesc(String dispatchStatusDesc) { this.dispatchStatusDesc = dispatchStatusDesc; }

    public String getIsAppointDesc() { return isAppointDesc; }
    public void setIsAppointDesc(String isAppointDesc) { this.isAppointDesc = isAppointDesc; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
}