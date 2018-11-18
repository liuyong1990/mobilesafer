package cn.itcast.mobliesafe.chapter08.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.itcast.mobliesafe.chapter08.db.dao.TrafficDao;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.IBinder;

public class TrafficMonitoringService extends Service {
	private long mOldRxBytes;
	private long mOldTxBytes;
	private TrafficDao dao;
	private SharedPreferences mSp;
	private long usedFlow;
	boolean flag = true;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		mOldRxBytes = TrafficStats.getMobileRxBytes();
		mOldTxBytes = TrafficStats.getMobileTxBytes();
		dao = new TrafficDao(this);
		mSp = getSharedPreferences("config", MODE_PRIVATE);
		mThread.start();
	}
	
	private Thread mThread = new Thread() {
		public void run() {
			while (flag) {
				try {
					Thread.sleep(2000 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateTodayGPRS();
			}
		}
		private void updateTodayGPRS() {
			// ��ȡ�Ѿ�ʹ���˵�����
			usedFlow = mSp.getLong("usedflow", 0);
			Date date = new Date();
			Calendar calendar = Calendar.getInstance(); // �õ�����
			calendar.setTime(date);// �ѵ�ǰʱ�丳������
			if (calendar.DAY_OF_MONTH == 1 & calendar.HOUR_OF_DAY == 0
					& calendar.MINUTE < 1 & calendar.SECOND < 30) {
				usedFlow = 0;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dataString = sdf.format(date);
			long moblieGPRS = dao.getMoblieGPRS(dataString);
			long mobileRxBytes = TrafficStats.getMobileRxBytes();
			long mobileTxBytes = TrafficStats.getMobileTxBytes();
			// �²���������
			long newGprs = (mobileRxBytes + mobileTxBytes) - mOldRxBytes
					- mOldTxBytes;
			mOldRxBytes = mobileRxBytes;
			mOldTxBytes = mobileTxBytes;
			if (newGprs < 0) {
				// �����л���
				newGprs = mobileRxBytes + mobileTxBytes;
			}
			if (moblieGPRS == -1) {
				dao.insertTodayGPRS(newGprs);
			} else {
				if (moblieGPRS < 0) {
					moblieGPRS = 0;
				}
				dao.UpdateTodayGPRS(moblieGPRS + newGprs);
			}
			usedFlow = usedFlow + newGprs;
			Editor edit = mSp.edit();
			edit.putLong("usedflow", usedFlow);
			edit.commit();
		};
	};
	
	@Override
	public void onDestroy() {
		if (mThread != null & !mThread.interrupted()) {
			flag = false;
			mThread.interrupt();
			mThread = null;
		}
		super.onDestroy();
	}
}
