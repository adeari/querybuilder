package apps.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.components.ButtonCustom;
import apps.controller.activity.ActivityDetailWindow;
import apps.controller.history.FileHistory;
import apps.controller.history.UserActivityWindow;
import apps.controller.querycontrol.QueryOperation;
import apps.controller.queryy.QueryListWindow;
import apps.controller.users.ChangePasswordWindow;
import apps.controller.users.ProfileWindow;
import apps.controller.users.UsersWindow;
import apps.entity.Activity;
import apps.entity.FileSizeTotal;
import apps.entity.FileSizeUsed;
import apps.entity.FilesData;
import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class MenuWindow extends Window {
	private static final long serialVersionUID = -2091055007101580190L;
	private static final Logger logger = Logger.getLogger(MenuWindow.class);
	private Users _user;
	private Session _session;
	private ServiceMain serviceMain;

	private Window menuWindow;
	private Vlayout vlayout;
	private Menuitem queryBuilderMenuitem;
	private Menuitem usersMenuitem;
	private Menuitem profileMenuitem;
	private Menuitem changePasswordMenuitem;
	private Menuitem queryManagementMenuitem;
	private Menuitem queryOperationMenuitem;
	private Menuitem logoutMenuitem;

	private Menuitem myAcivityMenuitem;
	private Menuitem usersAcivityMenuitem;
	private Menuitem filesMenuitem;

	private Window mainWindow;
	private Label userloginLabel;

	private Listbox activityListbox;
	private Auxheader topAuxheader;
	private Textbox queryNameSearchingTextbox;
	private Textbox querySearchingTextbox;
	private Textbox fileTypeSearchingTextbox;
	private Textbox createdAtSearchingTextbox;
	private Textbox doneAtSearchingTextbox;
	private Textbox fileSizeSearchingTextbox;

	private Textbox totalFileSizeTextbox;
	
	private org.hibernate.Session _querySession;
	
	private SimpleDateFormat _simpleDateFormat;

	public MenuWindow(Window windowMain) {
		super(null, null, false);
		serviceMain = new ServiceImplMain();
		_simpleDateFormat = new SimpleDateFormat();

		_session = Sessions.getCurrent();
		_user = (Users) _session.getAttribute("userlogin");

		mainWindow = windowMain;
		menuWindow = this;

		vlayout = new Vlayout();
		vlayout.setParent(menuWindow);
		vlayout.setStyle("position:absolute; top:0; bottom:0; right:0; left:0;border-style:none;");

		Menubar mainMenubar = new Menubar();
		mainMenubar.setParent(vlayout);

		Menu mainMenu = new Menu("Main");
		mainMenu.setParent(mainMenubar);

		Menupopup mainMenupopup = new Menupopup();
		mainMenupopup.setParent(mainMenu);

		Menu historyMenu = new Menu("History");
		historyMenu.setParent(mainMenubar);

		Menupopup historyMenupopup = new Menupopup();
		historyMenupopup.setParent(historyMenu);

		queryBuilderMenuitem = new Menuitem("Query builder");
		queryBuilderMenuitem.setVisible(false);
		queryBuilderMenuitem.setParent(mainMenupopup);
		queryBuilderMenuitem.setImage("image/database-icon.png");
		queryBuilderMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!queryBuilderMenuitem.isDisabled()) {
							queryBuilderMenuitem.setDisabled(true);
							QueryWindows queryWindows = new QueryWindows(
									queryBuilderMenuitem.getLabel(), null);
							queryWindows.setParent(menuWindow);
							queryWindows
									.setStyle("position:absolute;top:0; bottom:0; right:0; left:0;border-style:none");
							queryWindows.doModal();

							settingForLoginUser();

							queryBuilderMenuitem.setDisabled(false);
						}
					}
				});

		usersMenuitem = new Menuitem("Users");
		usersMenuitem.setParent(mainMenupopup);
		usersMenuitem.setVisible(false);
		usersMenuitem.setImage("image/user.png");
		usersMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!usersMenuitem.isDisabled()) {
							usersMenuitem.setDisabled(true);
							UsersWindow usersWindows = new UsersWindow(
									usersMenuitem.getLabel());
							usersWindows.setParent(menuWindow);
							usersWindows
									.setStyle("position:absolute;top:0; bottom:0; right:0; left:0;border-style:none");
							usersWindows.doModal();

							settingForLoginUser();

							usersMenuitem.setDisabled(false);
						}
					}
				});

		queryManagementMenuitem = new Menuitem("Query management");
		queryManagementMenuitem.setParent(mainMenupopup);
		queryManagementMenuitem.setVisible(false);
		queryManagementMenuitem.setImage("image/sql1.png");
		queryManagementMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!queryManagementMenuitem.isDisabled()) {
							queryManagementMenuitem.setDisabled(true);

							QueryListWindow queryListWindow = new QueryListWindow(
									queryManagementMenuitem.getLabel());
							queryListWindow.setParent(menuWindow);
							queryListWindow
									.setStyle("position:absolute;top:0; bottom:0; right:0; left:0;border-style:none");
							queryListWindow.doModal();

							settingForLoginUser();

							queryManagementMenuitem.setDisabled(false);
						}
					}
				});

		profileMenuitem = new Menuitem("Profile");
		profileMenuitem.setParent(mainMenupopup);
		profileMenuitem.setImage("image/rss.png");
		profileMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!profileMenuitem.isDisabled()) {
							profileMenuitem.setDisabled(true);

							ProfileWindow profileWindow = new ProfileWindow(
									profileMenuitem.getLabel(), _user);
							profileWindow.setParent(menuWindow);
							profileWindow.setWidth("400px");
							profileWindow.doModal();

							settingForLoginUser();

							profileMenuitem.setDisabled(false);
						}
					}
				});

		changePasswordMenuitem = new Menuitem("Change password");
		changePasswordMenuitem.setParent(mainMenupopup);
		changePasswordMenuitem.setImage("image/rss.png");
		changePasswordMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!changePasswordMenuitem.isDisabled()) {
							changePasswordMenuitem.setDisabled(true);

							ChangePasswordWindow changePasswordWindow = new ChangePasswordWindow(
									changePasswordMenuitem.getLabel());
							changePasswordWindow.setParent(menuWindow);
							changePasswordWindow.setWidth("400px");
							changePasswordWindow.doModal();

							settingForLoginUser();

							changePasswordMenuitem.setDisabled(false);
						}
					}
				});

		queryOperationMenuitem = new Menuitem("Query opearation");
		queryOperationMenuitem.setParent(mainMenupopup);
		queryOperationMenuitem.setImage("image/sql.png");
		queryOperationMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!queryOperationMenuitem.isDisabled()) {
							queryOperationMenuitem.setDisabled(true);

							QueryOperation queryOperation = new QueryOperation(
									"Query operation");
							queryOperation.setParent(menuWindow);
							queryOperation.doModal();

							refreshActivityListbox();

							queryOperationMenuitem.setDisabled(false);
						}
					}
				});

		logoutMenuitem = new Menuitem("Logout");
		logoutMenuitem.setParent(mainMenupopup);
		logoutMenuitem.setImage("image/logout.png");
		logoutMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!logoutMenuitem.isDisabled()) {
							logoutMenuitem.setDisabled(true);

							logout();

							logoutMenuitem.setDisabled(false);
						}
					}
				});

		myAcivityMenuitem = new Menuitem("My activity");
		myAcivityMenuitem.setParent(historyMenupopup);
		myAcivityMenuitem.setImage("image/activity.png");
		myAcivityMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!myAcivityMenuitem.isDisabled()) {
							myAcivityMenuitem.setDisabled(true);
							UserActivityWindow userActivity = new UserActivityWindow(true);
							userActivity.setParent(menuWindow);
							userActivity.doModal();
							myAcivityMenuitem.setDisabled(false);
						}
					}
				});

		usersAcivityMenuitem = new Menuitem("Users activity");
		usersAcivityMenuitem.setParent(historyMenupopup);
		usersAcivityMenuitem.setImage("image/activity.png");
		usersAcivityMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!myAcivityMenuitem.isDisabled()) {
							myAcivityMenuitem.setDisabled(true);
							UserActivityWindow userActivity = new UserActivityWindow(false);
							userActivity.setParent(menuWindow);
							userActivity.doModal();
							myAcivityMenuitem.setDisabled(false);
						}
					}
				});

		filesMenuitem = new Menuitem("Files history");
		filesMenuitem.setParent(historyMenupopup);
		filesMenuitem.setImage("image/activity.png");
		filesMenuitem.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
			public void onEvent(Event event) {
				if (!filesMenuitem.isDisabled()) {
					filesMenuitem.setDisabled(true);
					FileHistory fileHistory = new FileHistory();
					fileHistory.setParent(menuWindow);
					fileHistory.doModal();
					filesMenuitem.setDisabled(false);
				}
			}
		});

		Div themeDiv = new Div();
		themeDiv.setParent(menuWindow);
		themeDiv.setStyle("position: absolute; margin: -5px 0 0 500px; text-align: right");

		userloginLabel = new Label();
		userloginLabel.setParent(themeDiv);
		userloginLabel
				.setStyle("font-size: 20px; font-weight: bold; margin: 0 20px 0 0;");

		Label themeLabel = new Label("Theme");
		themeLabel.setParent(themeDiv);
		themeLabel.setStyle("margin: 0 20px 0 0;");

		Selectbox themeSelectbox = new Selectbox();
		themeSelectbox.setParent(themeDiv);
		themeSelectbox.setStyle("margin: 0 10px 0 0;");
		themeSelectbox.setWidth("100px");
		String[] themeList = new String[] { "Atlantic", "Bootstrap", "Breeze",
				"Cerulean", "Cosmo", "Cyan", "Dark", "Flatly", "Journal",
				"Sapphire", "Silvertail" };
		ListModelList<String> themeListModelList = new ListModelList<String>(
				themeList);
		ListModel<String> listModel = themeListModelList;
		themeSelectbox.setModel(listModel);
		themeSelectbox.addEventListener(Events.ON_SELECT,
				new EventListener<Event>() {
					public void onEvent(Event themeEvent) {
						Selectbox selectbox = (Selectbox) themeEvent
								.getTarget();
						Library.setProperty(
								"org.zkoss.theme.preferred",
								selectbox
										.getModel()
										.getElementAt(
												selectbox.getSelectedIndex())
										.toString().toLowerCase());
						try {
							_querySession = hibernateUtil.getSessionFactory(_querySession);
							_user.setTheme(Library
									.getProperty("org.zkoss.theme.preferred"));
							_querySession.update(_user);
							_querySession.flush();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
						Executions.sendRedirect(null);
					}

				});
		String themeUsed = Library.getProperty("org.zkoss.theme.preferred");
		themeUsed = themeUsed.substring(0, 1).toUpperCase()
				+ themeUsed.substring(1);
		themeListModelList.addToSelection(themeUsed);

		Div totalFileSizeDiv = new Div();
		totalFileSizeDiv.setParent(vlayout);
		totalFileSizeDiv.setStyle("text-align: right");

		Label totalFileSizeLabel = new Label("Total file size: ");
		totalFileSizeLabel.setParent(totalFileSizeDiv);

		totalFileSizeTextbox = new Textbox("0");
		totalFileSizeTextbox.setParent(totalFileSizeDiv);
		totalFileSizeTextbox.setReadonly(true);
		totalFileSizeTextbox.setStyle("text-align: right; margin: 0 20px 0 0;");

		activityListbox = new Listbox();
		activityListbox.setMold("paging");
		activityListbox.setAutopaging(true);
		activityListbox.setPagingPosition("bottom");
		activityListbox.setEmptyMessage("No actifity");
		activityListbox
				.setStyle("position: relative; bottom:0; right:0; left:0;border-style:none; width: 100%; height: 559px;");
		activityListbox.setItemRenderer(new ActivityItemRenderer());

		Auxhead topAuxhead = new Auxhead();
		topAuxhead.setParent(activityListbox);

		topAuxheader = new Auxheader();
		topAuxheader.setParent(topAuxhead);
		topAuxheader.setColspan(8);

		Listhead listhead = new Listhead();
		listhead.setParent(activityListbox);
		listhead.setSizable(true);

		Listheader blankListheader = new Listheader();
		blankListheader.setParent(listhead);
		blankListheader.setWidth("60px");

		Listheader queryNameListheader = new Listheader("Query name");
		queryNameListheader.setParent(listhead);
		queryNameListheader.setSort("auto(queryName)");

		Listheader queryListheader = new Listheader("Query");
		queryListheader.setParent(listhead);
		queryListheader.setSort("auto(query)");

		Listheader filetypeListheader = new Listheader("File type");
		filetypeListheader.setParent(listhead);
		filetypeListheader.setSort("auto(filetype)");

		Listheader createdAtListheader = new Listheader("Created At");
		createdAtListheader.setParent(listhead);
		createdAtListheader.setSort("auto(createdAt)");

		Listheader doneAtListheader = new Listheader("Done At");
		doneAtListheader.setParent(listhead);
		doneAtListheader.setSort("auto(doneAt)");

		Listheader downloadListheader = new Listheader("Download");
		downloadListheader.setParent(listhead);
		downloadListheader.setStyle("text-align: center;");
		downloadListheader.setWidth("80px");

		Listheader fileSizeListheader = new Listheader("File size");
		fileSizeListheader.setParent(listhead);
		fileSizeListheader.setStyle("text-align: right; padding: 0 25px 0 0;");
		fileSizeListheader.setSort("auto(fileData.filesize)");

		Auxhead activityAuxhead = new Auxhead();
		activityAuxhead.setParent(activityListbox);

		Auxheader blankAuxheader = new Auxheader();
		blankAuxheader.setParent(activityAuxhead);

		Auxheader queryNameAuxheader = new Auxheader();
		queryNameAuxheader.setParent(activityAuxhead);
		queryNameSearchingTextbox = new Textbox();
		queryNameSearchingTextbox.setParent(queryNameAuxheader);
		queryNameSearchingTextbox.setWidth("75%");
		queryNameSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						querySearchingTextbox.setValue("");
						fileTypeSearchingTextbox.setValue("");
						createdAtSearchingTextbox.setValue("");
						doneAtSearchingTextbox.setValue("");
						fileSizeSearchingTextbox.setValue("");
						refreshActivityListbox();
					}
				});
		Image searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(queryNameAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Auxheader queryAuxheader = new Auxheader();
		queryAuxheader.setParent(activityAuxhead);
		querySearchingTextbox = new Textbox();
		querySearchingTextbox.setParent(queryAuxheader);
		querySearchingTextbox.setWidth("75%");
		querySearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchingTextbox.setValue("");
						fileTypeSearchingTextbox.setValue("");
						createdAtSearchingTextbox.setValue("");
						doneAtSearchingTextbox.setValue("");
						fileSizeSearchingTextbox.setValue("");
						refreshActivityListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(queryAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Auxheader fileTypeAuxheader = new Auxheader();
		fileTypeAuxheader.setParent(activityAuxhead);
		fileTypeSearchingTextbox = new Textbox();
		fileTypeSearchingTextbox.setParent(fileTypeAuxheader);
		fileTypeSearchingTextbox.setWidth("75%");
		fileTypeSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchingTextbox.setValue("");
						querySearchingTextbox.setValue("");
						createdAtSearchingTextbox.setValue("");
						doneAtSearchingTextbox.setValue("");
						fileSizeSearchingTextbox.setValue("");
						refreshActivityListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(fileTypeAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Auxheader createdAtAuxheader = new Auxheader();
		createdAtAuxheader.setParent(activityAuxhead);
		createdAtSearchingTextbox = new Textbox();
		createdAtSearchingTextbox.setParent(createdAtAuxheader);
		createdAtSearchingTextbox.setWidth("75%");
		createdAtSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchingTextbox.setValue("");
						querySearchingTextbox.setValue("");
						fileTypeSearchingTextbox.setValue("");
						doneAtSearchingTextbox.setValue("");
						fileSizeSearchingTextbox.setValue("");
						refreshActivityListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(createdAtAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		Auxheader doneAtAuxheader = new Auxheader();
		doneAtAuxheader.setParent(activityAuxhead);
		doneAtSearchingTextbox = new Textbox();
		doneAtSearchingTextbox.setParent(doneAtAuxheader);
		doneAtSearchingTextbox.setWidth("75%");
		doneAtSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchingTextbox.setValue("");
						querySearchingTextbox.setValue("");
						fileTypeSearchingTextbox.setValue("");
						createdAtSearchingTextbox.setValue("");
						fileSizeSearchingTextbox.setValue("");
						refreshActivityListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(doneAtAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		blankAuxheader = new Auxheader();
		blankAuxheader.setParent(activityAuxhead);

		Auxheader fileSizeAuxheader = new Auxheader();
		fileSizeAuxheader.setParent(activityAuxhead);
		fileSizeSearchingTextbox = new Textbox();
		fileSizeSearchingTextbox.setParent(fileSizeAuxheader);
		fileSizeSearchingTextbox.setWidth("75%");
		fileSizeSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event namedSearchEvent) {
						queryNameSearchingTextbox.setValue("");
						querySearchingTextbox.setValue("");
						fileTypeSearchingTextbox.setValue("");
						createdAtSearchingTextbox.setValue("");
						doneAtSearchingTextbox.setValue("");
						refreshActivityListbox();
					}
				});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(fileSizeAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		
		settingForLoginUser();
		
		
		
		
	}

	private void refreshActivityListbox() {
		try {
			_querySession = hibernateUtil.getSessionFactory(_querySession);
			_querySession.clear();
			Criteria criteria = _querySession.createCriteria(Activity.class);
			if (!_user.getDivisi().equalsIgnoreCase("admin")) {
				criteria.add(Restrictions.eq("userCreated", _user));
			}
			if (!queryNameSearchingTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("queryName",
						queryNameSearchingTextbox.getValue() + "%")
						.ignoreCase());
			} else if (!querySearchingTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("query",
						querySearchingTextbox.getValue() + "%").ignoreCase());
			} else if (!fileTypeSearchingTextbox.getValue().isEmpty()) {
				criteria.add(Restrictions.like("filetype",
						fileTypeSearchingTextbox.getValue() + "%").ignoreCase());
			} else if (!createdAtSearchingTextbox.getValue().isEmpty()) {
				Timestamp basic = serviceMain.convertToTimeStamp(
						"dd/MM/yyyy HH:mm",
						createdAtSearchingTextbox.getValue(), _simpleDateFormat);
				if (basic == null) {
					Timestamp lowTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy", createdAtSearchingTextbox.getValue(), _simpleDateFormat);
					Timestamp highTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy HH:mm:ss",
							createdAtSearchingTextbox.getValue() + " 23:59:59", _simpleDateFormat);
					criteria.add(Restrictions.between("createdAt",
							lowTimestamp, highTimestamp));
				} else {
					Timestamp highTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy HH:mm:ss",
							createdAtSearchingTextbox.getValue() + ":59", _simpleDateFormat);
					criteria.add(Restrictions.between("createdAt", basic,
							highTimestamp));
				}
			} else if (!doneAtSearchingTextbox.getValue().isEmpty()) {
				Timestamp basic = serviceMain.convertToTimeStamp(
						"dd/MM/yyyy HH:mm", doneAtSearchingTextbox.getValue(), _simpleDateFormat);
				if (basic == null) {
					Timestamp lowTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy", doneAtSearchingTextbox.getValue(), _simpleDateFormat);
					Timestamp highTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy HH:mm:ss",
							doneAtSearchingTextbox.getValue() + " 23:59:59", _simpleDateFormat);
					criteria.add(Restrictions.between("doneAt", lowTimestamp,
							highTimestamp));
				} else {
					Timestamp highTimestamp = serviceMain.convertToTimeStamp(
							"dd/MM/yyyy HH:mm:ss",
							doneAtSearchingTextbox.getValue() + ":59", _simpleDateFormat);
					criteria.add(Restrictions.between("doneAt", basic,
							highTimestamp));
				}
			} else if (!fileSizeSearchingTextbox.getValue().isEmpty()) {
				criteria.createCriteria("fileData", "fileData");
				criteria.add(Restrictions.like("fileData.filesizeToShow",
						fileSizeSearchingTextbox.getValue() + "%").ignoreCase());
			}

			if (criteria.list().size() > 0
					&& activityListbox.getParent() == null) {
				activityListbox.setParent(vlayout);
			}

			activityListbox.setModel(new ListModelList<Activity>(
					(List<Activity>) criteria.list()));

			if (_user.getDivisi().equalsIgnoreCase("admin")) {
				criteria = _querySession.createCriteria(FileSizeTotal.class);
				FileSizeTotal fileSizeTotal = (FileSizeTotal) criteria
						.uniqueResult();
				if (fileSizeTotal != null) {
					totalFileSizeTextbox.setValue(fileSizeTotal
							.getFilesizeShow());
				}
			} else {
				criteria = _querySession.createCriteria(FileSizeUsed.class);
				criteria.add(Restrictions.eq("userOwner", _user));
				FileSizeUsed fileSizeUsed = (FileSizeUsed) criteria
						.uniqueResult();
				if (fileSizeUsed != null) {
					totalFileSizeTextbox.setValue(fileSizeUsed
							.getFilesizeShow());
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}
		
	}

	public void logout() {
		if (_user != null) {
			serviceMain.saveUserActivity(_querySession, "Logout");
			_session.removeAttribute("userlogin");

			MenuWindow menuWindow = (MenuWindow) mainWindow.getChildren()
					.get(1);
			menuWindow.setVisible(false);

			LoginWindow loginWindow = (LoginWindow) mainWindow.getChildren()
					.get(0);
			loginWindow.setVisible(true);

			loginWindow.openLoginWindow();
		}
	}

	public void settingForLoginUser() {
		if (_user == null) {
			logout();
		} else {
			serviceMain.saveUserActivity(_querySession, "Login");
			if (_user.getDivisi().equalsIgnoreCase("Admin")) {
				usersMenuitem.setVisible(true);
				queryBuilderMenuitem.setVisible(true);
				queryManagementMenuitem.setVisible(true);
				usersAcivityMenuitem.setVisible(true);
				filesMenuitem.setVisible(true);
			} else {
				usersMenuitem.setVisible(false);
				queryBuilderMenuitem.setVisible(false);
				queryManagementMenuitem.setVisible(false);
				usersAcivityMenuitem.setVisible(false);
				filesMenuitem.setVisible(false);
			}
			userloginLabel.setValue("Welcome " + _user.getUsername());
			topAuxheader.setLabel(_user.getUsername() + "'s query activity");

			refreshActivityListbox();
			Timer timer = new Timer(10000);
			timer.setParent(menuWindow);
			timer.addEventListener(Events.ON_TIMER, new EventListener<Event>() {  
		        public void onEvent(Event evt) {  
		        	refreshActivityListbox();
		     }  
		 });
			timer.setRepeats(true);
			timer.setRunning(true);
		}

	}

	public class ActivityItemRenderer implements ListitemRenderer<Activity> {

		@Override
		public void render(Listitem listitem, Activity activity, int index)
				throws Exception {

			Listcell getListcell = new Listcell();
			getListcell.setParent(listitem);
			ButtonCustom gearButton = new ButtonCustom("image/gear.png",
					activity);
			gearButton.setParent(getListcell);
			gearButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event gearEvent) {
							ButtonCustom buttonCustom = (ButtonCustom) gearEvent
									.getTarget();
							if (!buttonCustom.isDisabled()) {
								buttonCustom.setDisabled(true);
								ActivityDetailWindow activityDetailWindow = new ActivityDetailWindow(
										(Activity) buttonCustom.getDataObject());
								activityDetailWindow.setParent(menuWindow);
								activityDetailWindow.doModal();

								if (activityDetailWindow.isRefreshActivity()) {
									refreshActivityListbox();
								}
								buttonCustom.setDisabled(false);
							}
						}
					});

			listitem.appendChild(new Listcell(activity.getQueryName()));
			String query = activity.getQuery();
			if (query.length() > 25) {
				query = query.substring(0, 25) + "...";
			}
			listitem.appendChild(new Listcell(query));
			listitem.appendChild(new Listcell(activity.getFiletype()));
			listitem.appendChild(new Listcell(serviceMain
					.convertStringFromDate("dd/MM/yyyy HH:mm",
							activity.getCreatedAt(),_simpleDateFormat)));
			if (activity.getDoneAt() == null) {
				Listcell listcell = new Listcell("Process...");
				listcell.setSpan(3);
				listcell.setParent(listitem);
			} else {
				listitem.appendChild(new Listcell(serviceMain
						.convertStringFromDate("dd/MM/yyyy HH:mm",
								activity.getDoneAt(),_simpleDateFormat)));

				boolean isFileexist = false;
				if (activity.getFileData() != null) {
					FilesData filesData = activity.getFileData();
					File file = new File(serviceMain.getQuery("location."
							+ filesData.getFiletype().toLowerCase())
							+ "/" + filesData.getFilename());
					if (file.isFile()) {
						Listcell downloadListcell = new Listcell();
						downloadListcell.setParent(listitem);
						downloadListcell.setStyle("text-align: center; width: 80px;");
						ButtonCustom downloadButtonCustom = new ButtonCustom(
								"image/download.png", activity);
						downloadButtonCustom.addEventListener(Events.ON_CLICK,
								new EventListener<Event>() {
									public void onEvent(Event downloadEvent) {
										ButtonCustom buttonSelectedButtonCustom = (ButtonCustom) downloadEvent
												.getTarget();
										Activity activitySelected = (Activity) buttonSelectedButtonCustom
												.getDataObject();
										FilesData filesDataSelected = activitySelected
												.getFileData();
										File file = new File(serviceMain
												.getQuery("location."
														+ filesDataSelected
																.getFiletype()
																.toLowerCase())
												+ "/"
												+ filesDataSelected
														.getFilename());
										try {
											serviceMain
													.saveUserActivity(_querySession, "Download file "
															+ file.getName());
											Filedownload.save(file,
													activitySelected
															.getFiletype());
										} catch (FileNotFoundException e) {
											logger.error(e.getMessage(), e);
										}
									}
								});
						downloadButtonCustom.setParent(downloadListcell);

						Listcell fileSizeListcell = new Listcell(
								filesData.getFilesizeToShow());
						fileSizeListcell.setParent(listitem);
						fileSizeListcell
								.setStyle("text-align: right; padding: 0 25px 0 0;");
						isFileexist = true;
					}
				}
				if (!isFileexist) {
					Listcell listcell = new Listcell("File not exist");
					listcell.setParent(listitem);
					listcell = new Listcell("0");
					listcell.setStyle("text-align: right; padding: 0 25px 0 0;");
					listcell.setParent(listitem);
				}
			}
		}
	}
}
