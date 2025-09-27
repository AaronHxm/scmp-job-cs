//package com.scmp.service;
//
//import com.scmp.model.ContractInfo;
//import org.jetbrains.annotations.Contract;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//public class    ScheduledTaskService {
//
//    private ApiService apiService;
//    private LogService logService;
//    private ScheduledExecutorService scheduler;
//
//    private Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
//
//    public ScheduledTaskService(ApiService apiService, LogService logService) {
//        this.apiService = apiService;
//        this.logService = logService;
//        this.scheduler = Executors.newScheduledThreadPool(5);
//    }
//
//    // 安排定时抢单任务
//    public void scheduleGrabTask(String taskId, List<ContractInfo> selectedContracts, long delay, TimeUnit timeUnit) {
//        // 取消已存在的同名任务
//        cancelTask(taskId);
//
//        // 创建新任务
//        Runnable task = () -> {
//            for (ContractInfo contract : selectedContracts) {
//                boolean success = apiService.grabContract(contract.getContractNo());
//                if (success) {
//                    logService.success("定时抢单成功", contract.getContractNo());
//                } else {
//                    logService.error("定时抢单失败", contract.getContractNo());
//                }
//            }
//        };
//
//        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, 0, delay, timeUnit);
//        scheduledTasks.put(taskId, future);
//        logService.info("已安排定时抢单任务: " + taskId + " (每" + delay + " " + timeUnit.name() + "执行一次)", "-");
//    }
//
//    // 兼容旧版本的方法，默认每5秒执行一次
//    public void scheduleGrabTask(String taskId, List<ContractInfo> selectedContracts) {
//        scheduleGrabTask(taskId, selectedContracts, 5, TimeUnit.SECONDS);
//    }
//
//    // 取消定时任务
//    public void cancelTask(String taskId) {
//        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
//        if (future != null) {
//            future.cancel(false);
//            logService.info("已取消定时抢单任务: " + taskId, "-");
//        }
//    }
//
//    // 关闭调度器
//    public void shutdown() {
//        if (scheduler != null && !scheduler.isShutdown()) {
//            scheduler.shutdown();
//        }
//    }
//}