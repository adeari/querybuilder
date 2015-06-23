package apps.components;

import org.zkoss.zul.Menuitem;


public class MenuitemWithData extends Menuitem {
	private static final long serialVersionUID = 3214948044009148026L;
	private String _data;
	private String _querySelectFinal;
	private String _databaseKind;
	private Integer _indexDataSqlServerFinal;
	
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

	public String get_databaseKind() {
		return _databaseKind;
	}

	public void set_databaseKind(String _databaseKind) {
		this._databaseKind = _databaseKind;
	}

	public Integer get_indexDataSqlServerFinal() {
		return _indexDataSqlServerFinal;
	}

	public void set_indexDataSqlServerFinal(Integer _indexDataSqlServerFinal) {
		this._indexDataSqlServerFinal = _indexDataSqlServerFinal;
	}

	
}
