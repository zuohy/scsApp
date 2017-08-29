package cn.longmaster.ihuvc.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.longmaster.ihuvc.R;
import cn.longmaster.ihuvc.core.entity.MaterialTypeInfo;

/**
 * Created by WangHaiKun on 2017/5/8.
 */

public class MaterialTypeAdapter extends BaseAdapter {
    private Context context;
    private List<MaterialTypeInfo> listType;
    private LayoutInflater inflater;
    private ViewHolder viewHolder = null;

    public MaterialTypeAdapter(Context context, List<MaterialTypeInfo> listType) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listType = listType;
    }

    @Override
    public int getCount() {
        return listType.size();
    }

    @Override
    public Object getItem(int position) {
        return listType.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.data_type_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mDataTypeTv = (TextView) convertView.findViewById(R.id.data_type_tv);
            viewHolder.mPictureNumberTv = (TextView) convertView.findViewById(R.id.data_type_picture_number_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mDataTypeTv.setText(listType.get(position).getmMaterialTypeName() + "");
        viewHolder.mPictureNumberTv.setText(context.getString(R.string.picture_number, listType.get(position).getmMaterialNum() + ""));
        return convertView;
    }

    class ViewHolder {
        TextView mDataTypeTv;
        TextView mPictureNumberTv;
    }
}
