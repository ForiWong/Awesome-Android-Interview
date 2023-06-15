package com.wlp.myanjunote.ot;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.wlp.myanjunote.R;

public class BitmapActivity extends AppCompatActivity {
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLruCache();

        ListView listView = (ListView) findViewById(R.id.view);
        listView.setAdapter(new MyAdapter());
    }

    /**
     * 初始化缓存
     */
    private void initLruCache() {
        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(BitmapActivity.this, R.layout.activity_main, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//          imageView.setImageResource(R.drawable.girl);//完全显示图片大小,ListView 滑动界面异常卡顿

            String imageKey = String.valueOf(R.drawable.girl);//得到资源id的唯一标示
            Bitmap bitmap = mMemoryCache.get(imageKey);//从lruCache中取出该缓存
            //如果缓存中有该资源
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                //对非常大的图片进行压缩显示(压缩成100*100显示),以适合的宽高显示大小
                Bitmap bitmap2 = decodeSampleBitmapFromResource(getResources(), R.drawable.girl, 100, 100);
                imageView.setImageBitmap(bitmap2);
                mMemoryCache.put(imageKey, bitmap2);//添加到缓存中
            }
            return view;
        }
    }

    /*计算出合适的inSampleSize值：*/
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //源图片的高度和宽度
        final int height = options.outHeight;//得到要加载的图片高度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            //计算出实际宽高和目标宽高的比例
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        //第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        //将这个参数的inJustDecodeBounds属性设置为true就可以让解析方法禁止为bitmap分配内存
        options.inJustDecodeBounds = true;

        //解析源图片,返回一个 bitmap 对象,当 options.inJustDecodeBounds = true;
        /*禁止为bitmap分配内存，返回值也不再是一个Bitmap对象，而是null。虽然Bitmap是null了，
         但是BitmapFactory.Options的outWidth、outHeight和outMimeType属性都会被赋值。
         这个技巧让我们可以在加载图片之前就获取到图片的长宽值和MIME类型，从而根据情况对图片进行压缩*/
        BitmapFactory.decodeResource(res, resId, options);

        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;

        /*计算完inSampleSize 的合适大小后,需要把 options.inJustDecodeBounds = false;
        然后把再 BitmapFactory.decodeResource(res,resId,options)
        此时  options.inJustDecodeBounds = false; ,BitmapFactory.decodeResource() 方法返回一个bitmap对象给 imageView.setImageBitmap()方法
        从而显示一个合适大小的图片
        */
        return BitmapFactory.decodeResource(res,resId,options);
    }
}
