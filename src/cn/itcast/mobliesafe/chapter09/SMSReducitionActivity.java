package cn.itcast.mobliesafe.chapter09;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter09.utils.SmsReducitionUtils;
import cn.itcast.mobliesafe.chapter09.utils.UIUtils;
import cn.itcast.mobliesafe.chapter09.utils.SmsReducitionUtils.SmsReducitionCallBack;
import cn.itcast.mobliesafe.chapter09.widget.MyCircleProgress;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
/**���Ż�ԭ**/
public class SMSReducitionActivity extends Activity implements OnClickListener{

	private MyCircleProgress mProgressButton;
	private boolean flag = false;
	private SmsReducitionUtils smsReducitionUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reducition);
		initView();
		smsReducitionUtils = new SmsReducitionUtils();
	}

	private void initView() {
		findViewById(R.id.rl_titlebar).setBackgroundColor(
				getResources().getColor(R.color.bright_red));
		ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
		((TextView) findViewById(R.id.tv_title)).setText("���Ż�ԭ");
		mLeftImgv.setOnClickListener(this);
		mLeftImgv.setImageResource(R.drawable.back);
		
		mProgressButton = (MyCircleProgress) findViewById(R.id.mcp_reducition);
		mProgressButton.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		flag = false;
		smsReducitionUtils.setFlag(flag);
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_leftbtn:
			finish();
			break;
		case R.id.mcp_reducition:
			if(flag){
				flag = false;
				mProgressButton.setText("һ����ԭ");
			}else{
				flag = true;
				mProgressButton.setText("ȡ����ԭ");
			}
			smsReducitionUtils.setFlag(flag);
			new Thread(){

				public void run() {
					try {
						smsReducitionUtils.reducitionSms(SMSReducitionActivity.this, new SmsReducitionCallBack() {
							
							@Override
							public void onSmsReducition(int process) {
								mProgressButton.setProcess(process);
							}
							@Override
							public void beforeSmsReducition(int size) {
								mProgressButton.setMax(size);
							}
						});
					} catch (XmlPullParserException e) {
						e.printStackTrace();
						UIUtils.showToast(SMSReducitionActivity.this, "�ļ���ʽ����");
					} catch (IOException e) {
						e.printStackTrace();
						UIUtils.showToast(SMSReducitionActivity.this, "��д����");
					}
				}	
			}.start();
			break;
		}
	}
}
