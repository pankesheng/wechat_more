package com.pks.wechat.pojo;
/**
 * @author pks
 * @version 2017年4月11日
 */
public class UserBatchParamDto {
	
	/**
	 * 简体
	 */
	public final static String LANG_ZH_CN = "zh_CH";
	/**
	 * 繁体
	 */
	public final static String LANG_ZH_TW = "zh_TW";
	/**
	 * 英语
	 */
	public final static String LANG_EN = "en";
	
	private String openid;
	private String lang;
	
	public UserBatchParamDto() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 默认lang 为zh_CN
	 * @param openid
	 */
	public UserBatchParamDto(String openid) {
		super();
		this.openid = openid;
	}

	public UserBatchParamDto(String openid, String lang) {
		super();
		this.openid = openid;
		this.lang = lang;
	}

	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	
	
	

}

