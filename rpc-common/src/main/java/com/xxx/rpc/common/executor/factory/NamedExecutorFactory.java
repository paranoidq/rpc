package com.xxx.rpc.common.executor.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Customized executor thread factory
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class NamedExecutorFactory implements ThreadFactory {

    private static final AtomicInteger threadNumber  = new AtomicInteger(1);
    private static final AtomicInteger factoryNumber = new AtomicInteger(1);

    private final String      prefix;
    private final boolean     isDaemon;
    private final ThreadGroup threadGroup;

    public NamedExecutorFactory() {
        this("threadpool-" + factoryNumber.getAndIncrement() + "-");
    }

    public NamedExecutorFactory(String prefix) {
        this(prefix, false);
    }

    public NamedExecutorFactory(String prefix, boolean isDaemon) {
        this.prefix = prefix + "-threadpool-" + threadNumber.getAndIncrement() + "-";
        this.isDaemon = isDaemon;
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = prefix + threadNumber.getAndIncrement();
        Thread thread = new Thread(threadGroup, r, threadName, 0);
        thread.setDaemon(isDaemon);
        return thread;
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }
}
