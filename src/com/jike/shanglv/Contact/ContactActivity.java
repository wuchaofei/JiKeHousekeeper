package com.jike.shanglv.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.jike.shanglv.MainActivity;
import com.jike.shanglv.MyApplication;
import com.jike.shanglv.Common.DateUtil;
import com.jike.shanglv.R;

public class ContactActivity extends Activity {

	private ImageButton back_imgbtn, home_imgbtn;
	private ListView sortListView;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private Context context;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<ContactModel> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_select_activity);
			context = this;
			initViews();
			((MyApplication) getApplication()).addActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		// //设置右侧触摸监听
		// sideBar.setOnTouchingLetterChangedListener(new
		// OnTouchingLetterChangedListener() {
		//
		// @Override
		// public void onTouchingLetterChanged(String s) {
		// //该字母首次出现的位置
		// int position = adapter.getPositionForSection(s.charAt(0));
		// if(position != -1){
		// sortListView.setSelection(position);
		// }
		// }
		// });

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象

				// Toast.makeText(getApplication(),
				// ((ContactModel)adapter.getItem(position)).getCityName(),
				// Toast.LENGTH_SHORT).show();

				// 返回城市联系人及电话
				setResult(
						0,
						getIntent().putExtra(
								"pickedContact",
								((ContactModel) adapter.getItem(position))
										.getName()
										+ "#"
										+ ((ContactModel) adapter
												.getItem(position))
												.getPhoneNumber()));
				finish();
			}
		});

		SourceDateList = getAllContacts();
		// filledData(getResources().getStringArray(R.array.date));

		// 根据a-z进行排序源数据
		// Collections.sort(SourceDateList, pinyinComparator);
		// sortCities();
		adapter = new SortAdapter(context, SourceDateList);
		sortListView.setAdapter(adapter);
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
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
	 * 排序
	 */
	private void sortCities() {
		Comparator<ContactModel> comparator = new Comparator<ContactModel>() {
			@Override
			public int compare(ContactModel s1, ContactModel s2) {
				if (s1.nameSort.compareTo(s2.nameSort) != 0) {
					return s1.nameSort.compareTo(s2.nameSort);
				} else {
					return s1.name.compareTo(s2.name);
				}
			}
		};
		Collections.sort(SourceDateList, comparator);
	}

	/*
	 * 读取联系人的信息
	 */
	public ArrayList<ContactModel> getAllContacts() {
		ArrayList<ContactModel> names = new ArrayList<ContactModel>();
		try {
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			int contactIdIndex = 0;
			int nameIndex = 0;

			if (cursor.getCount() > 0) {
				contactIdIndex = cursor.getColumnIndex(BaseColumns._ID);
				nameIndex = cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			}
			while (cursor.moveToNext()) {
				ContactModel acm = new ContactModel();
				String contactId = cursor.getString(contactIdIndex);
				String name = cursor.getString(nameIndex);
				acm.setContactId(contactId);
				acm.setName(name);

				/*
				 * 查找该联系人的phone信息
				 */
				Cursor phones = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
								+ contactId, null, null);
				int phoneIndex = 0;
				if (phones.getCount() > 0) {
					phoneIndex = phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				}
				while (phones.moveToNext()) {
					String phoneNumber = phones.getString(phoneIndex);
					acm.setPhoneNumber(phoneNumber);
				}
				names.add(acm);
				phones.close();
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return names;
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<ContactModel> filledData(String[] date) {
		List<ContactModel> mSortList = new ArrayList<ContactModel>();

		for (int i = 0; i < date.length; i++) {
			ContactModel ContactModel = new ContactModel();
			ContactModel.setName(date[i]);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				ContactModel.setNameSort(sortString.toUpperCase());
			} else {
				ContactModel.setNameSort("#");
			}
			mSortList.add(ContactModel);
		}
		return mSortList;
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<ContactModel> filterDateList = new ArrayList<ContactModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (ContactModel ContactModel : SourceDateList) {
				String name = ContactModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(ContactModel);
				}

				if (name.indexOf(filterStr.toString()) != -1
						|| ContactModel.getName().startsWith(
								filterStr.toString())) {
					filterDateList.add(ContactModel);
				}
				if (name.indexOf(filterStr.toString()) != -1
						|| ContactModel.getPhoneNumber().startsWith(
								filterStr.toString())) {
					filterDateList.add(ContactModel);
				}
				if (name.indexOf(filterStr.toString()) != -1
						|| ContactModel.getNameSort().startsWith(
								filterStr.toString())) {
					filterDateList.add(ContactModel);
				}
			}
		}
		filterDateList = DateUtil.removeDuplicateWithOrder(filterDateList);
		// 根据a-z进行排序
		sortCities();
		adapter.updateListView(filterDateList);
	}
}
