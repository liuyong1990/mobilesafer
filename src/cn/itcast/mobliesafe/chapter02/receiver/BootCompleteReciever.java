package cn.itcast.mobliesafe.chapter02.receiver;

import cn.itcast.mobliesafe.App;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** ���������Ĺ㲥���࣬��Ҫ���ڼ��SIM���Ƿ񱻸�����������������Ͷ��Ÿ���ȫ���� */
public class BootCompleteReciever extends BroadcastReceiver {

	private static final String TAG = BootCompleteReciever.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		((App) context.getApplicationContext()).correctSIM();// ��ʼ��
	}

}
