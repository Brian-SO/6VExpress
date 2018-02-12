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

import java.util.Set;

/**
 * Created by suboan on 2018/2/12.
 *
 */

public class BaseActivity extends AppCompatActivity {

    private static final int PICK_CONTACT_REQUEST = 1;  // The request code
    public static BluetoothAdapter _myBluetoothAdapter;
//    public IBluetoothPrint iBluetoothPrint;
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
        Set<BluetoothDevice> pairedDevices = WPXMain.getBondedDevices();
        if (pairedDevices.size() <= 0){
            Toast.makeText(getApplicationContext(), "请先配对蓝牙打印机", Toast.LENGTH_LONG).show();
            return;
        }
        final int count = Math.round(Float.parseFloat(printBean.getJshj()));//打印张数
        if (count <= 0)
            return;
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
        for (BluetoothDevice device : pairedDevices) {
            DeviceName = device.getName();
            BDAddress = device.getAddress();
            break;
        }
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
            IBluetoothPrint iBluetoothPrint = WPXMain.getBluetoothPrint();//获取打印接口
            for(int i = 0 ; i < count ; i++) {
                //设置标题公司样式
                /*WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_GS_CHARACTER_DOUBLE_SIZE);//字符双倍宽高
                WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_ALIGN_CENTER);//居中
                iBluetoothPrint.printText(printBean.getCompany());
//                WPXMain.printCommand(new byte[]{GeneralAttributes.CR});//换行
                WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_GS_CHARACTER_SIZE_DEFUALT);//字符默认大小
                //设置单号样式
//                WPXMain.printCommand(GeneralAttributes.ESC_ALIGN_LEFT);//居左
                iBluetoothPrint.printText("NO.".concat(printBean.getTydh()));*/

                //设置标题公司样式
                IBluetoothPrint.Describe companyDes = new IBluetoothPrint.Describe();
                companyDes.setGravity((byte) IBluetoothPrint.GRAVITY_CENTER);//居中
                companyDes.setTextSize(IBluetoothPrint.TEXT_SIZE_B);//大号字体
                iBluetoothPrint.printText(printBean.getCompany(),companyDes);

                //设置托运单样式
                IBluetoothPrint.Describe tydhDes = new IBluetoothPrint.Describe();
                tydhDes.setGravity((byte) IBluetoothPrint.GRAVITY_LEFT);//居左
                tydhDes.setTextSize(IBluetoothPrint.TEXT_SIZE_N);//默认字体
                iBluetoothPrint.printText("NO.".concat(printBean.getTydh()),tydhDes);
                //设置日期样式
//                WPXMain.printCommand(GeneralAttributes.ESC_ALIGN_RIGHT);//居右
//                iBluetoothPrint.printText(printBean.getTyrq());
//                iBluetoothPrint.printText("NO.".concat(printBean.getTydh()),
//                        new IBluetoothPrint.Describe().setGravity((byte) IBluetoothPrint.GRAVITY_LEFT));//居左
//                iBluetoothPrint.printText(printBean.getTyrq(),
//                        new IBluetoothPrint.Describe().setGravity((byte) IBluetoothPrint.GRAVITY_RIGHT));//居右
//                iBluetoothPrint.printText("收货人：".concat(printBean.getShr().concat("    电话：".concat(printBean.getShrdh()))));
//                iBluetoothPrint.printText("地址：".concat(printBean.getShrdz()));
//                iBluetoothPrint.printText("寄件人：".concat(printBean.getFhr().concat("    电话：".concat(printBean.getFhrdh()))));
//                iBluetoothPrint.printText("货物名称：".concat(printBean.getHwmc().concat("    件数：".concat(String.valueOf(count).concat("-".concat(String.valueOf(i+1)))))));
//                iBluetoothPrint.printText("付款方式：".concat(printBean.getFkfs().concat("    交接方式：".concat(printBean.getTyfs()))));
//                //设置标题备注样式
//                IBluetoothPrint.Describe remarkDes = new IBluetoothPrint.Describe();
//                remarkDes.setGravity((byte) IBluetoothPrint.GRAVITY_LEFT);//居左
//                remarkDes.setTextSize(IBluetoothPrint.TEXT_SIZE_S);//字体
//                iBluetoothPrint.printText(printBean.getRemark(),remarkDes);
//                //设置标题图片样式
//                IBluetoothPrint.Describe imageDes = new IBluetoothPrint.Describe();
//                imageDes.setGravity((byte) IBluetoothPrint.GRAVITY_RIGHT);//居右
//                imageDes.setHeightP(50f);
//                imageDes.setWidthP(50f);
//                iBluetoothPrint.printImage(getApplicationContext().getResources(),R.drawable.logo,imageDes);
            }
            Toast.makeText(getApplicationContext(), "托运单号："+ printBean.getTydh() +" 打印完成", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getApplicationContext(), "未连接蓝牙设备，请重试", Toast.LENGTH_LONG).show();
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
}
