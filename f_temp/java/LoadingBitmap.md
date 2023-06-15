# 加载bitmap过程（怎样保证不产生内存溢出）
由于Android对图片使用内存有限制，若是加载几M的大图片便内存溢出。Bitmap会将图片的所有像素（即长x宽）加载到内存中，
如果图片分辨率过大，会直接导致内存OOM，只有在BitmapFactory加载图片时使用BitmapFactory.Options对相关参数进行配置来减少加载的像素。


采样率压缩是通过设置BitmapFactory.Options.inSampleSize，来减小图片的分辨率，进而减小图片所占用的磁盘空间和内存大小。
设置的inSampleSize会导致压缩的图片的宽高都为1/inSampleSize，整体大小变为原始图片的inSampleSize平方分之一，当然，这些有些注意点：
1、inSampleSize小于等于1会按照1处理
2、inSampleSize只能设置为2的平方，不是2的平方则最终会减小到最近的2的平方数。如设置7会按4进行压缩，设置15会按8进行压缩。


Android高效加载大图、多图解决方案，有效避免程序OOM

由于Android加载大图时容易导致OOM，所以应该对大图的加载单独处理，共有3点需要注意：
（1）图片压缩
由于图片的分辨率比手机屏幕分辨率高很多，因此应该根据ImageView控件大小对高分辨率的图片进行适当的压缩，防止OOM出现。
（2）分块加载
如果图片尺寸过大，但指向获取图片的某一小块区域时，可以对图片分块加载。适用于地图绘制的场景。
在Android中BitmapRegionDecoder类的功能就是加载一张图片的指定区域。
// 创建实例
mDecoder = BitmapRegionDecoder.newInstance(mFile.getAbsolutePath(), false);

// 获取原图片宽高
mDecoder.getWidth();
mDecoder.getHeight();

// 加载(10, 10) - (80, 80) 区域内原始精度的Bitmap对象
Rect rect = new Rect(10, 10, 80, 80);
BitmapFactory.Options options = new BitmapFactory.Options();
options.inSampleSize = 1;

Bitmap bitmap = mDecoder.decodeRegion(rect, options);

// 回收释放Native层内存
mDecoder.recycle();

（3）图片三级缓存机制
可以让组件快速地重新加载和处理图片，避免网络加载的性能损耗
图片的三级缓存机制是指加载图片时，分别访问内存、文件和网络而获取图片数据的机制。
一级：内存缓存LruCache
LruCache是Android提供的一个缓存工具类，采用最近最少使用算法。把最近使用的对象用强引用存储在LinkedHashMap中，并把最近最少使用的对象
在缓存值达到预设定值之前从内存中移除。
Android先访问内存，如果内存中没有缓存数据，则访问缓存文件。
二级：文件缓存
DiskLruCache是缓存工具类，存储位置是外存。
缓存数据的存储路径优先考虑SD卡的缓存目录，在SD卡下新建一个缓存文件用来存储缓存数据。若缓存文件中没有缓存数据，则联网加载图片。
三级：联网加载
通过网络请求加载网络图片，并将图片数据保存到内存和缓存文件中。


# Bitmap的高效加载策略
# 原文链接：https://blog.csdn.net/csj731742019/article/details/107707842
一、为什么Bitmap需要高效加载？
现在的高清大图，动辄就要好几M，而Android对单个应用所施加的内存限制，只有小几十M，如16M，这导致加载Bitmap
的时候很容易出现内存溢出。如下图所示，便是在开发中经常遇到的异常信息：

java.lang.OutofMemoryError:bitmap size exceeds VM budget

为了解决这个问题，就出现了Bitmap的高效加载策略。其实核心思想很简单。假设通过ImageView来显示图片，很多时候
ImageView并没有原始图片的尺寸那么大，这个时候把整个图片加载进来后再设置给ImageView，显然是没有必要的，因为
ImageView根本没办法显示原始图片。这时候就可以按一定的采样率来将图片缩小后再加载进来，这样图片既能在ImageView
显示出来，又能降低内存占用从而在一定程度上避免OOM，提高了Bitmap加载时的性能。

注意：有几个库遵循了加载图片的最佳做法。您可以在应用中使用这些库，从而以最优化的方式加载图片。我们建议您使用
Glide 库，该库会尽可能快速、顺畅地加载和显示图片。其他常用的图片加载库包括 Square 的 Picasso、Instacart 的 
Coil 和 Facebook 的 Fresco。这些库简化了与位图和 Android 上的其他图片类型相关的大多数复杂任务。

二、Bitmap高效加载的具体方式
1.加载Bitmap的方式
Bitmap在Android中指的是一张图片。通过BitmapFactory类提供的四类方法：decodeFile,decodeResource,decodeStream和
decodeByteArray,分别从文件系统，资源，输入流和字节数组中加载出一个Bitmap对象，其中decodeFile,decodeResource又
间接调用了decodeStream方法，这四类方法最终是在Android的底层实现的，对应着BitmapFactory类的几个native方法。

2.BitmapFactory.Options的参数
①inSampleSize参数
上述四类方法都支持BitmapFactory.Options参数，而Bitmap的按一定采样率进行缩放就是通过BitmapFactory.Options参数实
现的，主要用到了inSampleSize参数，即采样率。通过对inSampleSize的设置，对图片的像素的高和宽进行缩放。

当inSampleSize=1，即采样后的图片大小为图片的原始大小。小于1，也按照1来计算。
当inSampleSize>1，即采样后的图片将会缩小，缩放比例为1/(inSampleSize的二次方)。

例如：一张1024 ×1024像素的图片，采用ARGB8888格式存储，那么内存大小1024×1024×4=4M。如果inSampleSize=2，那么采
样后的图片内存大小：512×512×4=1M。

注意：官方文档支出，inSampleSize的取值应该总是2的指数，如1，2，4，8等。如果外界传入的inSampleSize的值不为2的指数，
那么系统会向下取整并选择一个最接近2的指数来代替。比如3，系统会选择2来代替。当时经验证明并非在所有Android版本上都成立。

关于inSampleSize取值的注意事项：
通常是根据图片宽高实际的大小/需要的宽高大小，分别计算出宽和高的缩放比。但应该取其中最小的缩放比，避免缩放图片太小，
到达指定控件中不能铺满，需要拉伸从而导致模糊。

例如：ImageView的大小是100×100像素，而图片的原始大小为200×300，那么宽的缩放比是2，高的缩放比是3。如果最终
inSampleSize=2，那么缩放后的图片大小100×150，仍然合适ImageView。如果inSampleSize=3，那么缩放后的图片大小小于
ImageView所期望的大小，这样图片就会被拉伸而导致模糊。

②inJustDecodeBounds参数

我们需要获取加载的图片的宽高信息，然后交给inSampleSize参数选择缩放比缩放。那么如何能先不加载图片却能获得图片的
宽高信息，通过inJustDecodeBounds=true，然后加载图片就可以实现只解析图片的宽高信息，并不会真正的加载图片，所以这
个操作是轻量级的。当获取了宽高信息，计算出缩放比后，然后在将inJustDecodeBounds=false,再重新加载图片，就可以加载缩放后的图片。

注意：BitmapFactory获取的图片宽高信息和图片的位置以及程序运行的设备有关，比如同一张图片放在不同的drawable目录下
或者程序运行在不同屏幕密度的设备上，都可能导致BitmapFactory获取到不同的结果，和Android的资源加载机制有关。

3.高效加载Bitmap的流程
①将BitmapFactory.Options的inJustDecodeBounds参数设为true并加载图片。
②从BitmapFactory.Options中取出图片的原始宽高信息，它们对应于outWidth和outHeight参数。
③根据采样率的规则并结合目标View的所需大小计算出采样率inSampleSize。
④将BitmapFactory.Options的inJustDecodeBounds参数设为false，然后重新加载图片。

# 三、Bitmap高效加载的代码实现

public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    //加载图片
    BitmapFactory.decodeResource(res,resId,options);
    //计算缩放比
    options.inSampleSize = calculateInSampleSize(options,reqHeight,reqWidth);
    //重新加载图片
    options.inJustDecodeBounds =false;
    return BitmapFactory.decodeResource(res,resId,options);
}

private static int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
    int height = options.outHeight;//图片的原始高
    int width = options.outWidth;//图片的原始宽
    int inSampleSize = 1;
    if(height>reqHeight||width>reqWidth){
        int halfHeight = height/2;
        int halfWidth = width/2;
        //计算缩放比，是2的指数
        while((halfHeight/inSampleSize)>=reqHeight&&(halfWidth/inSampleSize)>=reqWidth){
            inSampleSize*=2;
        }
    }
    return inSampleSize;
}
