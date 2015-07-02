package apps.components;

import org.zkoss.zul.Treeitem;

public class TreeItemWithData extends Treeitem {
	private static final long serialVersionUID = -1459659850739422778L;
	private String _dataPut;
	private String _querySelectFinal;
	private String driverName;
	private String urlData;
	
	
	public TreeItemWithData() {
		
	}
	
	public TreeItemWithData(String label) {
		this.setLabel(label);
	}
	

	public String getDataPut() {
		return _dataPut;
	}
	public void setDataPut(String dataPut) {
		this._dataPut = dataPut;
	}
	public String get_dataPut() {
		return _dataPut;
	}

	public void set_dataPut(String _dataPut) {
		this._dataPut = _dataPut;
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
