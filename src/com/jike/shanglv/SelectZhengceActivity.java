package com.jike.shanglv;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomProgressDialog;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Models.PolicyList;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;
import com.jike.shanglv.slideexpandlistview.ActionSlideExpandableListView;


public class SelectZhengceActivity extends Activity {
	public static final String PLICYLISTSTR = "policylist_str";
	public static final String SELECTEDPOLICY = "SELECTEDPLICY";
	protected static final int SELECTED_POLICY_CODE = 11;

	private ImageButton back_imgbtn;
	private com.jike.shanglv.slideexpandlistview.ActionSlideExpandableListView policy_lv;
	private Context context;
	private Boolean isSpecialPolicy = false;
	private String str = "", policyReturnJson = "";
	private ArrayList<PolicyList> basePolicyLists = null;
	private ArrayList<PolicyList> specialPolicyLists = null;
	private CustomProgressDialog progressdialog;

	private ImageView scrollbar_iv;
	private TextView putongzhengce_tv, gaofanzhengce_tv,
			special_policy_status_tv;
	private float screenWidth;// 手机屏幕宽度
	private int bmpW;// 动画图片宽度
	private int offset = 0;// 动画图片偏移量
	private Animation animation;
	private int one;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.select_zhengce_activity);
			context = this;
			initViews();
			((MyApplication) getApplication()).addActivity(this);
			startQueryPolicy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initViews() {
		basePolicyLists = new ArrayList<PolicyList>();
		specialPolicyLists = new ArrayList<PolicyList>();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(PLICYLISTSTR)) {
				str = bundle.getString(PLICYLISTSTR);
			}
		}
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels; // 获取分辨率宽度
		scrollbar_iv = (ImageView) findViewById(R.id.scrollbar_iv);
		bmpW = BitmapFactory
				.decodeResource(getResources(), R.drawable.typeline).getWidth();// 获取图片宽度
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		offset = (int) ((screenWidth / 2 - bmpW) / 2);// 计算偏移量
		one = (int) ((screenWidth / 2) + 50);
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, 0);
		scrollbar_iv.setImageMatrix(matrix);// 设置动画初始位置
		putongzhengce_tv = (TextView) findViewById(R.id.singleline_tv);
		gaofanzhengce_tv = (TextView) findViewById(R.id.doubleline_tv);
		special_policy_status_tv = (TextView) findViewById(R.id.special_policy_status_tv);
		putongzhengce_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					putongzhengce_tv.setTextColor(context.getResources()
							.getColor(R.color.blue_title_color));
					gaofanzhengce_tv.setTextColor(context.getResources()
							.getColor(R.color.black_txt_color));

					animation = new TranslateAnimation(one, 0, 0, 0);
					animation.setFillAfter(true);// True:图片停在动画结束位置
					animation.setDuration(300);
					scrollbar_iv.startAnimation(animation);
					isSpecialPolicy = false;
					blindListData(basePolicyLists);
					special_policy_status_tv.setVisibility(View.GONE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		gaofanzhengce_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					putongzhengce_tv.setTextColor(context.getResources()
							.getColor(R.color.black_txt_color));
					gaofanzhengce_tv.setTextColor(context.getResources()
							.getColor(R.color.blue_title_color));

					animation = new TranslateAnimation(offset, one, 0, 0);
					animation.setFillAfter(true);// True:图片停在动画结束位置
					animation.setDuration(300);
					scrollbar_iv.startAnimation(animation);
					isSpecialPolicy = true;
					blindListData(specialPolicyLists);
					if (specialPolicyLists.size() > 0) {
						special_policy_status_tv.setVisibility(View.VISIBLE);
					} else {
						special_policy_status_tv.setVisibility(View.VISIBLE);
						special_policy_status_tv.setText("该航班没有特殊高返政策");
						special_policy_status_tv.setTextSize(18);
						special_policy_status_tv.setGravity(Gravity.CENTER);
						special_policy_status_tv.setTextColor(getResources()
								.getColor(R.color.black6));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		policy_lv = (com.jike.shanglv.slideexpandlistview.ActionSlideExpandableListView) findViewById(R.id.policy_lv);
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		back_imgbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void startQueryPolicy() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String param = "action=policylist&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ "policylist" + str);
					policyReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在查询政策信息，请稍候...");
		progressdialog.setCancelable(true);
		progressdialog.show();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				progressdialog.dismiss();
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(policyReturnJson);
				if (policyReturnJson.length() == 0) {
					final CustomerAlertDialog cad = new CustomerAlertDialog(
							context, true);
					cad.setTitle("获取政策信息失败");
					cad.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							cad.dismiss();
						}
					});
				}
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					if (state.equals("0000")) {
						JSONArray jArray = jsonObject.getJSONArray("d");
						for (int i = 0; i < jArray.length(); i++) {
							PolicyList pl = JSONHelper.parseObject(
									jArray.getJSONObject(i), PolicyList.class);
							if (pl.getIsspepolicy().equals("0"))
								basePolicyLists.add(pl);
							else
								specialPolicyLists.add(pl);
						}
						blindListData(basePolicyLists);
					} else {
						String message = "";
						try {
							message = jsonObject.getJSONObject("d").getString(
									"msg");
						} catch (Exception e) {
							message = jsonObject.getString("msg");
						}
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(message);
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private void blindListData(ArrayList<PolicyList> pList) {
		policy_lv.setAdapter(new ListAdapter(context, pList));
		policy_lv.setItemActionListener(
				new ActionSlideExpandableListView.OnActionClickListener() {
					@Override
					public void onClick(View listView, View buttonview,
							int position) {
						String policy = "";
						if (!isSpecialPolicy
								&& basePolicyLists.size() > position) {
							policy = JSONHelper.toJSON(basePolicyLists
									.get(position));
						} else if (isSpecialPolicy
								&& specialPolicyLists.size() > position) {
							policy = JSONHelper.toJSON(specialPolicyLists
									.get(position));
						}
						setResult(SELECTED_POLICY_CODE,
								getIntent().putExtra(SELECTEDPOLICY, policy));
						finish();
					}
				}, R.id.select_btn);
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<PolicyList> str;

		public ListAdapter(Context context, List<PolicyList> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
		}

		@Override
		public int getCount() {
			return str.size();
		}

		@Override
		public Object getItem(int position) {
			return str.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				if (convertView == null) {
					convertView = inflater.inflate(
							R.layout.item_policy_expandable, null);
				}
				TextView policy_name_tv = (TextView) convertView
						.findViewById(R.id.policy_name_tv);
				TextView fandian_tv = (TextView) convertView
						.findViewById(R.id.fandian_tv);
				TextView time_section_tv = (TextView) convertView
						.findViewById(R.id.time_section_tv);
				TextView price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				TextView shuoming_tv = (TextView) convertView
						.findViewById(R.id.shuoming_tv);

				policy_name_tv.setText("政策" + (position + 1));
				fandian_tv.setText(str.get(position).getUserrate() + "%");
				time_section_tv.setText(str.get(position).getWtime()
						.replace(",", "\r\n").replace(" ", ""));
				price_tv.setText("￥" + str.get(position).getSale());
				shuoming_tv.setText(str.get(position).getRemark());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
}
