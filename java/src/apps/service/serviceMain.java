package apps.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

public class serviceMain {
	private static final Logger logger = Logger.getLogger(serviceMain.class);
	private static String _fileproperties = "data.properties";
	private static String _queryProperties = "query.properties";
	private static String[] columnTypeDate = new String[]{"java.sql.Timestamp"};
	
	
	public static String getPropSetting(String key) {
		try {
			InputStream  input = serviceMain.class.getClassLoader().getResourceAsStream(_fileproperties);
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
	
	public static String getQuery(String key) {
		try {
			InputStream  input = serviceMain.class.getClassLoader().getResourceAsStream(_queryProperties);
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
	
	public static  void handleMessage(Exception ex) {
		 Messagebox.show(ex.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
		 logger.error(ex.getMessage(), ex);
	}
	
	public static Connection getConnection(String driverName, 
			String url, 
			String host, 
			String port, 
			String databaseName, 
			String userName, 
			String password) {
		try {
			  Class.forName(driverName).newInstance();
			  Connection conn = DriverManager.getConnection(url+host+":"+port+"/"+databaseName
					  +"?autoReconnect=true&amp;allowMultiQueries=true&amp;zeroDateTimeBehavior=convertToNull",userName,password);
			  return conn;
		  } catch (Exception ex) {
			  handleMessage(ex);
		  }
		return null;
	}
	
	public static Connection getConnection(String driverName, 
			String url) {
		try {
			  Class.forName(driverName).newInstance();
			  Connection conn = DriverManager.getConnection(url);
			  return conn;
		  } catch (Exception ex) {
			  handleMessage(ex);
		  }
		return null;
	}
	
	/*public static Component getResultGrid(String sql, 
			String driverName, 
			String url, 
			String host, 
			String port, 
			String databaseName, 
			String userName, 
			String password) {
		String connectionName = "mysql."+driverName+".";
    	
    	try {
	    	Connection connection = getConnection(driverName, 
	    			url, 
	    			host, 
	    			port, 
	    			databaseName, 
	    			userName, 
	    			password);
	    	PreparedStatement preparedStatement = connection.prepareStatement(sql);
	    	
			if (sql.toUpperCase().startsWith("SELECT")) {
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					Grid gridResult = new Grid();
					gridResult.setSizedByContent(true);
					gridResult.setVflex(true);
					gridResult.setAutopaging(true);
					gridResult.setMold("paging");
					gridResult.setHeight("360px");
					Columns columnsGridResult = new Columns();

					ResultSetMetaData resultSetMetaData = resultSet
							.getMetaData();
					for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
						Column column = new Column(resultSetMetaData
								.getColumnName(i).toUpperCase());
						column.setParent(columnsGridResult);
					}
					columnsGridResult.setParent(gridResult);

					resultSet.beforeFirst();
					Rows rowsResult = new Rows();
					rowsResult.setStyle("overflow: scroll;");

					while (resultSet.next()) {
						Row rowResult = new Row();
						for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
							Label labelResult = null;
							
							if (Arrays.asList(columnTypeDate).contains(resultSetMetaData.getColumnClassName(i))) {
								try {
								labelResult = new Label(
										resultSet.getTimestamp(resultSetMetaData
												.getColumnName(i)).toString());
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
			} else {
				preparedStatement.executeUpdate();
				Label labelResult = new Label("Process Done");
				return labelResult;
			}
    	} catch (Exception ex) {
    		logger.error(ex.getMessage(), ex);
    		Label labelResult = new Label(ex.getMessage());
			return labelResult;
    	}
	}*/
	
	public static Component getResultGrid(String sql, 
			String driverName, 
			String url) {
		
		try {
			Connection connection = getConnection(driverName, url);
			sql = sql.trim();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			
			if (sql.toUpperCase().startsWith("SELECT")) {
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
							
							if (Arrays.asList(columnTypeDate).contains(resultSetMetaData.getColumnClassName(i))) {
								try {
									labelResult = new Label(
											resultSet.getTimestamp(resultSetMetaData
													.getColumnName(i)).toString());
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
			} else {
				System.out.println(sql);
				preparedStatement.executeUpdate();
				Label labelResult = new Label("Process Done");
				return labelResult;
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			Label labelResult = new Label("Error "+ex.getMessage());
			return labelResult;
		}
	}
	
	public static String getValueColumn(String columnName, String columnType, int columnLength){
		String valueInsert = "null";
		if (columnType.equalsIgnoreCase("java.lang.String")) {
			valueInsert = columnName;
			if (valueInsert.length() > columnLength) {
				valueInsert = valueInsert.substring(0, columnLength);
			}
			valueInsert = "'"+valueInsert+"'";
		} else if (Arrays.asList(columnTypeDate).contains(columnType)) {
			valueInsert = "'"+new Date()+"'";
		} else {
			valueInsert = "1";
		}
		return valueInsert;
	}
	
	public static void setTreeData(final String databaseKind, String getTableName, final Textbox textQuery,
			Window windowMain, final String _driverName, final String _url, Treechildren treechildrenTreeDAta) {
		
		boolean hasConnectionSQLServer = true;
		int indexDataSqlServer = 0;
		while (hasConnectionSQLServer) {
			if (serviceMain.getPropSetting(databaseKind+".name"
					+ indexDataSqlServer) == null) {
				hasConnectionSQLServer = false;
			} else {
				final int indexDataSqlServerFinal = indexDataSqlServer;
				final Treeitem treeitemDatabase = new Treeitem(
						serviceMain.getPropSetting(databaseKind+".name"
								+ indexDataSqlServer));
				
				
				try {
					Connection connection = serviceMain.getConnection(serviceMain.getPropSetting(databaseKind+".driver"
								+ indexDataSqlServer), serviceMain.getPropSetting(databaseKind+".url"
										+ indexDataSqlServer));
					
					
					PreparedStatement preparedStatement = connection
							.prepareStatement(serviceMain.getQuery(databaseKind+".getAllTable"));
					final ResultSet resultSetTable = preparedStatement.executeQuery();
			    	Treechildren treechildrenTable = new Treechildren();
			    	while (resultSetTable.next()) {
			    		final String tableName = resultSetTable.getString(getTableName);
			    		final Treeitem treeitemTable = new Treeitem(tableName.toUpperCase());
			    		treeitemTable.addEventListener("onClick", new EventListener<Event>() {
						    public void onEvent(Event event) {
						    	if (treeitemTable.isSelected()) {
							    	if (treeitemTable.isOpen()) {
							    		treeitemTable.setOpen(false);
							    	} else {
							    		treeitemTable.setOpen(true);
							    	}
						    	}
						    }
						});
			    		
			    		try {
				    		Treechildren treechildrenColumn = new Treechildren();
				    		preparedStatement = connection.prepareStatement(
				    				serviceMain.getQuery("mysql.getAllColumn")+tableName);
				    		ResultSet resultSetColumn = preparedStatement.executeQuery();
				    		final ResultSetMetaData resultSetMetaData = resultSetColumn.getMetaData();
				    		String querySelect = "";
				    		String queryUpdateValues = " ";
				    		String queryInsertValues = " (";
				    		for (int x = 1; x <= resultSetMetaData.getColumnCount(); x++) {
				    			String ColumnName = resultSetMetaData.getColumnName(x);
				    			String columnType = resultSetMetaData.getColumnClassName(x);
				    			Treeitem treeitemColumn = new Treeitem(ColumnName);
				    			String columnValue = serviceMain.getValueColumn(ColumnName, 
			    						columnType, resultSetMetaData.getColumnDisplaySize(x));
				    			
				    			if (x == 1) {
				    				querySelect += ColumnName;
				    				queryInsertValues += columnValue;
				    				queryUpdateValues += ColumnName +" = " +columnValue;
								} else {
									querySelect += ", " + ColumnName;
									queryInsertValues += ", " + columnValue;
									queryUpdateValues += ", "+ ColumnName +" = " +columnValue;
								}
				    			
				    			final String condition = ColumnName+" = "+columnValue;
				    			
				    			String conditionBeforeAdded = " "+condition;
						    	if (textQuery.getValue().length() > 0) {
						    		conditionBeforeAdded = "\n"+condition;
						    	}
						    	final String conditionAdded = conditionBeforeAdded;
						    	
						    	treeitemColumn.addEventListener("onDoubleClick", new EventListener<Event>() {
								    public void onEvent(Event event) {
								    	textQuery.setValue(textQuery.getValue()+conditionAdded);
								    }
								});
				    			
				    			Menupopup menupopupItemColumn = new Menupopup();
				    			Menuitem menuitemAddCondition = new Menuitem("Add condition");
				    			menuitemAddCondition.addEventListener("onClick", new EventListener<Event>() {
								    public void onEvent(Event event) {
								    	textQuery.setValue(textQuery.getValue()+conditionAdded);
								    }
								});
				    			menuitemAddCondition.setParent(menupopupItemColumn);
				    			Menuitem menuitemAndCondition = new Menuitem("AND condition");
				    			menuitemAndCondition.addEventListener("onClick", new EventListener<Event>() {
				    				public void onEvent(Event event) {
				    					String conditionAddedAnd = " AND "+condition;
								    	if (textQuery.getValue().length() > 0) {
								    		conditionAddedAnd = "\n"+condition;
								    	}
								    	textQuery.setValue(textQuery.getValue()+conditionAddedAnd);
				    				}
				    			});
				    			menuitemAndCondition.setParent(menupopupItemColumn);
				    			Menuitem menuitemORCondition = new Menuitem("OR condition");
				    			menuitemORCondition.addEventListener("onClick", new EventListener<Event>() {
				    				public void onEvent(Event event) {
				    					String conditionAddedOr = " OR "+condition;
								    	if (textQuery.getValue().length() > 0) {
								    		conditionAddedOr = "\n"+condition;
								    	}
								    	textQuery.setValue(textQuery.getValue()+conditionAddedOr);
				    				}
				    			});
				    			menuitemORCondition.setParent(menupopupItemColumn);
					    		
				    			menupopupItemColumn.setParent(windowMain);
				    			treeitemColumn.setContext(menupopupItemColumn);
				    			treeitemColumn.setParent(treechildrenColumn);
				    		}
				    		final String querySelectFinal = "SELECT "+querySelect+" FROM "+tableName;
				    		
				    		queryInsertValues += ") ";
				    		final String queryInsertFinal = "INSERT INTO "+tableName+" ("+querySelect+") VALUES "+queryInsertValues;
				    		final String queryUpdateFinal = "UPDATE "+tableName+" SET "+queryUpdateValues+" WHERE ";
				    		final String queryDeleteFinal = "DELETE FROM "+tableName+" WHERE ";
				    		
				    		treeitemTable.addEventListener("onDoubleClick", new EventListener<Event>() {
							    public void onEvent(Event event) {
							    	setSelectResult(textQuery, 
							    			querySelectFinal,
							    			serviceMain.getPropSetting(databaseKind+".driver"
													+ indexDataSqlServerFinal), 
											serviceMain.getPropSetting(databaseKind+".url"
															+ indexDataSqlServerFinal),
															_driverName,
															_url);
							    }
							});
				    		
				    		Menupopup menupopupItemTable = new Menupopup();
				    		Menuitem menuitemPopupItemTableSelect = new Menuitem("Select");
				    		menuitemPopupItemTableSelect.addEventListener("onClick", new EventListener<Event>() {
							    public void onEvent(Event event) {
							    	setSelectResult(textQuery, 
							    			querySelectFinal,
							    			serviceMain.getPropSetting(databaseKind+".driver"
													+ indexDataSqlServerFinal), 
											serviceMain.getPropSetting(databaseKind+".url"
															+ indexDataSqlServerFinal),
															_driverName,
															_url);
							    }
							});
				    		menuitemPopupItemTableSelect.setParent(menupopupItemTable);
				    		Menuitem menuitemPopupItemTableInsert = new Menuitem("Insert");
				    		menuitemPopupItemTableInsert.addEventListener("onClick", new EventListener<Event>() {
							    public void onEvent(Event event) {
							    	setSelectResult(textQuery, 
							    			queryInsertFinal,
							    			serviceMain.getPropSetting(databaseKind+".driver"
													+ indexDataSqlServerFinal), 
											serviceMain.getPropSetting(databaseKind+".url"
															+ indexDataSqlServerFinal),
															_driverName,
															_url);
							    }
							});
				    		menuitemPopupItemTableInsert.setParent(menupopupItemTable);
				    		Menuitem menuitemPopupItemTableUpdate = new Menuitem("Update");
				    		menuitemPopupItemTableUpdate.addEventListener("onClick", new EventListener<Event>() {
							    public void onEvent(Event event) {
							    	setSelectResult(textQuery, 
							    			queryUpdateFinal,
							    			serviceMain.getPropSetting(databaseKind+".driver"
													+ indexDataSqlServerFinal), 
											serviceMain.getPropSetting(databaseKind+".url"
															+ indexDataSqlServerFinal),
															_driverName,
															_url);
							    }
							});
				    		menuitemPopupItemTableUpdate.setParent(menupopupItemTable);
				    		
				    		Menuitem menuitemPopupItemTableDelete = new Menuitem("Delete");
				    		menuitemPopupItemTableDelete.addEventListener("onClick", new EventListener<Event>() {
							    public void onEvent(Event event) {
							    	setSelectResult(textQuery, 
							    			queryDeleteFinal,
							    			serviceMain.getPropSetting(databaseKind+".driver"
													+ indexDataSqlServerFinal), 
											serviceMain.getPropSetting(databaseKind+".url"
															+ indexDataSqlServerFinal),
															_driverName,
															_url);
							    }
							});
				    		menuitemPopupItemTableDelete.setParent(menupopupItemTable);
				    		menupopupItemTable.setParent(windowMain);
				    		
				    		treeitemTable.setContext(menupopupItemTable);
				    		
				    		resultSetColumn.close();
				    		treechildrenColumn.setParent(treeitemTable);
			    		} catch (Exception ex) {
				    	}
			    		
			    					    		
			    		treeitemTable.setParent(treechildrenTable);
			    		
			    		
			    		treeitemTable.setOpen(false);
			    		treeitemTable.setParent(treechildrenTable);
			    	}
			    	treechildrenTable.setParent(treeitemDatabase);
			    	
					connection.close();
				} catch (Exception ex) {
					serviceMain.handleMessage(ex);
				}
				
				treeitemDatabase.setParent(treechildrenTreeDAta);
				indexDataSqlServer++;
			}
		}
	}
	
	private static void setSelectResult(Textbox textQuery,
			String queSelectFinal, 
			String driverName,
			String url,
			String _driverName,
			String _url			
			) {
		textQuery.setValue(queSelectFinal);
		_driverName = driverName;
		_url = url;		
	}
}
