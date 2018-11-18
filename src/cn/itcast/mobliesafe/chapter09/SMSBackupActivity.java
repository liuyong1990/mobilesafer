package cn.itcast.mobliesafe.chapter09;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter09.utils.SmsBackUpUtils;
import cn.itcast.mobliesafe.chapter09.utils.UIUtils;
import cn.itcast.mobliesafe.chapter09.utils.SmsBackUpUtils.BackupStatusCallback;
import cn.itcast.mobliesafe.chapter09.widget.MyCircleProgress;
/**���ű���**/
public class SMSBackupActivity extends Activity implements OnClickListener{

	private MyCircleProgress mProgressButton;
	/**��ʶ����������ʶ����״̬��*/
	private boolean flag = false;
	private SmsBackUpUtils smsBackUpUtils;
	private static final int CHANGE_BUTTON_TEXT = 100;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CHANGE_BUTTON_TEXT:
				mProgressButton.setText("һ������");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_smsbackup);
		smsBackUpUtils = new SmsBackUpUtils();
		initView();
	}

	private void initView() {
		findViewById(R.id.rl_titlebar).setBackgroundColor(
				getResources().getColor(R.color.bright_red));
		ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
		((TextView) findViewById(R.id.tv_title)).setText("���ű���");
		mLeftImgv.setOnClickListener(this);
		mLeftImgv.setImageResource(R.drawable.back);
		
		mProgressButton = (MyCircleProgress) findViewById(R.id.mcp_smsbackup);
		mProgressButton.setOnClickListener(this);
	}

	
	@Override
	protected void onDestroy() {
		flag = false;
		smsBackUpUtils.setFlag(flag);
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_leftbtn:
			finish();
			break;
		case R.id.mcp_smsbackup:
			if(flag){
				flag = false;
				mProgressButton.setText("һ������");
			}else{
				flag = true;
				mProgressButton.setText("ȡ������");
			}
			smsBackUpUtils.setFlag(flag);
			new Thread(){

				public void run() {
					try {
						boolean backUpSms = smsBackUpUtils.backUpSms(SMSBackupActivity.this, new BackupStatusCallback() {
							
							

							@Override
							public void onSmsBackup(int process) {
								mProgressButton.setProcess(process);
							}
							
							@Override
							public void beforeSmsBackup(int size) {
								if(size <= 0){
									flag = false;
									smsBackUpUtils.setFlag(flag);
									UIUtils.showToast(SMSBackupActivity.this, "����û�ж��ţ�");
									handler.sendEmptyMessage(CHANGE_BUTTON_TEXT);
								}else{
									mProgressButton.setMax(size);
								}
							}
						});
						if(backUpSms){
							UIUtils.showToast(SMSBackupActivity.this, "���ݳɹ�");
						}else{
							UIUtils.showToast(SMSBackupActivity.this, "����ʧ��");
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						UIUtils.showToast(SMSBackupActivity.this, "�ļ�����ʧ��");
					} catch (IllegalStateException e) {
						e.printStackTrace();
						UIUtils.showToast(SMSBackupActivity.this, "SD�������û�SD���ڴ治��");
					} catch (IOException e) {
						e.printStackTrace();
						UIUtils.showToast(SMSBackupActivity.this, "��д����");
					}
				};
			}.start();
			break;
		}
	}
}
