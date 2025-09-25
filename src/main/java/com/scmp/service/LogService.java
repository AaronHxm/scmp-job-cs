package com.scmp.service;

import com.scmp.model.LogEntry;
import com.scmp.model.LogEntry.LogLevel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private static final String LOG_DIR = "logs";
    private Path currentLogFile;
    private final DateTimeFormatter dirFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'.log'");
    private static final LogService INSTANCE = new LogService();
    private final List<LogEntry> logs = new CopyOnWriteArrayList<>();
    private final List<Consumer<LogEntry>> logListeners = new CopyOnWriteArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 私有构造函数，确保单例
    private LogService() {
        initLogFile();
    }

    /**
     * 初始化日志文件
     */
    private synchronized void initLogFile() {
        try {
            // 获取用户主目录
            String userHome = System.getProperty("user.home");
            
            // 创建日期格式的目录和文件名
            LocalDateTime now = LocalDateTime.now();
            String dateDir = now.format(dirFormatter);
            String dateFile = now.format(fileFormatter);
            
            // 构建完整路径 logs/年月日/年月日.log
            Path logDir = Paths.get(userHome, LOG_DIR, dateDir);
            Files.createDirectories(logDir);
            
            currentLogFile = logDir.resolve(dateFile);
            if (!Files.exists(currentLogFile)) {
                Files.createFile(currentLogFile);
            }
        } catch (IOException e) {
            System.err.println("初始化日志文件失败: " + e.getMessage());
        }
    }

    /**
     * 写入日志到文件
     */
    private synchronized void writeToFile(LogEntry logEntry) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String dateDir = now.format(dirFormatter);
            
            // 检查是否需要创建新文件（跨天）
            if (!currentLogFile.getParent().endsWith(dateDir)) {
                initLogFile();
            }
            
            // 格式化日志内容
            String logLine = String.format("%s [%s] %s: %s%n",
                logEntry.getTimestamp().format(formatter),
                logEntry.getLevel(),
                logEntry.getContractNumber(),
                logEntry.getContent());
                
            // 追加写入文件
            Files.write(currentLogFile, logLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("写入日志文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取LogService实例
     */
    public static LogService getInstance() {
        return INSTANCE;
    }

    /**
     * 记录INFO级别日志
     * @param pattern 日志模式，使用{}作为占位符
     * @param contractNumber 合同号
     * @param args 替换占位符的参数
     */
    public static void info(String pattern, String contractNumber, Object... args) {
        String content = formatMessage(pattern, args);
        LogEntry logEntry = createLogEntry(LogLevel.INFO, content, contractNumber);
        getInstance().addLog(logEntry);
    }

    /**
     * 记录ERROR级别日志
     * @param pattern 日志模式，使用{}作为占位符
     * @param contractNumber 合同号
     * @param args 替换占位符的参数
     */
    public static void error(String pattern, String contractNumber, Object... args) {
        String content = formatMessage(pattern, args);
        LogEntry logEntry = createLogEntry(LogLevel.ERROR, content, contractNumber);
        getInstance().addLog(logEntry);
    }

    /**
     * 记录DEBUG级别日志
     * @param pattern 日志模式，使用{}作为占位符
     * @param contractNumber 合同号
     * @param args 替换占位符的参数
     */
    public static void debug(String pattern, String contractNumber, Object... args) {
        String content = formatMessage(pattern, args);
        LogEntry logEntry = createLogEntry(LogLevel.INFO, content, contractNumber);
        getInstance().addLog(logEntry);
    }

    /**
     * 记录WARN级别日志
     * @param pattern 日志模式，使用{}作为占位符
     * @param contractNumber 合同号
     * @param args 替换占位符的参数
     */
    public static void warn(String pattern, String contractNumber, Object... args) {
        String content = formatMessage(pattern, args);
        LogEntry logEntry = createLogEntry(LogLevel.WARNING, content, contractNumber);
        getInstance().addLog(logEntry);
    }

    /**
     * 格式化日志消息，替换{}占位符
     * @param pattern 原始消息模式
     * @param args 参数
     * @return 格式化后的消息
     */
    private static String formatMessage(String pattern, Object... args) {
        if (args == null || args.length == 0) {
            return pattern;
        }
        
        StringBuilder sb = new StringBuilder();
        int argIndex = 0;
        int patternIndex = 0;
        
        while (patternIndex < pattern.length()) {
            int placeholderStart = pattern.indexOf("{}", patternIndex);
            if (placeholderStart == -1) {
                sb.append(pattern.substring(patternIndex));
                break;
            }
            
            sb.append(pattern.substring(patternIndex, placeholderStart));
            if (argIndex < args.length) {
                sb.append(args[argIndex++]);
            } else {
                sb.append("{}");
            }
            patternIndex = placeholderStart + 2;
        }
        
        // 处理多余的参数
        if (argIndex < args.length) {
            sb.append(" [额外参数: ");
            for (int i = argIndex; i < args.length; i++) {
                if (i > argIndex) sb.append(", ");
                sb.append(args[i]);
            }
            sb.append("]");
        }
        
        return sb.toString();
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
        // 写入文件
        writeToFile(logEntry);
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