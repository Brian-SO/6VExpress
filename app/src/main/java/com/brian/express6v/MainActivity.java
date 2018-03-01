package com.brian.express6v;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.brian.express6v.entity.PrintBean;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by suboan on 2018/2/7.
 *
 */
public class MainActivity extends BaseActivity {

    private long mPressedTime = 0;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webview);

        webview.loadUrl("http://wl.huazhon.cn/CustomerManage/OrderEntry/login");//调用在线网页
        //WebSettings 几乎浏览器的所有设置都在该类中进行
        WebSettings webSettings = webview.getSettings();
        //添加设置，使js代码能运行
        webSettings.setJavaScriptEnabled(true);//设置webview支持JavaScript
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过js打开新的窗口
        webSettings.setAllowFileAccess(true);//设置是否允许WebView使用File协议
//        webview.setScrollBarStyle(0);//滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上
        webview.setVerticalScrollbarOverlay(true);//指定的垂直滚动条有叠加样式
        webview.setBackgroundColor(Color.argb(0, 0, 0, 0));//设置背景颜色，透明
        webview.setWebChromeClient(new WebChromeClient());//添加客户端支持（允许js代码中alert对话框的弹出）
        //JsInterface类是专门用来放js调用Java的代码，AndroidWebView就是js调用JsInterface时的名称(window.AndroidWebView.showInfoFromJs(name);)
        webview.addJavascriptInterface(new JsInterface(this), "AndroidWebView");//在js中调用本地Java方法，方法名随意定义

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，而在WebView里打开网页
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }
        });
        //捕捉js代码alert弹窗事件，统一转换成Toast
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                result.confirm();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if((mNowTime - mPressedTime) > 2000){//比较两次按键时间差
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        } else{//退出程序
            this.finish();
            System.exit(0);
        }
        /*String jsonStr = "{\"fhr\": \"发货人\""+
                ", \"fhrdh\": \"发货人电话\""+
                ", \"shr\": \"收货人\""+
                ", \"shrdh\": \"收货人电话\""+
                ", \"shrdz\": \"广东省广州市天河区吉山幼儿园\""+
                ", \"tydh\": \"02018020800001\""+
                ", \"shwd\": \"收货网点\""+
                ", \"mdwd\": \"目的网点\""+
                ", \"hwmc\": \"货物名称\""+
                ", \"jshj\": \"1\""+
                ", \"hj\": \"30.24\""+
                ", \"fkfs\": \"现付\""+
                ", \"tyfs\": \"自提\""+
                ", \"company\": \"金达物流\""+
                ", \"remark\": \"查询物流跟踪请上：www.6vwl.com\"}";
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            printBean = new PrintBean();
            printBean.setFhr(jsonObject.getString("fhr"));
            printBean.setFhrdh(jsonObject.getString("fhrdh"));
            printBean.setShr(jsonObject.getString("shr"));
            printBean.setShrdh(jsonObject.getString("shrdh"));
            printBean.setShrdz(jsonObject.getString("shrdz"));
            printBean.setTydh(jsonObject.getString("tydh"));
            printBean.setShwd(jsonObject.getString("shwd"));
            printBean.setMdwd(jsonObject.getString("mdwd"));
            printBean.setHwmc(jsonObject.getString("hwmc"));
            printBean.setJshj(jsonObject.getString("jshj"));
            printBean.setHj(jsonObject.getString("hj"));
            printBean.setFkfs(jsonObject.getString("fkfs"));
            printBean.setTyfs(jsonObject.getString("tyfs"));
            printBean.setCompany(jsonObject.getString("company"));
            printBean.setRemark(jsonObject.getString("remark"));
            printBean.setTyrq(DateFormat.format("yyyy年MM月dd日", new Date()).toString());
            connBluetoothDevice();
//            Toast.makeText(MainActivity.this,printBean.toString(), Toast.LENGTH_LONG).show();
//            Log.e("daf",printBean.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * js调用的android对象方法定义
     * */
    private class JsInterface {
        private Context context;
        public JsInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface   //sdk17版本以上加上注解,不然接收不了js的返回值
        public void getDataFromJs(String result){
            try {
                JSONObject jsonObject = new JSONObject(result);
                printBean = new PrintBean();
                printBean.setFhr(jsonObject.getString("fhr"));
                printBean.setFhrdh(jsonObject.getString("fhrdh"));
                printBean.setShr(jsonObject.getString("shr"));
                printBean.setShrdh(jsonObject.getString("shrdh"));
                printBean.setShrdz(jsonObject.getString("shrdz"));
                printBean.setTydh(jsonObject.getString("tydh"));
                printBean.setShwd(jsonObject.getString("shwd"));
                printBean.setMdwd(jsonObject.getString("mdwd"));
                printBean.setHwmc(jsonObject.getString("hwmc"));
                printBean.setJshj(jsonObject.getString("jshj"));
                printBean.setHj(jsonObject.getString("hj"));
                printBean.setFkfs(jsonObject.getString("fkfs"));
                printBean.setTyfs(jsonObject.getString("tyfs"));
                printBean.setCompany(jsonObject.getString("company"));
                printBean.setRemark(jsonObject.getString("remark"));
                printBean.setTyrq(DateFormat.format("yyyy年MM月dd日", new Date()).toString());
                connBluetoothDevice();
//                Toast.makeText(context,printBean.toString(), Toast.LENGTH_LONG).show();
//                Log.e("daf",printBean.toString());
                /*
                //跳转页面方法，使用Parcelable传递对象
                Bundle bundle = new Bundle();
                // Bundle有putParcelableArray和putParcelableArrayList方法，也就可以传递数组和列表
                bundle.putParcelable("printBean", printBean);
                Intent intent = new Intent(context,PrinterActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);*/
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "解析数据出错", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
