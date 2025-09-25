package com.scmp.service;

import com.scmp.model.LogEntry;
import com.scmp.model.LogEntry.LogLevel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 日志服务类，提供全局日志记录和管理功能
 */
public class LogService {
    private static final LogService INSTANCE = new LogService();
    private final List<LogEntry> logs = new CopyOnWriteArrayList<>();
    private final List<Consumer<LogEntry>> logListeners = new CopyOnWriteArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 私有构造函数，确保单例
    private LogService() {}

    /**
     * 获取LogService实例
     */
    public static LogService getInstance() {
        return INSTANCE;
    }

    /**
     * 记录INFO级别日志
     * @param content 日志内容
     * @param contractNumber 合同号
     */
    public static void info(String content, String contractNumber) {
        LogEntry logEntry = createLogEntry(LogLevel.INFO, content, contractNumber);
        getInstance().addLog(logEntry);
    }

    /**
     * 记录ERROR级别日志
     * @param content 日志内容
     * @param contractNumber 合同号
     */
    public static void error(String content, String contractNumber) {
        LogEntry logEntry = createLogEntry(LogLevel.ERROR, content, contractNumber);
        getInstance().addLog(logEntry);
    }

    /**
     * 记录DEBUG级别日志
     * @param content 日志内容
     * @param contractNumber 合同号
     */
    public static void debug(String content, String contractNumber) {
        LogEntry logEntry = createLogEntry(LogLevel.INFO, content, contractNumber);
        getInstance().addLog(logEntry);
    }

    /**
     * 记录WARN级别日志
     * @param content 日志内容
     * @param contractNumber 合同号
     */
    public static void warn(String content, String contractNumber) {
        LogEntry logEntry = createLogEntry(LogLevel.WARNING, content, contractNumber);
        getInstance().addLog(logEntry);
    }

    /**
     * 创建日志条目
     */
    private static LogEntry createLogEntry(LogLevel level, String content, String contractNumber) {
        LogEntry logEntry = new LogEntry();
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setLevel(level);
        logEntry.setContent(content);
        logEntry.setContractNumber(contractNumber);
        return logEntry;
    }

    /**
     * 添加日志到列表并通知监听器
     */
    private void addLog(LogEntry logEntry) {
        logs.add(logEntry);
        // 通知所有监听器
        for (Consumer<LogEntry> listener : logListeners) {
            listener.accept(logEntry);
        }
    }

    /**
     * 获取所有日志
     */
    public List<LogEntry> getAllLogs() {
        return new ArrayList<>(logs);
    }

    /**
     * 清除所有日志
     */
    public void clearLogs() {
        logs.clear();
    }

    /**
     * 添加日志监听器
     * @param listener 日志监听器
     */
    public void addLogListener(Consumer<LogEntry> listener) {
        logListeners.add(listener);
    }

    /**
     * 移除日志监听器
     * @param listener 日志监听器
     */
    public void removeLogListener(Consumer<LogEntry> listener) {
        logListeners.remove(listener);
    }
}