package cn.longmaster.ihuvc.core.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * 获取屏幕的分辨率帮助类
 * @param context
 * @return width or height
 * @see ScreenUtils
 * */
public class ScreenUtils {
	private int width, height;
	private Context context;
	
	public ScreenUtils(Context context) {
		this.context = context;
		init();
	}

	private void init() {
		WindowManager wm = (WindowManager) context  
                .getSystemService(Context.WINDOW_SERVICE);  
        DisplayMetrics outMetrics = new DisplayMetrics();  
        wm.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Dip转成对应Px值
	 *
	 * @param context 上下文
	 * @param dip
	 * @return
	 */
	public static int dipTopx(Context context, float dip) {
		if (context == null)
			return 0;
		float s = context.getResources().getDisplayMetrics().density;
		return (int) (dip * s + 0.5f);
	}

	//获取屏幕原始尺寸宽度,高度，包括虚拟功能键高度
	public static int[] getDpi(Context context) {
		int[] dpi = new int[2];
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, displayMetrics);
			dpi[0] = displayMetrics.widthPixels;
			dpi[1] = displayMetrics.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dpi;
	}

	/**
	 * 获取 虚拟按键的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getBottomStatusHeight(Context context) {
		int totalHeight = getDpi(context)[1];
		int contentHeight = getScreenHeight(context);
		return totalHeight - contentHeight;
	}

	/**
	 * 获得屏幕高度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得状态栏的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}
}
