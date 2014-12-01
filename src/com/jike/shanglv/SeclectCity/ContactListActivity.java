package com.jike.shanglv.SeclectCity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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
import com.jike.shanglv.SeclectCity.SideBarContact.OnTouchingLetterChangedListener;
import com.jike.shanglv.R;

public class ContactListActivity extends Activity {
	
	public static final int SELECTED_CONTACT_FINISH = 5;
	
	private ImageButton back_imgbtn, home_imgbtn;
	private ListView sortListView;
	private SideBarContact sideBar;
	private TextView dialog;
	private ContactListSortAdapter adapter;
	private ClearEditText mClearEditText;
	private Context context;
	
	public String name_select;
	public String number_select;
	public ArrayList<ContactListModel> Contact_List_Display = new ArrayList<ContactListModel>();
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Phone.SORT_KEY_PRIMARY };
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	private static final int PHONES_NUMBER_INDEX = 1;
	private static final int PHONES_PINYIN_INDEX = 2;
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<ContactListModel> SourceDateList;
	
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_select_activity);
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
		sideBar = (SideBarContact) findViewById(R.id.sidrbar);
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
				
				String numberString=((ContactListModel)adapter.getItem(position)).getnumber();
				setResult(0,getIntent().putExtra("pickedPhoneNum",numberString));
				finish();
			}
		});
		
		getPhoneContacts();
		SourceDateList = Contact_List_Display;
				//filledData(getResources().getStringArray(R.array.date));
		
		// 根据a-z进行排序源数据
		//Collections.sort(SourceDateList, pinyinComparator);
		sortCities();
		adapter = new ContactListSortAdapter(context, SourceDateList);
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
	 * 对列表进行排序
	 */
	private void sortCities() {
		Comparator<ContactListModel> comparator = new Comparator<ContactListModel>() {
			@Override
			public int compare(ContactListModel s1, ContactListModel s2) {
				return s1.pinyin.compareTo(s2.pinyin);
			}
		};
		Collections.sort(SourceDateList, comparator);
	}
	
	private void getPhoneContacts() {
		ContentResolver resolver = context.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, "sort_key asc");

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				String contactPinyin = phoneCursor
						.getString(PHONES_PINYIN_INDEX);
				ContactListModel c = new ContactListModel(contactName, contactPinyin,
						phoneNumber);
				c.setShortchar(contactPinyin.substring(0, 1).toUpperCase());
				Contact_List_Display.add(c);
			}
			phoneCursor.close();
		}
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<ContactListModel> filterDateList = new ArrayList<ContactListModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(ContactListModel Contact_List : SourceDateList){
				String name = Contact_List.getname();
				String num=Contact_List.getnumber();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(Contact_List);
				}
				if(num.indexOf(filterStr.toString()) != -1 ||num.startsWith(filterStr.toString())){
					filterDateList.add(Contact_List);
				}
			}
		}
		filterDateList=DateUtil.removeDuplicateWithOrder(filterDateList);
		// 根据a-z进行排序
		sortCities();
		adapter.updateListView(filterDateList);
	}

}
