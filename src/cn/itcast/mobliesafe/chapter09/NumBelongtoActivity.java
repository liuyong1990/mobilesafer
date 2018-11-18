package cn.itcast.mobliesafe.chapter09;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobliesafe.R;
import cn.itcast.mobliesafe.chapter09.db.dao.NumBelongtoDao;
/**�����ز�ѯ*/
public class NumBelongtoActivity extends Activity implements OnClickListener{

	private EditText mNumET;
	private TextView mResultTV;
	private String dbName = "address.db";
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_numbelongto);
		initView();
		copyDB(dbName);
	}

	/**��ʼ���ؼ�*/
	private void initView() {
		findViewById(R.id.rl_titlebar).setBackgroundColor(
				getResources().getColor(R.color.bright_red));
		ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
		((TextView) findViewById(R.id.tv_title)).setText("��������ز�ѯ");
		mLeftImgv.setOnClickListener(this);
		mLeftImgv.setImageResource(R.drawable.back);
		findViewById(R.id.btn_searchnumbelongto).setOnClickListener(this);
		mNumET = (EditText) findViewById(R.id.et_num_numbelongto);
		mResultTV = (TextView) findViewById(R.id.tv_searchresult);
		
		mNumET.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				//�ı��仯֮��
				String string = s.toString().toString().trim();
				if(string.length() == 0){
					mResultTV.setText("");
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_leftbtn:
			finish();
			break;

		case R.id.btn_searchnumbelongto:
			//�ж�edittext�еĺ����Ƿ�Ϊ��
			//�ж����ݿ��Ƿ����
			String phonenumber = mNumET.getText().toString().trim();
			if(!TextUtils.isEmpty(phonenumber)){
				File file = new File(getFilesDir(),dbName);
				if(!file.exists() || file.length()<=0){
					//���ݿⲻ����,�������ݿ�
					copyDB(dbName);
				}
				//��ѯ���ݿ�
				String location = NumBelongtoDao.getLocation(phonenumber);
				mResultTV.setText("�����أ� "+location);
			}else{
				Toast.makeText(this, "��������Ҫ��ѯ�ĺ���", 0).show();
			}
			break;
		}
	}
	
	
	/**
	 * �����ʲ�Ŀ¼�µ����ݿ��ļ�
	 * @param dbname  ���ݿ��ļ�������
	 */
	private void copyDB(final String dbname) {
		new Thread(){
			public void run() {
				try {
					File file = new File(getFilesDir(),dbname);
					if(file.exists()&&file.length()>0){
						Log.i("NumBelongtoActivity","���ݿ��Ѵ���");
						return ;
					}
					InputStream is = getAssets().open(dbname);
					FileOutputStream fos  = openFileOutput(dbname, MODE_PRIVATE);
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = is.read(buffer))!=-1){
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
}
