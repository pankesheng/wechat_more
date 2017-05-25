package com.pks.wechat.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pks.wechat.configuration.WeChatUrlConfiguration;
import com.pks.wechat.material.BasicMaterial;
import com.pks.wechat.material.NewsMaterial;
import com.pks.wechat.material.OtherMaterial;
import com.pks.wechat.material.UploadMaterail;

/**
 * 素材工具类
 * @author pks
 *
 */
public class SUtilMaterial {

	/**
	 * 
	 * @param accessToken 访问令牌
	 * @param file	文件地址
	 * @param title	素材标题
	 * @param introduction	素材描述
	 * @return 
	 */
	public static UploadMaterail uploadMaterail(String appId,File file,String title,String introduction){
		try {
			String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
			//这块是用来处理如果上传的类型时video的类型的
			JSONObject json = new JSONObject();
			json.put("title", title);
			json.put("introduction", introduction);
			
			//请求地址
			String uploadMediaUrl = WeChatUrlConfiguration.MATERIAL_ADD_MATERIAL.replace("ACCESS_TOKEN", accessToken);
			URL url = new URL(uploadMediaUrl);
			String result = null;
			long filelength = file.length();
			String fileName = file.getName();
			String suffix = fileName.substring(fileName.lastIndexOf("."),fileName.length());
			String type = "";//根据后缀suffix进行判断类型
			if("bmp".equals(suffix) || "png".equals(suffix) || "jpeg".equals(suffix) || "jpg".equals(suffix)||"gif".equals(suffix)){
				type = "images";
			}else if("mp3".equals(suffix) || "wma".equals(suffix) || "wav".equals(suffix) ||"amr".equals(suffix)){
				type = "voice";
			}else if("mp4".equals(suffix)){
				type = "video";
			}
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false); //post方式不能使用缓存
			//设置请求头信息
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			
			//设置边界，这里的boundary 是http协议里面的分割符，（http协议 boundary）
			String boundary = "----------" + System.currentTimeMillis(); 
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			
			//请求正文信息
			//第一部分
			StringBuilder sb = new StringBuilder();
			
			//这块是post提交type值也就是文件对应的mime类型的值
			sb.append("--");//必须多两道线 http协议要求的，用来分隔提交的参数用的
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"type\" \r\n\r\n"); //这里是参数名，参数名和值之间要用两次  
			sb.append(type+"\r\n"); //参数的值  
			
			//这块是上传video是必须的参数，你们可以在这里根据文件类型做if/else 判断  
            if("video".equals(type)){
				sb.append("--"); // 必须多两道线  
	            sb.append(boundary);  
	            sb.append("\r\n");  
	            sb.append("Content-Disposition: form-data;name=\"description\" \r\n\r\n");  
	            sb.append(json.toString()+"\r\n");  
            }
            /** 
             * 这里重点说明下，上面两个参数完全可以写在url地址后面 就想我们平时url地址传参一样， 
             * http://api.weixin.qq.com/cgi-bin/material/add_material?access_token=##ACCESS_TOKEN##&type=""&description={} 这样，如果写成这样，上面的 
             * 那两个参数的代码就不用写了，不过media参数能否这样提交我没有试，感兴趣的可以试试 
             */  
              
            sb.append("--"); // 必须多两道线
            sb.append(boundary);  
            sb.append("\r\n");  
            //这里是media参数相关的信息，这里是否能分开下我没有试，感兴趣的可以试试  
            sb.append("Content-Disposition: form-data;name=\"media\";filename=\""+ fileName + "\";filelength=\"" + filelength + "\" \r\n");  
            sb.append("Content-Type:application/octet-stream\r\n\r\n");  
//          System.out.println(sb.toString());  
            byte[] head = sb.toString().getBytes("utf-8");  
            // 获得输出流  
            OutputStream out = new DataOutputStream(conn.getOutputStream());  
            // 输出表头  
            out.write(head);  
            // 文件正文部分  
            // 把文件已流文件的方式 推入到url中  
            DataInputStream in = new DataInputStream(new FileInputStream(file));  
            int bytes = 0;  
            byte[] bufferOut = new byte[1024];  
            while ((bytes = in.read(bufferOut)) != -1) {  
                out.write(bufferOut, 0, bytes);  
            }  
            in.close();  
            // 结尾部分，这里结尾表示整体的参数的结尾，结尾要用"--"作为结束，这些都是http协议的规定  
            byte[] foot = ("\r\n--" + boundary + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线  
            out.write(foot);  
            out.flush();  
            out.close();  
            StringBuffer buffer = new StringBuffer();  
            BufferedReader reader = null;  
            // 定义BufferedReader输入流来读取URL的响应  
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                buffer.append(line);  
            }  
            if (result == null) {  
                result = buffer.toString();  
            }  
            // 使用JSON-lib解析返回结果  
            JSONObject jsonObject = JSONObject.fromObject(result);
            UploadMaterail materail = new UploadMaterail();
            if(jsonObject!=null && jsonObject.containsKey("media_id")){
            	materail.setMedia_id(jsonObject.getString("media_id"));
            	if(jsonObject.containsKey("url")){
            		materail.setUrl(jsonObject.getString("url"));
            	}
            }
            return materail;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	/**
	 * 根据分页信息 类型获得jsonObejct对象 在这个方法中可以自由在jsonObject 获取素材总数等信息
	 * @param accessToken
	 * @param type
	 * @param offset
	 * @param count
	 * @return
	 */
	public static JSONObject getMaterailListJson(String appId,String type,int offset,int count){
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		String url = WeChatUrlConfiguration.MATERIAL_BATCHGET_URL.replace("ACCESS_TOKEN", accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("offset", offset);
		params.put("count", count);
		String result = SUtilCommon.httpPostRequest(url, SUtilCommon.paramstourl(params));
		JSONObject jsonObject = JSONObject.fromObject(result);
		return jsonObject;
	}
	
	/**
	 * 根据分页信息 类型获取素材列表信息，不包含素材数量等信息
	 * @param accessToken
	 * @param type
	 * @param offset
	 * @param count
	 * @return
	 */
	public static List<BasicMaterial> getMaterailList(String appId,String type,int offset,int count){
		String accessToken = SUtilBase.getAccessToken(appId).getAccess_token();
		String url = WeChatUrlConfiguration.MATERIAL_BATCHGET_URL.replace("ACCESS_TOKEN", accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("offset", offset);
		params.put("count", count);
		String result = SUtilCommon.httpPostRequest(url, SUtilCommon.paramstourl(params));
		JSONObject jsonObject = JSONObject.fromObject(result);
		if(jsonObject!=null && jsonObject.containsKey("item")){
			List<BasicMaterial> list = new ArrayList<BasicMaterial>();
			if("news".equals(type)){
				list = new Gson().fromJson(jsonObject.getString("item"),  new TypeToken<List<NewsMaterial>>(){}.getType());
			}else{
				list = new Gson().fromJson(jsonObject.getString("item"),  new TypeToken<List<OtherMaterial>>(){}.getType());
			}
			return list;
		}
		return null;
	}
	
}
