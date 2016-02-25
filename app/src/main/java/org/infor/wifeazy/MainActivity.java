package org.infor.wifeazy;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    ActionBar actionBar;

    WifiManager wifi;

    ListView lv;
    int size = 0;
    List<ScanResult> results;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<>();
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        setTitle("WiFi 列表");
        setContentView(R.layout.activity_main);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_action_name);
        lv = (ListView)findViewById(R.id.wifiList);
        wifi = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) wifiDialog();
        this.adapter = new SimpleAdapter(MainActivity.this, arraylist, R.layout.row,
                new String[] { "key" },
                new int[] { R.id.list_value });
        lv.setAdapter(this.adapter);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                results = wifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        getWifiList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getWifiList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void wifiDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setTitle("ここが世界の圏外......か？")
                .setMessage("偵測到 WiFi 未被開啟, 是否開啟 WiFi ?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wifi.setWifiEnabled(true);
                        Toast.makeText(getApplicationContext(),
                                "將開啟 WiFi !", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),
                                "未連接！", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void getWifiList() {
        arraylist.clear();
        wifi.startScan();

        Toast.makeText(this, "掃描中...發現" + size + "個有效的 WiFi !", Toast.LENGTH_SHORT).show();
        try {
            --size;
            while (size >= 0) {
                HashMap<String, String> item = new HashMap<>();
                item.put("key", results.get(size).SSID + "  " + results.get(size).capabilities);
                arraylist.add(item);
                --size;
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            // crash
        }
    }

}
