package cn.itcast.mobliesafe.chapter04.utils;

import com.stericson.RootTools.RootTools;

import cn.itcast.mobliesafe.chapter04.entity.AppInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

/**ҵ�񹤾���*/
public class EngineUtils {

	/**
	 * ����Ӧ��
	 */
	public static void shareApplication(Context context,AppInfo appInfo) {
		Intent intent = new Intent("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"�Ƽ���ʹ��һ����������ƽУ�" + appInfo.appName
						+ "����·����https://play.google.com/store/apps/details?id="
						+ appInfo.packageName);
		context.startActivity(intent);
	}
	
	/**
	 * ����Ӧ�ó���
	 */
	public static void startApplication(Context context,AppInfo appInfo) {
		// �����Ӧ�ó�������activity��
		PackageManager pm = context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
		if (intent != null) {
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "��Ӧ��û����������", 0).show();
		}
	}
	/**
	 * ����Ӧ������ҳ��
	 * @param context
	 * @param appInfo
	 */
	public static  void SettingAppDetail(Context context,AppInfo appInfo) {
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		// dat=package:com.itheima.mobileguard
		intent.setData(Uri.parse("package:" + appInfo.packageName));
		context.startActivity(intent);
		
	}
	
	/**ж��Ӧ��*/
	public static  void uninstallApplication(Context context,AppInfo appInfo) {
		if (appInfo.isUserApp) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DELETE);
			intent.setData(Uri.parse("package:" + appInfo.packageName));
			context.startActivity(intent);
		}else{
			//ϵͳӦ�� ��rootȨ�� ����linux����ɾ���ļ���
			if(!RootTools.isRootAvailable()){
				Toast.makeText(context, "ж��ϵͳӦ�ã�����ҪrootȨ��", 0).show();
				return ;
			}
			try {
				if(!RootTools.isAccessGiven()){
					Toast.makeText(context, "����Ȩ����С����rootȨ��", 0).show();
					return ;
				}
				RootTools.sendShell("mount -o remount ,rw /system", 3000);
				RootTools.sendShell("rm -r "+appInfo.apkPath, 30000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}
