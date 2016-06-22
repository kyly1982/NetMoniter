package com.test.www.netmoniter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/6/16.
 */
public class DataAdapter extends BaseAdapter {
    private ArrayList<ServerInfo> infos;
    private Context mContext;

    public DataAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(ArrayList<ServerInfo> infos){
        this.infos = infos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == infos ? 0:infos.size();
    }

    @Override
    public ServerInfo getItem(int position) {
        return null == infos ? null:infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ServerInfo info = getItem(position);
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_serverinfo, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(info.getName());
        holder.value.setText(info.getValue());

        return convertView;
    }

    public class ViewHolder{
        public TextView name;
        public TextView value;

        public ViewHolder(View itemRoot) {
            name = (TextView) itemRoot.findViewById(R.id.name);
            value = (TextView) itemRoot.findViewById(R.id.value);
        }
    }
}
