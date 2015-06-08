package apps.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import apps.service.serviceMain;

public class mainWindows extends GenericForwardComposer<Window> {
	private static final long serialVersionUID = -2091055007101580190L;
	private static final Logger logger = Logger.getLogger(mainWindows.class);
	
	private String _driverName; 
	private String _url; 
	/*private String _host; 
	private String _port; 
	private String _databaseName; 
	private String _userName; 
	private String _password;*/
	
	//Component
	private Window windowMain;
	// End Component
	
	@Override
	public void doAfterCompose(Window window) throws Exception {
		super.doAfterCompose(window);
				
		Borderlayout borderlayout = new Borderlayout();
		borderlayout.setStyle("position:absolute; top:0; bottom:0; right:0; left:0;");
		borderlayout.setWidth("100%");
		
		Center center = new Center();
		center.setParent(borderlayout);
		center.setAutoscroll(true);
		
		Grid grid = new Grid();
		Rows rows = new Rows();
		
		Row rowQuery = new Row();
		rowQuery.appendChild(new Label("Query :"));
		rowQuery.setParent(rows);
		
		Row rowTextQuery = new Row();
		final Textbox textQuery = new Textbox();
		textQuery.setWidth("100%");
		textQuery.setRows(6);
		textQuery.setParent(rowTextQuery);
		rowTextQuery.setParent(rows);
		
		Row rowButtonRun = new Row();
		rowButtonRun.setStyle("text-align: center;");
		Button buttonRun = new Button("Run ...");
		buttonRun.setParent(rowButtonRun);
		rowButtonRun.setParent(rows);
		
		final Row rowResult = new Row();
		
		final Label labelResult = new Label("....");
		rowResult.appendChild(labelResult);
				
		rowResult.setParent(rows);
		
		rows.setParent(grid);
		grid.setParent(center);
		
		
		West weast = new West();		
		weast.setWidth("400px");
		weast.setAutoscroll(true);
		weast.setTitle("Database Panel");
		weast.setCollapsible(true);
		weast.setSplittable(true);
		weast.setParent(borderlayout);
		
		Tree treeData = new Tree();
		treeData.setWidth("100%");
		Treechildren treechildrenTreeDAta = new Treechildren();
		addTreeData("sqlserver", "NAME", treechildrenTreeDAta, textQuery);
		addTreeData("mysql", "table_name", treechildrenTreeDAta, textQuery);
		treechildrenTreeDAta.setParent(treeData);
		treeData.setParent(weast);
										
		borderlayout.setParent(windowMain);
		
		buttonRun.addEventListener("onClick", new EventListener<Event>() {
		    public void onEvent(Event event) {
		    	if (textQuery.getValue().length() > 0) {
		    			rowResult.removeChild(rowResult.getChildren().get(0));
			    		rowResult.appendChild(serviceMain.getResultGrid(
								textQuery.getValue().trim(), 
								_driverName, _url));
		    	} else {
		    		Messagebox.show("Enter query", "Information", Messagebox.OK, Messagebox.INFORMATION);
		    	}
		    	
		    }
		});
	}
	
	private void addTreeData(final String databaseKind, String getTableName,
			Treechildren treechildrenTreeDAta, final Textbox textQuery) {
		
		boolean hasConnectionSQLServer = true;
		int indexDataSqlServer = 0;
		while (hasConnectionSQLServer) {
			if (serviceMain.getPropSetting(databaseKind+".name"
					+ indexDataSqlServer) == null) {
				hasConnectionSQLServer = false;
			} else {
				final int indexDataSqlServerFinal = indexDataSqlServer;
				String databaseName = serviceMain.getPropSetting(databaseKind+".name"
						+ indexDataSqlServer);
				final Treeitem treeitemDatabase = new Treeitem(databaseName + "  ("+databaseKind+")");
				
				try {
					Connection connection = serviceMain.getConnection(serviceMain.getPropSetting(databaseKind+".driver"
								+ indexDataSqlServer), serviceMain.getPropSetting(databaseKind+".url"
										+ indexDataSqlServer));
					
					
					PreparedStatement preparedStatement = connection
							.prepareStatement(serviceMain.getQuery(databaseKind+".getAllTable"));
					if (databaseKind.equalsIgnoreCase("mysql")) {
						preparedStatement.setString(1, databaseName);
					}
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
															+ indexDataSqlServerFinal));
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
															+ indexDataSqlServerFinal));
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
															+ indexDataSqlServerFinal));
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
															+ indexDataSqlServerFinal));
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
															+ indexDataSqlServerFinal));
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
	
	private void setSelectResult(Textbox textQuery,
			String queSelectFinal, 
			String driverName,
			String url
			) {
		textQuery.setValue(queSelectFinal);
		_driverName = driverName;
		_url = url;		
	}
}
