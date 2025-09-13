package com.scmp.model;

import java.time.LocalDateTime;

public class LogEntry {
    private LocalDateTime timestamp;
    private String content;
    private String contractNumber;
    private LogLevel level;
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getContractNumber() {
        return contractNumber;
    }
    
    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }
    
    public LogLevel getLevel() {
        return level;
    }
    
    public void setLevel(LogLevel level) {
        this.level = level;
    }
    
    public enum LogLevel {
        INFO, SUCCESS, ERROR, WARNING
    }
}