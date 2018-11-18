package cn.itcast.mobliesafe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import cn.itcast.mobliesafe.chapter01.adapter.HomeAdapter;
import cn.itcast.mobliesafe.chapter02.LostFindActivity;
import cn.itcast.mobliesafe.chapter02.dialog.InterPasswordDialog;
import cn.itcast.mobliesafe.chapter02.dialog.InterPasswordDialog.MyCallBack;
import cn.itcast.mobliesafe.chapter02.dialog.SetUpPasswordDialog;
import cn.itcast.mobliesafe.chapter02.receiver.MyDeviceAdminReciever;
import cn.itcast.mobliesafe.chapter02.utils.MD5Utils;
import cn.itcast.mobliesafe.chapter03.SecurityPhoneActivity;
import cn.itcast.mobliesafe.chapter04.AppManagerActivity;
import cn.itcast.mobliesafe.chapter05.VirusScanActivity;
import cn.itcast.mobliesafe.chapter06.CacheClearListActivity;
import cn.itcast.mobliesafe.chapter07.ProcessManagerActivity;
import cn.itcast.mobliesafe.chapter08.TrafficMonitoringActivity;
import cn.itcast.mobliesafe.chapter09.AdvancedToolsActivity;
import cn.itcast.mobliesafe.chapter10.SettingsActivity;

public class HomeActivity extends Activity {

	/** ����GridView �ÿؼ�����ListView */
	private GridView gv_home;
	/** �洢�ֻ����������sp */
	private SharedPreferences msharedPreferences;
	/** �豸���� Ա */
	private DevicePolicyManager policyManager;
	/** ����Ȩ�� */
	private ComponentName componentName;
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ��ʼ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		msharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		// ��ʼ��GridView
		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new HomeAdapter(HomeActivity.this));
		// ������Ŀ�ĵ���¼�
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			// parent����gridView,view����ÿ����Ŀ��view����,postion����ÿ����Ŀ��λ��
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: // ����ֻ�����
					if (isSetUpPassword()) {
						// ������������Ի���
						showInterPswdDialog();
					} else {
						// ������������Ի���
						showSetUpPswdDialog();
					}
					break;
				case 1: // ���ͨѶ��ʿ
					startActivity(SecurityPhoneActivity.class);
					break;
				case 2: // ����ܼ�
					startActivity(AppManagerActivity.class);
					break;
				case 3:// �ֻ�ɱ��
					startActivity(VirusScanActivity.class);
					break;
				case 4:// ��������
					startActivity(CacheClearListActivity.class);
					break;
				case 5:// ���̹���
					startActivity(ProcessManagerActivity.class);
					break;
				case 6: // ����ͳ��
					startActivity(TrafficMonitoringActivity.class);
					break;
				case 7: // �߼�����
					startActivity(AdvancedToolsActivity.class);
					break;
				case 8: // ��������
					startActivity(SettingsActivity.class);
					break;
				}
			}
		});
		// 1.��ȡ�豸����Ա
		policyManager=(DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        // 2.����Ȩ��, MyDeviceAdminReciever�̳���DeviceAdminReceiver
		componentName=new ComponentName(this, MyDeviceAdminReciever.class);
	    // 3.�ж�,���û��Ȩ��������Ȩ��
		boolean active=policyManager.isAdminActive(componentName);
		if(!active) {
			//û�й���Ա��Ȩ�ޣ����ȡ����Ա��Ȩ��
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "��ȡ��������ԱȨ�ޣ�����Զ���������������");
			startActivity(intent);
		}
		
	}
	/***
	 * ������������Ի���
	 */
	private void showSetUpPswdDialog() {
		final SetUpPasswordDialog setUpPasswordDialog = new SetUpPasswordDialog(
				HomeActivity.this);
		setUpPasswordDialog
				.setCallBack(new cn.itcast.mobliesafe.chapter02.dialog.SetUpPasswordDialog.MyCallBack() {

					@Override
					public void ok() {
						String firstPwsd = setUpPasswordDialog.mFirstPWDET
								.getText().toString().trim();
						String affirmPwsd = setUpPasswordDialog.mAffirmET
								.getText().toString().trim();
						if (!TextUtils.isEmpty(firstPwsd)
								&& !TextUtils.isEmpty(affirmPwsd)) {
							if (firstPwsd.equals(affirmPwsd)) {
								// ��������һ��,�洢����
								savePswd(affirmPwsd);
								setUpPasswordDialog.dismiss();
								// ��ʾ��������Ի���
								showInterPswdDialog();
							} else {
								Toast.makeText(HomeActivity.this, "�������벻һ�£�", 0).show(); 
							}
						} else {
							Toast.makeText(HomeActivity.this, "���벻��Ϊ�գ�", 0).show();
						}
					}

					@Override
					public void cancle() {
						setUpPasswordDialog.dismiss();
					}
				});
		setUpPasswordDialog.setCancelable(true);
		setUpPasswordDialog.show();
	}

	/**
	 * ������������Ի���
	 */
	private void showInterPswdDialog() {
		final String password = getPassword();
		final InterPasswordDialog mInPswdDialog = new InterPasswordDialog(
				HomeActivity.this);
		mInPswdDialog.setCallBack(new MyCallBack() {
			@Override
			public void confirm() {
				if (TextUtils.isEmpty(mInPswdDialog.getPassword())) {
					Toast.makeText(HomeActivity.this, "���벻��Ϊ�գ�", 0).show();
				} else if (password.equals(MD5Utils.encode(mInPswdDialog
						.getPassword()))) {
					// �������������
					mInPswdDialog.dismiss();
					startActivity(LostFindActivity.class);
				} else {
					// �Ի�����ʧ��������˾
					mInPswdDialog.dismiss();
					Toast.makeText(HomeActivity.this, "�����������������룡", 0).show();
				}
			}

			@Override
			public void cancle() {
				mInPswdDialog.dismiss();
			}
		});
		mInPswdDialog.setCancelable(true);
		// �öԻ�����ʾ
		mInPswdDialog.show();
	}

	/***
	 * ��������
	 * 
	 * @param affirmPwsd
	 */
	private void savePswd(String affirmPwsd) {
		Editor edit = msharedPreferences.edit();
		// Ϊ�˷�ֹ�û���˽��й¶�������Ҫ��������
		edit.putString("PhoneAntiTheftPWD", MD5Utils.encode(affirmPwsd));
		edit.commit();
	}

	/***
	 * ��ȡ����
	 * 
	 * @return sp�洢������
	 */
	private String getPassword() {
		String password = msharedPreferences.getString("PhoneAntiTheftPWD",
				null);
		if (TextUtils.isEmpty(password)) {
			return "";
		}
		return password;
	}

	/** �ж��û��Ƿ����ù��ֻ��������� */
	private boolean isSetUpPassword() {
		String password = msharedPreferences.getString("PhoneAntiTheftPWD",
				null);
		if (TextUtils.isEmpty(password)) {
			return false;
		}
		return true;
	}

	/**
	 * �����µ�activity���ر��Լ�
	 * 
	 * @param cls
	 *            �µ�activity���ֽ���
	 */
	public void startActivity(Class<?> cls) {
		Intent intent = new Intent(HomeActivity.this, cls);
		startActivity(intent);
	}

	/***
	 * �����η��ؼ��˳�����
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
