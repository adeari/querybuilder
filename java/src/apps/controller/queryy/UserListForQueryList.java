package apps.controller.queryy;

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
import org.zkoss.zul.Cell;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.components.CheckboxCustomize;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.entity.UsersQuery;
import apps.service.CheckService;
import apps.service.hibernateUtil;

public class UserListForQueryList extends Window {
	private static final Logger logger = Logger
			.getLogger(UserListForQueryList.class);

	private CheckService checkService;

	private Window _userListWindow;
	private ListModelList<Users> userListModelList;
	private Grid userListGrid;
	private QueryData _queryData;
	private List<Integer> userIDOnData;
	private List<UsersQuery> userQueries;
	private Textbox userSearchTextbox;
	private Checkbox checkbox;
	private boolean _showFirst = true;

	UserListForQueryList(String title, QueryData queryData) {
		super(title, null, true);
		_queryData = queryData;

		checkService = new CheckService();
		_userListWindow = this;
		_userListWindow.setMaximizable(true);
		_userListWindow.setMaximized(true);
		Vlayout vlayout = new Vlayout();
		vlayout.setParent(_userListWindow);

		Grid headerGrid = new Grid();
		headerGrid.setParent(vlayout);

		Rows headerRows = new Rows();
		headerRows.setParent(headerGrid);

		Row namedQueryRow = new Row();
		namedQueryRow.setParent(headerRows);

		Cell namedQueryCell = new Cell();
		namedQueryCell.setParent(namedQueryRow);
		namedQueryCell.setValign("top");
		namedQueryCell.setWidth("120px");
		Label namedQueryLabel = new Label("Query name");
		namedQueryLabel.setParent(namedQueryCell);

		Textbox namedQueryTextbox = new Textbox(queryData.getNamed());
		namedQueryTextbox.setParent(namedQueryRow);
		namedQueryTextbox.setWidth("80%");
		namedQueryTextbox.setReadonly(true);

		Row sqlRow = new Row();
		sqlRow.setParent(headerRows);

		Cell sqlCell = new Cell();
		sqlCell.setParent(sqlRow);
		sqlCell.setValign("top");
		sqlCell.setWidth("120px");
		Label sqlLabel = new Label("Query");
		sqlLabel.setParent(sqlCell);

		Textbox sqlTextbox = new Textbox(queryData.getSql());
		sqlTextbox.setParent(sqlRow);
		sqlTextbox.setRows(3);
		sqlTextbox.setWidth("80%");
		sqlTextbox.setReadonly(true);

		Session session = null;
		try {
			session = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(UsersQuery.class);
			criteria.add(Restrictions.eq("queryData", _queryData));
			userIDOnData = new ArrayList<Integer>();
			userQueries = criteria.list();
			for (UsersQuery usersQuery : userQueries) {
				userIDOnData.add(usersQuery.getUserData().getId());
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

		userListGrid = new Grid();
		userListGrid.setParent(vlayout);
		userListGrid.setAutopaging(true);
		userListGrid.setHeight("400px");
		userListGrid.setRowRenderer(new MyRowRenderer());

		Auxhead auxhead = new Auxhead();
		auxhead.setParent(userListGrid);

		Auxheader labelGridAuxheader = new Auxheader(
				"Select users which can access this query");
		labelGridAuxheader.setStyle("background: #FFF;color: #000;");
		labelGridAuxheader.setImage("image/columnicon.png");
		labelGridAuxheader.setColspan(2);
		labelGridAuxheader.setParent(auxhead);

		Columns userColumns = new Columns();
		userColumns.setParent(userListGrid);
		userColumns.setSizable(true);

		Column blankColumn = new Column();
		blankColumn.setParent(userColumns);
		blankColumn.setWidth("50px");

		Column username = new Column("User name");
		username.setParent(userColumns);

		Auxhead searchAuxhead = new Auxhead();
		searchAuxhead.setParent(userListGrid);

		Auxheader checkAuxheader = new Auxheader();
		checkAuxheader.setParent(searchAuxhead);
		checkbox = new Checkbox();
		checkbox.setParent(checkAuxheader);
		checkbox.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event checkboxEvent) {
				_showFirst = false;
				if (checkbox.isChecked()) {
					List<Row> rows = userListGrid.getRows().getChildren();
					for (Row row : rows) {
						((CheckboxCustomize) row.getChildren().get(0))
								.setChecked(checkbox.isChecked());
					}
				} else {
					refreshUserGrid(userSearchTextbox.getValue());
				}
			}
		});

		Auxheader searchAuxheader = new Auxheader();
		searchAuxheader.setParent(searchAuxhead);
		userSearchTextbox = new Textbox();
		userSearchTextbox.setParent(searchAuxheader);
		userSearchTextbox.setStyle("width: 80%");
		userSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event userSearchEvent) {
						refreshUserGrid(userSearchTextbox.getValue());
					}
				});
		Image searchImage = new Image("image/small_search_icon.png");
		searchImage.setStyle("margin: 0 0 0 6px");
		searchImage.setParent(searchAuxheader);

		refreshUserGrid(null);

		Foot userListFoot = new Foot();
		userListFoot.setParent(userListGrid);
		Footer userListFooter = new Footer();
		userListFooter.setParent(userListFoot);
		userListFooter.setStyle("text-align:center;");
		userListFooter.setSpan(2);
		Button saveButton = new Button("Save");
		saveButton.setImage("image/save.png");
		saveButton.setParent(userListFooter);
		saveButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event rowSelectedEvent) {
						List<Row> rowComponents = userListGrid.getRows()
								.getChildren();

						Session session = null;
						try {
							session = hibernateUtil.getSessionFactory()
									.openSession();

							for (Row row : rowComponents) {
								CheckboxCustomize checkboxCustomize = (CheckboxCustomize) row
										.getChildren().get(0);
								Users user = (Users) checkboxCustomize
										.get_dataCustom();
								if (checkboxCustomize.isChecked()) {
									if (userIDOnData.size() == 0
											|| (userIDOnData.size() > 0 && !userIDOnData
													.contains(user.getId()))) {
										UsersQuery usersQuery = new UsersQuery(
												user, _queryData);
										Transaction trx = session
												.beginTransaction();
										session.save(usersQuery);
										user.setIsdeleted(false);
										session.update(user);
										_queryData.setDeleted(false);
										session.update(_queryData);
										trx.commit();
									}

								} else if (userIDOnData.size() > 0
										&& userIDOnData.contains(user.getId())) {

									for (UsersQuery usersQuery : userQueries) {
										Users user1inQuery = usersQuery
												.getUserData();
										if (user1inQuery.getId().equals(
												user.getId())) {
											Transaction trx = session
													.beginTransaction();
											session.delete(usersQuery);
											trx.commit();
											checkService
													.queryIsDeleted(usersQuery
															.getQueryData());
											checkService
													.userIsDeleted(user1inQuery);
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

	public void refreshUserGrid(String usernameSearch) {
		Session session = null;
		try {
			session = hibernateUtil.getSessionFactory().openSession();

			Criteria criteria = session.createCriteria(Users.class);
			criteria.add(Restrictions.ne("divisi", "admin").ignoreCase());
			if (usernameSearch != null && (!usernameSearch.isEmpty())) {
				criteria.add(Restrictions
						.like("username", usernameSearch + "%").ignoreCase());
			}
			List<Users> users = criteria.list();
			userListModelList = new ListModelList<Users>(users);
			userListGrid.setModel(userListModelList);

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
	}

	public class MyRowRenderer implements RowRenderer<Users> {

		@Override
		public void render(Row row, Users user, int index) throws Exception {
			CheckboxCustomize checkboxCustomize = new CheckboxCustomize(user);
			if (_showFirst && userIDOnData.contains(user.getId())) {
				checkboxCustomize.setChecked(true);
			} else if (!_showFirst && !checkbox.isChecked()) {
				checkboxCustomize.setChecked(false);
			}
			checkboxCustomize.setParent(row);
			Label usernameLabel = new Label(user.getUsername());
			usernameLabel.setParent(row);
		}
	}
}
