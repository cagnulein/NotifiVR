package com.example.vrphone;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class ListViewItemViewHolder extends RecyclerView.ViewHolder {
    private CheckBox itemCheckbox;
    private TextView itemTextView;

    public ListViewItemViewHolder(View view) {
        super(view);
    }

    public CheckBox getItemCheckbox() {
        return this.itemCheckbox;
    }

    public void setItemCheckbox(CheckBox checkBox) {
        this.itemCheckbox = checkBox;
    }

    public TextView getItemTextView() {
        return this.itemTextView;
    }

    public void setItemTextView(TextView textView) {
        this.itemTextView = textView;
    }
}
