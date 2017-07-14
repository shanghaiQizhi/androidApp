package com.shizhantouzi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.q_bean.Common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        StringBuilder log = new StringBuilder();
        InputStream instream = null;
        try {
            instream = new FileInputStream(Common.log4jPathAndName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(instream!=null){
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            try {
                while (( line = buffreader.readLine()) != null) {
                    //content += line + "\n";
                    String[] strings = line.split(",");
                    if(strings.length!=9){
                        log.append(line+ "\n");
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if(instream!=null){
                    try {
                        instream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        TextView tv = (TextView) findViewById(R.id.tvLogcat);
        tv.setText(log.toString());
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrlLogcat);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
