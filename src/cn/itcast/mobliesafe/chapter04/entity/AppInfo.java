package cn.itcast.mobliesafe.chapter04.entity;

import android.graphics.drawable.Drawable;

public class AppInfo {

	/** Ӧ�ó������ */
	public String packageName;
	/** Ӧ�ó���ͼ�� */
	public Drawable icon;
	/** Ӧ�ó������� */
	public String appName;
	/** Ӧ�ó���·�� */
	public String apkPath;
	/** Ӧ�ó����С */
	public long appSize;
	/** �Ƿ����ֻ��洢 */
	public boolean isInRoom;
	/** �Ƿ����û�Ӧ�� */
	public boolean isUserApp;
	/** �Ƿ�ѡ�У�Ĭ�϶�Ϊfalse */
	public boolean isSelected = false;

	/**�õ�Appλ���ַ���*/
	public String getAppLocation(boolean isInRoom) {
		if (isInRoom) {
			return "�ֻ��ڴ�";
		} else {
			return "�ⲿ�洢";
		}
	}

}
