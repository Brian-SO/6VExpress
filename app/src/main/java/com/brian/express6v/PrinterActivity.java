package com.brian.express6v;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrinterActivity extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    private ListView listView;
    private ImageView imageView;
    public static BluetoothAdapter _myBluetoothAdapter;

    public String BDAddress;//蓝牙设备地址
    public String DeviceName = "";//设备名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        imageView = (ImageView) findViewById(R.id.nav_btn_back);
        listView = (ListView) findViewById(R.id.listview);

        ListBluetoothDevice();
        //顶部后退按钮
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ListBluetoothDevice();
            }else {
                finish();
            }

        }
    }

    //加载已经配对的蓝牙设备在列表
    public void ListBluetoothDevice() {
        final List<Map<String,String>> list = new ArrayList<Map<String, String>>();
        SimpleAdapter m_adapter = new SimpleAdapter( this,list,
                android.R.layout.simple_list_item_2,
                new String[]{"DeviceName","BDAddress"},
                new int[]{android.R.id.text1,android.R.id.text2}
        );
        listView.setAdapter(m_adapter);
        if((_myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
            Toast.makeText(PrinterActivity.this, "没有找到蓝牙适配器", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(!_myBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, PICK_CONTACT_REQUEST);
            return;
        }
        Set<BluetoothDevice> pairedDevices = _myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() <= 0){
            Toast.makeText(PrinterActivity.this, "未找到蓝牙设备，请检查后再链接。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        for (BluetoothDevice device : pairedDevices) {
            Map<String,String> map = new HashMap<String, String>();
            map.put("DeviceName", device.getName());
            map.put("BDAddress", device.getAddress());
            list.add(map);
        }
        //列表点击事件监听
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BDAddress = list.get(i).get("BDAddress");
                DeviceName = list.get(i).get("DeviceName");
                if (((ListView)adapterView).getTag() != null){
                    ((View)((ListView)adapterView).getTag()).setBackgroundDrawable(null);
                }
                Toast.makeText(PrinterActivity.this, BDAddress + "   " + DeviceName, Toast.LENGTH_SHORT).show();
                /*if (DeviceName.length() >= "DP-HT301".length()){
                    if (DeviceName.substring(0, 8).equals("DP-HT301")){
                        DrawerService.addHandler(mHandler);
                        dialog.setMessage("正在连接 " + BDAddress);
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(false);
                        dialog.show();
                        DrawerService.workThread.connectBt(BDAddress);
                    }
                }
                if (DeviceName.length() >= "RG-LP58A".length()){
                    if (DeviceName.substring(0, 8).equals("RG-LP58A")){
                        if (ConnectDevice(BDAddress)== true){
                            Toast.makeText(Demo_ad_escActivity.this, "连接成功",
                                    Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(Demo_ad_escActivity.this, "连接失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }*/
                ((ListView)adapterView).setTag(view);
//                view.setBackgroundColor(Color.BLUE);
            }
        });
    }
}
