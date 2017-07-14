package com.shizhantouzi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.shizhantouzi.R;

public class IntroActivity extends Activity {

    RelativeLayout layout;
    TextView tv_title;
    TextView tv_html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        layout = (RelativeLayout)findViewById(R.id.activity_intro);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.app_name) + " - 手机APP介绍");

        tv_html = (TextView)findViewById(R.id.tv_html);
        tv_html.setLineSpacing(1.6f, 1.4f);
        tv_html.setIncludeFontPadding(false);
        tv_html.setVerticalScrollBarEnabled(true);
        tv_html.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_html.setText(Html.fromHtml("    本手机APP可接收最新即时喊单，包括建仓、平仓的具体点位和时间；可选择交易市场包括：伦敦金、伦敦银、国际原油、外汇(欧元美元，美元日元)、天通银、大圆银、汇丰银、铭爵银、粤贵银等。使用流程：访问 <a href=\"http://www.shizhantouzi.com\">www.shizhantouzi.com</a> 并登陆，进入“ <a href=\"http://www.shizhantouzi.com/rank.html\">操盘大赛</a> ”栏目订阅操盘手，订阅后手机App重新登陆即可。<br/><br/><a href=\"http://www.shizhantouzi.com/handan.apk\">点击安装最新版本</a>"));
        tv_html.setMovementMethod(LinkMovementMethod.getInstance());

    }


    /**
     * 点击事件
     * @param v 本页视图
     */
    public void onClick_Event_OnActivity(final View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                IntroActivity.this.finish();
                break;
        }
    }
}
