package cn.itcast.mobliesafe.chapter09.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter09.entity.AppInfo;
/**����ɸ��ã�δ�������Ѽ����������ô�Adapter**/
public class AppLockAdapter extends BaseAdapter {

	private List<AppInfo> appInfos;
	private Context context;
	
	/**
	 * ���췽��
	 * @param appInfos
	 * @param context
	 */
	public AppLockAdapter(List<AppInfo> appInfos, Context context) {
		super();
		this.appInfos = appInfos;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return appInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView !=null && convertView instanceof RelativeLayout){
			holder = (ViewHolder) convertView.getTag();
		}else{
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_list_applock, null);
			holder.mAppIconImgv = (ImageView) convertView.findViewById(R.id.imgv_appicon);
			holder.mAppNameTV = (TextView) convertView.findViewById(R.id.tv_appname);
			holder.mLockIcon = (ImageView) convertView.findViewById(R.id.imgv_lock);
			convertView.setTag(holder);
		}
			
		final AppInfo appInfo = appInfos.get(position);
		holder.mAppIconImgv.setImageDrawable(appInfo.icon);
		holder.mAppNameTV.setText(appInfo.appName);
		if(appInfo.isLock){
			//��ʾ��ǰӦ���Ѿ�����
			holder.mLockIcon.setBackgroundResource(R.drawable.applock_icon);
		}else{
			//��ǰӦ��δ����
			holder.mLockIcon.setBackgroundResource(R.drawable.appunlock_icon);
		}
		return convertView;
	}

	static class ViewHolder{
		TextView mAppNameTV;
		ImageView mAppIconImgv;
		/**����ͼƬ��ʾ�������ǲ�����*/
		ImageView mLockIcon;
	}
}
