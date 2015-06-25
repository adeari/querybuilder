package apps.components;

import org.zkoss.zul.Listcell;

public class ListcellCustomize extends Listcell {
	private static final long serialVersionUID = 8944756226633830221L;
	private Object _dataObject;
	
	public ListcellCustomize() {
		
	}
	
	public ListcellCustomize(String label, Object object) {
		_dataObject = object;
		this.setLabel(label);
	}
	
	public Object getDataObject() {
		return _dataObject;
	}
	public void setDataObject(Object dataObject) {
		this._dataObject = dataObject;
	}
}
