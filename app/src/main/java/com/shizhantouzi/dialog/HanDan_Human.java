package com.shizhantouzi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.q_bean.activityMain.BtnBuyNowClick;
import com.q_bizcmd.hanDan.HumanHandan;
import com.shizhantouzi.R;

public class HanDan_Human extends Dialog implements View.OnClickListener{
    private Context mContext;

    public HanDan_Human(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_handanhuman);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.share_pop_anim);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bindView();
    }

    private void bindView() {
        //((TextView) findViewById(R.id.tv_contactAgent_name)).setText(getTeamLeaderName());
        findViewById(R.id.bt_handanhuman_buy).setOnClickListener(this);
        findViewById(R.id.bt_handanhuman_sell).setOnClickListener(this);
        findViewById(R.id.bt_handanhuman_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_handanhuman_buy:
                HumanHandan obj = new HumanHandan();
                //买涨
                BtnBuyNowClick o = obj.getBtnBuyNowClick("1");
                obj.getResultSendBtnBuy(o);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HanDan_Human.this.dismiss();
                break;
            case R.id.bt_handanhuman_sell:
                HumanHandan obj2 = new HumanHandan();
                //买涨
                BtnBuyNowClick o2 = obj2.getBtnBuyNowClick("2");
                obj2.getResultSendBtnBuy(o2);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HanDan_Human.this.dismiss();
                break;
            case R.id.bt_handanhuman_cancel:
                HanDan_Human.this.dismiss();
                break;
        }
    }

}

