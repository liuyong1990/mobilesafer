package cn.itcast.mobliesafe.chapter02;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import cn.itcast.mobliesafe.R;

@SuppressLint("ShowToast")
public class SetUp3Activity extends BaseSetUpActivity implements OnClickListener{

	
	private EditText mInputPhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		initView();
	}
	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		((RadioButton)findViewById(R.id.rb_third)).setChecked(true);
		findViewById(R.id.btn_addcontact).setOnClickListener(this);
		mInputPhone = (EditText) findViewById(R.id.et_inputphone);
		String  safephone= sp.getString("safephone", null);
		if(!TextUtils.isEmpty(safephone)){
			mInputPhone.setText(safephone);
		}
	}

	@Override
	public void showNext() {
		//�ж��ı���������Ƿ��е绰����
		String safePhone = mInputPhone.getText().toString().trim();
		if(TextUtils.isEmpty(safePhone)){
			Toast.makeText(this, "�����밲ȫ����", 0).show();
			return;
		}
		Editor edit = sp.edit();
		edit.putString("safephone", safePhone);
		edit.commit();
		startActivityAndFinishSelf(SetUp4Activity.class);
	}

	@Override
	public void showPre() {
		startActivityAndFinishSelf(SetUp2Activity.class);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_addcontact:
				startActivityForResult(new Intent(this,ContactSelectActivity.class), 0);
				break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null){
			String phone = data.getStringExtra("phone");
			mInputPhone.setText(phone);
		}
	}
}
