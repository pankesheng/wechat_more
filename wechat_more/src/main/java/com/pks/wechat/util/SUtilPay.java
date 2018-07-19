package com.pks.wechat.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pks.wechat.configuration.WeChatConfigs;
import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.pojo.WeChatPayResult;
/**
 * 支付 接口
 * @author pks
 *
 */
public class SUtilPay {
	
	private static final Logger log = LoggerFactory.getLogger(SUtilPay.class);
	
	public static String payurl(String appId,String backUri,String state,Map<String, Object> params) throws UnsupportedEncodingException{
		String paramsurl = SUtilCommon.paramstourl(params);
		backUri = backUri + "?"+paramsurl;
		// URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
		backUri = URLEncoder.encode(backUri,"UTF-8");
		String scope = "snsapi_userinfo";
		String appid = appId;
		String url = WeChatUrlConfiguration.OAUTH2_LOAD_URL.replace("APPID", appid).replace("REDIRECT_URI", backUri).replace("SCOPE", scope).replace("STATE", state);
		return url;
	}

	@SuppressWarnings({ "static-access", "unused" })
	public static String topay(RequestHandler reqHandler,String appId,String orderNo,String money,String code,String userId,String descr,String ip,String url){
		float sessionmoney = Float.parseFloat(money);
		String finalmoney = String.format("%.2f", sessionmoney);
		finalmoney = finalmoney.replace(".", "");
		// 商户相关资料
		String appid = appId;
		String appsecret = WeChatConfigs.getConfig(appId).getAppSecret();
		String mch_id = WeChatConfigs.getConfig(appId).getMch_id();
		String api_key = WeChatConfigs.getConfig(appId).getApi_key();

		String openId = "";
		String URL = WeChatUrlConfiguration.OAUTH2_URL.replace("APPID", appid).replace("SECRET", appsecret).replace("CODE", code);
		HttpResponse temp = HttpConnect.getInstance().doGetStr(URL);
		String tempValue = "";
		if (temp == null) {
			return "";
		} else {
			try {
				tempValue = temp.getStringResult();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jsonObj = JSONObject.fromObject(tempValue);
			if (jsonObj.containsKey("errcode")) {
				return "";
			}
			openId = jsonObj.getString("openid");
		}

		// 获取openId后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
		String currTime = SUtilCommon.getCurrTime();
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = SUtilCommon.buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		String strReq = strTime + strRandom;
		// 子商户号 非必输
		// String sub_mch_id="";
		// 设备号 非必输
		//String device_info = "";
		// 随机数
		String nonce_str = strReq;
		// 商品描述
		// String body = describe;

		// 商品描述根据情况修改
		String body = descr;
		// 附加数据
		String attach = userId;
		// 商户订单号
		String out_trade_no = orderNo;
		int intMoney = Integer.parseInt(finalmoney);

		// 总金额以分为单位，不带小数点
		int total_fee = intMoney;
		// 订 单 生 成 时 间 非必输
		// String time_start ="";
		// 订单失效时间 非必输
		// String time_expire = "";
		// 商品标记 非必输
		// String goods_tag = "";

		// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
		String notify_url = WeChatConfigs.getConfig(appId).getNotify_url();
		String trade_type = "JSAPI";
		String openid = openId;
		// 非必输
		// String product_id = "";
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("attach", attach);
		packageParams.put("out_trade_no", out_trade_no);
		// 这里写的金额为1 分到时修改
		packageParams.put("total_fee", String.valueOf(total_fee));
		// packageParams.put("total_fee", "finalmoney");
		packageParams.put("spbill_create_ip", ip);
		packageParams.put("notify_url", notify_url);
		packageParams.put("trade_type", trade_type);
		packageParams.put("openid", openid);

		reqHandler.init(appid, appsecret, api_key);
		
		String sign = reqHandler.createSign(packageParams);
		String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
				+ mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
				+ "</nonce_str>" + "<sign>" + sign + "</sign>"
				+ "<body>" + body + "</body>" + "<attach>" + attach
				+ "</attach>" + "<out_trade_no>" + out_trade_no
				+ "</out_trade_no>" + "<attach>"
				+ attach
				+ "</attach>"
				+
				// 金额，这里写的1 分到时修改
				"<total_fee>"
				+ total_fee
				+ "</total_fee>"
				+
				// "<total_fee>"+finalmoney+"</total_fee>"+
				"<spbill_create_ip>" + ip + "</spbill_create_ip>"
				+ "<notify_url>" + notify_url + "</notify_url>"
				+ "<trade_type>" + trade_type + "</trade_type>" + "<openid>"
				+ openid + "</openid>" + "</xml>";
//		System.out.println(xml);
		String allParameters = "";
		try {
			allParameters = reqHandler.genPackage(packageParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String createOrderURL = WeChatUrlConfiguration.UNIFIED_ORDER_URL;
		String prepay_id = "";
		try {
			prepay_id = new GetWxOrderno().getPayNo(createOrderURL, xml);
			System.out.println("prepay_id:"+prepay_id);
			if (prepay_id.equals("")) {
				log.error("统一支付接口获取预支付订单出错！");
				return "";
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
		String appid2 = appid;
		String timestamp = SUtilCommon.getTimeStamp();
		String nonceStr2 = nonce_str;
		String prepay_id2 = "prepay_id=" + prepay_id;
		String packages = prepay_id2;
		finalpackage.put("appId", appid2);
		finalpackage.put("timeStamp", timestamp);
		finalpackage.put("nonceStr", nonceStr2);
		finalpackage.put("package", packages);
		finalpackage.put("signType", "MD5");
		String finalsign = reqHandler.createSign(finalpackage);
		return url+"?appid=" + appid2 + "&timeStamp=" + timestamp + "&nonceStr=" + nonceStr2 + "&package=" + packages + "&sign=" + finalsign+"&orderNo="+orderNo;
	}

	/**
	 * 退款
	 * @param response
	 * @param out_trade_no
	 * @param total_fee1
	 * @param refund_fee1
	 * @return
	 * @throws SDKRuntimeException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public static boolean refund(HttpServletResponse response,String appId,String out_trade_no,Double total_fee1,Double refund_fee1) throws SDKRuntimeException {
		String out_refund_no = SUtilCommon.getUUID();// 退款单号，随机生成 ，但长度应该跟文档一样（32位）(卖家信息校验不一致，请核实后再试)
		int total_fee = (int) (total_fee1*100);//订单的总金额,以分为单位（填错了貌似提示：同一个out_refund_no退款金额要一致）
		int refund_fee = (int) (refund_fee1*100);;// 退款金额，以分为单位（填错了貌似提示：同一个out_refund_no退款金额要一致）
		String nonce_str = SUtilCommon.buildRandom(4) + "";// 随机字符串
		//微信公众平台文档：“基本配置”--》“开发者ID”
		String appsecret = WeChatConfigs.getConfig(appId).getAppSecret();
		//商户号
		//微信公众平台文档：“微信支付”--》“商户信息”--》“商户号”，将该值赋值给partner
		String mch_id = WeChatConfigs.getConfig(appId).getMch_id();
		String op_user_id = mch_id;//就是MCHID
		//微信公众平台："微信支付"--》“商户信息”--》“微信支付商户平台”（登录）--》“API安全”--》“API密钥”--“设置密钥”（设置之后的那个值就是partnerkey，32位）
		String partnerkey = WeChatConfigs.getConfig(appId).getApi_key();
		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init(appId, appsecret, partnerkey);
		String xml = ClientCustomSSL.RefundNativePackage(appId,out_trade_no, out_refund_no, String.valueOf(total_fee), String.valueOf(refund_fee), nonce_str,reqHandler);
		String createOrderURL = WeChatUrlConfiguration.REFUND_URL; 
		try {
			String refundResult= ClientCustomSSL.doRefund(appId,createOrderURL, xml);
			Map m = XMLUtil.doXMLParse(refundResult);
			log.info("退款结果XML：" + refundResult);
			String return_code = (String) m.get("return_code");
			if ("SUCCESS".equals(return_code)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 通过支付结果通知回调接口从返回的数据中获取信息
	 * 获取方式可以如下：
	  	String inputLine;
		String notifyXml = "";
		String resXml = "";
		try {
			while ((inputLine = request.getReader().readLine()) != null) {
				notifyXml += inputLine;
			}
			request.getReader().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		相应处理可以在判断 WeChatPayResult.getResultCode() 进行：
		支付成功 该值为 ："SUCCESS"
	 * @param notifyXml
	 * @return
	 * @throws Exception 
	 * @throws JDOMException 
	 */
	@SuppressWarnings("rawtypes")
	public static WeChatPayResult notifyResult(String notifyXml) throws JDOMException, Exception{
		WeChatPayResult wpr = null;
		if(StringUtils.isNotBlank(notifyXml)){
			Map m = XMLUtil.doXMLParse(notifyXml);
			wpr = new WeChatPayResult();
			wpr.setAppid(m.get("appid").toString());
			wpr.setBankType(m.get("bank_type").toString());
			wpr.setCashFee(Integer.parseInt(m.get("cash_fee").toString()));
			wpr.setFeeType(m.get("fee_type").toString());
			wpr.setIsSubscribe(m.get("is_subscribe").toString());
			wpr.setMchId(m.get("mch_id").toString());
			wpr.setNonceStr(m.get("nonce_str").toString());
			wpr.setOpenid(m.get("openid").toString());
			wpr.setOutTradeNo(m.get("out_trade_no").toString());
			wpr.setResultCode(m.get("result_code").toString());
			wpr.setReturnCode(m.get("return_code").toString());
			wpr.setSign(m.get("sign").toString());
			wpr.setTimeEnd(m.get("time_end").toString());
			wpr.setTotalFee(Integer.parseInt(m.get("total_fee").toString()));
			wpr.setTradeType(m.get("trade_type").toString());
			wpr.setTransactionId(m.get("transaction_id").toString());
		}
		return wpr;
	}
	
	/**
	 * 根据支付返回结果里面的信息生成xml信息进行打印以通知微信
	 * 
	 * 例：
	  	BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(resXml.getBytes());
		out.flush();
		out.close();
	 * @return
	 */
	public static String xmlByResult(WeChatPayResult wpr){
		String resXml = "";
		if(wpr!=null){
			if ("SUCCESS".equals(wpr.getResultCode())) {
				//orderService.userPaySuccess(Long.parseLong(wpr.getOutTradeNo()), 3, wpr.getOutTradeNo());
				// 支付成功
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
							+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
			} else {
				resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
						+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			}
		}
		return resXml;
	}
}
