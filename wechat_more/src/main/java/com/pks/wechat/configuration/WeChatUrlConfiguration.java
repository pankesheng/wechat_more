package com.pks.wechat.configuration;

/**
 * @Description 微信请求接口连接管理类
 * @author pks
 * @date 2017年4月11日
 */
public class WeChatUrlConfiguration {
	/*
	 * 网页授权：
	 * 1、引导打开页面 load_url
	 * 2、通过返回code 换取access_token 与基础接口 access_token不同 accesstoken_url  
	 * 3、刷新access_token（如有需要）  refreshtoken_url
	 * 4、拉取用户信息(需scope为 snsapi_userinfo)
	 */
	/**网页授权引导页地址*/
	public final static String OAUTH2_LOAD_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
	/**获取网页授权凭证 (GET)*/
	public final static String OAUTH2_ACCESSTOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	/**刷新网页授权凭证 (GET)*/
	public final static String OAUTH2_REFRESHTOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
	/**验证网页授权凭证(GET)*/
	public final static String OAUTH_CHECK_URL = "https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID";
	/**拉取用户信息(需scope为 snsapi_userinfo) (GET) */
	public final static String SNS_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	
	/*
	 * 用户相关 
	 */
	/**根据openid 获取用户信息 (GET)*/
	public final static String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	/**获取用户列表信息(GET)  默认从头开始拉取 每次最多10000条，在获取下一批的时候 添加参数&next_openid=NEXT_OPENID */
	public final static String USER_LIST_URL = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
	/**批量获取用户基本信息 POST */
	public final static String USER_BATCHGET = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=ACCESS_TOKEN";
	/*
	 * 分组相关
	 */
	/**获取分组列表*/
	public final static String GROUP_LIST = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=ACCESS_TOKEN";
	/**创建分组*/
	public final static String GROUP_CREATE = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=ACCESS_TOKEN";
	/**修改分组名*/
	public final static String GROUP_UPDATE = "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=ACCESS_TOKEN";
	/**查询用户所在分组 POST*/
	public final static String GROUP_OPENID = "https://api.weixin.qq.com/cgi-bin/groups/getid?access_token=ACCESS_TOKEN";
	/**修改用户分组*/
	public final static String GROUP_MEMBERS_UPDATE = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=ACCESS_TOKEN";
	/**批量移动用户分组*/
	public final static String GROUP_MEMBERS_BATCHUPDATE = "https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate?access_token=ACCESS_TOKEN";
	
	/*
	 * 媒体相关
	 */
	/**上传媒体文件  POST*/
	public final static String FILE_MEDIA_UPLOAD = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	/**下载媒体文件 GET*/
	public final static String FILE_MEDIA_GET = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
	
	/*
	 * 微信基础接口地址
	 */
	/**获取token接口(GET)*/
	public final static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	/**oauth2授权接口(GET)*/
	public final static String OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	/**刷新access_token接口（GET）*/
	public final static String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
	/** 菜单创建接口（POST）*/
	public final static String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	/** 菜单查询（GET）*/
	public final static String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	/** 菜单删除（GET）*/
	public final static String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	/**获取jsapi_ticket (GET)*/
	public final static String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
	
	/*
	 * 微信支付接口地址
	 */
	/**微信支付统一接口(POST)*/
	public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/**微信退款接口(POST)*/
	public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	/**订单查询接口(POST)*/
	public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	/**关闭订单接口(POST)*/
	public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
	/**退款查询接口(POST)*/
	public final static String CHECK_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
	/**对账单接口(POST)*/
	public final static String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
	/**短链接转换接口(POST)*/
	public final static String SHORT_URL = "https://api.mch.weixin.qq.com/tools/shorturl";
	/**接口调用上报接口(POST)*/
	public final static String REPORT_URL = "https://api.mch.weixin.qq.com/payitil/report";
	
	/*
	 * 客服相关
	 */
	/**获取在线客服列表信息(GET)*/
	public final static String KF_ONLINE_LIST_URL = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=ACCESS_TOKEN";
	/**获取所有客服账号(GET)*/
	public final static String KF_ALL_URL = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=ACCESS_TOKEN";
	/**添加客服账号(POST)*/
	public final static String KF_ADD_URL = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=ACCESS_TOKEN";
	/**修改客服账号(POST)*/
	public final static String KF_UPDATE_URL = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token=ACCESS_TOKEN";
	/**删除客服账号(GET)*/
	public final static String KF_DELETE_URL = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token=ACCESS_TOKEN";
	/**设置客服头像(POST/FORM)*/
	public final static String KF_UPLOADHEADIMG_URL = "http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=ACCESS_TOKEN&kf_account=KFACCOUNT";
	/**发送客服消息(POST)*/
	public final static String KF_MESSAGE_CUSTOM_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
	/**发送模板消息*/
	public final static String TEMP_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	
	/*
	 * 素材管理
	 */
	
	/**上传图文消息内的图片获取URL(POST)*/
	public final static String MEDIA_UPLOADIMG = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
	public final static String MATERIAL_ADD_TEMP_MATERIAL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	/**新增素材信息(POST)*/
	public final static String MATERIAL_ADD_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN";
	/**新增永久图文素材 (POST)*/
	public final static String MATERIAL_ADD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN";
	/**获取某个素材信息(POST)*/
	public final static String MATERIAL_GET_URL = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";
	/**获取素材信息列表(POST) */
	public final static String MATERIAL_BATCHGET_URL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN";
	
	
	/**创建临时带参二维码 ticket
	 * 临时
	 * 		POST数据例子：{"expire_seconds": 604800, "action_name": "QR_SCENE", "action_info": {"scene": {"scene_id": 123}}}
	 * 永久
	 * 		POST数据例子：{"action_name": "QR_LIMIT_SCENE", "action_info": {"scene": {"scene_id": 123}}}
			或者也可以使用以下POST数据创建字符串形式的二维码参数：
			{"action_name": "QR_LIMIT_STR_SCENE", "action_info": {"scene": {"scene_str": "123"}}}
	 * */
	public final static String  QRCODE_CREATE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	
	/**
	 * 通过ticket换取二维码
	 * TICKET记得进行UrlEncode
	 */
	public final static String QRCODE_SHOW = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
	
	/**
	 * 获取微信服务ip地址  GET
	 */
	public final static String SERVER_IPS = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=ACCESS_TOKEN";
	
	
	/*
	 * 群发
	 */
	/** 上传图文消息素材【订阅号与服务号认证后均可用】 */
	public final static String MATERIAL_UPLOADNEWS_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN";
	/** 根据标签进行群发【订阅号与服务号认证后均可用】 */
	public final static String SENDALL_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";
	/** 根据OpenID列表群发【订阅号不可用，服务号认证后可用】*/
	public final static String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=ACCESS_TOKEN";
	
}
