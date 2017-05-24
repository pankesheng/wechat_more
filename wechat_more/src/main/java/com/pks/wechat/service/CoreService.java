package com.pks.wechat.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.pks.wechat.message.resp.KfMessage;
import com.pks.wechat.message.resp.NewsMessage;
import com.pks.wechat.message.resp.TextMessage;
import com.pks.wechat.message.resp.TransInfo;
import com.pks.wechat.pojo.AccessToken;
import com.pks.wechat.pojo.KfInfo;
import com.pks.wechat.pojo.OnLineKf;
import com.pks.wechat.pojo.WeChatUserInfo;
import com.pks.wechat.session.QuartzManager;
import com.pks.wechat.session.Session;
import com.pks.wechat.session.SessionList;
import com.pks.wechat.util.SUtilBase;
import com.pks.wechat.util.SUtilCustomer;
import com.pks.wechat.util.SUtilMessage;
import com.pks.wechat.util.SUtilUser;

/** 
 * 核心服务类 
 *  
 */  
public class CoreService {
	/** 
     * 处理微信发来的请求 
     *  
     * @param request 
     * @return 
     */  
	
	@SuppressWarnings("unused")
	public static String processRequest(HttpServletRequest request) {
		//TODO
		String appId = "" ;
		
		String respMessage = null;  
        try {  
            // 默认返回的文本消息内容  
            String respContent = "请求处理异常，请稍候尝试！";  
  
            // xml请求解析  
            Map<String, String> requestMap = SUtilMessage.parseXml(request);  
  
            // 发送方帐号（open_id）  
            String fromUserName = requestMap.get("FromUserName");  
            // 公众帐号  
            String toUserName = requestMap.get("ToUserName");  
            // 消息类型  
            String msgType = requestMap.get("MsgType");  
            // 创建文本消息
            TextMessage textMessage = new TextMessage();  
            textMessage.setToUserName(fromUserName);  
            textMessage.setFromUserName(toUserName);  
            textMessage.setCreateTime(new Date().getTime()); 
            textMessage.setMsgType(SUtilMessage.RESP_MESSAGE_TYPE_TEXT);  
            
            // 创建客服消息
            KfMessage kfMessage = new KfMessage();  
            kfMessage.setToUserName(fromUserName);  
            kfMessage.setFromUserName(toUserName);  
            kfMessage.setCreateTime(new Date().getTime()); 
            kfMessage.setMsgType(SUtilMessage.RESP_MESSAGE_TYPE_TEXT);  
            // 创建图文消息  
            NewsMessage newsMessage = new NewsMessage(); 
            newsMessage.setToUserName(fromUserName);  
            newsMessage.setFromUserName(toUserName);  
            newsMessage.setCreateTime(new Date().getTime());
            
            // 文本消息  
            if (msgType.equals(SUtilMessage.REQ_MESSAGE_TYPE_TEXT)) {  
                String content = requestMap.get("Content");
                if(SessionList.search(fromUserName, Session.phase_kf)>-1){
	            	if(content.equals("0") && "random".equals(SessionList.getSession(fromUserName,Session.phase_kf,content))){
	            		kfMessage.setMsgType(SUtilMessage.RESP_MESSAGE_TYPE_SERVICE);
	                	QuartzManager.removeJob("phase"+Session.phase_kf+fromUserName);
	                    respMessage = SUtilMessage.kfMessageToXml(kfMessage);
	            	}else{
		            	OnLineKf kf = (OnLineKf) SessionList.getSession(fromUserName,Session.phase_kf,content);
		                if(kf!=null){
	//	                	if(kf.getAcceped_case()>=kf.getAuto_accept()){
	//	                		textMessage.setContent("对不起，该客服接入人员已满，请稍后刷新在线客服列表重新选择。");
	//	                		respMessage = MessageUtil.textMessageToXml(textMessage);
	//	                	}else{
			                	kfMessage.setMsgType(SUtilMessage.RESP_MESSAGE_TYPE_SERVICE);
			                	TransInfo transInfo = new TransInfo();
			                	transInfo.setKfAccount(kf.getKf_account());
			                	kfMessage.setTransInfo(transInfo);
			                	QuartzManager.removeJob("phase"+Session.phase_kf+fromUserName);
			                    respMessage = SUtilMessage.kfMessageToXml(kfMessage);
	//	                	}
		                }else{
		                	textMessage.setContent("您想要接入的客服不存在或请求已超时！");
		                	SessionList.resetQuartz(fromUserName,Session.phase_kf);
		                	respMessage = SUtilMessage.textMessageToXml(textMessage);
		                }
	            	}
	            }else{
	            	textMessage.setContent(content);
                	SessionList.resetQuartz(fromUserName,Session.phase_kf);
                	respMessage = SUtilMessage.textMessageToXml(textMessage);
	            }
            }  
            // 图片消息  
            else if (msgType.equals(SUtilMessage.REQ_MESSAGE_TYPE_IMAGE)) {

            }  
            // 地理位置消息  
            else if (msgType.equals(SUtilMessage.REQ_MESSAGE_TYPE_LOCATION)) {  

            }  
            // 链接消息  
            else if (msgType.equals(SUtilMessage.REQ_MESSAGE_TYPE_LINK)) {

            }  
            // 音频消息  
            else if (msgType.equals(SUtilMessage.REQ_MESSAGE_TYPE_VOICE)) {  

            }  
            // 事件推送  
            else if (msgType.equals(SUtilMessage.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型  
                String eventType = requestMap.get("Event");
                // 订阅  
                if (eventType.equals(SUtilMessage.EVENT_TYPE_SUBSCRIBE)) {   
                	WeChatUserInfo userInfo = SUtilUser.getUserInfo(appId,fromUserName);
                    textMessage.setMsgType(SUtilMessage.RESP_MESSAGE_TYPE_TEXT);  
                    textMessage.setContent("欢迎你关注了该测试号。。。。。。");  
                    respMessage = SUtilMessage.textMessageToXml(textMessage);
                }  
                // 取消订阅  
                else if (eventType.equals(SUtilMessage.EVENT_TYPE_UNSUBSCRIBE)) {  
                    // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息  
                }  
                // 自定义菜单点击事件  
                else if (eventType.equals(SUtilMessage.EVENT_TYPE_CLICK)) {  
                	// 事件KEY值，与创建自定义菜单时指定的KEY值对应  
                	String eventKey = requestMap.get("EventKey");
                    textMessage.setMsgType(SUtilMessage.RESP_MESSAGE_TYPE_TEXT);  
                    if(eventKey.equals("zxkf")){
                    	StringBuilder sb = new StringBuilder();
                    	//TODO
                    	AccessToken accessToken = SUtilBase.getAccessToken(appId);
                    	List<OnLineKf> list = SUtilCustomer.getOnLineKfList(appId);
            			List<KfInfo> kfs = SUtilCustomer.getKfList(appId);
            			if(list!=null && list.size()>0){
            				sb.append("在线客服列表：\n");
                        	SessionList.setSeesion(fromUserName,Session.phase_kf);
                        	if(list.size()>1){
	                        	sb.append("0、随机分配\n");
								SessionList.setSeesion(fromUserName,Session.phase_kf,String.valueOf(0), "random");
                        	}
                        	for (int i = 0; i < list.size(); i++) {
								OnLineKf obj = list.get(i);
								String nickname = obj.getKf_id();
								for (KfInfo kf : kfs) {
									if(obj.getKf_account().equals(kf.getKf_account())){
										nickname = kf.getKf_nick();
										break;
									}
								}
//								String state = "可接入" ;
//								if(obj.getAcceped_case()>=obj.getAuto_accept()){
//									state = "已满";
//								}
								sb.append((i+1)+"、"+nickname+"【"+obj.getKf_id()+"】\n");
								SessionList.setSeesion(fromUserName,Session.phase_kf,String.valueOf(i+1), obj);
							}
            				sb.append("请输入对应的序号接入客服,3分钟内输入有效。");
            			}else{
            				sb.append("非常抱歉，暂时没有客服人员在线。");
            			}
                    	textMessage.setContent(sb.toString());
                    }
                    respMessage = SUtilMessage.textMessageToXml(textMessage);
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return respMessage;  
    
	}
	
	/** 
	 * 判断是否是QQ表情 
	 *  
	 * @param content 
	 * @return 
	 */  
	public static boolean isQqFace(String content) {  
	    boolean result = false;  
	  
	    // 判断QQ表情的正则表达式  
	    String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";  
	    Pattern p = Pattern.compile(qqfaceRegex);  
	    Matcher m = p.matcher(content);  
	    if (m.matches()) {  
	        result = true;  
	    }  
	    return result;
	}  

}
