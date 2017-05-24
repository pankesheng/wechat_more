package com.pks.wechat.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pks.wechat.configuration.WeChatErrorCode;
import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.material.NewsMaterial;
import com.pks.wechat.material.OtherMaterial;
import com.pks.wechat.menu.Menu;
import com.pks.wechat.message.resp.Article;
import com.pks.wechat.message.resp.Music;
import com.pks.wechat.pojo.AccessToken;
import com.zcj.web.dto.Page;


public class WechatApiHelper {
	
	/**
	 * 发送文本客服消息
	 * @param openid
	 * @param content
	 * @return 返回成功（true）会失败（false）
	 */
	public static boolean sendTextMessage(String appId,String openid,String content){
		if (StringUtils.isBlank(content)) {
			return false;
		}
		String jsonMsg = SUtilCustomer.makeTextCustomMessage(openid, content);
		return SUtilCustomer.sendCustomMessage(appId,jsonMsg);
	}

	/**
	 * 发送图文客服消息
	 * @param openid
	 * @param articleList
	 * @return 返回成功（true）会失败（false）
	 */
	public static boolean sendNewsMessage(String appId,String openid,List<Article> articleList){
		if(articleList==null || articleList.size()==0 || articleList.size()>10){
			return false;
		}
		String jsonMsg = SUtilCustomer.makeNewsCustomMessage(openid, articleList);
		return SUtilCustomer.sendCustomMessage(appId,jsonMsg);
	}
	
	/**
	 * 发送音乐消息
	 * @param openid
	 * @param music
	 * @return 返回成功（true）会失败（false）
	 */
	public static boolean send(String appId,String openid,Music music){
		String jsonMsg = SUtilCustomer.makeMusicCustomMessage(openid, music);
		return SUtilCustomer.sendCustomMessage(appId,jsonMsg);
	}
	
	/***
	 * 
	 * @param menu
	 * @return 如果成功返回空字符串，如果失败返回错误代码和错误信息;
	 */
	public static String createMenuMsg(String appId,Menu menu){
		if(menu.getButton()==null || menu.getButton().length==0 || menu.getButton().length>3){
			return "菜单按钮数量不符合";
		}
		AccessToken accessToken = SUtilBase.getAccessToken(appId);
        String url = WeChatUrlConfiguration.MENU_CREATE_URL.replace("ACCESS_TOKEN", accessToken.getAccess_token());  
        String jsonMenu = JSONObject.fromObject(menu).toString();  
        JSONObject jsonObject = SUtilCommon.httpsRequest(url, "POST", jsonMenu);  
        if (null != jsonObject) {
        	Integer errcode = jsonObject.getInt("errcode");
            if (0 != errcode) {
            	if(StringUtils.isNotBlank(WeChatErrorCode.errorCodeMap.get(errcode))){
            		return WeChatErrorCode.errorCodeMap.get(errcode);
            	}else{
            		return String.format("errcode:{%s} errmsg:{%s}",jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            	}
            }
        }
		return "";
	}
	
	@SuppressWarnings({ "static-access", "unused" })
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String appId = "";
		AccessToken accessToken = SUtilBase.getAccessToken(appId);
		String result = SUtilCommon.httpGetRequest(WeChatUrlConfiguration.USER_LIST_URL.replace("ACCESS_TOKEN", accessToken.getAccess_token()).replace("NEXT_OPENID", ""));
		System.out.println(result);
		JSONObject jsonObject = new JSONObject().fromObject(result);
		List<String> openids = new ArrayList<String>();
		if(jsonObject.containsKey("data")){
			JSONObject openidJsonObject = (JSONObject) jsonObject.get("data");
			System.out.println(openidJsonObject);
			openids = new Gson().fromJson(openidJsonObject.getString("openid"), new TypeToken<List<String>>(){}.getType());
		}
		for (String openid : openids) {
			if(openid.equals("oXWySjiKx0vEVHmMs-Ul9u8O4_vA")){
				boolean bool = sendTextMessage(appId,openid, "测试文本消息");
				List<Article> articleList = new ArrayList<Article>();
				Article article = new Article("测试图文消息", "测试图文消息测试图文消息测试图文消息测试图文消息测试图文消息", "https://ss1.bdstatic.com/5eN1bjq8AAUYm2zgoY3K/r/www/cache/static/protocol/https/global/img/icons_5c448026.gif", "www.baidu.com");
				articleList.add(article);articleList.add(article);articleList.add(article);
				bool = sendNewsMessage(appId,openid, articleList);
			}
		}
	}

	/**
	 * 根据分页信息获得分页数据
	 * @param type 素材类型 （image,video,voice,news）
	 * @param offset
	 * @param pagesize
	 * @return
	 */
	public static Page getMaterialList(String appId,String type,int offset,int pagesize) {
		Page page = new Page();
		if(StringUtils.isBlank(type) || (!"image".equals(type)&&!"news".equals(type) && !"video".equals(type) && !"voice".equals(type))){
			page.setRows(null);
			page.setTotal(0);
			return page;
		}
		JSONObject jsonObject = SUtilMaterial.getMaterailListJson(appId,type, offset, pagesize);
		if(jsonObject!=null && jsonObject.containsKey("item")){
			if("news".equals(type)){
				List<NewsMaterial> list = new Gson().fromJson(jsonObject.getString("item"),  new TypeToken<List<NewsMaterial>>(){}.getType());
				page.setRows(list);
			}else{
				List<OtherMaterial> list = new Gson().fromJson(jsonObject.getString("item"),  new TypeToken<List<OtherMaterial>>(){}.getType());
				page.setRows(list);
			}
			page.setTotal(jsonObject.getInt("total_count"));
			return page;
		}
		page.setTotal(0);
		page.setRows(null);
		return page;
	}
}
