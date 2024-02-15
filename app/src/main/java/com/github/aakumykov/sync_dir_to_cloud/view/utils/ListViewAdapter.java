package com.github.aakumykov.sync_dir_to_cloud.view.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Function;

import java.util.List;

public class ListViewAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater inflater;
    @LayoutRes private int layoutRes;
    @IdRes private int titleId;
    private List<T> mList;
    @Nullable private Function<T,String> mTitleGetter;

    public ListViewAdapter(Context context,
                           @LayoutRes int layoutResource,
                           @IdRes int titleId,
                           List<T> list,
                           @Nullable Function<T,String> titleGetter
    ) {
        super(context, layoutResource, list);
        init(context, layoutResource, titleId, list, titleGetter);
    }

    private void init(Context context,
                      @LayoutRes int layoutResource,
                      @IdRes int titleId,
                      List<T> list,
                      @Nullable Function<T,String> titleGetter
    ) {
        this.layoutRes = layoutResource;
        this.titleId = titleId;
        this.mList = list;
        this.mTitleGetter = titleGetter;

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

        viewHolder.titleView.setText(
                (null != mTitleGetter) ? mTitleGetter.apply(mList.get(position)) : mList.get(position).toString()
        );

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
