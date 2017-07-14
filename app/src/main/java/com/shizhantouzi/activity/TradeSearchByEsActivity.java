package com.shizhantouzi.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.q_bean.Common;
import com.q_util.DeviceTools;
import com.shizhantouzi.R;

public class TradeSearchByEsActivity extends Activity {

    RelativeLayout layout = null;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_search_by_es);

        //通用模块
        layout = (RelativeLayout)findViewById(R.id.activity_trade_search_by_es);
        layout.setBackgroundColor(Color.WHITE);

        //SurfaceView
        surfaceView view = new surfaceView(this);
        layout.addView(view);

        createElement();



    }

    private void createElement() {
        //Title
        TextView txtTitle = new TextView(this);
        txtTitle.setText(getResources().getString(R.string.app_name) + " - 系统设置");
        //tp = txtTitle.getPaint();
        //tp.setFakeBoldText(true);
        txtTitle.setTextSize(14);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        txtTitle.setTextColor(Color.rgb(255, 255, 255));
        int dip2pxTitleStartX = DeviceTools.dip2px(this.getApplicationContext(),10);
        int dip2pxTitleStartY = DeviceTools.dip2px(this.getApplicationContext(),5);
        txtTitle.setX(dip2pxTitleStartX);
        txtTitle.setY(dip2pxTitleStartY);
        layout.addView(txtTitle);

        webView = (WebView) findViewById(R.id.webView);
        int dip2pxWebViewStartY = DeviceTools.dip2px(this.getApplicationContext(),35);
        webView.setY(dip2pxWebViewStartY);
        webView.bringToFront();

        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //支持插件
        //webSettings.setPluginsEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        //webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        webView.loadUrl(Common.WebUrlPre+"getListFilter");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    class surfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

        private SurfaceHolder surfaceHolder;
        private Canvas canvas;
        private Paint paint;
        TextView txtTitle = null;
        private Rect touchAreaClose;
        int dip2px20 = DeviceTools.dip2px(this.getContext(),20);
        int dip2px10 = DeviceTools.dip2px(this.getContext(),10);


        public surfaceView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub

            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            paint = new Paint();
            touchAreaClose = new Rect(Common.ScreenWidth-70,0,Common.ScreenWidth,70);
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (surfaceHolder) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    paint.setColor(Color.rgb(255, 255, 255));//white
                    canvas.drawRect(0,0, Common.ScreenWidth,Common.ScreenHeight,paint);

                    paint.setColor(Color.rgb(51, 94, 155));//blue

                    //head bg
                    int dip2pxTopBlueHeight35 = DeviceTools.dip2px(this.getContext(),35);
                    canvas.drawRect(0,0, Common.ScreenWidth, dip2pxTopBlueHeight35,paint);
                    //canvas.drawRect(0,0, Common.ScreenWidth, 70,paint);

                    paint.setColor(Color.rgb(255, 0, 0));//red
                    canvas.drawRect(Common.ScreenWidth-dip2pxTopBlueHeight35,0,Common.ScreenWidth,dip2pxTopBlueHeight35, paint);//退出色块

                    paint.setColor(Color.rgb(255,255,255));
                    paint.setStrokeWidth(6);
                    canvas.drawLine(Common.ScreenWidth-dip2px20,dip2px20, Common.ScreenWidth-dip2px10,dip2px10, paint);//大叉
                    canvas.drawLine(Common.ScreenWidth-dip2px20,dip2px10, Common.ScreenWidth-dip2px10,dip2px20, paint);//大叉
                } catch (Exception e) {
                    // TODO: handle exception
                } finally{
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();
            if(touchAreaClose.contains(x, y)){
                //Common.TouchFirst = true;
                TradeSearchByEsActivity.this.finish();
                return true;
            }

            return false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                                   int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void surfaceCreated(SurfaceHolder arg0) {
            // TODO Auto-generated method stub
            new Thread(this).start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
            // TODO Auto-generated method stub

        }
    }
}
