package com.scmp.service;

import com.scmp.model.LogEntry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogService {
    
    private final List<LogEntry> logs = new CopyOnWriteArrayList<>();
    
    // 添加日志
    public void addLog(String content, String contractNumber, LogEntry.LogLevel level) {
        LogEntry logEntry = new LogEntry();
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setContent(content);
        logEntry.setContractNumber(contractNumber);
        logEntry.setLevel(level);
        logs.add(logEntry);
    }
    
    // 获取所有日志
    public List<LogEntry> getAllLogs() {
        return Collections.unmodifiableList(new ArrayList<>(logs));
    }
    
    // 清空日志
    public void clearLogs() {
        logs.clear();
    }
    
    // 添加信息日志
    public void info(String content, String contractNumber) {
        addLog(content, contractNumber, LogEntry.LogLevel.INFO);
    }
    
    // 添加成功日志
    public void success(String content, String contractNumber) {
        addLog(content, contractNumber, LogEntry.LogLevel.SUCCESS);
    }
    
    // 添加错误日志
    public void error(String content, String contractNumber) {
        addLog(content, contractNumber, LogEntry.LogLevel.ERROR);
    }
    
    // 添加警告日志
    public void warning(String content, String contractNumber) {
        addLog(content, contractNumber, LogEntry.LogLevel.WARNING);
    }
}