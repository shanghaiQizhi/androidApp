package com.q_bean;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpBiz {
	
	public static int clientport = 0;//主程序随机生成
	public static InetAddress inetServer = null;
	public static InetAddress inetSend = null;
	//public static String serverIP = "117.185.4.94";
	public static String serverIP = "127.0.0.1";
	public static DatagramSocket socket = null;
	public static String localhost = "";
	public static String sendIp = "";
	public static int serverport = 2348;
	public static int sendPort = 0;
	public static int socketTimeOut = 900000;//socket接收数据超时
	public static int socketSendTime = 5000;//心跳连接时间,服务器和发送端通用
	public static boolean threadToSend=false;//至发送端线程是否循环
	public static boolean threadToServer=true;//至服务端线程是否循环
	public static boolean threadRec=true;//接收线程是否循环
	public static boolean bSurfaceView=true;//SurfaceView是否循环
	public static int screenX;//可视区域x
	public static int screenY;//可视区域y
	public static int keshiWidth;//可视区域width
	public static int keshiHeight;//可视区域height
	
	public static final short cmdToServerRand = 2;
	public static final short cmdMoveArea = 3;
	public static final short cmdHeartToSend = 4;
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
