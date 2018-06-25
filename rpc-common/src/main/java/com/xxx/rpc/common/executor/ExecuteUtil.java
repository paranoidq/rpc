package com.xxx.rpc.common.executor;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.*;
import com.xxx.rpc.common.executor.factory.NamedExecutorFactory;
import com.xxx.rpc.common.executor.policy.AbortPolicyWithReport;
import com.xxx.rpc.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ExecuteUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteUtil.class);

    private        ListeningExecutorService listeningExecutorService;
    private static ThreadPoolExecutor       threadPoolExecutor;
    private        int                      executorShutdownAwaitTerminationSeconds = 60;


    private ExecuteUtil(ListeningExecutorService listeningExecutorService) {
        this.listeningExecutorService = listeningExecutorService;
    }

    /**
     * 创建执行器
     *
     * @param poolName
     * @param coreThreads
     * @param maxThreads
     * @param queueSize
     *              queue == 0 gives {@link SynchronousQueue},
     *              queues < 0 gives unbounded {@link LinkedBlockingQueue},
     *              queues > 0 gives bounded {@link LinkedBlockingQueue}
     *
     *
     * @param keepAliveSeconds
     * @return
     */
    public static ExecuteUtil getInstance(String poolName, int coreThreads, int maxThreads, int queueSize, int keepAliveSeconds) {
        Preconditions.checkArgument(StringUtil.isNotEmpty(poolName), "poolName is empty");
        Preconditions.checkArgument(coreThreads > 0, "coreThreads must be positive ");
        Preconditions.checkArgument(maxThreads >= coreThreads, "maxThreads must be larger than coreThreads");

        threadPoolExecutor = new ThreadPoolExecutor(
            coreThreads, maxThreads, keepAliveSeconds, TimeUnit.SECONDS,
            queueSize == 0 ? new SynchronousQueue<Runnable>()
                : (queueSize < 0 ? new LinkedBlockingQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queueSize)),
            new NamedExecutorFactory(poolName),
            new AbortPolicyWithReport(poolName)
        );

        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(threadPoolExecutor);
        return new ExecuteUtil(listeningExecutorService);
    }

    /**
     * 创建daemon执行器
     *
     * @param poolName
     * @param coreThreads
     * @param maxThreads
     * @param queueSize
     *              queue == 0 gives {@link SynchronousQueue},
     *              queues < 0 gives unbounded {@link LinkedBlockingQueue},
     *              queues > 0 gives bounded {@link LinkedBlockingQueue}
     * @param keepAliveSeconds
     * @return
     */
    public static ExecuteUtil getDaemonInstance(String poolName, int coreThreads, int maxThreads, int queueSize, int keepAliveSeconds) {
        Preconditions.checkArgument(StringUtil.isNotEmpty(poolName), "poolName is empty");
        Preconditions.checkArgument(coreThreads > 0, "coreThreads must be positive ");
        Preconditions.checkArgument(maxThreads >= coreThreads, "maxThreads must be larger than coreThreads");

        threadPoolExecutor = new ThreadPoolExecutor(
            coreThreads, maxThreads, keepAliveSeconds, TimeUnit.SECONDS,
            queueSize == 0 ? new SynchronousQueue<Runnable>()
                : (queueSize < 0 ? new LinkedBlockingQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queueSize)),
            new NamedExecutorFactory(poolName, true),
            new AbortPolicyWithReport(poolName)
        );

        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(threadPoolExecutor);
        return new ExecuteUtil(listeningExecutorService);
    }


    /**
     * 提交任务，通过callback异步处理任务的结果
     *
     * @param task
     * @param executeCallback
     * @param <T>
     */
    public <T> void submit(Callable<T> task, final ExecuteCallback<T> executeCallback) {
        final ListenableFuture<T> listenableFuture = this.listeningExecutorService.submit(task);
        Futures.addCallback(listenableFuture, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                executeCallback.onExecuteSuccess(result);
            }

            @Override
            public void onFailure(Throwable t) {
                executeCallback.onExecuteFailure(t);
            }
        }, listeningExecutorService);
    }


    /**
     * 执行任务，不关心任务处理的结果
     *
     * @param task
     */
    public void execute(Runnable task) {
        final ListenableFuture<?> listenableFuture = this.listeningExecutorService.submit(task);
        Futures.addCallback(listenableFuture, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                logger.debug("Task execute success");
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("Task execute failed", t);
            }
        }, listeningExecutorService);
    }

    /**
     * 执行任务，通过callback异步处理任务完成后的工作
     *
     * @param task
     * @param executeCallback
     */
    public void execute(Runnable task, final ExecuteCallback executeCallback) {
        final ListenableFuture<?> listenableFuture = this.listeningExecutorService.submit(task);
        Futures.addCallback(listenableFuture, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                logger.debug("Task execute success");
                executeCallback.onExecuteSuccess(result);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("Task execute failed", t);
                executeCallback.onExecuteFailure(t);
            }
        }, listeningExecutorService);
    }


    /**
     * 执行器是否已终止
     *
     * @return
     */
    public boolean isTerminated() {
        return this.listeningExecutorService.isTerminated();
    }


    /**
     * 关闭执行器
     *
     * @throws InterruptedException
     */
    public void shutdown() throws InterruptedException {
        this.listeningExecutorService.awaitTermination(executorShutdownAwaitTerminationSeconds, TimeUnit.SECONDS);
        this.listeningExecutorService.shutdown();
    }
}
