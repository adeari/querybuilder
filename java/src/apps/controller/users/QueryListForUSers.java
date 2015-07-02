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

		listbox = new Listbox();
		listbox.setParent(window);
		listbox.setHeight("500px");
		listbox.setMold("paging");
		listbox.setAutopaging(true);
		listbox.setCheckmark(true);
		listbox.setItemRenderer(new MyItemRenderer());
		listbox.setAttribute("onCheckSelectAll", true);
		listbox.addEventListener("onCheckSelectAll",
				new EventListener<Event>() {
					public void onEvent(Event itemEvent) {
						window.setClosable(false);
						List<Listitem> listitems = listbox.getItems();
						if (listitems.size() > 0) {
							Session session = null;
							try {

								session = hibernateUtil.getSessionFactory()
										.openSession();
								for (Listitem itemSelected : listitems) {
									ListcellCustomize listcellCustomize = (ListcellCustomize) itemSelected
											.getChildren().get(0);
									QueryData queryData = (QueryData) listcellCustomize
											.getDataObject();

									Criteria criteria = session
											.createCriteria(UsersQuery.class);
									criteria.add(Restrictions.eq("queryData",
											queryData));
									criteria.add(Restrictions.eq("userData",
											_user));

									if (itemSelected.isSelected()
											&& criteria.list().size() == 0) {
										UsersQuery usersQuery = new UsersQuery(
												_user, queryData);
										Transaction trx = session
												.beginTransaction();
										session.save(usersQuery);
										queryData.setDeleted(false);
										session.update(queryData);
										trx.commit();
									} else if (!itemSelected.isSelected()
											&& criteria.list().size() > 0) {
										UsersQuery usersQuery = (UsersQuery) criteria
												.uniqueResult();
										Transaction trx = session
												.beginTransaction();
										session.delete(usersQuery);
										trx.commit();
										checkService.queryIsDeleted(queryData);

									}
								}
								checkService.userIsDeleted(_user);
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
						window.setClosable(true);
					}
				});

		Listhead userListhead = new Listhead();
		userListhead.setParent(listbox);
		userListhead.setSizable(true);

		Listheader queryNameListheader = new Listheader("User name");
		queryNameListheader.setParent(userListhead);
		queryNameListheader.setSort("auto(named)");
		Listheader sqlNameListheader = new Listheader("User name");
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
			listbox.setModel(queryListModelList);
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

	public class MyItemRenderer implements ListitemRenderer<QueryData> {

		@Override
		public void render(Listitem item, QueryData queryData, int index)
				throws Exception {
			if (queryIDOnData.contains(queryData.getId())) {
				item.setSelected(true);
			}
			item.appendChild(new ListcellCustomize(queryData.getNamed(),
					queryData));
			item.appendChild(new Listcell(queryData.getSql()));
			item.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event itemEvent) {

					Session session = null;
					try {
						Listitem itemSelected = (Listitem) itemEvent
								.getTarget();
						ListcellCustomize listcellCustomize = (ListcellCustomize) itemSelected
								.getChildren().get(0);
						QueryData queryData = (QueryData) listcellCustomize
								.getDataObject();

						session = hibernateUtil.getSessionFactory()
								.openSession();
						Criteria criteria = session
								.createCriteria(UsersQuery.class);
						criteria.add(Restrictions.eq("queryData", queryData));
						criteria.add(Restrictions.eq("userData", _user));

						if (itemSelected.isSelected()
								&& criteria.list().size() == 0) {
							UsersQuery usersQuery = new UsersQuery(_user,
									queryData);
							Transaction trx = session.beginTransaction();
							session.save(usersQuery);
							_user.setIsdeleted(false);
							session.update(_user);
							queryData.setDeleted(false);
							session.update(queryData);
							trx.commit();
						} else if (!itemSelected.isSelected()
								&& criteria.list().size() > 0) {
							UsersQuery usersQuery = (UsersQuery) criteria
									.uniqueResult();
							Transaction trx = session.beginTransaction();
							session.delete(usersQuery);
							trx.commit();
							checkService.queryIsDeleted(queryData);
							checkService.userIsDeleted(_user);
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
			if (!listbox.isMultiple()) {
				listbox.setMultiple(true);
			}
		}
	}
}