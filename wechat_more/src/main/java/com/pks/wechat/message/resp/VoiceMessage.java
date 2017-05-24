package com.pks.wechat.message.resp;

/**
 * @Description: 语音消息
 * @author pks
 * @date 2015-12-16
 */
public class VoiceMessage extends BaseMessage {
	// 语音
	private Voice Voice;

	public Voice getVoice() {
		return Voice;
	}

	public void setVoice(Voice voice) {
		Voice = voice;
	}
}
