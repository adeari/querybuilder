package apps.controller.querycontrol;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
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

import apps.components.ButtonCustom;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.entity.UsersQuery;
import apps.service.hibernateUtil;

public class QueryOperation extends Window {
	private static final long serialVersionUID = -6251927468505405003L;
	private static final Logger logger = Logger.getLogger(QueryOperation.class);
	
	private Textbox namedSearchingTextbox;
	private Textbox sqlSearchingTextbox;
	private Listbox listbox;
	private Window window;
	
	private Users _user;


	public QueryOperation(String title) {
		super(title, null, true);
		window = this;
		window.setMaximizable(true);
		window.setMaximized(true);
		
		
		Session session = Sessions.getCurrent();
		_user = (Users) session.getAttribute("userlogin");

		listbox = new Listbox();
		listbox.setMold("paging");
		listbox.setAutopaging(true);
		listbox.setMultiple(false);
		listbox.setEmptyMessage("Query empty");
		listbox.setParent(window);
		listbox.setVflex(true);
		listbox.setPagingPosition("bottom");

		Listhead listhead = new Listhead();
		listhead.setParent(listbox);
		listhead.setSizable(true);

		Listheader listheader = new Listheader();
		listheader.setParent(listhead);
		listheader.setWidth("50px");

		Listheader nameListheader = new Listheader("Name");
		nameListheader.setParent(listhead);
		nameListheader.setSort("auto(named)");
		nameListheader.setWidth("250px");

		Listheader querylistheader = new Listheader("Query");
		querylistheader.setParent(listhead);
		querylistheader.setSort("auto(sql)");
		
		
		Auxhead auxhead = new Auxhead();
		auxhead.setParent(listbox);
		
		Auxheader blankAuxheader = new Auxheader();
		blankAuxheader.setParent(auxhead);

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

		listbox.setItemRenderer(new MyListitemRenderer());
	}
	
	private void refreshGrid() {
		Criteria criteria = null;
		org.hibernate.Session querySession = null;
		try {
			querySession = hibernateUtil.getSessionFactory().openSession();
			if (_user.getDivisi().equalsIgnoreCase("admin")) {
				criteria = querySession.createCriteria(QueryData.class);
				if (!sqlSearchingTextbox.getValue().isEmpty()) {
					criteria.add(Restrictions.like("sql",
							sqlSearchingTextbox.getValue() + "%"));
				} else if (!namedSearchingTextbox.getValue().isEmpty()) {
					criteria.add(Restrictions.like("named",
							namedSearchingTextbox.getValue() + "%"));
				}
			} else {
				criteria = querySession.createCriteria(UsersQuery.class)
						.setProjection(Projections.groupProperty("queryData"));
				criteria.add(Restrictions.eq("userData", _user));
				if (!sqlSearchingTextbox.getValue().isEmpty()) {
					criteria.createAlias("queryData", "queryData");
					criteria.add(Restrictions.like("queryData.sql",
							sqlSearchingTextbox.getValue() + "%"));
				} else if (!namedSearchingTextbox.getValue().isEmpty()) {
					criteria.createAlias("queryData", "queryData");
					criteria.add(Restrictions.like("queryData.named",
							namedSearchingTextbox.getValue() + "%"));
				}
			}
			List<QueryData> queryDatas = criteria.list();
			listbox.setModel(new ListModelList<QueryData>(queryDatas));
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
	}

	public class MyListitemRenderer implements ListitemRenderer<QueryData> {

		@Override
		public void render(Listitem item, QueryData queryData, int index)
				throws Exception {
			Listcell listcell = new Listcell();
			listcell.setParent(item);
			ButtonCustom buttonCustom = new ButtonCustom("image/gear.png",
					queryData);
			buttonCustom.setParent(listcell);
			buttonCustom.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(Event buttonCustomEvent) {
					ButtonCustom buttonSelectedButtonCustom = (ButtonCustom) buttonCustomEvent.getTarget();
					if (!buttonSelectedButtonCustom.isDisabled()) {
						buttonSelectedButtonCustom.setDisabled(true);
						QueryData queryDataSelected = (QueryData) buttonSelectedButtonCustom.getDataObject();
						QueryTask queryTask = new QueryTask(queryDataSelected);
						queryTask.setParent(window);
						queryTask.doModal();
						buttonSelectedButtonCustom.setDisabled(false);
					}
				}
			});
			item.appendChild(new Listcell(queryData.getNamed()));
			String sql = queryData.getSql();
			if (sql.length() > 200) {
				sql = sql.substring(0, 200) + "...";
			}
			item.appendChild(new Listcell(sql));
		}

	}
}
