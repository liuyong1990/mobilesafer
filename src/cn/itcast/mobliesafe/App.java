package cn.itcast.mobliesafe;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		correctSIM();
	}

	public void correctSIM() {
		// ���sim���Ƿ����仯
		SharedPreferences sp = getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// ��ȡ����������״̬
		boolean protecting = sp.getBoolean("protecting", true);
		if (protecting) {
			// �õ��󶨵�sim������
			String bindsim = sp.getString("sim", "");
			// �õ��ֻ����ڵ�sim������
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			// Ϊ�˲������ֻ����к���dafa ��ģ��SIM�������������
			String realsim = tm.getSimSerialNumber();
			if (bindsim.equals(realsim)) {
				Log.i("", "sim��δ�����仯�����������ֻ�");
			} else {
				Log.i("", "SIM���仯��");
				// ����ϵͳ�汾��ԭ������ķ����ſ����������ֻ��汾������
				String safenumber = sp.getString("safephone", "");
				if (!TextUtils.isEmpty(safenumber)) {
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(safenumber, null,
							"��������ֻ���SIM���Ѿ���������", null, null);
				}
			}
		}
	}
}
