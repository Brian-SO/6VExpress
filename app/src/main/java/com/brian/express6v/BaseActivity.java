package com.brian.express6v;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.brian.express6v.entity.PrintBean;
import com.wpx.IBluetoothPrint;
import com.wpx.WPXMain;
import com.wpx.util.GeneralAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suboan on 2018/2/12.
 *
 */
public class BaseActivity extends AppCompatActivity {

    private static final int PICK_CONTACT_REQUEST = 1;  // The request code
    public static BluetoothAdapter _myBluetoothAdapter;
    private ProgressDialog progressDialog;
    public String BDAddress;//蓝牙设备地址
    public String DeviceName = "";//设备名称
    public PrintBean printBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK)
                connBluetoothDevice();
        }
    }

    //连接蓝牙设备
    public void connBluetoothDevice() {
        if((_myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
            Toast.makeText(getApplicationContext(), "没有找到蓝牙适配器", Toast.LENGTH_LONG).show();
            return;
        }
        //让用户确定是否开启蓝牙
        if(!_myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, PICK_CONTACT_REQUEST);
            return;
        }
        //获取已经绑定的蓝牙设备
        List<BluetoothDevice> pairedDevices = new ArrayList<BluetoothDevice>(WPXMain.getBondedDevices());
        if (pairedDevices.size() <= 0){
            Toast.makeText(getApplicationContext(), "请先配对蓝牙打印机", Toast.LENGTH_LONG).show();
            return;
        }
        /*final int count = Math.round(Float.parseFloat(printBean.getJshj()));//打印张数
        if (count <= 0)
            return;*/
        final int count = 1;//打印张数
        if (WPXMain.isConnected()){
            //如果已经连接打印机就直接打印
            new Thread(new Runnable() {
                @Override
                public void run() {
                    xCasePrint(count);
                }
            }).start();
            return;
        }
        DeviceName = pairedDevices.get(0).getName();
        BDAddress = pairedDevices.get(0).getAddress();
        //异步连接设备
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
                dismissProgressDialog();
                if (result){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            xCasePrint(count);
                        }
                    }).start();
                    return;
                }
                Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    //打印
    private void xCasePrint(int count){
        Looper.prepare();
        if (WPXMain.isConnected()) {
            for(int i = 0 ; i < count ; i++) {
                try {
                    printText(true);
                    Thread.sleep(3000);
                    printText(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(getApplicationContext(), "托运单号："+ printBean.getTydh() +" 打印完成", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getApplicationContext(), "未连接蓝牙设备，请重试", Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    //打印内容
    private void printText(boolean isPrintImage){
        IBluetoothPrint iBluetoothPrint = WPXMain.getBluetoothPrint();//获取打印接口
        WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_GS_CHARACTER_DOUBLE_SIZE);//字符双倍宽高
        iBluetoothPrint.printText(printBean.getCompany());
        WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_GS_CHARACTER_SIZE_DEFUALT);//字符默认大小
        WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_ALIGN_LEFT);
        iBluetoothPrint.printText(printBean.getTyrq());
        iBluetoothPrint.printText("NO.".concat(printBean.getTydh()));
        iBluetoothPrint.printText("收货人:".concat(printBean.getShr()));
        iBluetoothPrint.printText("电话:".concat(printBean.getShrdh()));
        iBluetoothPrint.printText("地址:".concat(printBean.getShrdz()));
        iBluetoothPrint.printText("寄件人:".concat(printBean.getFhr()));
        iBluetoothPrint.printText("电话:".concat(printBean.getFhrdh()));
        iBluetoothPrint.printText("货物名称:".concat(printBean.getHwmc()));
//        iBluetoothPrint.printText("件数:".concat(String.valueOf(count).concat("-".concat(String.valueOf(i+1)))));
        iBluetoothPrint.printText("件数:".concat(printBean.getJshj()));
        iBluetoothPrint.printText("付款方式:".concat(printBean.getFkfs().concat("  交接方式:".concat(printBean.getTyfs()))));
        if (isPrintImage) {
            iBluetoothPrint.printText(printBean.getRemark());
            iBluetoothPrint.printText("或扫描二维码查询");
            iBluetoothPrint.printImage(getApplicationContext().getResources(), R.drawable.wechat);
            iBluetoothPrint.printText("注：如有货物损坏丢失，保价货物按照相关规定赔偿，未保价货物按运费的3倍赔偿");
            //走纸
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            iBluetoothPrint.printText("--------------------------------");
            //走纸
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
        }else{
            //走纸
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
            WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
        }
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
}
