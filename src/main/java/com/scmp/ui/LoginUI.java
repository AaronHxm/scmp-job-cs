package com.scmp.ui;

import com.scmp.model.User;
import com.scmp.service.ApiService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginUI extends Application {
    
    private ApiService apiService;
    private User currentUser = new User();
    
    @Override
    public void init() {
        // 手动创建服务实例（替代Spring依赖注入）
        apiService = new ApiService();
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("登录系统");
        
        // 创建主面板
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // 创建登录方式选择器
        ToggleGroup loginTypeGroup = new ToggleGroup();
        RadioButton credentialLogin = new RadioButton("账号密码登录");
        RadioButton tokenLogin = new RadioButton("Token登录");
        credentialLogin.setToggleGroup(loginTypeGroup);
        tokenLogin.setToggleGroup(loginTypeGroup);
        credentialLogin.setSelected(true);
        
        HBox loginTypeBox = new HBox(10, credentialLogin, tokenLogin);
        loginTypeBox.setPadding(new Insets(10, 0, 10, 0));
        
        // 创建表单字段
        TextField usernameField = new TextField();
        usernameField.setPromptText("用户名");
        usernameField.setPrefWidth(200);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码");
        passwordField.setPrefWidth(200);
        
        TextField tokenField = new TextField();
        tokenField.setPromptText("输入Token");
        tokenField.setPrefWidth(200);
        tokenField.setVisible(false);
        
        // 切换登录方式的事件处理
        credentialLogin.setOnAction(e -> {
            usernameField.setVisible(true);
            passwordField.setVisible(true);
            tokenField.setVisible(false);
        });
        
        tokenLogin.setOnAction(e -> {
            usernameField.setVisible(false);
            passwordField.setVisible(false);
            tokenField.setVisible(true);
        });
        
        // 创建登录按钮
        Button loginButton = new Button("登录");
        loginButton.setOnAction(e -> {
            if (credentialLogin.isSelected()) {
                // 账号密码登录
                currentUser.setUsername(usernameField.getText());
                currentUser.setPassword(passwordField.getText());
                boolean success = apiService.loginWithCredentials(currentUser);
                if (success) {
                    currentUser.setLoggedIn(true);
                    showMainUI(primaryStage);
                } else {
                    showAlert("登录失败", "用户名或密码错误");
                }
            } else {
                // Token登录
                String token = tokenField.getText();
                boolean success = apiService.loginWithToken(token);
                if (success) {
                    currentUser.setToken(token);
                    currentUser.setLoggedIn(true);
                    showMainUI(primaryStage);
                } else {
                    showAlert("登录失败", "Token无效");
                }
            }
        });
        
        // 创建表单面板
        VBox formBox = new VBox(10);
        formBox.getChildren().addAll(
            new Label("请选择登录方式："),
            loginTypeBox,
            usernameField,
            passwordField,
            tokenField,
            loginButton
        );
        
        // 设置居中布局
        root.setCenter(formBox);
        
        // 设置场景
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // 显示主界面
        private void showMainUI(Stage primaryStage) {
            // 使用已创建的apiService实例
            MainUI mainUI = new MainUI(apiService, currentUser);
            try {
                mainUI.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("错误", "无法加载主界面");
            }
        }
    
    // 显示警告对话框
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void stop() {
        // 清理资源
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}