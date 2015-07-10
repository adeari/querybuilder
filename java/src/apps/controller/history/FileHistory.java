package apps.controller.history;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import apps.components.ButtonCustom;
import apps.controller.activity.ActivityDetailWindow;
import apps.entity.Activity;
import apps.entity.UserActivity;
import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class FileHistory extends Window {
	private static final long serialVersionUID = -4946585693828337253L;
	private static final Logger logger = Logger.getLogger(FileHistory.class);
	private ServiceMain serviceMain;

	private Window window;
	private Listbox listbox;

	private Textbox queryNameSearchTextbox;
	private Textbox createdAtSearchTextbox;
	private Textbox startAtSearchTextbox;
	private Textbox doneAtSearchTextbox;
	private Textbox durationSearchTextbox;
	private Textbox memoryUsedSearchTextbox;
	private Textbox fileSizedSearchTextbox;

	private Button deleteButton;

	public FileHistory() {
		super("My activity", null, true);
		window = this;
		serviceMain = new ServiceImplMain();
		window.setMaximizable(true);
		window.setMaximized(true);

		listbox = new Listbox();
		listbox.setParent(window);
		listbox.setMold("paging");
		listbox.setAutopaging(true);
		listbox.setEmptyMessage("No actifity");
		listbox.setCheckmark(true);
		listbox.setItemRenderer(new ListItemRenderer());
		listbox.setVflex(true);
		listbox.setPagingPosition("bottom");

		Listhead listhead = new Listhead();
		listhead.setParent(listbox);
		listhead.setSizable(true);

		Auxhead auxhead = new Auxhead();
		auxhead.setParent(listbox);
		
		Listheader blankListheader = new Listheader();
		blankListheader.setParent(listhead);
		blankListheader.setWidth("100px");
		blankListheader.setStyle("text-align: center");
		Auxheader blankAuxheader = new Auxheader();
		blankAuxheader.setParent(auxhead);

		Listheader queryNameListheader = new Listheader("Query name");
		queryNameListheader.setParent(listhead);
		queryNameListheader.setSort("auto(queryName)");

		Auxheader queryNameAuxheader = new Auxheader();
		queryNameAuxheader.setParent(auxhead);
		queryNameSearchTextbox = new Textbox();
		queryNameSearchTextbox.setParent(queryNameAuxheader);
		queryNameSearchTextbox.setWidth("75%");
		queryNameSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						createdAtSearchTextbox.setValue("");
						startAtSearchTextbox.setValue("");
						doneAtSearchTextbox.setValue("");
						durationSearchTextbox.setValue("");
						memoryUsedSearchTextbox.setValue("");
						fileSizedSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		Image searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(queryNameAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listheader createdAtListheader = new Listheader("Activity date");
		createdAtListheader.setParent(listhead);
		createdAtListheader.setWidth("150px");
		createdAtListheader.setSort("auto(createdAt)");

		Auxheader createdAtAuxheader = new Auxheader();
		createdAtAuxheader.setParent(auxhead);
		createdAtSearchTextbox = new Textbox();
		createdAtSearchTextbox.setParent(createdAtAuxheader);
		createdAtSearchTextbox.setWidth("75%");
		createdAtSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchTextbox.setValue("");
						startAtSearchTextbox.setValue("");
						doneAtSearchTextbox.setValue("");
						durationSearchTextbox.setValue("");
						memoryUsedSearchTextbox.setValue("");
						fileSizedSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(createdAtAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listheader startAtListheader = new Listheader("Process on");
		startAtListheader.setParent(listhead);
		startAtListheader.setSort("auto(startAt)");

		Auxheader startAtAuxheader = new Auxheader();
		startAtAuxheader.setParent(auxhead);
		startAtSearchTextbox = new Textbox();
		startAtSearchTextbox.setParent(startAtAuxheader);
		startAtSearchTextbox.setWidth("75%");
		startAtSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchTextbox.setValue("");
						createdAtSearchTextbox.setValue("");
						doneAtSearchTextbox.setValue("");
						durationSearchTextbox.setValue("");
						memoryUsedSearchTextbox.setValue("");
						fileSizedSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(startAtAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listheader doneAtListheader = new Listheader("Process end");
		doneAtListheader.setParent(listhead);
		doneAtListheader.setSort("auto(doneAt)");

		Auxheader doneAtAuxheader = new Auxheader();
		doneAtAuxheader.setParent(auxhead);
		doneAtSearchTextbox = new Textbox();
		doneAtSearchTextbox.setParent(doneAtAuxheader);
		doneAtSearchTextbox.setWidth("75%");
		doneAtSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchTextbox.setValue("");
						createdAtSearchTextbox.setValue("");
						startAtSearchTextbox.setValue("");
						durationSearchTextbox.setValue("");
						memoryUsedSearchTextbox.setValue("");
						fileSizedSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(doneAtAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listheader durationListheader = new Listheader("Duration");
		durationListheader.setParent(listhead);
		durationListheader.setSort("auto(durationTime)");

		Auxheader durationAuxheader = new Auxheader();
		durationAuxheader.setParent(auxhead);
		durationSearchTextbox = new Textbox();
		durationSearchTextbox.setParent(durationAuxheader);
		durationSearchTextbox.setWidth("75%");
		durationSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchTextbox.setValue("");
						createdAtSearchTextbox.setValue("");
						startAtSearchTextbox.setValue("");
						doneAtSearchTextbox.setValue("");
						memoryUsedSearchTextbox.setValue("");
						fileSizedSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(durationAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listheader memoryUsedListheader = new Listheader("Memory used");
		memoryUsedListheader.setParent(listhead);
		memoryUsedListheader.setStyle("text-align: right");
		memoryUsedListheader.setSort("auto(memoryUsed)");

		Auxheader memoryUsedAuxheader = new Auxheader();
		memoryUsedAuxheader.setParent(auxhead);
		memoryUsedSearchTextbox = new Textbox();
		memoryUsedSearchTextbox.setParent(memoryUsedAuxheader);
		memoryUsedSearchTextbox.setWidth("75%");
		memoryUsedSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchTextbox.setValue("");
						createdAtSearchTextbox.setValue("");
						startAtSearchTextbox.setValue("");
						doneAtSearchTextbox.setValue("");
						durationSearchTextbox.setValue("");
						fileSizedSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(memoryUsedAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listheader fileSizedListheader = new Listheader("File size");
		fileSizedListheader.setParent(listhead);
		fileSizedListheader.setStyle("text-align: right");
		fileSizedListheader.setSort("auto(fileData.filesize)");

		Auxheader fileSizedAuxheader = new Auxheader();
		fileSizedAuxheader.setParent(auxhead);
		fileSizedSearchTextbox = new Textbox();
		fileSizedSearchTextbox.setParent(fileSizedAuxheader);
		fileSizedSearchTextbox.setWidth("75%");
		fileSizedSearchTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchTextbox.setValue("");
						createdAtSearchTextbox.setValue("");
						startAtSearchTextbox.setValue("");
						doneAtSearchTextbox.setValue("");
						durationSearchTextbox.setValue("");
						memoryUsedSearchTextbox.setValue("");
						refreshListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(fileSizedAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Listfoot listfoot = new Listfoot();
		listfoot.setParent(listbox);

		Listfooter listfooter = new Listfooter();
		listfooter.setParent(listfoot);
		listfooter.setSpan(listhead.getChildren().size() - 1);
		listfooter.setStyle("text-align: center;");
		deleteButton = new Button("Delete");
		deleteButton.setParent(listfooter);
		deleteButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event deleteEvent) {
						if (Messagebox.show("Delete selected data?",
								"Question", Messagebox.YES | Messagebox.NO,
								Messagebox.QUESTION) == Messagebox.YES) {
							for (Listitem listitem : listbox.getSelectedItems()) {
								Activity activitySelected = listitem.getValue();
								serviceMain.saveUserActivity("delete task activity with name = "+activitySelected.getQueryName());
								serviceMain.deleteActivity(activitySelected);
							}
							refreshListbox();
						}
					}
				});

		refreshListbox();
	}

	private void refreshListbox() {
		org.hibernate.Session sessionSelect = null;
		try {
			sessionSelect = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = sessionSelect.createCriteria(Activity.class);
			criteria.add(Restrictions.isNotNull("doneAt"));

			if (!queryNameSearchTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("queryName", queryNameSearchTextbox.getValue()+"%"));
			} else if (!createdAtSearchTextbox.getValue().isEmpty()) {
				criteria = serviceMain.getCriteriaAtDateBetween(criteria, "createdAt", createdAtSearchTextbox.getValue());
			} else if (!startAtSearchTextbox.getValue().isEmpty()) {
				criteria = serviceMain.getCriteriaAtDateBetween(criteria, "startAt", startAtSearchTextbox.getValue());
			} else if (!doneAtSearchTextbox.getValue().isEmpty()) {
				criteria = serviceMain.getCriteriaAtDateBetween(criteria, "doneAt", doneAtSearchTextbox.getValue());
			} else if (!durationSearchTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("showDuration", durationSearchTextbox.getValue()+"%"));
			} else if (!memoryUsedSearchTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("showMemoryUsed", memoryUsedSearchTextbox.getValue()+"%"));
			} else if (!fileSizedSearchTextbox.getValue().isEmpty()) {
				criteria.createAlias("fileData", "fileData");
				criteria.add(Restrictions.like("fileData.filesizeToShow", fileSizedSearchTextbox.getValue()+"%"));
			}
			
			listbox.setModel(new ListModelList<UserActivity>(
					(List<UserActivity>) criteria.list()));
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

	public class ListItemRenderer implements ListitemRenderer<Activity> {

		@Override
		public void render(Listitem listitem, Activity activity, int index)
				throws Exception {
			listitem.setValue(activity);
			
			Listcell gearListcell = new Listcell();
			gearListcell.setParent(listitem);
			gearListcell.setStyle("width: 100px;");
			ButtonCustom gearButton = new ButtonCustom("image/gear.png",
					activity);
			gearButton.setParent(gearListcell);
			gearButton.setStyle("margin-left: 10px;");
			gearButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event gearEvent) {
							ButtonCustom buttonCustom = (ButtonCustom) gearEvent
									.getTarget();
							if (!buttonCustom.isDisabled()) {
								buttonCustom.setDisabled(true);
								ActivityDetailWindow activityDetailWindow = new ActivityDetailWindow(
										(Activity) buttonCustom.getDataObject());
								activityDetailWindow.setParent(window);
								activityDetailWindow.doModal();

								if (activityDetailWindow.isRefreshActivity()) {
									refreshListbox();
								}
								buttonCustom.setDisabled(false);
							}
						}
					});
			
			listitem.appendChild(new Listcell(activity.getQueryName()));
			listitem.appendChild(new Listcell(serviceMain
					.convertStringFromDate("dd/MM/yyyy HH:mm",
							activity.getCreatedAt())));
			listitem.appendChild(new Listcell(serviceMain
					.convertStringFromDate("dd/MM/yyyy HH:mm",
							activity.getStartAt())));
			listitem.appendChild(new Listcell(serviceMain
					.convertStringFromDate("dd/MM/yyyy HH:mm",
							activity.getDoneAt())));
			listitem.appendChild(new Listcell(activity.getShowDuration()));
			
			Listcell showMemoryUsedListcell = new Listcell(activity.getShowMemoryUsed());
			showMemoryUsedListcell.setParent(listitem);
			showMemoryUsedListcell.setStyle("text-align: right");
			
			Listcell fileSizeListcell = new Listcell(activity.getFileData()
					.getFilesizeToShow());
			fileSizeListcell.setParent(listitem);
			fileSizeListcell.setStyle("text-align: right");
			
			if (!listbox.isMultiple()) {
				listbox.setMultiple(true);
			}
		}
	}

}
