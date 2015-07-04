package apps.controller.users;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class ProfileWindow extends Window {
	private static final long serialVersionUID = 1500061430000755956L;
	private static final Logger logger = Logger.getLogger(ProfileWindow.class);
	private Window window;
	private Users _user;

	private Textbox emailTextbox;

	public ProfileWindow(String title, Users user) {
		super(title, null, true);
		window = this;
		_user = user;

		Vlayout vlayout = new Vlayout();
		vlayout.setParent(window);

		Grid grid = new Grid();
		grid.setParent(vlayout);
		grid.setWidth("400px");
		grid.setSizedByContent(true);
		grid.setStyle("border: 0");

		Rows rows = new Rows();
		rows.setParent(grid);
		rows.setStyle("border: 0");

		Row emailRow = new Row();
		emailRow.setParent(rows);
		emailRow.setStyle("border: 0");
		Label emailLabel = new Label("Email");
		emailLabel.setParent(emailRow);
		emailLabel.setStyle("border: 0; width: 100px;");
		emailTextbox = new Textbox();
		if (_user.getEmail() != null && (!_user.getEmail().isEmpty())) {
			emailTextbox.setValue(_user.getEmail());
		}
		emailTextbox.setParent(emailRow);
		emailTextbox.setStyle("border: 0; width: 300px;");

		Row buttonRow = new Row();
		buttonRow.setParent(rows);
		buttonRow.setStyle("border: 0");
		Cell buttonCell = new Cell();
		buttonCell.setParent(buttonRow);
		buttonCell.setColspan(2);
		buttonCell.setStyle("text-align: center; border: 0");
		Button saveButton = new Button("Save");
		saveButton.setParent(buttonCell);
		saveButton.setStyle("margin: 0 15px 0 0;");
		saveButton.addEventListener("onClick", new EventListener<Event>() {
			public void onEvent(Event saveEvent) {

				boolean canSaved = true;

				if (canSaved && emailTextbox.getValue().isEmpty()) {
					canSaved = false;
					throw new WrongValueException(emailTextbox, "Enter email");
				}

				if (canSaved) {
					Session querySession = null;
					long emailCount = 0;
					try {
						querySession = hibernateUtil.getSessionFactory()
								.openSession();

						Criteria criteria = querySession.createCriteria(
								Users.class).setProjection(
								Projections.rowCount());
						criteria.add(Restrictions.eq("email",
								emailTextbox.getValue()));
						criteria.add(Restrictions.ne("id", _user.getId()));
						emailCount = (long) criteria.uniqueResult();
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					} finally {
						if (querySession != null) {
							try {
								querySession.close();
							} catch (Exception e) {
							}
						}

					}
					if (emailCount > 0) {
						canSaved = false;
						throw new WrongValueException(emailTextbox,
								"Email already exist");
					}
				}

				if (canSaved) {
					Session querySession = null;
					try {
						querySession = hibernateUtil.getSessionFactory()
								.openSession();
						Transaction trx = querySession.beginTransaction();
						_user.setEmail(emailTextbox.getValue());
						querySession.update(_user);
						trx.commit();
						ServiceMain serviceMain = new ServiceImplMain();
						serviceMain.saveUserActivity("Profile changed");
						detach();
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					} finally {
						if (querySession != null) {
							try {
								querySession.close();
							} catch (Exception e) {
							}
						}

					}

				}
			}
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setParent(buttonCell);
		cancelButton.addEventListener("onClick", new EventListener<Event>() {
			public void onEvent(Event cancelEvent) {
				detach();
			}
		});

	}

}
