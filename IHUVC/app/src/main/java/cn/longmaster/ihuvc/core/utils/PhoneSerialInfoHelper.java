package cn.longmaster.ihuvc.core.utils;

public class PhoneSerialInfoHelper {

	public static String getSerial() {
		String serial = "";
		serial = android.os.Build.SERIAL;
		return serial;
	}
}
