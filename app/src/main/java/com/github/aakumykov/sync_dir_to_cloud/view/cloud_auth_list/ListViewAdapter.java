package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.List;

public class ListViewAdapter<T> extends ArrayAdapter<T> {

    private final LayoutInflater inflater;
    @LayoutRes private final int layoutRes;
    @IdRes private final int titleId;
    private final List<T> mList;

    public ListViewAdapter(Context context,
                           @LayoutRes int resource,
                           @IdRes int titleId,
                           List<T> list
    ) {
        super(context, resource, list);

        this.layoutRes = resource;
        this.titleId = titleId;
        this.mList = list;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView==null){
            convertView = inflater.inflate(this.layoutRes, parent, false);
            viewHolder = new ViewHolder(convertView, titleId);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.titleView.setText(mList.get(position).toString());

        return convertView;
    }

    public void setList(List<T> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        final TextView titleView;
        ViewHolder(View view, @IdRes int titleId){
            titleView = view.findViewById(titleId);
        }
    }
}
