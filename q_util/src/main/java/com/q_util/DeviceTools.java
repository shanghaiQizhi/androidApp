package com.q_util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Window;

public class DeviceTools {
	private static int[] deviceWidthHeight = new int[2];
	private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

	public static Bitmap resizeBitmap(Bitmap bitmap,float scaleWidth,float scaleHeight) {
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Log.i("info", width + " " + height);
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth,scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int newWidth = w;
			int newHeight = h;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}

	public static int[] getDeviceInfo(Context context) {
		//这是获取全部高度，含状态栏，工具栏
		/*
		if ((deviceWidthHeight[0] == 0) && (deviceWidthHeight[1] == 0)) {
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);

			deviceWidthHeight[0] = metrics.widthPixels;
			deviceWidthHeight[1] = metrics.heightPixels;
		}
		*/
		if ((deviceWidthHeight[0] == 0) && (deviceWidthHeight[1] == 0)) {
			Rect outRect = new Rect();  
			((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);  

			deviceWidthHeight[0] = outRect.width();
			deviceWidthHeight[1] = outRect.height();
		}
		
		return deviceWidthHeight;
	}
	
	public static String getDateTime(){
		//SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter   =   new   SimpleDateFormat   ("dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}
	
	public static String getDateTime(int type,String datetime){
		String result = datetime;
		if(!datetime.equals("")){
			if(type==1 && datetime.length()>=15){
				result = datetime.substring(8,16);//dd HH:mm:ss
			}
		}
		
		return result;
	}

	/*
	public static String getCMD(short cmd){
		if(cmd==Common.DENGLU){
			return "DENGLU";
		}else if(cmd==Common.FABUROBOT){
			return "FABUROBOT";
		}else if(cmd==Common.NOTIFICATION){
			return "NOTIFICATION";
		}
		
		return "";
	}
	*/

	/*
	public static void savexml(List<TestInfo> lTestInfo,OutputStream out){

		//类似于这样格式的文件
		//<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
		//<persons>
		//<person id="25">
		//<name>ggg</name>
		//<sex>men</sex>
		//<age>21</age>
		//<phone>1234562</phone>
		//</person>
		//</persons>

		try{
			//xml解析器
			XmlSerializer xml=Xml.newSerializer();
			//设置xml文件的输出方向
			xml.setOutput(out, "UTF-8");
			//设置xml的开始文档内容及编码格式
			xml.startDocument("UTF-8", true);
			//设置xml的开始节点
			xml.startTag(null, "TestInfos");
			for(TestInfo p:lTestInfo){
				xml.startTag(null, "testinfo");
				String id="1";
				xml.attribute(null, "id",id);
				//设置节点
				xml.startTag(null,"datetime");
				xml.text(p.getDatetime());
				xml.endTag(null, "datetime");
				
				xml.startTag(null,"content");
				xml.text(p.getContent());
				xml.endTag(null, "content");
				
				//结束节点
				xml.endTag(null, "testinfo");
			}
			//xml的结束节点
			xml.endTag(null, "TestInfos");
			//结束文档
			xml.endDocument();
		} catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try{
			out.flush();
			out.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static List<TestInfo> getTestInfos(InputStream xml) throws Exception {        
        List<TestInfo> persons = null;
        TestInfo person = null;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(xml, "UTF-8"); //为Pull解释器设置要解析的XML数据        
        int event = pullParser.getEventType();
        
        while (event != XmlPullParser.END_DOCUMENT){
            
            switch (event) {
            
            case XmlPullParser.START_DOCUMENT:
                persons = new ArrayList<TestInfo>();                
                break;    
            case XmlPullParser.START_TAG:    
                if ("testinfo".equals(pullParser.getName())){
                    String id = pullParser.getAttributeValue(0);
                    person = new TestInfo();
                    person.setId(id);
                }else if ("content".equals(pullParser.getName())){
                    String content = pullParser.nextText();
                    person.setContent(content);
                }else if ("datetime".equals(pullParser.getName())){
                	String datetime = pullParser.nextText();
                    person.setDatetime(datetime);
                }
                break;
                
            case XmlPullParser.END_TAG:
                if ("testinfo".equals(pullParser.getName())){
                    persons.add(person);
                    person = null;
                }
                break;
                
            }
            
            event = pullParser.next();
        }
        
        
        return persons;
    }
	*/

	private static SharedPreferences getSharedPreferences(String path,
			int modePrivate) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//写数据到SD中的文件
	public static void writeFileSdcardFile(String fileName,String write_str) throws IOException{ 
	 try{ 

	       FileOutputStream fout = new FileOutputStream(fileName); 
	       byte [] bytes = write_str.getBytes(); 

	       fout.write(bytes); 
	       fout.close(); 
	     }

	      catch(Exception e){ 
	        e.printStackTrace(); 
	       } 
	   }


	/**
	 * Raturns all available SD-Cards in the system (include emulated)
	 *
	 * Warning: Hack! Based on Android source code of version 4.3 (API 18)
	 * Because there is no standart way to get it.
	 * TODO: Test on future Android versions 4.4+
	 *
	 * @return paths to all available SD-Cards in the system (include emulated)
	 */
	public static String[] getStorageDirectories()
	{
		// Final set of paths
		final Set<String> rv = new HashSet<String>();
		// Primary physical SD-CARD (not emulated)
		final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
		// All Secondary SD-CARDs (all exclude primary) separated by ":"
		final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
		// Primary emulated SD-CARD
		final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
		if(TextUtils.isEmpty(rawEmulatedStorageTarget))
		{
			// Device has physical external storage; use plain paths.
			if(TextUtils.isEmpty(rawExternalStorage))
			{
				// EXTERNAL_STORAGE undefined; falling back to default.
				rv.add("/storage/sdcard0");
			}
			else
			{
				rv.add(rawExternalStorage);
			}
		}
		else
		{
			// Device has emulated storage; external storage paths should have
			// userId burned into them.
			final String rawUserId;
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
			{
				rawUserId = "";
			}
			else
			{
				final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
				final String[] folders = DIR_SEPORATOR.split(path);
				final String lastFolder = folders[folders.length - 1];
				boolean isDigit = false;
				try
				{
					Integer.valueOf(lastFolder);
					isDigit = true;
				}
				catch(NumberFormatException ignored)
				{
				}
				rawUserId = isDigit ? lastFolder : "";
			}
			// /storage/emulated/0[1,2,...]
			if(TextUtils.isEmpty(rawUserId))
			{
				rv.add(rawEmulatedStorageTarget);
			}
			else
			{
				rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
			}
		}
		// Add all secondary storages
		if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
		{
			// All Secondary SD-CARDs splited into array
			final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
			Collections.addAll(rv, rawSecondaryStorages);
		}
		return rv.toArray(new String[rv.size()]);
	}
	/**
	 * 获取最近6个月的数组
	 * @return
	 */
	public String[] getRecent6Month(){
		int currentX = 5;
		String[] result = {"","","","","",""};
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH )+1;
		while(month>1){
			month--;
			result[currentX] = year+"."+month;
			currentX --;
		}
		year = cal.get(Calendar.YEAR)-1;
		month = 13;
		while(currentX>=0){
			month--;
			result[currentX] = year+"."+month;
			currentX --;
		}
		return result;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
