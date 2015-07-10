package apps.controller.querycontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import apps.service.ServiceImplMain;
import apps.service.ServiceMain;

public class QueryProperties extends Window  {
	private static final long serialVersionUID = 155960648161153061L;
	private static final Logger logger = Logger
			.getLogger(QueryProperties.class);
	private Window window;
	
	private ServiceMain serviceMain;
	
	public QueryProperties(String title, String databaseDriver, String databaseUrl, String tableName) {
		super(title, null, true);
		window = this;
		window.setStyle("width: 500px; height: 400px;");
		
		serviceMain = new ServiceImplMain();
		
		Listbox listbox = new Listbox();
		listbox.setParent(window);
		listbox.setMold("paging");
		listbox.setAutopaging(true);
		listbox.setVflex(true);
		listbox.setPagingPosition("bottom");
		
		Listhead listhead = new Listhead();
		listhead.setParent(listbox);
		listhead.setSizable(true);
		
		Listheader columnNameListheader = new Listheader("Column");
		columnNameListheader.setParent(listhead);
		
		Listheader typeListheader = new Listheader("Type");
		typeListheader.setParent(listhead);
		
		Listheader sizeListheader = new Listheader("Size");
		sizeListheader.setStyle("text-align: right;");
		sizeListheader.setParent(listhead);
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = serviceMain.getConnection(databaseDriver,
					databaseUrl);
			String sql = "SELECT * FROM "+tableName+" LIMIT 1";
			if (databaseDriver.equalsIgnoreCase("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
				sql = "SELECT TOP(1) * FROM "+tableName+"";
			}
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			ResultSetMetaData resultSetMetaData = resultSet
					.getMetaData();
			for (int x = 1; x <= resultSetMetaData
					.getColumnCount(); x++) {
				Listitem listitem = new Listitem();
				listitem.setParent(listbox);
				
				Listcell columnNameListcell = new Listcell(resultSetMetaData.getColumnName(x));
				columnNameListcell.setParent(listitem);
				
				Listcell typeColumnListcell = new Listcell(resultSetMetaData.getColumnTypeName(x));
				typeColumnListcell.setParent(listitem);
				
				Listcell sizeColumnListcell = new Listcell(String.valueOf(resultSetMetaData.getColumnDisplaySize(x)));
				sizeColumnListcell.setStyle("text-align: right;");
				sizeColumnListcell.setParent(listitem);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (Exception e) {
					
				}
			}
			if (resultSet != null) {
				try {
					resultSet.close();
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
