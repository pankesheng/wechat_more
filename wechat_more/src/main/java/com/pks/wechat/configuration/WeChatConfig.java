package com.pks.wechat.configuration;

import java.io.Serializable;

import com.pks.wechat.pojo.AccessToken;
import com.pks.wechat.pojo.JsApiTicket;

/**
 * @author pks
 * @version 2017年5月19日
 */
public class WeChatConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4573058621685137223L;
	// 与开发模式接口配置信息中的Token保持一致
	private String token ;
	// 用户标识֤
	private String appId ;
	// 用户标识密钥
	private String appSecret ;
	
	/**储存接口令牌 accessToken 由于accesstoken每天只能获取2000次，为了节省次数在项目启动的时候获取一次存储下来在即将失效的时候重新获取*/
	private AccessToken accessToken ;
	/**储存jsapi_ticket 游湖获取jsapi_ticket 接口限制，进行保存*/
	private JsApiTicket jsapi_ticket;
	
	private String mch_id ;//商户号
	private String api_key ;//API密钥
	private String sign_type ;//签名加密方式
	private String cert_path ;//微信支付证书存放路径地址
	//微信支付统一接口的回调action
	private String notify_url ;
	//真实域名
//	private String domain_url ;
	
	private String pay_action ;
	
	public WeChatConfig() {
		// TODO Auto-generated constructor stub
	}

	public WeChatConfig(String token, String appId, String appSecret,
			String mch_id, String api_key, String cert_path,
			String notify_url, String pay_action) {
		super();
		this.token = token;
		this.appId = appId;
		this.appSecret = appSecret;
		this.mch_id = mch_id;
		this.api_key = api_key;
		this.cert_path = cert_path;
		this.notify_url = notify_url;
//		this.domain_url = domain_url;
		this.pay_action = pay_action;
		this.sign_type = "SHA1";
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}

	public JsApiTicket getJsapi_ticket() {
		return jsapi_ticket;
	}

	public void setJsapi_ticket(JsApiTicket jsapi_ticket) {
		this.jsapi_ticket = jsapi_ticket;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getApi_key() {
		return api_key;
	}

	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getCert_path() {
		return cert_path;
	}

	public void setCert_path(String cert_path) {
		this.cert_path = cert_path;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

//	public String getDomain_url() {
//		return domain_url;
//	}
//
//	public void setDomain_url(String domain_url) {
//		this.domain_url = domain_url;
//	}

	public String getPay_action() {
		return pay_action;
	}

	public void setPay_action(String pay_action) {
		this.pay_action = pay_action;
	}
}

