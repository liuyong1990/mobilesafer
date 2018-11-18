package cn.itcast.mobliesafe.chapter03.reciever;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import cn.itcast.mobliesafe.chapter03.db.dao.BlackNumberDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class InterceptCallReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences mSP = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		boolean BlackNumStatus = mSP.getBoolean("BlackNumStatus", true);
		if (!BlackNumStatus) {
			// ���������عر�
			return;
		}
		BlackNumberDao dao = new BlackNumberDao(context);
		if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			String mIncomingNumber = "";
			// ���������
			TelephonyManager tManager = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);
			switch (tManager.getCallState()) {

			case TelephonyManager.CALL_STATE_RINGING:
				mIncomingNumber = intent.getStringExtra("incoming_number");
				int blackContactMode = dao.getBlackContactMode(mIncomingNumber);
				if (blackContactMode == 1 || blackContactMode == 3) {
					// �۲죨����һ��Ӧ�ó������ݿ�ı仯�����м�¼�ı仯��������м�¼�����ˣ��ͰѺ��м�¼��ɾ������
					Uri uri = Uri.parse("content://call_log/calls");
					context.getContentResolver().registerContentObserver(
							uri,
							true,
							new CallLogObserver(new Handler(), mIncomingNumber,
									context));
					endCall(context);
				}
				break;
			}

		}
	}

	private class CallLogObserver extends ContentObserver {
		private String incomingNumber;
		private Context context;

		public CallLogObserver(Handler handler, String incomingNumber,
				Context context) {
			super(handler);
			this.incomingNumber = incomingNumber;
			this.context = context;
		}

		// �۲쵽���ݿ����ݱ仯���õķ���
		@Override
		public void onChange(boolean selfChange) {
			Log.i("CallLogObserver", "���м�¼���ݿ�����ݱ仯�ˡ�");
			context.getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber, context);
			super.onChange(selfChange);
		}
	}

	/**
	 * ������м�¼
	 * 
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber, Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		Cursor cursor = resolver.query(uri, new String[] { "_id" }, "number=?",
				new String[] { incomingNumber }, "_id desc limit 1");
		if (cursor.moveToNext()) {
			String id = cursor.getString(0);
			resolver.delete(uri, "_id=?", new String[] { id });
		}
	}

	/**
	 * �Ҷϵ绰
	 */
	public void endCall(Context context) {
		try {
			Class clazz = context.getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null,
					Context.TELEPHONY_SERVICE);
			ITelephony itelephony = ITelephony.Stub.asInterface(iBinder);
			itelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
