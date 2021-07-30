package com.gmail.melnikau.concurrency.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class MyThreadFactory {

    private MyThreadFactory() {}

    public static ThreadFactory getThreadFactory(String threadName) {

        return new ThreadFactory() {

            private final AtomicLong threadIndex = new AtomicLong(0);

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName(String.format("%s %s", threadName, threadIndex.incrementAndGet()));
                return thread;
            }
        };
    }

}