package com.scmp.ui;


import com.scmp.executor.ContractThreadPoolLazy;
import com.scmp.manager.GrapTaskManager;
import com.scmp.model.ContractInfo;
import com.scmp.model.HistoryInfo;
import com.scmp.model.LogEntry;
import com.scmp.model.User;
import com.scmp.manager.QueryManager;
import com.scmp.service.HistoryService;
import com.scmp.service.LogService;
import javafx.application.Application;
import javafx.application.Platform;
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
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
public class MainUI extends Application {
    

    private User currentUser;
    private QueryManager apiService;
    private LogService logService = LogService.getInstance();
    
    private ObservableList<ContractInfo> contractData = FXCollections.observableArrayList();
    private TextArea logTextArea;
    
    public MainUI(QueryManager apiService, User currentUser) {
        this.apiService = apiService;
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
                // 查询合同
                if (currentUser != null && currentUser.isLoggedIn()) {
                    Integer maxOverdueDays = null;
                    try {
                        maxOverdueDays = Integer.parseInt(overdueDaysField.getText());
                    } catch (NumberFormatException ex) {
                        logInfo("逾期天数输入无效，使用默认值360", "-");
                        maxOverdueDays = 360;
                    }
                    
                    // 使用API查询合同，传入选中的字母列表和逾期天数
                    List<ContractInfo> filteredContracts = apiService.queryContracts(currentUser, maxOverdueDays, selectedLetters);
                    
                    // 为每个合同设置选中状态为false
                    for (ContractInfo contract : filteredContracts) {
                        contract.setSelected(false);
                    }

                    //
                    HistoryService historyService = new HistoryService();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    List<CompletableFuture<Void>> futures = filteredContracts.stream()
                            .map(contract -> CompletableFuture.runAsync(() -> {
                                try {
                                    List<HistoryInfo> historyInfoList = historyService.getHistoryInfoByContractNo(
                                            contract.getContractNo(), contract.getUserId());

                                    if (historyInfoList == null || historyInfoList.isEmpty()) {
                                        return;
                                    }

                                    historyInfoList.stream()
                                            .max(Comparator.comparing(HistoryInfo::getOrderCreateTime))
                                            .ifPresent(latestHistory -> {
                                                String formattedDate = latestHistory.getOrderCreateTime() != null
                                                        ? LocalDateTime.ofInstant(latestHistory.getOrderCreateTime().toInstant(), ZoneId.systemDefault())
                                                        .format(formatter)
                                                        : "null";

                                                String remarks = String.format("%s，客服：%s，结果：%s",
                                                        formattedDate,
                                                        latestHistory.getCreateName() != null ? latestHistory.getCreateName() : "null",
                                                        latestHistory.getRequireContent() != null ? latestHistory.getRequireContent() : "null");

                                                contract.setHistoryRemarks(remarks);
                                            });
                                } catch (Exception exception) {
                                    log.error("处理合同 {} 历史记录异常", contract.getContractNo(), exception);
                                }
                            }, ContractThreadPoolLazy.getInstance()))
                            .collect(Collectors.toList());


                    // 等待所有任务完成
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                    contractData.setAll( generateMockContracts());
                    logInfo("查询合同成功，获取到 " + filteredContracts.size() + " 条记录", "-");
                } else {
                    showAlert("错误", "用户未登录");
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
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);
        selectColumn.setPrefWidth(60);
        checkBox.setPadding(new Insets(0, 0, 0, 25)); // 调整位置使其居中
        
        // 全选功能实现
        checkBox.setOnAction(event -> {
            boolean isSelected = checkBox.isSelected();
            for (ContractInfo contract : contractData) {
                contract.setSelected(isSelected);
            }
            table.refresh(); // 刷新表格视图
        });
        
        // 将复选框设置为表头
        selectColumn.setGraphic(checkBox);
        
        // 监听数据变化，更新全选状态
        contractData.addListener((javafx.collections.ListChangeListener<ContractInfo>) c -> {
            while (c.next()) {
                updateSelectAllState(checkBox, table);
            }
        });
        
        // 监听表格选择变化，自动勾选复选框
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.setSelected(true);
                log.debug("行选中自动勾选合同: {}", newValue.getContractNo());
            }
            updateSelectAllState(checkBox, table);
            table.refresh();
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

        // 客户姓名列
        TableColumn<ContractInfo, String> customerHistoryRemarksColumn = new TableColumn<>("历史记录");
        customerHistoryRemarksColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHistoryRemarks()));
        customerHistoryRemarksColumn.setPrefWidth(500);


        table.getColumns().addAll(selectColumn, contractNumberColumn, overdueDaysColumn, customerNameColumn,customerHistoryRemarksColumn);
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

        // 返回登录按钮
        Button backToLoginButton = new Button("返回登录");
        backToLoginButton.setPrefWidth(100);
        backToLoginButton.setStyle("-fx-font-size: 14px;");
        backToLoginButton.setOnAction(e -> {
            try {
                // 清理资源
                stop();
                // 关闭当前窗口

                // 打开登录页面
                LoginUI loginUI = new LoginUI();
                Stage loginStage = new Stage();
                loginUI.start(loginStage);
            } catch (Exception ex) {
                logError("返回登录页面失败: " + ex.getMessage(), "");
            }
        });
        
        // 立即抢单按钮 - 增大尺寸
        Button grabNowButton = new Button("立即抢单");
        grabNowButton.setPrefWidth(100);
        grabNowButton.setStyle("-fx-font-size: 14px;");
        grabNowButton.setOnAction(e -> {
            // 强制刷新表格确保选中状态同步
            TableView<ContractInfo> table = (TableView<ContractInfo>) ((HBox) grabNowButton.getParent())
                .getScene().lookup(".table-view");
            if (table != null) {
                table.refresh();
            }

            List<ContractInfo> selectedContracts = getSelectedContracts();
            if (selectedContracts.isEmpty()) {
                showAlert("提示", "请先勾选要抢的合同");
                return;
            }

            logInfo("开始抢单，选中合同数量：" + selectedContracts.size(), "-");
            
            // 执行抢单操作，只处理选中的合同
            GrapTaskManager grapTaskManager = new GrapTaskManager();
            grapTaskManager.processContractsAsync(FXCollections.observableArrayList(selectedContracts));
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
        hourComboBox.setPrefWidth(100);

        ComboBox<Integer> minuteComboBox = new ComboBox<>();
        for (int i = 0; i < 60; i++) {
            minuteComboBox.getItems().add(i);
        }
        minuteComboBox.setPrefWidth(100);
        
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
        secondComboBox.setPrefWidth(100);

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
                    // 定时任务开始执行
                    GrapTaskManager grapTaskManager = new GrapTaskManager();

                    grapTaskManager.processContractsAsync(contractData);

                }, delayMs, java.util.concurrent.TimeUnit.MILLISECONDS);

                showAlert("成功", "已设置定时抢单任务，时间：" + grabTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception ex) {
                showAlert("错误", "时间设置不正确");
                ex.printStackTrace();
            }
        });

        panel.getChildren().addAll(
            backToLoginButton,
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
    
    // 获取选中的合同（检查复选框和行选中状态）
    private List<ContractInfo> getSelectedContracts() {
        List<ContractInfo> selected = new ArrayList<>();
        
        // 检查复选框选中状态
        for (ContractInfo contract : contractData) {
            if (contract.isSelected()) {
                selected.add(contract);
                log.debug("复选框选中合同: {}", contract.getContractNo());
            }
        }

        log.debug("总选中合同数: {}", selected.size());
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
        // 添加日志监听器，实时更新UI
        logService.addLogListener(logEntry -> {
            Platform.runLater(() -> {
                // 获取所有日志并更新UI
                updateLogTextArea();
            });
        });
        
        // 初始加载日志
        updateLogTextArea();
    }
    
    // 更新日志文本区域
    private void updateLogTextArea() {
        List<LogEntry> logs = logService.getAllLogs();
        StringBuilder logBuilder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (LogEntry log : logs) {
            logBuilder.append(log.getTimestamp().format(formatter))
                    .append(" [")
                    .append(log.getLevel())
                    .append("] ")
                    .append(log.getContent())
                    .append("\n");
        }
        
        final String logText = logBuilder.toString();
        logTextArea.setText(logText);
        logTextArea.setScrollTop(Double.MAX_VALUE); // 滚动到底部
    }
    
    // 内部日志记录方法
    private void logInfo(String content, String contractNumber) {
        LogService.info(content, contractNumber);
    }
    
    private void logError(String content, String contractNumber) {
        LogService.error(content, contractNumber);
    }
    
    // 获取所有日志
    private List<LogEntry> getAllLogs() {
        return logService.getAllLogs();
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