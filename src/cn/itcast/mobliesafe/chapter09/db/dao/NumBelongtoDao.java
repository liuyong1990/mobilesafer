package cn.itcast.mobliesafe.chapter09.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**��ѯ��������ص����ݿ��߼���*/
public class NumBelongtoDao {
	
	/**
	 * ���ص绰����Ĺ�����
	 * 
	 * @param phonenumber
	 *            �绰����
	 * @return ������
	 */
	public static String getLocation(String phonenumber) {

		String location = phonenumber;
		// 130 131 132 133 134 135 136 137 137 139
		// 15x
		// 14x
		// 18x
		// 17x
		// ������11λ
		// ^1[34578]\d{9}$
		// path ���ݿ��ļ���·��
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobliesafe/files/address.db", null,
				SQLiteDatabase.OPEN_READONLY);

		if (phonenumber.matches("^1[34578]\\d{9}$")) {
			// �ֻ�����Ĳ�ѯ
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { phonenumber.substring(0, 7) });
			if (cursor.moveToNext()) {
				location = cursor.getString(0);
			}
			cursor.close();
		} else {// �����绰
			switch (phonenumber.length()) {// �жϵ绰����ĳ���
			case 3: // 110 120 119 121 999
				if ("110".equals(phonenumber)) {
					location = "�˾�";
				} else if ("120".equals(phonenumber)) {
					location = "����";
				} else {
					location = "��������";
				}
				break;
			case 4:
				location = "ģ����";
				break;
			case 5:
				location = "�ͷ��绰";
				break;
			case 7:
				location = "���ص绰";
				break;
			case 8:
				location = "���ص绰";
				break;
			default:
				if(location.length()>=9&& location.startsWith("0")){
					String address = null;
					//select location from data2 where area = '10'
					Cursor cursor = db.rawQuery("select location from data2 where area = ?", new String[]{location.substring(1, 3)});
					if(cursor.moveToNext()){
						String str = cursor.getString(0);
						address = str.substring(0, str.length()-2);
					}
					cursor.close();
					cursor = db.rawQuery("select location from data2 where area = ?", new String[]{location.substring(1, 4)});
					if(cursor.moveToNext()){
						String str = cursor.getString(0);
						address = str.substring(0, str.length()-2);
					}
					cursor.close();
					if(!TextUtils.isEmpty(address)){
						location = address;
					}
				}
				break;
			}
		}
		db.close();
		return location;
	}
}
