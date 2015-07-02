package apps.components;

import org.zkoss.zul.Menuitem;


public class MenuitemWithData extends Menuitem {
	private static final long serialVersionUID = 3214948044009148026L;
	private String _data;
	private String _querySelectFinal;
	private String driverName;
	private String urlData;
	
	public MenuitemWithData() {
		
	}
	
	public MenuitemWithData(String label) {
		this.setLabel(label);
	}

	public String get_data() {
		return _data;
	}
	public void set_data(String _data) {
		this._data = _data;
	}

	public String get_querySelectFinal() {
		return _querySelectFinal;
	}

	public void set_querySelectFinal(String _querySelectFinal) {
		this._querySelectFinal = _querySelectFinal;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUrlData() {
		return urlData;
	}

	public void setUrlData(String urlData) {
		this.urlData = urlData;
	}
}
