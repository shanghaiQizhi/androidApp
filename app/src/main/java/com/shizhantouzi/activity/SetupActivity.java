package com.shizhantouzi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.q_bean.Common;
import com.shizhantouzi.R;

public class SetupActivity extends Activity {

    RelativeLayout layout;
    TextView tv_title;
    boolean boolean_spinner1 = false;
    CheckBox cb_test;
    Spinner sp_platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        layout = (RelativeLayout)findViewById(R.id.layout1);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.app_name) + " - 系统设置");
        cb_test = (CheckBox)findViewById(R.id.cb_test);
        if(Common.TiaoshiLog){
            cb_test.setChecked(true);
        }
        cb_test.setText("生成调试日志");
        //checkBox.setTextColor(Color.rgb(51, 94, 155));
        cb_test.setTextColor(Color.rgb(0, 0, 0));
        cb_test.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(SetupActivity.this,arg1?"启动成功":"取消成功", Toast.LENGTH_LONG).show();

                SharedPreferences settings = getSharedPreferences(Common.path, Context.MODE_PRIVATE);
                Editor nameEditor = settings.edit();
                nameEditor.putString("genmicrolog",arg1?"true":"false");
                nameEditor.commit();

                if(arg1){
                    Common.TiaoshiLog = true;
                }else{
                    Common.TiaoshiLog = false;
                }

            }
        });
        sp_platform = (Spinner)findViewById(R.id.sp_platform);
        sp_platform.setPrompt("暂支持天通银、大圆银、新华银、伦敦银");
        final String s[]={"伦敦金","伦敦银","粤贵银","天通银","大圆银","国际原油","EURUSD","USDJPY","美元指数","铭爵银","新华银"};//1开始
        ArrayAdapter<String> aa=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,s);
        //设置为下拉式Item格式
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_platform.setAdapter(aa);
        sp_platform.setSelection(Integer.valueOf(Common.JiaoyisuoID)-1);
        //添加监听
        OnItemSelectedListener otsc=new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                //tv3.setText("选项"+String.valueOf(arg2+1)+"被你选中了");
                if(boolean_spinner1){
                    if(Common.socket!=null){
                        //这里后面再改
                        /*
                        try {
                            Common.dos.writeShort(Common.DingyueJiaoyisuo);
                            Common.dos.writeUTF(Common.ID);
                            Common.dos.writeUTF(String.valueOf(arg2+1));//id
                            Common.dos.writeUTF(s[arg2]);//name
                        } catch (IOException e) {}
                        */
                    }
                    //Toast.makeText(SetupActivity.this,"选项"+s[arg2]+String.valueOf(arg2+1)+"被你选中了", Toast.LENGTH_LONG).show();
                }
                boolean_spinner1 = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        };
        sp_platform.setOnItemSelectedListener(otsc);

    }


    /**
     * 点击事件
     * @param v 本页视图
     */
    public void onClick_Event_OnActivity(final View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                SetupActivity.this.finish();
                break;
        }
    }
}
