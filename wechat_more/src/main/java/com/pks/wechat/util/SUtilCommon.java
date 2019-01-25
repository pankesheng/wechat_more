package com.pks.wechat.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class SUtilCommon {

	private static final Logger log = LoggerFactory.getLogger(SUtilCommon.class);

	/**
	 * 
	 * @Description: 发送https请求
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的数据
	 * @return JSONObject (通过JSONObject.get(key)的方式获取json对象的属性值)
	 * @throws
	 * @author pks
	 * @date 2015-12-16
	 */
	public static JSONObject httpsRequest(String requestUrl,String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);
			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// ע注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String str = null;
			StringBuffer stringBuffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				stringBuffer.append(str);
			}
			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			jsonObject = JSONObject.fromObject(stringBuffer.toString());

		} catch (ConnectException ce) {
			log.error("连接超时：{}", ce);
		} catch (Exception e) {
			log.error("https 请求异常：{}", e);
		}
		return jsonObject;
	}

	/**
	 * 发起http请求获取返回结果 get
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @return
	 */
	public static String httpRequest(String requestUrl) {
		StringBuffer buffer = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url
					.openConnection();

			httpUrlConn.setDoOutput(false);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);

			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.connect();

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return buffer.toString();
	}

	/**
	 * URL编码（utf-8）
	 * 
	 * @param source
	 * @return
	 */
	public static String urlEncodeUTF8(String source) {
		String result = source;
		try {
			result = java.net.URLEncoder.encode(source, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据内容类型判断文件扩展名
	 * 
	 * @param contentType
	 *            内容类型
	 * @return
	 */
	public static String getFileExt(String contentType) {
		String fileExt = "";
		if(contentType == null || contentType == ""){
			return fileExt;
		}
		if(contentType.contains("image/jpeg")){
			fileExt = ".jpg";
		} else if (contentType.contains("audio/mpeg")){
			fileExt = ".mp3";
		} else if (contentType.contains("audio/amr")){
			fileExt = ".amr";
		} else if (contentType.contains("video/mp4")){
			fileExt = ".mp4";
		} else if (contentType.contains("video/mpeg4")){
			fileExt = ".mp4";
		}
//		if ("image/jpeg".equals(contentType))
//			fileExt = ".jpg";
//		else if ("audio/mpeg".equals(contentType))
//			fileExt = ".mp3";
//		else if ("audio/amr".equals(contentType))
//			fileExt = ".amr";
//		else if ("video/mp4".equals(contentType))
//			fileExt = ".mp4";
//		else if ("video/mpeg4".equals(contentType))
//			fileExt = ".mp4";
		return fileExt;
	}

	/**
	 * 编码
	 * 
	 * @param source
	 * @return
	 */
	public static String urlEncode(String source, String encode) {
		String result = source;
		try {
			result = java.net.URLEncoder.encode(source, encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "0";
		}
		return result;
	}

	/**
	 * 发起http get请求获取返回结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @return
	 */
	public static String httpGetRequest(String requestUrl) {
		StringBuffer buffer = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url
					.openConnection();
			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			httpUrlConn.setDoOutput(false);
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			httpUrlConn.setDoInput(true);
			// Post 请求不能使用缓存
			httpUrlConn.setUseCaches(false);

			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.connect();

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return buffer.toString();
	}

	/**
	 * 发起http post请求获取返回结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @return
	 */
	public static String httpPostRequest(String requestUrl,String params) {
		StringBuffer buffer = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url
					.openConnection();
			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			httpUrlConn.setDoOutput(true);
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			httpUrlConn.setDoInput(true);
			// Post 请求不能使用缓存
			httpUrlConn.setUseCaches(false);

			httpUrlConn.setRequestMethod("POST");
			DataOutputStream out = new DataOutputStream(
					httpUrlConn.getOutputStream());
			out.writeBytes(params.toString());
			httpUrlConn.connect();
			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return buffer.toString();
	}

	public static String readTxtFile(String filePath) {
		StringBuilder sb = new StringBuilder();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
				}
				read.close();
				return sb.toString();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取客户端IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemortIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.contains(",")) {
			String[] ips = ip.split(",");
			if (ips.length > 1) {
				return ips[0];
			}
		}
		return ip;
	}

	public static String paramstourl(Map<String, Object> params) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Iterator<Map.Entry<String, Object>> it = params.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
					.next();
			if (i > 0) {
				sb.append("&");
			}
			String value = "";
            try {
            	value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
            sb.append(entry.getKey()+"="+value);
			i++;
		}
		return sb.toString();
	}

	/**
	 * 获取当前时间 yyyyMMddHHmmss
	 * 
	 * @return String
	 */
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}

	/**
	 * 取出一个指定长度大小的随机正整数.
	 * 
	 * @param length
	 *            int 设定所取出随机数的长度。length小于11
	 * @return int 返回生成的随机数。
	 */
	public static int buildRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}

	/**
	 * 获取编码字符集
	 * 
	 * @param request
	 * @param response
	 * @return String
	 */

	public static String getCharacterEncoding(HttpServletRequest request,
			HttpServletResponse response) {

		if (null == request || null == response) {
			return "gbk";
		}

		String enc = request.getCharacterEncoding();
		if (null == enc || "".equals(enc)) {
			enc = response.getCharacterEncoding();
		}

		if (null == enc || "".equals(enc)) {
			enc = "gbk";
		}

		return enc;
	}

	public static String getNonceStr() {
		return UUID.randomUUID().toString();
//		Random random = new Random();
//		return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

	/**
     * 拼接请求连接  map内的key排序
     * @param paraMap
     * @param urlencode 是否对地址参数进行加密
     * @return
     * @throws SDKRuntimeException
     */
    public static String FormatBizQueryParaMap(HashMap<String, String> paraMap,  
            boolean urlencode) throws SDKRuntimeException {  
        String buff = "";  
        try {  
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(  
                    paraMap.entrySet());  
            Collections.sort(infoIds,  
                new Comparator<Map.Entry<String, String>>() {  
                    public int compare(Map.Entry<String, String> o1,  
                            Map.Entry<String, String> o2) {  
                        return (o1.getKey()).toString().compareTo(  
                                o2.getKey());  
                    }  
                });  
            for (int i = 0; i < infoIds.size(); i++) {  
                Map.Entry<String, String> item = infoIds.get(i);  
                //System.out.println(item.getKey());  
                if (item.getKey() != "") {  
                    String key = item.getKey();  
                    String val = item.getValue();  
                    if (urlencode) {  
                        val = URLEncoder.encode(val, "utf-8");  
                    }  
                    buff += key.toLowerCase() + "=" + val + "&";  
                }  
            }  
            if (buff.isEmpty() == false) {  
                buff = buff.substring(0, buff.length() - 1);  
            }  
        } catch (Exception e) {  
            throw new SDKRuntimeException(e.getMessage());  
        }  
        return buff;  
    }  
}
