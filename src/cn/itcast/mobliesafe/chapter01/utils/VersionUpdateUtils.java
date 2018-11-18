package cn.itcast.mobliesafe.chapter01.utils;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;
import cn.itcast.mobliesafe.HomeActivity;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter01.entity.VersionEntity;
import cn.itcast.mobliesafe.chapter01.utils.DownLoadUtils.MyCallBack;

/** �������ѹ����� */
public class VersionUpdateUtils {
	private static final int MESSAGE_NET_EEOR = 101;
	private static final int MESSAGE_IO_EEOR = 102;
	private static final int MESSAGE_JSON_EEOR = 103;
	private static final int MESSAGE_SHOEW_DIALOG = 104;
	protected static final int MESSAGE_ENTERHOME = 105;
	/** ���ڸ���UI */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_IO_EEOR:
				Toast.makeText(context, "IO�쳣", 0).show();
				enterHome();
				break;
			case MESSAGE_JSON_EEOR:
				Toast.makeText(context, "JSON�����쳣", 0).show();
				enterHome();
				break;
			case MESSAGE_NET_EEOR:
				Toast.makeText(context, "�����쳣", 0).show();
				enterHome();
				break;
			case MESSAGE_SHOEW_DIALOG:
				showUpdateDialog(versionEntity);
				break;
			case MESSAGE_ENTERHOME:
				Intent intent = new Intent(context,HomeActivity.class);
				context.startActivity(intent);
				context.finish();
				break;
			}
		};
	};
	/** ���ذ汾�� */
	private String mVersion;
	private Activity context;
	private ProgressDialog mProgressDialog;
	private VersionEntity versionEntity;

	
	public VersionUpdateUtils(String Version,Activity activity) {
		mVersion = Version;
		context = activity;
	}

	/**
	 * ��ȡ�������汾��
	 */
	public void getCloudVersion(){
		try {
		HttpClient client = new DefaultHttpClient();
		  /*���ӳ�ʱ*/
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
        /*����ʱ*/
        HttpConnectionParams.setSoTimeout(client.getParams(), 5000);
		HttpGet httpGet = new HttpGet(
				"http://172.16.25.14:8080/updateinfo.html");
			HttpResponse execute = client.execute(httpGet);
			if (execute.getStatusLine().getStatusCode() == 200) {
				// �������Ӧ���ɹ���
				HttpEntity entity = execute.getEntity();
				String result = EntityUtils.toString(entity, "gbk");
				// ����jsonObject����
				JSONObject jsonObject = new JSONObject(result);
				versionEntity = new VersionEntity();
				String code = jsonObject.getString("code");
				versionEntity.versioncode = code;
				String des = jsonObject.getString("des");
				versionEntity.description = des;
				String apkurl = jsonObject.getString("apkurl");
				versionEntity.apkurl = apkurl;
				if (!mVersion.equals(versionEntity.versioncode)) {
					// �汾�Ų�һ��
					handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);
				}
			}
		} catch (ClientProtocolException e) {
			handler.sendEmptyMessage(MESSAGE_NET_EEOR);
			e.printStackTrace();
		} catch (IOException e) {
			handler.sendEmptyMessage(MESSAGE_IO_EEOR);
			e.printStackTrace();
		} catch (JSONException e) {
			handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
			e.printStackTrace();
		}
	}

	/**
	 * ����������ʾ�Ի���
	 * 
	 * @param versionEntity
	 */
	private void showUpdateDialog(final VersionEntity versionEntity) {
		// ����dialog
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("��鵽�°汾��" + versionEntity.versioncode);// ���ñ���
		builder.setMessage(versionEntity.description);// ���ݷ�������������,��������������Ϣ
		builder.setCancelable(false);// ���ò��ܵ���ֻ����ذ�ť���ضԻ���
		builder.setIcon(R.drawable.ic_launcher);// ���öԻ���ͼ��
		// ��������������ť����¼�  
		builder.setPositiveButton("��������",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						initProgressDialog();
						downloadNewApk(versionEntity.apkurl);
					}
				});
		// �����ݲ�������ť����¼�
		builder.setNegativeButton("�ݲ�����",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						enterHome();
					}
				});
		// �Ի���������show���� ������ʾ
		builder.show();
	}
	
	/**
	 * ��ʼ���������Ի���
	 */
	private void initProgressDialog() {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("׼������...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.show();
	}

	/***
	 * �����°汾
	 */
	protected void downloadNewApk(String apkurl) {
		DownLoadUtils downLoadUtils = new DownLoadUtils();
		downLoadUtils.downapk(apkurl,"/mnt/sdcard/mobilesafe2.0.apk" , new MyCallBack() {
			
			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				// TODO Auto-generated method stub
				mProgressDialog.dismiss();
				MyUtils.installApk(context);
			}
			
			@Override
			public void onLoadding(long total, long current, boolean isUploading) {
				// TODO Auto-generated method stub
				mProgressDialog.setMax((int)total);
				mProgressDialog.setMessage("��������...");
				mProgressDialog.setProgress((int) current);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mProgressDialog.setMessage("����ʧ��");
				mProgressDialog.dismiss();
				enterHome();
			}

			
		});
	}
	private void enterHome() {
		handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME, 2000);
	}
}
