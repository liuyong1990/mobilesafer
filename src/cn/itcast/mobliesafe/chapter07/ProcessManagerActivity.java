package cn.itcast.mobliesafe.chapter07;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter07.adapter.ProcessManagerAdapter;
import cn.itcast.mobliesafe.chapter07.entity.TaskInfo;
import cn.itcast.mobliesafe.chapter07.utils.SystemInfoUtils;
import cn.itcast.mobliesafe.chapter07.utils.TaskInfoParser;

public class ProcessManagerActivity extends Activity implements OnClickListener {

	private TextView mRunProcessNum;
	private TextView mMemoryTV;
	private TextView mProcessNumTV;
	private ListView mListView;
	ProcessManagerAdapter adapter;
	private List<TaskInfo> runningTaskInfos;
	private List<TaskInfo> userTaskInfos = new ArrayList<TaskInfo>();
	private List<TaskInfo> sysTaskInfo = new ArrayList<TaskInfo>();
	private ActivityManager manager;
	private int runningPocessCount;
	private long totalMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_processmanager);
		initView();
		fillData();
	}
	
	@Override
	protected void onResume() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	private void initView() {
		findViewById(R.id.rl_titlebar).setBackgroundColor(
				getResources().getColor(R.color.bright_green));
		ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
		mLeftImgv.setOnClickListener(this);
		mLeftImgv.setImageResource(R.drawable.back);
		ImageView mRightImgv = (ImageView) findViewById(R.id.imgv_rightbtn);
		mRightImgv.setImageResource(R.drawable.processmanager_setting_icon);
		mRightImgv.setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title)).setText("���̹���");
		mRunProcessNum = (TextView) findViewById(R.id.tv_runningprocess_num);
		mMemoryTV = (TextView) findViewById(R.id.tv_memory_processmanager);
		mProcessNumTV = (TextView) findViewById(R.id.tv_user_runningprocess);
		 runningPocessCount = SystemInfoUtils
			.getRunningPocessCount(ProcessManagerActivity.this);
		mRunProcessNum.setText("�����еĽ��̣� "+runningPocessCount+ "��");
		long totalAvailMem = SystemInfoUtils.getAvailMem(this);
		totalMem = SystemInfoUtils.getTotalMem();
		mMemoryTV.setText("����/���ڴ棺"
				+ Formatter.formatFileSize(this, totalAvailMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
		mListView = (ListView) findViewById(R.id.lv_runningapps);
		initListener();
	}

	private void initListener() {
		findViewById(R.id.btn_selectall).setOnClickListener(this);
		findViewById(R.id.btn_select_inverse).setOnClickListener(this);
		findViewById(R.id.btn_cleanprocess).setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object object = mListView.getItemAtPosition(position);
				if (object != null & object instanceof TaskInfo) {
					TaskInfo info = (TaskInfo) object;
					if (info.packageName.equals(getPackageName())) {
						// ��ǰ�������Ŀ�Ǳ�Ӧ�ó���
						return;
					}
					info.isChecked = !info.isChecked;
					adapter.notifyDataSetChanged();
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
				if (firstVisibleItem >= userTaskInfos.size() + 1){
					mProcessNumTV.setText("ϵͳ���̣�" + sysTaskInfo.size() + "��");
				}else{
					mProcessNumTV.setText("�û����̣� " + userTaskInfos.size() + "��");
				}
			}
		});
	}

	private void fillData() {
		userTaskInfos.clear();
		sysTaskInfo.clear();
		new Thread() {
			public void run() {
				runningTaskInfos = TaskInfoParser
						.getRunningTaskInfos(getApplicationContext());

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (TaskInfo taskInfo : runningTaskInfos) {
							if (taskInfo.isUserApp) {
								userTaskInfos.add(taskInfo);
							} else {
								sysTaskInfo.add(taskInfo);
							}
						}
						if (adapter == null) {
							adapter = new ProcessManagerAdapter(
									getApplicationContext(), userTaskInfos,
									sysTaskInfo);
							mListView.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						if (userTaskInfos.size() > 0){
							mProcessNumTV.setText("�û����̣� "
									+ userTaskInfos.size() + "��");
						}else{mProcessNumTV.setText("ϵͳ���̣�" + sysTaskInfo.size()
									+ "��");
						}
					}
				});
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_leftbtn:
			finish();
			break;
		case R.id.imgv_rightbtn:
			//��ת�� ���̹�������ҳ��
			startActivity(new Intent(this,ProcessManagerSettingActivity.class));
		break;
		case R.id.btn_selectall:
			selectAll();
			break;
		case R.id.btn_select_inverse:
			inverse();
			break;
		case R.id.btn_cleanprocess:
			cleanProcess();
			break;
		}
	}

	/**�������*/
	private void cleanProcess() {
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count=0;
		long saveMemory = 0;
		List<TaskInfo> killedtaskInfos = new ArrayList<TaskInfo>();
		//ע�⣬��������ʱ���ܸı伯�ϴ�С
		for(TaskInfo info : userTaskInfos){
			if(info.isChecked){
				count++;
				saveMemory += info.appMemory;
				manager.killBackgroundProcesses(info.packageName);
				killedtaskInfos.add(info);
			}
		}
		for(TaskInfo info : sysTaskInfo){
			if(info.isChecked){
				count++;
				saveMemory += info.appMemory;
				manager.killBackgroundProcesses(info.packageName);
				killedtaskInfos.add(info);
			}
		}
		
		for(TaskInfo info : killedtaskInfos){
			if(info.isUserApp){
				userTaskInfos.remove(info);
			}
			else{
				sysTaskInfo.remove(info);
			}
			
		}
		runningPocessCount -=count;
		mRunProcessNum.setText("�����еĽ��̣�"+runningPocessCount+"��");
		mMemoryTV.setText("����/���ڴ棺"
				+ Formatter.formatFileSize(this, SystemInfoUtils.getAvailMem(this)) + "/"
				+ Formatter.formatFileSize(this, totalMem));
		Toast.makeText(this, "������" + count + "������,�ͷ���"
				+ Formatter.formatFileSize(this, saveMemory) + "�ڴ�", 1).show();
		mProcessNumTV.setText("�û����̣�"+userTaskInfos.size()+"��");
		adapter.notifyDataSetChanged();
	}

	/** ��ѡ */
	private void inverse() {
		for (TaskInfo taskInfo : userTaskInfos) {
			// ���Ǳ�Ӧ�ó���
			if (taskInfo.packageName.equals(getPackageName())) {
				continue;
			}
			boolean checked = taskInfo.isChecked;
			taskInfo.isChecked = !checked;
		}
		for (TaskInfo taskInfo : sysTaskInfo) {
			boolean checked = taskInfo.isChecked;
			taskInfo.isChecked = !checked;
		}
		adapter.notifyDataSetChanged();
	}

	/** ȫѡ */
	private void selectAll() {
		for (TaskInfo taskInfo : userTaskInfos) {
			// ���Ǳ�Ӧ�ó���
			if (taskInfo.packageName.equals(getPackageName())) {
				continue;
			}
			taskInfo.isChecked = true;
		}
		for (TaskInfo taskInfo : sysTaskInfo) {
			taskInfo.isChecked = true;
		}
		adapter.notifyDataSetChanged();
	}

}
