package apps.service;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.zkoss.zk.ui.Component;

import apps.entity.Activity;
import apps.entity.Users;

public interface ServiceMain {
	public String getPropSetting(String key);

	public String getQuery(String key);

	public void handleMessage(Exception ex);

	public Connection getConnection(String driverName, String url);

	public Component getResultGrid(String sql, String driverName, String url);

	public String getValueColumn(String columnName, String columnType,
			int columnLength, SimpleDateFormat simpleDateFormat);

	public String convertStringFromDate(String format, Date date, SimpleDateFormat simpleDateFormat);

	public String convertPass(String pass);

	public Users get1UserByUsernameAndPassword(Session sessionSelect, String username, String pass);

	public Timestamp convertToTimeStamp(String format, String date, SimpleDateFormat simpleDateFormat);

	public void saveUserActivity(Session session, String notes);

	public void deleteActivity(org.hibernate.Session querySession, Activity activity);

	public Criteria getCriteriaAtDateBetween(Criteria criteria,
			String columnName, String dateString, SimpleDateFormat simpleDateFormat);
}
