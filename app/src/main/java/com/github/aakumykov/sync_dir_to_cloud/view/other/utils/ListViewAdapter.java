package com.github.aakumykov.sync_dir_to_cloud.view.other.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Function;

import java.util.List;

public abstract class ListViewAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater inflater;
    @LayoutRes private int layoutRes;
    @IdRes private int titleId;
    @IdRes private int iconId;
    private List<T> mList;
    @Nullable private Function<T,String> mTitleGetter;

    public abstract void modifyView(T item, ViewHolder viewHolder);

    public ListViewAdapter(Context context,
                           @LayoutRes int layoutResource,
                           @IdRes int titleId,
                           @IdRes int iconId,
                           List<T> list,
                           @Nullable Function<T,String> titleGetter
    ) {
        super(context, layoutResource, list);
        init(context, layoutResource, titleId, iconId, list, titleGetter);
    }

    private void init(Context context,
                      @LayoutRes int layoutResource,
                      @IdRes int titleId,
                      @IdRes int iconId,
                      List<T> list,
                      @Nullable Function<T,String> titleGetter
    ) {
        this.layoutRes = layoutResource;
        this.titleId = titleId;
        this.iconId = iconId;
        this.mList = list;
        this.mTitleGetter = titleGetter;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView==null){
            convertView = inflater.inflate(this.layoutRes, parent, false);
            viewHolder = new ViewHolder(convertView, titleId, iconId);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        T listItem = mList.get(position);

        viewHolder.titleView.setText(
                (null != mTitleGetter) ? mTitleGetter.apply(listItem) : listItem.toString()
        );

        modifyView(listItem, viewHolder);

        return convertView;
    }

    public void setList(List<T> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public final TextView titleView;
        public final ImageView iconView;
        ViewHolder(View view, @IdRes int titleId, @IdRes int iconId){
            titleView = view.findViewById(titleId);
            iconView = view.findViewById(iconId);
        }
    }
}
