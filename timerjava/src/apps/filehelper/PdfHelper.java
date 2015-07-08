package apps.filehelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfHelper implements PdfPTableEvent {

	private static final Logger logger = Logger.getLogger(PdfHelper.class);

	private String _sql;
	private String _driver;
	private String _url;
	private ServiceMain serviceMain;
	private long _id;

	public PdfHelper() {

	}

	public PdfHelper(ResultSet resultSetData) {
		serviceMain = new ServiceMain();

		Connection connection = null;
		String filename = serviceMain.getPropSetting("location.pdf") + "/"
				+ serviceMain.filename("pdf");
		File file = new File(filename);
		Document document = null;
		AdvancedObject advancedObject = new AdvancedObject();

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

				if (columnCount < 7) {
					document = new Document(PageSize.LEGAL);
				} else {
					document = new Document(PageSize.LEGAL.rotate());
				}
				PdfWriter.getInstance(document, new FileOutputStream(file));
				document.setMargins(10, 10, 10, 10);
				document.setMarginMirroring(true);
				document.open();
				PdfPTableEvent event = new PdfHelper();

				float[] columnSize = new float[columnCount];
				for (int i = 0; i < columnCount; i++) {
					columnSize[i] = 4;
				}

				PdfPTable table = new PdfPTable(columnSize);
				table.setWidthPercentage(100);
				table.getDefaultCell().setUseAscender(true);
				table.getDefaultCell().setUseDescender(true);
				table.getDefaultCell().setHorizontalAlignment(
						Element.ALIGN_LEFT);
				Font BOLD = new Font(FontFamily.TIMES_ROMAN, 15, Font.BOLD);

				for (int i = 0; i < columnCount; i++) {
					table.addCell(new Phrase(resultSetMetaData
							.getColumnName(i + 1), BOLD));
				}
				table.getDefaultCell().setBackgroundColor(null);
				table.setHeaderRows(1);

				while (resultSet.next()) {
					for (int i = 0; i < columnCount; i++) {
						try {
							table.addCell(resultSet.getString(i + 1));
						} catch (Exception e) {
							table.addCell("");
						}
					}
				}

				table.setTableEvent(event);
				document.add(table);
				document.newPage();
				serviceMain.showMemory();
				memoryUsedNow = serviceMain.getMemoryUsed();
				if (advancedObject.getMemoryUsed() < memoryUsedNow) {
					advancedObject.setMemoryUsed(memoryUsedNow);
				}

				document.close();
				document = null;

				serviceMain.updateActivity(_id, new File(filename), "PDF",
						"Complete", advancedObject);

			} finally {
				if (document != null) {
					try {
						document.close();
						document = null;
					} catch (Exception e) {
					}
				}
				if (resultSet != null) {
					try {
						resultSet.close();
						resultSet = null;
					} catch (Exception e) {
					}
				}
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
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			serviceMain.updateActivity(_id, null, null, e.getMessage(),
					advancedObject);
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
			serviceMain.updateActivity(_id, null, null, e.getMessage(),
					advancedObject);
		} finally {
			if (document != null) {
				try {
					document.close();
					document = null;
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

	public void tableLayout(PdfPTable table, float[][] widths, float[] heights,
			int headerRows, int rowStart, PdfContentByte[] canvases) {
		int columns;
		Rectangle rect;
		int footer = widths.length - table.getFooterRows();
		int header = table.getHeaderRows() - table.getFooterRows() + 1;
		for (int row = header; row < footer; row += 2) {
			columns = widths[row].length - 1;
			rect = new Rectangle(widths[row][0], heights[row],
					widths[row][columns], heights[row + 1]);
			rect.setBackgroundColor(BaseColor.LIGHT_GRAY);
			rect.setBorder(Rectangle.NO_BORDER);
			canvases[PdfPTable.BASECANVAS].rectangle(rect);
		}
	}

}
