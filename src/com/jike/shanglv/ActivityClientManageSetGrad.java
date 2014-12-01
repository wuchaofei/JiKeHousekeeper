package com.jike.shanglv;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jike.shanglv.Common.CommonFunc;
import com.jike.shanglv.Common.CustomerAlertDialog;
import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.SPkeys;
import com.jike.shanglv.Models.DealerLevel;
import com.jike.shanglv.NetAndJson.HttpUtils;
import com.jike.shanglv.NetAndJson.JSONHelper;


public class ActivityClientManageSetGrad extends Activity {
	protected static final int DEALERLEVELMSGCODE = 0;
	protected static final int ADDLEVELMSGCODE = 1;
	protected static final int SETDEFAULTLEVELMSGCODE = 2;
	protected static final int DELETELEVELMSGCODE = 3;
	protected static final int MODIFYLEVELMSGCODE = 4;
	public static final String DISPLAY_TYPENAME_STRING = "DISPLAY_TYPENAME_STRING";
	public static final String CUSTOMER_DISPLAYNAME = "客户";
	public static final String DEALER_DISPLAYNAME = "分销商";

	private RelativeLayout set_default_rl, add_grad_rl;
	private LinearLayout loading_ll;
	private ImageView frame_ani_iv;
	private TextView query_status_tv, default_grad_tv, title_tv;
	private com.jike.shanglv.Common.QQListView listview;
	private Context context;
	private SharedPreferences sp;
	private String dealerlevallistReturnJson = "",
			addcustomerlevalReturnJson = "", setdefaultlevalReturnJson = "",
			displayName = "", levellistActionName = "",
			addlevelActionName = "", setdefaultlevalActionName = "";
	private ArrayList<DealerLevel> customerlever_List;// 客户级别、经商上级别列表使用同一个Model
	private ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_clientmange_set_grad);

			context = this;
			sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
			customerlever_List = new ArrayList<DealerLevel>();
			listview = (com.jike.shanglv.Common.QQListView) findViewById(R.id.listview);
			listview.setDelButtonClickListener(new com.jike.shanglv.Common.QQListView.DelButtonClickListener() {
				@Override
				public void clickHappend(final int position) {
					startDeleteGrad(customerlever_List.get(position)
							.getLevalID());
					// customerlever_List.remove(position);
					// adapter.refreshData((List<DealerLevel>)
					// customerlever_List);
				}
			});

			loading_ll = (LinearLayout) findViewById(R.id.loading_ll);
			frame_ani_iv = (ImageView) findViewById(R.id.frame_ani_iv);
			query_status_tv = (TextView) findViewById(R.id.query_status_tv);
			set_default_rl = (RelativeLayout) findViewById(R.id.set_default_rl);
			add_grad_rl = (RelativeLayout) findViewById(R.id.add_grad_rl);
			default_grad_tv = (TextView) findViewById(R.id.default_grad_tv);
			title_tv = (TextView) findViewById(R.id.title_tv);
			((ImageButton) findViewById(R.id.back_imgbtn))
					.setOnClickListener(clickListener);
			set_default_rl.setOnClickListener(clickListener);
			add_grad_rl.setOnClickListener(clickListener);
			Bundle bundle = new Bundle();
			bundle = getIntent().getExtras();
			if (bundle != null) {
				displayName = bundle.containsKey(DISPLAY_TYPENAME_STRING) ? bundle
						.getString(DISPLAY_TYPENAME_STRING) : "";
				title_tv.setText(displayName + "级别设置");
			}
			if (displayName.equals(CUSTOMER_DISPLAYNAME)) {
				levellistActionName = "customerlevallist";
				addlevelActionName = "addcustomerleval";
				setdefaultlevalActionName = "setdefaultcustomerlevel";
			} else if (displayName.equals(DEALER_DISPLAYNAME)) {
				levellistActionName = "dealerlevallist";
				addlevelActionName = "adddealerleval";
				setdefaultlevalActionName = "setdefaultlevel";
			}
			startQueryGrad();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			try {
				switch (arg0.getId()) {
				case R.id.back_imgbtn:
					finish();
					break;
				case R.id.set_default_rl:
					iniPopupWindow(0, initLevelData());
					pwMyPopWindow.showAtLocation(set_default_rl,
							Gravity.BOTTOM, 0, 0);
					break;
				case R.id.add_grad_rl:
					final EditText inputServer = new EditText(context);
					inputServer.setFocusable(true);

					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle("新增" + displayName + "级别")
							.setView(inputServer).setNegativeButton("取消", null);
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String inputName = inputServer.getText()
											.toString().trim();
									if (inputName.length() > 0)
										startAddGrad(inputName);
								}
							});
					builder.show();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		try {
			super.onWindowFocusChanged(hasFocus);
			frame_ani_iv.setBackgroundResource(R.anim.frame_rotate_ani);
			AnimationDrawable anim = (AnimationDrawable) frame_ani_iv
					.getBackground();
			anim.setOneShot(false);
			anim.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startQueryGrad() {
		if (HttpUtils.showNetCannotUse(context)) {
			loading_ll.setVisibility(View.GONE);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\"}";
					String param = "action="
							+ levellistActionName
							+ "&str="
							+ str
							+ "&userkey="
							+ ma.getHm().get(PackageKeys.USERKEY.getString())
									.toString()
							+ "&sitekey="
							+ MyApp.sitekey
							+ "&sign="
							+ CommonFunc.MD5(ma.getHm()
									.get(PackageKeys.USERKEY.getString())
									.toString()
									+ levellistActionName + str);
					dealerlevallistReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = DEALERLEVELMSGCODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void startAddGrad(final String newName) {
		if (HttpUtils.showNetCannotUse(context)) {
			loading_ll.setVisibility(View.GONE);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "";
					str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"lname\":\"" + newName + "\"}";
					String param = "";
					try {
						param = "action="
								+ addlevelActionName
								+ "&str="
								+ URLEncoder.encode(str, "utf-8")
								+ "&userkey="
								+ ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
								+ "&sitekey="
								+ MyApp.sitekey
								+ "&sign="
								+ CommonFunc.MD5(ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
										+ addlevelActionName + str);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					addcustomerlevalReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = ADDLEVELMSGCODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void startSetDefaultGrad(final String id) {
		if (HttpUtils.showNetCannotUse(context)) {
			loading_ll.setVisibility(View.GONE);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "";
					str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"lID\":\"" + id + "\"}";
					String param = "";
					try {
						param = "action="
								+ setdefaultlevalActionName
								+ "&str="
								+ URLEncoder.encode(str, "utf-8")
								+ "&userkey="
								+ ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
								+ "&sitekey="
								+ MyApp.sitekey
								+ "&sign="
								+ CommonFunc.MD5(ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
										+ setdefaultlevalActionName + str);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					setdefaultlevalReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = SETDEFAULTLEVELMSGCODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void startDeleteGrad(final String id) {
		if (HttpUtils.showNetCannotUse(context)) {
			loading_ll.setVisibility(View.GONE);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "";
					str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"lID\":\"" + id + "\"}";
					String param = "";
					try {
						param = "action=deletelevel&str="
								+ URLEncoder.encode(str, "utf-8")
								+ "&userkey="
								+ ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
								+ "&sitekey="
								+ MyApp.sitekey
								+ "&sign="
								+ CommonFunc.MD5(ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
										+ "deletelevel" + str);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					setdefaultlevalReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = DELETELEVELMSGCODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void startModifyGrad(final String id, final String name) {
		if (HttpUtils.showNetCannotUse(context)) {
			loading_ll.setVisibility(View.GONE);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApp ma = new MyApp(context);
					String str = "";
					str = "{\"userID\":\""
							+ sp.getString(SPkeys.userid.getString(), "")
							+ "\",\"lname\":\"" + name + "\",\"lID\":\"" + id
							+ "\"}";
					String param = "";
					try {
						param = "action=modifylevel&str="
								+ URLEncoder.encode(str, "utf-8")
								+ "&userkey="
								+ ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
								+ "&sitekey="
								+ MyApp.sitekey
								+ "&sign="
								+ CommonFunc.MD5(ma.getHm()
										.get(PackageKeys.USERKEY.getString())
										.toString()
										+ "modifylevel" + str);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					setdefaultlevalReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Message msg = new Message();
					msg.what = MODIFYLEVELMSGCODE;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			JSONTokener jsonParser;
			switch (msg.what) {
			case MODIFYLEVELMSGCODE:
			case DELETELEVELMSGCODE:
			case SETDEFAULTLEVELMSGCODE:
				jsonParser = new JSONTokener(setdefaultlevalReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					String msgString = jsonObject.getJSONObject("d").getString(
							"msg");
					if (!state.equals("0000")) {
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(msgString);
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
					} else if (state.equals("0000"))
						startQueryGrad();// 修改删除后刷新数据
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case ADDLEVELMSGCODE:
				jsonParser = new JSONTokener(addcustomerlevalReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");
					String msgString = jsonObject.getJSONObject("d").getString(
							"msg");
					if (state.equals("0000")) {
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle("添加用户级别信息成功");
						cad.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cad.dismiss();
							}
						});
						startQueryGrad();
					} else {
						final CustomerAlertDialog cad = new CustomerAlertDialog(
								context, true);
						cad.setTitle(msgString);
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
				break;
			case DEALERLEVELMSGCODE:
				jsonParser = new JSONTokener(dealerlevallistReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						JSONArray cArray = jsonObject.getJSONArray("d");
						customerlever_List.clear();
						for (int i = 0; i < cArray.length(); i++) {
							DealerLevel cUser = JSONHelper.parseObject(
									cArray.getJSONObject(i), DealerLevel.class);
							customerlever_List.add(cUser);
							if (cUser.getIsDefault().equals("1")) {
								default_grad_tv.setText(cUser.getLevalName());
							}
						}
						adapter = new ListAdapter(context, customerlever_List);
						listview.setAdapter(adapter);
						if (customerlever_List.size() == 0) {
							query_status_tv.setText("未查询到客户级别信息");
							frame_ani_iv.setVisibility(View.INVISIBLE);
							break;
						}
						loading_ll.setVisibility(View.GONE);
						listview.setVisibility(View.VISIBLE);
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								DealerLevel cu = customerlever_List
										.get(position);
							}
						});
					} else {
						query_status_tv.setText("查询级别信息失败");
						frame_ani_iv.setVisibility(View.INVISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<DealerLevel> str;

		public ListAdapter(Context context, List<DealerLevel> list1) {
			this.inflater = LayoutInflater.from(context);
			this.str = list1;
		}

		public void refreshData(List<DealerLevel> data) {
			this.str = data;
			notifyDataSetChanged();
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
					convertView = inflater.inflate(R.layout.item_dealerlevel,
							null);
				}
				TextView dealerLevelName_tv = (TextView) convertView
						.findViewById(R.id.dealerLevelName_tv);
				TextView modify_tv = (TextView) convertView
						.findViewById(R.id.modify_tv);

				if (str.get(position).getLevalName() != null
						&& str.get(position).getLevalName().length() > 0)
					dealerLevelName_tv
							.setText(str.get(position).getLevalName());
				modify_tv.setTag(position + "");
				modify_tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final int index = Integer.valueOf(arg0.getTag()
								.toString());
						final EditText inputServer = new EditText(context);
						inputServer.setFocusable(true);
						inputServer.setText(customerlever_List.get(index)
								.getLevalName());
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setTitle("修改" + displayName + "级别")
								.setView(inputServer)
								.setNegativeButton("取消", null);
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										String inputName = inputServer
												.getText().toString();
										startModifyGrad(
												customerlever_List.get(index)
														.getLevalID(),
												inputName);
									}
								});
						// builder.setNeutralButton("删除", new
						// DialogInterface.OnClickListener() {
						// public void onClick(DialogInterface arg0, int arg1) {
						// startDeleteGrad(customerlever_List.get(index).getLevalID());
						// }
						// });
						builder.show();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private PopupWindow pwMyPopWindow;// popupwindow
	private ListView lvPopupList;
	private int currentID_XJ = 0;

	private void iniPopupWindow(final int xjOrJg,
			final List<Map<String, Object>> list1) {
		final LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popupwindow_list_select, null);
		lvPopupList = (ListView) layout.findViewById(R.id.lv_popup_list);
		pwMyPopWindow = new PopupWindow(layout);
		pwMyPopWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

		MyListAdapter adapter = new MyListAdapter(context, list1);
		adapter.setCurrentID(currentID_XJ);
		lvPopupList.setAdapter(adapter);
		lvPopupList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					default_grad_tv
							.setText(list1.get(position).get("title") != null ? list1
									.get(position).get("title").toString()
									: "");
					currentID_XJ = position;
					startSetDefaultGrad(customerlever_List.get(position)
							.getLevalID());
					pwMyPopWindow.dismiss();
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

	private ArrayList<Map<String, Object>> initLevelData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < customerlever_List.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", customerlever_List.get(i).getLevalName());
			list.add(map);
		}
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
				myHolder.title
						.setText(list.get(position).get("title") != null ? list
								.get(position).get("title").toString() : "");
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
