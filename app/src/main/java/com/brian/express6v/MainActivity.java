package com.brian.express6v;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by suboan on 2018/2/7.
 *
 */

public class MainActivity extends AppCompatActivity {

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
        /*if (webview.canGoBack()){//判断网页是否为最后一页
            webview.goBack();//返回上一页
        }else {
            this.finish();
            System.exit(0);//退出程序
        }*/

        /*long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if((mNowTime - mPressedTime) > 2000){//比较两次按键时间差
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        } else{//退出程序
            this.finish();
            System.exit(0);
        }*/
        Intent intent = new Intent();
        intent.setClass(this,PrinterActivity.class);
        // 转向登陆后的页面
        startActivity(intent);
    }

    /**
     * js调用的android对象方法定义
     * */
    private class JsInterface {
        private Context context;
        public JsInterface(Context context) {
            this.context=context;
        }

        @JavascriptInterface   //sdk17版本以上加上注解,不然接收不了js的返回值
        public void getDataFromJs(String result){
            try {
                JSONObject jsonObject = new JSONObject(result);
                /*final String name=jsonObject.getString("name");
                final int age=jsonObject.getInt("age");
                final String company=jsonObject.getString("company");

                //这是一条线程加上这个，才能更新ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textview.setText("名字："+name+"\n" +"年龄："+ age +"\n" +"公司："+ company);
                    }
                });*/
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "解析数据出错", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
