package com.q_util;

import java.io.File;

public class FileTools {
	public static boolean fileExists(File file){
        try{
                if(!file.exists()){
                        return false;
                }
                
        }catch (Exception e) {
                // TODO: handle exception
                return false;
        }
        return true;
}
}
