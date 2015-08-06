package apps.controller.users;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import apps.components.ListcellCustomize;
import apps.controller.queryy.QueryListWindow;
import apps.entity.QueryData;
import apps.entity.UserActivity;
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
	private Listbox listbox;
	private Users _user;
	private List<Integer> queryIDOnData;
	private List<UsersQuery> userQueries;

	private Session _sessionSelect;

	public QueryListForUSers(String title, Users user) {
		super(title, null, true);
		window = this;
		_user = user;

		serviceMain = new ServiceImplMain();
		checkService = new CheckService();

		window.setMaximizable(true);
		window.setMaximized(true);

		try {
			_sessionSelect = hibernateUtil.getSessionFactory(_sessionSelect);
			_sessionSelect.clear();
			Criteria criteria = _sessionSelect.createCriteria(UsersQuery.class);
			criteria.add(Restrictions.eq("userData", _user));
			queryIDOnData = new ArrayList<Integer>();
			userQueries = criteria.list();
			for (UsersQuery usersQuery : userQueries) {
				queryIDOnData.add(usersQuery.getQueryData().getId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}

		listbox = new Listbox();
		listbox.setParent(window);
		listbox.setHeight("500px");
		listbox.setMold("paging");
		listbox.setAutopaging(true);
		listbox.setVflex(true);
		listbox.setPagingPosition("bottom");
		listbox.setCheckmark(true);
		listbox.setItemRenderer(new MyItemRenderer());
		listbox.setAttribute("onCheckSelectAll", true);
		listbox.addEventListener("onCheckSelectAll",
				new EventListener<Event>() {
					public void onEvent(Event itemEvent) {
						window.setClosable(false);
						List<Listitem> listitems = listbox.getItems();
						if (listitems.size() > 0) {
							try {
								_sessionSelect = hibernateUtil
										.getSessionFactory(_sessionSelect);
								String message = "";
								for (Listitem itemSelected : listitems) {
									ListcellCustomize listcellCustomize = (ListcellCustomize) itemSelected
											.getChildren().get(0);
									QueryData queryData = (QueryData) listcellCustomize
											.getDataObject();

									Criteria criteria = _sessionSelect
											.createCriteria(UsersQuery.class);
									criteria.add(Restrictions.eq("queryData",
											queryData));
									criteria.add(Restrictions.eq("userData",
											_user));

									if (itemSelected.isSelected()
											&& criteria.list().size() == 0) {
										UsersQuery usersQuery = new UsersQuery(
												_user, queryData);

										_sessionSelect.save(usersQuery);
										if (queryData.isDeleted()) {
											queryData.setDeleted(false);
											_sessionSelect.update(queryData);
											_sessionSelect.flush();
										}
										message += "Add access from user "
												+ _user.getUsername() + " in "
												+ queryData.getNamed();
									} else if (!itemSelected.isSelected()
											&& criteria.list().size() > 0) {
										UsersQuery usersQuery = (UsersQuery) criteria
												.uniqueResult();
										_sessionSelect.delete(usersQuery);
										_sessionSelect.flush();
										checkService.queryIsDeleted(
												_sessionSelect, queryData);
										message += "Remove access from user "
												+ _user.getUsername() + " in "
												+ queryData.getNamed();
									}
								}
								checkService.userIsDeleted(_sessionSelect,
										_user);
								serviceMain.saveUserActivity(_sessionSelect,
										message);
							} catch (Exception e) {
								logger.error(e.getMessage(), e);

							}
						}
						window.setClosable(true);
					}
				});

		Listhead userListhead = new Listhead();
		userListhead.setParent(listbox);
		userListhead.setSizable(true);

		Listheader queryNameListheader = new Listheader("Query name11");
		queryNameListheader.setParent(userListhead);
		queryNameListheader.setSort("auto(named)");
		queryNameListheader.setWidth("200px");
		Listheader sqlNameListheader = new Listheader("Query");
		sqlNameListheader.setParent(userListhead);
		sqlNameListheader.setSort("auto(sql_query)");

		Auxhead auxhead = new Auxhead();
		auxhead.setParent(listbox);

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

	}

	public void refreshGrid() {
		try {
			_sessionSelect = hibernateUtil.getSessionFactory(_sessionSelect);
			_sessionSelect.clear();
			Criteria criteria = _sessionSelect.createCriteria(QueryData.class);
			if (!sqlSearchingTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("sql",
						sqlSearchingTextbox.getValue() + "%"));
			} else if (!namedSearchingTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("named",
						namedSearchingTextbox.getValue() + "%"));
			}
			List<QueryData> queryDatas = criteria.list();
			queryListModelList = new ListModelList<QueryData>(queryDatas);
			listbox.setModel(queryListModelList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}
	}

	public class MyItemRenderer implements ListitemRenderer<QueryData> {

		@Override
		public void render(Listitem item, QueryData queryData, int index)
				throws Exception {
			item.setValue(queryData);
			if (queryIDOnData.contains(queryData.getId())) {
				item.setSelected(true);
			}
			item.appendChild(new ListcellCustomize(queryData.getNamed(),
					queryData));
			String sql = queryData.getSql();
			if (sql.length() > 200) {
				sql = sql.substring(0, 200) + "...";
			}
			item.appendChild(new Listcell(sql));
			
			item.addEventListener(Events.ON_DOUBLE_CLICK,
					new EventListener<Event>() {
				public void onEvent(Event listitemEvent) {
					Listitem selectedListitem = (Listitem) listitemEvent.getTarget();
					QueryData selectedQueryData = ((Listitem) listitemEvent.getTarget()).getValue();
					
					
					Window detailWindow = new Window();
					detailWindow.setParent(window);
					detailWindow.setTitle("Query detail");
					detailWindow.setClosable(true);
					detailWindow.setMaximizable(true);
					Textbox detailNotesTextbox = new Textbox(selectedQueryData.getSql());
					detailNotesTextbox.setRows(6);
					detailNotesTextbox.setWidth("400px");
					detailNotesTextbox.setParent(detailWindow);
					detailWindow.doModal();
					
				}
			});
			
			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event itemEvent) {

					try {
						Listitem itemSelected = (Listitem) itemEvent
								.getTarget();
						ListcellCustomize listcellCustomize = (ListcellCustomize) itemSelected
								.getChildren().get(0);
						QueryData queryData = (QueryData) listcellCustomize
								.getDataObject();

						_sessionSelect = hibernateUtil
								.getSessionFactory(_sessionSelect);
						Criteria criteria = _sessionSelect
								.createCriteria(UsersQuery.class);
						criteria.add(Restrictions.eq("queryData", queryData));
						criteria.add(Restrictions.eq("userData", _user));

						if (itemSelected.isSelected()
								&& criteria.list().size() == 0) {
							UsersQuery usersQuery = new UsersQuery(_user,
									queryData);
							_sessionSelect.save(usersQuery);
							if (_user.isIsdeleted()) {
								_user.setIsdeleted(false);
								_sessionSelect.update(_user);
								_sessionSelect.flush();
							}
							if (queryData.isDeleted()) {
								queryData.setDeleted(false);
								_sessionSelect.update(queryData);
								_sessionSelect.flush();
							}
							serviceMain.saveUserActivity(
									_sessionSelect,
									"Add access from user "
											+ _user.getUsername() + " in "
											+ queryData.getNamed());
						} else if (!itemSelected.isSelected()
								&& criteria.list().size() > 0) {
							UsersQuery usersQuery = (UsersQuery) criteria
									.uniqueResult();
							_sessionSelect.delete(usersQuery);
							_sessionSelect.flush();
							checkService.queryIsDeleted(_sessionSelect,
									queryData);
							checkService.userIsDeleted(_sessionSelect, _user);
							serviceMain.saveUserActivity(
									_sessionSelect,
									"Remove access from user "
											+ _user.getUsername() + " in "
											+ queryData.getNamed());
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);

					}
				}

			});
			if (!listbox.isMultiple()) {
				listbox.setMultiple(true);
			}
		}
	}
}