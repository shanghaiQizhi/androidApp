package com.q_bizcmd;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


//import org.apache.log4j.Logger;

public class BizFunString {
	//private static List<SoftReference<BizFunString>> singletonList  = new ArrayList<SoftReference<BizFunString>>();
	//private static Semaphore semaphore = new Semaphore(1);
	//private static Zhubao obj_ = null;
	//private Logger log;
	/*
	public static BizFunString newInstance() {

		//if (common == null)
		//	common = new Member();
		//return common;

		if (singletonList.isEmpty() || singletonList.get(0).get() == null) {   
            try {   
                semaphore.acquire(); 
                if (singletonList.isEmpty() || singletonList.get(0).get() == null) {
                	singletonList.clear();
                    singletonList.add(new SoftReference<BizFunString>(new BizFunString()));   
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
	public BizFunString() {
		//log = Logger.getLogger(GetURL.class);
	}

	public String getSendMessageWithLength(String message) {
		int len = message.length();
        String strLengthJson = String.format("%5d", len).replace(" ", "0");
        String result = "length:"+strLengthJson+";"+message;
		return result;
	}
}
