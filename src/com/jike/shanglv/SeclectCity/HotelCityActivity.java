package com.jike.shanglv.SeclectCity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jike.shanglv.MainActivity;
import com.jike.shanglv.MyApplication;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.SeclectCity.SideBar.OnTouchingLetterChangedListener;
import com.jike.shanglv.R;

public class HotelCityActivity extends Activity {
	
	private ImageButton back_imgbtn, home_imgbtn;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private HotelSortAdapter adapter;
	private ClearEditText mClearEditText;
	private Context context;
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<HotelCityModel> SourceDateList;
	
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_select_activity);
		context=this;
		initViews();
		((MyApplication)getApplication()).addActivity(this);
	}

	private void initViews() {
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		home_imgbtn = (ImageButton) findViewById(R.id.home_imgbtn);
		back_imgbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		home_imgbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, MainActivity.class));
			}
		});
		
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
			}
		});
		
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象

				//Toast.makeText(getApplication(), ((ContactModel)adapter.getItem(position)).getCityName(), Toast.LENGTH_SHORT).show();
				
				//返回城市名称及三字码
				setResult(
						0,
						getIntent().putExtra("pickedCity",
								((HotelCityModel)adapter.getItem(position)).getCityname()));
				finish();
			}
		});
		
		SourceDateList = getHotelCityModel() ;
				//filledData(getResources().getStringArray(R.array.date));
		
		// 根据a-z进行排序源数据
		//Collections.sort(SourceDateList, pinyinComparator);
		sortCities();
		adapter = new HotelSortAdapter(context, SourceDateList);
		sortListView.setAdapter(adapter);
		
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	/*
	 * 对城市列表进行排序
	 */
	private void sortCities() {
		Comparator<HotelCityModel> comparator = new Comparator<HotelCityModel>() {
			@Override
			public int compare(HotelCityModel s1, HotelCityModel s2) {
				if (s1.ishot.compareTo(s2.ishot)!=0) {
					return s2.ishot.compareTo(s1.ishot);
				}
			    else if (s1.suoxie.compareTo(s2.suoxie) != 0) {
					return s1.suoxie.compareTo(s2.suoxie);
				} else {
					return s1.cityname.compareTo(s2.cityname);
				}
			}
		};
		Collections.sort(SourceDateList, comparator);
	}
	
	private ArrayList<HotelCityModel> getHotelCityModel() {
		ArrayList<HotelCityModel> names = new ArrayList<HotelCityModel>();
		try {
			String jsonStr = getJson("hotelcity");
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			String cities = jsonObject.getString("d");

			JSONArray array = new JSONArray(cities);
			int len = array.length();
			Map<String, String> map;
			List<Map<String, String>> data;
			for (int i = 0; i < len; i++) {
				JSONObject object = array.getJSONObject(i);
				HotelCityModel acm = new HotelCityModel();
				acm.id = object.getString("id");
				acm.cityname = object.getString("cityname");
				acm.hotelnum = object.getString("hotelnum");
				acm.abcd = object.getString("abcd");
				acm.pinyin = object.getString("pinyin");
				acm.ishot = object.getString("ishot");
				acm.suoxie = object.getString("suoxie");
				
				if (acm.ishot.equals("1")) {
					acm.abcd="热门";
				}
				
				names.add(acm);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return names;
	}

	/**
	 * 读取本地文件中JSON字符串
	 * 
	 * @param fileName
	 * @return
	 */
	private String getJson(String fileName) {

		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					getAssets().open(fileName), "GB2312"));
			String line;
			while ((line = bf.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<HotelCityModel> filledData(String [] date){
		List<HotelCityModel> mSortList = new ArrayList<HotelCityModel>();
		
		for(int i=0; i<date.length; i++){
			HotelCityModel HotelCityModel = new HotelCityModel();
			HotelCityModel.setCityname(date[i]);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
//			String ishot=
			
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				HotelCityModel.setNameSort(sortString.toUpperCase());
			}else{
				HotelCityModel.setNameSort("#");
			}
			mSortList.add(HotelCityModel);
		}
		return mSortList;
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<HotelCityModel> filterDateList = new ArrayList<HotelCityModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(HotelCityModel HotelCityModel : SourceDateList){
				String name = HotelCityModel.getCityname();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(HotelCityModel);
				}
				
				if(name.indexOf(filterStr.toString()) != -1 || HotelCityModel.getAbcd().startsWith(filterStr.toString())){
					filterDateList.add(HotelCityModel);
				}
				if(name.indexOf(filterStr.toString()) != -1 || HotelCityModel.getCityname().startsWith(filterStr.toString())){
					filterDateList.add(HotelCityModel);
				}
				if(name.indexOf(filterStr.toString()) != -1 || HotelCityModel.getPinyin().startsWith(filterStr.toString())){
					filterDateList.add(HotelCityModel);
				}
				if(name.indexOf(filterStr.toString()) != -1 || HotelCityModel.getNameSort().startsWith(filterStr.toString())){
					filterDateList.add(HotelCityModel);
				}
				if(name.indexOf(filterStr.toString()) != -1 || HotelCityModel.getSuoxie().startsWith(filterStr.toString())){
					filterDateList.add(HotelCityModel);
				}
			}
		}
		filterDateList=DateUtil.removeDuplicateWithOrder(filterDateList);
		// 根据a-z进行排序
		sortCities();
		adapter.updateListView(filterDateList);
	}
}
