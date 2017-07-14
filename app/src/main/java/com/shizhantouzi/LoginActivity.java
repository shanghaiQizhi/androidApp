package com.shizhantouzi;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputType;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.q_bean.Common;
import com.q_bean.cnst.Cmd;
import com.q_util.DeviceTools;
import com.q_util.MD5Encrypt;
import com.q_util.SocketTools;

import org.apache.log4j.Logger;

import java.io.IOException;

public class LoginActivity extends Activity {

    RelativeLayout layout;
    TextView textMobile;
    EditText editMobile;
    TextView textKey;
    EditText editKey;
    ImageButton imgbtnLogin;
    ImageButton imgbtnRegist;
    TextView txtTitle;
    SocketTools socketTools = null;
    Logger logger =  Logger.getLogger(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        socketTools = new SocketTools();

        //通用模块
        layout = (RelativeLayout) findViewById(R.id.layout1);
        layout.setBackgroundColor(Color.WHITE);

        //SurfaceView
        surfaceView view = new surfaceView(this);
        //view.setZOrderOnTop(true);
        //view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        layout.addView(view);



        creatElement();
    }

    private void creatElement() {
        //TextPaint tp = null;

        //title
        txtTitle = new TextView(this);
        txtTitle.setText(getResources().getString(R.string.app_name) + " - 账号登陆");
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

        textMobile = new TextView(this);
        textMobile.setText("手机号");
        //tp = textMobile.getPaint();
        //tp.setFakeBoldText(true);
        //textAccount.setTextSize(20);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        textMobile.setTextColor(Color.rgb(0, 0, 0));
        int dip2pxMobileStartX = DeviceTools.dip2px(this.getApplicationContext(),70);
        int dip2pxMobileStartY = DeviceTools.dip2px(this.getApplicationContext(),70);
        textMobile.setX(Common.ScreenWidth / 2 - dip2pxMobileStartX);
        textMobile.setY(Common.ScreenHeight / 2 - dip2pxMobileStartY);
        layout.addView(textMobile);

        editMobile = new EditText(this);
        editMobile.clearFocus();
        //tp = editMobile.getPaint();
        //tp.setFakeBoldText(true);
        //edit1.setTextSize(20);
        editMobile.setWidth(250);
        //edit1.setLineSpacing(1.6f, 1.4f);
        editMobile.setTextColor(Color.rgb(0, 0, 0));
        int dip2pxMobileEditStartX = DeviceTools.dip2px(this.getApplicationContext(),80);
        int dip2pxMobileEditStartY = DeviceTools.dip2px(this.getApplicationContext(),10);
        editMobile.setX(textMobile.getX() + dip2pxMobileEditStartX);
        editMobile.setY(textMobile.getY() - dip2pxMobileEditStartY);
        editMobile.setWidth(400);//setWidth的值小于默认，不生效
        layout.addView(editMobile);

        //密码文字
        textKey = new TextView(this);
        textKey.setText("密　码");
        //tp = textKey.getPaint();
        //tp.setFakeBoldText(true);
        //textAccount.setTextSize(20);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        textKey.setTextColor(Color.rgb(0, 0, 0));
        int dip2pxPasswordStartY = DeviceTools.dip2px(this.getApplicationContext(),50);
        textKey.setX(textMobile.getX());
        textKey.setY(textMobile.getY() + dip2pxPasswordStartY);
        layout.addView(textKey);

        editKey = new EditText(this);
        editKey.clearFocus();
        //tp = editKey.getPaint();
        //tp.setFakeBoldText(true);
        //edit1.setTextSize(20);
        editKey.setWidth(250);
        //edit1.setLineSpacing(1.6f, 1.4f);
        editKey.setTextColor(Color.rgb(0, 0, 0));
        editKey.setX(editMobile.getX());
        editKey.setY(textKey.getY() - dip2pxMobileEditStartY);
        editKey.setWidth(400);//setWidth的值小于默认，不生效
        editKey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(editKey);

        imgbtnLogin = new ImageButton(this);
        imgbtnLogin.setImageDrawable(getResources().getDrawable(R.drawable.loginbutton));
        imgbtnLogin.setBackgroundColor(Color.TRANSPARENT);
        int dip2pxLoginBtnStartX = DeviceTools.dip2px(this.getApplicationContext(),5);
        int dip2pxLoginBtnStartY = DeviceTools.dip2px(this.getApplicationContext(),50);
        imgbtnLogin.setX(Common.ScreenWidth / 2 - imgbtnLogin.getDrawable().getMinimumWidth() - dip2pxLoginBtnStartX);
        imgbtnLogin.setY(textKey.getY() + dip2pxLoginBtnStartY);
        imgbtnLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.loginbutton2));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.loginbutton));
                    imgbtnLogin.setEnabled(false);
                    loginIn();
                }
                return false;
            }
        });
        layout.addView(imgbtnLogin);

        imgbtnRegist = new ImageButton(this);
        imgbtnRegist.setImageDrawable(getResources().getDrawable(R.drawable.registbutton));
        imgbtnRegist.setBackgroundColor(Color.TRANSPARENT);
        int dip2pxRegistBtnStartX = DeviceTools.dip2px(this.getApplicationContext(),5);
        imgbtnRegist.setX(Common.ScreenWidth / 2 + dip2pxRegistBtnStartX);
        imgbtnRegist.setY(imgbtnLogin.getY());
        imgbtnRegist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.registbutton2));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.registbutton));

                    onBackPressed();
                    //imgbtnRegist.setEnabled(false);
                }
                return false;
            }
        });
        layout.addView(imgbtnRegist);
    }

    private void regist() {
        SharedPreferences settings = getSharedPreferences(Common.path, Context.MODE_PRIVATE);
        SharedPreferences.Editor nameEditor = settings.edit();
        nameEditor.putString("mobile", editMobile.getText().toString());
        nameEditor.putString("key", editKey.getText().toString());
        nameEditor.commit();
        Common.Mobile = editMobile.getText().toString();
        String password = MD5Encrypt.MD5Encode(editKey.getText().toString());
        String jsonOutPut = "{\"cmd\":\"" + Cmd.cmd_level1_regist + "\",\"moblie\":\"" + editMobile.getText().toString() + "\",\"password\":\"" + password + "\"}";
        //socketTools.sendMessage(Common.dos, jsonOutPut);
        try {
            socketTools.boolPrintWrite(Common.socket.getOutputStream(),jsonOutPut);
        } catch (IOException e) {
            logger.error("发送注册请求失败: "+e.getMessage());
        }
        LoginActivity.this.finish();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确定提交您填写的注册信息吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        regist();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();
        // super.onBackPressed();
    }

    private void loginIn() {
        SharedPreferences settings = getSharedPreferences(Common.path, Context.MODE_PRIVATE);
        Editor nameEditor = settings.edit();
        Common.Mobile = editMobile.getText().toString();
        Common.Key = MD5Encrypt.MD5Encode(editKey.getText().toString());
        nameEditor.putString("mobile", Common.Mobile);
        nameEditor.putString("key", Common.Key);
        nameEditor.commit();


        Intent aintent = new Intent(LoginActivity.this, MainActivity.class);
        aintent.putExtra("cmd", "login");
        setResult(RESULT_OK, aintent);

        LoginActivity.this.finish();
    }


    public class surfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

        private SurfaceHolder surfaceHolder;
        private Canvas canvas;
        private Paint paint;
        TextView txtTitle = null;
        int dip2pxTopBlueHeight35 = DeviceTools.dip2px(this.getContext(),35);

        public surfaceView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub

            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            paint = new Paint();
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (surfaceHolder) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    paint.setColor(Color.rgb(255, 255, 255));
                    canvas.drawRect(0, 0, Common.ScreenWidth, Common.ScreenHeight, paint);
                    paint.setColor(Color.rgb(51, 94, 155));//blue
                    //canvas.drawRect(0, 0, Common.ScreenWidth, 80, paint);
                    //head bg
                    canvas.drawRect(0,0, Common.ScreenWidth, dip2pxTopBlueHeight35,paint);

                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
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
