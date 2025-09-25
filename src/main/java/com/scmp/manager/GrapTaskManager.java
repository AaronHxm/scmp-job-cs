package com.scmp.manager;


import com.google.common.util.concurrent.RateLimiter;
import com.scmp.executor.ContractThreadPoolLazy;
import com.scmp.model.ContractInfo;
import com.scmp.model.ProcessResult;
import com.scmp.service.CaseGrabService;
import com.scmp.service.LogService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GrapTaskManager {


    private static final int MAX_RETRIES = 100;

    private LogService logService = LogService.getInstance();


    private CaseGrabService caseGrabService =  new CaseGrabService();

    int logical = Runtime.getRuntime().availableProcessors(); // 获取逻辑处理器数量

    // I/O密集型任务配置（HTTP调用为主）
    int corePoolSize = Math.min(64, logical * 4); // 20 * 4=80，保守上限64
    int maxPoolSize = corePoolSize;


    private Executor contractProcessorExecutor =   ContractThreadPoolLazy.getInstance();

    private static final int MAX_REQUESTS_PER_SECOND = 100;

    private final RateLimiter rateLimiter = RateLimiter.create(MAX_REQUESTS_PER_SECOND);



    public CompletableFuture<List<ProcessResult>> processContractsAsync(List<ContractInfo> contracts) {
        if (contracts == null || contracts.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        // 使用并行流处理合约，充分利用线程池ß
        List<CompletableFuture<ProcessResult>> futures = contracts.stream()
                .map(contract -> CompletableFuture.supplyAsync(() -> {
                    // 获取令牌，如果超过速率限制会阻塞
                    rateLimiter.acquire();
                    return processContractWithRetry(contract);
                }, contractProcessorExecutor))
                .collect(Collectors.toList());

        // allOf 收敛，不在池内阻塞等待；只在汇总时 join 已经完成的 future
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return all.thenApply(v ->
                futures.stream().map(CompletableFuture::join).collect(Collectors.toList())
        );
    }
    private ProcessResult processContractWithRetry(ContractInfo contract) {
        int attempts = 0;
        String lastResponse = null;
        long startTime = System.currentTimeMillis();

        while (attempts <= MAX_RETRIES) {
            attempts++;




            lastResponse = caseGrabService.grabCase(contract.getSyskey(), contract.getContractNo(), contract.getUserId());

            if (isSuccessResponse(lastResponse)) {
                if (lastResponse.contains("受理")){
                    logService.info("订单号:{},重试次数:{},结果:{},甲方返回信息:{}", contract.getContractNo(),contract.getContractNo(),attempts,"SUCCESS",lastResponse);
                }else {
                    logService.info("订单号:{},重试次数:{},结果:{},甲方返回信息:{}", contract.getContractNo(),contract.getContractNo(),attempts,"失败",lastResponse);
                }


                return new ProcessResult(contract.getContractNo(), true, lastResponse, attempts);
            }


            if (attempts < MAX_RETRIES) {
                long delay = calculateRetryDelay(attempts);
                if (delay > 0) {
                    try {

                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logService.info("订单号:{},重试次数:{},结果:{}", contract.getContractNo(),contract.getContractNo(),attempts,"ERROR");
                        return new ProcessResult(contract.getContractNo(), false, "Interrupted", attempts);
                    }
                }
            }
        }


        return new ProcessResult(contract.getContractNo(), false, lastResponse, attempts);
    }

    // 优化后的辅助方法
    private long calculateRetryDelay(int attempt) {
        // 动态调整的重试策略：
        // - 第一次立即重试（delay=0）
        // - 后续采用短延迟（避免过长等待）
        return attempt == 1 ? 0 : Math.min(200, (long) (50 * Math.pow(2, attempt)));
    }

    private boolean isSuccessResponse(String response) {
        // 这里根据实际业务逻辑实现
        // 示例：假设返回的JSON中包含 success:true 表示成功
        return  response.contains("受理") || response.contains("不存在");
    }


}
