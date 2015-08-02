package apps.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import apps.entity.Activity;
import apps.entity.FilesData;
import apps.entity.UserActivity;
import apps.entity.Users;

public class ServiceImplMain implements ServiceMain {
	private static final Logger logger = Logger
			.getLogger(ServiceImplMain.class);
	private static String _fileproperties = "data.properties";
	private static String _queryProperties = "query.properties";
	private static String[] columnTypeDate = new String[] {
			"java.sql.Timestamp", "java.sql.Date",
			"microsoft.sql.DateTimeOffset" };

	public String getPropSetting(String key) {
		try {
			InputStream input = ServiceImplMain.class.getClassLoader()
					.getResourceAsStream(_fileproperties);

			Properties prop = new Properties();
			prop.load(input);

			return prop.getProperty(key);
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public String getQuery(String key) {
		try {
			InputStream input = ServiceImplMain.class.getClassLoader()
					.getResourceAsStream(_queryProperties);
			Properties prop = new Properties();
			prop.load(input);
			return prop.getProperty(key);
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public void handleMessage(Exception ex) {
		Messagebox.show(ex.getMessage(), "Error", Messagebox.OK,
				Messagebox.ERROR);
		logger.error(ex.getMessage(), ex);
	}

	public Connection getConnection(String driverName, String url) {
		try {
			Class.forName(driverName).newInstance();
			Connection conn = DriverManager.getConnection(url);
			return conn;
		} catch (Exception ex) {
			handleMessage(ex);
		}
		return null;
	}

	public Component getResultGrid(String sql, String driverName, String url) {
		if (driverName == null || (driverName.isEmpty())) {
			Label labelResult = new Label("Select database on left first");
			return labelResult;
		}
		if (url == null || (url.isEmpty())) {
			Label labelResult = new Label("Select database on left first");
			return labelResult;
		}
		String messageHandle = "Data empty";

		Connection connection = null;
		try {
			connection = getConnection(driverName, url);
			sql = sql.trim();
			PreparedStatement preparedStatement = connection
					.prepareStatement(sql);

			if (sql.toUpperCase().trim().startsWith("SELECT")) {
				ResultSet resultSet = null;

				try {
					resultSet = preparedStatement.executeQuery();

					if (resultSet.next()) {
						resultSet = preparedStatement.executeQuery();
						Grid gridResult = new Grid();
						gridResult.setSizedByContent(true);
						gridResult.setVflex(true);
						gridResult.setMold("paging");
						gridResult.setAutopaging(true);
						gridResult.setVflex(true);
						gridResult.setPagingPosition("bottom");
						gridResult.setHeight("360px");
						Columns columnsGridResult = new Columns();
						columnsGridResult.setSizable(true);

						ResultSetMetaData resultSetMetaData = resultSet
								.getMetaData();
						for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
							Column column = new Column(resultSetMetaData
									.getColumnName(i).toUpperCase());
							column.setParent(columnsGridResult);
						}
						columnsGridResult.setParent(gridResult);

						Rows rowsResult = new Rows();
						rowsResult.setStyle("overflow: scroll;");

						int rowIndex = 0;
						while (resultSet.next() && rowIndex < 500) {
							Row rowResult = new Row();
							for (int i = 1; i <= resultSetMetaData
									.getColumnCount(); i++) {
								Label labelResult = null;

								if (Arrays.asList(columnTypeDate)
										.contains(
												resultSetMetaData
														.getColumnClassName(i))) {
									try {
										labelResult = new Label(resultSet
												.getTimestamp(i).toString());
									} catch (Exception ex) {
										labelResult = new Label(
												"0000-00-00 00:00");
									}
								} else {
									labelResult = new Label(
											resultSet.getString(i));
								}

								rowResult.appendChild(labelResult);
							}
							rowResult.setParent(rowsResult);
							rowIndex++;
						}
						rowsResult.setParent(gridResult);

						saveUserActivity(null, "Query : " + sql + " \nOn "
								+ url + " \nResult : success");
						return (Component) gridResult;
					} else {
						Label labelResult = new Label(messageHandle);
						saveUserActivity(null, "Query : " + sql + " \nOn "
								+ url + " \nResult : " + messageHandle);
						return labelResult;
					}
				} catch (Exception e) {
					messageHandle = e.getMessage();
					logger.error(messageHandle, e);
				} finally {
					resultSet.close();
					preparedStatement.close();
				}
				Label labelResult = new Label(messageHandle);
				saveUserActivity(null, "Query : " + sql + " \nOn " + url
						+ " \nResult : " + messageHandle);
				return labelResult;
			} else if (sql.toUpperCase().trim().startsWith("DELETE")) {
				if (Messagebox.show("Are you sure to delete data", "Important",
						Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
					preparedStatement.executeUpdate();
					Label labelResult = new Label("Process Done");
					saveUserActivity(null, "Query : " + sql + " \nOn " + url
							+ " \nResult : success");
					preparedStatement.close();
					return labelResult;
				} else {
					Label labelResult = new Label("Nothing");
					return labelResult;
				}
			} else {
				preparedStatement.executeUpdate();
				Label labelResult = new Label("Process Done");
				saveUserActivity(null, "Query : " + sql + " \nOn " + url
						+ " \nResult : success");
				preparedStatement.close();
				return labelResult;
			}
		} catch (Exception ex) {
			messageHandle = ex.getMessage();
			logger.error(messageHandle, ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		Label labelResult = new Label(messageHandle);
		saveUserActivity(null, "Query : " + sql + " \nOn " + url
				+ " \nResult : " + messageHandle);
		return labelResult;
	}

	public String getValueColumn(String columnName, String columnType,
			int columnLength, SimpleDateFormat simpleDateFormat) {
		String valueInsert = "null";
		if (columnType.equalsIgnoreCase("java.lang.String")) {
			valueInsert = columnName;
			if (valueInsert.length() > columnLength) {
				valueInsert = valueInsert.substring(0, columnLength);
			}
			valueInsert = "'" + valueInsert + "'";
		} else if (columnType.equalsIgnoreCase("java.sql.Time")) {
			simpleDateFormat.applyPattern("HH:mm:ss");
			valueInsert = "'"
					+ simpleDateFormat.format(new Date())
					+ "'";
		} else if (Arrays.asList(columnTypeDate).contains(columnType)) {
			simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
			valueInsert = "'"
					+ simpleDateFormat.format(new Date()) + "'";
		} else {
			valueInsert = "1";
		}
		return valueInsert;
	}

	public String convertStringFromDate(String format, Date date,
			SimpleDateFormat simpleDateFormat) {
		if (date != null) {
			simpleDateFormat.applyPattern(format);
			return simpleDateFormat.format(date);
		}
		return "";
	}

	public String convertPass(String pass) {
		String md5 = null;
		try {
			pass = "lo088" + pass + "i9900";
			MessageDigest mdEnc = MessageDigest.getInstance("MD5");
			mdEnc.update(pass.getBytes(), 0, pass.length());
			md5 = new BigInteger(1, mdEnc.digest()).toString(16);
			if (md5.length() > 200) {
				md5 = md5.substring(0, 200);
			}
		} catch (Exception ex) {
			return null;
		}
		return md5;
	}

	public Users get1UserByUsernameAndPassword(Session sessionSelect,
			String username, String pass) {
		Users user = null;
		try {
			sessionSelect = hibernateUtil.getSessionFactory(sessionSelect);
			Criteria criteria = sessionSelect.createCriteria(Users.class);
			criteria.add(Restrictions.eq("username", username));
			criteria.add(Restrictions.eq("pass", convertPass(pass)));

			if (criteria.list().size() > 0) {
				user = (Users) criteria.uniqueResult();

				user.setLast_loginAsDate(new Date());
				sessionSelect.update(user);
				sessionSelect.flush();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return user;
	}

	public Timestamp convertToTimeStamp(String format, String date, SimpleDateFormat simpleDateFormat) {
		if (date == null || (date.isEmpty())) {
			return null;
		}
		simpleDateFormat.applyPattern(format);
		try {
			return new Timestamp(simpleDateFormat.parse(date).getTime());
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public Time convertToTime(String time) {
		if (time == null || (time.isEmpty())) {
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			return new Time(sdf.parse(time).getTime());
		} catch (Exception e) {
			return null;
		}
	}

	public void saveUserActivity(Session session, String notes) {
		if (Sessions.getCurrent().getAttribute("userlogin") != null) {
			try {
				session = hibernateUtil.getSessionFactory(session);
				UserActivity userActivity = new UserActivity((Users) Sessions
						.getCurrent().getAttribute("userlogin"), notes);
				session.save(userActivity);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void deleteActivity(org.hibernate.Session querySession,
			Activity activity) {
		try {
			if (activity.getFileData() != null) {
				querySession = hibernateUtil.getSessionFactory(querySession);
				FilesData filesData = activity.getFileData();
				File file = new File(getQuery("location."
						+ filesData.getFiletype().toLowerCase())
						+ "/" + filesData.getFilename());
				if (file.isFile()) {
					file.delete();
				}
				querySession.delete(filesData);
				querySession.flush();
			}
			querySession.delete(activity);
			querySession.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Criteria getCriteriaAtDateBetween(Criteria criteria,
			String columnName, String dateString, SimpleDateFormat simpleDateFormat) {
		Timestamp basic = convertToTimeStamp("HH:mm:ss", dateString, simpleDateFormat);
		if (basic == null) {
			Timestamp lowTimestamp = convertToTimeStamp("dd/MM/yyyy",
					dateString, simpleDateFormat);
			Timestamp highTimestamp = convertToTimeStamp("dd/MM/yyyy HH:mm:ss",
					dateString + " 23:59:59", simpleDateFormat);
			criteria.add(Restrictions.between(columnName, lowTimestamp,
					highTimestamp));
		} else {
			Timestamp highTimestamp = convertToTimeStamp("dd/MM/yyyy HH:mm:ss",
					dateString + ":59", simpleDateFormat);
			criteria.add(Restrictions.between(columnName, basic, highTimestamp));
		}
		return criteria;
	}
}
