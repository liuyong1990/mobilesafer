package cn.itcast.mobliesafe.chapter03.test;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import cn.itcast.mobliesafe.chapter03.db.dao.BlackNumberDao;
import cn.itcast.mobliesafe.chapter03.entity.BlackContactInfo;

public class TestBlackNumberDao extends AndroidTestCase {
	private Context context;

	@Override
	protected void setUp() throws Exception {
		context = getContext();
		super.setUp();
	}

	/**
	 * 测试添加
	 * 
	 * @throws Exception
	 */
	public void testAdd() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(context);
		Random random = new Random(8979);
		for (long i = 1; i < 30; i++) {
			BlackContactInfo info = new BlackContactInfo();
			info.phoneNumber = 13500000000l + i + "";
			info.contactName = "zhangsan" + i;
			info.mode = random.nextInt(3) + 1;
			dao.add(info);
		}
	}

	/**
	 * 测试刪除
	 * 
	 * @throws Exception
	 */
	public void testDelete() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(context);

		BlackContactInfo info = new BlackContactInfo();
		for (long i = 1; i < 5; i++) {
			info.phoneNumber = 13500000000l + i + "";
			dao.detele(info);
		}
	}

	/**
	 * 测试分页查询
	 * 
	 * @throws Exception
	 */
	public void testGetPageBlackNumber() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(context);
		List<BlackContactInfo> list = dao.getPageBlackNumber(2, 5);
		for (int i = 0; i < list.size(); i++) {
			Log.i("TestBlackNumberDao", list.get(i).phoneNumber);
		}
	}

	/**
	 * 测试根据号码查询黑名单信息
	 * 
	 * @throws Exception
	 */
	public void testGetBlackContactMode() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(context);
		int mode = dao.getBlackContactMode(13500000008l + "");
		Log.i("TestBlackNumberDao", mode + "");
	}

	/**
	 * 测试数据总条目
	 * 
	 * @throws Exception
	 */
	public void testGetTotalNumber() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(context);
		int total = dao.getTotalNumber();
		Log.i("TestBlackNumberDao", "数据总条目：  " + total);
	}

	/**
	 * 测试号码是否在数据库中
	 * 
	 * @throws Exception
	 */
	public void testIsNumberExist() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(context);
		boolean isExist = dao.IsNumberExist(13500000008l + "");
		if (isExist) {
			Log.i("TestBlackNumberDao", "该号码在数据库中");
		} else {
			Log.i("TestBlackNumberDao", "该号码不在数据库中");
		}
	}
}
