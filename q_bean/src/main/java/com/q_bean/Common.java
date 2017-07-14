package com.q_bean;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;


import android.graphics.Bitmap;


public class Common {

	public static final String TAG = "test";
	public static final String projectName = "handan";
	public static int ScreenWidth = 0;
	public static int ScreenHeight = 0;
	public static String path = "handan.config";
	public static ArrayList<HashMap<String, Object>> listTest;
	
	public static Bitmap icon;

	//public static final Logger logger = LoggerFactory.getLogger(RecieveService.class);//只能一个地方用，多地方用会重复
	//public static final FileAppender fa =  (FileAppender) Common.logger.getAppender(1);//只能一个地方用，多地方用会重复
	
	public static String IPValue = "117.185.4.94";
	//public static String IPValue = "192.168.1.139";
	public static String IP = "";
	public static int PORT = 9102;
	public static String WebUrlPre = "http://es.shizhantouzi.com:8180/q_shizhantouziWeb/";
	public static Socket socket;
	//原始socket使用，改良后mina2不使用
	//public static DataOutputStream dos;
	public static PrintWriter pw;
	
	public static DataInputStream dis;
	public static Semaphore semaphore = new Semaphore(1);
	
	public static String ID = "";
	public static String Mobile = "";
	public static String Key = "";
	public static String NickName = "";
	public static String Name = "";
	public static String IdCard = "";
	public static String HandanLevel = "";
	public static String Email = "";
	public static String Memo = "";
	public static String DingyuePeople = "";
	public static String DingyuePeopleID = "";
	public static String JiaoyisuoID = "3";
	public static String JiaoyisuoName = "";
	public static String LastHanDateTime = "";//最后获取喊单时间
	public static boolean TiaoshiLog = false;

	//public static boolean TouchFirst;
	//public static File fileMicrolog;
	//日志路径和名称
	public static String log4jPathAndName;
	//喊单数据路径和名称
	//public static String log4jDataPathAndName;
	public static File testxmlFile;
	public static String testxmlPath;
	
	
}
