package com.pks.wechat.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 常量类
 * @author pks
 * @date 2017年4月11日
 */
public class WeChatConfigs {
	public static Map<String, WeChatConfig> config_map = new HashMap<String, WeChatConfig>();
	
	public static void init(String token,String appId,String appSecret,String mch_id,String api_key,String cert_path,String notify_url,String pay_action){
		WeChatConfig config = new WeChatConfig(token, appId, appSecret, mch_id, api_key, cert_path, notify_url, pay_action);
		config_map.put(appId, config);
	}
	
	public static WeChatConfig getConfig(String appId){
		WeChatConfig config = config_map.get(appId);
		if(config==null) config = new WeChatConfig();
		return config;
	}
	
}
