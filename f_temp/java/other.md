# 1、String a = new String(“abc”); 创建了几个对象？String a = “abc”; 呢？
答案：String a = new String(“abc”); 创建了1个或2个对象；String a = “abc”; 创建了0个或1个都对象

String a = new String(“abc”); 创建过程
首先在堆中创建一个实例对象new String， 并让a引用指向该对象。（创建第1个对象）
JVM拿字面量"abc"去字符串常量池试图获取其对应String对象的引用。
若存在，则让堆中创建好的实例对象new String引用字符串常量池中"abc"。（只创建1个对象的情况）
若不存在，则在堆中创建了一个"abc"的String对象，并将其引用保存到字符串常量池中，然后让实例对象new String
引用字符串常量池中"abc"（创建2个对象的情况）
String a = “abc”; 创建过程

首先JVM会在字符串常量池中查找是否存在内容为"abc"字符串对应String对象的引用。
若不存在，则在堆中创建了一个"abc"的String对象，并将其引用保存到字符串常量池中。（创建1个对象的情况）
若存在，则直接让a引用字符串常量池中"abc"。（创建0个对象的情况）

# 2、android中能不能new Activity()对象引发的思考
Activity也只是一个普通类，它的父类的父类的...父类也是Object，当然可以通过new来创建一个Activity对象，只
是Android系统中是通过类加载器的newInstance()方法来创建Activity实例的。Android系统为了便于管理Activity，
回调其生命周期，把Activity实例化相关操作都封装好了，我们只要轻松调用startActivity操作，就能实现Activity
中各种生命周期的回调了。
因为Android需要统一管理所有Activity及其生命周期回调，Android支持跨进程启动其他应用的Activity，如果发现要
启动的Activity组件的进程不存在是，会由AMS所在的SystemServer进程触发Zgote进程，fork出一个子进程作为新的
Activity组件的进程。AMS为了统一管理，不管是不是同一个进程中的Activity组件启动，都要到AMS中，告诉它要进
行什么操作。
使用Class.forName( )静态方法的目的是为了动态加载类。
在加载完成后，一般还要调用Class下的newInstance()静态方法来实例化对象以便操作。

首先，newInstance()是一个方法，而new是一个关键字。
其次，Class下的newInstance()的使用有局限，因为它生成对象只能调用无参的构造函数，而使用new关键字生成对象
没有这个限制。
newInstance()实际上是把new这个方式分解为两步，即首先调用Class加载方法加载某个类，然后实例化。 这样分步的
好处是显而易见的。我们可以在调用class的静态加载方法forName时获得更好的灵活性，提供给了一种降耦的手段。

