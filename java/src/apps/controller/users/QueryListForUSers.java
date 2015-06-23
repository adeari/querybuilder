package apps.controller.users;

import java.util.ArrayList;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import apps.components.CheckboxCustomize;
import apps.controller.queryy.QueryListWindow;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.entity.UsersQuery;
import apps.service.CheckService;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class QueryListForUSers extends Window {
	private static final long serialVersionUID = 2749911535028497104L;
	private static final Logger logger = Logger
			.getLogger(QueryListWindow.class);

	ServiceMain serviceMain;
	private CheckService checkService;
	
	private Window window;
	private Textbox namedSearchingTextbox;
	private Textbox sqlSearchingTextbox;
	private ListModelList<QueryData> queryListModelList;
	private Grid grid;
	private Users _user;
	private List<Integer> queryIDOnData;
	private List<UsersQuery> userQueries;
	private Checkbox checkbox;
	private boolean _showFirst = true;

	public QueryListForUSers(String title, Users user) {
		super(title, null, true);
		window = this;
		_user = user;
		
		serviceMain = new ServiceImplMain();
		checkService = new CheckService();

		Session session = null;
		try {
			session = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(UsersQuery.class);
			criteria.add(Restrictions.eq("userData", _user));
			queryIDOnData = new ArrayList<Integer>();
			userQueries = criteria.list();
			for (UsersQuery usersQuery : userQueries) {
				queryIDOnData.add(usersQuery.getQueryData().getId());
			}
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

		grid = new Grid();
		grid.setParent(window);
		grid.setHeight("500px");
		grid.setAutopaging(true);
		grid.setRowRenderer(new MyRowRenderer());

		Columns columns = new Columns();
		columns.setParent(grid);
		columns.setSizable(true);

		Column blankColumn = new Column();
		blankColumn.setStyle("border: 0; border-style: none; border-width: 0;");
		blankColumn.setWidth("50px");
		blankColumn.setParent(columns);

		Column queryNameColumn = new Column("Query name");
		queryNameColumn.setParent(columns);
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
		Column sqlColumn = new Column("Query");
		sqlColumn.setParent(columns);
		try {
			sqlColumn.setSort("auto(sql_query)");
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

		Auxhead auxhead = new Auxhead();
		auxhead.setParent(grid);

		Auxheader checkAuxheader = new Auxheader();
		checkAuxheader.setParent(auxhead);
		checkbox = new Checkbox();
		checkbox.setParent(checkAuxheader);
		checkbox.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event checkboxEvent) {
				_showFirst = false;
				if (checkbox.isChecked()) {
					List<Row> rows = grid.getRows().getChildren();
					for (Row row : rows) {
						((CheckboxCustomize) row.getChildren().get(0))
								.setChecked(checkbox.isChecked());
					}
				} else {
					refreshGrid();
				}
			}
		});

		Auxheader namedAuxheader = new Auxheader();
		namedAuxheader.setParent(auxhead);
		namedSearchingTextbox = new Textbox();
		namedSearchingTextbox.setParent(namedAuxheader);
		namedSearchingTextbox.setWidth("75%");
		namedSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						sqlSearchingTextbox.setValue("");
						refreshGrid();
					}
				});
		Image namedSearchImage = new Image("image/small_search_icon.png");
		namedSearchImage.setParent(namedAuxheader);
		namedSearchImage.setStyle("margin: 0 0 0 6px");

		Auxheader sqlAuxheader = new Auxheader();
		sqlAuxheader.setParent(auxhead);
		sqlSearchingTextbox = new Textbox();
		sqlSearchingTextbox.setParent(sqlAuxheader);
		sqlSearchingTextbox.setWidth("75%");
		sqlSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event sqlSearchingEvent) {
						namedSearchingTextbox.setValue("");
						refreshGrid();
					}
				});
		sqlAuxheader.appendChild(sqlSearchingTextbox);
		Image sqlSearchImage = new Image("image/small_search_icon.png");
		sqlSearchImage.setStyle("margin: 0 0 0 6px");
		sqlSearchImage.setParent(sqlAuxheader);
		auxhead.appendChild(sqlAuxheader);

		refreshGrid();

		Foot gridFoot = new Foot();
		gridFoot.setParent(grid);
		Footer gridFooter = new Footer();
		gridFooter.setParent(gridFoot);
		gridFooter.setStyle("text-align:center;");
		gridFooter.setSpan(2);
		Button saveButton = new Button("Save");
		saveButton.setImage("image/save.png");
		saveButton.setParent(gridFooter);
		saveButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event rowSelectedEvent) {
						List<Row> rowComponents = grid.getRows()
								.getChildren();

						Session session = null;
						try {
							session = hibernateUtil.getSessionFactory()
									.openSession();

							for (Row row : rowComponents) {
								CheckboxCustomize checkboxCustomize = (CheckboxCustomize) row
										.getChildren().get(0);
								QueryData queryData = (QueryData) checkboxCustomize
										.get_dataCustom();
								if (checkboxCustomize.isChecked()) {
									if (queryIDOnData.size() == 0
											|| (queryIDOnData.size() > 0 && !queryIDOnData
													.contains(queryData.getId()))) {
										UsersQuery usersQuery = new UsersQuery(
												_user, queryData);
										Transaction trx = session
												.beginTransaction();
										session.save(usersQuery);
										_user.setIsdeleted(false);
										session.update(_user);
										queryData.setDeleted(false);
										session.update(queryData);
										trx.commit();
									}

								} else if (queryIDOnData.size() > 0
										&& queryIDOnData.contains(queryData.getId())) {

									for (UsersQuery usersQuery : userQueries) {
										QueryData queryDataSelected = usersQuery
												.getQueryData();
										if (queryDataSelected.getId().equals(
												queryData.getId())) {
											Transaction trx = session
													.beginTransaction();
											session.delete(usersQuery);
											trx.commit();
											checkService
													.queryIsDeleted(queryDataSelected);
											checkService
													.userIsDeleted(usersQuery.getUserData());
										}
									}

								}
							}

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

						detach();
					}
				});
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

	public class MyRowRenderer implements RowRenderer<QueryData> {

		@Override
		public void render(Row row, QueryData queryData, int index)
				throws Exception {
			CheckboxCustomize checkboxCustomize = new CheckboxCustomize(
					queryData);
			if (_showFirst && queryIDOnData.contains(queryData.getId())) {
				checkboxCustomize.setChecked(true);
			} else if (!_showFirst && !checkbox.isChecked()) {
				checkboxCustomize.setChecked(false);
			}
			checkboxCustomize.setParent(row);

			row.appendChild(new Label(queryData.getNamed()));
			row.appendChild(new Label(queryData.getSql()));
		}
	}
}