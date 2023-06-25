package com.wlp.myanjunote.java.thread.threadsync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 https://www.cnblogs.com/bjlhx/p/7609100.html

 ThreadFactory是一个线程工厂。用来创建线程。这里为什么要使用线程工厂呢？其实就是为了统一在创建线程时设置一些参数，
 如是否守护线程。线程一些特性等，如优先级。通过这个TreadFactory创建出来的线程能保证有相同的特性。它首先是一个接口
 类，而且方法只有一个。就是创建一个线程。

 public interface ThreadFactory {
    Thread newThread(Runnable r);
 }

 Executors.newCachedThreadPool()//封装的线程池
 --> ThreadPoolExecutor
    --> Executors.defaultThreadFactory()
        --> Executors.DefaultThreadFactory//JDK默认实现的线程工厂
            --> implements ThreadFactory//线程工厂接口

 DefaultThreadFactory 作用：它做的事就是统一给线程池中的线程设置线程group、统一的线程前缀名。以及统一的优先级。

 */
public class ThreadFactoryTest {

    static class MyThreadFactory implements ThreadFactory {

        private int counter;
        private String name;
        private List<String> stats;

        public MyThreadFactory(String name) {
            counter = 0;
            this.name = name;
            stats = new ArrayList<String>();
        }

        @Override
        public Thread newThread(Runnable run) {
            Thread t = new Thread(run, name + "-Thread-" + counter);
            counter++;
            stats.add(String.format("Created thread %d with name %s on%s\n",t.getId(), t.getName(), new Date()));
            return t;
        }

        public String getStas() {
            StringBuffer buffer = new StringBuffer();
            Iterator<String> it = stats.iterator();
            while (it.hasNext()) {
                buffer.append(it.next());
                buffer.append("\n");
            }
            return buffer.toString();
        }

    }
    
    static class MyTask implements Runnable {
        
        private int num;
        
        public MyTask(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            System.out.println("Task "+ num+" is running");
            try {
                Thread.sleep(2*10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
    }

    public static void main(String[] args) {
        System.out.println("main thread beging");
        MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");  
 
        Thread thread = null;  
        for(int i = 0; i < 10; i++) {  
            thread = factory.newThread(new MyTask(i));  
            thread.start();  
        }  
        System.out.printf("Factory stats:\n");  
        System.out.printf("%s\n",factory.getStas());  
        System.out.println("main thread end");
    }

}