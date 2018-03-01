package com.project.common_basic.reactive;

import android.os.Process;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.NewThreadWorker;
import rx.internal.schedulers.ScheduledAction;
import rx.internal.util.RxThreadFactory;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * 自定义IO线程调度器
 * <p>
 * Created by zczhang on 16/6/12.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class CustomIoScheduler extends Scheduler {
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 2;
    private static final String WORKER_THREAD_NAME_PREFIX = "CustomIoScheduler-";
    private static final RxThreadFactory WORKER_THREAD_FACTORY =
            new RxThreadFactory(WORKER_THREAD_NAME_PREFIX);

    private static final String EVICTOR_THREAD_NAME_PREFIX = "RxCachedWorkerPoolEvictor-";
    private static final RxThreadFactory EVICTOR_THREAD_FACTORY =
            new RxThreadFactory(EVICTOR_THREAD_NAME_PREFIX);

    private static class LazyHolder {
        private static final CustomIoScheduler INSTANCE = new CustomIoScheduler();
    }

    private CustomIoScheduler() {
    }

    public static CustomIoScheduler getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static final class CachedWorkerPool {
        private final long keepAliveTime;
        private final ConcurrentLinkedQueue<ThreadWorker> expiringWorkerQueue;
        private final ScheduledExecutorService evictExpiredWorkerExecutor;

        CachedWorkerPool(long keepAliveTime, TimeUnit unit) {
            this.keepAliveTime = unit.toNanos(keepAliveTime);
            this.expiringWorkerQueue = new ConcurrentLinkedQueue<>();

            evictExpiredWorkerExecutor = Executors.newScheduledThreadPool(CORE_POOL_SIZE, EVICTOR_THREAD_FACTORY);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    evictExpiredWorkers();
                }
            };
            evictExpiredWorkerExecutor.scheduleWithFixedDelay(runnable,
                    this.keepAliveTime, this.keepAliveTime, TimeUnit.NANOSECONDS);
        }

        private static CachedWorkerPool INSTANCE = new CachedWorkerPool(
                60L, TimeUnit.SECONDS
        );

        ThreadWorker get() {
            while (!expiringWorkerQueue.isEmpty()) {
                ThreadWorker threadWorker = expiringWorkerQueue.poll();
                if (threadWorker != null) {
                    return threadWorker;
                }
            }

            // No cached worker found, so create a new one.
            return new ThreadWorker(WORKER_THREAD_FACTORY);
        }

        void release(ThreadWorker threadWorker) {
            // Refresh expire time before putting worker back in pool
            threadWorker.setExpirationTime(now() + keepAliveTime);

            expiringWorkerQueue.offer(threadWorker);
        }

        void evictExpiredWorkers() {
            if (!expiringWorkerQueue.isEmpty()) {
                long currentTimestamp = now();

                for (ThreadWorker threadWorker : expiringWorkerQueue) {
                    if (threadWorker.getExpirationTime() <= currentTimestamp) {
                        if (expiringWorkerQueue.remove(threadWorker)) {
                            threadWorker.unsubscribe();
                        }
                    } else {
                        // Queue is ordered with the worker that will expire first in the beginning, so when we
                        // find a non-expired worker we can stop evicting.
                        break;
                    }
                }
            }
        }

        long now() {
            return System.nanoTime();
        }
    }

    @Override
    public Worker createWorker() {
        return new EventLoopWorker(CachedWorkerPool.INSTANCE.get());
    }

    private static final class EventLoopWorker extends Scheduler.Worker {
        private final CompositeSubscription innerSubscription = new CompositeSubscription();
        private final ThreadWorker threadWorker;
        @SuppressWarnings("unused")
        volatile int once;
        static final AtomicIntegerFieldUpdater<EventLoopWorker> ONCE_UPDATER
                = AtomicIntegerFieldUpdater.newUpdater(EventLoopWorker.class, "once");

        EventLoopWorker(ThreadWorker threadWorker) {
            this.threadWorker = threadWorker;
        }

        @Override
        public void unsubscribe() {
            if (ONCE_UPDATER.compareAndSet(this, 0, 1)) {
                // unsubscribe should be idempotent, so only do this once
                CachedWorkerPool.INSTANCE.release(threadWorker);
            }
            innerSubscription.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return innerSubscription.isUnsubscribed();
        }

        @Override
        public Subscription schedule(Action0 action) {
            return schedule(action, 0, null);
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (innerSubscription.isUnsubscribed()) {
                // don't schedule, we are unsubscribed
                return Subscriptions.unsubscribed();
            }

            ScheduledAction s = threadWorker.scheduleActual(new ActionWrapper(action), delayTime, unit);
            innerSubscription.add(s);
            s.addParent(innerSubscription);
            return s;
        }
    }

    private static final class ThreadWorker extends NewThreadWorker {
        private long expirationTime;

        ThreadWorker(ThreadFactory threadFactory) {
            super(threadFactory);
            this.expirationTime = 0L;
        }

        public long getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(long expirationTime) {
            this.expirationTime = expirationTime;
        }
    }

    private static class ActionWrapper implements Action0 {

        private Action0 mAction0;

        public ActionWrapper(Action0 action0) {
            this.mAction0 = action0;
        }

        @Override
        public void call() {
            Process.setThreadPriority(Process.myPid(), Process.THREAD_PRIORITY_BACKGROUND);
            mAction0.call();
        }
    }
}
