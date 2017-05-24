package com.pks.wechat.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLUtil {

	/**
	 * 解析xml，返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map doXMLParse(String strxml) throws JDOMException,Exception{
		strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
		if (null==strxml||"".equals(strxml)) {
			return null;
		}
		Map m = new HashMap();
		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while(it.hasNext()){
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			}else{
				v = XMLUtil.getChildrenText(children);
			}
			m.put(k, v);
		}
		//关闭流
		in.close();
		return m;
	}
	
	@SuppressWarnings("rawtypes")
	public static String getChildrenText(List children){
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<"+name+">");
				if (!list.isEmpty()) {
					sb.append(XMLUtil.getChildrenText(list));
				}
				sb.append(value);
				sb.append("<"+name+">");
			}
		}
		return sb.toString();
	}
	
	/**
     * Map 转成xml数据
     * @param nativeObj
     * @return
     */
    public static String ArrayToXml(SortedMap<String, String> nativeObj) {  
        String xml = "<xml>";  
        Iterator<Entry<String, String>> iter = nativeObj.entrySet().iterator();  
        while (iter.hasNext()) {  
            Entry<String, String> entry = iter.next();  
            String key = entry.getKey();  
            String val = entry.getValue();  
            if (IsNumeric(val)) {  
                xml += "<" + key + ">" + val + "</" + key + ">";  
  
            } else  
                xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";  
        }  
        xml += "</xml>";  
         try {  
            return new String(xml.toString().getBytes(),"ISO8859-1");  
        } catch (UnsupportedEncodingException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }    
         return "";  
    }
    
    /**
     * 是否是单个数字
     * @param str
     * @return
     */
    public static boolean IsNumeric(String str) {  
        if (str.matches("\\d *")) {  
            return true;  
        } else {  
            return false;  
        }  
    }  
	
}
