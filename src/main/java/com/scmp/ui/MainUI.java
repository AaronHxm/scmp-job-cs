package com.scmp.ui;


import com.scmp.model.ContractInfo;
import com.scmp.model.LogEntry;
import com.scmp.model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainUI extends Application {
    

    private User currentUser;

    private CopyOnWriteArrayList<LogEntry> logs = new CopyOnWriteArrayList<>();
    
    private ObservableList<ContractInfo> contractData = FXCollections.observableArrayList();
    private TextArea logTextArea;
    
    public MainUI( User currentUser) {
        this.currentUser = currentUser;
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("合同抢单系统");
        
        // 创建主面板
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // 创建搜索面板
        VBox searchPanel = createSearchPanel();
        root.setTop(searchPanel);
        
        // 创建合同列表表格
        TableView<ContractInfo> contractTable = createContractTable();
        root.setCenter(contractTable);
        
        // 创建抢单按钮和定时抢单设置
        HBox actionPanel = createActionPanel();
        root.setBottom(actionPanel);
        
        // 创建日志区域
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setPrefHeight(200);
        
        // 创建分割面板，放置合同列表和日志
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        splitPane.getItems().addAll(contractTable, new VBox(new Label("抢单日志"), logTextArea));
        root.setCenter(splitPane);
        
        // 设置场景，增大宽度以适应字母选择区域
        Scene scene = new Scene(root, 1200, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // 启动日志刷新线程
        startLogRefreshThread();
    }
    
    // 创建搜索面板
    private VBox createSearchPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0;");

        HBox searchBox = new HBox(10);

        // 逾期天数输入框
        TextField overdueDaysField = new TextField("360");
        overdueDaysField.setPrefWidth(100);

        // 创建字母选择面板（不使用滚动条，直接适应页面宽度）
        HBox letterBox = new HBox(5);
        letterBox.setPadding(new Insets(0, 0, 0, 0));

        // 存储选中的字母
        ObservableList<String> selectedLetters = FXCollections.observableArrayList();

        // 创建26个字母复选框，放在一行
        for (char c = 'A'; c <= 'Z'; c++) {
            final String letter = String.valueOf(c);
            CheckBox checkBox = new CheckBox(letter);
            checkBox.setPrefWidth(30);

            // 添加选中事件
            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()) {
                    selectedLetters.add(letter);
                } else {
                    selectedLetters.remove(letter);
                }
            });

            letterBox.getChildren().add(checkBox);
        }

        // 查询按钮 - 增大尺寸
        Button queryButton = new Button("查询");
        queryButton.setPrefWidth(80);
        queryButton.setStyle("-fx-font-size: 14px;");
        queryButton.setOnAction(e -> {
            try {
                // 使用模拟数据查询合同
                if (currentUser != null && currentUser.getToken() != null) {
                    // 模拟API查询，返回一些示例数据
                    List<ContractInfo> allContracts = generateMockContracts();
                    
                    contractData.setAll(allContracts);
                    logInfo("查询合同成功，获取到 " + allContracts.size() + " 条记录", "-");
                } else {
                    showAlert("错误", "用户未登录或token为空");
                }
            } catch (Exception ex) {
                logError("查询合同失败: " + ex.getMessage(), "-");
                showAlert("错误", "查询合同失败: " + ex.getMessage());
            }
        });

        searchBox.getChildren().addAll(
            new Label("逾期天数小于："),
            overdueDaysField,
            queryButton
        );

        panel.getChildren().addAll(
            new Label("搜索条件"),
            searchBox,
            new Label("选择字母（可多选）："),
            letterBox
        );

        return panel;
    }
    
    // 创建合同表格
    private TableView<ContractInfo> createContractTable() {
        TableView<ContractInfo> table = new TableView<>();
        table.setItems(contractData);
        
        // 添加全选功能
        CheckBox checkBox = new CheckBox();
        
        // 勾选列
        TableColumn<ContractInfo, Boolean> selectColumn = new TableColumn<>("选择");
        selectColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isSelected()));
        selectColumn.setCellFactory((TableColumn<ContractInfo, Boolean> param) -> {
            CheckBoxTableCell<ContractInfo, Boolean> cell = new CheckBoxTableCell<>();
            cell.setOnMouseClicked(event -> {
                if (cell.getIndex() >= 0 && cell.getIndex() < contractData.size()) {
                    ContractInfo contract = contractData.get(cell.getIndex());
                    contract.setSelected(!contract.isSelected());
                    updateSelectAllState(checkBox, table);
                }
            });
            return cell;
        });
        selectColumn.setEditable(true);
        selectColumn.setPrefWidth(60);
        checkBox.setPadding(new Insets(0, 0, 0, 25)); // 调整位置使其居中
        
        // 全选功能实现
        checkBox.setOnAction(event -> {
            boolean isSelected = checkBox.isSelected();
            for (ContractInfo contract : contractData) {
                contract.setSelected(isSelected);
            }
        });
        
        // 将复选框设置为表头
        selectColumn.setGraphic(checkBox);
        
        // 监听数据变化，更新全选状态
        contractData.addListener((javafx.collections.ListChangeListener<ContractInfo>) c -> {
            while (c.next()) {
                updateSelectAllState(checkBox, table);
            }
        });
        
        // 监听表格选择变化，更新全选状态
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateSelectAllState(checkBox, table);
        });
        
        // 合同号列
        TableColumn<ContractInfo, String> contractNumberColumn = new TableColumn<>("合同号");
        contractNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContractNo()));
        contractNumberColumn.setPrefWidth(150);
        
        // 逾期天数列
        TableColumn<ContractInfo, Integer> overdueDaysColumn = new TableColumn<>("逾期天数");
        overdueDaysColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalODDays()).asObject());
        overdueDaysColumn.setPrefWidth(100);
        
        // 客户姓名列
        TableColumn<ContractInfo, String> customerNameColumn = new TableColumn<>("姓名");
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        customerNameColumn.setPrefWidth(100);
        
        table.getColumns().addAll(selectColumn, contractNumberColumn, overdueDaysColumn, customerNameColumn);
        table.setEditable(true);
        
        return table;
    }
    
    // 倒计时显示标签
    private Label countdownLabel;
    // 当前倒计时任务
    private Thread countdownThread;
    // 存储定时任务信息
    private String currentTaskId;
    private LocalDateTime currentGrabTime;
    
    // 创建操作面板
    private HBox createActionPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0;");

        // 立即抢单按钮 - 增大尺寸
        Button grabNowButton = new Button("立即抢单");
        grabNowButton.setPrefWidth(100);
        grabNowButton.setStyle("-fx-font-size: 14px;");
        grabNowButton.setOnAction(e -> {
            List<ContractInfo> selectedContracts = getSelectedContracts();
            if (selectedContracts.isEmpty()) {
                showAlert("提示", "请先选择要抢的合同");
                return;
            }

            // 执行抢单操作
            for (ContractInfo contract : selectedContracts) {
                try {
                    if (currentUser != null && currentUser.getToken() != null) {
                        // 模拟抢单操作，随机成功或失败
                        boolean success = Math.random() > 0.3; // 70%的成功率
                        if (success) {
                            logSuccess("抢单成功", contract.getContractNo());
                        } else {
                            logError("抢单失败", contract.getContractNo());
                        }
                    } else {
                        showAlert("错误", "用户未登录或token为空");
                    }
                } catch (Exception ex) {
                    logError("抢单异常: " + ex.getMessage(), contract.getContractNo());
                }
            }
        });

        // 定时抢单按钮和时间选择器
        Button scheduleGrabButton = new Button("定时抢单");
        scheduleGrabButton.setPrefWidth(100);
        scheduleGrabButton.setStyle("-fx-font-size: 14px;");

        // 创建时间选择器（小时、分钟、秒）
        ComboBox<Integer> hourComboBox = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            hourComboBox.getItems().add(i);
        }
        hourComboBox.setValue(LocalDateTime.now().getHour());
        hourComboBox.setPrefWidth(60);

        ComboBox<Integer> minuteComboBox = new ComboBox<>();
        for (int i = 0; i < 60; i++) {
            minuteComboBox.getItems().add(i);
        }
        minuteComboBox.setValue(LocalDateTime.now().getMinute());
        minuteComboBox.setPrefWidth(60);
        
        // 修复分钟选择功能
        minuteComboBox.setOnAction(event -> {
            // 确保值被正确设置
            if (minuteComboBox.getValue() == null) {
                minuteComboBox.setValue(LocalDateTime.now().getMinute());
            }
        });

        ComboBox<Integer> secondComboBox = new ComboBox<>();
        for (int i = 0; i < 60; i++) {
            secondComboBox.getItems().add(i);
        }
        secondComboBox.setValue(LocalDateTime.now().getSecond());
        secondComboBox.setPrefWidth(60);

        // 创建倒计时标签
        countdownLabel = new Label("无定时任务");
        countdownLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        scheduleGrabButton.setOnAction(e -> {
            List<ContractInfo> selectedContracts = getSelectedContracts();
            if (selectedContracts.isEmpty()) {
                showAlert("提示", "请先选择要抢的合同");
                return;
            }

            try {
                // 获取选择的时间
                int hour = hourComboBox.getValue();
                int minute = minuteComboBox.getValue();
                int second = secondComboBox.getValue();

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime grabTime = now.withHour(hour).withMinute(minute).withSecond(second);

                if (grabTime.isBefore(now)) {
                    // 如果设置的时间已经过了，设置为明天
                    grabTime = grabTime.plusDays(1);
                }

                // 计算延迟时间（毫秒）
                long delayMs = java.time.Duration.between(now, grabTime).toMillis();

                // 取消当前正在运行的倒计时
                if (countdownThread != null && countdownThread.isAlive()) {
                    countdownThread.interrupt();
                }

                // 保存定时任务信息
                currentTaskId = "grab_task_" + System.currentTimeMillis();
                currentGrabTime = grabTime;
                
                // 启动倒计时
                startCountdown();
                
                // 创建定时任务
                final String token = currentUser.getToken();
                // 使用Java内置的ScheduledExecutorService来替代taskManager
                java.util.concurrent.Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                    for (ContractInfo contract : selectedContracts) {
                        try {
                            if (currentUser != null && currentUser.getToken() != null) {
                                // 模拟抢单操作，随机成功或失败
                                boolean success = Math.random() > 0.3; // 70%的成功率
                                if (success) {
                                    logSuccess("定时抢单成功", contract.getContractNo());
                                } else {
                                    logError("定时抢单失败", contract.getContractNo());
                                }
                            }
                        } catch (Exception ex) {
                            logError("定时抢单异常: " + ex.getMessage(), contract.getContractNo());
                        }
                    }
                }, delayMs, java.util.concurrent.TimeUnit.MILLISECONDS);

                showAlert("成功", "已设置定时抢单任务，时间：" + grabTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception ex) {
                showAlert("错误", "时间设置不正确");
                ex.printStackTrace();
            }
        });

        panel.getChildren().addAll(
            grabNowButton,
            new Label("定时抢单时间："),
            hourComboBox,
            new Label("："),
            minuteComboBox,
            new Label("："),
            secondComboBox,
            scheduleGrabButton,
            countdownLabel
        );

        return panel;
    }
    
    // 启动倒计时
    private void startCountdown() {
        countdownThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    long secondsUntilGrab = now.until(currentGrabTime, java.time.temporal.ChronoUnit.SECONDS);
                    
                    if (secondsUntilGrab <= 0) {
                        Platform.runLater(() -> countdownLabel.setText("抢单任务已执行"));
                        break;
                    }
                    
                    // 计算时分秒
                    long hours = secondsUntilGrab / 3600;
                    long minutes = (secondsUntilGrab % 3600) / 60;
                    long seconds = secondsUntilGrab % 60;
                    
                    String countdownText = String.format("距离抢单还剩：%02d:%02d:%02d", hours, minutes, seconds);
                    
                    Platform.runLater(() -> countdownLabel.setText(countdownText));
                    
                    Thread.sleep(1000); // 每秒更新一次
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        countdownThread.setDaemon(true);
        countdownThread.start();
    }
    
    // 获取选中的合同
    private List<ContractInfo> getSelectedContracts() {
        List<ContractInfo> selected = new ArrayList<>();
        for (ContractInfo contract : contractData) {
            if (contract.isSelected()) {
                selected.add(contract);
            }
        }
        return selected;
    }
    
    // 更新全选状态
    private void updateSelectAllState(CheckBox checkBox, TableView<ContractInfo> table) {
        if (contractData.isEmpty()) {
            checkBox.setSelected(false);
            return;
        }
        
        boolean allSelected = true;
        for (ContractInfo contract : contractData) {
            if (!contract.isSelected()) {
                allSelected = false;
                break;
            }
        }
        
        checkBox.setSelected(allSelected);
        
        // 如果有部分选中，设置为不确定状态
        boolean noneSelected = true;
        for (ContractInfo contract : contractData) {
            if (contract.isSelected()) {
                noneSelected = false;
                break;
            }
        }
        
        if (!allSelected && !noneSelected) {
            checkBox.setIndeterminate(true);
        } else {
            checkBox.setIndeterminate(false);
        }
    }
    
    // 启动日志刷新线程
    private void startLogRefreshThread() {
        Thread logThread = new Thread(() -> {
            List<LogEntry> previousLogs = new ArrayList<>();
            while (true) {
                try {
                    Thread.sleep(1000); // 每秒刷新一次
                    List<LogEntry> currentLogs = getAllLogs();
                    
                    // 如果有新日志，更新UI
                    if (currentLogs.size() > previousLogs.size()) {
                        final StringBuilder logBuilder = new StringBuilder();
                        for (LogEntry log : currentLogs) {
                            logBuilder.append(log.getTimestamp())
                                    .append(" [")
                                    .append(log.getLevel())
                                    .append("] ")
                                    .append(log.getContractNumber())
                                    .append(": ")
                                    .append(log.getContent())
                                    .append("\n");
                        }
                        
                        final String logText = logBuilder.toString();
                        Platform.runLater(() -> {
                            logTextArea.setText(logText);
                            logTextArea.setScrollTop(Double.MAX_VALUE); // 滚动到底部
                        });
                        
                        previousLogs = new ArrayList<>(currentLogs);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        logThread.setDaemon(true);
        logThread.start();
    }
    
    // 内部日志记录方法
    private void logInfo(String content, String contractNumber) {
        LogEntry logEntry = new LogEntry();
        logEntry.setLevel(LogEntry.LogLevel.INFO);
        logEntry.setContent(content);
        logEntry.setContractNumber(contractNumber);
        logEntry.setTimestamp(LocalDateTime.now());
        logs.add(logEntry);
    }
    
    private void logSuccess(String content, String contractNumber) {
        LogEntry logEntry = new LogEntry();
        logEntry.setLevel(LogEntry.LogLevel.SUCCESS);
        logEntry.setContent(content);
        logEntry.setContractNumber(contractNumber);
        logEntry.setTimestamp(LocalDateTime.now());
        logs.add(logEntry);
    }
    
    private void logError(String content, String contractNumber) {
        LogEntry logEntry = new LogEntry();
        logEntry.setLevel(LogEntry.LogLevel.ERROR);
        logEntry.setContent(content);
        logEntry.setContractNumber(contractNumber);
        logEntry.setTimestamp(LocalDateTime.now());
        logs.add(logEntry);
    }
    
    // 获取所有日志
    private List<LogEntry> getAllLogs() {
        return new ArrayList<>(logs);
    }
    
    // 显示警告对话框
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void stop() {
        // 清理资源
        if (countdownThread != null && countdownThread.isAlive()) {
            countdownThread.interrupt();
        }
    }
    
    // 生成模拟合同数据
    private List<ContractInfo> generateMockContracts() {
        List<ContractInfo> contracts = new ArrayList<>();
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        
        for (int i = 0; i < 20; i++) {
            ContractInfo contract = new ContractInfo();
            contract.setContractNo("CONTRACT-" + letters[i % letters.length] + "-" + (1000 + i));
            contract.setTotalODDays((int)(Math.random() * 360));
            contract.setCustomerName("客户" + (i + 1));
            contract.setSelected(false);
            contracts.add(contract);
        }
        
        return contracts;
    }
}