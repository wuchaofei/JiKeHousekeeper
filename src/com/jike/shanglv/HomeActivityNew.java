package com.jike.shanglv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.MyGridView;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Models.AdShow;
import com.jike.shanglv.Models.HomeGridCell;

public class HomeActivityNew extends Activity {

	public static HomeActivityNew instance = null;
	private SharedPreferences sp;

	private MyGridView grid;
	private Context context;
	private ArrayList<HomeGridCell> cells;
	private MyAdapter adapter;
	// 默认有的栏目为：国内、国际、酒店、火车票、航班、话费、账户充值
	int[] defaultImg = { R.drawable.gnjp, R.drawable.gjjp, R.drawable.jdyd,
			R.drawable.hcp, R.drawable.hbdt, R.drawable.hfcz, R.drawable.zhcz };
	String[] defaultText = { "国内机票", "国际机票", "酒店预订", "火车票", "航班动态", "话费充值",
			"账户充值" };
	Class<?>[] defaultActivities = { ActivityInlandAirlineticket.class,
			ActivityInternationalAirlineticket.class, ActivityHotel.class,
			ActivityTrain.class, ActivityHangbandongtai.class,
			ActivityHuafeichongzhi.class, ActivityZhanghuchongzhi.class };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_home3);
			context = this;
			((MyApplication) getApplication()).addActivity(this);
			// sp = this.getSharedPreferences("mySPData", Context.MODE_PRIVATE);
			sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
			cells = new ArrayList<HomeGridCell>();
			grid = (MyGridView) findViewById(R.id.grid);
			grid.setFocusable(false);// 解决ScrollView起始位置不是最顶部的办法
			gridCells();

			initAd();
			// findViewById(R.id.ad_include).setVisibility(View.GONE);
			// findViewById(R.id.title_bg).setVisibility(View.VISIBLE);

			MyApp mApp = new MyApp(getApplicationContext());
			((ImageView) findViewById(R.id.menu_logo))
					.setBackgroundResource((Integer) mApp.getHm().get(
							PackageKeys.MENU_LOGO_DRAWABLE.getString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<HomeGridCell> getDefaultCellsData() {
		ArrayList<HomeGridCell> arrayList = new ArrayList<HomeGridCell>();
		for (int i = 0; i < defaultImg.length; i++) {
			try {
				Intent intent = new Intent(context, defaultActivities[i]);
				HomeGridCell hgc = new HomeGridCell(i, defaultImg[i],
						defaultText[i], intent);
				arrayList.add(hgc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return arrayList;
	}

	// 根据用户权限显示客户管理、分销管理等
	private void gridCells() {
		try {
			cells = getDefaultCellsData();
			if (sp.getString(SPkeys.utype.getString(), "0").equals("1")) {// B2B
				Intent intent3 = new Intent(context, Activity_Web_Frame.class);
				intent3.putExtra(Activity_Web_Frame.TITLE, "商学院");
				intent3.putExtra(Activity_Web_Frame.URL, getResources()
						.getString(R.string.shangxueyuan_url));
				Intent intent = new Intent(context, Activity_Web_Frame.class);
				intent.putExtra(Activity_Web_Frame.TITLE, "微平台");
				intent.putExtra(Activity_Web_Frame.URL, getResources()
						.getString(R.string.weipingtai_url));
				cells.add(new HomeGridCell(-1, R.drawable.sxy, "商学院", intent3));
				cells.add(new HomeGridCell(-1, R.drawable.wpt, "微平台", intent));
			}
			if (sp.getString(SPkeys.showCustomer.getString(), "0").equals("1")) {
				Intent intent2 = new Intent(context, ActivityClientManage.class);
				intent2.putExtra(
						ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING,
						ActivityClientManageSetGrad.CUSTOMER_DISPLAYNAME);
				cells.add(new HomeGridCell(-1, R.drawable.khgl, "客户管理", intent2));
			}
			if (sp.getString(SPkeys.showDealer.toString(), "0").equals("1")) {
				Intent intent1 = new Intent(context, ActivityClientManage.class);
				intent1.putExtra(
						ActivityClientManageSetGrad.DISPLAY_TYPENAME_STRING,
						ActivityClientManageSetGrad.DEALER_DISPLAYNAME);
				cells.add(new HomeGridCell(-1, R.drawable.fxgl, "分销管理", intent1));
			}

			adapter = new MyAdapter(context, cells);
			grid.setAdapter(adapter);
			grid.setOnItemClickListener(new ItemClickEvent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		gridCells();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public class MyAdapter extends BaseAdapter {

		private Context mContext;
		private ArrayList<HomeGridCell> arrayList;

		public MyAdapter(Context c, ArrayList<HomeGridCell> list) {
			mContext = c;
			arrayList = new ArrayList<HomeGridCell>();
			arrayList = list;
			// 如果菜单个数不是3的整数，则需要不足数的最后一行以空白填补
			int size = arrayList.size();
			if (size % 3 > 0) {
				for (int i = 0; i < 3 - size % 3; i++) {
					arrayList.add(new HomeGridCell(-1, R.drawable.blank75,
							"null", null));
				}
			}
		}

		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arrayList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(mContext, R.layout.item_home_grid, null);
			try {
				ImageView img = (ImageView) view.findViewById(R.id.img);
				TextView text = (TextView) view.findViewById(R.id.text);
				img.setImageResource(arrayList.get(position).getImg());
				if (arrayList.get(position).getName() == "null") {
					view.setFocusable(true);
				} else {
					text.setText(arrayList.get(position).getName());
				}
			} catch (Exception e) {
			}
			return view;
		}
	}

	class ItemClickEvent implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			try {
				// arg1是当前item的view，通过它可以获得该项中的各个组件。
				// arg2是当前item的ID。这个id根据你在适配器中的写法可以自己定义。
				// arg3是当前的item在listView中的相对位置！
				// Toast.makeText(context, "arg2:" + arg2 + " arg3:" + arg3,
				// Toast.LENGTH_SHORT).show();
				arg1.setPressed(false);
				arg1.setSelected(false);
				if (!sp.getBoolean(SPkeys.loginState.getString(), false)) {
					startActivity(new Intent(context, Activity_Login.class));
					return;
				}
				startActivity(cells.get(arg2).getIntent());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 以下代码为广告位的切换
	 */
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private LinearLayout dot_ll;
	private List<ImageView> imageViews; // 滑动的图片集合
	private List<View> dots; // 图片标题正文的那些点
	private ArrayList<AdShow> adsList;

	private String[] titles; // 图片标题
	private int[] imageResId; // 图片ID

	private TextView tv_title;
	private int currentItem = 0; // 当前图片的索引号

	private ScheduledExecutorService scheduledExecutorService;

	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};

	private void initAd() {
		try {
			adsList = new ArrayList<AdShow>();
//			adsList.add(new AdShow(
//					"http://b2b.51jp.cn/App_Themes/default/Images/Login_v2/banner_dzx.gif",
//					"http://b2b.51jp.cn/ComInfoDetail.aspx?id=198",
//					"舌尖上的美食，大闸蟹", "大闸蟹"));
			adsList.add(new AdShow(
					"http://b2b.51jp.cn/App_Themes/default/Images/Login_v2/banner0.png",
					"http://b2b.51jp.cn/Wallet/cash", "商旅钱包，可以省钱的钱包", "商旅钱包"));
			adsList.add(new AdShow(
					"http://b2b.51jp.cn/App_Themes/default/Images/Login_v2/banner1.jpg",
					"http://www.51jp.cn/Cooperation.asp", "商旅管家，一站式服务平台",
					"商旅管家"));

			imageResId = new int[] { R.drawable.ad_one,
					R.drawable.ad_three };// R.drawable.ad_two,
			titles = new String[imageResId.length];
			//titles[0] = "舌尖上的美食，大闸蟹";
			titles[0] = "商旅钱包，可以省钱的钱包";
			titles[1] = "商旅管家，一站式服务平台";

			imageViews = new ArrayList<ImageView>();

			// 初始化图片资源
			for (int i = 0; i < adsList.size(); i++) {
				ImageView imageView = new ImageView(this);
				imageView.setImageResource(imageResId[i]);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageViews.add(imageView);
			}
			dots = new ArrayList<View>();
			dot_ll = (LinearLayout) findViewById(R.id.dot_ll);
			for (int i = 0; i < imageResId.length; i++) {
				View view = null;
				if (currentItem == i)
					view = createView(true);
				else
					view = createView(false);
				dots.add(view);
				dot_ll.addView(view);
			}

			tv_title = (TextView) findViewById(R.id.tv_title);
			tv_title.setText(titles[0]);//

			viewPager = (ViewPager) findViewById(R.id.vp);
			viewPager.setAdapter(new ViewPagerAdapter());// 设置填充ViewPager页面的适配器
			// 设置一个监听器，当ViewPager中的页面改变时调用
			viewPager.setOnPageChangeListener(new MyPageChangeListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	protected View createView(Boolean isSelected) {
		View iv = new View(this);
		iv.setBackground(getResources().getDrawable(R.drawable.dot_pic));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(7, 7);
		lp.setMargins(5, 5, 5, 5);
		iv.setLayoutParams(lp);
		if (isSelected)
			iv.setSelected(true);
		return iv;
	}

	@Override
	protected void onStart() {
		super.onStart();

		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 2, 3,
				TimeUnit.SECONDS);
	}

	@Override
	protected void onStop() {
		// 当Activity不可见的时候停止切换
		super.onStop();
		scheduledExecutorService.shutdown();
	}

	/**
	 * 换行切换任务
	 */
	private class ScrollTask implements Runnable {
		public void run() {
			synchronized (viewPager) {
				try {
					System.out.println("currentItem: " + currentItem);
					currentItem = (currentItem + 1) % imageViews.size();
					handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		public void onPageSelected(int position) {
			currentItem = position;
			tv_title.setText(titles[position]);
			dots.get(oldPosition).setSelected(false);
			dots.get(position).setSelected(true);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 */
	private class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageResId.length;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			final int position = arg1;
			View view = imageViews.get(position);
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Toast.makeText(getApplicationContext(), position + "", 0)
					// .show();
					Intent intent = new Intent(context,
							Activity_Web_Frame.class);
					intent.putExtra(Activity_Web_Frame.TITLE,
							adsList.get(position).getTitle());
					intent.putExtra(Activity_Web_Frame.URL,
							adsList.get(position).getGoUrl());
					startActivity(intent);
				}
			});
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
}
