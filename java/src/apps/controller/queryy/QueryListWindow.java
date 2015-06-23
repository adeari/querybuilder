package apps.controller.queryy;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.compare.QueryDataCompare;
import apps.components.ButtonCustom;
import apps.controller.QueryWindows;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.service.CheckService;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class QueryListWindow extends Window {
	private static final long serialVersionUID = 2967416379868061194L;
	private static final Logger logger = Logger
			.getLogger(QueryListWindow.class);

	ServiceMain serviceMain;
	private CheckService checkService;

	private Window queryListWindow;
	private Button addButton;
	private Grid grid;
	private Textbox namedSearchingTextbox;
	private Textbox sqlSearchingTextbox;
	private Textbox modifiedSearchingTextbox;
	private Textbox modifiedBySearchingTextbox;

	private ListModelList<QueryData> queryListModelList;

	public QueryListWindow(String title) {
		super(title, null, true);
		queryListWindow = this;
		serviceMain = new ServiceImplMain();
		checkService = new CheckService();

		Borderlayout borderlayout = new Borderlayout();
		borderlayout
				.setStyle("position:absolute; top:0; bottom:0; right:0; left:0;border-style:none;");

		North north = new North();
		north.setHeight("46px");
		north.setParent(borderlayout);
		Grid nortGrid = new Grid();
		nortGrid.setStyle("border: 0");
		Rows northRows = new Rows();

		Row northRow = new Row();
		Cell iconCell = new Cell();
		iconCell.setStyle("background: yellow;border: 0;width: 2%;");
		northRow.appendChild(iconCell);
		Image image = new Image("image/rss.png");
		iconCell.appendChild(image);

		Cell titleCell = new Cell();
		titleCell.setParent(northRow);
		titleCell.setStyle("background: yellow;width: 95%;border: 0;");
		Label titleLabel = new Label(title);
		titleLabel.setStyle("font-weight: bold; font-size: 16px; ");
		titleLabel.setParent(titleCell);

		Cell closeCell = new Cell();
		closeCell.setStyle("background: yellow;width: 3%;border: 0;");
		Button closeButton = new Button("X");
		closeButton.setStyle("background: red; font-color: blue;");
		closeButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						detach();
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

		Vlayout vlayout = new Vlayout();
		addButton = new Button("Add " + title);
		addButton.setImage("image/add.png");
		addButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) {
				if (!addButton.isDisabled()) {
					addButton.setDisabled(true);

					QueryWindows queryWindows = new QueryWindows(
							"Add query builder", null);
					queryWindows.setParent(queryListWindow);
					queryWindows
							.setStyle("position:absolute;top:0; bottom:0; right:0; left:0;border-style:none");
					queryWindows.doModal();

					refreshGrid();

					addButton.setDisabled(false);
				}
			}
		});
		vlayout.appendChild(addButton);

		grid = new Grid();
		Columns columns = new Columns();
		columns.setSizable(true);

		Column blankColumn = new Column();
		blankColumn.setStyle("border: 0; border-style: none; border-width: 0;");
		blankColumn.setWidth("50px");
		columns.appendChild(blankColumn);
		blankColumn = new Column();
		blankColumn.setStyle("border: 0; border-style: none; border-width: 0;");
		blankColumn.setWidth("50px");
		columns.appendChild(blankColumn);
		blankColumn = new Column();
		blankColumn.setStyle("border: 0; border-style: none; border-width: 0;");
		blankColumn.setWidth("50px");
		columns.appendChild(blankColumn);

		Column queryNameColumn = new Column("Query name");
		try {
			queryNameColumn.setSort("auto(named)");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		}
		columns.appendChild(queryNameColumn);

		Column queryColumn = new Column("Query");
		try {
			queryColumn.setSort("auto(sql)");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		}
		queryColumn.setWidth("40%");
		columns.appendChild(queryColumn);

		Column modifiedDateColumn = new Column("Modified Date");
		try {
			modifiedDateColumn.setSort("auto(modifiedAt)");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		}
		columns.appendChild(modifiedDateColumn);

		Column modifiedByColumn = new Column("Modified By");
		modifiedByColumn.setSortAscending(new QueryDataCompare(true));
		modifiedByColumn.setSortDescending(new QueryDataCompare(false));
		columns.appendChild(modifiedByColumn);

		grid.appendChild(columns);

		Auxhead auxhead = new Auxhead();
		grid.appendChild(auxhead);

		Auxheader blankAuxheader = new Auxheader();
		blankAuxheader
				.setStyle("border: 0; border-style: none; border-width: 0;");
		auxhead.appendChild(blankAuxheader);
		blankAuxheader = new Auxheader();
		blankAuxheader
				.setStyle("border: 0; border-style: none; border-width: 0;");
		auxhead.appendChild(blankAuxheader);
		blankAuxheader = new Auxheader();
		blankAuxheader
				.setStyle("border: 0; border-style: none; border-width: 0;");
		auxhead.appendChild(blankAuxheader);

		Auxheader namedAuxheader = new Auxheader();
		namedSearchingTextbox = new Textbox();
		namedAuxheader.appendChild(namedSearchingTextbox);
		namedSearchingTextbox.setWidth("75%");
		namedSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						modifiedBySearchingTextbox.setValue("");
						sqlSearchingTextbox.setValue("");
						modifiedSearchingTextbox.setValue("");
						refreshGrid();
					}
				});

		Image searchImage = new Image("image/small_search_icon.png");
		searchImage.setStyle("margin: 0 0 0 6px");
		namedAuxheader.appendChild(searchImage);
		auxhead.appendChild(namedAuxheader);

		Auxheader sqlAuxheader = new Auxheader();
		sqlSearchingTextbox = new Textbox();
		sqlSearchingTextbox.setWidth("75%");
		sqlSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event sqlSearchingEvent) {
						modifiedBySearchingTextbox.setValue("");
						namedSearchingTextbox.setValue("");
						modifiedSearchingTextbox.setValue("");
						refreshGrid();
					}
				});
		sqlAuxheader.appendChild(sqlSearchingTextbox);
		Image sqlSearchImage = new Image("image/small_search_icon.png");
		sqlSearchImage.setStyle("margin: 0 0 0 6px");
		sqlAuxheader.appendChild(sqlSearchImage);
		auxhead.appendChild(sqlAuxheader);

		Auxheader modifiedAuxheader = new Auxheader();
		modifiedSearchingTextbox = new Textbox();
		modifiedAuxheader.appendChild(modifiedSearchingTextbox);
		modifiedSearchingTextbox.setWidth("75%");
		modifiedSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event sqlSearchingEvent) {
						modifiedBySearchingTextbox.setValue("");
						sqlSearchingTextbox.setValue("");
						namedSearchingTextbox.setValue("");
						refreshGrid();
					}
				});
		Image modifiedSearchImage = new Image("image/small_search_icon.png");
		modifiedSearchImage.setStyle("margin: 0 0 0 6px");
		modifiedAuxheader.appendChild(modifiedSearchImage);
		auxhead.appendChild(modifiedAuxheader);

		Auxheader modifiedByAuxheader = new Auxheader();
		modifiedBySearchingTextbox = new Textbox();
		modifiedByAuxheader.appendChild(modifiedBySearchingTextbox);
		modifiedBySearchingTextbox.setWidth("75%");
		modifiedBySearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event modifiedByEvent) {
						namedSearchingTextbox.setValue("");
						modifiedSearchingTextbox.setValue("");
						sqlSearchingTextbox.setValue("");
						refreshGrid();
					}
				});
		Image modifiedBySearchImage = new Image("image/small_search_icon.png");
		modifiedBySearchImage.setStyle("margin: 0 0 0 6px");
		modifiedByAuxheader.appendChild(modifiedBySearchImage);
		auxhead.appendChild(modifiedByAuxheader);

		refreshGrid();

		grid.setRowRenderer(new MyRowRenderer());

		grid.setAutopaging(true);
		grid.setMold("paging");
		grid.setHeight("520px");

		vlayout.appendChild(grid);
		center.appendChild(vlayout);
		queryListWindow.appendChild(borderlayout);
	}

	public class MyRowRenderer implements RowRenderer<QueryData> {

		@Override
		public void render(Row row, QueryData queryData, int index)
				throws Exception {

			Cell updateCell = new Cell();
			updateCell.setValign("top");
			row.appendChild(updateCell);
			ButtonCustom updateButton = new ButtonCustom("image/icon_edit.gif",
					queryData);
			updateButton.setWidth("40px");
			updateButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event event) {
							ButtonCustom updateEventButton = (ButtonCustom) event
									.getTarget();
							if (!updateEventButton.isDisabled()) {
								updateEventButton.setDisabled(true);

								QueryData selectedData = (QueryData) updateEventButton
										.getDataObject();

								QueryWindows queryWindows = new QueryWindows(
										"Edit query builder", selectedData);
								queryWindows.setParent(queryListWindow);
								queryWindows
										.setStyle("position:absolute;top:0; bottom:0; right:0; left:0;border-style:none");
								queryWindows.doModal();

								refreshGrid();

								addButton.setDisabled(false);

								updateEventButton.setDisabled(false);
							}
						}
					});

			updateCell.appendChild(updateButton);

			Cell deleteCell = new Cell();
			deleteCell.setValign("top");
			row.appendChild(deleteCell);
			ButtonCustom deleteButton = new ButtonCustom(
					"image/delete-icon.png", queryData);
			deleteButton.setWidth("40px");
			if (!queryData.isDeleted()) {
				deleteButton.setDisabled(true);
			}
			deleteButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event event) {
							ButtonCustom deleteEventButton = (ButtonCustom) event
									.getTarget();
							if (!deleteEventButton.isDisabled()) {
								deleteEventButton.setDisabled(true);

								if (Messagebox.show("Delete this data?",
										"Question", Messagebox.YES
												| Messagebox.NO,
										Messagebox.QUESTION) == Messagebox.YES) {

									QueryData selectedData = (QueryData) deleteEventButton
											.getDataObject();
									Users userBefore = selectedData
											.getModifiedBy();

									Session session = null;
									try {
										session = hibernateUtil
												.getSessionFactory()
												.openSession();

										Transaction trx = session
												.beginTransaction();
										session.delete(selectedData);
										trx.commit();

										checkService.userIsDeleted(userBefore);
									} catch (Exception e) {
										logger.error(e.getMessage(), e);

									} finally {
										if (session != null) {
											try {
												session.close();
											} catch (Exception e) {
												logger.error(e.getMessage(), e);
											}
										}

									}

									refreshGrid();
								}

								deleteEventButton.setDisabled(false);
							}
						}
					});
			deleteCell.appendChild(deleteButton);

			Cell gearCell = new Cell();
			gearCell.setValign("top");
			row.appendChild(gearCell);
			ButtonCustom gearButtonCustom = new ButtonCustom("image/gear.png",
					queryData);
			gearButtonCustom.setWidth("40px");
			gearButtonCustom.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event gearEvent) {
							ButtonCustom buttonSelected = (ButtonCustom) gearEvent
									.getTarget();

							UserListForQueryList userListForQueryList = new UserListForQueryList(
									"Query properties",
									(QueryData) buttonSelected.getDataObject());
							userListForQueryList.setParent(queryListWindow);
							userListForQueryList.doModal();

							refreshGrid();
						}
					});
			gearCell.appendChild(gearButtonCustom);

			Cell nameCell = new Cell();
			nameCell.setValign("top");
			row.appendChild(nameCell);
			nameCell.appendChild(new Label(queryData.getNamed()));

			Cell sqlCell = new Cell();
			sqlCell.setValign("top");
			row.appendChild(sqlCell);
			String sqlData = queryData.getSql();
			if (sqlData != null && (sqlData.length() > 70)) {
				sqlData = sqlData.substring(0, 70) + "...";
			}
			sqlCell.appendChild(new Label(sqlData));

			Cell dateCell = new Cell();
			dateCell.setValign("top");
			row.appendChild(dateCell);
			dateCell.appendChild(new Label(serviceMain.convertStringFromDate(
					"dd/MM/yyyy HH:mm", queryData.getModifiedAt())));

			Cell modifiedBy = new Cell();
			modifiedBy.setValign("top");
			row.appendChild(modifiedBy);
			modifiedBy.appendChild(new Label(queryData.getModifiedBy()
					.getUsername()));
		}
	}

	public void refreshGrid() {
		Session sessionSelect = null;
		try {
			sessionSelect = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = sessionSelect.createCriteria(QueryData.class);
			

			if (!sqlSearchingTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("sql",
						sqlSearchingTextbox.getValue() + "%"));
			} else if (!namedSearchingTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("named",
						namedSearchingTextbox.getValue() + "%"));
			} else if (!modifiedSearchingTextbox.getValue().isEmpty()) {
				Timestamp basic = serviceMain
						.convertToTimeStamp("dd/MM/yyyy HH:mm",
								modifiedSearchingTextbox.getValue());
				if (basic == null) {
					Timestamp lowTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy", modifiedSearchingTextbox.getValue());
					Timestamp highTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy HH:mm:ss",
							modifiedSearchingTextbox.getValue() + " 23:59:59");
					criteria.add(Restrictions.between("modifiedAt",
							lowTimestamp, highTimestamp));
				} else {
					Timestamp highTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy HH:mm:ss",
							modifiedSearchingTextbox.getValue() + ":59");
					criteria.add(Restrictions.between("modifiedAt", basic,
							highTimestamp));
				}
			} else if (!modifiedBySearchingTextbox.getValue().isEmpty()) {
				criteria.createAlias("modifiedBy", "userModified");
				criteria.add(Restrictions.like("userModified.username",
						modifiedBySearchingTextbox.getValue() + "%"));
			}
			List<QueryData> queryDatas = criteria.list();
			queryListModelList = new ListModelList<QueryData>(queryDatas);
			grid.setModel(queryListModelList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
			if (sessionSelect != null) {
				try {
					sessionSelect.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

		}
	}
}
