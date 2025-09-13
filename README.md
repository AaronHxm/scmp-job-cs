# SCMP 合同抢单系统

这是一个Java客户端软件，用于合同查询和抢单功能。

## 功能特点

1. **登录功能**：支持账号密码登录和Token直接登录两种方式
2. **合同查询**：可以根据逾期天数和字母条件筛选合同
3. **抢单功能**：支持立即抢单和定时抢单两种方式
4. **日志显示**：实时显示抢单过程的日志信息

## 技术栈

- Java 11
- Spring Boot 2.7.6
- JavaFX 17
- Maven

## 项目结构

```
src/main/java/com/scmp/
├── config/           # 配置类
├── model/            # 数据模型
├── service/          # 业务服务
├── ui/               # 用户界面
├── Application.java  # Spring Boot主类
└── JavaFxApp.java    # JavaFX启动类
```

## 主要类说明

### 数据模型
- `User`：用户信息模型
- `Contract`：合同信息模型
- `LogEntry`：日志信息模型

### 业务服务
- `ApiService`：接口服务类，处理与后端的交互
- `LogService`：日志服务类，管理抢单日志
- `ScheduledTaskService`：定时任务服务，处理定时抢单

### 用户界面
- `LoginUI`：登录界面
- `MainUI`：主界面，包含查询、抢单和日志显示

## 如何运行

1. 确保已安装JDK 11和Maven
2. 使用Maven构建项目：`mvn clean package`
3. 运行JavaFX应用程序：`java -jar target/job-cs-1.0-SNAPSHOT.jar`

## 特别说明

- 查询和抢单接口目前使用伪代码实现，实际使用时需要替换为真实的接口调用
- 定时抢单功能使用Spring的TaskScheduler实现
- 日志功能使用独立线程定期刷新显示

## 注意事项

- 登录成功后进入主界面
- 查询条件支持设置逾期天数（整数，默认360）和选择字母（A-Z）
- 定时抢单时间格式为HH:mm:ss
- 抢单日志会实时显示在界面底部