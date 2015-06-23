package apps.compare;

import java.util.Comparator;

import apps.entity.QueryData;

public class QueryDataCompare implements Comparator<QueryData> {
	
	private boolean _asc = true;
	
	public QueryDataCompare(boolean asc) {
		_asc = asc;		
	}

	@Override
	public int compare(QueryData queryData1, QueryData queryData2) {
		return queryData1.getModifiedBy().getUsername().compareTo(queryData2.getModifiedBy().getUsername())  * (_asc ? 1 : -1);
	}

}
