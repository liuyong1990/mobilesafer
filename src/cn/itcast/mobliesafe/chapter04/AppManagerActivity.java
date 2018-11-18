package cn.itcast.mobliesafe.chapter04;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter04.adapter.AppManagerAdapter;
import cn.itcast.mobliesafe.chapter04.entity.AppInfo;
import cn.itcast.mobliesafe.chapter04.utils.AppInfoParser;

public class AppManagerActivity extends Activity implements OnClickListener{
	/**�ֻ�ʣ���ڴ�TextView*/
	private TextView mPhoneMemoryTV;
	/**չʾSD��ʣ���ڴ�TextView*/
	private TextView mSDMemoryTV;
	private ListView mListView;
	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos = new ArrayList<AppInfo>();
	private  List<AppInfo> systemAppInfos = new ArrayList<AppInfo>();
	private AppManagerAdapter adapter;
	/**����Ӧ�ó���ж�سɹ��Ĺ㲥*/
	private UninstallRececiver receciver;
	private Handler mHandler = new Handler(){

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 10:
				if(adapter == null){
					adapter = new AppManagerAdapter(userAppInfos, systemAppInfos, AppManagerActivity.this);
				}
				mListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case 15:
				adapter.notifyDataSetChanged();
				break;
			}
		};
	};
	private TextView mAppNumTV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_app_manager);
		//ע��㲥
		receciver = new UninstallRececiver();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addDataScheme("package");
		registerReceiver(receciver, intentFilter);
		initView();
	}

	/**��ʼ���ؼ�*/
	private void initView() {
		findViewById(R.id.rl_titlebar).setBackgroundColor(
				getResources().getColor(R.color.bright_yellow));
		ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
		((TextView) findViewById(R.id.tv_title)).setText("����ܼ�");
		mLeftImgv.setOnClickListener(this);
		mLeftImgv.setImageResource(R.drawable.back);
		mPhoneMemoryTV = (TextView) findViewById(R.id.tv_phonememory_appmanager);
		mSDMemoryTV = (TextView) findViewById(R.id.tv_sdmemory_appmanager);
		mAppNumTV = (TextView) findViewById(R.id.tv_appnumber);
		mListView = (ListView) findViewById(R.id.lv_appmanager);
		//�õ��ֻ�ʣ���ڴ��SD��ʣ���ڴ�
		getMemoryFromPhone();
		initData();
		initListener();
	}

	private void initListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (adapter != null) {
					new Thread(){
						public void run() {
							AppInfo mappInfo  = (AppInfo) adapter.getItem(position);
							//��ס��ǰ��Ŀ��״̬
							boolean flag = mappInfo.isSelected;
							//�Ƚ�������������Ŀ��AppInfo��Ϊδѡ��״̬
							for(AppInfo appInfo :userAppInfos){
								appInfo.isSelected = false;
							}
							for(AppInfo appInfo : systemAppInfos){
								appInfo.isSelected = false;
							}
							if(mappInfo != null){
								//����Ѿ�ѡ�У����Ϊδѡ��
								if(flag){
									mappInfo.isSelected = false;
								}else{
									mappInfo.isSelected = true;
								}
								mHandler.sendEmptyMessage(15);
							}
						};
					}.start();
					
				}
				
				
			}
		});
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem >= userAppInfos.size()+1){
					mAppNumTV.setText("ϵͳ����"+systemAppInfos.size()+"��");
				}else{
					mAppNumTV.setText("�û�����"+userAppInfos.size()+"��");
				}
			}
		});
	}

	private void initData() {
		appInfos = new ArrayList<AppInfo>();
		 new Thread(){
			 public void run() {
				 appInfos.clear();
				 userAppInfos.clear();
				 systemAppInfos.clear();
				 appInfos.addAll(AppInfoParser.getAppInfos(AppManagerActivity.this));
				 for( AppInfo appInfo : appInfos){
					 //������û�App
					 if(appInfo.isUserApp){
						 userAppInfos.add(appInfo);
					 }else{
						 systemAppInfos.add(appInfo);
					 }
				 }
				 mHandler.sendEmptyMessage(10);
			 };
		 }.start();
		 
	}

	/**�õ��ֻ���SD��ʣ���ڴ�*/
	private void getMemoryFromPhone() {
		long avail_sd = Environment.getExternalStorageDirectory()
				.getFreeSpace();
		long avail_rom = Environment.getDataDirectory().getFreeSpace();
		//��ʽ���ڴ�
		String str_avail_sd = Formatter.formatFileSize(this, avail_sd);
		String str_avail_rom = Formatter.formatFileSize(this, avail_rom);
		mPhoneMemoryTV.setText("ʣ���ֻ��ڴ棺" + str_avail_rom);
		mSDMemoryTV.setText("ʣ��SD���ڴ棺" + str_avail_sd);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_leftbtn:
			finish();
			break;
		}
	}
	
	/***
	 * ����Ӧ�ó���ж�صĹ㲥
	 * @author admin
	 */
	class UninstallRececiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// �յ��㲥��
			initData();
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receciver);
		receciver = null;
		super.onDestroy();
	}
}
