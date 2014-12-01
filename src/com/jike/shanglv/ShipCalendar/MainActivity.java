package com.jike.shanglv.ShipCalendar;

import static android.widget.Toast.LENGTH_SHORT;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;
import com.jike.shanglv.MyApplication;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;
import com.jike.shanglv.R;

public class MainActivity extends Activity{
	public static final String TITLE="TITLE";
	private CalendarPickerView calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_picker);
		((MyApplication)getApplication()).addActivity(this);
		
		String title="请选择日期";
		Bundle bundle=getIntent().getExtras();
		if (bundle!=null) {
			if(bundle.containsKey("TITLE"))title=bundle.getString(TITLE);
		}
		((TextView)findViewById(R.id.title_tv)).setText(title);

		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);
		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -1);

		final Calendar thisMonth = Calendar.getInstance();
		thisMonth.add(Calendar.MONTH, 0);
		final Calendar lastMonth = Calendar.getInstance();
		lastMonth.add(Calendar.MONTH, 6);

		calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
		// calendar.init(lastYear.getTime(), nextYear.getTime()) //
		// .inMode(SelectionMode.SINGLE) //
		// .withSelectedDate(new Date());
		calendar.init(thisMonth.getTime(), lastMonth.getTime()) //
				.inMode(SelectionMode.SINGLE) //
				.withSelectedDate(new Date());
		calendar.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(MainActivity.this,
						calendar.getSelectedDate().getTime() + "aaa",
						LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(MainActivity.this,
						calendar.getSelectedDate().getTime() + "sss",
						LENGTH_SHORT).show();
			}
		});
		
		findViewById(R.id.finish_tv).setOnClickListener(
				selected_finishClickListener);
		findViewById(R.id.back_imgbtn).setOnClickListener(
				selected_finishClickListener);
		
		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {
			@Override
			public void onDateUnselected(Date date) {
				throwDate();
			}
			@Override
			public void onDateSelected(Date date) {
				throwDate();
			}
		});
	}

	OnClickListener selected_finishClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			throwDate();
		}
	};

	/**将选择的日期抛出到上一个调用activity
	 */
	private void throwDate(){
		String month = "",day="";
		if (calendar.getSelectedDate().getMonth() < 9)
			month = "0" + (calendar.getSelectedDate().getMonth() + 1);
		else
			month = String.valueOf(calendar.getSelectedDate().getMonth() + 1);
		day=calendar.getSelectedDate().getDate()+"";
		if (calendar.getSelectedDate().getDate()<9) {
			day= "0" + day;
		}
		String date1 = (calendar.getSelectedDate().getYear() + 1900) + "-"+ month+ "-"+ day;
		// Toast.makeText(MainActivity.this, date, LENGTH_SHORT).show();
		setResult(0, getIntent().putExtra("pickedDate", date1));
		finish();
	}
}
