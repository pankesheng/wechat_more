package com.pks.wechat.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.pks.wechat.configuration.WeChatConfigs;

/** 
 * 请求校验工具类 
 * 
 */
public class SUtilSign {
  
    /** 
     * 验证签名 
     *  
     * @param signature 
     * @param timestamp 
     * @param nonce 
     * @return 
     */  
    public static boolean checkSignature(String appId,String signature, String timestamp, String nonce) {  
        String[] arr = new String[] { WeChatConfigs.getConfig(appId).getToken(), timestamp, nonce };  
        // 将token、timestamp、nonce三个参数进行字典序排序  
        Arrays.sort(arr);  
        StringBuilder content = new StringBuilder();  
        for (int i = 0; i < arr.length; i++) {  
            content.append(arr[i]);  
        }  
        MessageDigest md = null;  
        String tmpStr = null;  
  
        try {  
            md = MessageDigest.getInstance("SHA-1");  
            // 将三个参数字符串拼接成一个字符串进行sha1加密  
            byte[] digest = md.digest(content.toString().getBytes());  
            tmpStr = byteToStr(digest);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        content = null;  
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信  
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;  
    }  
  
    /** 
     * 将字节数组转换为十六进制字符串 
     *  
     * @param byteArray 
     * @return 
     */  
    private static String byteToStr(byte[] byteArray) {  
        String strDigest = "";  
        for (int i = 0; i < byteArray.length; i++) {  
            strDigest += byteToHexStr(byteArray[i]);  
        }  
        return strDigest;  
    }  
  
    /** 
     * 将字节转换为十六进制字符串 
     *  
     * @param mByte 
     * @return 
     */  
    private static String byteToHexStr(byte mByte) {  
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
        char[] tempArr = new char[2];  
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];  
        tempArr[1] = Digit[mByte & 0X0F];  
  
        String s = new String(tempArr);  
        return s;  
    }  

  //创建签名SHA1
  	@SuppressWarnings("rawtypes")
  	public static String createSHA1Sign(SortedMap<String, String> signParams) throws Exception {
  		StringBuffer sb = new StringBuffer();
  		Set es = signParams.entrySet();
  		Iterator it = es.iterator();
  		while (it.hasNext()) {
  			Map.Entry entry = (Map.Entry) it.next();
  			String k = (String) entry.getKey();
  			String v = (String) entry.getValue();
  			sb.append(k + "=" + v + "&");
  			//要采用URLENCODER的原始值！
  		}
  		String params = sb.substring(0, sb.lastIndexOf("&"));
//  	System.out.println("sha1之前:" + params);
//  	System.out.println("SHA1签名为："+getSha1(params));
  		return getSha1(params);
  	}
  	
  	public static String getSha1(String str) {
  		if (str == null || str.length() == 0) {
  			return null;
  		}
  		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
  				'a', 'b', 'c', 'd', 'e', 'f' };

  		try {
  			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
  			mdTemp.update(str.getBytes("GBK"));

  			byte[] md = mdTemp.digest();
  			int j = md.length;
  			char buf[] = new char[j * 2];
  			int k = 0;
  			for (int i = 0; i < j; i++) {
  				byte byte0 = md[i];
  				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
  				buf[k++] = hexDigits[byte0 & 0xf];
  			}
  			return new String(buf);
  		} catch (Exception e) {
  			return null;
  		}
  	}
  	
  	public static Map<String, String> jsApiTicketSign(String appId,String url) {
  		String jsapi_ticket = SUtilBase.getJsApiTicket(appId).getTicket();
		Map<String, String> ret = new HashMap<String, String>();
		String nonce_str = SUtilCommon.getNonceStr();
		String timestamp = SUtilCommon.getTimeStamp();
		String string1;
		String signature = "";
		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str
				+ "&timestamp=" + timestamp + "&url=" + url;
		System.out.println(string1);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ret.put("url", url);
		ret.put("jsapi_ticket", jsapi_ticket);
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

}
