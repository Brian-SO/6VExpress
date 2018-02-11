package com.brian.express6v;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.brian.express6v.entity.PrintBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wpx.IBluetoothPrint;
import com.wpx.WPXMain;


/**
 * Created by suboan on 2018/2/8.
 *
 */

public class PrinterActivity extends AppCompatActivity {

    private static final int PICK_CONTACT_REQUEST = 1;  // The request code
    private PrintBean printBean;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ImageView imageView;
    public static BluetoothAdapter _myBluetoothAdapter;
    private IBluetoothPrint iBluetoothPrint;
    private ProgressDialog progressDialog;
    public String BDAddress;//蓝牙设备地址
    public String DeviceName = "";//设备名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        // 获取使用Parcelable传的实体类对象
        printBean = (PrintBean) getIntent().getParcelableExtra("printBean");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        imageView = (ImageView) findViewById(R.id.nav_btn_back);
        listView = (ListView) findViewById(R.id.listview);

        //顶部后退按钮
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //设置下拉刷新卷内的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ListBluetoothDevice();
                        //停止刷新动画
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        ListBluetoothDevice();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK)
                ListBluetoothDevice();
            else
                finish();
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
        //让用户确定是否开启蓝牙
        if(!_myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, PICK_CONTACT_REQUEST);
            return;
        }
        //获取已经绑定的蓝牙设备
        Set<BluetoothDevice> pairedDevices = WPXMain.getBondedDevices();
        if (pairedDevices.size() <= 0){
            AlertDialog.Builder ad1 = new AlertDialog.Builder(PrinterActivity.this);
            ad1.setTitle("未找到蓝牙设备");
            ad1.setMessage("请先配对设备，再下拉刷新");
            ad1.setNegativeButton("确定",null);
            ad1.show();// 显示对话框
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
            public void onItemClick(final AdapterView<?> adapterView, final View view, int i, long l) {
                BDAddress = list.get(i).get("BDAddress");
                DeviceName = list.get(i).get("DeviceName");
                if (((ListView)adapterView).getTag() != null)
                    ((View)((ListView)adapterView).getTag()).setBackgroundDrawable(null);

                new AsyncTask<Void, Void, Boolean>(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        showProgressDialog("正在连接 " + BDAddress, false);
                    }
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        return WPXMain.connectDevice(BDAddress);
                    }
                    @Override
                    protected void onPostExecute(Boolean result) {
                        super.onPostExecute(result);
                        Toast.makeText(getApplicationContext(),
                                result ? "连接成功" : "连接失败",
                                Toast.LENGTH_SHORT).show();
                        dismissProgressDialog();
                        if (result)
                            showPrintCountDialog(Float.parseFloat(printBean.getJshj()));
                    }
                }.execute();
                ((ListView)adapterView).setTag(view);
            }
        });
    }

    //打印张数
    private void showPrintCountDialog(float js) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.printchanges, null);
        final EditText printCount = (EditText) textEntryView.findViewById(R.id.printCount);
        printCount.setText(Math.round(js)+"");//件数四舍五入取整数
        printCount.setSelection(printCount.getText().toString().length());//设置光标位置
        AlertDialog.Builder ad1 = new AlertDialog.Builder(PrinterActivity.this);
        ad1.setTitle("打印数量:");
        ad1.setIcon(R.mipmap.printchange);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("打印", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                final String count = printCount.getText().toString().length() > 0 ?  printCount.getText().toString() : "0";
                if (Integer.parseInt(count) > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            xCasePrint(Integer.parseInt(count));
                        }
                    }).start();

                }else
                    Toast.makeText(PrinterActivity.this,"请输入打印张数", Toast.LENGTH_SHORT).show();
            }
        });
        ad1.setNegativeButton("取消",null);
        ad1.show();// 显示对话框
    }

    //打印
    private void xCasePrint(int count){
        Looper.prepare();
        if (WPXMain.isConnected()) {
            iBluetoothPrint = WPXMain.getBluetoothPrint();//获取打印接口
            for(int i = 0 ; i < count ; i++) {
                //设置标题公司样式
                IBluetoothPrint.Describe companyDes = new IBluetoothPrint.Describe();
                companyDes.setGravity((byte) IBluetoothPrint.GRAVITY_CENTER);//居中
                companyDes.setTextSize(IBluetoothPrint.TEXT_SIZE_B);//字体
                iBluetoothPrint.printText(printBean.getCompany(),companyDes);
                iBluetoothPrint.printText("NO.".concat(printBean.getTydh()),
                        new IBluetoothPrint.Describe().setGravity((byte) IBluetoothPrint.GRAVITY_LEFT));//居左
                iBluetoothPrint.printText(printBean.getTyrq(),
                        new IBluetoothPrint.Describe().setGravity((byte) IBluetoothPrint.GRAVITY_RIGHT));//居右
                iBluetoothPrint.printText("收货人：".concat(printBean.getShr().concat("    电话：".concat(printBean.getShrdh()))));
                iBluetoothPrint.printText("地址：".concat(printBean.getShrdz()));
                iBluetoothPrint.printText("寄件人：".concat(printBean.getFhr().concat("    电话：".concat(printBean.getFhrdh()))));
                iBluetoothPrint.printText("货物名称：".concat(printBean.getHwmc().concat("    件数：".concat(printBean.getJshj()))));
                iBluetoothPrint.printText("付款方式：".concat(printBean.getFkfs().concat("    交接方式：".concat(printBean.getTyfs()))));
                //设置标题备注样式
                IBluetoothPrint.Describe remarkDes = new IBluetoothPrint.Describe();
                remarkDes.setGravity((byte) IBluetoothPrint.GRAVITY_LEFT);//居左
                remarkDes.setTextSize(IBluetoothPrint.TEXT_SIZE_S);//字体
                iBluetoothPrint.printText(printBean.getRemark(),remarkDes);
                //设置标题图片样式
                IBluetoothPrint.Describe imageDes = new IBluetoothPrint.Describe();
                imageDes.setGravity((byte) IBluetoothPrint.GRAVITY_RIGHT);//居右
                imageDes.setHeightP(50f);
                imageDes.setWidthP(50f);
                iBluetoothPrint.printImage(PrinterActivity.this.getResources(),R.drawable.logo,imageDes);
            }
            Toast.makeText(PrinterActivity.this, "托运单号："+ printBean.getTydh() +" 打印完成", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(PrinterActivity.this, "未连接蓝牙设备，请重试", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    private void showProgressDialog(String msg, boolean cancel) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, null, msg);
        } else {
            progressDialog.setMessage(msg);
        }
        progressDialog.setCanceledOnTouchOutside(cancel);
        progressDialog.setCancelable(cancel);

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog.cancel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WPXMain.disconnectDevice();//断开设备
    }
}
