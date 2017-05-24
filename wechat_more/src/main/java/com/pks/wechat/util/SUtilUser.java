package com.pks.wechat.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.pojo.AccessToken;
import com.pks.wechat.pojo.UserBatchParamDto;
import com.pks.wechat.pojo.WeChatUserInfo;
import com.pks.wechat.pojo.WeChatUserList;

/**
 * @author pks
 * @version 2017年4月11日
 */
public class SUtilUser {
	
	private static Logger log = LoggerFactory.getLogger(SUtilUser.class);
	
	
	/**
	 * @Description: 获取用户信息
	 * @param accessToken
	 *            接口访问凭证
	 * @param openId
	 *            用户标识
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static WeChatUserInfo getUserInfo(String appId,String openId) {
		AccessToken accessToken = SUtilBase.getAccessToken(appId);
		WeChatUserInfo weixinUserInfo = null;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.USER_INFO_URL;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken.getAccess_token()).replace(
				"OPENID", openId);
		// 获取用户信息
		JSONObject jsonObject = SUtilCommon
				.httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject) {
			try {
				weixinUserInfo = new WeChatUserInfo();
				// 用户的标识
				weixinUserInfo.setOpenId(jsonObject.getString("openid"));
				// 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
				if(jsonObject.containsKey("subscribe")) weixinUserInfo.setSubscribe(jsonObject.getInt("subscribe"));
				// 用户关注时间
				if(jsonObject.containsKey("subscribe_time")) weixinUserInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
				// 昵称
				if(jsonObject.containsKey("nickname")) weixinUserInfo.setNickname(jsonObject.getString("nickname"));
				// 用户的性别（1是男性，2是女性，0是未知）
				if(jsonObject.containsKey("sex")) weixinUserInfo.setSex(jsonObject.getInt("sex"));
				// 用户所在国家
				if(jsonObject.containsKey("country")) weixinUserInfo.setCountry(jsonObject.getString("country"));
				// 用户所在省份
				if(jsonObject.containsKey("province")) weixinUserInfo.setProvince(jsonObject.getString("province"));
				// 用户所在城市
				if(jsonObject.containsKey("city")) weixinUserInfo.setCity(jsonObject.getString("city"));
				// 用户的语言，简体中文为zh_CN
				if(jsonObject.containsKey("language")) weixinUserInfo.setLanguage(jsonObject.getString("language"));
				// 用户头像
				if(jsonObject.containsKey("headimgurl")) weixinUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
				if(jsonObject.containsKey("unionid")) weixinUserInfo.setUnionid(jsonObject.getString("unionid"));
				if(jsonObject.containsKey("remark")) weixinUserInfo.setRemark(jsonObject.getString("remark"));
				if(jsonObject.containsKey("groupid")) weixinUserInfo.setGroupid(jsonObject.getInt("groupid"));
				
				
			} catch (Exception e) {
				if (0 == weixinUserInfo.getSubscribe()) {
					log.error("用户{}已取消关注", weixinUserInfo.getOpenId());
				} else {
					int errorCode = jsonObject.getInt("errcode");
					String errorMsg = jsonObject.getString("errmsg");
					log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode,errorMsg);
				}
			}
		}
		return weixinUserInfo;
	}

	/**
	 * @Description: 获取关注者列表
	 * @param accessToken
	 *            调用接口凭证
	 * @param nextOpenId
	 *            第一个拉取的openId，不填默认从头开始拉取
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static WeChatUserList getUserList(String appId,String nextOpenId) {
		AccessToken accessToken = SUtilBase.getAccessToken(appId);
		WeChatUserList weixinUserList = null;
		if (null == nextOpenId) {
			nextOpenId = "";
		}
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.USER_LIST_URL;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken.getAccess_token()).replace("NEXT_OPENID", nextOpenId);
		// 获取关注者列表
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				weixinUserList = new WeChatUserList();
				weixinUserList.setTotal(jsonObject.getInt("total"));
				weixinUserList.setCount(jsonObject.getInt("count"));
				weixinUserList.setNextOpenId(jsonObject.getString("next_openid"));
				JSONObject dataObject = (JSONObject) jsonObject.get("data");
				weixinUserList.setOpenIdList(JSONArray.toList(dataObject.getJSONArray("openid"), List.class));
			} catch (JSONException e) {
				weixinUserList = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("获取关注者列表失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return weixinUserList;
	}
	
	/**
	 * 批量获取用户基本信息
	 * @param user_list
	 * @return
	 */
	public static List<WeChatUserInfo> batchUsers(String appId,List<UserBatchParamDto> user_list) {
		List<WeChatUserInfo> result = null;
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.USER_BATCHGET;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		StringBuilder sb = new StringBuilder("{\"user_list\":[");
		for (int i = 0; i < user_list.size(); i++) {
			if(i>0) sb.append(",");
			sb.append("{\"openid\":\""+user_list.get(i).getOpenid()+"\",\"lang\":\""+user_list.get(i).getLang()+"\"}");
		}
		sb.append("]}");
		
		String jsonData = sb.toString();
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",String.format(jsonData));

		if (null != jsonObject) {
			try {
				if(jsonObject.containsKey("user_info_list")){
					result = new ArrayList<WeChatUserInfo>();
					JSONArray array = jsonObject.getJSONArray("user_info_list");
					for (int i = 0; i < array.size(); i++) {
						JSONObject j = array.getJSONObject(i);
						WeChatUserInfo info = new WeChatUserInfo();
						info.setOpenId(j.getString("openid"));
						if(j.containsKey("subscribe"))info.setSubscribe(j.getInt("subscribe"));
						if(j.containsKey("subscribe_time"))info.setSubscribeTime(j.getString("subscribe_time"));
						if(j.containsKey("nickname"))info.setNickname(j.getString("nickname"));
						if(j.containsKey("sex"))info.setSex(j.getInt("sex"));
						if(j.containsKey("country"))info.setCountry(j.getString("country"));
						if(j.containsKey("province"))info.setProvince(j.getString("province"));
						if(j.containsKey("city"))info.setCity(j.getString("city"));
						if(j.containsKey("language"))info.setLanguage(j.getString("language"));
						if(j.containsKey("headimgurl"))info.setHeadImgUrl(j.getString("headimgurl"));
						if(j.containsKey("unionid")) info.setUnionid(j.getString("unionid"));
						if(j.containsKey("remark")) info.setRemark(j.getString("remark"));
						if(j.containsKey("groupid")) info.setGroupid(j.getInt("groupid"));
						result.add(info);
					}
				}
			} catch (JSONException e) {
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("批量查询用户基本信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
	
}

