
/**
 * @author admin
 *
 */
package cn.itcast.mobliesafe.chapter09.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import cn.itcast.mobliesafe.chapter09.entity.AppInfo;

/**
 * ������������ȡӦ����Ϣ�������ظ�
 */
public class AppInfoParser{
	/**
	 * ��ȡ�ֻ���������е�Ӧ�ó���
	 * @param context ������
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		//�õ�һ��java��֤�� ����������
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		for(PackageInfo packInfo:packInfos){
			AppInfo appinfo = new AppInfo();
			String packname = packInfo.packageName;
			appinfo.packageName = packname;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			appinfo. icon = icon;
			String appname = packInfo.applicationInfo.loadLabel(pm).toString();
			appinfo.appName = appname;
			//Ӧ�ó���apk����·��
			String apkpath = packInfo.applicationInfo.sourceDir;
			appinfo.apkPath = apkpath;
			appinfos.add(appinfo);
			appinfo = null;
		}
		return appinfos;
	}
}