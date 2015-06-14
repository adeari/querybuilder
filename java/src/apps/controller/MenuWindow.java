package apps.controller;

import org.apache.log4j.Logger;
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

import apps.controller.users.UsersWindow;
import apps.entity.Users;

	public class MenuWindow extends Window {
	private static final long serialVersionUID = -2091055007101580190L;
	private static final Logger logger = Logger.getLogger(MenuWindow.class);
	
	private Window menuWindow;
	private Menuitem queryBuilderMenu;
	private Menuitem usersMenu;
	private Menuitem logoutMenu;
	private Window mainWindow;
	private Label userloginLabel;
	
	public MenuWindow(Window windowMain) {
		super(null, null, false);
		mainWindow = windowMain;
		menuWindow= this;
		
		Vlayout vlayout = new Vlayout();
		vlayout.setStyle("position:absolute; top:0; bottom:0; right:0; left:0;border-style:none;");
		
		Menubar mainMenubar = new Menubar();
		Menu mainMenu = new Menu("Main");
		
		Menupopup mainMenupopup = new Menupopup();
		
		queryBuilderMenu = new Menuitem("Query builder");
		queryBuilderMenu.setVisible(false);
		queryBuilderMenu.setParent(mainMenupopup);
		queryBuilderMenu.setImage("image/database-icon.png");
		queryBuilderMenu.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
		    public void onEvent(Event event) {
		    	if (!queryBuilderMenu.isDisabled()) {
		    		queryBuilderMenu.setDisabled(true);
			    	QueryWindows queryWindows = new QueryWindows("Query Builder");
			    	queryWindows.setParent(menuWindow);
			    	queryWindows.setStyle("position:absolute;top:0; bottom:0; right:0; left:0;border-style:none");
			    	queryWindows.doModal();
			    	queryBuilderMenu.setDisabled(false);
		    	}
		    }
		});
		
		usersMenu = new Menuitem("Users");
		usersMenu.setParent(mainMenupopup);
		usersMenu.setVisible(false);
		usersMenu.setImage("image/users.jpg");
		usersMenu.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) {
				if (!usersMenu.isDisabled()) {
					usersMenu.setDisabled(true);
					UsersWindow usersWindows = new UsersWindow("Users");
					usersWindows.setParent(menuWindow);
					usersWindows.setStyle("position:absolute;top:0; bottom:0; right:0; left:0;border-style:none");
					usersWindows.doModal();
					usersMenu.setDisabled(false);
				}
			}
		});
		
		logoutMenu = new Menuitem("Logout");
		logoutMenu.setParent(mainMenupopup);
		logoutMenu.setImage("image/logout.jpg");
		logoutMenu.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) {
				if (!logoutMenu.isDisabled()) {
					logoutMenu.setDisabled(true);
					
					Session session = Sessions.getCurrent();
		    		session.removeAttribute("userlogin");
					
					MenuWindow menuWindow =  (MenuWindow) mainWindow.getChildren().get(1);
					menuWindow.setVisible(false);
					
					LoginWindow loginWindow =  (LoginWindow) mainWindow.getChildren().get(0);
		    		loginWindow.setVisible(true);
		    		
		    		loginWindow.openLoginWindow();
					
					logoutMenu.setDisabled(false);
				}
			}
		});
		
		mainMenupopup.setParent(mainMenu);
		mainMenu.setParent(mainMenubar);
		mainMenubar.setStyle("position:absolute; right:0; left:0;");
		
				
		vlayout.appendChild(mainMenubar);
		userloginLabel = new Label();
		userloginLabel.setStyle("color: blue; background: white; "
				+ "font-weight: bold; font-size : 25px;float: right; margin: 0 20px 0 0; padding: 2px 20px 2px 20px;");
		vlayout.appendChild(userloginLabel);
		menuWindow.appendChild(vlayout);
	}
	
	public void settingForLoginUser() {
		Session session = Sessions.getCurrent();
		Users user = (Users) session.getAttribute("userlogin");
		if (user.getDivisi().equalsIgnoreCase("Admin")) {
			usersMenu.setVisible(true);
			queryBuilderMenu.setVisible(true);
		} else {
			usersMenu.setVisible(false);
			queryBuilderMenu.setVisible(false);
		}
		userloginLabel.setValue("Welcome "+user.getUsername());
	}
}
