package cn.itcast.mobliesafe.chapter02;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobliesafe.R;

public class SetUp1Activity extends BaseSetUpActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		initView();
	}

	private void initView() {
		// ���õ�һ��СԲ�����ɫ
		((RadioButton) findViewById(R.id.rb_first)).setChecked(true);
	}

	@Override
	public void showNext() {
		startActivityAndFinishSelf(SetUp2Activity.class);
	}

	@Override
	public void showPre() {
		Toast.makeText(this, "��ǰҳ���Ѿ��ǵ�һҳ", 0).show();
	}
}
