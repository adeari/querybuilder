package apps.controller;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.entity.Users;
import apps.service.ServiceImplMain;

public class LoginWindow extends Window {
	private static final long serialVersionUID = -6233909855353185900L;

	private ServiceImplMain serviceMain;

	private Label commentLabel;
	private Textbox usernameTextbox;
	private Textbox passwordTextbox;
	private Button loginButton;

	private Window loginWindow;
	private Window mainWindow;

	public LoginWindow(Window windowMain) {
		super(null, null, false);
		loginWindow = this;
		mainWindow = windowMain;

		serviceMain = new ServiceImplMain();
		Vlayout vlayout = new Vlayout();
		vlayout.setWidth("350px");
		vlayout.setStyle("margin:0 auto; border: 0; margin-top:100px;text-align: center; background: white; padding: 20px;");
		Label titleLabel = new Label("L O G I N");
		titleLabel.setStyle("font-weight: bold; font-size: 27px;");
		vlayout.appendChild(titleLabel);

		commentLabel = new Label();
		commentLabel.setStyle("color: red;");
		commentLabel.setVisible(false);
		vlayout.appendChild(commentLabel);

		Grid grid = new Grid();
		grid.setStyle("border: 0;");

		Rows rows = new Rows();

		Row userRow = new Row();
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
		userRow.setStyle("border: 0; background: transparent; ");
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
		passwordRow.setStyle("border: 0; background: transparent;");
		rows.appendChild(passwordRow);

		grid.appendChild(rows);
		vlayout.appendChild(grid);

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
		vlayout.appendChild(loginButton);

		loginWindow.appendChild(vlayout);

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
