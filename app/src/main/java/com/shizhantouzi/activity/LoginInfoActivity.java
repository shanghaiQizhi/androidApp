package com.shizhantouzi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.q_bean.Common;
import com.shizhantouzi.R;

public class LoginInfoActivity extends Activity {

    RelativeLayout layout;
    Button btn_close;
    TextView tv_truename;
    TextView tv_nickname;
    TextView tv_mobile;
    TextView tv_email;
    TextView tv_idcard;
    TextView tv_jiaoyisuo;
    TextView tv_caopanshou;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);

        layout = (RelativeLayout)findViewById(R.id.activity_login_info);
        btn_close = (Button)findViewById(R.id.btn_close);
        tv_truename = (TextView)findViewById(R.id.tv_truename);
        tv_truename.setText(Common.Name);
        tv_nickname = (TextView)findViewById(R.id.tv_nickname);
        tv_nickname.setText(Common.NickName);
        tv_mobile = (TextView)findViewById(R.id.tv_mobile);
        tv_mobile.setText(Common.Mobile);
        tv_email = (TextView)findViewById(R.id.tv_email);
        tv_email.setText(Common.Email);
        tv_idcard = (TextView)findViewById(R.id.tv_idcard);
        tv_idcard.setText(Common.IdCard);
        tv_jiaoyisuo = (TextView)findViewById(R.id.tv_jiaoyisuo);
        tv_jiaoyisuo.setText(Common.JiaoyisuoName);
        tv_caopanshou = (TextView)findViewById(R.id.tv_caopanshou);
        tv_caopanshou.setText(Common.DingyuePeople);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.app_name) + " - 个人信息");
    }

    /**
     * 点击事件
     * @param v 本页视图
     */
    public void onClick_Event_OnActivity(final View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                LoginInfoActivity.this.finish();
                break;
        }
    }
}
