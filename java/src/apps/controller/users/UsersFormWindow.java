package apps.controller.users;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ItemRenderer;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class UsersFormWindow extends Window {
	private static final long serialVersionUID = -7546871546233729815L;
	private static final Logger logger = Logger
			.getLogger(UsersFormWindow.class);

	private Users _user;
	private ListModelList<String> _optionsListModelList;
	private String _eventName;

	private Button saveButton;
	private Textbox usernameTextbox;
	private Selectbox divisiSelectbox;
	private Textbox password2Textbox;
	private Textbox passwordTextbox;
	private Label commentLabel;
	private Div div;

	private ServiceMain servviceMain;

	public UsersFormWindow(String title, Users user) {
		super(title, null, true);
		_eventName = "";

		servviceMain = new ServiceImplMain();
		
		div = new Div();
		div.setParent(this);
		div.setVisible(false);
		div.setStyle("width: 100%; text-align: center;height: 30px;");
		commentLabel = new Label();
		commentLabel.setParent(div);
		commentLabel.setStyle("color: red;");
		
		Grid grid = new Grid();

		Rows rows = new Rows();

		
		Row row = new Row();
		row.setParent(rows);
		Cell labelCell = new Cell();
		labelCell.setStyle("width: 130px");
		labelCell.setParent(row);
		Label usernameLabel = new Label("User name");
		usernameLabel.setParent(labelCell);
		Cell insertCell = new Cell();
		insertCell.setParent(row);
		usernameTextbox = new Textbox();
		usernameTextbox.setParent(insertCell);

		
		
		row = new Row();
		Label divisiLabel = new Label("Divisi");
		divisiLabel.setWidth("90px");
		row.appendChild(divisiLabel);
		String[] options = new String[] { "Operator", "Admin" };
		_optionsListModelList = new ListModelList<String>(options);
		if (user == null) {
			_optionsListModelList.addToSelection(options[0]);
		}
		ListModel<String> optionListModel = _optionsListModelList;
		divisiSelectbox = new Selectbox();
		divisiSelectbox.setModel(optionListModel);
		divisiSelectbox.setItemRenderer(new OptionsRenderer());
		divisiSelectbox.setWidth("190px");

		row.appendChild(divisiSelectbox);
		divisiSelectbox.setSelectedIndex(1);

		rows.appendChild(row);

		row = new Row();
		Label passwordLabel = new Label("Password");
		passwordLabel.setWidth("90px");
		row.appendChild(passwordLabel);
		passwordTextbox = new Textbox();
		passwordTextbox.setType("password");
		row.appendChild(passwordTextbox);
		rows.appendChild(row);

		row = new Row();
		Label password2Label = new Label("Re Password");
		password2Label.setWidth("90px");
		row.appendChild(password2Label);
		password2Textbox = new Textbox();
		password2Textbox.setType("password");
		row.appendChild(password2Textbox);
		rows.appendChild(row);

		if (user != null) {
			_user = user;
			usernameTextbox.setValue(_user.getUsername());
			usernameTextbox.setReadonly(true);

			for (int i = 0; i < divisiSelectbox.getModel().getSize(); i++) {

				if (_user.getDivisi().equalsIgnoreCase(
						divisiSelectbox.getModel().getElementAt(i).toString())) {
					_optionsListModelList.addToSelection(options[i]);
					break;
				}
			}
		}
		
		row = new Row();
		Cell cell = new Cell();
		cell.setColspan(2);
		cell.setAlign("center");
		saveButton = new Button("Save");
		saveButton.setImage("image/save.png");
		saveButton.setStyle("margin: 0 20px 0 0;backgound: green;");
		saveButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!saveButton.isDisabled()) {
							saveButton.setDisabled(true);

							boolean canSAve = true;
							if (canSAve && usernameTextbox.getValue().isEmpty()) {
								commentLabel.setValue("Enter user name");
								div.setVisible(true);
								usernameTextbox.setFocus(true);
								canSAve = false;
							}

							if (canSAve
									&& !usernameTextbox.getValue().isEmpty()) {

								Session sessionSelect = null;
								try {
									sessionSelect = hibernateUtil
											.getSessionFactory().openSession();
									Criteria criteria = sessionSelect
											.createCriteria(Users.class);
									criteria.add(Restrictions.eq("username",
											usernameTextbox.getValue()));
									if (_user != null) {
										criteria.add(Restrictions.ne("id",
												_user.getId()));
									}
									if (criteria.list().size() > 0) {
										commentLabel
												.setValue("This username already exist");
										div.setVisible(true);
										usernameTextbox.setFocus(true);
										canSAve = false;
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

							if (_user == null) {
								if (canSAve
										&& passwordTextbox.getValue().isEmpty()) {
									commentLabel.setValue("Enter password");
									div.setVisible(true);
									passwordTextbox.setFocus(true);
									canSAve = false;
								}

								if (canSAve
										&& password2Textbox.getValue()
												.isEmpty()) {
									commentLabel.setValue("Enter re password");
									div.setVisible(true);
									password2Textbox.setFocus(true);
									canSAve = false;
								}
							}

							if (canSAve
									&& (passwordTextbox.getValue().length() > 0 || password2Textbox
											.getValue().length() > 0)) {
								if (canSAve
										&& passwordTextbox.getValue().length() < 6) {
									commentLabel
											.setValue("Enter more then 5 characters");
									div.setVisible(true);
									passwordTextbox.setFocus(true);
									canSAve = false;
								}

								if (canSAve
										&& !password2Textbox.getValue().equals(
												passwordTextbox.getValue())) {
									commentLabel.setValue("password not same");
									div.setVisible(true);
									password2Textbox.setFocus(true);
									canSAve = false;
								}
							}

							if (canSAve) {
								Session session = hibernateUtil
										.getSessionFactory().openSession();
								if (_user == null) {
									Transaction trx = session
											.beginTransaction();
									Users tbUsers = new Users(
											usernameTextbox.getValue(),
											servviceMain
													.convertPass(password2Textbox
															.getValue()),
											divisiSelectbox
													.getModel()
													.getElementAt(
															divisiSelectbox
																	.getSelectedIndex())
													.toString(), true);

									session.save(tbUsers);
									trx.commit();
									_eventName = "Add";
								} else {
									Criteria criteria = session
											.createCriteria(Users.class);
									criteria.add(Restrictions.eq("id",
											_user.getId()));
									Transaction trx = session
											.beginTransaction();
									Users tbUsers = (Users) criteria
											.uniqueResult();
									if (!password2Textbox.getValue().isEmpty()) {
										tbUsers.setPass(servviceMain
												.convertPass(password2Textbox
														.getValue()));
									}
									tbUsers.setDivisi(divisiSelectbox
											.getModel()
											.getElementAt(
													divisiSelectbox
															.getSelectedIndex())
											.toString());
									_user.setDivisi(tbUsers.getDivisi());
									session.update(tbUsers);
									trx.commit();
									_eventName = "Edit";
								}
								session.close();
								detach();
							}

							saveButton.setDisabled(false);

						}
					}
				});
		cell.appendChild(saveButton);
		Button cancelButton = new Button("Cancel");
		cancelButton.setImage("image/cancel.png");
		cancelButton.setStyle("backgound: yellow");
		cancelButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						detach();
					}
				});
		cell.appendChild(cancelButton);
		row.appendChild(cell);
		rows.appendChild(row);

		grid.appendChild(rows);
		appendChild(grid);
	}

	public class OptionsRenderer implements ItemRenderer<Object> {

		@Override
		public String render(Component owner, Object data, int index)
				throws Exception {
			return data.toString();
		}

	}

	public Users get_user() {
		return _user;
	}

	public String getEventName() {
		return _eventName;
	}

}
