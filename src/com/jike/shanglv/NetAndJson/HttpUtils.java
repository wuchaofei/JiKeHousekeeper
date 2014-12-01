package com.jike.shanglv.NetAndJson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.jike.shanglv.Common.CustomerAlertDialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class HttpUtils {
	// 定义一个静态的方法获取JSON内容
	// 这里的path是web服务的网址
	//URLEncoder.encode(mySpinner.getSelectedItem().toString(), "utf-8")
	public static String getJsonContent(String path, String param) {
		try {
			// 根据路径创建URL地址
			URL url = new URL(path + "?" + param);
			// 通过url地址打开连接  
			Log.v("URL：",url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置超时时间
			conn.setConnectTimeout(10000);
			// 设置请求方式
			conn.setRequestMethod("GET");
			// 设置属性
//			 设置该连接是否可输入
			conn.setDoInput(true);
			int code = conn.getResponseCode();
			System.out.println(code + "****");
			if (code == 200) {
				return changeInputString(conn.getInputStream());
			}
//			else {
//				return changeInputString(conn.getErrorStream());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/*
	 * 自定义方法根据io流得到字符串
	 */
	public static String changeInputString(InputStream is) {
		String jsonString = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(data)) != -1) {
				baos.write(data, 0, len);
			}
			jsonString = new String(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
    
    public static String myPost(String url,String Post_String){
    	 HttpURLConnection conn = null;
    	 InputStream inputStream = null;
    	 OutputStream outputStrean;
    	
    	try {
			conn = (HttpURLConnection) (new URL(url)).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		conn.setDoInput(true);
		conn.setDoOutput(true);
		// conn.setConnectTimeout(5000);
		try {
			conn.setRequestMethod("POST");
			outputStrean = conn.getOutputStream();
			outputStrean.write(Post_String.getBytes());
			outputStrean.flush();
			inputStream = conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getString(inputStream);
    }
    
    protected static String getString(InputStream inputstream) {

		int length = 10000;

		StringBuffer stringBuffer = new StringBuffer();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputstream, HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		} catch (Exception e) {
			return null;
		}
		return stringBuffer.toString();
	}
    
    /** 
     * 获取网落图片资源  
     * @param url 
     * @return 
     */  
    public static Bitmap getHttpBitmap(String url){  
        URL myFileURL;  
        Bitmap bitmap=null;  
        try{  
            myFileURL = new URL(url);  
            //获得连接  
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();  
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制  
            conn.setConnectTimeout(6000);  
            //连接设置获得数据流  
            conn.setDoInput(true);  
            //不使用缓存  
            conn.setUseCaches(false);  
            //这句可有可无，没有影响  
            //conn.connect();  
            //得到数据流  
            InputStream is = conn.getInputStream();  
            //解析得到图片  
            bitmap = BitmapFactory.decodeStream(is);  
            //关闭数据流  
            is.close();  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
        return bitmap;  
          
    }  

	/*
	 * 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
	 */
	public static boolean checkNet(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Boolean showNetCannotUse(Context context){
		if (!HttpUtils.checkNet(context)) {
//			new AlertDialog.Builder(context).setTitle("网络连接失败")
//					.setMessage("无法连接网路，请检查网络设置！")
//					.setPositiveButton("确定", null).show();
			final CustomerAlertDialog cad=new CustomerAlertDialog(context,true);
			cad.setTitle("无法连接网路，请检查网络设置！");
			cad.setPositiveButton("知道了", new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					cad.dismiss();
				}});
			return true;
		}
		return false;
	}

	public static Boolean sendJsonContent(String path, JSONObject jsonObject) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(path);
		try {
//			jsonObject = new JSONObject();
//			jsonObject.put("name", "ze");
			StringEntity entity = new StringEntity(jsonObject.toString());
			post.setEntity(entity);
			HttpResponse responString = client.execute(post);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}

