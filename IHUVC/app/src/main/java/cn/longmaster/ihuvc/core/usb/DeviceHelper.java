package cn.longmaster.ihuvc.core.usb;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Toast;

import cn.longmaster.ihuvc.R;

/**
 * 获取外置摄像头驱动，在显示图像的activity中要实现本类中的CameraDialogParent接口，将USBMonitor传递给本类，
 * 然后通过USBMonitor将得到的device作为参数去请求连接
 * @see DeviceHelper
 * */
public class DeviceHelper {
	
	private Context context;
	protected USBMonitor mUSBMonitor;
	private DeviceListAdapter mDeviceListAdapter;
	
	public interface CameraDialogParent {
		public USBMonitor getUSBMonitor();
	}
	
	public DeviceHelper(Context context) {
		this.context = context;
	}

	public void requestDevices() {
//		mUSBMonitor.dumpDevices();
		mUSBMonitor = ((CameraDialogParent)context).getUSBMonitor();
		if (mUSBMonitor != null) {
			final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(context, R.xml.device_filter);
			mDeviceListAdapter = new DeviceListAdapter(context, mUSBMonitor.getDeviceList(filter.get(0)));
			try {
				UsbDevice device = mDeviceListAdapter.getItem(0);
				if (device != null) {
					mUSBMonitor.requestPermission(device);
				}
			} catch (Exception e) {
				Toast.makeText(context, "未检测到摄像头驱动，请插拔重试！", Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	private static final class DeviceListAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;
		private final List<UsbDevice> mList;

		public DeviceListAdapter(final Context context, final List<UsbDevice>list) {
			mInflater = LayoutInflater.from(context);
			mList = list != null ? list : new ArrayList<UsbDevice>();
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public UsbDevice getItem(final int position) {
			if ((position >= 0) && (position < mList.size()))
				return mList.get(position);
			else
				return null;
		}

		@Override
		public long getItemId(final int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.listitem_device, parent, false);
			}
			if (convertView instanceof CheckedTextView) {
				final UsbDevice device = getItem(position);
				((CheckedTextView)convertView).setText(
					String.format("UVC Camera:(%x:%x:%s)", device.getVendorId(), device.getProductId(), device.getDeviceName()));
			}
			return convertView;
		}
	}
}
