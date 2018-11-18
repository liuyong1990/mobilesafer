package cn.itcast.mobliesafe.chapter08.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.itcast.mobliesafe.chapter08.service.TrafficMonitoringService;
import cn.itcast.mobliesafe.chapter08.utils.SystemInfoUtils;

/**监听开机的广播该类，更新数据库，开启服务*/
public class BootCompleteReciever extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		//开机广播
		//判断流量监控服务是否开启，如果没开启则开启
		if(!SystemInfoUtils.isServiceRunning(context,"cn.itcast.mobliesafe.chapter08.service.TrafficMonitoringService")){
			//开启服务
			context.startService(new Intent(context, TrafficMonitoringService.class));
		}
	}
}
