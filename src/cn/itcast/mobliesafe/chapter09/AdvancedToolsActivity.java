package cn.itcast.mobliesafe.chapter09;

import cn.itcast.mobliesafe.HomeActivity;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter09.widget.AdvancedToolsView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class AdvancedToolsActivity extends Activity implements OnClickListener{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_advancetools);
		initView();
	}

	/**��ʼ���ؼ�*/
	private void initView() {
		findViewById(R.id.rl_titlebar).setBackgroundColor(
				getResources().getColor(R.color.bright_red));
		ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
		((TextView) findViewById(R.id.tv_title)).setText("�߼�����");
		mLeftImgv.setOnClickListener(this);
		mLeftImgv.setImageResource(R.drawable.back);
		
		 findViewById(R.id.advanceview_applock).setOnClickListener(this);
		 findViewById(R.id.advanceview_numbelongs).setOnClickListener(this);
		 findViewById(R.id.advanceview_smsbackup).setOnClickListener(this);
		 findViewById(R.id.advanceview_smsreducition).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_leftbtn:
			finish();
			break;
		case R.id.advanceview_applock:
			//���������ҳ��
			startActivity(AppLockActivity.class);
			break;
		case R.id.advanceview_numbelongs:
			//��������ز�ѯҳ��
			startActivity(NumBelongtoActivity.class);
			break;
		case R.id.advanceview_smsbackup:
			//������ű���ҳ��
			startActivity(SMSBackupActivity.class);
			break;
		case R.id.advanceview_smsreducition:
			//������Ż�ԭҳ��
			startActivity(SMSReducitionActivity.class);
			break;
		}
	}
	
	/**
	 * �����µ�activity���ر��Լ�
	 * @param cls �µ�activity���ֽ���
	 */
	public void startActivity(Class<?> cls){
		Intent intent = new Intent(this,cls);
		startActivity(intent);
	}
}
