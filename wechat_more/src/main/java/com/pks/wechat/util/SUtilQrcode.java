package com.pks.wechat.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.material.BasicMaterial;
import com.pks.wechat.material.NewsMaterial;
import com.pks.wechat.material.OtherMaterial;
import com.pks.wechat.pojo.WeChatQRCode;

public class SUtilQrcode {
	private static Logger log = LoggerFactory.getLogger(SUtilQrcode.class);
	
	
	/**
	 * @Description: 创建临时带参二维码
	 * @param accessToken
	 *            接口访问凭证
	 * @param expireSeconds
	 *            二维码有效时间，单位为秒，最大不超过1800
	 * @param sceneId
	 *            场景ID
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static WeChatQRCode createTemporaryQRCode(String appId,int expireSeconds, int sceneId) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		WeChatQRCode weixinQRCode = null;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.QRCODE_CREATE;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonMsg = "{\"expire_seconds\": %d, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %d}}}";
		// 创建临时带参二维码
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",
				String.format(jsonMsg, expireSeconds, sceneId));

		if (null != jsonObject) {
			try {
				weixinQRCode = new WeChatQRCode();
				weixinQRCode.setTicket(jsonObject.getString("ticket"));
				weixinQRCode.setExpireSeconds(jsonObject
						.getInt("expire_seconds"));
				log.info("创建临时带参二维码成功 ticket:{} expire_seconds:{}",
						weixinQRCode.getTicket(),
						weixinQRCode.getExpireSeconds());
			} catch (Exception e) {
				weixinQRCode = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("创建临时带参二维码失败 errcode:{} errmsg:{}", errorCode,
						errorMsg);
			}
		}
		return weixinQRCode;
	}

	/**
	 * @Description: 创建永久带参二维码
	 * @param accessToken
	 *            接口访问凭证
	 * @param sceneId
	 *            场景ID
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static String createPermanentQRCode(String appId,int sceneId) {
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		String ticket = null;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.QRCODE_CREATE;
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonMsg = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %d}}}";
		// 创建永久带参二维码
		JSONObject jsonObject = SUtilCommon.httpsRequest(requestUrl, "POST",
				String.format(jsonMsg, sceneId));

		if (null != jsonObject) {
			try {
				ticket = jsonObject.getString("ticket");
				log.info("创建永久带参二维码成功 ticket:{}", ticket);
			} catch (Exception e) {
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("创建永久带参二维码失败 errcode:{} errmsg:{}", errorCode,
						errorMsg);
			}
		}
		return ticket;
	}

	/**
	 * @Description: 根据ticket换取二维码
	 * @param ticket
	 *            二维码ticket
	 * @param savePath
	 *            保存路径
	 * @return
	 * @throws
	 * @author pks
	 * @date 2015-12-22
	 */
	public static String getQRCode(String appId,String savePath) {
		String ticket = SUtilBase.getJsApiTicket(appId).getTicket();
		String filePath = null;
		// 拼接请求地址
		String requestUrl = WeChatUrlConfiguration.QRCODE_SHOW;
		requestUrl = requestUrl.replace("TICKET",
				SUtilCommon.urlEncodeUTF8(ticket));
		try {
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");

			if (!savePath.endsWith("/")) {
				savePath += "/";
			}
			// 将ticket作为文件名
			filePath = savePath + ticket + ".jpg";

			// 将微信服务器返回的输入流写入文件
			BufferedInputStream bis = new BufferedInputStream(
					conn.getInputStream());
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1)
				fos.write(buf, 0, size);
			fos.close();
			bis.close();

			conn.disconnect();
			log.info("根据ticket换取二维码成功，filePath=" + filePath);
		} catch (Exception e) {
			filePath = null;
			log.error("根据ticket换取二维码失败：{}", e);
		}
		return filePath;
	}

	
	public static void main(String args[]) {
		String type = "image";
		JSONObject jsonObject = SUtilMaterial.getMaterailListJson("",type, 0, 50);
		if(jsonObject!=null && jsonObject.containsKey("item")){
			List<BasicMaterial> list = new ArrayList<BasicMaterial>();
			//List<String> itemList = new Gson().fromJson(jsonObject.getString("item"), new TypeToken<List<String>>(){}.getType());
			if("news".equals(type)){
				list = new Gson().fromJson(jsonObject.getString("item"),  new TypeToken<List<NewsMaterial>>(){}.getType());
			}else{
				list = new Gson().fromJson(jsonObject.getString("item"),  new TypeToken<List<OtherMaterial>>(){}.getType());
			}
			System.out.println(list.size());
		}
//			List<String> itemList = new Gson().fromJson(jsonObject.getString("item"), new TypeToken<List<String>>(){}.getType());
//			List<BasicMaterial> list = new ArrayList<BasicMaterial>();
//			for (String itemStr : itemList) {
//				JSONObject itemJson = JSONObject.fromObject(itemStr);
//				if(itemJson.containsKey("content")){
//					String newsJson = itemJson.getJSONObject("content").getString("news_item");
//					List<MaterialNews> news = new Gson().fromJson(newsJson, new TypeToken<List<MaterialNews>>(){}.getType());
//					ImageMaterial im = new ImageMaterial();
//					im.setNews_item(news);
//					im.setMedia_id(itemJson.getString("media_id"));
//					im.setUpdate_time(itemJson.getString("update_time"));
//					list.add(im);
//				}else{
//					OtherMaterial om = new OtherMaterial();
//					om.setMedia_id(itemJson.getString("media_id"));
//					om.setUpdate_time(itemJson.getString("update_time"));
//					om.setName(itemJson.getString("name"));
//					om.setUrl(itemJson.getString("url"));
//					list.add(om);
//				}
//			}
//		}
		
		//		File file = new File("E:/images/404.jpg");
//		WeChatMaterail materail = uploadMaterail(accessToken.getAccess_token(), file, "标题", "描述");
//		System.out.println(materail.getMedia_id()); 
//		
//		// 获取接口访问凭证
//		String accessToken = CommonUtil.getToken("APPID", "APPSECRET")
//				.getAccessToken();
//
//		/**
//		 * 发送客服消息（文本消息）
//		 */
//		// 组装文本客服消息
//		String jsonTextMsg = makeTextCustomMessage(
//				"oEdzejiHCDqafJbz4WNJtWTMbDcE",
//				"点击查看<a href=\"http://blog.csdn.net/lyq8479\">柳峰的博客</a>");
//		// 发送客服消息
//		sendCustomMessage(accessToken, jsonTextMsg);
//
//		/**
//		 * 发送客服消息（图文消息）
//		 */
//		Article article1 = new Article();
//		article1.setTitle("微信上也能斗地主");
//		article1.setDescription("");
//		article1.setPicUrl("http://www.egouji.com/xiaoq/game/doudizhu_big.png");
//		article1.setUrl("http://resource.duopao.com/duopao/games/small_games/weixingame/Doudizhu/doudizhu.htm");
//		Article article2 = new Article();
//		article2.setTitle("傲气雄鹰\n80后不得不玩的经典游戏");
//		article2.setDescription("");
//		article2.setPicUrl("http://www.egouji.com/xiaoq/game/aoqixiongying.png");
//		article2.setUrl("http://resource.duopao.com/duopao/games/small_games/weixingame/Plane/aoqixiongying.html");
//		List<Article> list = new ArrayList<Article>();
//		list.add(article1);
//		list.add(article2);
//		// 组装图文客服消息
//		String jsonNewsMsg = makeNewsCustomMessage(
//				"oEdzejiHCDqafJbz4WNJtWTMbDcE", list);
//		// 发送客服消息
//		sendCustomMessage(accessToken, jsonNewsMsg);
//
//		/**
//		 * 创建临时二维码
//		 */
//		WeChatQRCode weixinQRCode = createTemporaryQRCode(accessToken, 900,
//				111111);
//		// 临时二维码的ticket
//		System.out.println(weixinQRCode.getTicket());
//		// 临时二维码的有效时间
//		System.out.println(weixinQRCode.getExpireSeconds());
//
//		/**
//		 * 根据ticket换取二维码
//		 */
//		String ticket = "gQEg7zoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL2lIVVJ3VmJsTzFsQ0ZuQ0Y1bG5WAAIEW35+UgMEAAAAAA==";
//		String savePath = "G:/download";
//		// 根据ticket换取二维码
//		getQRCode(ticket, savePath);
//
//		/**
//		 * 获取用户信息
//		 */
//		WeChatUserInfo user = getUserInfo(accessToken,
//				"oEdzejiHCDqafJbz4WNJtWTMbDcE");
//		System.out.println("OpenID：" + user.getOpenId());
//		System.out.println("关注状态：" + user.getSubscribe());
//		System.out.println("关注时间：" + user.getSubscribeTime());
//		System.out.println("昵称：" + user.getNickname());
//		System.out.println("性别：" + user.getSex());
//		System.out.println("国家：" + user.getCountry());
//		System.out.println("省份：" + user.getProvince());
//		System.out.println("城市：" + user.getCity());
//		System.out.println("语言：" + user.getLanguage());
//		System.out.println("头像：" + user.getHeadImgUrl());
//
//		/**
//		 * 获取关注者列表
//		 */
//		WeChatUserList weixinUserList = getUserList(accessToken, "");
//		System.out.println("总关注用户数：" + weixinUserList.getTotal());
//		System.out.println("本次获取用户数：" + weixinUserList.getCount());
//		System.out.println("OpenID列表："
//				+ weixinUserList.getOpenIdList().toString());
//		System.out.println("next_openid：" + weixinUserList.getNextOpenId());
//
//		/**
//		 * 查询分组
//		 */
//		List<WeChatGroup> groupList = getGroups(accessToken);
//		// 循环输出各分组信息
//		for (WeChatGroup group : groupList) {
//			System.out.println(String.format("ID：%d 名称：%s 用户数：%d",
//					group.getId(), group.getName(), group.getCount()));
//		}
//
//		/**
//		 * 创建分组
//		 */
//		WeChatGroup group = createGroup(accessToken, "公司员工");
//		System.out.println(String.format("成功创建分组：%s id：%d", group.getName(),
//				group.getId()));
//
//		/**
//		 * 修改分组名
//		 */
//		updateGroup(accessToken, 100, "同事");
//
//		/**
//		 * 移动用户分组
//		 */
//		updateMemberGroup(accessToken, "oEdzejiHCDqafJbz4WNJtWTMbDcE", 100);
//
//		/**
//		 * 上传多媒体文件
//		 */
//		WeChatMedia weixinMedia = uploadMedia(accessToken, "voice",
//				"http://localhost:8080/WeChatmpapi/test.mp3");
//		System.out.println(weixinMedia.getMediaId());
//		System.out.println(weixinMedia.getType());
//		System.out.println(weixinMedia.getCreatedAt());
//
//		/**
//		 * 下载多媒体文件
//		 */
//		getMedia(
//				accessToken,
//				"N7xWhOGYSLWUMPzVcGnxKFbhXeD_lLT5sXxyxDGEsCzWIB2CcUijSeQOYjWLMpcn",
//				"G:/download");
	}
}