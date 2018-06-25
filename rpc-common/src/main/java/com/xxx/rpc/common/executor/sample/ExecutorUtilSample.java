package com.xxx.rpc.common.executor.sample;

import com.xxx.rpc.common.executor.ExecuteCallback;
import com.xxx.rpc.common.executor.ExecuteUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ExecutorUtilSample {

    static class Task implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            return 1 + 1;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecuteUtil util = ExecuteUtil.getInstance("sample", 10, 100, 100, 60);
        for (int i=0; i< 1000; i++) {
            util.submit(new Task(), new ExecuteCallback<Integer>() {
                @Override
                public void onExecuteSuccess(Integer result) {
                    System.out.println(result);
                }
            });
        }
    }
}
