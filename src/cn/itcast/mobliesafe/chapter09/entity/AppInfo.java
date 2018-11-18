/**
 * @author admin
 */
package cn.itcast.mobliesafe.chapter09.entity;

import android.graphics.drawable.Drawable;

/**
 * App信息Bean,此类是重复
 * @author admin
 */
public class AppInfo{
	
	/** 应用程序包名 */
	public String packageName;
	/** 应用程序图标 */
	public Drawable icon;
	/** 应用程序名称 */
	public String appName;
	/** 应用程序路径 */
	public String apkPath;
	/**应用程序是否加锁*/
	public boolean isLock;
}