package apps.controller.history;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import apps.entity.UserActivity;
import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class UserActivityWindow extends Window {
	private static final long serialVersionUID = -4946585693828337253L;
	private static final Logger logger = Logger
			.getLogger(UserActivityWindow.class);
	private ServiceMain serviceMain;

	private Window window;
	private Listbox listbox;
	private boolean _isMine;

	private Textbox usernameSearchTextbox;
	private Textbox createdSearchTextbox;
	private Textbox notesSearchTextbox;
	private Button deleteButton;
	
	private org.hibernate.Session _sessionSelect;
	
	private SimpleDateFormat _simpleDateFormat;

	public UserActivityWindow(boolean isMine) {
		super("My activity", null, true);
		window = this;
		_isMine = isMine;
		if (!_isMine) {
			setTitle("Users activity");
		}
		serviceMain = new ServiceImplMain();
		window.setMaximizable(true);
		window.setMaximized(true);
		_simpleDateFormat = new SimpleDateFormat();

		listbox = new Listbox();
		listbox.setParent(window);
		listbox.setMold("paging");
		listbox.setAutopaging(true);
		listbox.setEmptyMessage("No actifity");
		if (!isMine) {
			listbox.setCheckmark(true);
		}
		listbox.setItemRenderer(new ListItemRenderer());
		listbox.setVflex(true);
		listbox.setPagingPosition("bottom");

		Listhead listhead = new Listhead();
		listhead.setParent(listbox);
		listhead.setSizable(true);

		Auxhead auxhead = new Auxhead();
		auxhead.setParent(listbox);

		Image searchImage = null;
		usernameSearchTextbox = new Textbox();
		if (!_isMine) {
			Listheader usernameListheader = new Listheader("Username");
			usernameListheader.setParent(listhead);
			usernameListheader.setSort("auto(userCreated.username)");
			usernameListheader.setWidth("180px");

			Auxheader usernameAuxheader = new Auxheader();
			usernameAuxheader.setParent(auxhead);
			usernameSearchTextbox.setParent(usernameAuxheader);
			usernameSearchTextbox.setWidth("75%");
			usernameSearchTextbox.addEventListener(Events.ON_OK,
					new EventListener<Event>() {
						public void onEvent(Event namedSearchEvent) {
							createdSearchTextbox.setValue("");
							notesSearchTextbox.setValue("");
							refreshListbox();
						}
					});
			searchImage = new Image("image/small_search_icon.png");
			searchImage.setParent(usernameAuxheader);
			searchImage.setStyle("margin: 0 0 0 6px");
		}

		Listheader createdListheader = new Listheader("Activity date");
		createdListheader.setParent(listhead);
		createdListheader.setWidth("150px");
		createdListheader.setSort("auto(createdAt)");

		Auxheader createdAuxheader = new Auxheader();
		createdAuxheader.setParent(auxhead);
		createdSearchTextbox = new Textbox();
		createdSearchTextbox.setParent(createdAuxheader);
		createdSearchTextbox.setWidth("75%");
		createdSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						usernameSearchTextbox.setValue("");
						notesSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(createdAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listheader notesListheader = new Listheader("Activity");
		notesListheader.setParent(listhead);
		notesListheader.setSort("auto(createdAt)");

		Auxheader notesAuxheader = new Auxheader();
		notesAuxheader.setParent(auxhead);
		notesSearchTextbox = new Textbox();
		notesSearchTextbox.setParent(notesAuxheader);
		notesSearchTextbox.setWidth("75%");
		notesSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						usernameSearchTextbox.setValue("");
						createdSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(notesAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listfoot listfoot = new Listfoot();
		listfoot.setParent(listbox);

		Listfooter listfooter = new Listfooter();
		listfooter.setParent(listfoot);
		listfooter.setSpan(listhead.getChildren().size() - 1);
		listfooter.setStyle("text-align: center;");
		deleteButton = new Button("Delete");
		if (!isMine) {
		deleteButton.setParent(listfooter);
		deleteButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event deleteEvent) {
						if (Messagebox.show("Delete selected data?",
								"Question", Messagebox.YES | Messagebox.NO,
								Messagebox.QUESTION) == Messagebox.YES) {

							try {
								_sessionSelect = hibernateUtil
										.getSessionFactory(_sessionSelect);
								for (Listitem listitem : listbox
										.getSelectedItems()) {
									UserActivity userActivity = listitem
											.getValue();
									_sessionSelect.delete(userActivity);
									_sessionSelect.flush();
								}
								
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}

							refreshListbox();
						}
					}
				});
		}

		refreshListbox();
	}

	private void refreshListbox() {
		try {
			_sessionSelect = hibernateUtil.getSessionFactory(_sessionSelect);
			_sessionSelect.clear();
			Criteria criteria = _sessionSelect
					.createCriteria(UserActivity.class);
			if (_isMine) {
				criteria.add(Restrictions.eq("userCreated", ((Users) Sessions
						.getCurrent().getAttribute("userlogin"))));
			}
			if (!usernameSearchTextbox.getValue().isEmpty()) {
				criteria.createAlias("userCreated", "userCreated");
				criteria.add(Restrictions.like("userCreated.username",
						usernameSearchTextbox.getValue() + "%"));
			} else if (!createdSearchTextbox.getValue().isEmpty()) {
				Timestamp basic = serviceMain.convertToTimeStamp(
						"dd/MM/yyyy HH:mm", createdSearchTextbox.getValue(), _simpleDateFormat);
				if (basic == null) {
					Timestamp lowTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy", createdSearchTextbox.getValue(), _simpleDateFormat);
					Timestamp highTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy HH:mm:ss",
							createdSearchTextbox.getValue() + " 23:59:59", _simpleDateFormat);
					criteria.add(Restrictions.between("createdAt",
							lowTimestamp, highTimestamp));
				} else {
					Timestamp highTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy HH:mm:ss",
							createdSearchTextbox.getValue() + ":59", _simpleDateFormat);
					criteria.add(Restrictions.between("createdAt", basic,
							highTimestamp));
				}
			} else if (!notesSearchTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("notes",
						notesSearchTextbox.getValue() + "%"));
			}
			if (listbox.getModel() == null) {
				ListModelList<UserActivity> userActifityListModelList = new ListModelList<UserActivity>(
						(List<UserActivity>) criteria.list());
				userActifityListModelList.setMultiple(true);
				listbox.setModel(userActifityListModelList);
			} else {
				ListModel<UserActivity> userActivityListModel = listbox.getModel();
				ListModelList<UserActivity> userActifityListModelList = (ListModelList<UserActivity>) userActivityListModel;
				userActifityListModelList.clear();
				userActifityListModelList.addAll(criteria.list());
				userActifityListModelList.setMultiple(true);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public class ListItemRenderer implements ListitemRenderer<UserActivity> {

		@Override
		public void render(Listitem listitem, UserActivity userActivity,
				int index) throws Exception {
			listitem.setValue(userActivity);
			listitem.addEventListener(Events.ON_DOUBLE_CLICK,
					new EventListener<Event>() {
				public void onEvent(Event listitemEvent) {
					Listitem selectedListitem = (Listitem) listitemEvent.getTarget();
					UserActivity selectedActivity = selectedListitem.getValue();
					
					
					Window detailWindow = new Window();
					detailWindow.setParent(window);
					detailWindow.setTitle("Detail notes activity");
					detailWindow.setClosable(true);
					detailWindow.setMaximizable(true);
					Textbox detailNotesTextbox = new Textbox(selectedActivity.getNotes());
					detailNotesTextbox.setRows(6);
					detailNotesTextbox.setWidth("400px");
					detailNotesTextbox.setParent(detailWindow);
					detailWindow.doModal();
					
				}
			});
			if (!_isMine) {
				listitem.appendChild(new Listcell(userActivity.getUserCreated()
						.getUsername()));
			}
			listitem.appendChild(new Listcell(serviceMain
					.convertStringFromDate("dd/MM/yyyy HH:mm",
							userActivity.getCreatedAt(), _simpleDateFormat)));
			String notes = userActivity.getNotes();
			if (notes.length() > 170) {
				notes = notes.substring(0,170)+"...";
			}
			listitem.appendChild(new Listcell(notes));
		}
	}

}
