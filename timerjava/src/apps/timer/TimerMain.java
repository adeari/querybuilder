package apps.timer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import apps.filehelper.CSVHelper;
import apps.filehelper.ExcelHelper;
import apps.filehelper.PdfHelper;
import apps.service.ServiceMain;

public class TimerMain {
	private static final Logger logger = Logger.getLogger(TimerMain.class);
	private Timer timer;
	private Timer timerRefreshLink;
	private ServiceMain serviceMain;

	public TimerMain() {
		serviceMain = new ServiceMain();
		timer = new Timer();
		timerRefreshLink = new Timer();
		// in 1 minutes
		// timer.schedule(new RemindTask(), 0, 60 * 1000);
		// timerRefreshLink.schedule(new RefreshLink(), 0, 60 * 1000);

		// in 1 seconds
		// timer.schedule(new RemindTask(), 0, 1 * 1000);
		// timerRefreshLink.schedule(new RefreshLink(), 0, 1 * 1000);

		timer.schedule(new RemindTask(), 0, 6);
		timerRefreshLink.schedule(new RefreshLink(), 0, 2);
	}

	class RefreshLink extends TimerTask {
		public void run() {
			Connection connection = null;
			try {
				Class.forName(serviceMain.getPropSetting("database.driver"))
						.newInstance();
				connection = DriverManager.getConnection(serviceMain
						.getPropSetting("database.url"));

				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT COUNT(*) AS counti FROM tb_file WHERE TIMESTAMPDIFF(MONTH, updated_at, now()) >= 3 ");
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					if (resultSet.getLong("counti") > 0) {

						preparedStatement = connection
								.prepareStatement("SELECT id FROM tb_file WHERE TIMESTAMPDIFF(MONTH, updated_at, now()) >= 3 limit 100");
						resultSet = preparedStatement.executeQuery();
						preparedStatement = connection
								.prepareStatement("UPDATE tb_file SET download_link= ?, updated_at = now() WHERE id = ?");
						while (resultSet.next()) {
							preparedStatement.setString(1,
									serviceMain.downloadlink());
							preparedStatement.setLong(2, resultSet.getLong(1));
							preparedStatement.addBatch();
						}
						preparedStatement.executeBatch();
					}
				}
				resultSet.close();
				preparedStatement.close();

			} catch (InstantiationException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (IllegalAccessException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (ClassNotFoundException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (SQLException e1) {
				logger.error(e1.getMessage(), e1);
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						logger.error(" on Close" + e.getMessage(), e);
					}
				}
			}
		}
	}

	class RemindTask extends TimerTask {
		public void run() {

			Connection connection = null;
			try {
				Class.forName(serviceMain.getPropSetting("database.driver"))
						.newInstance();
				connection = DriverManager.getConnection(serviceMain
						.getPropSetting("database.url"));

				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT id, filetype, query, driver, connection_string FROM tb_activity WHERE done_at IS NULL LIMIT 10");

				ResultSet resultSet = null;
				try {
					resultSet = preparedStatement.executeQuery();

					while (resultSet.next()) {
						if (resultSet.getString("filetype").equalsIgnoreCase(
								"CSV")) {
							new CSVHelper(resultSet, "csv");
						} else if (resultSet.getString("filetype")
								.equalsIgnoreCase("Excel")) {
							new ExcelHelper(resultSet);
						} else if (resultSet.getString("filetype")
								.equalsIgnoreCase("PDF")) {
							new PdfHelper(resultSet);
						} else if (resultSet.getString("filetype")
								.equalsIgnoreCase("Text")) {
							new CSVHelper(resultSet, "txt");
						}
					}

				} finally {
					resultSet.close();
					preparedStatement.close();
				}

			} catch (InstantiationException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (IllegalAccessException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (ClassNotFoundException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (SQLException e1) {
				logger.error(e1.getMessage(), e1);
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						logger.error(" on Close" + e.getMessage(), e);
					}
				}
			}
		}
	}

	public static void main(String args[]) {
		new TimerMain();
	}
}
