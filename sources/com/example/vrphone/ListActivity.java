package com.example.vrphone;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        final SharedPreferences sharedPreferences = getSharedPreferences("Questnotifier", 0);
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        if (!sharedPreferences.contains("filter")) {
            edit.putBoolean("filter", false);
            edit.commit();
        }
        setContentView((int) R.layout.activity_list_view_with_checkbox);
        getSupportActionBar().setTitle((CharSequence) "Filter Apps");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = (ListView) findViewById(R.id.list_view_with_checkbox);
        final ListViewItemCheckboxBaseAdapter listViewItemCheckboxBaseAdapter = new ListViewItemCheckboxBaseAdapter(getApplicationContext(), getInitViewItemDtoList());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ListViewItemDTO listViewItemDTO = (ListViewItemDTO) adapterView.getItemAtPosition(i);
                listViewItemDTO.setChecked(!listViewItemDTO.isChecked());
                if (listViewItemDTO.isChecked()) {
                    SharedPreferences.Editor editor = edit;
                    editor.putString("filterapps", sharedPreferences.getString("filterapps", BuildConfig.FLAVOR) + "|" + listViewItemDTO.getPnameText());
                    edit.commit();
                } else {
                    SharedPreferences.Editor editor2 = edit;
                    String string = sharedPreferences.getString("filterapps", BuildConfig.FLAVOR);
                    editor2.putString("filterapps", string.replace("|" + listViewItemDTO.getPnameText(), BuildConfig.FLAVOR));
                    edit.commit();
                }
                listViewItemCheckboxBaseAdapter.notifyDataSetChanged();
            }
        });
        listViewItemCheckboxBaseAdapter.notifyDataSetChanged();
        listView.setAdapter(listViewItemCheckboxBaseAdapter);
        Switch switchR = new Switch(this);
        ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{-16842910}, new int[]{16842912}, new int[0]}, new int[]{-16776961, -16711936, Color.argb(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 255, 0, 0)});
        switchR.getThumbDrawable().setTintList(colorStateList);
        switchR.getTrackDrawable().setTintList(colorStateList);
        ActionBar supportActionBar = getSupportActionBar();
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(-2, -2, 8388629);
        switchR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                edit.putBoolean("filter", z);
                edit.commit();
            }
        });
        switchR.setChecked(sharedPreferences.getBoolean("filter", false));
        supportActionBar.setCustomView(switchR, layoutParams);
        supportActionBar.setDisplayShowCustomEnabled(true);
        InitList();
    }

    private final class LongOperation extends AsyncTask<Void, Void, String> {
        /* access modifiers changed from: protected */
        public String doInBackground(Void... voidArr) {
            return "Executed";
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
        }

        private LongOperation() {
        }
    }

    public void InitList() {
        ListViewItemCheckboxBaseAdapter listViewItemCheckboxBaseAdapter = (ListViewItemCheckboxBaseAdapter) ((ListView) findViewById(R.id.list_view_with_checkbox)).getAdapter();
        SharedPreferences sharedPreferences = getSharedPreferences("Questnotifier", 0);
        for (int i = 0; i < listViewItemCheckboxBaseAdapter.getCount(); i++) {
            ListViewItemDTO listViewItemDTO = (ListViewItemDTO) listViewItemCheckboxBaseAdapter.getItem(i);
            if (sharedPreferences.getString("filterapps", BuildConfig.FLAVOR).contains(listViewItemDTO.getPnameText())) {
                listViewItemDTO.setChecked(true);
            }
        }
        listViewItemCheckboxBaseAdapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    private List<ListViewItemDTO> getInitViewItemDtoList() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        ArrayList arrayList = new ArrayList();
        int size = installedApplications.size();
        for (int i = 0; i < size; i++) {
            ApplicationInfo applicationInfo = installedApplications.get(i);
            if (packageManager.getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                ListViewItemDTO listViewItemDTO = new ListViewItemDTO();
                listViewItemDTO.setChecked(false);
                listViewItemDTO.setItemText((String) packageManager.getApplicationLabel(applicationInfo));
                listViewItemDTO.setPnameText(installedApplications.get(i).packageName);
                arrayList.add(listViewItemDTO);
            }
        }
        Collections.sort(arrayList, new Comparator<ListViewItemDTO>() {
            public int compare(ListViewItemDTO listViewItemDTO, ListViewItemDTO listViewItemDTO2) {
                return listViewItemDTO.getItemText().compareToIgnoreCase(listViewItemDTO2.getItemText());
            }
        });
        return arrayList;
    }
}
