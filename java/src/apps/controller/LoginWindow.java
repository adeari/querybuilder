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
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
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
		if (
				Library.getProperty("org.zkoss.theme.preferred").equalsIgnoreCase("dark")) {
			vlayout2.setStyle("border-radius: 10px; text-align: center; padding: 20px; margin-top: 200px; background: #3c3c3c;");
		} else if (
					Library.getProperty("org.zkoss.theme.preferred").equalsIgnoreCase("cyan")) {
				vlayout2.setStyle("border-radius: 10px; text-align: center; padding: 20px; margin-top: 200px; background: #18cbd8;");
		} else {
			vlayout2.setStyle("border-radius: 10px; text-align: center; padding: 20px; margin-top: 200px; background: #FFF;");
		}

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

		if (serviceMain.getQuery("apps.login").equalsIgnoreCase("0")) {
			usernameTextbox.setValue("ade");
			passwordTextbox.setValue("123456");
		}
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
			if (user != null
					&& (user.getTheme() != null && (!user.getTheme().isEmpty()))) {
				Library.setProperty("org.zkoss.theme.preferred", user.getTheme());
			} else {
				Library.setProperty("org.zkoss.theme.preferred", "flatly");
			}
			Executions.sendRedirect(null);
		}
	}

	public void openLoginWindow() {
		usernameTextbox.setFocus(true);
	}
}
