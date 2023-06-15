# ActivityThread是什么 
参考 https://blog.csdn.net/zhuzp_blog/article/details/120894309

ActivityThread是应用进程的初始化类，它的main()方法就是应用的入口方法，也就是说应用进程被创建后会调用
ActivityThread.main()方法。
ActivityThread也是我们常说的主线程，但是这种描述不太准确，ActivityThread不是线程，只不过它是在运行在
主线程（main）的main()方法中创建的对象，自然它也是运行在主线程中。
只能说ActivityThread是主线程的一部分，但不并能代表主线程。

main()方法主要做了以下几件事：
初始化Environment；
创建主线程Looper；
创建ActivityThread对象，并调用其attach()方法；
调用Looper.loop()，进入循环，阻塞式等待消息。

一句话概括，ActivityThread管理着四大组件的生命周期方法的调用。

总结：
最后我们稍稍总结下
ActivityThread类是应用初始化类，它的main()方法是应用的入口方法；
ActivityThread不是线程，我们之所以称它为“主线程”，是因为它运行在主线程中；
ActivityThread负责创建Application对象以及管理其生命周期方法调用；
ActivityThread管理着四大组件的生命周期方法调用；

