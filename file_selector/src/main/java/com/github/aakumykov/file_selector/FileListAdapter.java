package com.github.aakumykov.file_selector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.github.aakumykov.cloud_file_lister.file_lister.DirItem;
import com.github.aakumykov.cloud_file_lister.file_lister.FSItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class FileListAdapter extends ArrayAdapter<FSItem> {

    private final LayoutInflater inflater;
    private final int layout;
    private final int titleViewId;
    private final List<FSItem> list;
    private final List<FSItem> selectionsList = new ArrayList<>();

    public FileListAdapter(Context context,
                           @LayoutRes int resource,
                           @IdRes int titleViewId,
                           List<FSItem> list
    ) {
        super(context, resource, list);
        this.list = list;
        this.layout = resource;
        this.titleViewId = titleViewId;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FSItem fsItem = list.get(position);

        String title = (fsItem instanceof DirItem) ? "["+fsItem.getName()+"]" : fsItem.getName();

        if (selectionsList.contains(fsItem))
            title = "*" + title;

        viewHolder.nameView.setText(title);

        return convertView;
    }

    public void updateSelections(@NotNull List<FSItem> list) {
        selectionsList.clear();
        selectionsList.addAll(list);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        final TextView nameView;
        ViewHolder(View view){
            nameView = view.findViewById(titleViewId);
        }
    }
}