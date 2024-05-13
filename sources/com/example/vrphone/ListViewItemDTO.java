package com.example.vrphone;

public class ListViewItemDTO {
    private boolean checked = false;
    private String itemText = BuildConfig.FLAVOR;
    private String pnameText = BuildConfig.FLAVOR;

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean z) {
        this.checked = z;
    }

    public String getItemText() {
        return this.itemText;
    }

    public void setItemText(String str) {
        this.itemText = str;
    }

    public String getPnameText() {
        return this.pnameText;
    }

    public void setPnameText(String str) {
        this.pnameText = str;
    }
}
