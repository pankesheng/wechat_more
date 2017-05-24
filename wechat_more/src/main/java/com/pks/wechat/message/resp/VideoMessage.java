package com.pks.wechat.message.resp;

/**
 * @Description: 视频消息
 * @author pks
 * @date 2015-12-16
 */
public class VideoMessage extends BaseMessage {

	// 视频
	private Video Video;

	public Video getVideo() {
		return Video;
	}

	public void setVideo(Video video) {
		Video = video;
	}
}
