package com.xxx.rpc.common.executor.policy;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {

    private final String threadName;

    public AbortPolicyWithReport(String threadName) {
        this.threadName = threadName;
    }

    /**
     * Always throws RejectedExecutionException.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     * @throws RejectedExecutionException always
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("Thread aborted: ["
            + "Thread name: %s, Pool size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)"
            + "Executor status: (isShutdown: %s, isTerminated: %s, isTerminating: %s)]",
            threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
            e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
        throw new RejectedExecutionException(msg);
    }
}
