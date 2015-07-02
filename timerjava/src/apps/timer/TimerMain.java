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
	private ServiceMain serviceMain;

	public static void main(String args[]) {
		new TimerMain();
	}

	public TimerMain() {
		serviceMain = new ServiceMain();
		timer = new Timer();
		// in 1 minutes
		// timer.schedule(new RemindTask(), 0, 60 * 1000);

		// in 1 seconds
		// timer.schedule(new RemindTask(), 0, 1 * 1000);

		timer.schedule(new RemindTask(), 0, 6);
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
						} else if (resultSet.getString("filetype").equalsIgnoreCase(
								"Excel")) {
							new ExcelHelper(resultSet);
						} else if (resultSet.getString("filetype").equalsIgnoreCase(
								"PDF")) {
							new PdfHelper(resultSet);
						} else if (resultSet.getString("filetype").equalsIgnoreCase(
								"Text")) {
							new CSVHelper(resultSet, "txt");
						}
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

}
