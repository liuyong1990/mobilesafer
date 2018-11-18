package cn.itcast.mobliesafe.chapter09.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcast.mobliesafe.R;

public class AdvancedToolsView extends RelativeLayout{

	private TextView mDesriptionTV;
	private String desc = "";
	private Drawable drawable;
	private ImageView mLeftImgv;

	public AdvancedToolsView(Context context) {
		super(context);
		init(context);
	}
	
	public AdvancedToolsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	public AdvancedToolsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//�õ����Զ����ֵ
	    TypedArray mTypedArray = context.obtainStyledAttributes(attrs,  
                R.styleable.AdvancedToolsView);
	    desc = mTypedArray.getString(R.styleable.AdvancedToolsView_desc);
	    drawable = mTypedArray.getDrawable(R.styleable.AdvancedToolsView_android_src);
	    mTypedArray.recycle();
	    init(context);
	}
	
	/**
	 * �ؼ���ʼ��
	 * @param context
	 */
	private void init(Context context) {
		//����Դת����view������ʾ���Լ�����
		View view  = View.inflate(context, R.layout.ui_advancedtools_view, null);
		this.addView(view);
		mDesriptionTV = (TextView) findViewById(R.id.tv_decription);
		mLeftImgv = (ImageView) findViewById(R.id.imgv_left);
		mDesriptionTV.setText(desc);
		if(drawable != null)mLeftImgv.setImageDrawable(drawable);
	}
}
