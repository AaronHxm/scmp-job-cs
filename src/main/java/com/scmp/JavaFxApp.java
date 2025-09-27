package com.scmp;

import com.scmp.ui.LoginUI;
import javafx.application.Application;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class JavaFxApp {
    public static void main(String[] args) {
        try {
            Application.launch(LoginUI.class, args);
        } catch (Exception e) {
            String logPath = System.getProperty("user.home") + "/app_error.log";
            try (PrintWriter writer = new PrintWriter(new FileWriter(logPath, true))) {
                e.printStackTrace(writer);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.err.println("应用程序启动失败，错误日志已保存至: " + logPath);
        }
    }
}