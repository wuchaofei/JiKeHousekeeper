package com.jike.shanglv.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jike.shanglv.Models.ProvinceCity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.jike.shanglv.R;

public class SelectProvinceCityAlertDialog
{
	/** 微软雅黑默认 */
	private static Typeface typefacenormal = Typeface.create("微软雅黑", Typeface.NORMAL);
	/** 微软雅黑粗体 */
	private static Typeface typefacebold = Typeface.create("微软雅黑", Typeface.BOLD);
	private AlertDialog add_addressdlg;
	private String selcityid = "";
	private ArrayList<ProvinceCity> province_citys;
	private ProvinceCity nowProvinceCity;
	private Spinner add_province_box;
	private ArrayList<ProvinceCity> provinceList = new ArrayList<ProvinceCity>();
	private ArrayAdapter<String> provinceAdapter;
	private Spinner add_city_box;
	private ArrayList<ProvinceCity> cityList = new ArrayList<ProvinceCity>();
	private ArrayAdapter<String> cityAdapter;
	private Button add_address_close;
	private LinearLayout add_address_cancle;
	private Context context;
	
	public SelectProvinceCityAlertDialog(Context context){
		this.context=context;
		province_citys =getProvinceCity();
		CreateAlertCity();
	}

	private void CreateAlertCity()
	{
		if (add_addressdlg != null)
		{
			if (add_addressdlg.isShowing())
			{
				add_addressdlg.dismiss();
			}
			add_addressdlg = null;
		}
		View add_addressdlgView;
		LayoutInflater factory = LayoutInflater.from(context);
		add_addressdlg = new AlertDialog.Builder(context).setCancelable(false).create();
		add_addressdlgView = factory.inflate(R.layout.alertdialog_sel_city, null);
		add_addressdlg.setView(add_addressdlgView);
		add_addressdlg.show();
		Window window = add_addressdlg.getWindow();
		add_addressdlg.setContentView(R.layout.alertdialog_sel_city);
		TextView title = (TextView) window.findViewById(R.id.AlertDialogTitle);
		title.setTypeface(typefacenormal);
		title.setText("请选择客户所在城市");

		TextView add_province_txt = (TextView) window.findViewById(R.id.add_province_txt);
		add_province_box = (Spinner) window.findViewById(R.id.add_province_box);
		add_province_txt.setTypeface(typefacenormal);
		GetProvinceList();

		if (nowProvinceCity != null)
		{
			BindProvince(nowProvinceCity.getCityID());
		} else
		{
			BindProvince(null);
		}

		add_province_box.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				String name = (String) add_province_box.getSelectedItem();
				ProvinceCity model = GetProvinceByName(name);
				GetCityList(model.getCityID());
				if (nowProvinceCity != null)
				{
					BindCity(nowProvinceCity.getCityName());
				} else
				{
					BindCity(null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
			}
		});

		TextView add_city_txt = (TextView) window.findViewById(R.id.add_city_txt);
		add_city_box = (Spinner) window.findViewById(R.id.add_city_box);
		add_city_txt.setTypeface(typefacenormal);

		add_city_box.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				String name = (String) add_city_box.getSelectedItem();
				ProvinceCity model = GetCityByName(name);
				selcityid=model.getCityID();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
			}
		});

		add_address_close = (Button) window.findViewById(R.id.btn_close);

		TextView ok_btn_txt = (TextView) window.findViewById(R.id.ok_btn_txt);
		ok_btn_txt.setTypeface(typefacebold);

		add_address_cancle = (LinearLayout) window.findViewById(R.id.cancle_btn_layout);
		TextView cancle_btn_txt = (TextView) window.findViewById(R.id.cancle_btn_txt);
		cancle_btn_txt.setTypeface(typefacebold);

		add_address_close.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				add_addressdlg.dismiss();
			}
		});

		add_address_cancle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				add_addressdlg.dismiss();
			}
		});

		ok_btn_txt.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				GetNowWeatherArea(selcityid);
				add_addressdlg.dismiss();
			}
		});
	}

	private ArrayList<ProvinceCity> getProvinceCity() {
		ArrayList<ProvinceCity> names = new ArrayList<ProvinceCity>();
		try {
			String jsonStr = getJson("glocity");
			JSONArray array = new JSONArray(jsonStr);
			int len = array.length();
			Map<String, String> map;
			List<Map<String, String>> data;
			for (int i = 0; i < len; i++) {
				JSONObject object = array.getJSONObject(i);
				ProvinceCity acm = new ProvinceCity();
				acm.setCityName(object.getString("cityName"));
				acm.setCityID(object.getString("cityID"));
				acm.setParentCity(object.getString("parentCity"));
				names.add(acm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return names;
	}
	
	/**
	 * 读取本地文件中JSON字符串
	 */
	private String getJson(String fileName) {

		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					context.getAssets().open(fileName), "GB2312"));
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
	 * @param ctiyid
	 */
	private void GetNowWeatherArea(String getCityID)
	{
		for (ProvinceCity item : province_citys)
		{
			if (item.getCityID().equals(getCityID))
			{
				nowProvinceCity = item;
				break;
			}
		}
		if (nowProvinceCity == null)
		{
			nowProvinceCity = province_citys.get(0);
		}

		Message msg = new Message();
		msg.what = 0;
		msg.obj = nowProvinceCity.getCityName()	+ "-" 
				+ GetCityByID(nowProvinceCity.getParentCity()).getCityName() ;//GetCityByID(nowProvinceCity.getParentCity()).getCityName()
		messageHandler.sendMessage(msg);
	}

	/**
	 * 根据名称获取一级城市
	 * 
	 * @param name
	 * @return
	 */
	private ProvinceCity GetProvinceByName(String name)
	{
		ProvinceCity model = null;
		for (ProvinceCity item : provinceList)
		{
			if (item.getCityName().equals(name))
			{
				model = item;
				break;
			}
		}
		return model;
	}

	/**
	 * 根据名称获取城市
	 * 
	 * @param name
	 * @return
	 */
	private ProvinceCity GetCityByName(String name)
	{
		ProvinceCity model = null;
		for (ProvinceCity item : cityList)
		{
			if (item.getCityName().equals(name))
			{
				model = item;
				break;
			}
		}
		return model;
	}
	
	/**
	 * 根据ID获取城市
	 * 
	 * @param name
	 * @return
	 */
	private ProvinceCity GetCityByID(String id)
	{
		ProvinceCity model = null;
		for (ProvinceCity item : province_citys)
		{
			if (item.getCityID().equals(id))
			{
				model = item;
				break;
			}
		}
		return model;
	}

	/**
	 * 获取省列表
	 */
	private void GetProvinceList()
	{
		provinceList = new ArrayList<ProvinceCity>();
		for (ProvinceCity item : province_citys)
		{
			if (item.getParentCity().equals("0")&&!checkProvince(item.getCityName()))
			{
				provinceList.add(item);
			}
		}
	}

	/**
	 * 绑定省
	 * 
	 * @param provincename
	 */
	private void BindProvince(String provincename)
	{
		ArrayList<String> list = new ArrayList<String>();
		for (ProvinceCity item : provinceList)
		{
			list.add(item.getCityName());
		}
		provinceAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
		provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		add_province_box.setAdapter(provinceAdapter);
		if (provincename == null)
		{
			add_province_box.setSelection(0);
		} else
		{
			for (int i = 0; i < provinceList.size(); i++)
			{
				if (provincename.equals(provinceList.get(i).getCityName()))
				{
					add_province_box.setSelection(i);
					break;
				}
			}
		}
	}

	/**
	 * 是否已存在该一级城市
	 * 
	 * @param name
	 * @return
	 */
	private boolean checkProvince(String name)
	{
		boolean t = false;
		for (ProvinceCity item : provinceList)
		{
			if (item.getCityName().equals(name))
			{
				t = true;
				break;
			}
		}
		return t;
	}

	/**
	 * 获取二级城市
	 * 
	 * @param pname
	 */
	private void GetCityList(String pname)
	{
		cityList = new ArrayList<ProvinceCity>();
		for (ProvinceCity item : province_citys)
		{
			if (item.getParentCity().equals(pname))
			{
				if (!checkCity(item.getCityID()))
				{
					cityList.add(item);
				}
			}
		}
	}

	/**
	 * 绑定市
	 * 
	 * @param cityname
	 */
	private void BindCity(String cityname)
	{
		ArrayList<String> list = new ArrayList<String>();
		for (ProvinceCity item : cityList)
		{
			list.add(item.getCityName());
		}
		cityAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
		cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		add_city_box.setAdapter(cityAdapter);
		if (cityname == null)
		{
			add_city_box.setSelection(0);
		} else
		{
			for (int i = 0; i < cityList.size(); i++)
			{
				if (cityname.equals(cityList.get(i).getCityName()))
				{
					add_city_box.setSelection(i);
					break;
				}
			}
		}
	}

	/**
	 * 是否已存在该二级城市
	 * 
	 * @param name
	 * @return
	 */
	private boolean checkCity(String cityid)
	{
		boolean t = false;
		for (ProvinceCity item : cityList)
		{
			if (item.getCityID().equals(cityid))
			{
				t = true;
				break;
			}
		}
		return t;
	}

	private Handler messageHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				 final Intent intent=new Intent("com.province_city.rocky");  
			     intent.putExtra("msgContent", msg.obj.toString());  
			     context.sendBroadcast(intent);  
//				message_txt.setText(msg.obj + "");
				break;
			}
		}
	};
	
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		add_addressdlg.dismiss();
	}
}
