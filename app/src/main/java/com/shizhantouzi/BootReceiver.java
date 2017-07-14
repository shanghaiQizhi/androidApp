package com.shizhantouzi;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.shizhantouzi.bizCmd.iService.impl.RecieveService;


public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
			//啥都启动不了，这功能有啥用
			
			context.startService(new Intent(context,RecieveService.class));//启动倒计时服务
			
			//Toast.makeText(context, "请打开喊单平台，即可收到即时喊单！", Toast.LENGTH_LONG).show();    
			
			//这边可以添加开机自动启动的应用程序代码
			//Intent intent2=new Intent(context,MainActivity.class);
			//intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//context.startActivity(intent2);
			
			/*
			Notification m_Notification = new Notification();
			m_Notification.icon = R.drawable.ic_launcher;
			m_Notification.tickerText = "请打开喊单平台，即可收到即时喊单！";
			m_Notification.defaults = Notification.DEFAULT_SOUND;
			m_Notification.flags = Notification.FLAG_AUTO_CANCEL;
			Intent myi = new Intent(context, MainActivity.class);
			myi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); 
			PendingIntent m_PendingIntent = PendingIntent.getActivity(context, 0,myi,PendingIntent.FLAG_UPDATE_CURRENT);
			m_Notification.setLatestEventInfo(context,"喊单平台", "请打开喊单平台，即可收到即时喊单！",m_PendingIntent);
			
			NotificationManager m_NotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
			m_NotificationManager.notify(1, m_Notification);
			*/
        }
	}
}
