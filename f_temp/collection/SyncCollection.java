package com.wlp.myanjunote.kotlin.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * java线程安全集合操作
 * <p>
 * 线程安全常见的集合三个：
 * Vector 功能同ArrayList，但是线程安全的。
 * HashTable 是线程安全的；HashTable 内部的方法基本都经过synchronized 修饰，HashtableEntry数组 + 链表。
 * ConcurrentHashMap是HashMap的线程安全版（自JDK1.5引入），提供比Hashtable更高效的并发性能。采用分离锁，高效。
 * Vector和HashTable都是很少使用了，过时。
 **/
public class SyncCollection {

    private void list() {
        // 并发下ArrayList是不安全的
        List<String> lst = new ArrayList<>();
        // java.util.ConcurrentModificationException  并发修改异常
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                lst.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(lst);
            }, String.valueOf(i)).start();
        }

        //1.  使用vector， vector是线程安全的
        List<String> lst1 = new Vector();

        /**
         * 将非线程安全 List 对象，封装成一个线程安全的 List 对象，处理 List 上的并发性问题。类似一个工具类，减少开发人员的重复性工作。
         * SynchronizedList 的实现里，get, set, add 等操作都加了 mutex 对象锁，再将操作委托给最初传入的 list。
         * */
        //2. 使用Collections.synchronizedList
        List<String> lst2 = Collections.synchronizedList(new ArrayList<>());

        /**
         CopyOnWriteArrayList原理

         JDK 中提供了 CopyOnWriteArrayList 类。为了将读取的性能发挥到极致，CopyOnWriteArrayList 读取是完全不用加锁的，
         并且更厉害的是：写入也不会阻塞读取操作。只有写入和写入之间需要进行同步等待。这样一来，读操作的性能就会大幅度
         提升。
         那它是怎么做的呢？

         CopyOnWriteArrayList 类的所有可变操作（add，set 等等）都是通过创建底层数组的新副本来实现的。当 List 需要被修
         改的时候，我并不修改原有内容，而是对原有数据进行一次复制，将修改的内容写入副本。写完之后，再将修改完的副本替
         换原来的数据，这样就可以保证写操作不会影响读操作了。

         从 CopyOnWriteArrayList 的名字就能看出CopyOnWriteArrayList 是满足CopyOnWrite 的 ArrayList，所谓CopyOnWrite 也
         就是说：在计算机，如果你想要对一块内存进行修改时，我们不在原有内存块中进行写操作，而是将内存拷贝一份，在新的
         内存中进行写操作，写完之后呢，就将指向原来内存指针指向新的内存，原来的内存就可以被回收掉了。

         CopyOnWriteArrayList 读取操作的实现
         读取操作没有任何同步控制和锁操作，理由就是内部数组 array 不会发生修改，只会被另外一个 array 替换，因此可以保
         证数据安全。

         CopyOnWriteArrayList 写入操作的实现
         CopyOnWriteArrayList 写入操作 add() 方法在添加集合的时候加了锁，保证了同步，避免了多线程写的时候会 copy 出多
         个副本出来。

         优点
         读操作性能很高，因为无需任何同步措施，比较适用于读多写少的并发场景。Java的List在遍历时，若中途有别的线程对
         List容器进行修改，则会抛出ConcurrentModificationException异常。而CopyOnWriteArrayList由于其"读写分离"的思想，
         遍历和修改操作分别作用在不同的List容器，所以在使用迭代器进行遍历时候，也就不会抛出ConcurrentModification
         Exception异常了。

         缺点
         1、由于写操作的时候，需要拷贝数组，会消耗内存。
         2、不能用于实时读的场景，像拷贝数组、新增元素都需要时间，所以调用一个set操作后，读取到数据可能还是旧的,虽然
         CopyOnWriteArrayList 能做到最终一致性,但是还是没法满足实时性要求。
         * */
        //3. 使用CopyOnWriteArrayList  写入时复制
        List<String> lst3 = new CopyOnWriteArrayList<>();
    }

    private void set() {
        Set<String> set = new HashSet<>();
        // java.util.ConcurrentModificationException  并发修改异常
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                set.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(set);
            }, String.valueOf(i)).start();
        }

        //1. 使用Collections.synchronizedSet
        // 原理同 SynchronizedList
        Set<String> set1 = Collections.synchronizedSet(new HashSet<>());

        //2. 使用CopyOnWriteArraySet
        // CopyOnWriteArraySet则是通过“动态数组(CopyOnWriteArrayList)”实现的，并不是散列表。
        Set<String> set2 = new CopyOnWriteArraySet<>();
    }

    private void map() {
        Map<String, String> map = new HashMap<>();
        // java.util.ConcurrentModificationException  并发修改异常
        for (int i = 1; i <= 20; i++) {
            new Thread(() -> {
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 5));
                System.out.println(map);
            }, String.valueOf(i)).start();
        }

        //1. 使用ConcurrentHashMap
        Map<String, String> map1 = new ConcurrentHashMap();
    }

}

