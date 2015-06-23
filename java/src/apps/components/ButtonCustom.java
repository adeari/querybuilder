package apps.components;

import org.zkoss.zul.Button;

public class ButtonCustom extends Button {
	private static final long serialVersionUID = -4515717424028231156L;
	private Object dataObject;
	
	public ButtonCustom() {
		
	}
	
	
	public ButtonCustom(String iconImage, Object object) {
		this.setImage(iconImage);
		dataObject = object;
	}
	
	public Object getDataObject() {
		return dataObject;
	}
	public void setDataObject(Object dataObject) {
		this.dataObject = dataObject;
	}
	
	
}
