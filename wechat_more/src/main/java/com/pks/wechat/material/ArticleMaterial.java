package com.pks.wechat.material;
/**
 * @author pks
 * @version 2019年1月3日
 */
public class ArticleMaterial {
	
	private String thumb_media_id;
	private String author;
	private String title;
	private String content_source_url;
	private String content;
	private String digest;
	private Integer show_cover_pic;
	private Integer need_open_comment;
	private Integer only_fans_can_comment;
	
	public ArticleMaterial() {
		// TODO Auto-generated constructor stub
	}
	
	/***
	 * thumb_media_id	是	图文消息缩略图的media_id，可以在基础支持-上传多媒体文件接口中获得
	 * author	否	图文消息的作者
	 * title	是	图文消息的标题
	 * content_source_url	否	在图文消息页面点击“阅读原文”后的页面，受安全限制，如需跳转Appstore，可以使用itun.es或appsto.re的短链服务，并在短链后增加 #wechat_redirect 后缀。
	 * content	是	图文消息页面的内容，支持HTML标签。具备微信支付权限的公众号，可以使用a标签，其他公众号不能使用，如需插入小程序卡片，可参考下文。
	 * digest	否	图文消息的描述，如本字段为空，则默认抓取正文前64个字
	 * show_cover_pic	否	是否显示封面，1为显示，0为不显示
	 * need_open_comment	否	Uint32 是否打开评论，0不打开，1打开
	 * only_fans_can_comment	否	Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
	 */
	public ArticleMaterial(String thumb_media_id, String author, String title,
			String content_source_url, String content, String digest,
			Integer show_cover_pic, Integer need_open_comment,
			Integer only_fans_can_comment) {
		super();
		this.thumb_media_id = thumb_media_id;
		this.author = author;
		this.title = title;
		this.content_source_url = content_source_url;
		this.content = content;
		this.digest = digest;
		this.show_cover_pic = show_cover_pic;
		this.need_open_comment = need_open_comment;
		this.only_fans_can_comment = only_fans_can_comment;
	}




	public String getThumb_media_id() {
		return thumb_media_id;
	}
	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent_source_url() {
		return content_source_url;
	}
	public void setContent_source_url(String content_source_url) {
		this.content_source_url = content_source_url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public Integer getShow_cover_pic() {
		return show_cover_pic;
	}
	public void setShow_cover_pic(Integer show_cover_pic) {
		this.show_cover_pic = show_cover_pic;
	}
	public Integer getNeed_open_comment() {
		return need_open_comment;
	}
	public void setNeed_open_comment(Integer need_open_comment) {
		this.need_open_comment = need_open_comment;
	}
	public Integer getOnly_fans_can_comment() {
		return only_fans_can_comment;
	}
	public void setOnly_fans_can_comment(Integer only_fans_can_comment) {
		this.only_fans_can_comment = only_fans_can_comment;
	}
	
}

