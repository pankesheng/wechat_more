package com.pks.wechat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.message.resp.Article;
import com.pks.wechat.message.resp.Music;
import com.pks.wechat.pojo.KfInfo;
import com.pks.wechat.pojo.OnLineKf;

/**
 * 客服工具类
 * @author pks
 *
 */
public class SUtilCustomer {

	private static Logger log = LoggerFactory.getLogger(SUtilCustomer.class);
	
	/**
	 * 获取在线客服列表
	 * @param accessToken
	 * @return
	 */
	public static List<OnLineKf> getOnLineKfList(String appId){
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		String url = WeChatUrlConfiguration.KF_ONLINE_LIST_URL.replace("ACCESS_TOKEN", accessToken);
		String result = SUtilCommon.httpRequest(url);
		if(result.equals("{\"kf_online_list\":[]}")){
			Random random = new Random();
			int case1 = random.nextInt(10);
			int case2 = random.nextInt(10);
			int auto1 = random.nextInt(5);
			int auto2 = random.nextInt(5);
			result = "{\"kf_online_list\": [{\"kf_account\": \"test1@test\",\"status\": 1,\"kf_id\": \"1001\",\"auto_accept\": "+auto1+",\"accepted_case\": "+case1+"},{\"kf_account\": \"test2@test\",\"status\": 1,\"kf_id\": \"1002\",\"auto_accept\": "+auto2+",\"accepted_case\": "+case2+"}]}";
		};
		System.out.println(result);
		JSONObject jsonObject = JSONObject.fromObject(result);
		List<OnLineKf> kflist = new ArrayList<OnLineKf>();
		if(jsonObject.containsKey("kf_online_list")){
			kflist = new Gson().fromJson(jsonObject.getString("kf_online_list"), new TypeToken<List<OnLineKf>>(){}.getType());
		}
		return kflist;
	}
	
	/**
	 * 获取公众号所有客服人员
	 * @param accessToken
	 * @return
	 */
	public static List<KfInfo> getKfList(String appId){
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		String url = WeChatUrlConfiguration.KF_ALL_URL.replace("ACCESS_TOKEN", accessToken);
		String result = SUtilCommon.httpGetRequest(url);
		System.out.println(result);
//		result = "{\"kf_list\":[{\"kf_account\":\"kf2001@gh_a7ccf31b9f9c\",\"kf_headimgurl\":\"http://mmbiz.qpic.cn/mmbiz/QfooYeGw5iacpapMaCAmAG5ahqjf5ibAQVGFyWmd9W3aO1GPg5KMxzX8NhWdl0eugLksjzibjyXlqnOkuvpicFg9Uw/300?wx_fmt=jpeg\",\"kf_id\":2001,\"kf_nick\":\"测试客服\",\"kf_wx\":\"pankesheng157\"},{\"kf_account\":\"kf2002@gh_a7ccf31b9f9c\",\"kf_headimgurl\":\"http://mmbiz.qpic.cn/mmbiz/QfooYeGw5iacpapMaCAmAG5ahqjf5ibAQVb3tKAyXicC6sq2L6nILmdncFBKeQiaG2JHuHsabibxFYs3iajvyovctYhA/300?wx_fmt=png\",\"kf_id\":2002,\"kf_nick\":\"测试客服2\",\"kf_wx\":\"lisfan\"},{\"kf_account\":\"kf2004@gh_a7ccf31b9f9c\",\"kf_headimgurl\":\"http://mmbiz.qpic.cn/mmbiz/QfooYeGw5iacpapMaCAmAG5ahqjf5ibAQVIbJG48QWkMuD5wic2yYXG1vJsYqRXPIgOKFhbS998giaRu8fBAbXNFuw/300?wx_fmt=png\",\"kf_id\":2004,\"kf_nick\":\"舟岛小鲜\",\"kf_wx\":\"yecool\"}]}";
		JSONObject jsonObject = JSONObject.fromObject(result);
		List<KfInfo> kfList = new ArrayList<KfInfo>();
		if(jsonObject.containsKey("kf_list")){
			kfList = new Gson().fromJson(jsonObject.getString("kf_list"), new TypeToken<List<KfInfo>>(){}.getType());
		}
		return kfList;
	}

	/**
	 * @Description: 组装文本客服消息
	 * @param openId
	 *            消息发送对象
	 * @param content
	 *            文本消息内容
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static String makeTextCustomMessage(String openId, String content) {
		// 对消息内容中的双引号进行转义
		content = content.replace("\"", "\\\"");
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
		return String.format(jsonMsg, openId, content);
	}

	/**
	 * @Description: 组装图片客服消息
	 * @param openId
	 *            消息发送对象
	 * @param mediaId
	 *            媒体文件id
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static String makeImageCustomMessage(String openId, String mediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"image\",\"image\":{\"media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId);
	}

	/**
	 * @Description: 组装语音客服消息
	 * @param openId
	 *            消息发送对象
	 * @param mediaId
	 *            媒体文件id
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static String makeVoiceCustomMessage(String openId, String mediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"voice\",\"voice\":{\"media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId);
	}

	/**
	 * @Description: 组装视频客服消息
	 * @param openId
	 *            消息发送对象
	 * @param mediaId
	 *            媒体文件id
	 * @param thumbMediaId
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static String makeVideoCustomMessage(String openId, String mediaId,
			String thumbMediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"video\",\"video\":{\"media_id\":\"%s\",\"thumb_media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId, thumbMediaId);
	}

	/**
	 * @Description: 组装音乐客服消息
	 * @param openId
	 *            消息发送对象
	 * @param music
	 *            音乐对象
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static String makeMusicCustomMessage(String openId, Music music) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"music\",\"music\":%s}";
		jsonMsg = String.format(jsonMsg, openId, JSONObject.fromObject(music).toString());
		// 将jsonMsg中的thumbmediaid替换为thumb_media_id
		jsonMsg = jsonMsg.replace("thumbmediaid", "thumb_media_id");
		return jsonMsg;
	}

	/**
	 * @Description: 组装图文客服消息
	 * @param openId
	 *            消息发送对象
	 * @param articleList
	 *            图文消息列表
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static String makeNewsCustomMessage(String openId,List<Article> articleList) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"news\",\"news\":{\"articles\":%s}}";
		jsonMsg = String.format(jsonMsg,openId,JSONArray.fromObject(articleList).toString().replaceAll("\"", "\\\""));
		// 将jsonMsg中的picUrl替换为picurl
		jsonMsg = jsonMsg.replace("picUrl", "picurl");
		return jsonMsg;
	}

	/**
	 * @Description: 发送客服消息
	 * @param accessToken
	 *            接口访问凭证
	 * @param jsonMsg
	 *            json格式的客服消息（包括touser、msgtype和消息内容）
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static boolean sendCustomMessage(String appId,String jsonMsg) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		log.info("消息内容：{}", jsonMsg);
		boolean result = false;
		// 拼接请求地址
		String requestUrl =WeChatUrlConfiguration.KF_MESSAGE_CUSTOM_SEND_URL;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 发送客服消息
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",jsonMsg);
		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("客服消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("客服消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
}
