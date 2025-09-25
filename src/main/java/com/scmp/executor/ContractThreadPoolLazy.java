package com.scmp.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ContractThreadPoolLazy {
    private ContractThreadPoolLazy() {}

    // 静态内部类实现懒加载
    private static class Holder {
        private static final ThreadPoolExecutor INSTANCE = createExecutor();

        private static ThreadPoolExecutor createExecutor() {
            int logical = Runtime.getRuntime().availableProcessors();
            int corePoolSize = Math.min(64, logical * 4);

            return new ThreadPoolExecutor(
                    corePoolSize,
                    corePoolSize,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(20000),
                    new ThreadFactory() {
                        private int count = 1;
                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "ContractProcessor-" + count++);
                        }
                    },
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
    }

    public static ThreadPoolExecutor getInstance() {
        return Holder.INSTANCE;
    }
}
