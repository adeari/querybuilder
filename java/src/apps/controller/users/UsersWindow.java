package apps.controller.users;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.components.ButtonCustom;
import apps.controller.queryy.QueryListWindow;
import apps.controller.queryy.UserListForQueryList;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class UsersWindow extends Window {
	private static final long serialVersionUID = 5874906714938898422L;
	private static final Logger logger = Logger.getLogger(UsersWindow.class);

	ServiceMain serviceMain;

	private Users userLogin;

	private Window singleWindow;
	private Button addButton;
	private Grid grid;
	private Label messageProcess;
	private ListModelList<Users> usersListModelList;
	private Textbox usernameSearchingTextbox;
	private Textbox divisiSearchingTextbox;

	public UsersWindow(String title) {
		super(title, null, true);
		singleWindow = this;
		serviceMain = new ServiceImplMain();

		org.zkoss.zk.ui.Session session = Sessions.getCurrent();
		userLogin = (Users) session.getAttribute("userlogin");

		Borderlayout borderlayout = new Borderlayout();
		borderlayout
				.setStyle("position:absolute; top:0; bottom:0; right:0; left:0;border-style:none;");

		North north = new North();
		north.setHeight("46px");
		north.setParent(borderlayout);
		Grid nortGrid = new Grid();
		Rows northRows = new Rows();

		Row northRow = new Row();
		Cell iconCell = new Cell();
		iconCell.setStyle("background: yellow;border: 0;width: 4%;");
		northRow.appendChild(iconCell);
		Image image = new Image("image/users.jpg");
		iconCell.appendChild(image);
		

		Cell titleCell = new Cell();
		titleCell.setParent(northRow);
		titleCell.setStyle("background: yellow;width: 93%;border: 0;");
		Label titleLabel = new Label(title);
		titleLabel.setStyle("font-weight: bold; font-size: 16px");
		titleLabel.setParent(titleCell);
		

		Cell closeCell = new Cell();
		closeCell.setStyle("background: yellow;width: 3%;border: 0;");
		Button closeButton = new Button("X");
		closeButton.setStyle("background: red; font-color: blue;");
		closeButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						detach();
					}
				});
		closeButton.setParent(closeCell);
		closeCell.setParent(northRow);

		northRow.setParent(northRows);
		northRows.setParent(nortGrid);
		nortGrid.setParent(north);

		Center center = new Center();
		center.setParent(borderlayout);
		center.setAutoscroll(true);

		Vlayout vlayout = new Vlayout();
		Div divlayout = new Div();
		addButton = new Button("Add " + title);
		addButton.setImage("image/usersadd.jpg");
		addButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) {
				if (!addButton.isDisabled()) {
					addButton.setDisabled(true);
					UsersFormWindow usersFormWindow = new UsersFormWindow(
							"Add user", null);
					usersFormWindow.setWidth("600px");
					usersFormWindow.setParent(singleWindow);
					usersFormWindow.doModal();

					messageProcess.setValue("");
					if (usersFormWindow.getEventName().equalsIgnoreCase("Add")) {
						messageProcess.setValue("User added");
						refreshGrid();
					}

					addButton.setDisabled(false);
				}
			}
		});
		divlayout.appendChild(addButton);

		messageProcess = new Label();
		messageProcess.setStyle("margin: 0 0 0 30px");
		divlayout.appendChild(messageProcess);
		vlayout.appendChild(divlayout);

		grid = new Grid();
		Columns columns = new Columns();

		Column blankColumn = new Column();
		blankColumn.setStyle("border: 0; border-style: none; border-width: 0;");
		blankColumn.setWidth("50px");
		columns.appendChild(blankColumn);
		blankColumn = new Column();
		blankColumn.setStyle("border: 0; border-style: none; border-width: 0;");
		blankColumn.setWidth("50px");
		columns.appendChild(blankColumn);
		blankColumn = new Column();
		blankColumn.setStyle("border: 0; border-style: none; border-width: 0;");
		blankColumn.setWidth("50px");
		columns.appendChild(blankColumn);

		Column userNameColumn = new Column("User Name");
		columns.appendChild(userNameColumn);
		try {
			userNameColumn.setSort("auto(username)");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		}

		Column divisiColumn = new Column("Divisi");
		try {
			divisiColumn.setSort("auto(divisi)");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		}
		columns.appendChild(divisiColumn);

		Column lastLoginColumn = new Column("Last login");
		try {
			lastLoginColumn.setSort("auto(last_login)");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(e.getMessage(), "Error", Messagebox.OK,
					Messagebox.ERROR);
		}
		columns.appendChild(lastLoginColumn);

		columns.setSizable(true);
		grid.appendChild(columns);
		
		Auxhead auxhead = new Auxhead();
		auxhead.setParent(grid);
		
		Auxheader blankAuxheader = new Auxheader();
		blankAuxheader
				.setStyle("border: 0; border-style: none; border-width: 0;");
		blankAuxheader.setParent(auxhead);
		blankAuxheader = new Auxheader();
		blankAuxheader
				.setStyle("border: 0; border-style: none; border-width: 0;");
		blankAuxheader.setParent(auxhead);
		blankAuxheader = new Auxheader();
		blankAuxheader
				.setStyle("border: 0; border-style: none; border-width: 0;");
		blankAuxheader.setParent(auxhead);
		
		Auxheader usernameAuxheader = new Auxheader();
		usernameAuxheader.setParent(auxhead);
		usernameSearchingTextbox = new Textbox();
		usernameSearchingTextbox.setParent(usernameAuxheader);
		usernameSearchingTextbox.setWidth("75%");
		usernameSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
			public void onEvent(Event namedSearchEvent) {
				if (usernameSearchingTextbox.getValue().isEmpty()) {
					refreshGrid();
				} else {
					Session sessionSelect = null;
					try {
						sessionSelect = hibernateUtil
								.getSessionFactory().openSession();
						Criteria criteria = sessionSelect
								.createCriteria(Users.class);
						criteria.add(Restrictions.like("username",
								usernameSearchingTextbox.getValue() + "%"));
						List<Users> users = criteria.list();
						usersListModelList = new ListModelList<Users>(
								users);
						grid.setModel(usersListModelList);
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
			}
		});
		Image searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(usernameAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");
		
		
		Auxheader divisiAuxheader = new Auxheader();
		divisiAuxheader.setParent(auxhead);
		divisiSearchingTextbox = new Textbox();
		divisiSearchingTextbox.setParent(divisiAuxheader);
		divisiSearchingTextbox.setWidth("75%");
		divisiSearchingTextbox.addEventListener(Events.ON_OK,
				new EventListener<Event>() {
			public void onEvent(Event namedSearchEvent) {
				if (divisiSearchingTextbox.getValue().isEmpty()) {
					refreshGrid();
				} else {
					Session sessionSelect = null;
					try {
						sessionSelect = hibernateUtil
								.getSessionFactory().openSession();
						Criteria criteria = sessionSelect
								.createCriteria(Users.class);
						criteria.add(Restrictions.like("divisi",
								divisiSearchingTextbox.getValue() + "%"));
						List<Users> users = criteria.list();
						usersListModelList = new ListModelList<Users>(
								users);
						grid.setModel(usersListModelList);
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
			}
		});
		searchImage = new Image("image/small_search_icon.png");
		searchImage.setParent(divisiAuxheader);
		searchImage.setStyle("margin: 0 0 0 6px");

		refreshGrid();

		grid.setRowRenderer(new MyRowRenderer());

		grid.setAutopaging(true);
		grid.setMold("paging");
		grid.setHeight("520px");

		vlayout.appendChild(grid);
		center.appendChild(vlayout);
		appendChild(borderlayout);
	}

	public class MyRowRenderer implements RowRenderer<Users> {
		@Override
		public void render(Row row, Users user, int index) throws Exception {
			Label divisiLabel = new Label(user.getDivisi());

			ButtonCustom updateButton = new ButtonCustom("image/icon_edit.gif",
					user);
			updateButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event event) {
							ButtonCustom updateEventButton = (ButtonCustom) event
									.getTarget();
							if (!updateEventButton.isDisabled()) {
								updateEventButton.setDisabled(true);

								Row editRow = (Row) event.getTarget()
										.getParent();
								Users userEvent = (Users) updateEventButton
										.getDataObject();

								UsersFormWindow usersFormWindow = new UsersFormWindow(
										"Edit user", userEvent);
								usersFormWindow.setWidth("600px");
								usersFormWindow.setParent(singleWindow);
								usersFormWindow.doModal();

								messageProcess.setValue("");
								if (usersFormWindow.getEventName()
										.equalsIgnoreCase("Edit")) {
									messageProcess.setValue("User editted");
									Users userFromModal = usersFormWindow
											.get_user();
									((Label) editRow
									.getChildren().get(4)).setValue(userFromModal
											.getDivisi());
								}

								updateEventButton.setDisabled(false);
							}
						}
					});

			row.appendChild(updateButton);

			ButtonCustom deleteButton = new ButtonCustom(
					"image/delete-icon.png", user);
			if (user.getId() != 1 && user.isIsdeleted()
					&& !userLogin.getId().equals(user.getId())) {
				deleteButton.addEventListener(Events.ON_CLICK,
						new EventListener<Event>() {
							public void onEvent(Event event) {
								ButtonCustom deleteEventButton = (ButtonCustom) event
										.getTarget();
								if (!deleteEventButton.isDisabled()) {
									deleteEventButton.setDisabled(true);

									if (Messagebox.show("Delete this data?",
											"Question", Messagebox.YES
													| Messagebox.NO,
											Messagebox.QUESTION) == Messagebox.YES) {
										Users userEvent = (Users) deleteEventButton
												.getDataObject();
										if (userEvent.isIsdeleted()) {
											Session session = null;
											try {
												session = hibernateUtil
														.getSessionFactory()
														.openSession();
												Transaction trx = session
														.beginTransaction();
												session.delete(userEvent);
												trx.commit();
											} catch (Exception e) {
												logger.error(e.getMessage(), e);

											} finally {
												if (session != null) {
													try {
														session.close();
													} catch (Exception e) {
														logger.error(
																e.getMessage(),
																e);
													}
												}

											}
										}

										refreshGrid();
										messageProcess
												.setValue("User deletted");
									}

									deleteEventButton.setDisabled(false);
								}
							}
						});
			} else {
				deleteButton.setDisabled(true);
			}
			row.appendChild(deleteButton);
			
			ButtonCustom gearButtonCustom = new ButtonCustom("image/gear.png",
					user);
			gearButtonCustom.setParent(row);
			gearButtonCustom.setWidth("40px");
			gearButtonCustom.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
				public void onEvent(Event gearEvent) {
					ButtonCustom buttonSelected = (ButtonCustom) gearEvent.getTarget();
					
					Users userSelected = (Users) buttonSelected.getDataObject();
					
					QueryListForUSers queryListForUSers = new QueryListForUSers("User "+userSelected.getUsername()+" properties", userSelected);
					queryListForUSers.setParent(singleWindow);
					queryListForUSers.doModal();
					
					refreshGrid();
				}
			});

			row.appendChild(new Label(user.getUsername()));
			row.appendChild(divisiLabel);
			row.appendChild(new Label(serviceMain.convertStringFromDate(
					"dd/MM/yyyy HH:mm", user.getLast_login())));
		}
	}

	public void refreshGrid() {

		Session sessionSelect = null;
		try {
			sessionSelect = hibernateUtil.getSessionFactory().openSession();
			Criteria criteria = sessionSelect.createCriteria(Users.class);
			List<Users> users = criteria.list();
			usersListModelList = new ListModelList<Users>(users);
			grid.setModel(usersListModelList);
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
}
