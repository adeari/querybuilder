package apps.filehelper;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import apps.service.ServiceMain;

import com.opencsv.CSVWriter;

public class CSVHelper {
	private static final Logger logger = Logger.getLogger(CSVHelper.class);

	private String _sql;
	private String _driver;
	private String _url;
	private ServiceMain serviceMain;

	public CSVHelper(long id, String sql, String driver, String url) {
		_sql = sql;
		_driver = driver;
		_url = url;

		serviceMain = new ServiceMain();
		Connection connection = null;
		try {
			Class.forName(_driver).newInstance();
			connection = DriverManager.getConnection(_url);
			String filename = serviceMain.getPropSetting("location.csv") + "/"
					+ serviceMain.filename("csv");

			PreparedStatement preparedStatement = connection
					.prepareStatement(_sql);

			ResultSet resultSet = null;
			try {
				resultSet = preparedStatement.executeQuery();

				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				int columnCount = resultSetMetaData.getColumnCount();

				FileWriter topFileWriter = new FileWriter(filename, true);
				CSVWriter topCsvWriter = new CSVWriter(topFileWriter);

				String[] tableName = new String[columnCount];
				for (int i = 0; i < columnCount; i++) {
					tableName[i] = resultSetMetaData.getTableName(i + 1);
				}

				topCsvWriter.writeNext(tableName, false);

				String[] columnName = new String[columnCount];
				for (int i = 0; i < columnCount; i++) {
					columnName[i] = resultSetMetaData.getColumnName(i + 1);
				}

				topCsvWriter.writeNext(columnName, false);

				topCsvWriter.close();
				topFileWriter.close();

				String[] rowVAlue = new String[columnCount];
				while (resultSet.next()) {
					serviceMain.showMemory();

					FileWriter fileWriter = new FileWriter(filename, true);
					CSVWriter writer = new CSVWriter(fileWriter);

					for (int i = 0; i < columnCount; i++) {
						if (resultSet.getString(i + 1) == null) {
							rowVAlue[i] = "";
						} else {
							rowVAlue[i] = resultSet.getString(i + 1);
						}
					}

					writer.writeNext(rowVAlue, true);

					writer.close();
					fileWriter.close();

				}

				serviceMain.updateActivity(id, new File(filename), "csv",
						"Complete");

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				serviceMain.updateActivity(id, null, null, e.getMessage());
			} finally {
				if (resultSet != null) {
					try {
						resultSet.close();
					} catch (SQLException e) {
						logger.error(" on Close" + e.getMessage(), e);
					}
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
