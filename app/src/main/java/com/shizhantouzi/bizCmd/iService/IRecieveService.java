package com.shizhantouzi.bizCmd.iService;

import org.json.JSONObject;

public interface IRecieveService {
	/**
	 * 发送信息到前端
	 * @param value 发送的信息
	 */
	public void voidSendBroad(String value);
	
	/**
	 * 输出信息到服务端
	 * @param value 输出的信息
	 */
	public void voidPrintWrite(String value);
	
	/**
	 * 得到数据流并堵塞
	 */
	public void voidDataInputStreamRead();
	
	/**
	 * 心跳业务处理
	 */
	public void voidCmdHeart();
	
	/**
	 * 登陆业务处理
	 */
	public void voidCmdLogin();
	
	/**
	 * 机器人喊单业务处理
	 * @param jsonObject
	 */
	public void voidCmdFabuRobot(JSONObject jsonObject);
	
	/**
	 * 获取喊单列表业务处理
	 * @param jsonObject
	 */
	public void voidCmdHanList(JSONObject jsonObject);
	
}
