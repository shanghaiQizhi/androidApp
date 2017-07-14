package com.q_bizbi;

import org.apache.log4j.Logger;
import java.util.ArrayList;


public class BiBiz {
	Logger logger = Logger.getLogger(BiBiz.class);
	//public int perHeight = 30;//Y轴每段高度
	//public int leftTopStartY;//维度层左上角X坐标
	//public int leveHigh = 200;//2个维度层之间的高度
	//public int leftTopOffX;//X偏差宽度.四边形左边上面点和下面点的X轴距离.
	//public int perOffX = 40;//Y轴每一格的X偏差宽度
	public ArrayList<String> strX;//X轴标题
	public ArrayList<String> strY;//Y轴标题
	public ArrayList<String> strZ;//Z轴标题
	public ArrayList<String> strLev;//指标
	//public int dataOffY = 3;//数据柱子上移高度


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private static BiBiz obj = null;

	public static BiBiz newInstance() {
		if (obj == null)
			obj = new BiBiz();
		return obj;
	}

	private BiBiz() {
		strX = new ArrayList<String>();
		strY = new ArrayList<String>();
		strZ = new ArrayList<String>();
		strLev = new ArrayList<String>();
	}


	public void setLeftTopStartY(){
		//this.leftTopStartY = Common.ScreenHeight-this.strY.size()*perHeight-100;
		//this.leveHigh = this.perHeight * strY.size() + 100;
		//this.leftTopOffX = this.perOffX * strY.size();
	}

	public void clearData(){
		strX.clear();
		strY.clear();
		strLev.clear();
	}

	/**
	 * 初始化BI数据.先初始化数据,再初始化图的起点Y轴.
	 */
	public void initData(){

		strX.add("2016.01");
		strX.add("2016.02");
		strX.add("2016.03");
		strX.add("2016.04");
		strX.add("2016.05");
		strX.add("2016.06");
		
		strY.add("多金老师");
		strY.add("九齿钉耙老师");
		strY.add("庆爷老师");
		strY.add("鑫明老师");
		strY.add("健健老师");
		
		
		strZ.add("正确率");
		strZ.add("失败率");
		strZ.add("利润率");
		/*
		Common.strZ.add("亏损率");
		Common.strZ.add("出手次数");
		Common.strZ.add("日出手率");
		Common.strZ.add("周出手率");
		Common.strZ.add("月出手率");
		Common.strZ.add("多单出手率");
		Common.strZ.add("空单出售率");
		*/
		
		strLev.add("0.50");
		strLev.add("0.30");
		strLev.add("0.50");
		strLev.add("0.20");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.10");
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.5");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.20");
		strLev.add("0.50");
		strLev.add("0.40");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.1");
		strLev.add("0.50");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.30");
		strLev.add("0.50");
		strLev.add("0.10");
		
		//第二层
		strLev.add("0.50");
		strLev.add("0.30");
		strLev.add("0.50");
		strLev.add("0.20");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.10");
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.5");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.20");
		strLev.add("0.50");
		strLev.add("0.40");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.1");
		strLev.add("0.50");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.30");
		strLev.add("0.50");
		strLev.add("0.10");
		
		//第三层
		strLev.add("0.50");
		strLev.add("0.30");
		strLev.add("0.50");
		strLev.add("0.20");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.10");
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.5");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.20");
		strLev.add("0.50");
		strLev.add("0.40");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.1");
		strLev.add("0.50");
		strLev.add("0.50");
		
		strLev.add("0.50");
		strLev.add("0.50");
		strLev.add("0.30");
		strLev.add("0.50");
		strLev.add("0.10");

		//界面计算初始化放到Activity里面
		//this.setLeftTopStartY();

		logger.debug("BI默认数据初始化完成");
	}
}
