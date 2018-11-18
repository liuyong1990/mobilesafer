package cn.itcast.mobliesafe.chapter10.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.itcast.mobliesafe.R;

public class SettingView extends RelativeLayout {

	private String setTitle ="";
	private String status_on ="";
	private String status_off ="";
	private TextView mSettingTitleTV;
	private TextView mSettingStatusTV;
	private ToggleButton mToggleBtn;
	private boolean isChecked;
	private OnCheckedStatusIsChanged onCheckedStatusIsChanged;

	public SettingView(Context context) {
		super(context);
		init(context);
	}

	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//�õ����Զ����ֵ
	    TypedArray mTypedArray = context.obtainStyledAttributes(attrs,  
                R.styleable.SettingView);
	    setTitle = mTypedArray.getString(R.styleable.SettingView_settitle);
	    status_on = mTypedArray.getString(R.styleable.SettingView_status_on);
	    status_off = mTypedArray.getString(R.styleable.SettingView_status_off);
	    isChecked = mTypedArray.getBoolean(R.styleable.SettingView_status_ischecked,false);
	    mTypedArray.recycle();
	    init(context);
	    setStatus(status_on, status_off, isChecked);
	}

	/***
	 * ��ʼ���ؼ�
	 * @param context
	 */
	private void init(Context context) {
		View view  = View.inflate(context, R.layout.ui_settings_view, null);
		this.addView(view);
		mSettingTitleTV = (TextView) findViewById(R.id.tv_setting_title);
		mSettingStatusTV = (TextView) findViewById(R.id.tv_setting_status);
		mToggleBtn = (ToggleButton) findViewById(R.id.toggle_setting_status);
		mSettingTitleTV.setText(setTitle);
		mToggleBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setChecked(isChecked);
				onCheckedStatusIsChanged.onCheckedChanged(SettingView.this, isChecked);
			}
		});
	}
	
	/***
	 * ������Ͽؼ��Ƿ�ѡ��
	 * @return
	 */
	public boolean isChecked() {
		return mToggleBtn.isChecked();
	}

	/**
	 * ������Ͽؼ���ѡ�з�ʽ
	 * @param checked
	 */
	public void setChecked(boolean checked) {
		mToggleBtn.setChecked(checked);
		isChecked = checked;
		if (checked) {
			if (!TextUtils.isEmpty(status_on)) {
				mSettingStatusTV.setText(status_on);
			}
		} else {
			if (!TextUtils.isEmpty(status_off)) {
				mSettingStatusTV.setText(status_off);
			}
		}
	}
	
	/**
	 * �����Զ���ؼ�������
	 * @param text
	 */
	public void setStatus(String status_on, String status_off, boolean checked) {
		if (checked) {
			mSettingStatusTV.setText(status_on);
		} else {
			mSettingStatusTV.setText(status_off);
		}
		mToggleBtn.setChecked(checked);
	}
	
	/**
	 * ����״̬�仯����
	 */
	public void setOnCheckedStatusIsChanged(OnCheckedStatusIsChanged onCheckedStatusIsChanged){
		this.onCheckedStatusIsChanged = onCheckedStatusIsChanged;
	}
	
	/***
	 * �ص��ӿ�
	 * @author admin
	 */
	public interface OnCheckedStatusIsChanged{
		/***
		 * ��ǰView��check״̬�����仯ʱ����ô˷���
		 * @param view 
		 * @param isChecked
		 */
		void onCheckedChanged(View view,boolean isChecked);
	}
}
