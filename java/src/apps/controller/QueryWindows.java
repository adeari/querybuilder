package apps.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import apps.components.MenuitemWithData;
import apps.components.TreeItemWithData;
import apps.controller.querycontrol.QueryProperties;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.service.CheckService;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class QueryWindows extends Window {
	private static final long serialVersionUID = -2091055007101580190L;
	private static final Logger logger = Logger.getLogger(QueryWindows.class);

	private ServiceMain serviceMain;
	private CheckService checkService;

	private String _driverName;
	private String _url;
	private QueryData _queryData;

	// Component
	private Textbox textQuery;
	private Row rowResult;
	private Label labelResult;
	private Window queryWindow;
	private Button buttonRun;
	private Button saveQueryButton;

	// End Component

	public QueryWindows(String title, QueryData queryData) {
		super(title, null, true);
		queryWindow = this;

		checkService = new CheckService();

		serviceMain = new ServiceImplMain();
		Borderlayout borderlayout = new Borderlayout();
		borderlayout
				.setStyle("position:absolute; top:0; bottom:0; right:0; left:0;border-style:none");

		North north = new North();
		north.setHeight("46px");
		north.setParent(borderlayout);
		Grid nortGrid = new Grid();
		Rows northRows = new Rows();

		Row northRow = new Row();

		Cell titleCell = new Cell();
		Label titleLabel = new Label(title);
		titleLabel.setStyle("font-weight: bold; font-size: 16px");
		titleLabel.setParent(titleCell);
		titleCell.setWidth("97%");
		titleCell.setParent(northRow);
		titleCell.setStyle("background: yellow; color: #000;");

		Cell closeCell = new Cell();
		closeCell.setStyle("background: yellow");
		Button closeButton = new Button("X");
		closeButton.setStyle("background: red; font-color: blue;");
		closeButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						queryWindow.detach();
					}
				});
		closeButton.setParent(closeCell);
		closeCell.setParent(northRow);

		northRow.setParent(northRows);
		northRows.setParent(nortGrid);
		nortGrid.setParent(north);

		Center center = new Center();
		center.setParent(borderlayout);
		center.setAutoscroll(true);

		Grid grid = new Grid();
		Rows rows = new Rows();

		Row rowQuery = new Row();
		rowQuery.appendChild(new Label("Query :"));
		rowQuery.setParent(rows);

		Row rowTextQuery = new Row();
		textQuery = new Textbox();
		textQuery.setWidth("100%");
		textQuery.setRows(6);
		textQuery.setParent(rowTextQuery);
		if (queryData != null) {
			_queryData = queryData;
			_driverName = _queryData.getDriver();
			_url = _queryData.getConnectionString();
			textQuery.setValue(_queryData.getSql());
		}
		rowTextQuery.setParent(rows);

		Row rowButtonRun = new Row();
		rowButtonRun.setStyle("text-align: center;");

		Cell runCell = new Cell();
		buttonRun = new Button("Run ...");
		runCell.appendChild(buttonRun);
		saveQueryButton = new Button("Save");
		saveQueryButton.setImage("image/save.png");
		saveQueryButton.setStyle("margin: 0 0 0 20px");
		saveQueryButton.addEventListener("onClick", new EventListener<Event>() {
			public void onEvent(Event event) {
				if (!saveQueryButton.isDisabled()) {
					saveQueryButton.setDisabled(true);

					boolean canSaved = true;

					org.hibernate.Session querySession = null;
					try {
						querySession = hibernateUtil.getSessionFactory()
								.openSession();

						if (_driverName == null || (_driverName.isEmpty())) {
							Messagebox.show("Please choose Table on left",
									"Information", Messagebox.OK,
									Messagebox.INFORMATION);
							canSaved = false;
						} else if (_url == null || (_url.isEmpty())) {
							Messagebox.show("Please choose Table on left",
									"Information", Messagebox.OK,
									Messagebox.INFORMATION);
							canSaved = false;
						} else if (textQuery.getValue().isEmpty()) {
							textQuery.setFocus(true);
							Messagebox.show("Enter query", "Information",
									Messagebox.OK, Messagebox.INFORMATION);
							canSaved = false;
						} else {
							Connection connection = null;
							try {
								connection = serviceMain.getConnection(
										_driverName, _url);
								connection.setAutoCommit(false);
								PreparedStatement preparedStatement = connection
										.prepareStatement(textQuery.getValue());
								if (textQuery.getValue().trim().toLowerCase()
										.startsWith("select")) {
									preparedStatement.executeQuery();
								} else {
									preparedStatement.executeUpdate();
								}
								preparedStatement.close();
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								Messagebox.show("Query syntax was wrong",
										"Information", Messagebox.OK,
										Messagebox.INFORMATION);
								canSaved = false;
							} finally {
								if (connection != null) {
									try {
										connection.close();
									} catch (Exception e) {
										logger.error(e.getMessage(), e);
									}
								}

							}
						}

						if (canSaved) {
							int dataSize = 0;
							Criteria criteria = querySession
									.createCriteria(QueryData.class);
							criteria.add(Restrictions.eq("sql",
									textQuery.getValue()));
							if (_queryData != null) {
								criteria.add(Restrictions.ne("id",
										_queryData.getId()));
							}
							dataSize = criteria.list().size();
							if (dataSize > 0) {
								Messagebox.show("This query already exist",
										"Information", Messagebox.OK,
										Messagebox.INFORMATION);
							} else {
								if (_queryData == null) {
									QuerySavedWindow querySavedWindow = new QuerySavedWindow(
											"Add query", _driverName, _url,
											textQuery.getValue());
									queryWindow.appendChild(querySavedWindow);
									querySavedWindow.setWidth("400px");
									querySavedWindow.doModal();

								} else {
									Transaction trx = querySession
											.beginTransaction();

									org.zkoss.zk.ui.Session sessionLocal = Sessions
											.getCurrent();
									Users user = (Users) sessionLocal
											.getAttribute("userlogin");

									Users userBefore = _queryData
											.getModifiedBy();

									_queryData.setDriver(_driverName);
									_queryData.setSql(textQuery.getValue());
									_queryData.setConnectionString(_url);
									_queryData.setModifiedBy(user);
									_queryData
											.setModifiedAt(new java.sql.Timestamp(
													new Date().getTime()));

									querySession.update(_queryData);

									user.setIsdeleted(false);
									querySession.update(user);

									trx.commit();
									if (!userBefore.equals(user)) {
										checkService.userIsDeleted(userBefore);
									}
									serviceMain.saveUserActivity("Query "
											+ _queryData.getNamed()
											+ " editted");
									queryWindow.detach();

								}
							}
						}

					} catch (Exception e) {
						logger.error(e.getMessage(), e);

					} finally {
						if (querySession != null) {
							try {
								querySession.close();
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
						}

					}

					saveQueryButton.setDisabled(false);
				}
			}
		});
		runCell.appendChild(saveQueryButton);
		rowButtonRun.appendChild(runCell);
		rows.appendChild(rowButtonRun);

		rowResult = new Row();

		labelResult = new Label("....");
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

		addTreeData("sqlserver", "NAME", treechildrenTreeDAta);
		// addTreeData("mysql", "table_name", treechildrenTreeDAta);

		treechildrenTreeDAta.setParent(treeData);
		treeData.setParent(weast);

		borderlayout.setParent(queryWindow);

		buttonRun.addEventListener("onClick", new EventListener<Event>() {
			public void onEvent(Event event) {
				if (!buttonRun.isDisabled()) {
					buttonRun.setDisabled(true);
					if (textQuery.getValue().length() > 0) {
						rowResult.removeChild(rowResult.getChildren().get(0));
						rowResult.appendChild(serviceMain.getResultGrid(
								textQuery.getValue().trim(), _driverName, _url));
					} else {
						textQuery.setFocus(true);
						Messagebox.show("Enter query", "Information",
								Messagebox.OK, Messagebox.INFORMATION);
					}
					buttonRun.setDisabled(false);
				}

			}
		});
	}

	private void addTreeData(String databaseKind, String getTableName,
			Treechildren treechildrenTreeDAta) {
		boolean hasConnectionSQLServer = true;
		int indexDataSqlServer = 0;
		while (hasConnectionSQLServer) {
			if (serviceMain.getPropSetting(databaseKind + ".name"
					+ indexDataSqlServer) == null) {
				hasConnectionSQLServer = false;
			} else {
				String databaseName = serviceMain.getPropSetting(databaseKind
						+ ".name" + indexDataSqlServer);
				String databaseDriver = serviceMain.getPropSetting(databaseKind
						+ ".driver" + indexDataSqlServer);
				String databaseUrl = serviceMain.getPropSetting(databaseKind
						+ ".url" + indexDataSqlServer);
				TreeItemWithData treeitemDatabase = new TreeItemWithData(
						databaseName + "  (" + databaseKind + ")");
				treeitemDatabase.setImage("image/database-icon.png");
				treeitemDatabase.setDriverName(databaseDriver);
				treeitemDatabase.setUrlData(databaseUrl);
				treeitemDatabase.addEventListener(Events.ON_CLICK,
						new EventListener<Event>() {
							public void onEvent(Event treeItemEvent) {
								TreeItemWithData treeItemSelectedData = (TreeItemWithData) treeItemEvent
										.getTarget();
								_driverName = treeItemSelectedData
										.getDriverName();
								_url = treeItemSelectedData.getUrlData();
							}
						});

				Menupopup databaseMenupopup = new Menupopup();
				databaseMenupopup.setParent(queryWindow);

				MenuitemWithData databaseSelectData = new MenuitemWithData(
						"Select this database");
				databaseSelectData.setParent(databaseMenupopup);
				databaseSelectData.setImage("image/database-icon.png");
				databaseSelectData.setDriverName(databaseDriver);
				databaseSelectData.setUrlData(databaseUrl);
				databaseSelectData.addEventListener(Events.ON_CLICK,
						new EventListener<Event>() {
							public void onEvent(Event databaseSelectEvent) {
								MenuitemWithData menuitemWithData = (MenuitemWithData) databaseSelectEvent
										.getTarget();
								_driverName = menuitemWithData.getDriverName();
								_url = menuitemWithData.getUrlData();
							}
						});

				treeitemDatabase.setContext(databaseMenupopup);

				Connection connection = null;
				ResultSet resultSetTable = null;
				try {
					connection = serviceMain.getConnection(databaseDriver,
							databaseUrl);

					PreparedStatement preparedStatement = connection
							.prepareStatement(serviceMain.getQuery(databaseKind
									+ ".getAllTable"));
					if (databaseKind.equalsIgnoreCase("mysql")) {
						preparedStatement.setString(1, databaseName);
					}
					resultSetTable = preparedStatement.executeQuery();
					Treechildren treechildrenTable = new Treechildren();
					while (resultSetTable.next()) {
						String tableName = resultSetTable
								.getString(getTableName);
						TreeItemWithData treeitemTable = new TreeItemWithData(
								tableName.toUpperCase());
						treeitemTable.addEventListener("onClick",
								new EventListener<Event>() {
									public void onEvent(Event treeitemTableEvent) {
										final TreeItemWithData treeitemSelected = (TreeItemWithData) treeitemTableEvent
												.getTarget();

										if (treeitemSelected.isSelected()) {
											if (treeitemSelected.isOpen()) {
												treeitemSelected.setOpen(false);
											} else {
												treeitemSelected.setOpen(true);
											}
										}
									}
								});
						ResultSet resultSetColumn = null;
						try {
							Treechildren treechildrenColumn = new Treechildren();
							treechildrenColumn.setParent(treeitemTable);
							preparedStatement = connection
									.prepareStatement(serviceMain
											.getQuery("mysql.getAllColumn")
											+ tableName);
							resultSetColumn = preparedStatement.executeQuery();
							ResultSetMetaData resultSetMetaData = resultSetColumn
									.getMetaData();

							String querySelect = "";
							String querySelectForAccess = "";
							String queryUpdateValues = " ";
							String queryInsertValues = " (";

							for (int x = 1; x <= resultSetMetaData
									.getColumnCount(); x++) {
								String ColumnName = resultSetMetaData
										.getColumnName(x);
								String columnType = resultSetMetaData
										.getColumnClassName(x);
								String columnTypeName = resultSetMetaData
										.getColumnTypeName(x);
								TreeItemWithData treeitemColumn = new TreeItemWithData(
										ColumnName);
								String columnValue = serviceMain
										.getValueColumn(
												ColumnName,
												columnType,
												resultSetMetaData
														.getColumnDisplaySize(x));

								if (x == 1) {
									querySelect += ColumnName;
									if (!columnType.equalsIgnoreCase("[B")) {
										querySelectForAccess += ColumnName;
										queryInsertValues += columnValue;
										queryUpdateValues += ColumnName + " = "
												+ columnValue;
									}
								} else {
									querySelect += ", " + ColumnName;
									if (!columnType.equalsIgnoreCase("[B")) {
										querySelectForAccess += ", " + ColumnName;
										queryInsertValues += ", " + columnValue;
										queryUpdateValues += ", " + ColumnName
												+ " = " + columnValue;
									}
								}

								String condition = ColumnName + " = "
										+ columnValue;

								String conditionBeforeAdded = " " + condition;
								if (textQuery.getValue().length() > 0) {
									conditionBeforeAdded = "\n" + condition;
								}
								String conditionAdded = conditionBeforeAdded;

								treeitemColumn.setDataPut(conditionAdded);

								treeitemColumn.addEventListener(
										"onDoubleClick",
										new EventListener<Event>() {
											public void onEvent(
													Event treeitemColumnEvent) {
												TreeItemWithData treeitemColumnSelected = (TreeItemWithData) treeitemColumnEvent
														.getTarget();
												textQuery.setValue(textQuery
														.getValue()
														+ treeitemColumnSelected
																.getDataPut());
											}
										});

								if (columnType.equalsIgnoreCase("[B")) {
									Menupopup menupopupItemColumn = new Menupopup();
									menupopupItemColumn.setParent(queryWindow);
									Menuitem menuitem = new Menuitem("Type "
											+ columnTypeName + " can't access");
									menuitem.setParent(menupopupItemColumn);
									menuitem.setImage("image/delete-icon.png");
									
									treeitemColumn
											.setContext(menupopupItemColumn);
									treeitemColumn
											.setParent(treechildrenColumn);
								} else {
									Menupopup menupopupItemColumn = new Menupopup();
									menupopupItemColumn.setParent(queryWindow);
									MenuitemWithData menuitemAddCondition = new MenuitemWithData(
											"Add condition");
									menuitemAddCondition
											.set_data(conditionAdded);
									menuitemAddCondition
											.setImage("image/columnicon.png");
									menuitemAddCondition.addEventListener(
											"onClick",
											new EventListener<Event>() {
												public void onEvent(
														Event menuitemAddConditionEvent) {
													MenuitemWithData menuitemWithData = (MenuitemWithData) menuitemAddConditionEvent
															.getTarget();
													textQuery.setValue(textQuery
															.getValue()
															+ menuitemWithData
																	.get_data());
												}
											});
									menuitemAddCondition
											.setParent(menupopupItemColumn);
									MenuitemWithData menuitemAndCondition = new MenuitemWithData(
											"AND condition");
									menuitemAndCondition.set_data(condition);
									menuitemAndCondition
											.setImage("image/columnicon.png");

									menuitemAndCondition.addEventListener(
											"onClick",
											new EventListener<Event>() {
												public void onEvent(
														Event menuitemAndConditionEvent) {
													MenuitemWithData menuitemWithData = (MenuitemWithData) menuitemAndConditionEvent
															.getTarget();
													String conditionAddedAnd = " AND "
															+ menuitemWithData
																	.get_data();
													if (textQuery.getValue()
															.length() > 0) {
														conditionAddedAnd = "\n"
																+ conditionAddedAnd;
													}
													textQuery.setValue(textQuery
															.getValue()
															+ conditionAddedAnd);
												}
											});
									menuitemAndCondition
											.setParent(menupopupItemColumn);
									MenuitemWithData menuitemORCondition = new MenuitemWithData(
											"OR condition");
									menuitemORCondition
											.setImage("image/columnicon.png");
									menuitemORCondition.set_data(condition);
									menuitemORCondition.addEventListener(
											"onClick",
											new EventListener<Event>() {
												public void onEvent(
														Event menuitemORConditionEvent) {
													MenuitemWithData menuitemWithData = (MenuitemWithData) menuitemORConditionEvent
															.getTarget();
													String conditionAddedOr = " OR "
															+ menuitemWithData
																	.get_data();
													if (textQuery.getValue()
															.length() > 0) {
														conditionAddedOr = "\n"
																+ conditionAddedOr;
													}
													textQuery.setValue(textQuery
															.getValue()
															+ conditionAddedOr);
												}
											});
									menuitemORCondition
											.setParent(menupopupItemColumn);

									treeitemColumn
											.setContext(menupopupItemColumn);
									treeitemColumn
											.setParent(treechildrenColumn);
								}

							}

							treeitemTable.setParent(treechildrenTable);

							String querySelectFinal = "SELECT " + querySelect
									+ " FROM " + tableName;
							String querySelect300 = "SELECT TOP(300) "
									+ querySelect + " FROM " + tableName;
							if (databaseKind.equalsIgnoreCase("mysql")) {
								querySelect300 = "SELECT " + querySelect
										+ " FROM " + tableName + " LIMIT 300";
							}
							String querySelect300Final = querySelect300;

							queryInsertValues += ") ";
							String queryInsert = "INSERT INTO " + tableName
									+ " (" + querySelectForAccess + ") VALUES "
									+ queryInsertValues;
							String queryUpdate = "UPDATE " + tableName
									+ " SET " + queryUpdateValues + " WHERE ";
							String queryDelete = "DELETE FROM " + tableName
									+ " WHERE ";

							treeitemTable
									.set_querySelectFinal(querySelectFinal);
							treeitemTable.setDriverName(databaseDriver);
							treeitemTable.setUrlData(databaseUrl);
							treeitemTable.addEventListener("onDoubleClick",
									new EventListener<Event>() {
										public void onEvent(
												Event treeitemTableEvent) {
											TreeItemWithData treeItemWithData = (TreeItemWithData) treeitemTableEvent
													.getTarget();
											treeItemWithData.setSelected(true);
											setSelectResult(treeItemWithData
													.get_querySelectFinal(),
													treeItemWithData
															.getDriverName(),
													treeItemWithData
															.getUrlData());
										}
									});

							Menupopup menupopupItemTable = new Menupopup();
							menupopupItemTable.setParent(queryWindow);
							MenuitemWithData menuitemPopupItemTableSelect = new MenuitemWithData(
									"Select");
							menuitemPopupItemTableSelect
									.setImage("image/queryIcon.png");

							menuitemPopupItemTableSelect
									.set_querySelectFinal(querySelectFinal);
							menuitemPopupItemTableSelect
									.setDriverName(databaseDriver);
							menuitemPopupItemTableSelect
									.setUrlData(databaseUrl);
							menuitemPopupItemTableSelect.addEventListener(
									"onClick", new EventListener<Event>() {
										public void onEvent(
												Event menuitemWithDataEvent) {
											MenuitemWithData menuitemWithData = (MenuitemWithData) menuitemWithDataEvent
													.getTarget();
											setSelectResult(menuitemWithData
													.get_querySelectFinal(),
													menuitemWithData
															.getDriverName(),
													menuitemWithData
															.getUrlData());
										}
									});
							menuitemPopupItemTableSelect
									.setParent(menupopupItemTable);
							MenuitemWithData select300ItemTableMenuitem = new MenuitemWithData(
									"Select 300");
							select300ItemTableMenuitem
									.setImage("image/queryIcon.png");
							select300ItemTableMenuitem
									.set_querySelectFinal(querySelect300Final);
							select300ItemTableMenuitem
									.setDriverName(databaseDriver);
							select300ItemTableMenuitem.setUrlData(databaseUrl);
							select300ItemTableMenuitem.addEventListener(
									"onClick", new EventListener<Event>() {
										public void onEvent(
												Event select300ItemTableMenuitemEvent) {
											MenuitemWithData menuitemWithData = (MenuitemWithData) select300ItemTableMenuitemEvent
													.getTarget();
											setSelectResult(menuitemWithData
													.get_querySelectFinal(),
													menuitemWithData
															.getDriverName(),
													menuitemWithData
															.getUrlData());
										}
									});
							select300ItemTableMenuitem
									.setParent(menupopupItemTable);

							MenuitemWithData menuitemPopupItemTableInsert = new MenuitemWithData(
									"Insert");
							menuitemPopupItemTableInsert
									.setImage("image/sql.png");
							menuitemPopupItemTableInsert
									.set_querySelectFinal(queryInsert);
							menuitemPopupItemTableInsert
									.setDriverName(databaseDriver);
							menuitemPopupItemTableInsert
									.setUrlData(databaseUrl);
							menuitemPopupItemTableInsert.addEventListener(
									"onClick", new EventListener<Event>() {
										public void onEvent(
												Event menuitemPopupItemTableInsertEvent) {
											MenuitemWithData menuitemWithData = (MenuitemWithData) menuitemPopupItemTableInsertEvent
													.getTarget();
											setSelectResult(menuitemWithData
													.get_querySelectFinal(),
													menuitemWithData
															.getDriverName(),
													menuitemWithData
															.getUrlData());
										}
									});
							menuitemPopupItemTableInsert
									.setParent(menupopupItemTable);
							MenuitemWithData menuitemPopupItemTableUpdate = new MenuitemWithData(
									"Update");
							menuitemPopupItemTableUpdate
									.set_querySelectFinal(queryUpdate);
							menuitemPopupItemTableUpdate
									.setDriverName(databaseDriver);
							menuitemPopupItemTableUpdate
									.setUrlData(databaseUrl);
							menuitemPopupItemTableUpdate
									.setImage("image/sql.png");
							menuitemPopupItemTableUpdate.addEventListener(
									"onClick", new EventListener<Event>() {
										public void onEvent(
												Event menuitemPopupItemTableUpdateEvent) {
											MenuitemWithData menuitemWithData = (MenuitemWithData) menuitemPopupItemTableUpdateEvent
													.getTarget();
											setSelectResult(menuitemWithData
													.get_querySelectFinal(),
													menuitemWithData
															.getDriverName(),
													menuitemWithData
															.getUrlData());
										}
									});
							menuitemPopupItemTableUpdate
									.setParent(menupopupItemTable);

							MenuitemWithData menuitemPopupItemTableDelete = new MenuitemWithData(
									"Delete");
							menuitemPopupItemTableDelete
									.setImage("image/sql.png");
							menuitemPopupItemTableDelete
									.set_querySelectFinal(queryDelete);
							menuitemPopupItemTableDelete
									.setDriverName(databaseDriver);
							menuitemPopupItemTableDelete
									.setUrlData(databaseUrl);
							menuitemPopupItemTableDelete.addEventListener(
									"onClick", new EventListener<Event>() {
										public void onEvent(
												Event menuitemPopupItemTableDeleteEvent) {
											MenuitemWithData menuitemWithData = (MenuitemWithData) menuitemPopupItemTableDeleteEvent
													.getTarget();
											setSelectResult(menuitemWithData
													.get_querySelectFinal(),
													menuitemWithData
															.getDriverName(),
													menuitemWithData
															.getUrlData());
										}
									});
							menuitemPopupItemTableDelete
									.setParent(menupopupItemTable);

							MenuitemWithData propertiesTableMenuitem = new MenuitemWithData(
									"Properties");
							propertiesTableMenuitem.setImage("image/sql.png");
							propertiesTableMenuitem
									.set_querySelectFinal(queryDelete);
							propertiesTableMenuitem
									.setDriverName(databaseDriver);
							propertiesTableMenuitem.setUrlData(databaseUrl);
							propertiesTableMenuitem.setTableName(tableName);
							propertiesTableMenuitem.addEventListener("onClick",
									new EventListener<Event>() {
										public void onEvent(
												Event propertiesTableMenuitemEvent) {
											MenuitemWithData menuitemWithData = (MenuitemWithData) propertiesTableMenuitemEvent
													.getTarget();
											QueryProperties queryProperties = new QueryProperties(
													menuitemWithData
															.getTableName()
															+ " properties",
													menuitemWithData
															.getDriverName(),
													menuitemWithData
															.getUrlData(),
													menuitemWithData
															.getTableName());
											queryProperties
													.setParent(queryWindow);
											queryProperties.doModal();
										}
									});
							propertiesTableMenuitem
									.setParent(menupopupItemTable);
							treeitemTable.setContext(menupopupItemTable);

						} catch (Exception ex) {
							logger.error(ex.getMessage(), ex);
						} finally {
							preparedStatement.close();
							if (resultSetColumn != null) {
								try {
									resultSetColumn.close();
								} catch (SQLException e) {
									logger.error(e.getMessage(), e);
									Messagebox.show(e.getMessage(), "Error",
											Messagebox.OK, Messagebox.ERROR);
								}
							}
						}

						treeitemTable.setOpen(false);
					}
					treechildrenTable.setParent(treeitemDatabase);

				} catch (Exception ex) {
					serviceMain.handleMessage(ex);
				} finally {
					if (resultSetTable != null) {
						try {
							resultSetTable.close();
						} catch (SQLException e) {
							logger.error(e.getMessage(), e);
							Messagebox.show(e.getMessage(), "Error",
									Messagebox.OK, Messagebox.ERROR);
						}
					}
					if (connection != null) {
						try {
							connection.close();
						} catch (SQLException e) {
							logger.error(e.getMessage(), e);
							Messagebox.show(e.getMessage(), "Error",
									Messagebox.OK, Messagebox.ERROR);
						}
					}

				}

				treeitemDatabase.setParent(treechildrenTreeDAta);
				indexDataSqlServer++;
			}
		}
	}

	public void setSelectResult(String queSelectFinal, String driverName,
			String url) {
		textQuery.setValue(queSelectFinal);
		_driverName = driverName;
		_url = url;
	}
}
