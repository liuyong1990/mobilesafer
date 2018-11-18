package cn.itcast.mobliesafe.chapter02;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.itcast.mobliesafe.R;

public class SetUp4Activity extends BaseSetUpActivity{

	
	private TextView mStatusTV;
	private ToggleButton mToggleButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		initView();
	}
	
	private void initView() {
		((RadioButton)findViewById(R.id.rb_four)).setChecked(true);
		mStatusTV = (TextView) findViewById(R.id.tv_setup4_status);
		mToggleButton = (ToggleButton) findViewById(R.id.togglebtn_securityfunction);
		mToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mStatusTV.setText("���������Ѿ�����");
				}else{
					mStatusTV.setText("��������û�п���");
				}
				Editor editor = sp.edit();
				editor.putBoolean("protecting", isChecked);
				editor.commit();
			}
		});
		
		boolean protecting = sp.getBoolean("protecting", true);
		if(protecting){
			mStatusTV.setText("���������Ѿ�����");
			mToggleButton.setChecked(true);
		}else{
			mStatusTV.setText("��������û�п���");
			mToggleButton.setChecked(false);
		}
	}

	@Override
	public void showNext() {
		//��ת�� ��������ҳ��
		Editor editor = sp.edit();
		editor.putBoolean("isSetUp", true);
		editor.commit();
		startActivityAndFinishSelf(LostFindActivity.class);
	}

	@Override
	public void showPre() {
		startActivityAndFinishSelf(SetUp3Activity.class);
	}
}
