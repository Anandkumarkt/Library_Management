package com.library.management.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {
    private static ExecutorService executorService;
    private ThreadPoolUtil() {
        // Instantiation of this class is not permitted.
        throw new UnsupportedOperationException();
    }
    static {

        // This approach is platform dependent but intended to be in that way.
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
    public static ExecutorService getThreadPool() {

        return executorService;
    }
}
