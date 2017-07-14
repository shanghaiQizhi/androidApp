package com.q_util;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

//import org.apache.log4j.Logger;

public class StringFun {
	//private static List<SoftReference<StringFun>> singletonList  = new ArrayList<SoftReference<StringFun>>();
	//private static Semaphore semaphore = new Semaphore(1);
	//private static Zhubao obj_ = null;
	//private Logger log;
	/*
	public static StringFun newInstance() {

		//if (common == null)
		//	common = new Member();
		//return common;

		if (singletonList.isEmpty() || singletonList.get(0).get() == null) {   
            try {   
                semaphore.acquire(); 
                if (singletonList.isEmpty() || singletonList.get(0).get() == null) {
                	singletonList.clear();
                    singletonList.add(new SoftReference<StringFun>(new StringFun()));   
                }   
                semaphore.release();   
            } catch (InterruptedException e2) {   
                //System.err.println("Could not create Singleton - " + e2.getMessage());   
                //Thread.currentThread().interrupt(); // restore the interrupt   
            }
		}
		return singletonList.get(0).get();
	}
	*/

	public StringFun() {
		//log = Logger.getLogger(GetURL.class);
	}

	public String getString(String source, String start, String end) {
		String result = "";
		try{
			int iBegin = source.indexOf(start);
			result = source.substring(iBegin+start.length());
			int iEnd = result.indexOf(end);
			if(iBegin<0 || iEnd<0){
				result="";
			}else{
				result = result.substring(0, iEnd);
			}
		}catch(Exception e){
			//log.info(e.getMessage());
		}

		return result;
	}
	
	public String returnManySplit(String source,String pagestrbegin,String pagestrend){
		String result = "";
		String[] pagestrbegin1 = pagestrbegin.split("@@@");
		String[] pagestrbegin2 = pagestrend.split("@@@");
		for(int z=0;z<pagestrbegin1.length;z++){
			if(z==0){
				result = this.getString(source, pagestrbegin1[z], pagestrbegin2[z]);
			}else{
				result = this.getString(result, pagestrbegin1[z], pagestrbegin2[z]);
			}
		}
		return result;
	}
	
	public String getSendMessageWithLength(String message) {
		int len = message.length();
        String strLengthJson = String.format("%5d", len).replace(" ", "0");
        String result = "length:"+strLengthJson+";"+message;
		return result;
	}
}
