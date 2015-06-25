package apps.query.control;

import org.apache.log4j.Logger;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class QueryTask extends Window {
	private static final long serialVersionUID = -2216130550245303155L;
	private static final Logger logger = Logger.getLogger(QueryTask.class);
	
	public QueryTask() {
		super(title, null, true);
		
		Vlayout vlayout = new Vlayout();
		vlayout.setParent(this);
	}
}
