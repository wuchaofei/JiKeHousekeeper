package com.jike.shanglv;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivityInternationalRequisitionSuccess extends Activity {

	protected static final String RECEIPT_ORDER_ID = "RECEIPT_ORDER_ID";
	private ImageButton back_imgbtn, home_imgbtn;
	private TextView order_no_tv;
	private String orderId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_international_requisition_success);
			((MyApplication) getApplication()).addActivity(this);

			back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
			home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
			order_no_tv = (TextView) findViewById(R.id.order_no_tv);
			back_imgbtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			home_imgbtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(
							ActivityInternationalRequisitionSuccess.this,
							MainActivity.class));
				}
			});
			Bundle bundle = this.getIntent().getExtras();
			if (bundle != null) {
				if (bundle.containsKey(RECEIPT_ORDER_ID))
					orderId = bundle.getString((RECEIPT_ORDER_ID));
			}
			order_no_tv.setText("¶©µ¥ºÅ£º\n" + orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
