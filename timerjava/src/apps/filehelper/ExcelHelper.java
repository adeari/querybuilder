package apps.filehelper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;

import apps.beans.AdvancedObject;
import apps.service.ServiceMain;

public class ExcelHelper {

	private static final Logger logger = Logger.getLogger(ExcelHelper.class);

	private String _sql;
	private String _driver;
	private String _url;
	private ServiceMain serviceMain;
	private long _id;

	public ExcelHelper(ResultSet resultSetData) {
		serviceMain = new ServiceMain();

		Connection connection = null;
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		String filename = serviceMain.getPropSetting("location.excel") + "/"
				+ serviceMain.filename("xls");
		File file = new File(filename);
		WritableWorkbook writeWorkbook = null;
		WritableWorkbook writableWorkbook = null;
		AdvancedObject advancedObject = new AdvancedObject();
		try {
			writeWorkbook = Workbook.createWorkbook(file, ws);
			_id = resultSetData.getLong("id");
			_sql = resultSetData.getString("query");
			_driver = resultSetData.getString("driver");
			_url = resultSetData.getString("connection_string");

			Class.forName(_driver).newInstance();
			connection = DriverManager.getConnection(_url);

			PreparedStatement preparedStatement = connection
					.prepareStatement(_sql);

			ResultSet resultSet = null;
			try {
				resultSet = preparedStatement.executeQuery();

				advancedObject.setMemoryMax(serviceMain.getMemoryMax());
				long memoryUsedNow = serviceMain.getMemoryUsed();
				advancedObject.setMemoryUsed(memoryUsedNow);
				advancedObject
						.setStartAt(new Timestamp((new Date()).getTime()));

				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				int columnCount = resultSetMetaData.getColumnCount();

				WritableSheet writableSheet = writeWorkbook.createSheet("data",
						0);
				int rowUsed = 0;
				for (int i = 0; i < columnCount; i++) {
					writableSheet.addCell(new Label(i, rowUsed,
							resultSetMetaData.getColumnName(i + 1)));

				}
				writeWorkbook.write();
				writeWorkbook.close();
				writeWorkbook = null;

				rowUsed++;

				while (resultSet.next()) {
					Workbook workbook = Workbook.getWorkbook(file);

					writableWorkbook = Workbook.createWorkbook(file, workbook);
					WritableSheet writableSheet2 = writableWorkbook.getSheet(0);
					for (int i = 0; i < columnCount; i++) {
						try {
							writableSheet2.addCell(new Label(i, rowUsed,
									resultSet.getString(i + 1)));
						} catch (Exception e) {
							writableSheet2.addCell(new Label(i, rowUsed, ""));
						}
					}
					rowUsed++;

					serviceMain.showMemory();
					memoryUsedNow = serviceMain.getMemoryUsed();
					if (advancedObject.getMemoryUsed() < memoryUsedNow) {
						advancedObject.setMemoryUsed(memoryUsedNow);
					}

					writableWorkbook.write();
					writableWorkbook.close();
					writableWorkbook = null;
				}

				serviceMain.updateActivity(_id, new File(filename), "Excel",
						"Complete", advancedObject);
			} catch (RowsExceededException e) {
				logger.error(e.getMessage(), e);
				serviceMain.updateActivity(_id, null, null, e.getMessage(),
						advancedObject);
			} catch (WriteException e) {
				logger.error(e.getMessage(), e);
				serviceMain.updateActivity(_id, null, null, e.getMessage(),
						advancedObject);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				serviceMain.updateActivity(_id, null, null, e.getMessage(),
						advancedObject);
			} catch (BiffException e) {
				logger.error(e.getMessage(), e);
				serviceMain.updateActivity(_id, null, null, e.getMessage(),
						advancedObject);
			} finally {
				if (resultSet != null) {
					try {
						resultSet.close();
						resultSet = null;
					} catch (SQLException e) {
					}
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			serviceMain.updateActivity(_id, null, null, e.getMessage(),
					advancedObject);
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
			if (writeWorkbook != null) {
				try {
					writeWorkbook.close();
				} catch (Exception e) {
				}
			}
			if (writableWorkbook != null) {
				try {
					writableWorkbook.close();
				} catch (Exception e) {
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}

		}

	}
}
