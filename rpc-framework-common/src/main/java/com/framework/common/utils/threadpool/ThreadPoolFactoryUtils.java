package com.framework.common.utils.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 创建线程池的工具类
 */
@Slf4j
public final class ThreadPoolFactoryUtils {

    /**
     * 通过 threadNamePrefix（线程池名称前缀） 来区分不同线程池
     * 我们把相同的threadNamePrefix 的线程池看作是为同一业务场景服务
     * key: threadNamePrefix
     * value:threadPool
     */
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix) {
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    private static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, boolean daemon) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));
        // 如果threadPool 被 shutdown的话就重新创建一个
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaximumPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getUnit(), customThreadPoolConfig.getWorkQueue(),
                threadFactory);
    }

    /**
     * 创建   ThreadFactory.如果threadNamePrefix不为空则使用自建的threadFactory。否则使用
     *
     * @param threadNamePrefix 作为创建线程名字的前缀
     * @param daemon           标记是否为 daemon Thread (守护线程)
     * @return
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder()
                        // 设置名称格式
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }

        return Executors.defaultThreadFactory();
    }
    public static void shutDownAllThreadPool(){
        log.info("开始调用 shutDownAllThreadPool()");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry->{
            ExecutorService executorService=entry.getValue();
            // 关闭线程
            executorService.shutdown();
            log.info("关闭线程池 [{}] [{}]",entry.getKey(),executorService.isTerminated());
            try {
                // 检测一段时间内线程是否完全终止
                executorService.awaitTermination(10,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("线程吃停止失败 强制停止");
                executorService.shutdownNow();
            }
        });
    }

    /**
     * 打印线程池状态
     *
     * @param threadPool 线程池对象
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool){
        ScheduledExecutorService scheduledExecutorService=new ScheduledThreadPoolExecutor(1,createThreadFactory("print-htread-pool-status",false));
        // 定时周期执行任务
        scheduledExecutorService.scheduleAtFixedRate(()->{
            log.info("============ThreadPool Status=============");
            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("Active Threads: [{}]", threadPool.getActiveCount());
            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
        },0,1,TimeUnit.SECONDS);
    }


}
