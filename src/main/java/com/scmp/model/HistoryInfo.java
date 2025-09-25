package com.scmp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryInfo {

    /**
     *
     * 合同号
     */
    private String contractNo;

    private String callTime;

    private String requireContent;

    /**
     * 联系人
     */
    private String createName;
}
