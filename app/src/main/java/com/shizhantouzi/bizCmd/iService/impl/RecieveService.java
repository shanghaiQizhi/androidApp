package com.shizhantouzi.bizCmd.iService.impl;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;

import com.q_bean.Common;
import com.q_bean.cnst.Cmd;
import com.q_bizbi.BiBiz;
import com.q_bizcmd.BizFunString;
import com.q_util.DateTime;
import com.q_util.DeviceTools;
import com.q_util.SocketTools;
import com.shizhantouzi.MainActivity;
import com.shizhantouzi.R;
import com.shizhantouzi.bizCmd.iService.IRecieveService;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/*
import com.example.handan.Common;
import com.example.handan.MainActivity;
import com.example.handan.R;
import com.example.handan.biz.BiBiz;
import com.example.handan.bizFun.BizFunString;
import com.example.handan.iservice.IRecieveService;
import com.example.handan.tools.DateTime;
import com.example.handan.tools.DeviceTools;
import com.example.handan.tools.FileTools;
import com.example.handan.tools.SocketTools;
*/
/*
import com.google.code.microlog4android.Level;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.appender.LogCatAppender;
import com.google.code.microlog4android.config.PropertyConfigurator;
*/

public class RecieveService extends Service implements IRecieveService {
	BizFunString bizFunString = new BizFunString();
	DateTime dateTime = new DateTime();
	BiBiz biBiz = BiBiz.newInstance();
    private NotificationManager m_NotificationManager=null;
	//private final Logger logger = LoggerFactory.getLogger(RecieveService.class);//只能一个地方用，多地方用会重复
    Alarmreceiver alarmreceiver;
    SocketTools socketTools = null;
	Logger logger = Logger.getLogger(RecieveService.class);
	//Logger loggerData = null;

    @Override
    public void onCreate() {

		/*
		try {
			DeviceTools.writeFileSdcardFile(this.getFilesDir().getAbsoluteFile().toString()+"/mylog.txt","");
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		//PropertyConfigurator.getConfigurator(this).configure();//只能一个地方用，多地方用会重复
    	//final FileAppender  fa =  (FileAppender) logger.getAppender(1);//只能一个地方用，多地方用会重复
    	//fa.setAppend(true);//只能一个地方用，多地方用会重复
    	//Common.fileMicrolog = fa.getLogFile();//只能一个地方用，多地方用会重复

    	init();
    	getMicroLogToList();
		new Thread(new threadSocket()).start();
    	//注册
    	alarmreceiver = new Alarmreceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.shizhantouzi.alarmreceiver");
        registerReceiver(alarmreceiver, filter);
    	
    	Thread getArticlesThread = new Thread(null, mTask, "getNewArticles");
	    getArticlesThread.start();
        super.onCreate();
    }

    private void getMicroLogToList(){
    	voidSendBroad("开始读取历史记录");
		logger.debug("开始读取历史记录");
		//测试存储各个路径
		//String[] result = DeviceTools.getStorageDirectories();
    	//新SD卡报错，无microlog.txt文件。判断是否存在，然后创建
		/*
		if(!FileTools.fileExists(Common.fileMicrolog)){
			try {
				DeviceTools.writeFileSdcardFile(Common.fileMicrolog.getPath(),"");
			} catch (IOException e) {
				voidSendBroad("写入Microlog文件失败！");
			}
		}
		*/
		InputStream instream = null;
		try {
			instream = new FileInputStream(Common.log4jPathAndName);
			InputStreamReader inputreader = new InputStreamReader(instream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line;
			int i = 0;
			
			while (( line = buffreader.readLine()) != null) {
				//content += line + "\n";
				String[] strings = line.split("#");
				if(strings.length==9){
					i = i + 1;
					Intent serviceIntent = new Intent();
					serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
					serviceIntent.putExtra("CMD", Cmd.cmd_level1_microlog);
					serviceIntent.putExtra("i",i);
					serviceIntent.putExtra("jianping", strings[1]);
					serviceIntent.putExtra("updown", strings[2]);
					serviceIntent.putExtra("jianprice", strings[3]);
					serviceIntent.putExtra("jiantime", strings[4]);
					serviceIntent.putExtra("pingprice", strings[5]);
					serviceIntent.putExtra("pingtime", strings[6]);
					serviceIntent.putExtra("people", strings[7]);
					serviceIntent.putExtra("jiaoyisuo", strings[8]);
					sendBroadcast(serviceIntent);
				}
			}
		} catch (FileNotFoundException e) {
			voidSendBroad("未找到Microlog文件！");
		} catch (IOException e) {
			voidSendBroad("Microlog文件错误！IO");
		}finally{
			if(instream!=null){
				try {
					instream.close();
				} catch (IOException e) {
					voidSendBroad("Microlog文件关闭错误！IO");
				}
			}
		}
		voidSendBroad("完成读取历史记录");
    }
    
    private void init() {
		socketTools = new SocketTools();
		//logger = ALogger.getLogger(RecieveService.class);
		//loggerData = ALoggerData.getLogger(RecieveService.class);
		logger.debug("RecieveService -> onCreate -> init start");
    	voidSendBroad("初始化Service线程");
    	m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	Common.testxmlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"handan.xml");
    	Common.testxmlPath = Environment.getExternalStorageDirectory()+"/handan.xml";
    
    	SharedPreferences settings = getSharedPreferences(Common.path, Context.MODE_PRIVATE);  
 		
 		if(!settings.getString("IP", "").equals("")){
 			Common.IP = settings.getString("IP", "");
 		}else{
 			Common.IP = Common.IPValue;
 		}
 		Common.Mobile = settings.getString("mobile", "");
 		Common.Key = settings.getString("key", "");
 		Common.TiaoshiLog = Boolean.valueOf(settings.getString("genmicrolog", "false"));

 		voidSendBroad("完成初始化Service线程");
	}


    public void showNotification(String text) {
		Intent myi = new Intent(this, MainActivity.class);
		myi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent m_PendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,myi,PendingIntent.FLAG_UPDATE_CURRENT);
		//m_Notification.setLatestEventInfo(getApplicationContext(),getString(R.string.app_name), text,m_PendingIntent);
		Builder builder = new Notification.Builder(this);
		Resources res = this.getResources();
		builder.setContentIntent(m_PendingIntent)
				//.setSmallIcon(R.drawable.ic_launcher)
				//.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
				//.setTicker("this is bitch!").setWhen(System.currentTimeMillis())
				//.setAutoCancel(true)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(text);
		//Notification m_Notification = new Notification();
		Notification m_Notification = builder.getNotification();
		m_Notification.icon = R.drawable.ic_launcher;
		m_Notification.tickerText = text;
		m_Notification.defaults = Notification.DEFAULT_SOUND;
		m_Notification.flags = Notification.FLAG_AUTO_CANCEL;
		m_NotificationManager.notify(1, m_Notification);

	}


	/*
    private void Exit(){
        try {
        	//Common.dos.close();
        	//Common.dos = null;
        	Common.dis.close();
        	Common.dis = null;
        	Common.socket.getInputStream().close();
        	Common.socket.getOutputStream().close();
        	Common.socket.shutdownInput();
        	Common.socket.shutdownOutput();
			Common.socket.close();
			Common.socket = null;
		} catch (IOException e) {
			//logger.debug(DeviceTools.getDateTime()+" ThreadSocket Exit Socket CLose IOException "+e.getMessage());
		}
    }
	*/

	/**
	 * App开始时启动通讯线程。之后定时判断socket，为空时，在此启动通讯线程
	 */
	class threadSocket implements Runnable {
		public void run() {
			logger.debug("RecieveService -> threadSocket start");
			if(Common.socket==null){
				try {
					Common.socket = new Socket(Common.IP, Common.PORT);
				}
				catch (UnknownHostException e) {
					voidSendBroad("连接服务器失败！UnknownHost");
				} catch (IOException e) {
					voidSendBroad("连接服务器失败！IO");
				}

				if(Common.socket!=null){
					String keyString = "null";
					if(!Common.Key.equals("")){
						keyString = "***";
					}
					voidSendBroad("向服务器提交信息"+Common.Mobile+""+keyString);

					if(Common.TiaoshiLog){
						logger.debug(DeviceTools.getDateTime()+" Common.DENGLU "+Common.Mobile);
					}

					voidCmdLogin();
					do {
						voidDataInputStreamRead();
					} while (true);
				}
			}
		}
	}


    class Alarmreceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
			logger.debug("RecieveService -> Alarmreceiver -> onReceive start");
        	//判断socket状态
        	if(Common.socket==null){
        		Intent serviceIntent = new Intent();
				serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
				serviceIntent.putExtra("CMD", Cmd.cmd_level1_error);
				serviceIntent.putExtra("VALUE","启动新线程连接服务器");
				sendBroadcast(serviceIntent);
				
        		new Thread(new threadSocket()).start();
        	}else{
        		try{   
					Common.socket.sendUrgentData(0xFF);
					if(Common.TiaoshiLog){
	        			logger.debug(DeviceTools.getDateTime()+" Alarmreceiver Socket "+Common.socket.isInputShutdown()+" "+Common.socket.isOutputShutdown()+" "+Common.socket.getLocalPort());
	        		}
					Intent serviceIntent = new Intent();
					serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
					serviceIntent.putExtra("CMD", Cmd.cmd_level1_error);
					serviceIntent.putExtra("VALUE","欢迎 "+Common.NickName+" 联网状态良好");
					sendBroadcast(serviceIntent);
				}catch(IOException e){
					if(Common.TiaoshiLog){
	        			logger.debug(DeviceTools.getDateTime()+" Alarmreceiver sendUrgentData IOException "+e.getMessage());
	        		}
					Intent serviceIntent = new Intent();
					serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
					serviceIntent.putExtra("CMD", Cmd.cmd_level1_error);
					serviceIntent.putExtra("VALUE","断网,启动新线程连接服务器");
					sendBroadcast(serviceIntent);
					Common.socket = null;
					new Thread(new threadSocket()).start();
				}
        	}
        }
    }

    Runnable mTask = new Runnable() {
        @Override
        public void run() {
			logger.debug("RecieveService -> Runnable mTask start");
        	try{
        		if(Common.TiaoshiLog){
					logger.debug(DeviceTools.getDateTime()+" 周期服务启动,请等待5分钟...");
				}
        		voidSendBroad("周期服务启动,请等待5分钟...");
				
				Intent intent = new Intent();
	        	intent.setAction("com.shizhantouzi.alarmreceiver");
	        	PendingIntent sender=PendingIntent.getBroadcast(RecieveService.this, 0, intent, 0);

	            AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
	            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(),300*1000,sender);
        	}catch(Exception e){
        		if(Common.TiaoshiLog){
					logger.debug(DeviceTools.getDateTime()+" 周期服务启动失败："+e.getMessage());
				}
        		voidSendBroad("周期服务启动失败："+e.getMessage());
        	}
			
        	
        }
    };
    
    @Override
    public IBinder onBind(Intent intent) {
    	//logger.debug(DeviceTools.getDateTime()+" RecieveService onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
    	//logger.debug(DeviceTools.getDateTime()+" RecieveService onDestroy");
    	m_NotificationManager.cancel(1);
        super.onDestroy();
    }

    public final IBinder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply,int flags) throws RemoteException {
        	//logger.debug(DeviceTools.getDateTime()+" RecieveService onTransact");
        	return super.onTransact(code, data, reply, flags);
        }
    };

	@Override
	public void voidSendBroad(String value) {
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
		serviceIntent.putExtra("CMD",Cmd.cmd_level1_error);
		serviceIntent.putExtra("VALUE",value);
		sendBroadcast(serviceIntent);

	}

	@Override
	public void voidPrintWrite(String value) {
		if(Common.socket!=null){
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(Common.socket.getOutputStream());
			} catch (IOException e2) {
				voidSendBroad("printWrite失败");
			}
			String result = bizFunString.getSendMessageWithLength(value);
			logger.info("准备发送请求: "+result);
			pw.println(result);
			pw.flush();
		}

	}

	@Override
	public void voidDataInputStreamRead() {

		if(Common.socket!=null){
			InputStream is =null;
			try {
				is = Common.socket.getInputStream();
			} catch (IOException e2) {
				voidSendBroad("获取InputStream失败");
			}
			if(is!=null){
				DataInputStream input = new DataInputStream(is);
		        byte[] buffer=null;
		        try {
					buffer = new byte[input.available()];
				} catch (IOException e1) {
					voidSendBroad("读取合法字节流失败");
				}
		        if (buffer.length != 0) {
		            try {
		            	input.read(buffer);
					} catch (IOException e) {
						voidSendBroad("数据堵塞失败");
					}
		            String jsonRequest = new String(buffer);
		            
		            jsonRequest = jsonRequest.substring(13, jsonRequest.length());
					logger.info("接收数据: "+jsonRequest);
					JSONObject jsonObject = null;
					try {
						jsonObject = new JSONObject(jsonRequest);
					} catch (JSONException e) {
						voidSendBroad("JSON数据失败！");
					}
		            if(jsonObject!=null){
		            	String cmd = null;
		            	try {
							cmd = jsonObject.getString("cmd");
						} catch (JSONException e) {
							voidSendBroad("JSON数据失败！");
						}
		            	if(cmd!=null){
		            		if(Integer.parseInt(cmd)==Cmd.cmd_level1_faBuRobot){
		            			voidCmdFabuRobot(jsonObject);
		            		}else if(Integer.parseInt(cmd)==Cmd.cmd_level1_faBuNews){
								String value = null;
								
								try {
									value = jsonObject.getString("value");
								} catch (JSONException e) {
									logger.error("获取返回数据json出错: "+e.getMessage());
									/*
									Intent serviceIntent = new Intent();
									serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
									serviceIntent.putExtra("CMD", Common.ERROR);
									serviceIntent.putExtra("VALUE", "JSON数据失败！");
									sendBroadcast(serviceIntent);
									*/
								}
								
					        	showNotification(value);
							}else if(Integer.parseInt(cmd)==Cmd.cmd_level1_dingyueJiaoyisuo){
								String jiaoyisuoid = null;
								String jiaoyisuoname = null;
								
								try {
									jiaoyisuoid = jsonObject.getString("jiaoyisuoid");
									jiaoyisuoname = jsonObject.getString("jiaoyisuoname");
									Common.JiaoyisuoID = jiaoyisuoid;
									Common.JiaoyisuoName = jiaoyisuoname;
									
									Intent serviceIntent = new Intent();
									serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
									serviceIntent.putExtra("CMD", Cmd.cmd_level1_dingyueJiaoyisuo);
									sendBroadcast(serviceIntent);
								} catch (JSONException e) {
									Intent serviceIntent = new Intent();
									serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
									serviceIntent.putExtra("CMD", Cmd.cmd_level1_error);
									serviceIntent.putExtra("VALUE", "JSON数据失败！");
									sendBroadcast(serviceIntent);
								}
							}else if(Integer.parseInt(cmd)==Cmd.cmd_level1_hanList){
								voidCmdHanList(jsonObject);
							}else if(Integer.parseInt(cmd)==Cmd.cmd_level1_bi){
								String dataMap = "";
								String xMap = "";
								String yMap = "";
								try {
									dataMap = jsonObject.getString("dataMap");
									xMap = jsonObject.getString("xMap");
									yMap = jsonObject.getString("yMap");
								} catch (JSONException e) {
									
								}
								biBiz.clearData();
			            		
			            		if(!xMap.equals("")){
			            			String[] str = xMap.split(",");
			            			for(int i=0;i<str.length;i++){
			            				biBiz.strX.add(str[i]);
			            			}
			            		}
			            		
			            		if(!yMap.equals("")){
			            			String[] str = yMap.split(",");
			            			for(int i=0;i<str.length;i++){
			            				biBiz.strY.add(str[i]);
			            			}
			            		}
			            		
			            		if(!dataMap.equals("")){
			            			String[] str = dataMap.split(",");
			            			for(int i=0;i<str.length;i++){
			            				biBiz.strLev.add(str[i]);
			            			}
			            		}
			            		
			            		biBiz.setLeftTopStartY();
			            		
							}else if(Integer.parseInt(cmd)==Cmd.cmd_level1_dengLu){
								//logger.debug(DeviceTools.getDateTime()+" Common.DENGLU begin");
								String isHanList = null;
								try {
									String result = jsonObject.getString("result");
									if(result.equals("yes")){
										Common.ID = jsonObject.getString("id");
										Common.NickName = jsonObject.getString("nickname");
										Common.Mobile = jsonObject.getString("mobile");
										Common.Name = jsonObject.getString("name");
										Common.IdCard = jsonObject.getString("idcard");
										Common.Email = jsonObject.getString("email");
										Common.HandanLevel = jsonObject.getString("hanlevel");
										Common.Memo = jsonObject.getString("memo");
										Common.DingyuePeople = jsonObject.getString("dingyuePeople");
										Common.DingyuePeopleID = jsonObject.getString("dingyuePeopleid");
										Common.JiaoyisuoID = jsonObject.getString("jiaoyisuoid");
										Common.JiaoyisuoName = jsonObject.getString("jiaoyisuoName");
										isHanList = jsonObject.getString("isHanList");
										Common.LastHanDateTime = jsonObject.getString("lasthandatetime");
									}else if(result.equals("no")){
										logger.debug("no people");
									}

								} catch (JSONException e) {
									logger.error("获取返回数据json出错: "+e.getMessage());
									Intent serviceIntent = new Intent();
									serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
									serviceIntent.putExtra("CMD", Cmd.cmd_level1_error);
									serviceIntent.putExtra("VALUE", "JSON数据失败！");
									sendBroadcast(serviceIntent);
								}
								if(!Common.ID.equals("")){
									Intent serviceIntent = new Intent();
									serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
									serviceIntent.putExtra("CMD", Cmd.cmd_level1_dengLuHanList);
									sendBroadcast(serviceIntent);
								}else{
									Intent serviceIntent = new Intent();
									serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
									serviceIntent.putExtra("CMD", Cmd.cmd_level1_dengLu);
									sendBroadcast(serviceIntent);
								}
								//logger.debug(DeviceTools.getDateTime()+" Common.DENGLU end");
							}else if(Integer.parseInt(cmd)==Cmd.cmd_level1_regist){
								try {
									Common.ID = jsonObject.getString("id");
								} catch (JSONException e) {
									logger.error("获取返回数据json出错: "+e.getMessage());
								}
								Intent serviceIntent = new Intent();
								serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
								serviceIntent.putExtra("CMD", Cmd.cmd_level1_regist);
								sendBroadcast(serviceIntent);
							}else if(Integer.parseInt(cmd)==Cmd.cmd_level1_fromServerHeart){
								voidSendBroad("心跳 "+dateTime.GetNowDate(6));
								voidCmdHeart();
							}
		            	}
		            }
		        }
			}
		}
	}

	@Override
	public void voidCmdHeart() {
		String jsonOutPut=null; 
		jsonOutPut = "{\"cmd\":\""+Cmd.cmd_level1_toServerHeart+"\"}";
		voidPrintWrite(jsonOutPut);
	}

	@Override
	public void voidCmdLogin() {
		String jsonOutPut=null;
		jsonOutPut = "{\"cmd\":\""+Cmd.cmd_level1_dengLu+"\",\"mobile\":\""+Common.Mobile+"\",\"password\":\""+Common.Key+"\",\"hanlist\":\"yes\",\"clientType\":\"android\"}";
		try {
			socketTools.boolPrintWrite(Common.socket.getOutputStream(),jsonOutPut);
		} catch (IOException e) {
			logger.error("发送登录请求失败: "+e.getMessage());
		}
		//voidPrintWrite(jsonOutPut);
	}

	@Override
	public void voidCmdFabuRobot(JSONObject jsonObject) {
		String jsonOutPut = null;
		JSONArray jsonArray = null;
		try {
			jsonArray = jsonObject.getJSONArray("MessageList");
		} catch (JSONException e) {
			logger.error("获取返回数据json出错: "+e.getMessage());
			//voidSendBroad("JSON数据失败！");
		}
		if (jsonArray!=null && jsonArray.length() != 0){
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject2 = null;
				String num = null;
				String people = null;
				String updown = null;
				String jianping = null;
				String jianprice = null;
				String pingprice = null;
				String jiaoyisuo = null;
				String jiantime = null;
				String pingtime = null;
				
				try {
					jsonObject2 = jsonArray.getJSONObject(i);
					num = jsonObject2.getString("num");
					people = jsonObject2.getString("people");
					updown = jsonObject2.getString("updown");
					jianping = jsonObject2.getString("jianping");
					jianprice = jsonObject2.getString("jianprice");
					jiantime = jsonObject2.getString("jiantime");
					pingprice = jsonObject2.getString("pingprice");
					pingtime = jsonObject2.getString("pingtime");
					jiaoyisuo = jsonObject2.getString("jiaoyisuo");
				} catch (JSONException e) {
					logger.error("获取返回数据json出错: "+e.getMessage());
					//voidSendBroad("JSON数据失败！");
				}
				
				logger.info("#"+jianping+"#"+updown+"#"+jianprice+"#"+jiantime+"#"+pingprice+"#"+pingtime+"#"+people+"#"+jiaoyisuo);
				
				Intent serviceIntent = new Intent();
				serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
				serviceIntent.putExtra("CMD", Cmd.cmd_level1_faBuRobot);
				//serviceIntent.putExtra("num", num);
				serviceIntent.putExtra("jianping", jianping);
				serviceIntent.putExtra("updown", updown);
				serviceIntent.putExtra("jianprice", jianprice);
				serviceIntent.putExtra("jiantime", jiantime);
				serviceIntent.putExtra("pingprice", pingprice);
				serviceIntent.putExtra("pingtime", pingtime);
				serviceIntent.putExtra("people", people);
				serviceIntent.putExtra("jiaoyisuo", jiaoyisuo);
				sendBroadcast(serviceIntent);
				
	        	String contentText = "";
	        	if(jianping.equals("建仓")){
	        		contentText = jianping+" "+updown+" "+jianprice+" "+jiantime+" "+jiaoyisuo+" "+people+" 单号："+num;
	        	}else if(jianping.equals("平仓")){
	        		contentText = jianping+" "+updown+" "+ pingprice +" "+pingtime+" "+jiaoyisuo+" "+people+" 单号："+num;
	        	}
	        	showNotification(contentText);
	        	
	        	if(i==jsonArray.length()-1){
	        		String lasthantime = null;
	        		if(jianping.equals("建仓")){
	        			lasthantime = jiantime;
	        		}else if(jianping.equals("平仓")){
	        			lasthantime = pingtime;
	        		}
	        		jsonOutPut = "{\"cmd\":\""+Cmd.cmd_level1_lastHanTime+"\",\"id\":\""+Common.ID+"\",\"lasthantime\":\""+lasthantime+"\"}";
        			//socketTools.sendMessage(Common.dos,jsonOutPut);
					voidPrintWrite(jsonOutPut);
	        	}
			}
		}
	
	}

	@Override
	public void voidCmdHanList(JSONObject jsonObject) {
		String jsonOutPut = null;
		JSONArray jsonArray = null;
		try {
			jsonArray = jsonObject.getJSONArray("MessageList");
		} catch (JSONException e) {
			logger.error("获取返回数据json出错: "+e.getMessage());
			//voidSendBroad("JSON数据失败！");
		}
		if (jsonArray!=null && jsonArray.length() != 0){
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject2 = null;
				String num = null;
				String people = null;
				String updown = null;
				String jianping = null;
				String jianprice = null;
				String pingprice = null;
				String jiaoyisuo = null;
				String jiantime = null;
				String pingtime = null;
				
				try {
					jsonObject2 = jsonArray.getJSONObject(i);
					num = jsonObject2.getString("num");
					people = jsonObject2.getString("people");
					updown = jsonObject2.getString("updown");
					jianping = jsonObject2.getString("jianping");
					jianprice = jsonObject2.getString("jianprice");
					jiantime = jsonObject2.getString("jiantime");
					pingprice = jsonObject2.getString("pingprice");
					pingtime = jsonObject2.getString("pingtime");
					jiaoyisuo = jsonObject2.getString("jiaoyisuo");
					//为啥有时候是建仓平仓?,涨跌也是?,貌似是cmd=9时,FABUROBOT
					if(jianping.equals("?")){
						if(!pingtime.equals("")){
							jianping = "平仓";
						}else{
							jianping = "建仓";
						}
					}
				} catch (JSONException e) {
					logger.error("获取返回数据json出错: "+e.getMessage());
					//voidSendBroad("JSON数据失败！");
				}
				logger.info("#"+jianping+"#"+updown+"#"+jianprice+"#"+jiantime+"#"+pingprice+"#"+pingtime+"#"+people+"#"+jiaoyisuo);
				
				Intent serviceIntent = new Intent();
				serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
				serviceIntent.putExtra("CMD", Cmd.cmd_level1_hanList);
				serviceIntent.putExtra("i",i+1);
				serviceIntent.putExtra("MaxI",jsonArray.length()-1);
				serviceIntent.putExtra("num", num);
				serviceIntent.putExtra("people", people);
				serviceIntent.putExtra("updown", updown);
				serviceIntent.putExtra("jianping", jianping);
				serviceIntent.putExtra("jianprice", jianprice);
				serviceIntent.putExtra("jiantime", jiantime);
				serviceIntent.putExtra("pingprice", pingprice);
				serviceIntent.putExtra("pingtime", pingtime);
				serviceIntent.putExtra("jiaoyisuo", jiaoyisuo);
				sendBroadcast(serviceIntent);
				
				String contentText = "";
	        	if(jianping.equals("建仓")){
	        		contentText = jianping+" "+updown+" "+jianprice+" "+jiantime+" "+jiaoyisuo+" "+people+" 单号："+num;
	        	}else if(jianping.equals("平仓")){
	        		contentText = jianping+" "+updown+" "+ pingprice +" "+pingtime+" "+jiaoyisuo+" "+people+" 单号："+num;
	        	}
	        	showNotification(contentText);
	        	
	        	//最后一个喊单修改最后喊单时间
	        	if(i==jsonArray.length()-1){
	        		String lasthantime = null;
	        		if(jianping.equals("建仓")){
	        			lasthantime = jiantime;
	        		}else if(jianping.equals("平仓")){
	        			lasthantime = pingtime;
	        		}
	        		jsonOutPut = "{\"cmd\":\""+Cmd.cmd_level1_lastHanTime+"\",\"id\":\""+Common.ID+"\",\"lasthantime\":\""+lasthantime+"\"}";
        			//socketTools.sendMessage(Common.dos,jsonOutPut);
					voidPrintWrite(jsonOutPut);
	        	}
			}
		}else{
			Intent serviceIntent = new Intent();
			serviceIntent.setAction("com.shizhantouzi.ServiceActivityConnectionActivity");
			serviceIntent.putExtra("CMD", Cmd.cmd_level1_hanList);
			serviceIntent.putExtra("i",0);
			sendBroadcast(serviceIntent);
		}
	
	}

}