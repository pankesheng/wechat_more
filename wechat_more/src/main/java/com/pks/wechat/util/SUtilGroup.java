package com.pks.wechat.util;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.pojo.WeChatGroup;

/** 
 * 分组工具类
 * @author pks
 * @version 2017年4月11日
 */
public class SUtilGroup {
	private static Logger log = LoggerFactory.getLogger(SUtilGroup.class);
	
	
	
	/**
	 * @Description: 查询分组
	 * @param accessToken
	 *            调用接口凭证
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static List<WeChatGroup> getGroups(String appId) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		List<WeChatGroup> weixinGroupList = null;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.GROUP_LIST;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 查询分组
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject) {
			try {
				weixinGroupList = JSONArray.toList(jsonObject.getJSONArray("groups"), WeChatGroup.class);
			} catch (JSONException e) {
				weixinGroupList = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("查询分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return weixinGroupList;
	}

	/**
	 * @Description: 创建分组
	 * @param accessToken
	 *            接口访问凭证
	 * @param groupName
	 *            分组名称
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static WeChatGroup createGroup(String appId,String groupName) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		WeChatGroup weixinGroup = null;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.GROUP_CREATE;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonData = "{\"group\" : {\"name\" : \"%s\"}}";
		// 创建分组
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",
				String.format(jsonData, groupName));

		if (null != jsonObject) {
			try {
				weixinGroup = new WeChatGroup();
				weixinGroup.setId(jsonObject.getJSONObject("group")
						.getInt("id"));
				weixinGroup.setName(jsonObject.getJSONObject("group")
						.getString("name"));
			} catch (JSONException e) {
				weixinGroup = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("创建分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return weixinGroup;
	}

	/**
	 * @Description: 修改分组名
	 * @param accessToken
	 *            接口访问凭证
	 * @param groupId
	 *            分组id
	 * @param groupName
	 *            修改后的分组名
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static boolean updateGroup(String appId,int groupId,String groupName) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		boolean result = false;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.GROUP_UPDATE;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonData = "{\"group\": {\"id\": %d, \"name\": \"%s\"}}";
		// 修改分组名
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",
				String.format(jsonData, groupId, groupName));
		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("修改分组名成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("修改分组名失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
	
	/**
	 * @Description:   查询用户所在分组
	 * @param accessToken
	 *            接口访问凭证
	 * @param openid
	 *            用户openid
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static Integer usergroup(String appId,String openid) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.GROUP_OPENID;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonData = "{\"openid\": \""+openid+"\"}";
		// 修改分组名
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",String.format(jsonData));
		if (null != jsonObject) {
			if(jsonObject.containsKey("groupid")){
				return jsonObject.getInt("groupid");
			}
		}
		return null;
	}
	
	
	

	/**
	 * @Description: 移动用户分组
	 * @param accessToken
	 *            接口访问凭证
	 * @param openId
	 *            用户标识
	 * @param groupId
	 *            分组id
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static boolean updateMemberGroup(String appId,String openId,int groupId) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		boolean result = false;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.GROUP_MEMBERS_UPDATE;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonData = "{\"openid\":\"%s\",\"to_groupid\":%d}";
		// 移动用户分组
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",
				String.format(jsonData, openId, groupId));

		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("移动用户分组成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("移动用户分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
	
	/**
	 * @Description: 批量移动用户分组
	 * @param accessToken
	 *            接口访问凭证
	 * @param openIds
	 *            用户标识集合
	 * @param groupId
	 *            分组id
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static boolean batchUpdateMemberGroup(String appId,List<String> openIds,int groupId) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		boolean result = false;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.GROUP_MEMBERS_BATCHUPDATE;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		StringBuilder sb = new StringBuilder("{\"openid_list\":[");
		for (int i = 0; i < openIds.size(); i++) {
			if(i>0) sb.append(",");
			sb.append("\""+openIds.get(i)+"\"");
		}
		sb.append("],\"to_groupid\":"+groupId+"}");
		String jsonData = sb.toString();
		// 移动用户分组
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",String.format(jsonData));

		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("移动用户分组成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("移动用户分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return result;
	}
	
}

