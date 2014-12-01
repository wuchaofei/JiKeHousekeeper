package com.jike.shanglv;

import java.util.HashMap;
import android.content.Context;

import com.jike.shanglv.Enums.PackageKeys;
import com.jike.shanglv.Enums.Platform;


//百度地图 SHA1 0D:3C:EC:2E:C2:01:A0:E6:C7:AE:44:B4:05:17:9D:F8:BE:A9:70:9E
public class MyApp {
	private Context context;
	
	HashMap<String,Object> self_hm=new HashMap<String,Object>();//际珂B2C 商旅管家
	HashMap<String,Object> self_b_hm=new HashMap<String,Object>();//际珂B2B 商旅助手
	HashMap<String,Object> nanbei_hm=new HashMap<String,Object>();//
	HashMap<String,Object> nanbei_b_hm=new HashMap<String,Object>();
	HashMap<String,Object> menghang_hm=new HashMap<String,Object>();
	HashMap<String,Object> menghang_b_hm=new HashMap<String,Object>();

//	HashMap的使用
//	put(K key, V value) 
//	hm.put(a,b); //插入值为b,key值为a
//	hm.get(key); //返回值为value
	/**打包不同程序时更改此处   另外打包时需要更改百度地图的key
	 * 此类中只需更改以下三个值：RELEASE、hm、AndroidManifest中的程序名及图标
	 */
	public static boolean RELEASE = true;//测试  or 发布，接口
	private HashMap<String,Object> hm=self_hm;//menghang_hm;//self_hm
	
//	public static String userkey="5b13658a9fc945e34893f806027d467a";//5b13658a9fc945e34893f806027d467a有效期到2014.09.10
	public static String sitekey="";
	
	public MyApp(Context context){
		this.context=context;
		createValue();
	}
	
	public boolean isRELEASE() {
		return RELEASE;
	}
	
	public HashMap<String, Object> getHm() {
		return hm;
	}
	
	/**获取接口的地址
	 */
	public String getServeUrl() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_server_url);
		else
			return context.getResources().getString(R.string.test_server_url);
	
	}
	/**获取支付接口的地址
	 */
	public String getPayServeUrl() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_pay_server_url);
		else return context.getResources().getString(R.string.test_pay_server_url);
	}
	/**获取火车票验证码接口的地址
	 */
	public String getValidcodeServeUrl() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_train_validcode);
		else return context.getResources().getString(R.string.test_train_validcode);
	}
	/**获取航空公司logo
	 */
	public String getFlightCompanyLogo() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_flight_company_logo);
		else return context.getResources().getString(R.string.test_flight_company_logo);
	}
	/**关于软件说明Url
	 */
	public String getAbout() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_about);
		else return context.getResources().getString(R.string.test_about);
	}
	/**获取update接口的地址
	 */
	public String getUpdateServeUrl() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_update_url);
		else return context.getResources().getString(R.string.test_update_url);
	}

	/**构建不同厂家的打包数据
	 */
	public void createValue(){
		//商旅管家
		self_hm.put(PackageKeys.WELCOME_DRAWABLE.getString(), R.drawable.welcome);
		self_hm.put(PackageKeys.APP_NAME.getString(), R.string.app_name);
		self_hm.put(PackageKeys.MENU_LOGO_DRAWABLE.getString(), R.drawable.menu_logo);
		self_hm.put(PackageKeys.UPDATE_NOTE.getString(), "jike");
		self_hm.put(PackageKeys.PLATFORM.getString(), Platform.B2C);
		self_hm.put(PackageKeys.USERKEY.getString(),RELEASE?"ffdd14d2e6c26b70749c8b2c08067c69":"5b13658a9fc945e34893f806027d467a");
		self_hm.put(PackageKeys.ORGIN.getString(),0);//该参数加于20141104，用于区分订单、请求的来源
		/*Android商旅管家0 Android商旅助手1 IOS商旅管家2 IOS商旅助手3  梦航 管家4  梦航 助手5*/
		
		//商旅助手
//		self_b_hm.put(PackageKeys.WELCOME_DRAWABLE.getString(), R.drawable.welcome_b);
		self_b_hm.put(PackageKeys.APP_NAME.getString(), R.string.app_name_b);
		self_b_hm.put(PackageKeys.MENU_LOGO_DRAWABLE.getString(), R.drawable.menu_logo_b);
		self_b_hm.put(PackageKeys.UPDATE_NOTE.getString(), "jike_b");
		self_b_hm.put(PackageKeys.PLATFORM.getString(), Platform.B2B);
		self_b_hm.put(PackageKeys.USERKEY.getString(),RELEASE?"ffdd14d2e6c26b70749c8b2c08067c69":"5b13658a9fc945e34893f806027d467a");
		self_b_hm.put(PackageKeys.ORGIN.getString(),1);
		
		//梦航商旅
//		menghang_hm.put(PackageKeys.WELCOME_DRAWABLE.getString(), R.drawable.welcome_menghang);
		menghang_hm.put(PackageKeys.APP_NAME.getString(), R.string.app_name_menghang);
		//menghang_hm.put(PackageKeys.MENU_LOGO_DRAWABLE.getString(), R.drawable.menu_logo_menghang);
		menghang_hm.put(PackageKeys.UPDATE_NOTE.getString(), "menghangshanglv");
		menghang_hm.put(PackageKeys.PLATFORM.getString(), Platform.B2C);
		menghang_hm.put(PackageKeys.USERKEY.getString(),RELEASE?"fc5865a78e9cb8b3d63c5428d4d32a4c":"5b13658a9fc945e34893f806027d467a");
		menghang_hm.put(PackageKeys.ORGIN.getString(),4);
	}
}
