package com.q_log4j;


import android.content.Context;

import com.q_bean.Common;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class ALogger {
    public ALogger(){

    }

    public  org.apache.log4j.Logger getLogger(Class clazz) {
        final LogConfigurator logConfigurator = new LogConfigurator();
        //logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "testlog/file.log");
        //logConfigurator.setFileName(context.getFilesDir().getAbsoluteFile().toString() + File.separator + "shizhantouzi/log4j.log");
        logConfigurator.setFileName(Common.log4jPathAndName);

        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.DEBUG);
        logConfigurator.setUseFileAppender(true);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
        Logger log = Logger.getLogger(clazz);
        return log;
    }
}