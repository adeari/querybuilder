package apps.service;

import java.sql.Connection;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Window;

import apps.entity.Users;

public interface ServiceMain {
	public String getPropSetting(String key);
	public String getQuery(String key);
	public void handleMessage(Exception ex);
	public Connection getConnection(String driverName, 
			String url);
	public Component getResultGrid(String sql, 
			String driverName, 
			String url);
	public String getValueColumn(String columnName, String columnType, int columnLength);
	public void setTreeData(final String databaseKind, String getTableName, final Textbox textQuery,
			Window windowMain, final String _driverName, final String _url, Treechildren treechildrenTreeDAta);
	public String convertStringFromDate(String format, Date date);
	public String convertPass(String pass);
	public Users get1UserByUsernameAndPassword(String username, String pass);
}
