//酒店筛选
package com.jike.shanglv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jike.shanglv.Common.ClearEditText;
import com.jike.shanglv.Common.StarLevel;
import com.jike.shanglv.Enums.SPkeys;


public class ActivityHotelFilter extends Activity {

	protected static final int FITER_RESULT_CODE = 0;
	private ImageButton back_imgbtn;
	private TextView xingji_tv, jiage_tv, reset_tv;
	private com.jike.shanglv.Common.ClearEditText keywords_et;
	private LinearLayout xingji_ll, jiage_ll;
	private Button ok_button;
	private Context context;
	InputMethodManager imm;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_hotel_filter);
			initView();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		context = this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		xingji_tv = (TextView) findViewById(R.id.xingji_tv);
		jiage_tv = (TextView) findViewById(R.id.jiage_tv);
		keywords_et = (ClearEditText) findViewById(R.id.keywords_et);
		xingji_ll = (LinearLayout) findViewById(R.id.xingji_ll);
		jiage_ll = (LinearLayout) findViewById(R.id.jiage_ll);
		reset_tv = (TextView) findViewById(R.id.reset_tv);
		back_imgbtn.setOnClickListener(clickListener);
		xingji_ll.setOnClickListener(clickListener);
		jiage_ll.setOnClickListener(clickListener);
		ok_button = (Button) findViewById(R.id.ok_button);
		ok_button.setOnClickListener(clickListener);
		reset_tv.setOnClickListener(clickListener);
	}

	View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.back_imgbtn:
					finish();
					break;
				case R.id.home_imgbtn:
					startActivity(new Intent(context, MainActivity.class));
					break;
				case R.id.xingji_ll:
					imm.hideSoftInputFromWindow(((Activity) context)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					iniPopupWindow(0, initXingjiData());
					pwMyPopWindow.showAtLocation(ok_button, Gravity.BOTTOM, 0,
							0);
					break;
				case R.id.reset_tv:
					xingji_tv.setText("不限");
					jiage_tv.setText("不限");
					keywords_et.setText("");
					break;
				case R.id.jiage_ll:
					imm.hideSoftInputFromWindow(((Activity) context)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					iniPopupWindow(1, initJiageData());
					pwMyPopWindow.showAtLocation(ok_button, Gravity.BOTTOM, 0,
							0);
					break;
				case R.id.ok_button:
					// Intent intents = new Intent(context,
					// ActivityHotelSearchlist.class);
					// intents.putExtra("starlevel",
					// xingji_tv.getText().toString());
					// intents.putExtra("price", jiage_tv.getText().toString());
					// intents.putExtra("keywords",
					// keywords_et.getText().toString());
					// startActivity(intents);
					String keywords = keywords_et.getText().toString();
					String star = StarLevel.StarlevelReverse.get(xingji_tv
							.getText().toString());
					String price = jiage_tv.getText().toString();
					String minprice = "",
					maxprice = "";
					if (price.equals("￥150以下")) {
						minprice = "";
						maxprice = "150";
					} else if (price.equals("￥150-￥300")) {
						minprice = "150";
						maxprice = "300";
					} else if (price.equals("￥301-￥450")) {
						minprice = "301";
						maxprice = "450";
					} else if (price.equals("￥451-￥600")) {
						minprice = "451";
						maxprice = "600";
					} else if (price.equals("￥601-￥1000")) {
						minprice = "601";
						maxprice = "1000";
					} else if (price.equals("￥1000以上")) {
						minprice = "1000";
						maxprice = "";
					}
					Bundle bundle = new Bundle();// minprice = "", maxprice =
													// "",
													// star
					bundle.putString("minprice", minprice);
					bundle.putString("maxprice", maxprice);
					bundle.putString("star", star);
					bundle.putString("keywords", keywords_et.getText()
							.toString());
					setResult(0, getIntent().putExtra("filterdDate", bundle));
					finish();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private PopupWindow pwMyPopWindow;// popupwindow
	private ListView lvPopupList;
	private int currentID_XJ = 0;
	private int currentID_JG = 0;

	/*
	 * xjOrJg 0:星级；1：价格
	 */
	private void iniPopupWindow(final int xjOrJg,
			final List<Map<String, Object>> list1) {
		final LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popupwindow_list_select, null);
		lvPopupList = (ListView) layout.findViewById(R.id.lv_popup_list);
		pwMyPopWindow = new PopupWindow(layout);
		pwMyPopWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

		MyListAdapter adapter = new MyListAdapter(context, list1);
		adapter.setCurrentID(xjOrJg == 0 ? currentID_XJ : currentID_JG);
		lvPopupList.setAdapter(adapter);
		lvPopupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					if (xjOrJg == 0) {// 0:星级
						xingji_tv.setText(list1.get(position).get("title")
								.toString());
						currentID_XJ = position;
						pwMyPopWindow.dismiss();
					} else if (xjOrJg == 1) {// 1：价格
						jiage_tv.setText(list1.get(position).get("title")
								.toString());
						currentID_JG = position;
						pwMyPopWindow.dismiss();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 控制popupwindow的宽度和高度自适应
		lvPopupList.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		pwMyPopWindow.setWidth(LayoutParams.FILL_PARENT);// lvPopupList.getMeasuredWidth()
		pwMyPopWindow.setHeight(LayoutParams.FILL_PARENT);// ((lvPopupList.getMeasuredHeight())*
															// list1.size());
		pwMyPopWindow.setAnimationStyle(R.style.AnimBottomPopup);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 控制popupwindow点击屏幕其他地方消失
		pwMyPopWindow.setBackgroundDrawable(dw);// (new BitmapDrawable());//
												// 设置背景图片，不能在布局中设置，要通过代码来设置
		pwMyPopWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上

		// 对弹出的全屏选择框添加OnTouchListener监听判断获取触屏位置，如果在listview外面则销毁弹出框
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					View layout = inflater.inflate(
							R.layout.popupwindow_list_select, null);
					int height = lvPopupList.getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							pwMyPopWindow.dismiss();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});
	}

	private ArrayList<Map<String, Object>> initXingjiData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "不限");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "五星级/豪华");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "四星级/高档");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "三星级/舒适");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "二星级及以下");
		list.add(map);
		return list;
	}

	private ArrayList<Map<String, Object>> initJiageData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "不限");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥150以下");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥150-￥300");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥301-￥450");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥451-￥600");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥601-￥1000");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("title", "￥1000以上");
		list.add(map);
		return list;
	}

	private class MyListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		private LayoutInflater inflater;
		List<Map<String, Object>> list;
		Context c;
		int currentID = 0;

		public MyListAdapter(Context context, List<Map<String, Object>> list2) {
			inflater = LayoutInflater.from(context);
			this.c = context;
			this.list = list2;
		}

		public void setList(ArrayList<Map<String, Object>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				Holder myHolder;
				if (convertView == null) {
					myHolder = new Holder();
					convertView = inflater.inflate(
							R.layout.item_train_baoxian_list_single, null);
					myHolder.title = (TextView) convertView
							.findViewById(R.id.title);
					myHolder.iv = (ImageView) convertView
							.findViewById(R.id.img);
					convertView.setTag(myHolder);
				} else {
					myHolder = (Holder) convertView.getTag();
				}
				if (position == this.currentID)
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.radio_clk));
				else
					myHolder.iv.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.radio));
				myHolder.title.setText(list.get(position).get("title")
						.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

		class Holder {
			ImageView iv;
			TextView title;
		}

		public void setCurrentID(int currentID) {
			this.currentID = currentID;
		}
	}
}
