package cn.itcast.mobliesafe.chapter09.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/**
 * ���ŵĹ����� �ṩ���ű���API
 * 
 * @author Administrator
 * 
 */
public class SmsBackUpUtils {
	/**
	 * �����һ���ӿڣ������ص���
	 * @author Administrator
	 *
	 */
	public interface BackupStatusCallback{
		/**
		 * �ڶ��ű���֮ǰ���õķ���
		 * @param size �ܵĶ��ŵĸ���
		 */
		public void beforeSmsBackup(int size);
		
		/**
		 * ��sms���ű��ݹ����е��õķ���
		 * @param process ��ǰ�Ľ���
		 */
		public void onSmsBackup(int process);
	}
	
	private  boolean flag = true;
	
	public  void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * ���ݶ���
	 * 
	 * @param context
	 *            ������
	 * @param callback �ӿ�
	 * @return
	 * @throws FileNotFoundException
	 */
	public  boolean backUpSms(Context context,BackupStatusCallback callback)
			throws FileNotFoundException, IllegalStateException, IOException {
		XmlSerializer serializer = Xml.newSerializer();
		File sdDir = Environment.getExternalStorageDirectory();
		long freesize = sdDir.getFreeSpace();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& freesize > 1024l * 1024l) {
			File file = new File(Environment.getExternalStorageDirectory(),
					"backup.xml");
			FileOutputStream os = new FileOutputStream(file);
			// ��ʼ��xml�ļ������л���
			serializer.setOutput(os, "utf-8");
			// дxml�ļ���ͷ
			serializer.startDocument("utf-8", true);
			// д���ڵ�
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse("content://sms/");
			Cursor cursor = resolver.query(uri, new String[] { "address",
					"body", "type", "date" }, null, null, null);
			// �õ��ܵ���Ŀ�ĸ���
			int size = cursor.getCount();
			//���ý��ȵ��ܴ�С
			callback.beforeSmsBackup(size);
			serializer.startTag(null, "smss");
			serializer.attribute(null, "size", String.valueOf(size));
			int process  = 0;
			while (cursor.moveToNext() & flag) {
				serializer.startTag(null, "sms");
				serializer.startTag(null, "body");
				//���ܻ�������������Ҫ���������������ᵼ�±���ʦ��
				try {
					String bodyencpyt = Crypto.encrypt("123", cursor.getString(1));
					serializer.text(bodyencpyt);
				} catch (Exception e1) {
					e1.printStackTrace();
					serializer.text("���Ŷ�ȡʧ��");
				}
				serializer.endTag(null, "body");
				serializer.startTag(null, "address");
				serializer.text(cursor.getString(0));
				serializer.endTag(null, "address");
				serializer.startTag(null, "type");
				serializer.text(cursor.getString(2));
				serializer.endTag(null, "type");
				serializer.startTag(null, "date");
				serializer.text(cursor.getString(3));
				serializer.endTag(null, "date");
				serializer.endTag(null, "sms");
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//���ý������Ի���Ľ���
				process++;
				callback.onSmsBackup(process);
			}
			cursor.close();
			serializer.endTag(null, "smss");
			serializer.endDocument();
			os.flush();
			os.close();
			return flag;
		} else {
			throw new IllegalStateException("sd�������ڻ��߿ռ䲻��");
		}
	}
}
