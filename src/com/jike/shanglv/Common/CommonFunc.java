package com.jike.shanglv.Common;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 公共函数方法
 * 
 * @author Administrator
 * 
 */
public class CommonFunc {

	/**
	 * 判断手机格式是否正确
	 * 
	 * @param String
	 *            mobiles
	 * @return boolean
	 */
	public static boolean isMobileNO(String mobiles) {
//		Pattern p = Pattern
//				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//		Matcher m = p.matcher(mobiles);
//		return m.matches();
		 Pattern p = null;  
	        Matcher m = null;  
	        boolean b = false;   
	        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号  
	        m = p.matcher(mobiles);  
	        b = m.matches();   
	        return b;  
	}
	
	 /** 
     * 电话号码验证 
     *  
     * @param  str 
     * @return 验证通过返回true 
     */  
    public static boolean isPhone(String str) {   
        Pattern p1 = null,p2 = null;  
        Matcher m = null;  
        boolean b = false;    
        p1 = Pattern.compile("^[0][1-9]{2,3}[0-9]{5,10}$");  // 验证带区号的  
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的  
        if(str.length() >9)  
        {   m = p1.matcher(str);  
            b = m.matches();    
        }else{  
            m = p2.matcher(str);  
            b = m.matches();   
        }    
        return b;  
    }  

	/**
	 * 判断email格式是否正确
	 * 
	 * @param String
	 *            email
	 * @return boolean
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 判断用户名格式是否正确（6-12位的数字或字母或_组成）
	 * 
	 * @param String
	 * @return boolean
	 */
	public static boolean isValidUserName(String username) {
		String str = "^[0-9a-zA-Z_]{6,12}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(username);
		return m.matches();
	}

	/**
	 * 判断密码格式是否正确（6-20位的数字或字母组成）
	 * 
	 * @param String
	 *            password
	 * @return boolean
	 */
	public static boolean isValidPassword(String password) {
		String str = "^[0-9a-zA-Z]{6,20}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(password);
		return m.matches();
	}
	/**
	 * 国际需求单，姓名规则：以英文开头，只包含英文字符和斜杠,
	 * @param input
	 * @return
	 */
	public static Boolean isEnglishName(String input){
		if (!input.contains("/")) {
			return false;
		}
		String str = "^[a-zA-Z][a-z A-Z/]*$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(input);
		return m.matches();
	}
	
	/**正则表达式数字验证
	 * */
    public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9.]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
             return false;
        }
        else
        {
             return true;
        }
    }

	/**
	 * 获取MD5加密
	 */
	public static String MD5(String password) {
		MessageDigest md;
		try {
			// 生成一个MD5加密计算摘要
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(password.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			String pwd = new BigInteger(1, md.digest()).toString(16);
			if (pwd.length()<32) {
				return getMD5Str(password);
			}
			return pwd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return password;
	}
	
	/** 
     * MD5 加密 
     */  
    private static String getMD5Str(String str) {  
        MessageDigest messageDigest = null;  
  
        try {  
            messageDigest = MessageDigest.getInstance("MD5");  
  
            messageDigest.reset();  
  
            messageDigest.update(str.getBytes("UTF-8"));  
        } catch (NoSuchAlgorithmException e) {  
            System.out.println("NoSuchAlgorithmException caught!");  
            System.exit(-1);  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
  
        byte[] byteArray = messageDigest.digest();  
  
        StringBuffer md5StrBuff = new StringBuffer();  
  
        for (int i = 0; i < byteArray.length; i++) {              
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
        }  
  
        return md5StrBuff.toString();  
    }  

	public static String getPhoneNumber(Context context) {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String numberString="";
		try {//没有电话卡的手机  发生异常   返回""
			numberString =mTelephonyMgr.getLine1Number().replace("+86", "");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return numberString;
	}

	/**
	 * 去除相同元素的方法
	 * 
	 * @param al
	 * @return
	 */
	public static ArrayList<Object> singleElement(ArrayList<Object> al) {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		Iterator<Object> it = al.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			// 如果不包含该元素,则添加进来,contains() 方法底层调用的是 Person 的 equals() 方法
			if (!arrayList.contains(obj))
				arrayList.add(obj);
		}
		// 返回新的没有重复元素的ArrayList集合对象
		return arrayList;
	}

	/**
	 * 验证日期字符串是否是YYYY-MM-DD格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDataFormat(String str) {
		boolean flag = false;
		// String
		// regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
		String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		Pattern pattern1 = Pattern.compile(regxStr);
		Matcher isNo = pattern1.matcher(str);
		if (isNo.matches()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 功能：判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

}
