package com.shizhantouzi.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.q_bean.Common;
import com.q_bean.UdpBiz;
import com.q_util.DateTime;
import com.q_util.SocketTools;
import com.q_util.StringFun;
import com.shizhantouzi.R;

import org.apache.log4j.Logger;

public class ScreenActivity extends Activity {
    Logger logger = Logger.getLogger(ScreenActivity.class);

    Bitmap bm = null;
    TextPaint tp = null;
    TextView txtTitle=null;
    TextView txtMain=null;
    TextView txtToServer=null;
    TextView txtToSend=null;
    TextView txtRec=null;
    RelativeLayout layout = null;
    ImageButton imgBtnLeft;
    ImageButton imgBtnRight;
    ImageButton imgBtnUp;
    ImageButton imgBtnDown;
    boolean boolTaskRecSend = false;
    SocketTools socketTools=null;
    //ImageView image1=null;
    DateTime dateTime = null;
    StringFun stringFun = null;

    //需要的全局变量
    String sendlistexist = null;
    String netip = null;
    String netport = null;
    String localip = null;
    String localport = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        logger.info("启动视频页面");

        dateTime = new DateTime();
        stringFun = new StringFun();

        if(Common.ID.equals("")){
            new AlertDialog.Builder(this).setTitle("请先登陆！")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ScreenActivity.this.finish();
                        }
                    }).show();
        }

        UdpBiz.keshiHeight = Common.ScreenHeight-70-20;
        UdpBiz.keshiWidth = Common.ScreenWidth/4*3;



        //通用模块
        layout = (RelativeLayout)findViewById(R.id.activity_screen);
        layout.setBackgroundColor(Color.WHITE);

        //SurfaceView
        logger.info("准备加载surfaceView");
        surfaceView view = new surfaceView(this);
        //view.setZOrderOnTop(true);
        //view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        layout.addView(view);
        logger.info("加载surfaceView完成");

        createElement();
        logger.info("createElement完成");
        initSocket();
        logger.info("全部完成");
    }

    /**
     * 获取本地wifi地址
     * @return
     */
    private String getWifiIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    /**
     * 初始化Socket
     */
    private void initSocket(){
        if(UdpBiz.inetServer==null){
            socketTools = new SocketTools();

            Random random = new Random();
            UdpBiz.clientport =  random.nextInt(8999)+1000;
            try {
                UdpBiz.inetServer = InetAddress.getByName(UdpBiz.serverIP);
            } catch (UnknownHostException e1) {
                txtMain.setText(dateTime.GetNowDate(6)+" 网络初始化失败");
                logger.error(e1.getMessage());
            }
            try {
                UdpBiz.socket = new DatagramSocket(UdpBiz.clientport);
            } catch (SocketException e1) {
                txtMain.setText(dateTime.GetNowDate(6)+" 网络初始化失败");
                logger.error(e1.getMessage());
            }
            try {
                UdpBiz.socket.setSoTimeout(UdpBiz.socketTimeOut);
            } catch (SocketException e1) {
                txtMain.setText(dateTime.GetNowDate(6)+" 网络初始化超时");
                logger.error(e1.getMessage());
            }
            UdpBiz.localhost = getWifiIpAddress();
			/*
			int maxMemory = ((int) Runtime.getRuntime().maxMemory())/1024/1024;
			int totalMemory = ((int) Runtime.getRuntime().totalMemory())/1024/1024;
			int freeMemory = ((int) Runtime.getRuntime().freeMemory())/1024/1024;
			*/
            txtMain.setText(dateTime.GetNowDate(6)+" 初始化["+UdpBiz.localhost+":"+UdpBiz.clientport+"]");
        }else{
            txtMain.setText(dateTime.GetNowDate(6)+" 已存在["+UdpBiz.localhost+":"+UdpBiz.clientport+"]");
        }

        UdpBiz.threadRec = true;
        UdpBiz.bSurfaceView = true;
        UdpBiz.threadToServer = true;
        UdpBiz.threadToSend = false;

        new Thread(taskToServer).start();
        new Thread(taskRec).start();
    }

    /**
     * 创建界面控件
     */
    private void createElement() {
		/*
		image1 = new ImageView(this);
		image1.setX(0);
		image1.setY(70);
		image1.setMaxWidth(UdpBiz.keshiWidth);
		image1.setMaxHeight(UdpBiz.keshiHeight);
		image1.setMinimumWidth(UdpBiz.keshiWidth);
		image1.setMinimumHeight(UdpBiz.keshiHeight);
		layout.addView(image1);
		*/
        //Title
        txtTitle = new TextView(this);
        txtTitle.setText(getResources().getString(R.string.app_name) + " - 直播室");
        //tp = txtTitle.getPaint();
        //tp.setFakeBoldText(true);
        txtTitle.setTextSize(16);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        txtTitle.setTextColor(Color.rgb(255, 255, 255));
        txtTitle.setX(10);
        txtTitle.setY(15);
        layout.addView(txtTitle);

        txtMain = new TextView(this);
        txtMain.setText("系统状态");
        //tp = txtTitle.getPaint();
        //tp.setFakeBoldText(true);
        txtMain.setTextSize(12);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        txtMain.setTextColor(Color.rgb(51, 94, 155));
        txtMain.setX(5);
        txtMain.setY(Common.ScreenHeight-70-10*2);
        layout.addView(txtMain);

        txtToServer = new TextView(this);
        txtToServer.setText("至服务器状态");
        //tp = txtTitle.getPaint();
        //tp.setFakeBoldText(true);
        txtToServer.setTextSize(12);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        txtToServer.setTextColor(Color.rgb(51, 94, 155));
        txtToServer.setX(Common.ScreenWidth/10*4);
        txtToServer.setY(Common.ScreenHeight-70-10*2);
        layout.addView(txtToServer);

        txtToSend = new TextView(this);
        txtToSend.setText("至发送者状态");
        //tp = txtTitle.getPaint();
        //tp.setFakeBoldText(true);
        txtToSend.setTextSize(12);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        txtToSend.setTextColor(Color.rgb(51, 94, 155));
        txtToSend.setX(5);
        txtToSend.setY(Common.ScreenHeight-70);
        layout.addView(txtToSend);

        txtRec = new TextView(this);
        txtRec.setText("接收状态");
        //tp = txtTitle.getPaint();
        //tp.setFakeBoldText(true);
        txtRec.setTextSize(12);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        txtRec.setTextColor(Color.rgb(51, 94, 155));
        txtRec.setX(Common.ScreenWidth/10*4);
        txtRec.setY(Common.ScreenHeight-70);
        layout.addView(txtRec);

        //向左
        imgBtnLeft = new ImageButton(this);
        imgBtnLeft.setImageDrawable(getResources().getDrawable(R.drawable.fxleft));
        imgBtnLeft.setBackgroundColor(Color.TRANSPARENT);
        imgBtnLeft.setX(5);
        imgBtnLeft.setY(Common.ScreenHeight/2);
        layout.addView(imgBtnLeft);

        //向右
        imgBtnRight = new ImageButton(this);
        imgBtnRight.setImageDrawable(getResources().getDrawable(R.drawable.fxright));
        imgBtnRight.setBackgroundColor(Color.TRANSPARENT);
        imgBtnRight.setX(UdpBiz.keshiWidth-50);
        imgBtnRight.setY(Common.ScreenHeight/2);
        layout.addView(imgBtnRight);

        //向上
        imgBtnUp = new ImageButton(this);
        imgBtnUp.setImageDrawable(getResources().getDrawable(R.drawable.fxup));
        imgBtnUp.setBackgroundColor(Color.TRANSPARENT);
        imgBtnUp.setX(UdpBiz.keshiWidth/2);
        imgBtnUp.setY(70+10);
        layout.addView(imgBtnUp);

        //向下
        imgBtnDown = new ImageButton(this);
        imgBtnDown.setImageDrawable(getResources().getDrawable(R.drawable.fxdown));
        imgBtnDown.setBackgroundColor(Color.TRANSPARENT);
        imgBtnDown.setX(UdpBiz.keshiWidth/2);
        imgBtnDown.setY(Common.ScreenHeight-20-70-40);
        layout.addView(imgBtnDown);

        //聊天列表
        ListView listView = new ListView(this);
        //listView.layout(UdpBiz.keshiWidth,70 , 0, UdpBiz.keshiHeight);
        listView.setX(UdpBiz.keshiWidth);
        listView.setY(70);
        //ListView listView = new ListView(this);
        //listView.setX(0);
        //listView.setY(80);
        listView.setDividerHeight(0);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        ArrayList<HashMap<String, Object>> listHandan = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map;

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--今晚21:30非农数据");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--今晚非农数据参考jin10.com");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--今晚21:30非农数据");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--今晚非农数据参考jin10.com");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);

        map = new HashMap<String, Object>();
        map.put("nicknamecontent","多金--语音");
        listHandan.add(map);


        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listHandan
                ,R.layout.list_talk
                ,new String[] {"nicknamecontent"}
                ,new int[] {R.id.NICKNAMECONTENT}
        );
        listView.setAdapter(listItemAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //setTitle("点击第"+arg2+"个项目");
                //showInfo("点击第"+arg2+"个项目");
                //showInfo("第 "+arg2+" 个喊单");
            }
        });
        listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("弹出长按菜单");
                menu.add(0, 0, 0, "弹出长按菜单0");
                menu.add(0, 1, 0, "弹出长按菜单1");
            }
        });
        layout.addView(listView);
        listView.bringToFront();
    }

    /**
     * 发送UDP给发送者
     * @param buf 发送内容
     * @return null
     */
    private String udpSendToSend(String buf){
        byte[] buffer = buf.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, UdpBiz.inetSend, UdpBiz.sendPort);
        try {
            UdpBiz.socket.send(packet);//发送数据
        } catch (IOException e) {
            logger.error("发送UDP数据失败"+e.getMessage());
            return e.getMessage();
        }
        return null;
    }


    /**
     * 发送UDP给服务端
     * @param buf 发送内容
     * @return
     */
    private String udpSendToServer(String buf){
        byte[] buffer = buf.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, UdpBiz.inetServer, UdpBiz.serverport);
        try {
            UdpBiz.socket.send(packet);//发送数据
        } catch (IOException e) {
            logger.error("发送UDP数据失败"+e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    /**
     * 发送数据至服务器端Handler
     */
    Handler handlerToServer = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String title = data.getString("title");

            txtToServer.setText(dateTime.GetNowDate(6)+" "+title);
        }
    };

    /**
     * 发送数据至服务器端
     */
    Runnable taskToServer = new Runnable() {
        @Override
        public void run() {
            String result = "";
            while(UdpBiz.threadToServer){
                result = "["+UdpBiz.localhost+":"+UdpBiz.clientport+"] to server";
                String buf = "{\"cmd\":\""+UdpBiz.cmdToServerRand+"\"" +
                        ",\"id\":\""+Common.ID+"\",\"key\":\"test\"" +
                        ",\"localip\":\""+UdpBiz.localhost+"\",\"localport\":\""+UdpBiz.clientport+"\"" +
                        ",\"friendid\":\"\"}";

                String s =udpSendToServer(buf);
                if(s!=null){
                    result = "至服务端发送错误";
                }

                try {
                    Thread.sleep(UdpBiz.socketSendTime);
                } catch (InterruptedException e1) {
                    result = "至服务端心跳连接发送超时";
                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                message.setData(bundle);
                bundle.putString("title", result);
                handlerToServer.sendMessage(message);
            }
        }
    };

    /**
     * 至发送端心跳Handler
     */
    Handler handlerToSend = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String title = data.getString("title");
            txtToSend.setText(dateTime.GetNowDate(6)+" "+title);
        }
    };

    /**
     * 至发送端心跳线程
     */
    Runnable taskToSend = new Runnable() {
        @Override
        public void run() {
            String result = "";
            while(UdpBiz.threadToSend){
                result = "to sender ["+UdpBiz.sendIp+":"+UdpBiz.sendPort+"]";
                String receiveIP = null;
                String receivePort = null;
                if(sendlistexist.equals("yesLocal")){
                    receiveIP = localip;
                    receivePort = localport;
                }else if(sendlistexist.equals("yesNet")){
                    receiveIP = netip;
                    receivePort = netport;
                }
                String buf = "{\"cmd\":\""+UdpBiz.cmdHeartToSend+"\",\"sendlistexist\":\""+sendlistexist+"\",\"receiveId\":\""+Common.ID+"\",\"receiveIP\":\""+receiveIP+"\",\"receivePort\":\""+receivePort+"\",\"width\":\""+UdpBiz.keshiWidth+"\",\"height\":\""+UdpBiz.keshiHeight+"\"}";
                String s = udpSendToSend(buf);
                if(s!=null){
                    result = "至发送端发送失败";
                }
                try {
                    Thread.sleep(UdpBiz.socketSendTime);
                } catch (InterruptedException e1) {
                    result = "至发送端发送超时";
                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                message.setData(bundle);
                bundle.putString("title", result);
                handlerToSend.sendMessage(message);
            }
        }
    };

    /**
     * 接受服务器端返回数据Handler
     */
    Handler handlerRec = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String title = data.getString("title");
            String mainTitle = data.getString("mainTitle");
            if(!title.equals("")){
                txtRec.setText(dateTime.GetNowDate(6)+" "+title);
            }
            if(!mainTitle.equals("")){
                txtMain.setText(dateTime.GetNowDate(6)+" "+mainTitle);
            }
			/*
			if(bm!=null){
				image1.setImageBitmap(bm);
			}
			*/
        }
    };

    /**
     * 接受服务端返回数据
     */
    Runnable taskRec = new Runnable() {
        @Override
        public void run() {
            DecimalFormat df = new DecimalFormat("0.0");
            //data数据专用
            ArrayList<Integer> UDPReceiveOKList = new ArrayList<Integer>();
            //Image ImageReceiveClient;
            //String data = "data:";
            int size = 1*1024;//接收是1024为一单位，和data开头没关系
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            HashMap<String,byte[]> UDPReceiveOKListMap = new HashMap<String, byte[]>();
            ArrayList<byte[]> UDPReceiveOKByteList = new ArrayList<byte[]>();
            String piciPre = "";
            //data数据专用

            byte[] imagebyte=null;
            int allSize=0;
            int maxMemory=0;
            int totalMemory=0;
            int freeMemory=0;
            //SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(null);
            StringBuffer infoMain = new StringBuffer();
            StringBuffer infoRecFromSend = new StringBuffer();

            while(UdpBiz.threadRec){
                //infoMain.append(dateTime.GetNowDate(6)+" ");
                //infoRecFromSend.append(dateTime.GetNowDate(6)+" ");

                byte[] buffer = new byte[size];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    UdpBiz.socket.receive(packet);
                } catch (IOException e) {
                    if(e.getMessage().equals("Receive timed out")){
                        infoRecFromSend.append("超时.");
                        //result=dateTime.GetNowDate(6)+" 接收超时";
                    }else{
                        infoRecFromSend.append("失败.");
                        //result=dateTime.GetNowDate(6)+" 接收失败";
                    }
                }
                byte[] packetdata = packet.getData();
                String msg = new String(packet.getData(), 0, packet.getLength());
                String cmdStr = stringFun.getString(msg, "\"cmd\":\"", "\"");
                short cmd=0;
                if(!cmdStr.equals("")){
                    cmd = Short.parseShort(cmdStr);
                }
                //Log.e(Common.projectName, msg);
                if(cmd==UdpBiz.cmdToServerRand){
                    netip = stringFun.getString(msg, "\"netip\":\"", "\"");
                    netport = stringFun.getString(msg, "\"netport\":\"", "\"");
                    localip = stringFun.getString(msg, "\"localip\":\"", "\"");
                    localport = stringFun.getString(msg, "\"localport\":\"", "\"");
                    String sendIp = stringFun.getString(msg, "\"sendIp\":\"", "\"");
                    String sendPort = stringFun.getString(msg, "\"sendPort\":\"", "\"");
                    sendlistexist = stringFun.getString(msg, "\"sendlistexist\":\"", "\"");
                    if(sendlistexist.equals("no")){
                        //result=dateTime.GetNowDate(6)+" 暂无直播。外网 "+netip+":"+netport+" 内网 "+localip+":"+localport;
                    }else{
                        infoMain.append("mine ["+netip+":"+netport+"].");
                        //result=dateTime.GetNowDate(6)+" ["+sendIp+":"+sendPort+"]正在直播.["+netip+":"+netport+"]["+localip+":"+localport+"]";
                        //重置发送者信息
                        if(!sendIp.equals("")){
                            UdpBiz.sendIp = sendIp;
                            try {
                                UdpBiz.inetSend=InetAddress.getByName(sendIp);
                            } catch (UnknownHostException e) {
                                logger.error("cmdToServerRand错误: "+e.getMessage());
                            }
                            UdpBiz.sendPort=Integer.parseInt(sendPort);
                        }
                        if(!UdpBiz.threadToSend){
                            UdpBiz.threadToSend = true;
                            new Thread(taskToSend).start();
                        }

                    }
                }else if(msg.startsWith("data:")){
                    //infoRecFromSend.append("get data.");
                    String[] msgarray = msg.split(":");
                    String no = msgarray[1];//分段批次
                    String pici = msgarray[2];//屏幕批次
                    int realDataLength = Integer.parseInt(msgarray[3]);

                    maxMemory = ((int) Runtime.getRuntime().maxMemory())/1024/1024;
                    totalMemory = ((int) Runtime.getRuntime().totalMemory())/1024/1024;
                    freeMemory = ((int) Runtime.getRuntime().freeMemory())/1024/1024;

                    infoRecFromSend.append("["+maxMemory+","+totalMemory+","+freeMemory+"]["+no+"/"+pici+"].");
                    infoRecFromSend.append(df.format((float)realDataLength/1024)+"k.");

                    //String test = "msgarray[0]="+msgarray[0]+",[1]="+msgarray[1]+",[2]="+msgarray[2]+"piciPre="+piciPre;

                    if(!piciPre.equals(pici) && !piciPre.equals("")){
                        //Commonbiz.isRun_NoComplete = false;
                        //冒泡，按小->大排序
                        int b;
                        for(int i=0;i<UDPReceiveOKList.size();i++){
                            for(int j=0;j<UDPReceiveOKList.size()-i-1;j++){
                                if(UDPReceiveOKList.get(j)>=UDPReceiveOKList.get(j+1))
                                {
                                    b=UDPReceiveOKList.get(j);
                                    UDPReceiveOKList.set(j, UDPReceiveOKList.get(j+1));
                                    UDPReceiveOKList.set(j+1,b);
                                }
                            }
                        }
                        for(int i=0;i<UDPReceiveOKList.size();i++){
                            Iterator<String> itor = UDPReceiveOKListMap.keySet().iterator();
                            while(itor.hasNext()){
                                String key = (String)itor.next();
                                if(UDPReceiveOKList.get(i)==Integer.parseInt(key)){
                                    //byte[] bytes1 = (data + key + ":"+"0:").getBytes();
                                    //byte[] bytes2 = UDPReceiveOKListMap.get(key);
                                    //byte[] bytes = new byte[bytes2.length-bytes1.length];
                                    //System.arraycopy(bytes2,bytes1.length,bytes,0,bytes2.length-bytes1.length);
                                    //UDPReceiveOKByteList.add(bytes);
                                    UDPReceiveOKByteList.add(UDPReceiveOKListMap.get(key));
                                    break;
                                }
                            }
                        }
                        for(int i=0;i<UDPReceiveOKByteList.size();i++){
                            byte[] bytes = UDPReceiveOKByteList.get(i);
                            out.write(bytes,0,bytes.length);
                        }
                        imagebyte = out.toByteArray();
                        infoRecFromSend.append(df.format((float)imagebyte.length/1024)+"k.");
                        //allSize = df.format(imagebyte.length*0.0001024);

                        //装载bitmap
                        //BitmapFactory.Options options = new BitmapFactory.Options();
                        //options.inSampleSize = 1;
                        InputStream input = new ByteArrayInputStream(imagebyte);
                        //清空内存
                        if (imagebyte != null) {
                            imagebyte = null;
                        }

                        //加软引用，依然内存溢出

                        SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(BitmapFactory.decodeStream(input));
                        if(softRef.get()!=null){
                            bm = softRef.get();
                        }
                        softRef.clear();
						/*
						if(BitmapFactory.decodeStream(input)!=null){
							bm = BitmapFactory.decodeStream(input);
						}
						*/
                        if(bm==null){
                            infoRecFromSend.append("bmp null.");
                            //sampleSizeAndNull=dateTime.GetNowDate(6)+" 图像转化null,["+allSize+"/"+softBmpSize+"k],["+maxMemory+","+totalMemory+","+freeMemory+"]";
                        }else{
                            infoRecFromSend.append("bmp ok.");
                            //sampleSizeAndNull=dateTime.GetNowDate(6)+" 图像已转化,["+allSize+"/"+softBmpSize+"k],["+maxMemory+","+totalMemory+","+freeMemory+"]";
                        }
                        infoRecFromSend.append(pici+" over.");
                        //清空内存
                        if (input != null) {
                            try {
                                input.close();
                            } catch (IOException e) {
                                logger.error("清空内存错误: "+e.getMessage());
                            }
                        }
                        //装载bitmap

                        //result = allSize+"k 接受完毕,来源客户端,重新循环";
                        //result = dateTime.GetNowDate(6)+" ["+UdpBiz.sendIp+":"+UdpBiz.sendPort+"]->["+UdpBiz.localhost+":"+UdpBiz.clientport+"] "+allSize+"k"+" ["+pici+"/"+piciPre+"]";
                        out.reset();
                        UDPReceiveOKListMap.clear();
                        UDPReceiveOKByteList.clear();
                        UDPReceiveOKList.clear();
                    }

                    //判断接受no是否已存在于接收集合
                    boolean existNo = false;
                    for(int i=0;i<UDPReceiveOKList.size();i++){
                        if(UDPReceiveOKList.get(i)==Integer.parseInt(no)){
                            existNo = true;
                            break;
                        }
                    }
                    //不存在则添加no
                    if(!existNo){
                        byte[] realData = new byte[realDataLength];
                        System.arraycopy(packetdata,("data:0:0:"+String.valueOf(realDataLength)+":").length(),realData,0,realDataLength);
                        UDPReceiveOKListMap.put(no, realData);
                        UDPReceiveOKList.add(Integer.parseInt(no));

                        //UDPReceiveOKListMap.put(no, packetdata);
                        //UDPReceiveOKList.add(Integer.parseInt(no));
                    }

                    //result = "共接收"+UDPReceiveOKListMap.size()+"批,每批"+size+"b,来源客户端,循环";
                    //System.out.println(Commonbiz.msg);
                    //logger.info(Commonbiz.msg+" "+Commonbiz.clientport);
                    //infoRecFromSend.append(pici+"/"+piciPre);
                    piciPre = pici;
                }else{
                    infoRecFromSend.append("other cmd.");
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.setData(bundle);
                bundle.putString("title", infoRecFromSend.toString());
                bundle.putString("mainTitle", infoMain.toString());
                handlerRec.sendMessage(message);

                infoMain.setLength(0);
                infoRecFromSend.setLength(0);
            }
        }
    };

    /**
     * SurfaceView显示界面
     *
     */
    class surfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{
        int lastX, lastY;
        private SurfaceHolder surfaceHolder;
        private Canvas canvas;
        private Paint paint;
        TextView txtTitle = null;
        private Rect touchAreaClose;
        private Rect touchAreaLeft;
        boolean isTouchAreaLeft=false;
        private Rect touchAreaRight;
        boolean isTouchAreaRight=false;
        private Rect touchAreaUp;
        boolean isTouchAreaUp=false;
        private Rect touchAreaDown;
        boolean isTouchAreaDown=false;

        public surfaceView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub

            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            paint = new Paint();
            touchAreaClose = new Rect(Common.ScreenWidth-70,0,Common.ScreenWidth,70);
            touchAreaLeft = new Rect(0,70,50,UdpBiz.keshiHeight);
            touchAreaRight = new Rect(UdpBiz.keshiWidth-50,70,UdpBiz.keshiWidth,UdpBiz.keshiHeight);
            touchAreaUp = new Rect(0,70,UdpBiz.keshiWidth,70+50);
            touchAreaDown = new Rect(0,(Common.ScreenHeight-20-50-70),UdpBiz.keshiWidth,(Common.ScreenHeight-20-70));
        }

        @Override
        public void run() {
            while(UdpBiz.bSurfaceView){
                synchronized (surfaceHolder) {
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
                        //paint.setAlpha(255);
                        paint.setColor(Color.rgb(255, 255, 255));//white
                        canvas.drawRect(0,0, Common.ScreenWidth,Common.ScreenHeight,paint);

                        paint.setColor(Color.rgb(51, 94, 155));//blue
                        canvas.drawRect(0,0, Common.ScreenWidth, 70,paint);

                        paint.setColor(Color.rgb(255, 0, 0));//red
                        canvas.drawRect(Common.ScreenWidth-70,0,Common.ScreenWidth,70, paint);

                        paint.setColor(Color.rgb(255,255,255));
                        canvas.drawLine(Common.ScreenWidth-50,50, Common.ScreenWidth-20,20, paint);//大叉
                        canvas.drawLine(Common.ScreenWidth-50,20, Common.ScreenWidth-20,50, paint);//大叉

                        if(bm!=null){
                            Rect r1 = new Rect(0,0,bm.getWidth(),bm.getHeight());
                            Rect r2 = new Rect(0,70,UdpBiz.keshiWidth,UdpBiz.keshiHeight);
                            canvas.drawBitmap(bm, r1, r2, paint);
                        }
                        //bm.recycle();

                        //canvas.drawText(UdpBiz.screenX+"*"+UdpBiz.screenY, Common.ScreenWidth/2, 30, paint);

                        if(isTouchAreaLeft){
                            paint.setColor(Color.rgb(51, 94, 155));//blue
                            paint.setAlpha(100);
                            canvas.drawRect(touchAreaLeft, paint);
                            isTouchAreaLeft = false;
                            udpSendToSend("{\"cmd\":\""+UdpBiz.cmdMoveArea+"\",\"direct\":\"3\",\"id\":\""+Common.ID+"\"}");
                        }else  if(isTouchAreaRight){
                            paint.setColor(Color.rgb(51, 94, 155));//blue
                            paint.setAlpha(100);
                            canvas.drawRect(touchAreaRight, paint);
                            isTouchAreaRight = false;
                            udpSendToSend("{\"cmd\":\""+UdpBiz.cmdMoveArea+"\",\"direct\":\"4\",\"id\":\""+Common.ID+"\"}");
                        }else if(isTouchAreaUp){
                            paint.setColor(Color.rgb(51, 94, 155));//blue
                            paint.setAlpha(100);
                            canvas.drawRect(touchAreaUp, paint);
                            isTouchAreaUp = false;
                            udpSendToSend("{\"cmd\":\""+UdpBiz.cmdMoveArea+"\",\"direct\":\"1\",\"id\":\""+Common.ID+"\"}");
                        }else if(isTouchAreaDown){
                            paint.setColor(Color.rgb(51, 94, 155));//blue
                            paint.setAlpha(100);
                            canvas.drawRect(touchAreaDown, paint);
                            isTouchAreaDown = false;
                            udpSendToSend("{\"cmd\":\""+UdpBiz.cmdMoveArea+"\",\"direct\":\"2\",\"id\":\""+Common.ID+"\"}");
                        }
                    } catch (Exception e) {

                        UdpBiz.threadRec = false;//这个线程先关
                        UdpBiz.bSurfaceView = false;
                        UdpBiz.threadToServer = false;
                        UdpBiz.threadToSend = false;
                        UdpBiz.bSurfaceView=false;

                    } finally{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            int ea=event.getAction();

            switch(ea){
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    if(touchAreaClose.contains(x, y)){
                        //Common.TouchFirst = true;
                        UdpBiz.threadRec = false;//这个线程先关
                        UdpBiz.bSurfaceView = false;
                        UdpBiz.threadToServer = false;
                        UdpBiz.threadToSend = false;
                        ScreenActivity.this.finish();
                        return true;
                    }else if(touchAreaLeft.contains(x, y)){
                        isTouchAreaLeft = true;
                        return true;
                    }else if(touchAreaRight.contains(x, y)){
                        isTouchAreaRight = true;
                        return true;
                    }else if(touchAreaUp.contains(x, y)){
                        isTouchAreaUp = true;
                        return true;
                    }else if(touchAreaDown.contains(x, y)){
                        isTouchAreaDown = true;
                        return true;
                    }
                    break;
				/*
				case MotionEvent.ACTION_MOVE:
					int dx =(int)event.getRawX() - lastX;
					int dy =(int)event.getRawY() - lastY;
					UdpBiz.screenX = UdpBiz.screenX - dx;
					UdpBiz.screenY = UdpBiz.screenY - dy;
					if(UdpBiz.screenX<0){
						UdpBiz.screenX = 0;
					}
					if(UdpBiz.screenY<0){
						UdpBiz.screenY = 0;
					}

					break;
				*/
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
