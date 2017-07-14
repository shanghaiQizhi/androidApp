package com.shizhantouzi;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.q_bean.Common;
import com.q_bean.cnst.Cmd;
import com.q_bizbi.BiBiz;
import com.q_bizcmd.BizFunString;
import com.q_util.AlwaysMarqueeTextView;
import com.q_util.DateTime;
import com.q_util.DeviceTools;
import com.q_util.SocketTools;
import com.shizhantouzi.activity.ChartActivity;
import com.shizhantouzi.activity.IntroActivity;
import com.shizhantouzi.activity.LoginInfoActivity;
import com.shizhantouzi.activity.MoreActivity;
import com.shizhantouzi.activity.ScreenActivity;
import com.shizhantouzi.activity.SetupActivity;
import com.shizhantouzi.activity.TradeSearchByEsActivity;
import com.shizhantouzi.bizCmd.iService.impl.RecieveService;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.shizhantouzi.dialog.HanDan_Human;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class MainActivity extends Activity {
    DateTime dateTime = new DateTime();
    BiBiz biBiz = BiBiz.newInstance();
    RelativeLayout layout;
    TextView tv_ClearHistory;
    Button btn_information;
    Button btn_handan;
    Button btn_about;
    Button btn_setup;
    Button btn_analysis;
    Button btn_video;
    Button btn_logs;
    Button btn_more;
    //RelativeLayout layout2;
    MyHandler myHandler;
    SimpleAdapter listItemAdapter = null;
    ArrayList<HashMap<String, Object>> listHandan;
    TextView txtTitle;
    //private final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    MyBroadcast mybroadcast;
    SocketTools socketTools = null;
    Dialog waiting;
    //ImageButton btnClearHistory;
    ListView listView;
    Logger logger =  Logger.getLogger(MainActivity.class);

    //Bitmap ic_information;

    int dip2px10 ;
    int dip2px20 ;
    int dip2pxIcon75 ;

	/*
	 * launchMode为singleTask的时候，通过intent启到一个activity,如果系统已经存在一个实例
	 * ，系统就会将请求发送到这个实例上，但这个时候，系统就不会再调用通常情况下我们处理请求数据的onCreate方法
	 * ，而是调用onNewIntent方法，如下所示:
	protected void onNewIntent(Intent intent) {
		 super.onNewIntent(intent);
		 setIntent(intent);
		  //在这里取数据
		 Intent intent2 = getIntent();
	}
	*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        //此两段代码必须设置在setContentView()方法之前
        */
        setContentView(R.layout.activity_main);

        //PropertyConfigurator.getConfigurator(this).configure();
        //final FileAppender  fa =  (FileAppender) logger.getAppender(1);
        //fa.setAppend(true);
        //logger.debug(DeviceTools.getDateTime()+" onCreate");
        //if(!fa.getLogFile().exists()){
        //logger.info(",操作,涨跌,建仓,--,平仓,--,投资者,交易所");
        //}

        dip2px10 = DeviceTools.dip2px(this.getApplicationContext(),10);
        dip2px20 = DeviceTools.dip2px(this.getApplicationContext(),20);
        dip2pxIcon75 = DeviceTools.dip2px(this.getApplicationContext(),75);

        firstInit();//首次进入APP初始化

        //通用模块
        layout = (RelativeLayout)findViewById(R.id.layout1);
        layout.setBackgroundColor(Color.WHITE);



        //SurfaceView
        surfaceView view = new surfaceView(this);
        //view.setZOrderOnTop(true);
        //view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        layout.addView(view);

        createElement();

        waiting = DialogWaiting.show(this);
        startService();
        broadcastStart();

        if(Common.socket!=null && !Common.ID.equals("")){
            String jsonOutPut = "{\"cmd\":\""+Cmd.cmd_level1_dengLu+"\",\"mobile\":\""+Common.Mobile+"\",\"password\":\""+Common.Key+"\",\"hanlist\":\"yes\",\"clientType\":\"android\"}";
            //socketTools.sendMessage(Common.dos,jsonOutPut);
            try {
                socketTools.boolPrintWrite(Common.socket.getOutputStream(),jsonOutPut);
            } catch (IOException e) {
                logger.error("发送登录请求失败: "+e.getMessage());
            }
        }

        //跳过所有步骤，直接测试日志页面
        /*
        Intent intent = new Intent(MainActivity.this,LogActivity.class);
        startActivity(intent);
        */

        //跳过所有步骤，直接测试登录页面
        /*
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.putExtra("null", "");
        startActivityForResult(intent,0);
        */
    }

    private void startService(){
        Intent intent = new Intent(this, RecieveService.class);
        if(!isServiceRunning(this,"com.q_bizcmd.iservice.impl.RecieveService")){
            txtTitle.setText(getResources().getString(R.string.app_name) + " - 启动新服务...");
            startService(intent);
        }else{
            txtTitle.setText(getResources().getString(R.string.app_name) + " - 服务重启中...");
            stopService(intent);
            startService(intent);
        }
    }

    private void stopService(){
        Intent intent = new Intent(this, RecieveService.class);
        if(isServiceRunning(this,"com.q_bizcmd.iservice.impl.RecieveService")){
            stopService(intent);
        }
    }

    //给菜单项添加事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //info.id得到listview中选择的条目绑定的id
        String id = String.valueOf(info.id);
        switch (item.getItemId()) {
            case 0:
                Intent intent1 = new Intent(MainActivity.this,TradeSearchByEsActivity.class);
                startActivity(intent1);
                return true;
            case 1:
                Intent intent2 = new Intent(MainActivity.this,TradeSearchByEsActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void createElement() {
        //listView
        listView = (ListView) findViewById(R.id.listView1);
        //ListView listView = new ListView(this);
        //listView.setX(0);
        //listView.setY(80);
        listView.setDividerHeight(0);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listHandan = new ArrayList<HashMap<String, Object>>();
        listItemAdapter = new SimpleAdapter(this,listHandan
                ,R.layout.list_items
                ,new String[] {"jianping", "updown","jianprice","jiantime","pingprice","pingtime","people","jiaoyisuo"}
                ,new int[] {R.id.JIANPING,R.id.UPDOWN,R.id.JIANPRICE,R.id.JIANTIME,R.id.PINGPRICE,R.id.PINGTIME,R.id.PEOPLE,R.id.JIAOYISUO}
        );
        listView.setAdapter(listItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //setTitle("点击第"+arg2+"个项目");
                //showInfo("点击第"+arg2+"个项目");
                showInfo("第 "+arg2+" 个喊单");
            }
        });
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                // Get the list item position
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                int position = info.position;
                HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
                String people = data.get("people").toString();

                menu.setHeaderTitle("操盘手 ["+people+"] 的菜单");
                //menu.setHeaderIcon(R.drawable.icon);
                menu.add(0, 0, 0, "跳转到TA的空间");
                menu.add(0, 1, 0, "跳转到TA的操盘记录");
            }
        });

        //layout.addView(listView);

        listView.bringToFront();

        //Title
        txtTitle = new TextView(this);
        txtTitle.setText(getResources().getString(R.string.app_name) + " - 即时喊单");
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


        int dip2pxHandanStartY = Common.ScreenHeight-DeviceTools.dip2px(this.getApplicationContext(),42);
        int iconWidthHeight = 75;
        int dip2pxIconTextStartX = iconWidthHeight/10;

        //喊单图标按钮及文字
        btn_handan = (Button) findViewById(R.id.btn_handan);
        btn_handan.bringToFront();

        //个人图标按钮及文字
        btn_information  = (Button) findViewById(R.id.btn_information);
        btn_information.bringToFront();

        //说明图标按钮及文字
        btn_about = (Button) findViewById(R.id.btn_about);
        btn_about.bringToFront();

        //设置图标按钮及文字
        btn_setup = (Button) findViewById(R.id.btn_setup);
        btn_setup.bringToFront();

        //分析图标按钮及文字
        btn_analysis = (Button) findViewById(R.id.btn_analysis);
        btn_analysis.bringToFront();

        //直播室图标按钮及文字
        btn_video = (Button) findViewById(R.id.btn_video);
        btn_video.bringToFront();

        //日志图标按钮及文字
        btn_logs = (Button) findViewById(R.id.btn_logs);
        btn_logs.bringToFront();

        //更多图标按钮及文字
        btn_more = (Button) findViewById(R.id.btn_more);
        btn_more.bringToFront();

        //滚动提示
        AlwaysMarqueeTextView scrollTextView;
        scrollTextView = new AlwaysMarqueeTextView(this);
        scrollTextView.setText(getResources().getString(R.string.app_name) + " - 全球交易市场周期(周一至周五)：[夏令时] 新西兰4:00-12:00，澳大利亚6:00-14:00，日本8:00-14:30，新加坡9:00-16:00，德国14:30-23:30，英国15:30-00:30，美国21:00-4:00，[冬令时] 以上时间推迟30分钟。周六日停盘。");
        //tp = txtTitle.getPaint();
        //tp.setFakeBoldText(true);
        scrollTextView.setTextSize(14);
        //textAccount.setLineSpacing(1.6f, 1.4f);
        scrollTextView.setTextColor(Color.rgb(51, 94, 155));
        scrollTextView.setX(0);
        int dip2pxMarqueeStartY = Common.ScreenHeight-DeviceTools.dip2px(this.getApplicationContext(),30+75+20);
        scrollTextView.setY(dip2pxMarqueeStartY);
        scrollTextView.setFocusable(true);
        scrollTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        scrollTextView.setFocusableInTouchMode(true);
        scrollTextView.setMarqueeRepeatLimit(-1);
        scrollTextView.setHorizontalScrollBarEnabled(true);
        scrollTextView.setSingleLine(true);
        scrollTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        layout.addView(scrollTextView);

        /*
        //清空历史
        btnClearHistory = new ImageButton(this);
        btnClearHistory.setImageDrawable(getResources().getDrawable(R.drawable.clearhistory));
        btnClearHistory.setBackgroundColor(Color.TRANSPARENT);

        int dip2pxClearStartX = Common.ScreenWidth-DeviceTools.dip2px(this.getApplicationContext(),55);
        int dip2pxClearStartY = DeviceTools.dip2px(this.getApplicationContext(),35);
        btnClearHistory.setX(dip2pxClearStartX);
        btnClearHistory.setY(dip2pxClearStartY);
        btnClearHistory.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //重新设置按下时的背景图片
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.clearhistory2));
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    //再修改为抬起时的正常图片
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.clearhistory));
                    //btnClearHistory.setEnabled(false);
                    onClearHistory();
                }
                return false;
            }
        });
        layout.addView(btnClearHistory);
        */

        //菜单栏标题
        TextView textView = new TextView(this);
        textView.setTextSize(14);
        int dip2pxMenuStartX = DeviceTools.dip2px(this.getApplicationContext(),20);
        int dip2pxMenuStartY = DeviceTools.dip2px(this.getApplicationContext(),35+10);
        textView.setX(dip2pxMenuStartX);
        textView.setY(dip2pxMenuStartY);
        textView.setTextColor(Color.rgb(51, 94, 155));
        textView.setText("操作　涨跌　建仓价[时间]　平仓价[时间]　投资者　交易所");
        layout.addView(textView);

        //清除历史Text按钮
        tv_ClearHistory  = (TextView) findViewById(R.id.tv_ClearHistory);
        tv_ClearHistory.bringToFront();
    }

    public void showInfo(String str){
        new AlertDialog.Builder(this)
                .setTitle("我的listview")
                .setMessage("介绍..."+str)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    private void Login() {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.putExtra("null", "");
        startActivityForResult(intent,0);
    }

    private boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    private void firstInit() {
        socketTools = new SocketTools();
        Common.ScreenWidth = DeviceTools.getDeviceInfo(this)[0];
        Common.ScreenHeight = DeviceTools.getDeviceInfo(this)[1];

        //这2个路径是啥
        //Common.testxmlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"handan.xml");
        //Common.testxmlPath = Environment.getExternalStorageDirectory()+"/handan.xml";
        Common.testxmlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"shizhantouzi/","handan.xml");
        Common.testxmlPath = Environment.getExternalStorageDirectory()+"/shizhantouzi/handan.xml";

        //log4j日志内部存储路径(含文件名)
        Common.log4jPathAndName = this.getFilesDir().getAbsoluteFile().toString() + File.separator + "shizhantouzi/log4j.log";
        //System.setProperty ("log4jPathAndName", Common.log4jPathAndName);
        //Common.log4jDataPathAndName = this.getFilesDir().getAbsoluteFile().toString() + File.separator + "shizhantouzi/log4jData.log";
        //ALogger aLogger = new ALogger();

        final LogConfigurator logConfigurator = new LogConfigurator();
        //logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "testlog/file.log");
        //logConfigurator.setFileName(context.getFilesDir().getAbsoluteFile().toString() + File.separator + "shizhantouzi/log4j.log");
        logConfigurator.setFileName(Common.log4jPathAndName);
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.DEBUG);
        logConfigurator.setUseFileAppender(true);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();

        logger.info("project start");
	    /*
	    if(!Common.testxmlFile.exists()){
	    	//初始化sd卡测试数据
			List<TestInfo> lTestInfo = new ArrayList<TestInfo>();
			TestInfo testInfo=new TestInfo("begin",DeviceTools.getDateTime());
        	lTestInfo.add(testInfo);
			try {
				FileOutputStream output = new FileOutputStream(Common.testxmlFile);
				DeviceTools.savexml(lTestInfo,output);
			} catch (FileNotFoundException e) {

			}
			//end
	    }
	    */
        myHandler = new MyHandler();

        Common.icon = BitmapFactory.decodeResource(getResources(), R.drawable.gif1);
        //ic_information = BitmapFactory.decodeResource(getResources(), R.drawable.ic_information);

        SharedPreferences settings = getSharedPreferences(Common.path, Context.MODE_PRIVATE);
        if(!settings.getString("IP", "").equals("")){
            Common.IP = settings.getString("IP", "");
        }else{
            Common.IP = Common.IPValue;
        }
        Common.Mobile = settings.getString("mobile", "");
        Common.Key = settings.getString("key", "");

        Common.listTest = new ArrayList<HashMap<String, Object>>();

        //new Timer().schedule(new RemindTask(),60*1000, 60*1000);//检测socket是否连接


    	/*
		Intent intent = new Intent(this, RecieveService.class);
		if(Common.existService){
			Common.existService = false;
			stopService(intent);
		}else{
			Common.existService = true;
			startService(intent);
		}
		*/

    }

    private void broadcastStart(){
        mybroadcast = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.shizhantouzi.ServiceActivityConnectionActivity");
        registerReceiver(mybroadcast, filter);
    }

    private void broadcastStop(){
        unregisterReceiver(mybroadcast);
    }

    private String getZeroToEmpty(String str){
        String result = str;
        if(result.equals("0")){
            result = "";
        }
        return result;
    }


    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String count = intent.getStringExtra("count");
            //Toast.makeText(context, "当前数字为："+count, Toast.LENGTH_LONG).show();
            short cmd = intent.getShortExtra("CMD", (short) 0);
            if(cmd==Cmd.cmd_level1_microlog){
                HashMap<String, Object> map = null;
                if(intent.getIntExtra("i",0)==1){
                    //初始化BI数据不应该放在这里，新客户不会出发到此
                    //biBiz.initData();//初始化BI数据
                    listHandan.clear();
                }
                map = new HashMap<String, Object>();
                map.put("jianping", intent.getStringExtra("jianping"));
                map.put("updown", intent.getStringExtra("updown"));
                map.put("jianprice", intent.getStringExtra("jianprice"));
                //map.put("jiantime","["+DeviceTools.getDateTime(1, intent.getStringExtra("jiantime"))+"]");
                map.put("jiantime","["+dateTime.getSpecialDateTime(intent.getStringExtra("jiantime"))+"]");
                map.put("pingprice", getZeroToEmpty(intent.getStringExtra("pingprice")));
                //map.put("pingtime", "["+DeviceTools.getDateTime(1, intent.getStringExtra("pingtime"))+"]");
                map.put("pingtime","["+dateTime.getSpecialDateTime(intent.getStringExtra("pingtime"))+"]");
                map.put("people", intent.getStringExtra("people"));
                map.put("jiaoyisuo", intent.getStringExtra("jiaoyisuo"));
                listHandan.add(map);
                listItemAdapter.notifyDataSetChanged();
                listView.setSelection(ListView.FOCUS_DOWN);
            }else if(cmd==Cmd.cmd_level1_dengLu){
                logger.debug("未登录用户，准备跳转登录页面");
                if(Common.ID.equals("")){
                    Login();
                }else{

                    txtTitle.setText(getResources().getString(R.string.app_name) + " - 欢迎 "+Common.NickName);


                    if(Common.DingyuePeopleID.equals("")){
                        onTellDingyue();
                    }else{

                    }
                }
            }else if(cmd==Cmd.cmd_level1_dengLuHanList){
                //登陆成功后，初始化BI数据
                biBiz.initData();
                txtTitle.setText(getResources().getString(R.string.app_name) + " - 正在读取今日错过喊单......请等待");
                String jsonOutPut = "{\"cmd\":\""+Cmd.cmd_level1_hanList+"\",\"dingyuePeopleID\":\""+Common.DingyuePeopleID+"\",\"lasthantime\":\""+Common.LastHanDateTime+"\"}";
                //socketTools.sendMessage(Common.dos,jsonOutPut);
                try {
                    socketTools.boolPrintWrite(Common.socket.getOutputStream(), jsonOutPut);
                } catch (IOException e) {
                    txtTitle.setText(getResources().getString(R.string.app_name) +" - 网络断开");
                }
            }else if(cmd==Cmd.cmd_level1_dingyueJiaoyisuo){
                Toast.makeText(context, "设置交易所成功", Toast.LENGTH_LONG).show();
            }else if(cmd==Cmd.cmd_level1_faBuRobot){
                String jianping = intent.getStringExtra("jianping");
                String updown = intent.getStringExtra("updown");
                String jianprice = intent.getStringExtra("jianprice");
                String jiantime = intent.getStringExtra("jiantime");
                String pingtime = intent.getStringExtra("pingtime");
                String people = intent.getStringExtra("people");
                String pingprice = intent.getStringExtra("pingprice");
                String jiaoyisuo = intent.getStringExtra("jiaoyisuo");

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("jianping", jianping);
                map.put("updown", updown);
                map.put("jianprice", jianprice);
                //map.put("jiantime", "["+DeviceTools.getDateTime(1, jiantime)+"]");
                map.put("jiantime", "["+dateTime.getSpecialDateTime(jiantime)+"]");
                map.put("pingprice",getZeroToEmpty(pingprice));
                //map.put("pingtime", "["+DeviceTools.getDateTime(1, pingtime)+"]");
                map.put("pingtime", "["+dateTime.getSpecialDateTime(pingtime)+"]");
                map.put("people", people);
                map.put("jiaoyisuo", jiaoyisuo);

                listHandan.add(map);
                listItemAdapter.notifyDataSetChanged();
                listView.setSelection(ListView.FOCUS_DOWN);
                //showNotification();

            }else if(cmd==Cmd.cmd_level1_hanList){
                int i = intent.getIntExtra("i",0);
                int MaxI = intent.getIntExtra("MaxI",0);
                if(i>0){
                    txtTitle.setText(getResources().getString(R.string.app_name) + " - 获取今日错过喊单完成，正在显示......请等待");
                    HashMap<String, Object> map = null;
                    map = new HashMap<String, Object>();
                    map.put("jianping", intent.getStringExtra("jianping"));
                    map.put("updown", intent.getStringExtra("updown"));
                    map.put("jianprice", intent.getStringExtra("jianprice"));
                    map.put("jiantime","["+DeviceTools.getDateTime(1, intent.getStringExtra("jiantime"))+"]");
                    map.put("pingprice", getZeroToEmpty(intent.getStringExtra("pingprice")));
                    map.put("pingtime", "["+DeviceTools.getDateTime(1, intent.getStringExtra("pingtime"))+"]");
                    map.put("people", intent.getStringExtra("people"));
                    map.put("jiaoyisuo", intent.getStringExtra("jiaoyisuo"));
                    listHandan.add(map);
                    //listItemAdapter.notifyDataSetChanged();
                    //listView.setSelection(ListView.FOCUS_DOWN);
                }
                txtTitle.setText(getResources().getString(R.string.app_name) + " - 欢迎 "+Common.NickName);

                listItemAdapter.notifyDataSetChanged();
                listView.setSelection(ListView.FOCUS_DOWN);

                //最后一批
                if(i==MaxI){
                    if (waiting != null && waiting.isShowing() && !isFinishing()) {
                        waiting.dismiss();
                    }
                }
            }else if(cmd==Cmd.cmd_level1_regist){
                if(Common.ID.equals("")){
                    txtTitle.setText(getResources().getString(R.string.app_name) + " - 注册失败");
                }else{
                    txtTitle.setText(getResources().getString(R.string.app_name) + " - 注册成功，请先修改个人信息");

                }
            }else if(cmd==Cmd.cmd_level1_error){
                String valueString = intent.getStringExtra("VALUE");
                txtTitle.setText(getResources().getString(R.string.app_name) +" - "+ valueString);
            }

            //txtTitle.setText(txtTitle.getText()+"---"+Common.ScreenHeight);
        }
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确定退出并清空登陆信息吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Common.ID = "";
                        SharedPreferences settings = getSharedPreferences(Common.path, Context.MODE_PRIVATE);
                        SharedPreferences.Editor nameEditor = settings.edit();
                        nameEditor.putString("mobile","");
                        nameEditor.putString("key","");
                        nameEditor.commit();
                        broadcastStop();
                        stopService();
                        System.exit(0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        // super.onBackPressed();
    }

    //点击事件
    public void onClick_Event_OnActivity(final View v) {
        switch (v.getId()) {
            case R.id.btn_more://更多按钮事件
                v.setEnabled(false);
                Intent i_more = new Intent(MainActivity.this,MoreActivity.class);
                startActivity(i_more);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //第二个参数每隔一秒会回调一次方法onTick
                    }
                    @Override
                    public void onFinish() {
                        //第一个参数10秒之后会回调onFinish方法
                        v.setEnabled(true);
                    }
                }.start();
                break;
            case R.id.btn_logs://日志按钮事件
                v.setEnabled(false);
                Intent i_logs = new Intent(MainActivity.this,LogActivity.class);
                startActivity(i_logs);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //第二个参数每隔一秒会回调一次方法onTick
                    }
                    @Override
                    public void onFinish() {
                        //第一个参数10秒之后会回调onFinish方法
                        v.setEnabled(true);
                    }
                }.start();
                break;
            case R.id.btn_video://直播室按钮事件
                v.setEnabled(false);
                Intent i_video = new Intent(MainActivity.this,ScreenActivity.class);
                startActivity(i_video);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //第二个参数每隔一秒会回调一次方法onTick
                    }
                    @Override
                    public void onFinish() {
                        //第一个参数10秒之后会回调onFinish方法
                        v.setEnabled(true);
                    }
                }.start();
                break;
            case R.id.btn_analysis://分析按钮事件
                v.setEnabled(false);
                Intent i_analysis = new Intent(MainActivity.this,ChartActivity.class);
                startActivity(i_analysis);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //第二个参数每隔一秒会回调一次方法onTick
                    }
                    @Override
                    public void onFinish() {
                        //第一个参数10秒之后会回调onFinish方法
                        v.setEnabled(true);
                    }
                }.start();
                break;
            case R.id.btn_setup://设置按钮事件
                v.setEnabled(false);
                Intent i_setup = new Intent(MainActivity.this,SetupActivity.class);
                startActivity(i_setup);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //第二个参数每隔一秒会回调一次方法onTick
                    }
                    @Override
                    public void onFinish() {
                        //第一个参数10秒之后会回调onFinish方法
                        v.setEnabled(true);
                    }
                }.start();
                break;
            case R.id.btn_about://说明按钮事件
                v.setEnabled(false);
                Intent i_about = new Intent(MainActivity.this,IntroActivity.class);
                startActivity(i_about);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //第二个参数每隔一秒会回调一次方法onTick
                    }
                    @Override
                    public void onFinish() {
                        //第一个参数10秒之后会回调onFinish方法
                        v.setEnabled(true);
                    }
                }.start();
                break;
            case R.id.btn_handan://喊单按钮事件
                v.setEnabled(false);
                //事件主体
                Dialog dialog1 = new HanDan_Human(MainActivity.this);
                dialog1.setCanceledOnTouchOutside(true);
                dialog1.show();
                //事件主体
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //第二个参数每隔一秒会回调一次方法onTick
                    }
                    @Override
                    public void onFinish() {
                        //第一个参数10秒之后会回调onFinish方法
                        v.setEnabled(true);
                    }
                }.start();
                break;
            case R.id.btn_information://个人按钮事件
                v.setEnabled(false);
                Intent intent = new Intent(MainActivity.this,LoginInfoActivity.class);
                startActivity(intent);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //第二个参数每隔一秒会回调一次方法onTick
                    }
                    @Override
                    public void onFinish() {
                        //第一个参数10秒之后会回调onFinish方法
                        v.setEnabled(true);
                    }
                }.start();
                break;
            case R.id.tv_ClearHistory://清空历史事件
                new AlertDialog.Builder(this).setTitle("确定清空历史喊单吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Common.fa.getLogFile().deleteOnExit();
                                //btnClearHistory.setEnabled(true);
                                //下面待开发，暂时注释
                                /*
                                try {
                                    DeviceTools.writeFileSdcardFile(Common.fileMicrolog.getPath(),"");
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    //e.printStackTrace();
                                }
                                */
                                listHandan.clear();
                                listItemAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //btnClearHistory.setEnabled(true);
                            }
                        }).show();
                break;
        }

        // super.onBackPressed();
    }

    public void onTellDingyue() {
        new AlertDialog.Builder(this).setTitle("您还未订阅操盘手，请访问网页www.shizhantouzi.com登陆后，进入\"操盘大赛\"栏目，未与本平台签约合作的客户可订阅3个，与本平台签约合作的客户无限制，并享有更多服务。")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        // super.onBackPressed();
    }

    /*
    public void showNotification(String title, String text) {
        NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification m_Notification = new Notification();
        //m_Notification.icon = R.drawable.ic_launcher;
        m_Notification.icon = R.mipmap.ic_launcher;
        m_Notification.tickerText = title;
        m_Notification.defaults = Notification.DEFAULT_SOUND;
        m_Notification.flags = Notification.FLAG_AUTO_CANCEL;
        Intent myi = new Intent(this, MainActivity.class);
        PendingIntent m_PendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,myi, 0);
        m_Notification.setLatestEventInfo(getApplicationContext(),getString(R.string.app_name), text,m_PendingIntent);
        m_NotificationManager.notify(1, m_Notification);
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Bundle b=data.getExtras();
                String str=b.getString("cmd");
                if(str.equals("login")){
                    String jsonOutPut = "{\"cmd\":\""+Cmd.cmd_level1_dengLu+"\",\"mobile\":\""+Common.Mobile+"\",\"password\":\""+Common.Key+"\",\"hanlist\":\"no\",\"clientType\":\"android\"}";
                    //socketTools.sendMessage(Common.dos,jsonOutPut);
                    voidPrintWrite(jsonOutPut);
                }
                break;
            default:
                break;
        }
    }

    public void voidPrintWrite(String value) {
        BizFunString bizFunString = new BizFunString();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(Common.socket.getOutputStream());
        } catch (IOException e2) {
            txtTitle.setText(getResources().getString(R.string.app_name) + " - 请求失败");
        }
        String result = bizFunString.getSendMessageWithLength(value);
        pw.println(result);
        pw.flush();
    }

    class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            short cmd = b.getShort("cmd");

        }
    }

    class surfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

        private SurfaceHolder surfaceHolder;
        private Canvas canvas;
        private Paint paint;
        TextView txtTitle = null;
        //private Rect touchAreaTiaoshi;
        //private Rect touchAreaHandan;
        //private Rect touchAreaPerson;
        //private Rect touchAreaIntro;
        private Rect touchAreaClose;
        //private Rect touchAreaSetup;
        //private Rect touchAreaChart;
        //private Rect touchAreaScreen;
        //private Rect touchAreaLog;



        public surfaceView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub

            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            paint = new Paint();
            //touchAreaTiaoshi = new Rect(260,Common.ScreenHeight-85,260+Common.icon.getWidth(),Common.ScreenHeight-5);
            //touchAreaHandan = new Rect(dip2px10+dip2pxIcon75*0, Common.ScreenHeight-dip2pxIcon75-dip2px20,dip2px10+dip2pxIcon75*0+Common.icon.getWidth(),Common.ScreenHeight);
            //touchAreaPerson = new Rect(dip2px10+dip2pxIcon75*1, Common.ScreenHeight-dip2pxIcon75-dip2px20,dip2px10+dip2pxIcon75*1+Common.icon.getWidth(),Common.ScreenHeight);
            //touchAreaIntro = new Rect(dip2px10+dip2pxIcon75*2,Common.ScreenHeight-dip2pxIcon75-dip2px20,dip2px10+dip2pxIcon75*2+Common.icon.getWidth(),Common.ScreenHeight);
            //touchAreaSetup = new Rect(dip2px10+dip2pxIcon75*3,Common.ScreenHeight-dip2pxIcon75-dip2px20,dip2px10+dip2pxIcon75*3+Common.icon.getWidth(),Common.ScreenHeight);
            //touchAreaChart = new Rect(dip2px10+dip2pxIcon75*4,Common.ScreenHeight-dip2pxIcon75-dip2px20,dip2px10+dip2pxIcon75*4+Common.icon.getWidth(),Common.ScreenHeight);
            //touchAreaScreen = new Rect(dip2px10+dip2pxIcon75*5,Common.ScreenHeight-dip2pxIcon75-dip2px20,dip2px10+dip2pxIcon75*5+Common.icon.getWidth(),Common.ScreenHeight);
            //touchAreaLog = new Rect(dip2px10+dip2pxIcon75*6,Common.ScreenHeight-dip2pxIcon75-dip2px20,dip2px10+dip2pxIcon75*6+Common.icon.getWidth(),Common.ScreenHeight);

            touchAreaClose = new Rect(Common.ScreenWidth-70,0,Common.ScreenWidth,70);
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (surfaceHolder) {

                try {
                    canvas = surfaceHolder.lockCanvas();
                    /*
                    paint.setColor(Color.rgb(50, 50, 50));
                    canvas.drawRect(touchAreaPerson,paint);

                    paint.setColor(Color.rgb(155, 155, 155));
                    canvas.drawRect(touchAreaIntro,paint);

                    paint.setColor(Color.rgb(155, 155, 155));
                    canvas.drawRect(touchAreaSetup,paint);

                    paint.setColor(Color.rgb(155, 155, 155));
                    canvas.drawRect(touchAreaChart,paint);

                    paint.setColor(Color.rgb(155, 155, 155));
                    canvas.drawRect(touchAreaScreen,paint);
                    */
                    paint.setColor(Color.rgb(255, 255, 255));//white
                    canvas.drawRect(0,0, Common.ScreenWidth,Common.ScreenHeight,paint);
                    //paint.setColor(Color.rgb(150, 150, 150));
                    //canvas.drawRect(70,70,70,70,paint);
                    paint.setColor(Color.rgb(51, 94, 155));//blue
                    //head bg
                    int dip2pxTopBlueHeight35 = DeviceTools.dip2px(this.getContext(),35);
                    canvas.drawRect(0,0, Common.ScreenWidth, dip2pxTopBlueHeight35,paint);
                    //bottom bg
                    canvas.drawRect(0,Common.ScreenHeight-dip2px10*3-dip2pxIcon75, Common.ScreenWidth, Common.ScreenHeight,paint);
                    //icon
                    //canvas.drawBitmap(Common.icon, dip2px10+dip2pxIcon75*0, Common.ScreenHeight-dip2pxIcon75-dip2px20, paint);//喊单
                    //canvas.drawBitmap(ic_information, dip2px10+dip2pxIcon75*1, Common.ScreenHeight-dip2pxIcon75-dip2px20, paint);//个人
                    //canvas.drawBitmap(Common.icon, dip2px10+dip2pxIcon75*2, Common.ScreenHeight-dip2pxIcon75-dip2px20, paint);//说明
                    //canvas.drawBitmap(Common.icon, dip2px10+dip2pxIcon75*3, Common.ScreenHeight-dip2pxIcon75-dip2px20, paint);//设置
                    //canvas.drawBitmap(Common.icon, dip2px10+dip2pxIcon75*4, Common.ScreenHeight-dip2pxIcon75-dip2px20, paint);//统计
                    //canvas.drawBitmap(Common.icon, dip2px10+dip2pxIcon75*5, Common.ScreenHeight-dip2pxIcon75-dip2px20, paint);//直播室
                    //canvas.drawBitmap(Common.icon, dip2px10+dip2pxIcon75*6, Common.ScreenHeight-dip2pxIcon75-dip2px20, paint);//日志
                    //paint.setColor(Color.rgb(255, 0, 0));//个人色块
                    //canvas.drawRect(100,Common.ScreenHeight-85,100+Common.icon.getWidth(),Common.ScreenHeight-5, paint);
                    paint.setColor(Color.rgb(255, 0, 0));//red
                    canvas.drawRect(Common.ScreenWidth-dip2pxTopBlueHeight35,0,Common.ScreenWidth,dip2pxTopBlueHeight35, paint);//退出色块
                    paint.setColor(Color.rgb(255,255,255));

                    paint.setStrokeWidth(6);
                    canvas.drawLine(Common.ScreenWidth-dip2px20,dip2px20, Common.ScreenWidth-dip2px10,dip2px10, paint);//大叉
                    canvas.drawLine(Common.ScreenWidth-dip2px20,dip2px10, Common.ScreenWidth-dip2px10,dip2px20, paint);//大叉

                } catch (Exception e) {

                } finally{
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            /*
            if(touchAreaHandan.contains(x, y)){//个人
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Dialog dialog1 = new HanDan_Human(MainActivity.this);
                    dialog1.setCanceledOnTouchOutside(true);
                    dialog1.show();
                }
                return true;
            }else if(touchAreaPerson.contains(x, y)){//个人
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this,LoginInfoActivity.class);
                    //Bundle bundle = new Bundle();
                    //bundle.putString("cityid",cityArray[num][0]);
                    //intent.putExtras(bundle);
                    startActivity(intent);
                }
                return true;
            }else if(touchAreaIntro.contains(x, y)){//介绍
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this,IntroActivity.class);
                    startActivity(intent);
                }

                return true;
            }else if(touchAreaSetup.contains(x, y)){//设置
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this,SetupActivity.class);
                    startActivity(intent);
                }
                return true;
            }else if(touchAreaChart.contains(x, y)){//统计
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this,ChartActivity.class);
                    startActivity(intent);
                }
                return true;
            }else if(touchAreaScreen.contains(x, y)){//直播室
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this,ScreenActivity.class);
                    startActivity(intent);
                }
                return true;
            }else if(touchAreaLog.contains(x, y)){//日志
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(MainActivity.this,LogActivity.class);
                    startActivity(intent);
                }
                return true;
            }else */if(touchAreaClose.contains(x, y)){//关闭
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    onBackPressed();
                }
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
