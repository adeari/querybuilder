package apps.components;

import org.zkoss.zul.Checkbox;

public class CheckboxCustomize extends Checkbox {
	private static final long serialVersionUID = -8642926519830023740L;
	private Object _dataCustom;
	
	public CheckboxCustomize() {
		
	}
	
	public CheckboxCustomize(Object dataCustom) {
		_dataCustom = dataCustom;
	}
	
	public Object get_dataCustom() {
		return _dataCustom;
	}
	public void set_dataCustom(Object _dataCustom) {
		this._dataCustom = _dataCustom;
	}
}
