package cn.longmaster.ihuvc.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.longmaster.doctorlibrary.utils.UtilStatus;
import cn.longmaster.doctorlibrary.utils.log.Logger;

public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();
    private static final int OPTIONS_NONE = 0x0;
    private static final int OPTIONS_SCALE_UP = 0x1;
    @SuppressWarnings("unused")
    private static int mScreenWidth = -1;
    @SuppressWarnings("unused")
    private static int mScreenHeight = -1;

    /**
     * Constant used to indicate we should recycle the input in
     * {@link #extractThumbnail(Bitmap, int, int, int)} unless the output is the
     * input.
     */
    public static final int OPTIONS_RECYCLE_INPUT = 0x2;

    /**
     * Creates a centered bitmap of the desired size.
     *
     * @param source original bitmap source
     * @param width  targeted width
     * @param height targeted height
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height) {
        return extractThumbnail(source, width, height, OPTIONS_NONE);
    }

    /**
     * Creates a centered bitmap of the desired size.
     *
     * @param source  original bitmap source
     * @param width   targeted width
     * @param height  targeted height
     * @param options options used during thumbnail extraction
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height, int options) {
        if (source == null) {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap thumbnail = transform(matrix, source, width, height, OPTIONS_SCALE_UP | options);
        return thumbnail;
    }

    /**
     * Transform source Bitmap to targeted width and height.
     */
    private static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, int options) {
        boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
        boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()), deltaYHalf
                    + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
            c.drawBitmap(source, src, dst, null);
            if (recycle) {
                source.recycle();
            }
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1 = null;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            try {
                b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }
        } else {
            b1 = source;
        }

        if (recycle && b1 != source) {
            source.recycle();
        }

        if (b1 == null) // modify by zj 2013-05-10
        {
            return null;
        }
        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

        if (b2 != b1) {
            if (recycle || b1 != source) {
                b1.recycle();
            }
        }
        return b2;
    }

    /**
     * 使用Bitmap加Matrix来缩放,缩放到指定宽高
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options a_oOptions, int a_iMinSideLength, int a_iMaxNumOfPixels) {
        double w = a_oOptions.outWidth;
        double h = a_oOptions.outHeight;

        int lowerBound = (a_iMaxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / a_iMaxNumOfPixels));
        int upperBound = (a_iMinSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / a_iMinSideLength), Math.floor(h / a_iMinSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((a_iMaxNumOfPixels == -1) && (a_iMinSideLength == -1)) {
            return 1;
        } else if (a_iMinSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 获取灰度图片
     *
     * @param original 原始图片
     * @return 灰度处理后的图片
     */
    public static Bitmap toGrayscale(Bitmap original) {
        int width, height;
        height = original.getHeight();
        width = original.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(original, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * 根据高宽比缩放图片
     *
     * @param bitmap
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int viewWidth, int viewHeight) {

        if (bitmap == null || viewWidth == 0 || viewHeight == 0) {
            return bitmap;
        }
        try {
            int mapWidth = bitmap.getWidth();
            int mapHeigh = bitmap.getHeight();
            float scale = 0;

            if ((float) mapWidth / (float) mapHeigh < (float) viewWidth / (float) viewHeight) {
                scale = (float) viewWidth / (float) mapWidth;
            } else {
                scale = (float) viewHeight / (float) mapHeigh;
            }

            if (scale < 1.0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (mapWidth * scale), (int) (mapHeigh * scale), true);
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 根据质量压缩图片
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static final Bitmap getCompressBitmap(Bitmap bm, int maxWidth, int maxHeight) {
        if (maxWidth < 150) {
            maxWidth = 150;
        }
        if (maxHeight < 150) {
            maxHeight = 150;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;

        int roundedSize = 1, picWidth = bm.getWidth(), picHeight = bm.getHeight();

        if (picWidth > maxWidth || picHeight > maxHeight) {
            int scaleWidth = (int) Math.ceil((float) picWidth / maxWidth);
            int scaleHeight = (int) Math.ceil((float) picHeight / maxHeight);
            roundedSize = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
        }
        opts.inSampleSize = roundedSize;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            bm = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, opts);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 根据指定的最大宽度和最大高度读取图像
     *
     * @param aPath
     * @param aMaxWidth
     * @param aMaxHeight
     * @return
     */
    public static Bitmap decodeFromPath(String aPath, int aMaxWidth, int aMaxHeight) {
        File lBitmapFile = new File(aPath);
        if (!lBitmapFile.exists() || !lBitmapFile.isFile()) {
            lBitmapFile = null;
            return null;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(aPath, opts);

        opts.inSampleSize = computeSampleSize(opts, aMaxWidth > aMaxHeight ? aMaxHeight : aMaxWidth, aMaxWidth * aMaxHeight);
        opts.inJustDecodeBounds = false;

        Bitmap resultBitmap = null;

        try {
            resultBitmap = BitmapFactory.decodeFile(aPath, opts);
        } catch (OutOfMemoryError oom) {
            // 没有足够的内存来读取图像
        }

        return resultBitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }


    /**
     * Drawable转Bitmap
     *
     * @param drawable Drawable对象
     * @return 转换后的Bitmap对象
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将指定路径图片文件按指定大小缩放解码为Bitmap对象
     *
     * @param path      图片路径
     * @param reqWidth  缩放宽度
     * @param reqHeight 缩放高度
     * @return 缩放后的Bitmap对象
     */
    public static Bitmap decodeSampledFile(String path, int reqWidth, int reqHeight, Bitmap.Config config) {
        if (TextUtils.isEmpty(path) || reqWidth <= 0 || reqHeight <= 0) {
            return null;
        }

        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);

        opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
        opts.inJustDecodeBounds = false;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = config;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path, opts);

            int degree = readPictureDegree(path);
            if (degree != 0 && bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.setRotate(degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 计算BitmapFactory.Options对象中inSampleSize（缩放比例）值
     *
     * @param options   BitmapFactory.Options 对象
     * @param reqWidth  缩放宽度
     * @param reqHeight 缩放高度
     * @return inSampleSize（缩放比例）值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static float getScaleRate(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        if (srcWidth > dstWidth || srcHeight > dstHeight) {
            float scaleWidth = (float) dstWidth / (float) srcWidth;
            float scaleHeight = (float) dstHeight / (float) srcHeight;
            return Math.min(scaleWidth, scaleHeight);
        }
        return 1;
    }

    /**
     * 获取原长宽比例圆角图片
     *
     * @param bitmap   原始图片
     * @param a_bIsBig 是否是大圆度
     * @return 圆角图片
     */
    public static Bitmap getOriginalShapeRoundedCornerBitmap(Bitmap bitmap, boolean a_bIsBig) {
        if (bitmap == null)
            return null;

        Bitmap output = null;

        try {
            int l_iScale = 0;

            if (a_bIsBig)
                l_iScale = 24;
            else
                l_iScale = 12;

            float fRound = bitmap.getWidth() / l_iScale;
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, fRound, fRound, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * 生成一个新的正方形的圆角图片。如果生成成功，原位图将被释放，否则返回原位图。
     *
     * @param bitmap 原位图
     * @param radius 圆角半径
     * @return
     */
    public static Bitmap getRoundedSquareBitmap(Bitmap bitmap, float radius) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        final boolean useWidth = width < height;
        int sideLength = useWidth ? width : height;

        final int offsetX = (int) (useWidth ? 0 : (width - sideLength) / 2.0f);
        final int offsetY = (int) (useWidth ? (height - sideLength) / 2.0f : 0);

        return getRoundedSquareBitmap(bitmap, sideLength, radius, offsetX, offsetY);
    }

    /**
     * 生成一个新的正方形的圆角图片。如果生成成功，原位图将被释放，否则返回原位图。
     *
     * @param bitmap     原位图
     * @param sideLength 边长
     * @param radius     圆角半径
     * @param offsetX    X轴偏移量
     * @param offsetY    Y轴偏移量
     * @return
     */
    public static Bitmap getRoundedSquareBitmap(Bitmap bitmap, int sideLength, float radius, int offsetX, int offsetY) {
        if (bitmap == null) {
            return null;
        }

        try {
            Bitmap result = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(result);
            final Paint paint = new Paint();

            final Rect src = new Rect(offsetX, offsetY, offsetX + sideLength, offsetY + sideLength);
            final Rect dst = new Rect(0, 0, sideLength, sideLength);
            final RectF roundRect = new RectF(dst);

            final int color = 0xff808080;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(roundRect, radius, radius, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            canvas.drawBitmap(bitmap, src, dst, paint);

            if (result != null) {
                bitmap.recycle();
                bitmap = result;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap cutBitmapNoAmplification(Bitmap sec, int viewWidth, int viewHeight) throws Exception {
        if (sec == null) {
            throw new Exception("catBitmap：原图不能为null！！！！");
        }

        if (viewWidth <= 0 || viewHeight <= 0) {
            throw new Exception("catBitmap：图片裁剪：宽度或者高度不能小于0！！！！！");
        }

        float newWidth;
        float newHeight;

        float scale = 1f;
        if (sec.getWidth() < viewWidth && sec.getHeight() < viewHeight) {
            int srcWidth = sec.getWidth();
            int srcHeight = sec.getHeight();

            if ((float) srcWidth / (float) srcHeight > (float) viewWidth / (float) viewHeight) {
                scale = (float) viewHeight / (float) srcHeight;
            } else {
                scale = (float) viewWidth / (float) srcWidth;
            }
        }

        newWidth = viewWidth / scale;
        newHeight = viewHeight / scale;

        return cutBitmap(sec, (int) newWidth, (int) newHeight);
    }

    /**
     * 缩放图片大小到合适
     *
     * @param sec        原图
     * @param viewWidth  要求宽高
     * @param viewHeight
     * @return
     * @throws Exception
     */
    public static Bitmap transBitmapAmplification(Bitmap sec, int viewWidth, int viewHeight) throws Exception {
        if (sec == null) {
            if (UtilStatus.isDebugMode())
                throw new Exception("catBitmap：原图不能为null！！！！");
            return sec;
        }

        if (viewWidth <= 0 || viewHeight <= 0) {
            if (UtilStatus.isDebugMode())
                throw new Exception("catBitmap：图片缩放：宽度或者高度不能小于0！！！！！");
            return sec;
        }

        float scaleW = 1f;
        float scaleH = 1f;
        if (sec.getWidth() > viewWidth) {
            scaleW = (float) viewWidth / (float) sec.getWidth();
        }
        if (sec.getHeight() > viewHeight) {
            scaleH = (float) viewHeight / (float) sec.getHeight();
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(sec, 0, 0, sec.getWidth(), sec.getHeight(), matrix, true);
        return resizeBmp;

    }

    /**
     * 图片裁剪方法
     *
     * @param sec    原图
     * @param width  裁剪宽度
     * @param height 裁剪高度
     * @return
     * @throws Exception 内存溢出或者是参数不合法都会抛出异常
     */
    public static Bitmap cutBitmap(Bitmap sec, int width, int height) throws Exception {
        if (sec == null) {
            throw new Exception("catBitmap：原图不能为null！！！！");
        }

        if (width <= 0 || height <= 0) {
            throw new Exception("catBitmap：图片裁剪：宽度或者高度不能小于0！！！！！");
        }

        int srcWidth = sec.getWidth();
        int srcHeight = sec.getHeight();

        float scale = 1f;

        if ((float) srcWidth / (float) srcHeight > (float) width / (float) height) {
            scale = (float) height / (float) srcHeight;
        } else {
            scale = (float) width / (float) srcWidth;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(-(srcWidth * scale - width) / 2, -(srcHeight * scale - height) / 2);

        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(sec, matrix, paint);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        if (bitmap == null) {
            throw new Exception("catBitmap：内存溢出了！！！！！！！");
        }

        return bitmap;
    }

    /**
     * 旋转图像。如果操作成功并生成一个新的图像，旧图像将被释放
     */
    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        return rotateAndMirror(bitmap, degrees, false);
    }

    /**
     * 图像旋转和镜像。如果操作成功并生成一个新的图像，旧图像将被释放
     */
    public static Bitmap rotateAndMirror(Bitmap aBitmap, int aDegrees, boolean aMirror) {
        if ((aDegrees != 0 || aMirror) && aBitmap != null) {
            Matrix m = new Matrix();
            // Mirror first.
            // horizontal flip + rotation = -rotation + horizontal flip
            if (aMirror) {
                m.postScale(-1, 1);
                aDegrees = (aDegrees + 360) % 360;
                if (aDegrees == 0 || aDegrees == 180) {
                    m.postTranslate(aBitmap.getWidth(), 0);
                } else if (aDegrees == 90 || aDegrees == 270) {
                    m.postTranslate(aBitmap.getHeight(), 0);
                } else {
                    throw new IllegalArgumentException("Invalid degrees=" + aDegrees);
                }
            }
            if (aDegrees != 0) {
                // clockwise
                m.postRotate(aDegrees, (float) aBitmap.getWidth() / 2, (float) aBitmap.getHeight() / 2);
            }

            try {
                Bitmap b2 = Bitmap.createBitmap(aBitmap, 0, 0, aBitmap.getWidth(), aBitmap.getHeight(), m, true);
                if (aBitmap != b2) {
                    aBitmap.recycle();
                    aBitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return aBitmap;
    }

    public static Bitmap rotate(Bitmap bitmap, String filePath) {
        Bitmap res = bitmap;
        if (filePath != null) {
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                String orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                Logger.logI(Logger.COMMON, filePath + ", " + ExifInterface.TAG_ORIENTATION + ": " + orientation);
                Matrix m = new Matrix();
                switch (Integer.parseInt(orientation)) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        m.setRotate(90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        m.setRotate(180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        m.setRotate(270);
                        break;

                    default:
                        return res;
                }
                res = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), m, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static Bitmap compound(Bitmap source, Drawable compound, int left, int top, int right, int bottom) {
        Bitmap result = null;
        // try
        // {
        result = source;

        Canvas canvas;
        try {
            canvas = new Canvas(result);
        } catch (IllegalStateException ile) {
            result = source.copy(source.getConfig(), true);
            canvas = new Canvas(result);
        }

        Drawable temp = compound.mutate();

        temp.setBounds(left, top, right, bottom);
        temp.draw(canvas);

        return result;
    }

    /**
     * 抠图 从Bitmap中抠出一块图片
     *
     * @param srcBitmap 原图
     * @param rect      抠图区域
     * @return 抠好的图片 or 抠图失败返回null
     */
    public static Bitmap cutBitmap(Bitmap srcBitmap, Rect rect) {
        if (srcBitmap == null || rect == null)
            return null;

        Bitmap result = null;
        try {
            int left = rect.left > 0 ? rect.left : 0;
            int top = rect.top > 0 ? rect.top : 0;

            int width = Math.min(rect.width(), srcBitmap.getWidth() - left);
            int height = Math.min(rect.height(), srcBitmap.getHeight() - top);

            result = Bitmap.createBitmap(srcBitmap, left, top, width, height);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将bitmap转换为Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable getDrawable(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable(UtilStatus.getApplication().getResources(), bitmap);
        bitmapDrawable.setAntiAlias(true);
        bitmapDrawable.setFilterBitmap(true);
        return bitmapDrawable;
    }

    /**
     * 获取带动画的Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable getTransitionDrawable(Bitmap bitmap) {
        Drawable bitmapDrawable = getDrawable(bitmap);
        Drawable currentDrawable = new ColorDrawable(Color.TRANSPARENT);
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{currentDrawable, bitmapDrawable});
        transitionDrawable.setCrossFadeEnabled(true);
        transitionDrawable.startTransition(600);
        return transitionDrawable;
    }

    /**
     * 获取带动画的Drawable
     *
     * @param drawable
     * @return
     */
    public static Drawable getDefaultTransitionDrawable(Drawable drawable) {
        Drawable currentDrawable = new ColorDrawable(Color.TRANSPARENT);
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{currentDrawable, drawable});
        transitionDrawable.setCrossFadeEnabled(true);
        transitionDrawable.startTransition(600);
        return transitionDrawable;
    }

    /**
     * 根据图片， 计算抠图区域
     *
     * @param bitmap
     * @return
     */
    public static Rect getDefaultRect(Bitmap bitmap) {
        float srcWidth = bitmap.getWidth();
        float srcHeight = bitmap.getHeight();

        float width;
        float height;

        if (srcWidth / srcHeight > 2.0f / 3.0f) {
            height = srcHeight;
            width = height * 2.0f / 3.0f;
        } else {
            width = srcWidth;
            height = width * 3.0f / 2.0f;
        }

        float left = (srcWidth - width) / 2;
        float top = (srcHeight - height) / 2;
        float right = left + width;
        float bottom = top + height;
        Rect rect = new Rect((int) left, (int) top, (int) right, (int) bottom);
        return rect;
    }

    /**
     * 将png图转成jpeg并保存
     *
     * @param oldFilePath png文件绝对路径
     * @param newFilePath jpeg文件绝对路径
     */
    public static void savePNG2JPEG(String oldFilePath, String newFilePath) {
        try {
            FileUtil.makeDirs(newFilePath);
            File file = new File(oldFilePath);
            FileInputStream fis = new FileInputStream(file);
            int size = fis.available();
            fis.close();
            Logger.logI(Logger.COMMON, "savePNG2JPEG()->file: " + oldFilePath + "  size: " + size);
            Bitmap bitmap = BitmapFactory.decodeFile(oldFilePath);
            FileOutputStream outputStream = new FileOutputStream(newFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            bitmap.recycle();
            bitmap = null;
        } catch (OutOfMemoryError | IOException e) {
            e.printStackTrace();
        }
    }

    public static void compressImageFile(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            int size = fis.available();
            fis.close();
            Logger.logI(Logger.COMMON, "compressImageFile()->file: " + filePath + "  size: " + size);
            if (size > 1024 * 1024 * 5) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, opts);
                opts.inSampleSize = computeSampleSize(opts, -1, size);
                opts.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts);
                FileUtil.deleteFile(filePath);

                int quality = 100;
                if (size > 1024 * 1024 * 10) {
//                    quality = (int) (((1024 * 1024 * 10.0) / size) * 100);
                    quality = 90;
                }
                FileOutputStream outputStream = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.close();
                bitmap.recycle();
                bitmap = null;
            }
        } catch (OutOfMemoryError | IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromPath(String path) {
        try {
            InputStream is = new FileInputStream(path);
            //2.为位图设置100K的缓存
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inTempStorage = new byte[100 * 1024];
            //3.设置位图颜色显示优化方式
            //ALPHA_8：每个像素占用1byte内存（8位）
            //ARGB_4444:每个像素占用2byte内存（16位）
            //ARGB_8888:每个像素占用4byte内存（32位）
            //RGB_565:每个像素占用2byte内存（16位）
            //Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。但同样的，占用的内存//也最大。也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4 bytes //=30M。如此惊人的数字！哪怕生命周期超不过10s，Android也不会答应的。
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            //4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
            opts.inPurgeable = true;
            //5.设置位图缩放比例
            //width，hight设为原来的四分一（该参数请使用2的整数倍）,这也减小了位图占用的内存大小；例如，一张//分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为//512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为//ARGB_8888)。
            opts.inSampleSize = 4;
            //6.设置解码位图的尺寸信息
            opts.inInputShareable = true;
            //7.解码位图
            return BitmapFactory.decodeStream(is, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
