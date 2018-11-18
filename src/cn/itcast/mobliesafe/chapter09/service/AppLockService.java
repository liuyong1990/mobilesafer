package cn.itcast.mobliesafe.chapter09.service;

import java.util.List;

import cn.itcast.mobliesafe.chapter09.EnterPswActivity;
import cn.itcast.mobliesafe.chapter09.db.dao.AppLockDao;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

/***
 * ����������
 * 
 * @author admin
 */
public class AppLockService extends Service {
	/** �Ƿ�������������ı�־ */
	private boolean flag = false;
	private AppLockDao dao;
	private Uri uri = Uri.parse("content://com.itcast.mobilesafe.applock");
	private List<String> packagenames;
	private Intent intent;
	private ActivityManager am;
	private List<RunningTaskInfo> taskInfos;
	private RunningTaskInfo taskInfo;
	private String pacagekname;
	private String tempStopProtectPackname;
	private AppLockReceiver receiver;
	private MyObserver observer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// ����AppLockDaoʵ��
		dao = new AppLockDao(this);
		observer = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(uri, true,
				observer);
		// ��ȡ���ݿ��е����а���
		packagenames = dao.findAll();
		receiver = new AppLockReceiver();
		IntentFilter filter = new IntentFilter("cn.itcast.mobliesafe.applock");
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
		// ����Intentʵ������������������ҳ��
		intent = new Intent(AppLockService.this, EnterPswActivity.class);
		// ��ȡActivityManager����
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		startApplockService();
		super.onCreate();
	}

	/***
	 * ������س������
	 */
	private void startApplockService() {
		new Thread() {
			public void run() {
				flag = true;
				while (flag) {
					// ��������ջ������� ���ʹ�õĴ򿪵�����ջ�ڼ��ϵ���ǰ��
					taskInfos = am.getRunningTasks(1);
					// ���ʹ�õ�����ջ
					taskInfo = taskInfos.get(0);
					pacagekname = taskInfo.topActivity.getPackageName();
					// �ж���������Ƿ���Ҫ��������
					if (packagenames.contains(pacagekname)) {
						// �жϵ�ǰӦ�ó����Ƿ���Ҫ��ʱֹͣ��������������ȷ�����룩
						if (!pacagekname.equals(tempStopProtectPackname)) {
							// ��Ҫ����
							// ����һ����������Ľ��档
							intent.putExtra("packagename", pacagekname);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	// �㲥������
	class AppLockReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("cn.itcast.mobliesafe.applock".equals(intent.getAction())) {
				tempStopProtectPackname = intent.getStringExtra("packagename");
			} else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
				tempStopProtectPackname = null;
				// ֹͣ��س���
				flag = false;
			} else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				// ������س���
				if (flag == false) {
					startApplockService();
				}
			}
		}
	}

	// ���ݹ۲���
	class MyObserver extends ContentObserver {

		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			packagenames = dao.findAll();
			super.onChange(selfChange);
		}
	}
	
	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(receiver);
		receiver = null;
		getContentResolver().unregisterContentObserver(observer);
		observer = null;
		super.onDestroy();
	}
}
