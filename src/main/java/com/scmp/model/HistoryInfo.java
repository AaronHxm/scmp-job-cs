package com.scmp.model;

import lombok.Data;

import java.util.Date;

@Data
public class HistoryInfo {

    /**
     *
     * 合同号
     */
    private String contractNo;

    private Date callTime;

    private String requireContent;

    /**
     * 联系人
     */
    private String createName;
}
