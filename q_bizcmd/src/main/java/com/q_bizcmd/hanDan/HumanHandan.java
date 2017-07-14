package com.q_bizcmd.hanDan;

import com.q_bean.Common;
import com.q_bean.activityMain.BtnBuyNowClick;
import com.q_bean.cnst.Cmd;
import com.q_util.SocketTools;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * 操盘业务类
 */
public class HumanHandan {
    Logger logger =  Logger.getLogger(HumanHandan.class);

    public HumanHandan(){

    }

    /**
     * 封装建仓对象并返回
     * @param upOrDown 涨(1)或跌(2)
     * @return 建仓对象
     */
    public BtnBuyNowClick getBtnBuyNowClick(String upOrDown){
        BtnBuyNowClick o = new BtnBuyNowClick();
        o.setCmd(String.valueOf(Cmd.cmd_level1_faBu));
        o.setCmd2(String.valueOf(Cmd.cmd_level2_faBu_jiShi));
        o.setOpenUpDown(upOrDown);
        o.setNickName(Common.NickName);
        o.setPeopleId(Common.ID);
        return o;
    }

    /**
     * 发送建仓报文并返回发送结果
     * @param btnBuyNowClick 建仓对象
     * @return 是否发送成功
     */
    public boolean getResultSendBtnBuy(BtnBuyNowClick btnBuyNowClick){
        boolean result = true;
        SocketTools socketTools = new SocketTools();
        String cmd = String.valueOf(Cmd.cmd_level1_faBu);
        String cmd2 = String.valueOf(Cmd.cmd_level2_faBu_jiShi);
        String peopleId = btnBuyNowClick.getPeopleId();
        String openUpDown = btnBuyNowClick.getOpenUpDown();
        String nickName = btnBuyNowClick.getNickName();

        String jsonOutPut = "{\"cmd\":\""+cmd+"\",\"cmd2\":\""+cmd2+"\",\"peopleId\":\""+peopleId+"\"" +
                ",\"openUpDown\":\""+openUpDown+"\",\"nickName\":\""+nickName+"\"}";
        try {
            socketTools.boolPrintWrite(Common.socket.getOutputStream(), jsonOutPut);
        } catch (IOException e) {
            result = false;
            logger.error("发送建仓报文失败: "+e.getMessage());
        }
        return result;
    }
}
