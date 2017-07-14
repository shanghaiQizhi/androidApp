package com.q_util;


//import com.q_log4j.ALogger;

import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;




public class SocketTools {
	//private static SocketTools obj = null;
	Logger logger =  Logger.getLogger(SocketTools.class);

	/*
	public static SocketTools newInstance() {
		if (obj == null)
			obj = new SocketTools();
		return obj;
	}
	*/

	public SocketTools() {
		//if (!GetURL.newInstance().boolkey())
			// System.exit(0);
		//	return;
		//logger = ALogger.getLogger(SocketTools.class);

	}
	
	/**
	 * 发送socket数据(带长度不足补0)
	 * @param dos DataOutputStream
	 * @param inputStr 字符串
	 */
	public void sendMessage(DataOutputStream dos,String inputStr){
        int lenJson1 = inputStr.length();
        String strLengthJson = String.format("%5d", lenJson1).replace(" ", "0");
        //System.out.print(lenJson1);
        String reqJson2 = "length:"+strLengthJson+";"+inputStr;
        //System.out.print(reqJson2);
		if(dos!=null){
			try {
				dos.write(reqJson2.getBytes());
			} catch (IOException e) {
				logger.error("socket send error: "+e.getMessage());
			}
		}else{
			logger.error("DataOutputStream为null");
		}

    }
	
	/**
	 * PrintWriter发送信息
	 * @param value 发送内容
	 * @return 是否成功
	 */
	public boolean boolPrintWrite(OutputStream ops,String value) {
		boolean result = true;
		if(ops!=null){
			StringFun stringFun = new StringFun();
			PrintWriter pw = null;
			pw = new PrintWriter(ops);
			String value2 = stringFun.getSendMessageWithLength(value);
			logger.info("请求内容: "+value2);
			pw.println(value2);
			pw.flush();
			result = true;
		}else{
			logger.error("OutputStream is null");
			result = false;
		}

		return result;
	}

}
