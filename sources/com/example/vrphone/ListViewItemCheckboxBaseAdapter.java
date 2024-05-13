package com.example.vrphone;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;

public class ListViewItemCheckboxBaseAdapter extends BaseAdapter {
    private Context ctx = null;
    private List<ListViewItemDTO> listViewItemDtoList = null;

    public long getItemId(int i) {
        return (long) i;
    }

    public ListViewItemCheckboxBaseAdapter(Context context, List<ListViewItemDTO> list) {
        this.ctx = context;
        this.listViewItemDtoList = list;
    }

    public int getCount() {
        List<ListViewItemDTO> list = this.listViewItemDtoList;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public Object getItem(int i) {
        List<ListViewItemDTO> list = this.listViewItemDtoList;
        if (list != null) {
            return list.get(i);
        }
        return null;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ListViewItemViewHolder listViewItemViewHolder;
        if (view != null) {
            listViewItemViewHolder = (ListViewItemViewHolder) view.getTag();
        } else {
            view = View.inflate(this.ctx, R.layout.activity_list_view_with_checkbox_item, (ViewGroup) null);
            ListViewItemViewHolder listViewItemViewHolder2 = new ListViewItemViewHolder(view);
            listViewItemViewHolder2.setItemCheckbox((CheckBox) view.findViewById(R.id.list_view_item_checkbox));
            listViewItemViewHolder2.setItemTextView((TextView) view.findViewById(R.id.list_view_item_text));
            view.setTag(listViewItemViewHolder2);
            listViewItemViewHolder = listViewItemViewHolder2;
        }
        ListViewItemDTO listViewItemDTO = this.listViewItemDtoList.get(i);
        listViewItemViewHolder.getItemCheckbox().setChecked(listViewItemDTO.isChecked());
        listViewItemViewHolder.getItemTextView().setText(listViewItemDTO.getItemText());
        return view;
    }
}
