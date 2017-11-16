package com.pks.wechat.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pks.wechat.configuration.WeChatConfigs;
import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.pojo.AccessToken;
import com.pks.wechat.pojo.JsApiTicket;
import com.pks.wechat.pojo.OnLineKf;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class SUtilBase {
	
	private static final Logger log = LoggerFactory.getLogger(SUtilBase.class);
	
	/**
	 * 获取access_token
	 * 
	 * @param appid
	 *            凭证
	 * @param appsecret
	 *            密钥
	 * @return
	 */
	private static AccessToken getAccessToken(String appid, String appsecret) {
		AccessToken accessToken = null;
		String requestUrl = WeChatUrlConfiguration.TOKEN_URL.replace("APPID",appid).replace("APPSECRET", appsecret);
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				accessToken = new AccessToken();
				accessToken.setAccess_token(jsonObject.getString("access_token"));
				int expires_in = jsonObject.getInt("expires_in");
				long endtime = new Date().getTime()+expires_in*1000-500*1000;
				accessToken.setExpires_in(expires_in);
				accessToken.setEndtime(endtime);
			} catch (JSONException e) {
				accessToken = null;
				// 获取token失败
				System.out.println("获取token失败 errcode:{} errmsg:{}"
						+ jsonObject.getInt("errcode")
						+ jsonObject.getString("errmsg"));
				log.error("获取token失败 errcode:{} errmsg:{}",
						jsonObject.getInt("errcode"),
						jsonObject.getString("errmsg"));
			}
		}
		return accessToken;
	}
	/**
	 * 获取保存的accesstoken 如果accesstoken为null 或者过去重新获取accesstoken
	 * @return
	 */
	public static AccessToken getAccessToken(String appId){
		AccessToken accessToken = WeChatConfigs.getConfig(appId).getAccessToken();
		if(accessToken==null || accessToken.getEndtime()<new Date().getTime()){
			accessToken = getAccessToken(appId, WeChatConfigs.getConfig(appId).getAppSecret());
			WeChatConfigs.getConfig(appId).setAccessToken(accessToken);
		}
		return accessToken;
	}
	
	
	private static JsApiTicket getJsApiTicketByAccessToken(String accessToken) {  
        JsApiTicket jsApiTicket = null;  
        String requestUrl = WeChatUrlConfiguration.JSAPI_TICKET_URL.replace("ACCESS_TOKEN", accessToken);  
        JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "GET", null);  
        // 如果请求成功  
        if (null != jsonObject) {  
        	try {  
        		jsApiTicket = new JsApiTicket();  
        		jsApiTicket.setTicket(jsonObject.getString("ticket")); 
        		int expires_in = jsonObject.getInt("expires_in");
        		long endtime = new Date().getTime()+expires_in*1000 - 500*1000;
        		jsApiTicket.setExpires_in(expires_in);
        		jsApiTicket.setEndtime(endtime);
        	} catch (JSONException e) {  
        		accessToken = null;  
        		// 获取jsApiTicket失败  
        		log.error("获取jsApiTicket失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));  
        	}  
        }  
        return jsApiTicket;  
	}
	
	/**
	 * 获取jsapiticket 如果为空或者过期 重新发起请求获取
	 * @return
	 */
	public static JsApiTicket getJsApiTicket(String appId){
		JsApiTicket jsApiTicket = WeChatConfigs.getConfig(appId).getJsapi_ticket();
		if(jsApiTicket==null || jsApiTicket.getEndtime()<new Date().getTime()){
			AccessToken accessToken = getAccessToken(appId);
			jsApiTicket = getJsApiTicketByAccessToken(accessToken.getAccess_token());
			WeChatConfigs.getConfig(appId).setJsapi_ticket(jsApiTicket);
		}
		return jsApiTicket;
	}
	
	
	public static String getServerIps(String appId){
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		String url = WeChatUrlConfiguration.SERVER_IPS.replace("ACCESS_TOKEN", accessToken);
		String result = SUtilCommon.httpRequest(url);
		return result;
	}
	
}
