package apps.service;

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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import apps.entity.Users;

public class ServiceImplMain implements ServiceMain {
	private static final Logger logger = Logger
			.getLogger(ServiceImplMain.class);
	private static String _fileproperties = "data.properties";
	private static String _queryProperties = "query.properties";
	private static String[] columnTypeDate = new String[] {
			"java.sql.Timestamp", "java.sql.Date" };

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

		try {
			Connection connection = getConnection(driverName, url);
			sql = sql.trim();
			PreparedStatement preparedStatement = connection
					.prepareStatement(sql);

			if (sql.toUpperCase().trim().startsWith("SELECT")) {
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					resultSet = preparedStatement.executeQuery();
					Grid gridResult = new Grid();
					gridResult.setSizedByContent(true);
					gridResult.setVflex(true);
					gridResult.setAutopaging(true);
					gridResult.setMold("paging");
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

					while (resultSet.next()) {
						Row rowResult = new Row();
						for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
							Label labelResult = null;

							if (Arrays.asList(columnTypeDate).contains(
									resultSetMetaData.getColumnClassName(i))) {
								try {
									labelResult = new Label(resultSet
											.getTimestamp(
													resultSetMetaData
															.getColumnName(i))
											.toString());
								} catch (Exception ex) {
									labelResult = new Label("0000-00-00 00:00");
								}
							} else {
								labelResult = new Label(
										resultSet.getString(resultSetMetaData
												.getColumnName(i)));
							}

							rowResult.appendChild(labelResult);
						}
						rowResult.setParent(rowsResult);
					}
					rowsResult.setParent(gridResult);

					resultSet.close();
					connection.close();
					return (Component) gridResult;
				} else {
					Label labelResult = new Label("Data empty");
					return labelResult;
				}
			} else if (sql.toUpperCase().trim().startsWith("DELETE")) {
				if (Messagebox.show("Are you sure to delete data", "Important",
							Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
					preparedStatement.executeUpdate();
					Label labelResult = new Label("Process Done");
					return labelResult;
				} else {
					Label labelResult = new Label("Nothing");
					return labelResult;
				}
			} else {
				preparedStatement.executeUpdate();
				Label labelResult = new Label("Process Done");
				return labelResult;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			Label labelResult = new Label("Error " + ex.getMessage());
			return labelResult;
		}
	}

	public String getValueColumn(String columnName, String columnType,
			int columnLength) {
		String valueInsert = "null";
		if (columnType.equalsIgnoreCase("java.lang.String")) {
			valueInsert = columnName;
			if (valueInsert.length() > columnLength) {
				valueInsert = valueInsert.substring(0, columnLength);
			}
			valueInsert = "'" + valueInsert + "'";
		} else if (Arrays.asList(columnTypeDate).contains(columnType)) {
			valueInsert = "'" + new Date() + "'";
		} else {
			valueInsert = "1";
		}
		return valueInsert;
	}

	public String convertStringFromDate(String format, Date date) {
		if (date != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
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

	public Users get1UserByUsernameAndPassword(String username, String pass) {
		Users user = null;
		Session sessionSelect = null;
		try {
			sessionSelect = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = sessionSelect.createCriteria(Users.class);
			criteria.add(Restrictions.eq("username", username));
			criteria.add(Restrictions.eq("pass", convertPass(pass)));

			if (criteria.list().size() > 0) {
				user = (Users) criteria.uniqueResult();

				Transaction trx = sessionSelect.beginTransaction();
				user.setLast_loginAsDate(new Date());
				sessionSelect.update(user);
				trx.commit();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (sessionSelect != null) {
				try {
					sessionSelect.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				} 
			}
		}
		return user;
	}
	
	public Timestamp convertToTimeStamp(String format, String date) {
		if (date == null || (date.isEmpty())) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return new Timestamp(sdf.parse(date).getTime());
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

}
