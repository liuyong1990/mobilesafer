package cn.itcast.mobliesafe.chapter08;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter08.db.dao.TrafficDao;
import cn.itcast.mobliesafe.chapter08.service.TrafficMonitoringService;
import cn.itcast.mobliesafe.chapter08.utils.SystemInfoUtils;

public class TrafficMonitoringActivity extends Activity implements
		OnClickListener {
	private SharedPreferences mSP;
	private Button mCorrectFlowBtn;
	private TextView mTotalTV;
	private TextView mUsedTV;
	private TextView mToDayTV;
	private TrafficDao dao;
	private ImageView mRemindIMGV;
	private TextView mRemindTV;
	private CorrectFlowReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_trafficmonitoring);
		mSP = getSharedPreferences("config", MODE_PRIVATE);
		boolean flag = mSP.getBoolean("isset_operator", false);
		// ���û��������Ӫ����Ϣ�������Ϣ����ҳ��
		if (!flag) {
			startActivity(new Intent(this, OperatorSetActivity.class));
			finish();
		}
		if (!SystemInfoUtils
				.isServiceRunning(this,
						"cn.itcast.mobliesafe.chapter08.service.TrafficMonitoringService")) {
			startService(new Intent(this, TrafficMonitoringService.class));
		}
		initView();
		registReceiver();
		initData();
	}

	private void initView() {
		findViewById(R.id.rl_titlebar).setBackgroundColor(
				getResources().getColor(R.color.light_green));
		ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
		((TextView) findViewById(R.id.tv_title)).setText("�������");
		mLeftImgv.setOnClickListener(this);
		mLeftImgv.setImageResource(R.drawable.back);
		mCorrectFlowBtn = (Button) findViewById(R.id.btn_correction_flow);
		mCorrectFlowBtn.setOnClickListener(this);
		mTotalTV = (TextView) findViewById(R.id.tv_month_totalgprs);
		mUsedTV = (TextView) findViewById(R.id.tv_month_usedgprs);
		mToDayTV = (TextView) findViewById(R.id.tv_today_gprs);
		mRemindIMGV = (ImageView) findViewById(R.id.imgv_traffic_remind);
		mRemindTV = (TextView) findViewById(R.id.tv_traffic_remind);
	}

	private void initData() {
		long totalflow = mSP.getLong("totalflow", 0);
		long usedflow = mSP.getLong("usedflow", 0);
		if (totalflow > 0 & usedflow >= 0) {
			float scale = usedflow / totalflow;
			if (scale > 0.9) {
				mRemindIMGV.setEnabled(false);
				mRemindTV.setText("�����ײ������������꣡");
			} else {
				mRemindIMGV.setEnabled(true);
				mRemindTV.setText("�����������������ʹ��");
			}
		}
		mTotalTV.setText("����������" + Formatter.formatFileSize(this, totalflow));
		mUsedTV.setText("�������ã�" + Formatter.formatFileSize(this, usedflow));
		dao = new TrafficDao(this);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dataString = sdf.format(date);
		long moblieGPRS = dao.getMoblieGPRS(dataString);
		if (moblieGPRS < 0) {
			moblieGPRS = 0;
		}
		mToDayTV.setText("�������ã�" + Formatter.formatFileSize(this, moblieGPRS));
	}

	private void registReceiver() {
		receiver = new CorrectFlowReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(receiver, filter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_leftbtn:
			finish();
			break;
		case R.id.btn_correction_flow:
			// �����ж����ĸ���Ӫ�̣�
			int i = mSP.getInt("operator", 0);
			SmsManager smsManager = SmsManager.getDefault();
			switch (i) {
			case 0:
				// û��������Ӫ��
				Toast.makeText(this, "����û��������Ӫ����Ϣ", 0).show();
				break;
			case 1:
				// �й��ƶ�
				break;
			case 2:
				// �й���ͨ
				// ����LLCX��10010
				// ��ȡϵͳĬ�ϵĶ��Ź�����
				smsManager.sendTextMessage("10010", null, "LLCX", null, null);
				break;
			case 3:
				// �й�����
				break;
			}
		}
	}

	class CorrectFlowReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String body = smsMessage.getMessageBody();
				String address = smsMessage.getOriginatingAddress();
				// ���¶��ŷָ�ֻ�����ͨ3G�û�
				if (!address.equals("10010")) {
					return;
				}
				String[] split = body.split("��");
				// ����ʣ������
				long left = 0;
				// ������������
				long used = 0;
				// ���³�������
				long beyond = 0;
				for (int i = 0; i < split.length; i++) {
					if (split[i].contains("����������ʹ��")) {
						// �ײ�����
						String usedflow = split[i].substring(7,
								split[i].length());
						used = getStringTofloat(usedflow);
					} else if (split[i].contains("ʣ������")) {
						String leftflow = split[i].substring(4,
								split[i].length());
						left = getStringTofloat(leftflow);
					} else if (split[i].contains("�ײ�������")) {
						String beyondflow = split[i].substring(5,
								split[i].length());
						beyond = getStringTofloat(beyondflow);
					}
				}
				Editor edit = mSP.edit();
				edit.putLong("totalflow", used + left);
				edit.putLong("usedflow", used + beyond);
				edit.commit();
				mTotalTV.setText("����������"
						+ Formatter.formatFileSize(context, (used + left)));
				mUsedTV.setText("�������ã�"
						+ Formatter.formatFileSize(context, (used + beyond)));
			}
		}
	}

	/** ���ַ���ת����Float�������� **/
	private long getStringTofloat(String str) {
		long flow = 0;
		if (!TextUtils.isEmpty(str)) {
			if (str.contains("KB")) {
				String[] split = str.split("KB");
				float m = Float.parseFloat(split[0]);
				flow = (long) (m * 1024);
			} else if (str.contains("MB")) {
				String[] split = str.split("MB");
				float m = Float.parseFloat(split[0]);
				flow = (long) (m * 1024 * 1024);
			} else if (str.contains("GB")) {
				String[] split = str.split("GB");
				float m = Float.parseFloat(split[0]);
				flow = (long) (m * 1024 * 1024 * 1024);
			}
		}
		return flow;
	}

	@Override
	public void onDestroy() {
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		super.onDestroy();
	}
}
