package apps.controller.users;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
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
import org.zkoss.zul.Window;

import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class ChangePasswordWindow extends Window {
	private static final long serialVersionUID = -3985285325116532781L;
	private static final Logger logger = Logger
			.getLogger(ChangePasswordWindow.class);
	private ServiceMain serviceMain;

	private Label commentLabel;
	private Window changePasswordWindow;
	private Textbox passwordOldTextbox;
	private Textbox passwordTextbox;
	private Textbox rePasswordTextbox;
	private Button saveButton;

	private org.hibernate.Session _session;

	public ChangePasswordWindow(String title) {
		super(title, null, true);
		serviceMain = new ServiceImplMain();

		changePasswordWindow = this;
		Grid grid = new Grid();
		grid.setStyle("border: 0");
		Rows rows = new Rows();

		Row row = new Row();
		Cell cell = new Cell();
		cell.setColspan(2);
		cell.setAlign("center");
		commentLabel = new Label();
		commentLabel.setStyle("color: red");
		commentLabel.setVisible(false);
		cell.appendChild(commentLabel);
		row.appendChild(cell);
		rows.appendChild(row);

		Row passwordOldRow = new Row();
		Label passwordOldLabel = new Label("Old password");
		passwordOldRow.appendChild(passwordOldLabel);
		passwordOldTextbox = new Textbox();
		passwordOldTextbox.setHflex("1");
		passwordOldTextbox.setType("password");
		passwordOldRow.appendChild(passwordOldTextbox);
		passwordOldRow.setStyle("border: 0");
		rows.appendChild(passwordOldRow);

		Row passwordRow = new Row();
		Label passwordLabel = new Label("New password");
		passwordRow.appendChild(passwordLabel);
		passwordTextbox = new Textbox();
		passwordTextbox.setHflex("1");
		passwordTextbox.setType("password");
		passwordRow.appendChild(passwordTextbox);
		passwordRow.setStyle("border: 0");
		rows.appendChild(passwordRow);

		Row rePasswordRow = new Row();
		Label rePasswordLabel = new Label("Re new rePassword");
		rePasswordRow.appendChild(rePasswordLabel);
		rePasswordTextbox = new Textbox();
		rePasswordTextbox.setHflex("1");
		rePasswordTextbox.setType("password");
		rePasswordRow.appendChild(rePasswordTextbox);
		rePasswordRow.setStyle("border: 0");
		rows.appendChild(rePasswordRow);

		Row buttonRow = new Row();
		Cell buttonCell = new Cell();
		buttonCell.setStyle("text-align: center;");
		buttonCell.setColspan(2);
		saveButton = new Button("Save");
		saveButton.setImage("image/save.png");
		saveButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!saveButton.isDisabled()) {
							saveButton.setDisabled(true);

							if (passwordOldTextbox.getValue().isEmpty()) {
								commentLabel.setVisible(true);
								commentLabel.setValue("Enter old password");
								passwordOldTextbox.setFocus(true);
								return;
							}
							if (passwordTextbox.getValue().isEmpty()) {
								commentLabel.setVisible(true);
								commentLabel.setValue("Enter new password");
								passwordTextbox.setFocus(true);
								return;
							}
							if (rePasswordTextbox.getValue().isEmpty()) {
								commentLabel.setVisible(true);
								commentLabel.setValue("Enter re new password");
								rePasswordTextbox.setFocus(true);
								return;
							}

							if (!rePasswordTextbox.getValue().equalsIgnoreCase(
									passwordTextbox.getValue())) {
								commentLabel.setVisible(true);
								commentLabel
										.setValue("New password is not same");
								rePasswordTextbox.setFocus(true);
								return;
							}
							Session sessionZK = Sessions.getCurrent();
							Users user = (Users) sessionZK
									.getAttribute("userlogin");

							if (!serviceMain.convertPass(
									passwordOldTextbox.getValue()).equals(
									user.getPass())) {
								commentLabel.setVisible(true);
								commentLabel.setValue("Wrong old password");
								passwordOldTextbox.setFocus(true);
								return;
							}

							try {
								_session = hibernateUtil
										.getSessionFactory(_session);

								Criteria citeria = _session
										.createCriteria(Users.class);
								citeria.add(Restrictions.eq("id", user.getId()));

								Users userUpdate = (Users) citeria
										.uniqueResult();

								userUpdate.setPass(serviceMain
										.convertPass(passwordTextbox.getValue()));
								_session.update(userUpdate);
								_session.flush();
								serviceMain.saveUserActivity(_session,
										"Change password");
								detach();
							} catch (Exception e) {
								logger.error(e.getMessage(), e);

							}

							saveButton.setDisabled(false);
						}
					}
				});
		buttonCell.appendChild(saveButton);
		Button cancelButton = new Button("Cancel");
		cancelButton.setImage("image/cancel.png");
		cancelButton.setStyle("margin: 0 0 0 20px");
		cancelButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						detach();
					}
				});
		buttonRow.setStyle("border: 0");
		buttonCell.appendChild(cancelButton);
		buttonRow.appendChild(buttonCell);

		rows.appendChild(buttonRow);

		grid.appendChild(rows);
		changePasswordWindow.appendChild(grid);
	}

}
