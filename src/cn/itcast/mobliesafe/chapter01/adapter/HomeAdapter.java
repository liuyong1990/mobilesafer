package cn.itcast.mobliesafe.chapter01.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.itcast.mobliesafe.R;

/**GridView显示界面用到的Adapter*/
public class HomeAdapter extends BaseAdapter {
	
	int[] imageId = { R.drawable.safe, 
			 R.drawable.callmsgsafe, R.drawable.app, R.drawable.trojan, R.drawable.sysoptimize,R.drawable.taskmanager, 
			 R.drawable.netmanager,R.drawable.atools,R.drawable.settings };
	String[] names = { "手机防盗",   "通讯卫士","软件管家","手机杀毒","缓存清理","进程管理", 
			"流量统计", "高级工具", "设置中心" };
	private Context context;
	
	public HomeAdapter(Context context) {
		this.context = context;
	}
	// 设置gridView一个多少个条目
	@Override
	public int getCount() {
		return 9;
	}
	// 设置每个条目的界面
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(context,
				R.layout.item_home, null);
		ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		iv_icon.setImageResource(imageId[position]);
		tv_name.setText(names[position]);
		return view;
	}
	//  后面两个方法暂时不需要设置
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
}