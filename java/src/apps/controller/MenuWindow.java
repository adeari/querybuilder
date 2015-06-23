package apps.controller;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.controller.queryy.QueryListWindow;
import apps.controller.users.UsersWindow;
import apps.entity.Users;

public class MenuWindow extends Window {
	private static final long serialVersionUID = -2091055007101580190L;
	private Window menuWindow;
	private Menuitem queryBuilderMenuitem;
	private Menuitem usersMenuitem;
	private Menuitem changePasswordMenuitem;
	private Menuitem queryManagementMenuitem;
	private Menuitem logoutMenuitem;
	private Window mainWindow;
	private Label userloginLabel;

	public MenuWindow(Window windowMain) {
		super(null, null, false);
		mainWindow = windowMain;
		menuWindow = this;

		Vlayout vlayout = new Vlayout();
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
		usersMenuitem.setImage("image/users.jpg");
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
		queryManagementMenuitem.setImage("image/rss.png");
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

		logoutMenuitem = new Menuitem("Logout");
		logoutMenuitem.setParent(mainMenupopup);
		logoutMenuitem.setImage("image/logout.jpg");
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
		mainMenubar.setStyle("position:absolute; right:0; left:0;");

		vlayout.appendChild(mainMenubar);
		userloginLabel = new Label();
		userloginLabel
				.setStyle("color: blue; font-weight: bold; font-size : 20px;float: right; margin: 0 20px 0 0;");
		vlayout.appendChild(userloginLabel);
		menuWindow.appendChild(vlayout);
	}
	
	public void logout() {
		Session session = Sessions.getCurrent();
		session.removeAttribute("userlogin");

		MenuWindow menuWindow = (MenuWindow) mainWindow
				.getChildren().get(1);
		menuWindow.setVisible(false);

		LoginWindow loginWindow = (LoginWindow) mainWindow
				.getChildren().get(0);
		loginWindow.setVisible(true);

		loginWindow.openLoginWindow();
	}

	public void settingForLoginUser() {
		Session session = Sessions.getCurrent();
		Users user = (Users) session.getAttribute("userlogin");
		if (user == null) {
			logout();
		} else {
			if (user.getDivisi().equalsIgnoreCase("Admin")) {
				usersMenuitem.setVisible(true);
				queryBuilderMenuitem.setVisible(true);
				queryManagementMenuitem.setVisible(true);
			} else {
				usersMenuitem.setVisible(false);
				queryBuilderMenuitem.setVisible(false);
				queryManagementMenuitem.setVisible(false);
			}
		}
		userloginLabel.setValue("Welcome " + user.getUsername());
	}
}
