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
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.components.ListcellCustomize;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.entity.UsersQuery;
import apps.service.CheckService;
import apps.service.hibernateUtil;

public class UserListForQueryList extends Window {
	private static final long serialVersionUID = -2427362927265074408L;

	private static final Logger logger = Logger
			.getLogger(UserListForQueryList.class);

	private CheckService checkService;

	private Window _userListWindow;
	private ListModelList<Users> userListModelList;
	private Listbox userListbox;
	private QueryData _queryData;
	private List<Integer> userIDOnData;
	private List<UsersQuery> userQueries;
	private Textbox userSearchTextbox;

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

		userListbox = new Listbox();
		userListbox.setParent(vlayout);
		userListbox.setMold("paging");
		userListbox.setAutopaging(true);
		userListbox.setHeight("400px");
		userListbox.setCheckmark(true);
		userListbox.setItemRenderer(new MyListitemRenderer());
		userListbox.setAttribute("onCheckSelectAll", true);
		userListbox.addEventListener("onCheckSelectAll",
				new EventListener<Event>() {
					public void onEvent(Event itemEvent) {
						_userListWindow.setClosable(false);
						List<Listitem> listitems = userListbox.getItems();
						if (listitems.size() > 0) {
							Session session = null;
							try {

								session = hibernateUtil.getSessionFactory()
										.openSession();
								for (Listitem itemSelected : listitems) {
									ListcellCustomize listcellCustomize = (ListcellCustomize) itemSelected
											.getChildren().get(0);
									Users userSelected = (Users) listcellCustomize
											.getDataObject();

									Criteria criteria = session
											.createCriteria(UsersQuery.class);
									criteria.add(Restrictions.eq("queryData",
											_queryData));
									criteria.add(Restrictions.eq("userData",
											userSelected));

									if (itemSelected.isSelected()
											&& criteria.list().size() == 0) {
										UsersQuery usersQuery = new UsersQuery(
												userSelected, _queryData);
										Transaction trx = session
												.beginTransaction();
										session.save(usersQuery);
										userSelected.setIsdeleted(false);
										session.update(userSelected);
										trx.commit();
									} else if (!itemSelected.isSelected()
											&& criteria.list().size() > 0) {
										UsersQuery usersQuery = (UsersQuery) criteria
												.uniqueResult();
										Transaction trx = session
												.beginTransaction();
										session.delete(usersQuery);
										trx.commit();

										checkService
												.userIsDeleted(userSelected);
									}
								}
								checkService.queryIsDeleted(_queryData);
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
						_userListWindow.setClosable(true);
					}
				});

		Auxhead auxhead = new Auxhead();
		auxhead.setParent(userListbox);

		Auxheader labelGridAuxheader = new Auxheader(
				"Select users which can access this query");
		labelGridAuxheader.setImage("image/columnicon.png");
		labelGridAuxheader.setParent(auxhead);

		Listhead userListhead = new Listhead();
		userListhead.setParent(userListbox);
		userListhead.setSizable(true);

		Listheader usernameListheader = new Listheader("User name");
		usernameListheader.setParent(userListhead);
		usernameListheader.setSort("auto(username)");

		Auxhead searchAuxhead = new Auxhead();
		searchAuxhead.setParent(userListbox);

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
			userListbox.setModel(userListModelList);
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

	public class MyListitemRenderer implements ListitemRenderer<Users> {

		@Override
		public void render(Listitem item, Users user, int index)
				throws Exception {
			if (userIDOnData.contains(user.getId())) {
				item.setSelected(true);
			}

			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event itemEvent) {

					Session session = null;
					try {
						Listitem itemSelected = (Listitem) itemEvent
								.getTarget();
						ListcellCustomize listcellCustomize = (ListcellCustomize) itemSelected
								.getChildren().get(0);
						Users userSelected = (Users) listcellCustomize
								.getDataObject();

						session = hibernateUtil.getSessionFactory()
								.openSession();
						Criteria criteria = session
								.createCriteria(UsersQuery.class);
						criteria.add(Restrictions.eq("queryData", _queryData));
						criteria.add(Restrictions.eq("userData", userSelected));

						if (itemSelected.isSelected()
								&& criteria.list().size() == 0) {
							UsersQuery usersQuery = new UsersQuery(
									userSelected, _queryData);
							Transaction trx = session.beginTransaction();
							session.save(usersQuery);
							userSelected.setIsdeleted(false);
							session.update(userSelected);
							_queryData.setDeleted(false);
							session.update(_queryData);
							trx.commit();
						} else if (!itemSelected.isSelected()
								&& criteria.list().size() > 0) {
							UsersQuery usersQuery = (UsersQuery) criteria
									.uniqueResult();
							Transaction trx = session.beginTransaction();
							session.delete(usersQuery);
							trx.commit();
							checkService.queryIsDeleted(_queryData);
							checkService.userIsDeleted(userSelected);
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
				}

			});

			ListcellCustomize usernameListcell = new ListcellCustomize(
					user.getUsername(), user);

			usernameListcell.setParent(item);
			if (!userListbox.isMultiple()) {
				userListbox.setMultiple(true);
			}
		}
	}
}
