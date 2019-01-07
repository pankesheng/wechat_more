package com.pks.wechat.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.material.ArticleMaterial;
import com.pks.wechat.pojo.WeChatMedia;

/**
 * 群发接口
 * @author pks
 * @version 2019年1月3日
 */
public class SUtilSend {
	
	private static Logger log = LoggerFactory.getLogger(SUtilSend.class);
	
	/***
	 * 上传图文消息素材【订阅号与服务号认证后均可用】
	 * @param appId
	 * @param list com.pks.wechat.material.ArticleMaterial 列表
	 * @return
	 */
	public static WeChatMedia uploadnews(String appId,List<ArticleMaterial> list) {
		String jsonMsg = makeUploadNewsMessage(list);
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		String requestUrl =WeChatUrlConfiguration.MATERIAL_UPLOADNEWS_URL;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",jsonMsg);
		WeChatMedia weixinMedia = null;
		if (null != jsonObject) {
			log.info(jsonObject.toString());
			weixinMedia = new WeChatMedia();
			weixinMedia.setType(jsonObject.getString("type"));
			weixinMedia.setMediaId(jsonObject.getString("media_id"));
			weixinMedia.setCreatedAt(jsonObject.getInt("created_at"));
		}
		return weixinMedia;
	}
	
	private static String makeUploadNewsMessage(List<ArticleMaterial> list) {
		String jsonMsg = "{\"articles\":%s}";
		jsonMsg = String.format(jsonMsg,JSONArray.fromObject(list).toString());
		return jsonMsg;
	}
	
	/***
	 * 标签形式群发
	 * @param appId	微信公众号appid
	 * @param jsonMsg	消息主题，根据不同消息体进行组装，该类有提供相关对应的组装接口，可直接调用
	 * @return
	 */
	public static boolean sendTagMessage(String appId,String jsonMsg) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		boolean result = false;
		String requestUrl =WeChatUrlConfiguration.SENDALL_URL;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",jsonMsg);
		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("群发消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("群发消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
	
	/***
	 * 标签方式群发的图文消息格式 此处的media_id需要 SUtilSend.uploadnews 方法获取
	 * @param is_to_all 用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据tag_id发送给指定群组的用户
	 * @param tag_id	群发到的标签的tag_id，参见用户管理中用户分组接口，若is_to_all值为true，可不填写tag_id
	 * @param media_id	用于群发的消息的media_id
	 * @param send_ignore_reprint 图文消息被判定为转载时，是否继续群发。 1为继续群发（转载），0为停止群发。 该参数默认为0。
	 * @return
	 */
	public static String makeArticleTagMessage(Boolean is_to_all,Integer tag_id,String media_id,Integer send_ignore_reprint) {
		if(is_to_all==null) is_to_all = false;
		if(send_ignore_reprint==null) send_ignore_reprint = 0;
		String jsonMsg = "";
		if(is_to_all){
			jsonMsg = "{\"filter\":{\"is_to_all\":true},\"mpnews\":{\"media_id\":\"%s\"},\"msgtype\":\"mpnews\",\"send_ignore_reprint\":%s}";
			jsonMsg = String.format(jsonMsg,media_id,send_ignore_reprint);
		}else{
			jsonMsg = "{\"filter\":{\"is_to_all\":false,\"tag_id\":%s},\"mpnews\":{\"media_id\":\"%s\"},\"msgtype\":\"mpnews\",\"send_ignore_reprint\":%s}";
			jsonMsg = String.format(jsonMsg,tag_id,media_id,send_ignore_reprint);
		}
			
		return jsonMsg;
	}
	/***
	 * 标签方式群发的图片消息格式
	 * @param is_to_all
	 * @param tag_id
	 * @param media_id
	 * @return
	 */
	public static String makeImageTagMessage(Boolean is_to_all,Integer tag_id,String media_id){
		if(is_to_all==null) is_to_all = false;
		String jsonMsg = "";
		if(is_to_all){
			jsonMsg = "{\"filter\":{\"is_to_all\":true},\"image\":{\"media_id\":\"%s\"},\"msgtype\":\"image\"}";
			jsonMsg = String.format(jsonMsg,media_id);
		}else{
			jsonMsg = "{\"filter\":{\"is_to_all\":false,\"tag_id\":%s},\"image\":{\"media_id\":\"%s\"},\"msgtype\":\"image\"}";
			jsonMsg = String.format(jsonMsg,tag_id,media_id);
		}
		return jsonMsg;
	}
	
	/***
	 * 标签方式群发的文本消息格式
	 * @param is_to_all
	 * @param tag_id
	 * @param text 文本内容
	 * @return
	 */
	public static String makeTextTagMessage(Boolean is_to_all,Integer tag_id,String text){
		if(is_to_all==null) is_to_all = false;
		String jsonMsg = "";
		if(is_to_all){
			jsonMsg = "{\"filter\":{\"is_to_all\":true},\"text\":{\"content\":\"%s\"},\"msgtype\":\"text\"}";
			jsonMsg = String.format(jsonMsg,text);
		}else{
			jsonMsg = "{\"filter\":{\"is_to_all\":false,\"tag_id\":%s},\"text\":{\"content\":\"%s\"},\"msgtype\":\"text\"}";
			jsonMsg = String.format(jsonMsg,tag_id,text);
		}
		return jsonMsg;
	}
	
	
	
	
	public static boolean sendUserMessage(String appId,String jsonMsg) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		boolean result = false;
		String requestUrl =WeChatUrlConfiguration.SEND_URL;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",jsonMsg);
		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("群发消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("群发消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
	
	public static String makeArticleUserMessage(List<String> openIds,String media_id,Integer send_ignore_reprint) {
		if(send_ignore_reprint==null) send_ignore_reprint = 0;
		String jsonMsg = "{\"touser\":%s,\"mpnews\":{\"media_id\":\"%s\"},\"msgtype\":\"mpnews\",\"send_ignore_reprint\":%s}";
		jsonMsg = String.format(jsonMsg,JSONArray.fromObject(openIds).toString(),media_id,send_ignore_reprint);
		return jsonMsg;
	}
	
	public static String makeImageUserMeesage(List<String> openIds,String media_id){
		String jsonMsg = "{\"touser\":%s,\"image\":{\"media_id\":\"%s\"},\"msgtype\":\"image\"}";
		jsonMsg = String.format(jsonMsg,JSONArray.fromObject(openIds).toString(),media_id);
		return jsonMsg;
	}
	
	public static boolean sendPreviewMessage(String appId,String jsonMsg) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		boolean result = false;
		String requestUrl = WeChatUrlConfiguration.SEND_PREVIEW_URL;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",jsonMsg);
		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("群发预览消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("群发预览消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
	
	public static String makeArticlePreviewMessage(String openId,String media_id){
		String jsonMsg = "{\"touser\":\"%s\",\"mpnews\":{\"media_id\":\"%s\"},\"msgtype\":\"mpnews\"}";
		jsonMsg = String.format(jsonMsg,openId,media_id);
		return jsonMsg;
	}
	
}

