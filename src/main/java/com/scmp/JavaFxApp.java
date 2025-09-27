package com.scmp;

import com.scmp.ui.LoginUI;
import javafx.application.Application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class JavaFxApp {
    public static void main(String[] args) {
        // 设置日志文件路径
        String logDir = System.getProperty("user.home") + "/scmp_logs";
        new File(logDir).mkdirs();
        System.setProperty("LOG_DIR", logDir);
        
        // 增加JVM内存参数
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.verbose", "true");
        
        try {
            Application.launch(LoginUI.class, args);
        } catch (Exception e) {
            String logPath = logDir + "/app_error.log";
            try (PrintWriter writer = new PrintWriter(new FileWriter(logPath, true))) {
                writer.println("[" + LocalDateTime.now() + "] 应用程序启动失败:");
                e.printStackTrace(writer);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.err.println("应用程序启动失败，错误日志已保存至: " + logPath);
        }
    }
}