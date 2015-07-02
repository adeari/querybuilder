package apps.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
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
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.components.ButtonCustom;
import apps.controller.queryy.QueryListWindow;
import apps.controller.users.ChangePasswordWindow;
import apps.controller.users.ProfileWindow;
import apps.controller.users.UsersWindow;
import apps.entity.Activity;
import apps.entity.FileSizeUsed;
import apps.entity.Users;
import apps.query.control.QueryOperation;
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
	private Menuitem queryBuilderMenuitem;
	private Menuitem usersMenuitem;
	private Menuitem profileMenuitem;
	private Menuitem changePasswordMenuitem;
	private Menuitem queryManagementMenuitem;
	private Menuitem queryOperationMenuitem;
	private Menuitem logoutMenuitem;
	private Window mainWindow;
	private Label userloginLabel;
	private Listbox activityListbox;
	private Auxheader topAuxheader;
	private Textbox totalFileSizeTextbox;

	public MenuWindow(Window windowMain) {
		super(null, null, false);
		serviceMain = new ServiceImplMain();

		_session = Sessions.getCurrent();
		_user = (Users) _session.getAttribute("userlogin");

		mainWindow = windowMain;
		menuWindow = this;

		Vlayout vlayout = new Vlayout();
		vlayout.setParent(menuWindow);
		vlayout.setStyle("position:absolute; top:0; bottom:0; right:0; left:0;border-style:none;");

		Menubar mainMenubar = new Menubar();
		Menu mainMenu = new Menu("Main");

		Menupopup mainMenupopup = new Menupopup();

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

		mainMenupopup.setParent(mainMenu);
		mainMenu.setParent(mainMenubar);
		mainMenubar.setParent(vlayout);
		
		Div themeDiv = new Div();
		themeDiv.setParent(menuWindow);
		themeDiv.setStyle("position: absolute; margin: -5px 0 0 500px; text-align: right");
		
		
		userloginLabel = new Label();
		userloginLabel.setParent(themeDiv);
		userloginLabel.setStyle("font-size: 20px; font-weight: bold; margin: 0 20px 0 0;");

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
						org.hibernate.Session querySession = null;
						try {
							querySession = hibernateUtil.getSessionFactory()
									.openSession();
							_user.setTheme(Library
									.getProperty("org.zkoss.theme.preferred"));
							Transaction trx = querySession.beginTransaction();
							querySession.update(_user);
							trx.commit();
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
		
		Label totalFileSizeLabel = new Label("Total Size: ");
		totalFileSizeLabel.setParent(totalFileSizeDiv);
		
		
		totalFileSizeTextbox = new Textbox("0");
		totalFileSizeTextbox.setParent(totalFileSizeDiv);
		totalFileSizeTextbox.setReadonly(true);
		totalFileSizeTextbox.setStyle("text-align: right; margin: 0 20px 0 0;");
		
		activityListbox = new Listbox();
		activityListbox.setParent(vlayout);
		activityListbox.setMold("paging");
		activityListbox.setAutopaging(true);
		activityListbox.setEmptyMessage("No actifity");
		activityListbox.setStyle("position: relative; bottom:0; right:0; left:0;border-style:none; width: 100%;");
		activityListbox.setItemRenderer(new ActivityItemRenderer());
		
		Auxhead topAuxhead = new Auxhead();
		topAuxhead.setParent(activityListbox);
		
		topAuxheader = new Auxheader();
		topAuxheader.setParent(topAuxhead);
		topAuxheader.setColspan(7);
		
		Listhead listhead = new Listhead();
		listhead.setParent(activityListbox);
		
		Listheader queryNameListheader = new Listheader("Query name");
		queryNameListheader.setParent(listhead);
		
		Listheader queryListheader = new Listheader("Query");
		queryListheader.setParent(listhead);
		
		Listheader filetypeListheader = new Listheader("File type");
		filetypeListheader.setParent(listhead);
		
		Listheader createdAtListheader = new Listheader("Created At");
		createdAtListheader.setParent(listhead);
		
		Listheader doneAtListheader = new Listheader("Done At");
		doneAtListheader.setParent(listhead);
		
		Listheader downloadListheader = new Listheader("Download");
		downloadListheader.setParent(listhead);
		downloadListheader.setStyle("text-align: center;");
		
		Listheader fileSizeListheader = new Listheader("File size");
		fileSizeListheader.setParent(listhead);
		fileSizeListheader.setStyle("text-align: right; padding: 0 25px 0 0;");
		
		settingForLoginUser();
	}
	
	public void refreshActivityListbox() {
		org.hibernate.Session sessionSelect = null;
		try {
			sessionSelect = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = sessionSelect.createCriteria(Activity.class);
			if (!_user.getDivisi().equalsIgnoreCase("admin")) {
				criteria.add(Restrictions.eq("userCreated", _user));
			}
			
			activityListbox.setModel(new ListModelList<Activity>((List<Activity>) criteria.list()));
			
			criteria = sessionSelect.createCriteria(FileSizeUsed.class);
			if (_user.getDivisi().equalsIgnoreCase("admin")) {
				criteria.add(Restrictions.eq("userOwner", "total"));
			} else {
				criteria.add(Restrictions.eq("userOwner", _user));
			}
			if (criteria.list().size() > 0) {
				FileSizeUsed fileSizeUsed = (FileSizeUsed) criteria.uniqueResult();
				if (fileSizeUsed.getFilesize() > 0) {
					totalFileSizeTextbox.setValue(fileSizeUsed.getFilesizeShow());
				}
			}
			
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

	public void logout() {
		if (_user != null) {
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
			if (_user.getDivisi().equalsIgnoreCase("Admin")) {
				usersMenuitem.setVisible(true);
				queryBuilderMenuitem.setVisible(true);
				queryManagementMenuitem.setVisible(true);
			} else {
				usersMenuitem.setVisible(false);
				queryBuilderMenuitem.setVisible(false);
				queryManagementMenuitem.setVisible(false);
			}
			userloginLabel.setValue("Welcome " + _user.getUsername());
			topAuxheader.setLabel(_user.getUsername()+" Activity");
			
			refreshActivityListbox();
		}

	}
	
	public class ActivityItemRenderer implements ListitemRenderer<Activity> {

		@Override
		public void render(Listitem listitem, Activity activity, int index)
				throws Exception {
			listitem.appendChild(new Listcell(activity.getQueryName()));
			String query = activity.getQuery();
			if (query.length() > 100) {
				query = query.substring(0, 100);
			}
			listitem.appendChild(new Listcell(query));
			listitem.appendChild(new Listcell(activity.getFiletype()));
			listitem.appendChild(new Listcell(serviceMain.convertStringFromDate("dd/MM/yyyy HH:mm", activity.getCreatedAt())));
			if (activity.getDoneAt() == null) {
				Listcell listcell = new Listcell("Process...");
				listcell.setSpan(3);
				listcell.setParent(listitem);
			} else {
				listitem.appendChild(new Listcell(serviceMain.convertStringFromDate("dd/MM/yyyy HH:mm", activity.getDoneAt())));
				Listcell downloadListcell = new Listcell();
				downloadListcell.setParent(listitem);
				downloadListcell.setStyle("text-align: center;");
				ButtonCustom downloadButtonCustom = new ButtonCustom("image/download.png", activity);
				downloadButtonCustom.addEventListener(Events.ON_CLICK,
						new EventListener<Event>() {
							public void onEvent(Event downloadEvent) {
								ButtonCustom buttonSelectedButtonCustom = (ButtonCustom) downloadEvent.getTarget();
								Activity activitySelected = (Activity) buttonSelectedButtonCustom.getDataObject();
								File file = new File(serviceMain.getQuery("location.csv")+"/"+activitySelected.getFileData().getFilename());
								try {
									Filedownload.save(file, activitySelected.getFiletype());
								} catch (FileNotFoundException e) {
									logger.error(e.getMessage(), e);
								}
							}});
				downloadButtonCustom.setParent(downloadListcell);
				
				
				Listcell fileSizeListcell = new Listcell(activity.getFileData().getFilesizeToShow());
				fileSizeListcell.setParent(listitem);
				fileSizeListcell.setStyle("text-align: right; padding: 0 25px 0 0;");
				
			}
		}
		
	}
}
