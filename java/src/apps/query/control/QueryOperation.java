package apps.query.control;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import apps.components.ButtonCustom;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.entity.UsersQuery;
import apps.service.hibernateUtil;

public class QueryOperation extends Window {
	private static final long serialVersionUID = -6251927468505405003L;
	private static final Logger logger = Logger.getLogger(QueryOperation.class);


	public QueryOperation(String title) {
		super(title, null, true);

		Listbox listbox = new Listbox();
		listbox.setHeight("600px");
		listbox.setAutopaging(true);
		listbox.setMultiple(false);
		listbox.setEmptyMessage("Query empty");
		listbox.setParent(this);

		Listhead listhead = new Listhead();
		listhead.setParent(listbox);

		Listheader listheader = new Listheader();
		listheader.setParent(listhead);
		listheader.setWidth("50px");

		Listheader nameListheader = new Listheader("Name");
		nameListheader.setParent(listhead);
		nameListheader.setWidth("200px");

		Listheader querylistheader = new Listheader("Query");
		querylistheader.setParent(listhead);

		Session session = Sessions.getCurrent();
		Users user = (Users) session.getAttribute("userlogin");

		Criteria criteria = null;
		org.hibernate.Session querySession = null;
		try {
			querySession = hibernateUtil.getSessionFactory().openSession();
			if (user.getDivisi().equalsIgnoreCase("admin")) {
				criteria = querySession.createCriteria(QueryData.class);
			} else {
				criteria = querySession.createCriteria(UsersQuery.class)
						.setProjection(Projections.groupProperty("queryData"));
				criteria.add(Restrictions.eq("userData", user));
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
		listbox.setItemRenderer(new MyListitemRenderer());
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
			item.appendChild(new Listcell(queryData.getNamed()));
			String sql = queryData.getSql();
			if (sql.length() > 150) {
				sql.substring(0, 150);
			}
			item.appendChild(new Listcell(sql));
		}

	}
}
