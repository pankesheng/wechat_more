package com.pks.wechat.menu;
/**
 * @author pks
 * @version 2017年10月26日
 */
public class QrcodeButton extends Button {
	private String type;  
    private String key;  
    private Button[] sub_button;
    
    public QrcodeButton() {
		// TODO Auto-generated constructor stub
	}
    
	public QrcodeButton(String name,String type, String key, Button[] sub_button) {
		super();
		super.setName(name);
		this.type = type;
		this.key = key;
		this.sub_button = sub_button;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Button[] getSub_button() {
		return sub_button;
	}
	public void setSub_button(Button[] sub_button) {
		this.sub_button = sub_button;
	}
    
    
}

