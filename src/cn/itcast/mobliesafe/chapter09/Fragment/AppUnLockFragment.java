package cn.itcast.mobliesafe.chapter09.Fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter09.adapter.AppLockAdapter;
import cn.itcast.mobliesafe.chapter09.db.dao.AppLockDao;
import cn.itcast.mobliesafe.chapter09.entity.AppInfo;
import cn.itcast.mobliesafe.chapter09.utils.AppInfoParser;

public class AppUnLockFragment extends Fragment{

	private TextView mUnLockTV;
	private ListView mUnLockLV;
	List<AppInfo> unlockApps = new ArrayList<AppInfo>();
	private AppLockAdapter adapter;
	private AppLockDao dao;
	private Uri uri = Uri.parse("content://com.itcast.mobilesafe.applock");
	private List<AppInfo> appInfos;
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 100:
				unlockApps.clear();
				unlockApps.addAll(((List<AppInfo>)msg.obj));
				if(adapter == null){
					adapter = new AppLockAdapter(unlockApps, getActivity());
					mUnLockLV.setAdapter(adapter);
				}else{
					adapter.notifyDataSetChanged();
				}
				mUnLockTV.setText("δ����Ӧ��"+unlockApps.size()+"��");
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_appunlock, null);
		mUnLockTV = (TextView) view.findViewById(R.id.tv_unlock);
		mUnLockLV = (ListView) view.findViewById(R.id.lv_unlock);
		return view;
	}
	
	@Override
	public void onResume() {
		dao = new AppLockDao(getActivity());
		appInfos = AppInfoParser.getAppInfos(getActivity());
		fillData();
		initListener();
		super.onResume();
		getActivity().getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				fillData();
			}
		});
	}

	public void fillData() {
		
		final List<AppInfo>  aInfos = new ArrayList<AppInfo>();
		new Thread(){
			public void run() {
				for(AppInfo info : appInfos){
					if(!dao.find(info.packageName)){
						//δ����
						info.isLock = false;
						aInfos.add(info);
					}
				}
				Message msg = new Message();
				msg.obj = aInfos;
				msg.what = 100;
				mhandler.sendMessage(msg);
			};
		}.start();
		
		
	}

	private void initListener() {
		mUnLockLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if(unlockApps.get(position).packageName.equals("cn.itcast.mobliesafe")){
					return;
				}
				//��Ӧ�ü���
				//����һ������Ч��
				TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f,
						Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(300);
				view.startAnimation(ta);
				new Thread(){
					public void run() {
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//��������Ϣ�����뵽���ݿ���
								dao.insert(unlockApps.get(position).packageName);
								unlockApps.remove(position);
								adapter.notifyDataSetChanged();//֪ͨ�������
							}
						});
					};
				}.start();
			}
		});
	}
}
