package com.wlp.myanjunote.java.thread.threadsync;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
//        thread();
//        runnable();
//        threadFactory();
//        executor();
//        callable();
//        runSynchronized1Demo();
//        runSynchronized2Demo();
        runSynchronized3Demo();
//        runReadWriteLockDemo();
    }

    /**
     * 使用 Thread 类来定义工作,继承Thread
     */
    static void thread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("Thread started!");
            }
        };
        thread.start();
    }

    /**
     * Runnable + Thread
     * 使用 Runnable 类来定义工作
     * 推荐使用：避免单继承局限性，灵活方便，方便同一个对象被多个线程使用。
     */
    static void runnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread with Runnable started!");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Runnable + ThreadFactory
     * ThreadFactory 线程工厂来创建线程
     **/
    static void threadFactory() {
        ThreadFactory factory = new ThreadFactory() {
            AtomicInteger count = new AtomicInteger(0); // int

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Thread-" + count.incrementAndGet()); // ++count
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " started!");
            }
        };

        Thread thread = factory.newThread(runnable);
        thread.start();
        Thread thread1 = factory.newThread(runnable);
        thread1.start();
    }

    /**
     * Runnable + ThreadPoolExecutor（线程池执行程序）
     * 使用线程池创建线程
     */
    static void executor() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread with Runnable started!");
            }
        };

        Executor executor = Executors.newCachedThreadPool();
        executor.execute(runnable);
        executor.execute(runnable);
        executor.execute(runnable);

        ExecutorService myExecutor = new ThreadPoolExecutor(5, 100,
                5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

        myExecutor.execute(runnable);
    }

    /**
     * 实现callable接口，有返回值
     */
    static void callable() {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Done!";
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<String> future = executor.submit(callable);
        while (true) {
            if (future.isDone()) {
                try {
                    String result = future.get();
                    System.out.println("result: " + result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 实现Callable接口
     * Callable与Runable类似，它是JDK1.5之后才出现的。
     * 与Thread区别：相比于callable来说,thread没有返回值,且效率没有callable高
     * 与Runnable区别：
     * 可以有返回值
     * 可以抛出异常
     * 方法不同，分别是call()和run()
     * */
    static void callable2() throws Exception{
        Callable<String> myCallable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("MyCallable.call");
                return "abc";
            }
        };

        //需要适配类和Thread建立关联
        FutureTask<String> futureTask = new FutureTask<>(myCallable);
        Thread thread = new Thread(futureTask, "CallableDemo");
        thread.start();
        String str = futureTask.get();
        System.out.println(str);
    }

    static void runSynchronized1Demo() {
        new Synchronized1Demo().runTest();
    }

    static void runSynchronized2Demo() {
        new Synchronized2Demo().runTest();
    }

    static void runSynchronized3Demo() {
        new Synchronized3Demo().runTest();
    }

    static void runReadWriteLockDemo() {
        new ReadWriteLockDemo().runTest();
    }
}
