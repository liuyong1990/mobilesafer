package cn.itcast.mobliesafe.chapter08.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.itcast.mobliesafe.chapter08.service.TrafficMonitoringService;
import cn.itcast.mobliesafe.chapter08.utils.SystemInfoUtils;

/**���������Ĺ㲥���࣬�������ݿ⣬��������*/
public class BootCompleteReciever extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		//�����㲥
		//�ж�������ط����Ƿ��������û��������
		if(!SystemInfoUtils.isServiceRunning(context,"cn.itcast.mobliesafe.chapter08.service.TrafficMonitoringService")){
			//��������
			context.startService(new Intent(context, TrafficMonitoringService.class));
		}
	}
}
