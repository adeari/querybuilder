package apps.controller;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.entity.Users;
import apps.service.ServiceImplMain;

public class LoginWindow extends Vlayout {
	private static final long serialVersionUID = -6233909855353185900L;

	private ServiceImplMain serviceMain;

	private Label commentLabel;
	private Textbox usernameTextbox;
	private Textbox passwordTextbox;
	private Button loginButton;

	private Window mainWindow;

	public LoginWindow(Window windowMain) {
		mainWindow = windowMain;

		serviceMain = new ServiceImplMain();
		Vlayout vlayout = this;
		vlayout.setWidth("350px");
		vlayout.setStyle("margin:0 auto; text-align: center;");

		Vlayout vlayout2 = new Vlayout();
		vlayout2.setParent(vlayout);
		vlayout2.setWidth("350px");
		vlayout2.setClass("clear");
		

		Label titleLabel = new Label("L O G I N");
		titleLabel.setParent(vlayout2);
		titleLabel.setStyle("font-weight: bold; font-size: 27px;");

		commentLabel = new Label();
		commentLabel.setStyle("color: red;");
		commentLabel.setVisible(false);
		commentLabel.setParent(vlayout2);

		Grid grid = new Grid();
		grid.setStyle("border: 0;");

		Rows rows = new Rows();

		Row userRow = new Row();
		userRow.setStyle("text-align: left");
		Cell usernameLabelCell = new Cell();
		Label usernameLabel = new Label("User name");
		usernameLabelCell.setWidth("100px");
		usernameLabelCell.appendChild(usernameLabel);
		userRow.appendChild(usernameLabelCell);
		usernameTextbox = new Textbox();
		usernameTextbox.setWidth("200px");
		usernameTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						passwordTextbox.setFocus(true);
					}
				});
		userRow.appendChild(usernameTextbox);
		userRow.setStyle("border: 0;text-align: left;");
		rows.appendChild(userRow);

		Row passwordRow = new Row();
		Cell passwordLabelCell = new Cell();
		Label passwordLabel = new Label("Password");
		passwordLabelCell.setWidth("100px");
		passwordLabelCell.appendChild(passwordLabel);
		passwordRow.appendChild(passwordLabelCell);
		passwordTextbox = new Textbox();
		passwordTextbox.setWidth("200px");
		passwordTextbox.setType("password");
		passwordTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!passwordTextbox.isDisabled()) {
							passwordTextbox.setDisabled(true);
							doLogin();
							passwordTextbox.setDisabled(false);
						}
					}
				});

		passwordRow.appendChild(passwordTextbox);
		passwordRow.setStyle("border: 0;text-align: left;");
		rows.appendChild(passwordRow);

		Row themeRow = new Row();
		themeRow.setParent(rows);
		themeRow.setStyle("border: 0;text-align: left;");
		Cell themeCell = new Cell();
		themeCell.setParent(themeRow);
		themeCell.setWidth("100px");
		Label themeLabel = new Label("Theme");
		themeLabel.setParent(themeCell);
		Selectbox themeSelectbox = new Selectbox();
		themeSelectbox.setParent(themeRow);
		themeSelectbox.setWidth("200px");
		String[] themeList = new String[] {  "Atlantic", "Bootstrap",
				"Breeze", "Cerulean", "Cosmo", "Cyan", "Dark", "Flatly", "Journal",
				"Sapphire", "Silvertail" };
		ListModelList<String> themeListModelList = new ListModelList<String>(themeList);
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
						Executions.sendRedirect(null);
					}

				});
		String themeUsed = Library.getProperty("org.zkoss.theme.preferred");
		if (themeUsed.equalsIgnoreCase("dark")) {
			vlayout2.setStyle("border-radius: 10px; text-align: center; padding: 20px; margin-top: 200px; background: #060606;");
		}	else if (themeUsed.equalsIgnoreCase("cyan")) {
			vlayout2.setStyle("border-radius: 10px; text-align: center; padding: 20px; margin-top: 200px; background: #108a93;");
		} else {
			vlayout2.setStyle("border-radius: 10px; text-align: center; padding: 20px; margin-top: 200px; background: #FFF;");
		}
		themeUsed = themeUsed.substring(0,1).toUpperCase() + themeUsed.substring(1);
		themeListModelList.addToSelection(themeUsed);

		grid.appendChild(rows);
		grid.setParent(vlayout2);

		loginButton = new Button("Login");
		loginButton.setStyle("backgound: green;");
		loginButton.setImage("image/login.png");
		loginButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!loginButton.isDisabled()) {
							loginButton.setDisabled(true);
							doLogin();
							loginButton.setDisabled(false);
						}
					}
				});
		loginButton.setParent(vlayout2);

	}

	private void doLogin() {
		boolean canLogin = true;

		if (canLogin && usernameTextbox.getValue().isEmpty()) {
			commentLabel.setVisible(true);
			commentLabel.setValue("Enter username");
			usernameTextbox.setFocus(true);
			canLogin = false;
		}

		if (canLogin && passwordTextbox.getValue().isEmpty()) {
			commentLabel.setVisible(true);
			commentLabel.setValue("Enter password");
			passwordTextbox.setFocus(true);
			canLogin = false;
		}

		Users user = serviceMain.get1UserByUsernameAndPassword(
				usernameTextbox.getValue(), passwordTextbox.getValue());
		if (canLogin && user == null) {
			commentLabel.setVisible(true);
			commentLabel.setValue("Wrong user name or password");
			usernameTextbox.setFocus(true);
			canLogin = false;
		}

		if (canLogin) {
			Session session = Sessions.getCurrent();
			session.setAttribute("userlogin", user);
			commentLabel.setVisible(false);
			passwordTextbox.setValue("");
			usernameTextbox.setValue("");

			MenuWindow menuWindow = (MenuWindow) mainWindow.getChildren()
					.get(1);
			menuWindow.settingForLoginUser();
			menuWindow.setVisible(true);

			LoginWindow loginWindow = (LoginWindow) mainWindow.getChildren()
					.get(0);
			loginWindow.setVisible(false);

		}
	}

	public void openLoginWindow() {
		usernameTextbox.setFocus(true);
	}
}
