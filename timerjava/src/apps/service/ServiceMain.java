package apps.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;

import apps.timer.TimerMain;

public class ServiceMain {
	private static final Logger logger = Logger.getLogger(ServiceMain.class);
	private static String _fileproperties = "data.properties";
	private static int mb = 1024 * 1024;

	public void showMemory() {
		logger.info("Memory on " + (new Date()));
		Runtime runtime = Runtime.getRuntime();
		logger.info("Used Memory:"
				+ ((runtime.totalMemory() - runtime.freeMemory()) / mb)
				+ " MB, Free Memory:" + (runtime.freeMemory() / mb)
				+ " MB, Total Memory:" + (runtime.totalMemory() / mb)
				+ " MB, Max Memory:" + (runtime.maxMemory() / mb) + " MB");

	}

	public String getPropSetting(String key) {
		try {
			InputStream input = TimerMain.class.getClassLoader()
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

	private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static Random rnd = new Random();

	private String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public String filename(String fileType) {
		String fileName = null;
		boolean isFilename = true;
		do {
			fileName = "dy"+randomString(5)
					+ (new SimpleDateFormat("yyyyMMddHHmmsss"))
							.format(new Date()) + "." + fileType;
			Connection connection = null;
			try {
				Class.forName(getPropSetting("database.driver")).newInstance();
				connection = DriverManager
						.getConnection(getPropSetting("database.url"));

				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT COUNT(*) as counti from tb_file where filename = '"
								+ fileName + "'");

				ResultSet resultSet = null;
				try {
					resultSet = preparedStatement.executeQuery();

					if (resultSet.next()) {
						if (resultSet.getString("counti") == null
								|| (resultSet.getLong("counti") == 0)) {
							isFilename = false;
						}
					} else {
						isFilename = false;
					}

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				} finally {
					if (resultSet != null) {
						try {
							resultSet.close();
						} catch (SQLException e) {
							logger.error(" on Close" + e.getMessage(), e);
						}
					}
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						logger.error(" on Close" + e.getMessage(), e);
					}
				}
			}

		} while (isFilename);

		return fileName;
	}

	public String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "b", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}

	public void updateActivity(long activityID, 
			File fileCheck, String fileType, String notes) {
		Connection connection = null;
		try {
			Class.forName(getPropSetting("database.driver")).newInstance();
			connection = DriverManager.getConnection(getPropSetting("database.url"));
			if (fileCheck == null) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE tb_activity SET done_at = ?, notes = ? WHERE id = ?");
				preparedStatement.setTimestamp(1, new Timestamp(new Date().getTime()));
				preparedStatement.setString(2, notes);
				preparedStatement.setLong(3, activityID);
				preparedStatement.executeUpdate();
				
				preparedStatement.executeUpdate();
			} else {
				PreparedStatement preparedStatement = connection
						.prepareStatement("INSERT INTO tb_file (filename, filetype, isdeleted, filesize, filesize_show) "
								+ "VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, fileCheck.getName());
				preparedStatement.setString(2, fileType);
				preparedStatement.setBoolean(3, false);
				preparedStatement.setLong(4, fileCheck.length());
				preparedStatement.setString(5, readableFileSize(fileCheck.length()));
				preparedStatement.executeUpdate();
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					preparedStatement = connection
							.prepareStatement("UPDATE tb_activity SET done_at = ?, file_id=? WHERE id = ?");
					preparedStatement.setTimestamp(1, new Timestamp(new Date().getTime()));
					preparedStatement.setLong(2, resultSet.getLong(1));
					preparedStatement.setLong(3, activityID);
					preparedStatement.executeUpdate();
				}
			}
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(" connection on Close" + e.getMessage(), e);
				}
			}
		}
	}
}
