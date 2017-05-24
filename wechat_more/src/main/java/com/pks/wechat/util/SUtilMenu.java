package com.pks.wechat.util;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.menu.Menu;

/**
 * 自定义菜单工具类
 * 
 */
public class SUtilMenu {
	private static Logger log = LoggerFactory.getLogger(SUtilMenu.class);

	/** 
     * 创建菜单 
     *  
     * @param menu 菜单实例 
     * @param accessToken 有效的access_token 
     * @return 0表示成功，其他值表示失败 
     */  
    public static int createMenu(String appId,Menu menu) {  
        int result = 0;  
      
        // 拼装创建菜单的url  
        String url = WeChatUrlConfiguration.MENU_CREATE_URL.replace("ACCESS_TOKEN", SUtilBase.getAccessToken(appId).getAccess_token());  
        // 将菜单对象转换成json字符串  
        String jsonMenu = JSONObject.fromObject(menu).toString();  
        // 调用接口创建菜单  
        JSONObject jsonObject = SUtilCommon.httpsRequest(url, "POST", jsonMenu);  
      
        if (null != jsonObject) {
            if (0 != jsonObject.getInt("errcode")) {  
                result = jsonObject.getInt("errcode"); 
                System.out.println("创建菜单失败 errcode:{} errmsg:{}"+ jsonObject.getInt("errcode")+jsonObject.getString("errmsg"));
                log.error("创建菜单失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));  
                
            }  
        }  
        return result;  
    }  
	
	/**
	 * 查询菜单
	 * 
	 * @param accessToken 凭证
	 * @return
	 */
	public static String getMenu(String accessToken) {
		String result = null;
		String requestUrl = WeChatUrlConfiguration.MENU_GET_URL.replace("ACCESS_TOKEN", accessToken);
		// 发起GET请求查询菜单
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject) {
			result = jsonObject.toString();
		}
		return result;
	}

	/**
	 * 删除菜单
	 * 
	 * @param accessToken 凭证
	 * @return true成功 false失败
	 */
	public static boolean deleteMenu(String accessToken) {
		boolean result = false;
		String requestUrl = WeChatUrlConfiguration.MENU_DELETE_URL.replace("ACCESS_TOKEN", accessToken);
		// 发起GET请求删除菜单
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
			} else {
				result = false;
				log.error("删除菜单失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
}