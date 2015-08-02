package apps.controller.users;

import java.util.Iterator;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import apps.controller.queryy.UserListForQueryList;
import apps.entity.Users;
import apps.service.hibernateUtil;

public class UserEmailWindow  extends Window {
	private static final long serialVersionUID = -7381770636724696081L;
	private static final Logger logger = Logger
			.getLogger(UserEmailWindow.class);
	private Window window;
	private String usersEmail;
	private Listbox usersListbox;
	private Textbox usernameSearchTextbox;
	private Textbox emailSearchTextbox;
	
	private Session _session;
	
	public UserEmailWindow() {
		super("User's email", null, true);
		window = this;
		
		Hbox hbox = new Hbox();
		hbox.setParent(window);
		hbox.setWidth("400px");
		
		usersListbox = new Listbox();
		usersListbox.setParent(hbox);
		usersListbox.setHeight("500px");
		usersListbox.setMold("paging");
		usersListbox.setAutopaging(true);
		usersListbox.setVflex(true);
		usersListbox.setPagingPosition("bottom");
		usersListbox.setCheckmark(true);
		usersListbox.setItemRenderer(new MyListitemRenderer());
		
		Listhead listhead = new Listhead();
		listhead.setParent(usersListbox);
		listhead.setSizable(true);
		
		Listheader usernameListheader = new Listheader("Username");
		usernameListheader.setParent(listhead);
		usernameListheader.setSort("auto(username)");
		
		Listheader emailListheader = new Listheader("Email");
		emailListheader.setParent(listhead);
		emailListheader.setSort("auto(email)");
		
		Auxhead auxhead = new Auxhead();
		auxhead.setParent(usersListbox);
		
		Auxheader usernameSearchAuxheader = new Auxheader();
		usernameSearchAuxheader.setParent(auxhead);
		usernameSearchTextbox = new Textbox();
		usernameSearchTextbox.setParent(usernameSearchAuxheader);
		usernameSearchTextbox.setWidth("75%");
		usernameSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						emailSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		Image namedSearchImage = new Image("image/small_search_icon.png");
		namedSearchImage.setParent(usernameSearchAuxheader);
		namedSearchImage.setStyle("margin: 0 0 0 6px");
		
		Auxheader emailSearchAuxheader = new Auxheader();
		emailSearchAuxheader.setParent(auxhead);
		emailSearchTextbox = new Textbox();
		emailSearchTextbox.setParent(emailSearchAuxheader);
		emailSearchTextbox.setWidth("75%");
		emailSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
			public void onEvent(Event namedSearchEvent) {
				usernameSearchTextbox.setValue("");
				refreshListbox();
			}
		});
		namedSearchImage = new Image("image/small_search_icon.png");
		namedSearchImage.setParent(emailSearchAuxheader);
		namedSearchImage.setStyle("margin: 0 0 0 6px");
		
		Listfoot listfoot = new Listfoot();
		listfoot.setParent(usersListbox);
		
		Listfooter listfooter = new Listfooter();
		listfooter.setParent(listfoot);
		listfooter.setSpan(2);
		listfooter.setStyle("text-align: center;");
		
		Button closeButton = new Button("Close");
		closeButton.setParent(listfooter);
		closeButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
			public void onEvent(Event closeEvent) {
				Button button = (Button) closeEvent.getTarget();
				for (Listitem listitem : usersListbox.getSelectedItems()) {
					if (usersEmail == null) {
						usersEmail = ((Listcell) listitem.getChildren().get(1)).getLabel();
					} else {
						usersEmail += ", "+((Listcell) listitem.getChildren().get(1)).getLabel();
					}
				}
				detach();
				button.setDisabled(false);
			}
		});
		
		refreshListbox();
	}
	
	private void refreshListbox() {
		try {
			_session = hibernateUtil.getSessionFactory(_session);
			_session.clear();
			Criteria criteria = _session.createCriteria(Users.class);
			criteria.add(Restrictions.isNotNull("email"));
			criteria.add(Restrictions.ne("email",""));
			if (!usernameSearchTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("username", usernameSearchTextbox.getValue()+"%"));
			} else if (!emailSearchTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("email", emailSearchTextbox.getValue()+"%"));
			}
			List<Users> users = criteria.list();
			ListModelList<Users> userListModelList = new ListModelList<Users>(users);
			usersListbox.setModel(userListModelList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}
	}

	public String getUsersEmail() {
		return usersEmail;
	}
	
	public class MyListitemRenderer implements ListitemRenderer<Users> {

		@Override
		public void render(Listitem item, Users user, int index)
				throws Exception {
			item.appendChild(new Listcell(user.getUsername()));
			item.appendChild(new Listcell(user.getEmail()));
			if (!usersListbox.isMultiple()) {
				usersListbox.setMultiple(true);
			}
		}
	}
}
