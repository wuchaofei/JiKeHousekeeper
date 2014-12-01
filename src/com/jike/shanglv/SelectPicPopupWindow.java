package com.jike.shanglv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectPicPopupWindow extends PopupWindow {
	private String curentString = "";
	private int currentID = -1;

	/*
	 * list数据源 currentID当前选中的项目
	 */
	public SelectPicPopupWindow(Activity context,
			List<Map<String, Object>> list, int currentID,
			OnClickListener itemsOnClick) {
		super(context);
		try {
			this.currentID = currentID;
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.alert_bottom_dialog, null);
			ListView baoxian_listview = (ListView) view
					.findViewById(R.id.baoxian_listview);

			final MyListAdapter adapter = new MyListAdapter(context, list);
			adapter.setCurrentID(currentID);
			baoxian_listview.setAdapter(adapter);
			baoxian_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// if(position!=currentID){
					// adapter.setCurrentID(position);
					// adapter.notifyDataSetChanged();
					// }
					// currentID=position;

					// switch (currentID) {
					// case 0:
					// curentBaoxian=No_Baoxian;
					// break;
					// case 1:
					// curentBaoxian=Baoxian_Five;
					// break;
					// case 2:
					// curentBaoxian=Baoxian_Ten;
					// break;
					// default:
					// break;
					// }
					// setResult(0,
					// getIntent().putExtra(BAOXIAN_BUNDSTRING,curentBaoxian));
					// finish();
					// }
				}
			});

			this.setContentView(view);
			// 设置SelectPicPopupWindow弹出窗体的宽
			this.setWidth(LayoutParams.FILL_PARENT);
			// 设置SelectPicPopupWindow弹出窗体的高
			this.setHeight(LayoutParams.WRAP_CONTENT);
			// 设置SelectPicPopupWindow弹出窗体可点击
			this.setFocusable(true);
			// 设置SelectPicPopupWindow弹出窗体动画效果
			this.setAnimationStyle(R.style.AnimBottomPopup);
			// 实例化一个ColorDrawable颜色为半透明
			ColorDrawable dw = new ColorDrawable(0x10000000);
			// 设置SelectPicPopupWindow弹出窗体的背景
			this.setBackgroundDrawable(dw);
			// this.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyListAdapter extends BaseAdapter {

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
				// myHolder.iv.setBackgroundResource((Integer)
				// list.get(position).get("img"));
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
