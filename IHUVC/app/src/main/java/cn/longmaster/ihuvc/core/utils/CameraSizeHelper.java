package cn.longmaster.ihuvc.core.utils;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

public class CameraSizeHelper {

	/**
	 * 获取设备Camera支持的分辨率
	 * 
	 * */
	public static String getCameraSupportSize() {
		StringBuffer sb = new StringBuffer();
		try {
			Camera mCamera = Camera.open();
			Parameters p =  mCamera.getParameters();
			List<Size> list = p.getSupportedPreviewSizes();
			for (int i = 0; i < list.size(); i++) {
				sb.append(list.get(i).width + " * " + list.get(i).height + ",");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
		
	}
}
