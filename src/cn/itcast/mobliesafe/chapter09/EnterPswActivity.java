package cn.itcast.mobliesafe.chapter09;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter02.utils.MD5Utils;

public class EnterPswActivity extends Activity implements OnClickListener{
	
	private ImageView mAppIcon;
	private TextView mAppNameTV;
	private EditText mPswET;
	private ImageView mGoImgv;
	private LinearLayout mEnterPswLL;
	private SharedPreferences sp;
	private String password;
	private String packagename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_enterpsw);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		password = sp.getString("PhoneAntiTheftPWD", null);
		Intent intent = getIntent();
		packagename = intent.getStringExtra("packagename");
		PackageManager pm = getPackageManager();
		initView();
		try {
			mAppIcon.setImageDrawable(pm.getApplicationInfo(packagename, 0).loadIcon(pm));
			mAppNameTV.setText(pm.getApplicationInfo(packagename, 0).loadLabel(pm).toString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		mAppIcon = (ImageView) findViewById(R.id.imgv_appicon_enterpsw);
		mAppNameTV = (TextView) findViewById(R.id.tv_appname_enterpsw);
		mPswET = (EditText) findViewById(R.id.et_psw_enterpsw);
		mGoImgv = (ImageView) findViewById(R.id.imgv_go_enterpsw);
		mEnterPswLL = (LinearLayout) findViewById(R.id.ll_enterpsw);
		mGoImgv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_go_enterpsw:
			//�Ƚ�����
			String inputpsw = mPswET.getText().toString().trim();
			if(TextUtils.isEmpty(inputpsw)){
				startAnim();
				Toast.makeText(this, "���������룡", 0).show();
				return;
			}else{
				if(!TextUtils.isEmpty(password)){
					if(MD5Utils.encode(inputpsw).equals(password)){
						//�����Զ���Ĺ㲥��Ϣ��
						Intent intent = new Intent();
						intent.setAction("cn.itcast.mobliesafe.applock");
						intent.putExtra("packagename",packagename);
						sendBroadcast(intent);
						finish();
					}else{
						startAnim();  
						Toast.makeText(this, "���벻��ȷ��", 0).show();
						return;
					}
				}
			}
			break;
		}
	}

	private void startAnim() {
		Animation animation =AnimationUtils.loadAnimation(this, R.anim.shake);
		mEnterPswLL.startAnimation(animation);
	}

}
