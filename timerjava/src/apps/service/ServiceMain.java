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

import apps.beans.AdvancedObject;
import apps.beans.EmailObject;
import apps.timer.TimerMain;

public class ServiceMain {
	private static final Logger logger = Logger.getLogger(ServiceMain.class);
	private static String _fileproperties = "data.properties";
	private static int mb = 1024 * 1024;

	public void showMemory() {
		Runtime runtime = Runtime.getRuntime();
		logger.info("Memory on " + (new Date()) + "Used Memory:"
				+ ((runtime.totalMemory() - runtime.freeMemory()) / mb)
				+ " MB, Free Memory:" + (runtime.freeMemory() / mb)
				+ " MB, Total Memory:" + (runtime.totalMemory() / mb)
				+ " MB, Max Memory:" + (runtime.maxMemory() / mb) + " MB");

	}

	public long getMemoryUsed() {
		Runtime runtime = Runtime.getRuntime();
		return Long.valueOf(runtime.totalMemory() - runtime.freeMemory())
				.longValue();
	}

	public long getMemoryMax() {
		Runtime runtime = Runtime.getRuntime();
		return Long.valueOf(runtime.maxMemory()).longValue();
	}

	public String getMemoryShow(long memorySized) {
		return (memorySized / mb) + " MB";
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
			fileName = "dy"
					+ randomString(5)
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

	public String downloadlink() {
		String downloadLink = null;
		boolean isDownloadLink = true;
		do {
			downloadLink = randomString(200);
			Connection connection = null;
			try {
				Class.forName(getPropSetting("database.driver")).newInstance();
				connection = DriverManager
						.getConnection(getPropSetting("database.url"));

				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT COUNT(*) as counti from tb_file where download_link = '"
								+ downloadLink + "'");

				ResultSet resultSet = null;
				try {
					resultSet = preparedStatement.executeQuery();

					if (resultSet.next()) {
						if (resultSet.getString("counti") == null
								|| (resultSet.getLong("counti") == 0)) {
							isDownloadLink = false;
						}
					} else {
						isDownloadLink = false;
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

		} while (isDownloadLink);

		return downloadLink;
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

	public String getStringTimeMysql(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000);
		String showTime = "";
		if (diffHours / 10 > 0) {
			showTime += " " + diffHours;
		} else {
			showTime += " 0" + diffHours;
		}

		if (diffMinutes / 10 > 0) {
			showTime += ":" + diffMinutes;
		} else {
			showTime += ":0" + diffMinutes;
		}
		if (diffSeconds / 10 > 0) {
			showTime += ":" + diffSeconds;
		} else {
			showTime += ":0" + diffSeconds;
		}
		return showTime;
	}

	public String getDurationTimeShow(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);

		String showTime = "";
		if (diffDays > 0) {
			showTime += "" + diffDays;
			if (diffDays == 1) {
				showTime += " day";
			} else {
				showTime += " days";
			}
		}

		if (diffHours / 10 > 0) {
			showTime += " " + diffHours;
		} else {
			showTime += " 0" + diffHours;
		}

		if (diffMinutes / 10 > 0) {
			showTime += ":" + diffMinutes;
		} else {
			showTime += ":0" + diffMinutes;
		}
		if (diffSeconds / 10 > 0) {
			showTime += ":" + diffSeconds;
		} else {
			showTime += ":0" + diffSeconds;
		}
		return showTime;
	}

	public void updateActivity(long activityID, File fileCheck,
			String fileType, String notes, AdvancedObject advancedObject) {
		Connection connection = null;
		try {
			Class.forName(getPropSetting("database.driver")).newInstance();
			connection = DriverManager
					.getConnection(getPropSetting("database.url"));
			Date doneDate = new Date();
			boolean isSuccess = false;
			if (fileCheck == null) {
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE tb_activity SET done_at = ?, notes = ?, start_at = ?, "
								+ "memory_used = ?, memory_max = ?, show_memory_used = ?, show_memory_max = ?, "
								+ "show_duration = ?, duration_time = ?  WHERE id = "
								+ activityID);
				preparedStatement.setTimestamp(1,
						new Timestamp(doneDate.getTime()));
				preparedStatement.setString(2, notes);
				preparedStatement.setTimestamp(3, new Timestamp(advancedObject
						.getStartAt().getTime()));
				preparedStatement.setLong(4, advancedObject.getMemoryUsed());
				preparedStatement.setLong(5, advancedObject.getMemoryMax());
				preparedStatement.setString(6,
						getMemoryShow(advancedObject.getMemoryUsed()));
				preparedStatement.setString(7,
						getMemoryShow(advancedObject.getMemoryMax()));
				preparedStatement.setString(
						8,
						getDurationTimeShow(advancedObject.getStartAt(),
								doneDate));
				preparedStatement.setString(
						9,
						getStringTimeMysql(advancedObject.getStartAt(),
								doneDate));

				preparedStatement.executeUpdate();

				preparedStatement.executeUpdate();
			} else {
				PreparedStatement preparedStatement = connection
						.prepareStatement(
								"INSERT INTO tb_file (filename, filetype, isdeleted, filesize, filesize_show, download_link) "
										+ "VALUES (?,?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(1, fileCheck.getName());
				preparedStatement.setString(2, fileType);
				preparedStatement.setBoolean(3, false);
				preparedStatement.setLong(4, fileCheck.length());
				preparedStatement.setString(5,
						readableFileSize(fileCheck.length()));
				preparedStatement.setString(6, downloadlink());
				preparedStatement.executeUpdate();
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					preparedStatement = connection
							.prepareStatement("UPDATE tb_activity SET done_at = ?, file_id=?, start_at = ?, "
									+ "memory_used = ?, memory_max = ?, show_memory_used = ?, show_memory_max = ?, "
									+ "show_duration = ?, duration_time = ?  WHERE id = "
									+ activityID);
					preparedStatement.setTimestamp(1,
							new Timestamp(new Date().getTime()));
					preparedStatement.setLong(2, resultSet.getLong(1));
					preparedStatement.setTimestamp(3, new Timestamp(
							advancedObject.getStartAt().getTime()));
					preparedStatement
							.setLong(4, advancedObject.getMemoryUsed());
					preparedStatement.setLong(5, advancedObject.getMemoryMax());
					preparedStatement.setString(6,
							getMemoryShow(advancedObject.getMemoryUsed()));
					preparedStatement.setString(7,
							getMemoryShow(advancedObject.getMemoryMax()));
					preparedStatement.setString(
							8,
							getDurationTimeShow(advancedObject.getStartAt(),
									doneDate));
					preparedStatement.setString(
							9,
							getStringTimeMysql(advancedObject.getStartAt(),
									doneDate));
					preparedStatement.executeUpdate();
					resultSet.close();
					isSuccess = true;
				}
			}

			PreparedStatement preparedStatement = connection
					.prepareStatement("SELECT users.email, activity.query_name, users.id as user_id  FROM tb_activity AS activity "
							+ "INNER JOIN tb_users AS users "
							+ "ON activity.user_created_id = users.id "
							+ "where users.email IS NOT NULL AND activity.id ="
							+ activityID);

			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				String emailTo = resultSet.getString("email");
				String emailSubject = "Query "
						+ resultSet.getString("query_name") + " problem";
				String emailDescription = "File not created because : " + notes;
				if (isSuccess) {
					setTotalFileUsedSized(resultSet.getInt("user_id"));
					emailSubject = "Query " + resultSet.getString("query_name")
							+ " file completed";
					resultSet.close();
					preparedStatement = connection
							.prepareStatement("SELECT tb_file.download_link "
									+ "FROM tb_activity AS activity "
									+ "INNER JOIN tb_file AS tb_file ON activity.file_id = tb_file.id "
									+ "WHERE activity.id =" + activityID);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						emailDescription = "You can download file <br/> <a href=\""
								+ getPropSetting("server.host")
								+ "/download/file?ridfil="
								+ resultSet.getString("download_link")
								+ "\" target=\"_blank\" style=\"cursor:pointer;\">Download</a>";
					}
				}
				new EmailService(new EmailObject("from@test.com", emailTo,
						emailSubject, emailDescription));
			}
			resultSet.close();
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
	
	public void setTotalFile() {
		String sql = "SELECT COUNT(*) AS rows FROM tb_filetotal";
		Connection connection = null;
		try {
			Class.forName(getPropSetting("database.driver")).newInstance();
			connection = DriverManager
					.getConnection(getPropSetting("database.url"));
			PreparedStatement preparedStatement = connection
					.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				if (resultSet.getLong("rows") > 0) {
					sql = "UPDATE tb_filetotal SET filesize = (SELECT SUM(filesize) FROM tb_file)";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.executeUpdate();

				} else {
					sql = "INSERT INTO tb_filetotal (filesize) VALUES ((SELECT SUM(filesize) FROM tb_file))";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.executeUpdate();
				}
			}
			
			sql = "SELECT SUM(filesize) AS summ FROM tb_filetotal";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				sql = "UPDATE tb_filetotal SET filesize_show='"
						+ readableFileSize(resultSet.getLong("summ"))
						+ "'";
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.executeUpdate();
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

	public void setTotalFileUsedSized(int userId) {
		String sql = "";
		Connection connection = null;
		try {
			Class.forName(getPropSetting("database.driver")).newInstance();
			connection = DriverManager
					.getConnection(getPropSetting("database.url"));

				sql = "SELECT COUNT(*) AS rows FROM tb_filesize_used WHERE user_id = "
						+ userId;
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					if (resultSet.getLong("rows") > 0) {

						sql = "update tb_filesize_used SET filesize = (SELECT SUM(tb_file.filesize) FROM tb_file AS tb_file "
								+ "INNER JOIN tb_activity AS tb_activity ON tb_file.id = tb_activity.file_id "
								+ "WHERE tb_activity.user_created_id = "
								+ userId
								+ ") WHERE user_id = " + userId;
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.executeUpdate();
					} else {

						sql = "INSERT INTO tb_filesize_used (user_id, filesize) VALUES ("
								+ userId
								+ ", "
								+ "(SELECT SUM(tb_file.filesize) FROM tb_file AS tb_file "
								+ "INNER JOIN tb_activity AS tb_activity ON tb_file.id = tb_activity.file_id "
								+ "WHERE tb_activity.user_created_id = "
								+ userId
								+ "));";
						preparedStatement = connection.prepareStatement(sql);
						preparedStatement.executeUpdate();
					}
				}

				sql = "SELECT SUM(filesize) AS summ FROM tb_filesize_used WHERE user_id = "+userId;
				preparedStatement = connection.prepareStatement(sql);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					sql = "UPDATE tb_filesize_used SET filesize_show='"
							+ readableFileSize(resultSet.getLong("summ"))
							+ "' WHERE user_id = "+userId;
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.executeUpdate();
				}
				resultSet.close();
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
		setTotalFile();
	}
}
