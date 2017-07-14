package com.shizhantouzi.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.q_bean.Common;
import com.q_bean.cnst.Cmd;
import com.q_bizbi.BiBiz;
import com.q_util.DeviceTools;
import com.q_util.SocketTools;
import com.shizhantouzi.R;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ChartActivity extends Activity {
    Logger logger = Logger.getLogger(ChartActivity.class);

    SocketTools socketTools = null;
    BiBiz biBiz = BiBiz.newInstance();
    TextView txtTitle = null;
    RelativeLayout layout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        socketTools = new SocketTools();

        //通用模块
        layout = (RelativeLayout)findViewById(R.id.activity_chart);
        layout.setBackgroundColor(Color.WHITE);

        //SurfaceView
        surfaceView view = new surfaceView(this);
        //view.setZOrderOnTop(true);
        //view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        layout.addView(view);

        createElement();
    }

    private void createElement() {
        //Title
        txtTitle = new TextView(this);
        txtTitle.setText(getResources().getString(R.string.app_name) + " - 统计分析");
        //tp = txtTitle.getPaint();
        //tp.setFakeBoldText(true);
        txtTitle.setTextSize(16);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        txtTitle.setTextColor(Color.rgb(255, 255, 255));
        int titleX = DeviceTools.dip2px(this.getApplicationContext(),5);
        int titleY = DeviceTools.dip2px(this.getApplicationContext(),7);
        txtTitle.setX(titleX);
        txtTitle.setY(titleY);
        layout.addView(txtTitle);

        //清空历史
        ImageButton btnClearHistory = new ImageButton(this);
        btnClearHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_refresh));
        btnClearHistory.setBackgroundColor(Color.TRANSPARENT);
        int btnClearHistoryX = Common.ScreenWidth - DeviceTools.dip2px(this.getApplicationContext(),55);
        int btnClearHistoryY = DeviceTools.dip2px(this.getApplicationContext(),50);
        btnClearHistory.setX(btnClearHistoryX);
        btnClearHistory.setY(btnClearHistoryY);
        btnClearHistory.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //重新设置按下时的背景图片
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.ic_refresh2));
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    //再修改为抬起时的正常图片
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.ic_refresh));
                    //btnClearHistory.setEnabled(false);
                    onrefresh();
                }
                return false;
            }
        });
        layout.addView(btnClearHistory);
    }

    protected void onrefresh() {
        //meaCiShu,meaPingCang,meaNoPingCang
        String jsonOutPut = "{\"cmd\":\""+ Cmd.cmd_level1_bi+"\",\"columCount\":\""+biBiz.strZ.size()+"\",\"columnsStr\":\"meaCiShu,meaPingCang,meaNoPingCang\"}";
        //socketTools.sendMessage(Common.dos,jsonOutPut);
        try {
            socketTools.boolPrintWrite(Common.socket.getOutputStream(),jsonOutPut);
        } catch (IOException e) {
            logger.error("发送登录请求失败: "+e.getMessage());
        }
    }


    class surfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{
        int lastX, lastY;
        private Context context;
        private SurfaceHolder surfaceHolder;
        private Canvas canvas;
        private Paint paint;
        private Paint paintGrayAlpha;
        //private Paint paintTxt;
        TextView txtTitle = null;
        private Rect touchAreaClose;
        int colorO = -20;
        int allHeight=0;
        int allWidth = 0;
        private boolean closeBool = false;
        //int perOffX = 0;
        int dip2px35 = DeviceTools.dip2px(this.getContext(),35);
        int dip2px10 = DeviceTools.dip2px(this.getContext(),10);
        int dip2px25 = DeviceTools.dip2px(this.getContext(),25);
        int dip2px5 = DeviceTools.dip2px(this.getContext(),5);
        int dip2px30 = DeviceTools.dip2px(this.getContext(),30);
        int dip2px2 = DeviceTools.dip2px(this.getContext(),2);
        int dip2px8 = DeviceTools.dip2px(this.getContext(),8);
        int dip2px50 = DeviceTools.dip2px(this.getContext(),50);

        int dip2pxPerHeight = DeviceTools.dip2px(this.getContext(),15);//Y轴每段高度
        int dip2pxLeftTopStartY = Common.ScreenHeight-biBiz.strY.size()*dip2pxPerHeight-dip2px50;//维度层左上角X坐标
        int dip2pxLeveHigh = dip2pxPerHeight * biBiz.strY.size() + dip2px50;//2个维度层之间的高度
        int dip2pxPerOffX = DeviceTools.dip2px(this.getContext(),20);//Y轴每一格的X偏差宽度
        int dip2pxLeftTopOffX = dip2pxPerOffX * biBiz.strY.size();//X偏差宽度.四边形左边上面点和下面点的X轴距离.
        int dip2pxDataOffY = DeviceTools.dip2px(this.getContext(),2);//数据柱子上移高度
        int dip2pxPerWidthX = DeviceTools.dip2px(this.getContext(),50);//X轴每段宽度。
        int mMapPosX =0;//拖动x
        int mMapPosY =0;//拖动y
        int dip2pxLeftTopStartX = DeviceTools.dip2px(this.getContext(),60);//维度层左上角X坐标
        int dip2pxDataHeight = DeviceTools.dip2px(this.getContext(),25);//数据柱子高度

        public surfaceView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            this.context = context;
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            //paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(18);
            paintGrayAlpha = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintGrayAlpha.setColor(Color.rgb(100, 100, 100));
            paintGrayAlpha.setAlpha(100);
            paintGrayAlpha.setStyle(Paint.Style.STROKE);
            paintGrayAlpha.setStrokeWidth(1);
            //PathEffect effects = new DashPathEffect(new float[] { 1, 2, 4, 8}, 1);
            PathEffect effects = new DashPathEffect(new float[] { 5,5}, 1);
            paintGrayAlpha.setPathEffect(effects);
            //paintTxt = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

            touchAreaClose = new Rect(Common.ScreenWidth-dip2px35,0,Common.ScreenWidth,dip2px35);
            allHeight = dip2pxPerHeight*biBiz.strY.size()*biBiz.strZ.size()+dip2pxLeveHigh-dip2px25;
            //perOffX = biBiz.leftTopOffX/biBiz.strY.size();
            allWidth = dip2pxPerWidthX*(biBiz.strX.size()-2);

        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(!closeBool){
                synchronized (surfaceHolder) {
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        paint.setColor(Color.rgb(255, 255, 255));//white
                        canvas.drawRect(0,0, Common.ScreenWidth,Common.ScreenHeight,paint);
                        int levDataNo = 0;
                        int columnCount = biBiz.strZ.size();
                        for(int c=0;c<columnCount;c++){
                            int yDiff = dip2pxLeftTopStartY-(dip2pxLeveHigh*c)+mMapPosY;
                            //paint.setColor(Color.rgb(0,0,0));
                            //canvas.drawText("yDiff:"+yDiff,20,120, paint);
                            int modInt = c % 7;

                            //层
                            if (c == 0) {
                                paint.setColor(Color.rgb(64, 181, 224));//蓝
                            }else if (modInt == 0){
                                paint.setColor(Color.rgb(64, 181, 224));//蓝
                            }else if (modInt == 1){
                                paint.setColor(Color.rgb(227-colorO, 207-colorO, 87-colorO));//香蕉黄
                            }else if (modInt == 2){
                                paint.setColor(Color.rgb(218-colorO, 112-colorO, 214-colorO));//淡紫色
                            }else if (modInt == 3){
                                paint.setColor(Color.rgb(107-colorO, 142-colorO, 35-colorO));//草绿色
                            }else if (modInt == 4){
                                paint.setColor(Color.rgb(255, 99-colorO, 71-colorO));//番茄红
                            }else if (modInt == 5){
                                paint.setColor(Color.rgb(64-colorO, 224-colorO, 205-colorO));//青绿色
                            }else if (modInt == 6){
                                paint.setColor(Color.rgb(255, 128-colorO, 0-colorO));//橘黄
                            }

                            //paint.setAlpha(100);
                            Path path = new Path();
                            path.moveTo(mMapPosX+dip2pxLeftTopStartX, yDiff);
                            path.lineTo(mMapPosX+dip2pxLeftTopStartX+(dip2pxPerWidthX*biBiz.strX.size()),yDiff);
                            path.lineTo(mMapPosX+dip2pxLeftTopStartX+(dip2pxPerWidthX*biBiz.strX.size())+dip2pxLeftTopOffX,yDiff+(dip2pxPerHeight*biBiz.strY.size()));
                            path.lineTo(mMapPosX+dip2pxLeftTopStartX+dip2pxLeftTopOffX,yDiff+(dip2pxPerHeight*biBiz.strY.size()));
                            path.lineTo(mMapPosX+dip2pxLeftTopStartX, yDiff);
                            canvas.drawPath(path, paint);

                            //x轴标题
                            for(int i=0;i<biBiz.strX.size();i++){
                                String oStr = biBiz.strX.get(i);
                                int x = mMapPosX+dip2pxLeftTopStartX+dip2pxLeftTopOffX+(i*dip2pxPerWidthX);
                                int y = yDiff+(biBiz.strY.size()*dip2pxPerHeight)+dip2px8;
                                canvas.drawText(oStr, x, y, paint);

                                if(i>0){
                                    //竖的虚线
                                    canvas.drawLine(mMapPosX+dip2pxLeftTopStartX+(i*dip2pxPerWidthX)
                                            ,yDiff
                                            ,mMapPosX+dip2pxLeftTopStartX+dip2pxLeftTopOffX+(i*dip2pxPerWidthX)
                                            ,yDiff+(dip2pxPerHeight*biBiz.strY.size())
                                            , paintGrayAlpha);
                                }
                            }

                            //y轴标题
                            for(int i=0;i<biBiz.strY.size();i++){
                                String oStr = biBiz.strY.get(i);
                                int x = mMapPosX+(dip2pxLeftTopStartX-dip2px35)+(dip2pxPerOffX*i);
                                int y = yDiff+((i+1)*dip2pxPerHeight)+dip2px2;
                                canvas.drawText(oStr, x, y, paint);

                                if(i<biBiz.strY.size()-1){
                                    canvas.drawLine(mMapPosX+dip2pxLeftTopStartX+(dip2pxPerOffX*(i+1))
                                            ,yDiff+((i+1)*dip2pxPerHeight)
                                            ,mMapPosX+dip2pxLeftTopStartX+(dip2pxPerOffX*(i+1))+biBiz.strX.size()*dip2pxPerWidthX
                                            ,yDiff+((i+1)*dip2pxPerHeight)
                                            , paintGrayAlpha);
                                }
                            }

                            //z轴标题
                            String oStr = biBiz.strZ.get(c);
                            int x = mMapPosX+dip2pxLeftTopStartX-dip2px30;
                            int y =  yDiff-(dip2pxLeveHigh/dip2px5)+dip2px5;
                            canvas.drawText(oStr, x, y, paint);

                            //指标数据
                            int width = dip2px10;
                            //int lvNo = 0;//数据个数计数
                            for(int j=0;j<biBiz.strX.size();j++){//从左到右
                                for(int i=0;i<biBiz.strY.size();i++){//从上到下
                                    int perOffWidthX = dip2pxLeftTopOffX/biBiz.strY.size();//每一行的X轴偏移长度
                                    int perOffX = perOffWidthX*(i+1);//每一行的X轴偏移坐标
                                    int basePerY = dip2pxPerHeight*(i+1);

                                    Path path_temp;
                                    //paint.setAlpha(60);
                                    //最大柱子
                                    path_temp = new Path();
                                    paint.setColor(Color.rgb(255, 255, 255));
                                    paint.setShadowLayer(1,1,-1, 0xFF000000);
                                    path_temp.moveTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX),yDiff+basePerY-dip2pxDataOffY);
                                    path_temp.lineTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX)+width,yDiff+basePerY-dip2pxDataOffY);
                                    path_temp.lineTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX)+width,yDiff+basePerY-dip2pxDataHeight-dip2pxDataOffY);
                                    path_temp.lineTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX),yDiff+basePerY-dip2pxDataHeight-dip2pxDataOffY);
                                    path_temp.lineTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX),yDiff+basePerY-dip2pxDataOffY);
                                    canvas.drawPath(path_temp, paint);

                                    //实际柱子
                                    int dataHeight2 = (int) Math.round(dip2pxDataHeight*Double.parseDouble(biBiz.strLev.get(levDataNo)));
                                    path_temp = new Path();
                                    if (modInt == 0) {
                                        paint.setColor(Color.rgb(20, 143, 190));//蓝
                                    } else if (modInt == 1){
                                        paint.setColor(Color.rgb(227, 207, 87));//香蕉黄
                                    }else if (modInt == 2){
                                        paint.setColor(Color.rgb(218, 112, 214));//淡紫色
                                    }else if (modInt == 3){
                                        paint.setColor(Color.rgb(107, 142, 35));//草绿色
                                    }else if (modInt == 4){
                                        paint.setColor(Color.rgb(255, 99, 71));//番茄红
                                    }else if (modInt == 5){
                                        paint.setColor(Color.rgb(64, 224, 205));//青绿色
                                    }else if (modInt == 6){
                                        paint.setColor(Color.rgb(255, 128, 0));//橘黄
                                    }else{
                                        paint.setColor(Color.rgb(64, 181, 224));//蓝
                                    }
                                    paint.clearShadowLayer();
                                    path_temp.moveTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX),yDiff+basePerY-dip2pxDataOffY);
                                    path_temp.lineTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX)+width,yDiff+basePerY-dip2pxDataOffY);
                                    path_temp.lineTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX)+width,yDiff+basePerY-dataHeight2-dip2pxDataOffY);
                                    path_temp.lineTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX),yDiff+basePerY-dataHeight2-dip2pxDataOffY);
                                    path_temp.lineTo(mMapPosX+dip2pxLeftTopStartX+perOffX+(j*dip2pxPerWidthX),yDiff+basePerY-dip2pxDataOffY);
                                    canvas.drawPath(path_temp, paint);

                                    //lvNo = lvNo + 1;
                                    levDataNo = levDataNo + 1;
                                }
                            }

                        }

                        //界面元素
                        paint.setColor(Color.rgb(51, 94, 155));//blue
                        canvas.drawRect(0,0, Common.ScreenWidth, dip2px35,paint);

                        paint.setColor(Color.rgb(255, 0, 0));//red
                        canvas.drawRect(Common.ScreenWidth-dip2px35,0,Common.ScreenWidth,dip2px35, paint);

                        paint.setColor(Color.rgb(255,255,255));
                        paint.setStrokeWidth(6);
                        canvas.drawLine(Common.ScreenWidth-dip2px25,dip2px25, Common.ScreenWidth-dip2px10,dip2px10, paint);//大叉
                        canvas.drawLine(Common.ScreenWidth-dip2px25,dip2px10, Common.ScreenWidth-dip2px10,dip2px25, paint);//大叉


                    } catch (Exception e) {
                        //e.printStackTrace();
                        logger.error(e.getMessage());
                    } finally{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
            rtnFinish();
        }

        /**
         * 退出窗口
         */
        private void rtnFinish(){
            try {
                ((ChartActivity)context).finish();
            } catch (Exception e) {
                //e.printStackTrace();
                logger.error(e.getMessage());
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int ea=event.getAction();

            switch(ea){
                case MotionEvent.ACTION_DOWN:
                    //this.txtTitle.setText("test");
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    //System.out.print(""+lastX);
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    if(touchAreaClose.contains(x, y)){
                        closeBool = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx =(int)event.getRawX() - lastX;
                    int dy =(int)event.getRawY() - lastY;

                    mMapPosX += dx/dip2px10;
                    mMapPosY += dy/dip2px10;

                    //防止地图越界
                    if(mMapPosX >=0) {
                        mMapPosX=0;
                    }else if((biBiz.strX.size()+dip2px5)*dip2pxPerWidthX+dip2pxPerOffX<=Common.ScreenWidth){//这个要在下面的上面
                        mMapPosX = 0;
                    }else if(mMapPosX<=-(((biBiz.strX.size()+dip2px5)*dip2pxPerWidthX)+dip2pxPerOffX-Common.ScreenWidth)) {//这个必须再上面的下面
                        mMapPosX = -(((biBiz.strX.size()+dip2px5)*dip2pxPerWidthX)+dip2pxPerOffX-Common.ScreenWidth);
                    }
                    if(mMapPosY >=allHeight) {
                        mMapPosY=allHeight;
                    }else if(mMapPosY<=0){
                        mMapPosY = 0;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
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
