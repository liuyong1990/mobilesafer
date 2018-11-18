package cn.itcast.mobliesafe.chapter01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;
import cn.itcast.mobliesafe.HomeActivity;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter01.utils.MyUtils;
import cn.itcast.mobliesafe.chapter01.utils.VersionUpdateUtils;

/**
 * ��ӭҳ��
 * 
 * @author admin
 */
public class SplashActivity extends Activity {

	/** Ӧ�ð汾�� */
	private TextView mVersionTV;
	/** ���ذ汾�� */
	private String mVersion;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����û�б����� �ڼ��ز���֮ǰ����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		mVersion = MyUtils.getVersion(getApplicationContext());
		initView();
		final VersionUpdateUtils updateUtils = new VersionUpdateUtils(mVersion,
				SplashActivity.this);
		new Thread() {
			public void run() {
				// ��ȡ�������汾��
				updateUtils.getCloudVersion();
			};
		}.start();
	}

	/** ��ʼ���ؼ� */
	private void initView() {
		mVersionTV = (TextView) findViewById(R.id.tv_splash_version);
		mVersionTV.setText("�汾�� " + mVersion);
	}
}
