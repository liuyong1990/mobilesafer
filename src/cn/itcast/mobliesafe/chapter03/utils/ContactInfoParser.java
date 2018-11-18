package cn.itcast.mobliesafe.chapter03.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import cn.itcast.mobliesafe.chapter03.entity.ContactInfo;

public class ContactInfoParser {
	
	
	
	public static List<ContactInfo> getSystemContact(Context context){
		ContentResolver resolver = context.getContentResolver();
		// 1. ��ѯraw_contacts������ϵ�˵�idȡ����
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			if (id != null) {
				System.out.println("��ϵ��id��" + id);
				ContactInfo info = new ContactInfo();
				info.id = id;
				// 2. ������ϵ�˵�id����ѯdata�������id������ȡ����
				// ϵͳapi ��ѯdata���ʱ�� ���������Ĳ�ѯdata�� ���ǲ�ѯ��data�����ͼ
				Cursor dataCursor = resolver.query(datauri, new String[] {
						"data1", "mimetype" }, "raw_contact_id=?",
						new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String data1 = dataCursor.getString(0);
					String mimetype = dataCursor.getString(1);
					if ("vnd.android.cursor.item/name".equals(mimetype)) {
						System.out.println("����=" + data1);
						info.name = data1;
					}  else if ("vnd.android.cursor.item/phone_v2"
							.equals(mimetype)) {
						System.out.println("�绰=" + data1);
						info.phone = data1;
					} 
					
				}
				//����������ֻ���Ϊ�գ���������������
				if(TextUtils.isEmpty(info.name) && TextUtils.isEmpty(info.phone))
					continue;
				infos.add(info);
				dataCursor.close();
			}
		}
		cursor.close();
		return infos;
	}
	
	 public static  List<ContactInfo> getSimContacts( Context context){
		 Uri uri = Uri.parse("content://icc/adn");
		 List<ContactInfo> infos = new ArrayList<ContactInfo>();
	     Cursor   mCursor = context.getContentResolver().query(uri, null, null, null, null);
	        if (mCursor != null) {
	            while (mCursor.moveToNext()) {
	            	ContactInfo info = new ContactInfo();
	                // ȡ����ϵ������
	                int nameFieldColumnIndex = mCursor.getColumnIndex("name");
	                info.name =  mCursor.getString(nameFieldColumnIndex);
	                // ȡ�õ绰����
	                int numberFieldColumnIndex = mCursor
	                        .getColumnIndex("number");
	                info.phone= mCursor.getString(numberFieldColumnIndex);
	                infos.add(info);
	            }
	        }
	        mCursor.close();
	        return infos;
	    }
	
}
