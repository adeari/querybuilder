package apps.filehelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import apps.beans.AdvancedObject;
import apps.service.ServiceMain;

import com.opencsv.CSVWriter;

public class CSVHelper {
	private static final Logger logger = Logger.getLogger(CSVHelper.class);

	private String _sql;
	private String _driver;
	private String _url;
	private ServiceMain serviceMain;
	private long _id;

	public CSVHelper(ResultSet resultSetData, String extension) {
		serviceMain = new ServiceMain();
		Connection connection = null;
		FileWriter topFileWriter = null;
		CSVWriter topCsvWriter = null;
		FileWriter fileWriter = null;
		CSVWriter writer = null;

		AdvancedObject advancedObject = new AdvancedObject();

		String filename = serviceMain.getPropSetting("location." + extension)
				+ "/" + serviceMain.filename(extension);
		try {
			_id = resultSetData.getLong("id");
			_sql = resultSetData.getString("query");
			_driver = resultSetData.getString("driver");
			_url = resultSetData.getString("connection_string");

			advancedObject.setMemoryMax(serviceMain.getMemoryMax());
			long memoryUsedNow = serviceMain.getMemoryUsed();
			advancedObject.setMemoryUsed(memoryUsedNow);
			advancedObject.setStartAt(new Timestamp((new Date()).getTime()));

			Class.forName(_driver).newInstance();
			connection = DriverManager.getConnection(_url);

			PreparedStatement preparedStatement = connection
					.prepareStatement(_sql);

			ResultSet resultSet = null;

			try {
				resultSet = preparedStatement.executeQuery();

				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				int columnCount = resultSetMetaData.getColumnCount();

				topFileWriter = new FileWriter(filename, true);
				topCsvWriter = new CSVWriter(topFileWriter);

				/*
				 * String[] tableName = new String[columnCount+1]; for (int i =
				 * 0; i < columnCount; i++) { tableName[i] =
				 * resultSetMetaData.getTableName(i + 1); }
				 * tableName[columnCount] = "<- Tablename's column";
				 * 
				 * topCsvWriter.writeNext(tableName, false);
				 */

				String[] columnName = new String[columnCount];
				for (int i = 0; i < columnCount; i++) {
					columnName[i] = resultSetMetaData.getColumnName(i + 1);
				}

				topCsvWriter.writeNext(columnName, false);

				topCsvWriter.close();
				topFileWriter.close();
				topCsvWriter = null;
				topFileWriter = null;

				String[] rowVAlue = new String[columnCount];
				while (resultSet.next()) {
					fileWriter = new FileWriter(filename, true);
					writer = new CSVWriter(fileWriter);

					for (int i = 0; i < columnCount; i++) {
						try {
							if (resultSet.getString(i + 1) == null) {
								rowVAlue[i] = "";
							} else {
								rowVAlue[i] = resultSet.getString(i + 1);
							}
						} catch (Exception e) {
							rowVAlue[i] = "";
						}
					}

					writer.writeNext(rowVAlue, true);

					serviceMain.showMemory();
					memoryUsedNow = serviceMain.getMemoryUsed();
					if (advancedObject.getMemoryUsed() < memoryUsedNow) {
						advancedObject.setMemoryUsed(memoryUsedNow);
					}
					writer.close();

					fileWriter.close();
					writer = null;
					fileWriter = null;
				}

				serviceMain.updateActivity(_id, new File(filename), extension,
						"Complete", advancedObject);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				serviceMain.updateActivity(_id, null, null, e.getMessage(),
						advancedObject);
			} finally {
				resultSet.close();
				preparedStatement.close();
			}
			
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			serviceMain.updateActivity(_id, null, null, e.getMessage(),
					advancedObject);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			serviceMain.updateActivity(_id, null, null, e.getMessage(),
					advancedObject);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			serviceMain.updateActivity(_id, null, null, e.getMessage(),
					advancedObject);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			serviceMain.updateActivity(_id, null, null, e.getMessage(),
					advancedObject);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
				}
			}
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (Exception e) {
				}
			}
			if (topCsvWriter != null) {
				try {
					topCsvWriter.close();
				} catch (Exception e) {
				}
			}
			if (topFileWriter != null) {
				try {
					topFileWriter.close();
				} catch (Exception e) {
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
				}
			}
		}

	}
}
