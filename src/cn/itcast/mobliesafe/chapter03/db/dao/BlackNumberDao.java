package cn.itcast.mobliesafe.chapter03.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import cn.itcast.mobliesafe.chapter03.db.BlackNumberOpenHelper;
import cn.itcast.mobliesafe.chapter03.entity.BlackContactInfo;

public class BlackNumberDao {

	private BlackNumberOpenHelper blackNumberOpenHelper;

	public BlackNumberDao(Context context) {
		super();
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}

	/***
	 * �������
	 * 
	 * @param blackContactInfo
	 * @return
	 */
	public boolean add(BlackContactInfo blackContactInfo) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if (blackContactInfo.phoneNumber.startsWith("+86")) {
			blackContactInfo.phoneNumber = blackContactInfo.phoneNumber
					.substring(3, blackContactInfo.phoneNumber.length());
		}
		values.put("number", blackContactInfo.phoneNumber);
		values.put("name", blackContactInfo.contactName);
		values.put("mode", blackContactInfo.mode);
		long rowid = db.insert("blacknumber", null, values);
		if (rowid == -1){ // �������ݲ��ɹ�
			return false;
		}else{
			return true;
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param blackContactInfo
	 * @return
	 */
	public boolean detele(BlackContactInfo blackContactInfo) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		int rownumber = db.delete("blacknumber", "number=?",
				new String[] { blackContactInfo.phoneNumber });
		if (rownumber == 0){
			return false; // ɾ�����ݲ��ɹ�
		}else{
			return true;
		}
	}
 
	/**
	 * ��ҳ��ѯ���ݿ�ļ�¼
	 * @param pagenumber,�ڼ�ҳҳ�� �ӵ�0ҳ��ʼ
	 * @param pagesize
	 *            ÿһ��ҳ��Ĵ�С
	 */
	public List<BlackContactInfo> getPageBlackNumber(int pagenumber,
			int pagesize) {
		// �õ��ɶ������ݿ�
		SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode,name from blacknumber limit ? offset ?",
				new String[] { String.valueOf(pagesize),
						String.valueOf(pagesize * pagenumber) });
		List<BlackContactInfo> mBlackContactInfos = new ArrayList<BlackContactInfo>();
		while (cursor.moveToNext()) {
			BlackContactInfo info = new BlackContactInfo();
			info.phoneNumber = cursor.getString(0);
			info.mode = cursor.getInt(1);
			info.contactName = cursor.getString(2);
			mBlackContactInfos.add(info);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(30);
		return mBlackContactInfos;
	}

	/**
	 * �жϺ����Ƿ��ں������д���
	 * 
	 * @param number
	 * @return
	 */
	public boolean IsNumberExist(String number) {
		// �õ��ɶ������ݿ�
		SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", null, "number=?",
				new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}

	/**
	 * ���ݺ����ѯ��������Ϣ
	 * 
	 * @param number
	 * @return
	 */
	public int getBlackContactMode(String number) {
		// �õ��ɶ������ݿ�
		SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?",
				new String[] { number }, null, null, null);
		int mode = 0;
		if (cursor.moveToNext()) {
			mode = cursor
					.getInt(cursor.getColumnIndex("mode"));
			
		}
		cursor.close();
		db.close();
		return mode;
	}

	/**
	 * ��ȡ���ݿ������Ŀ����
	 * 
	 * @param pagenumber
	 *            �ڼ�ҳ��ҳ�� �ӵ�0ҳ��ʼ
	 * @param pagesize
	 *            ÿһ��ҳ��Ĵ�С
	 */
	public int getTotalNumber() {
		// �õ��ɶ������ݿ�
		SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}
}
