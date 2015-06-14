package apps.controller.users;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class UsersWindow extends Window {
	private static final long serialVersionUID = 5874906714938898422L;

	ServiceMain serviceMain;

	private Window singleWindow;

	private Button addButton;
	private Grid grid;
	private ListModelList<Users> usersListModelList;

	public UsersWindow(String title) {
		super(title, null, true);
		singleWindow = this;
		serviceMain = new ServiceImplMain();

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
		Image image = new Image("image/users.jpg");
		iconCell.appendChild(image);
		iconCell.setStyle("background: yellow");
		northRow.appendChild(iconCell);

		Cell titleCell = new Cell();
		Label titleLabel = new Label(title);
		titleLabel.setStyle("font-weight: bold; font-size: 16px");
		titleLabel.setParent(titleCell);
		titleCell.setWidth("92%");
		titleCell.setParent(northRow);
		titleCell.setStyle("background: yellow");

		Cell closeCell = new Cell();
		closeCell.setStyle("background: yellow");
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

					refreshGrid();

					addButton.setDisabled(false);
				}
			}
		});
		vlayout.appendChild(addButton);

		grid = new Grid();
		Columns columns = new Columns();

		Column blankColumn = new Column();
		blankColumn.setWidth("50px");
		columns.appendChild(blankColumn);
		blankColumn = new Column();
		blankColumn.setWidth("50px");
		columns.appendChild(blankColumn);

		Column userNameColumn = new Column("User Name");
		columns.appendChild(userNameColumn);

		Column divisiColumn = new Column("Divisi");
		columns.appendChild(divisiColumn);

		Column lastLoginColumn = new Column("Last login");
		columns.appendChild(lastLoginColumn);

		grid.appendChild(columns);

		refreshGrid();

		grid.setRowRenderer(new MyRowRenderer());
		grid.setAutopaging(true);
		grid.setMold("paging");
		grid.setHeight("520px");

		vlayout.appendChild(grid);
		center.appendChild(vlayout);
		appendChild(borderlayout);
	}

	public class MyRowRenderer implements RowRenderer<Object> {
		@Override
		public void render(Row row, Object data, int index) throws Exception {
			Users user = (Users) data;
			final int indexEvent = index;

			Label divisiLabel = new Label(user.getDivisi());

			Button updateButton = new Button();
			updateButton.setImage("image/icon_edit.gif");
			updateButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event event) {
							Button updateEventButton = (Button) event
									.getTarget();
							if (!updateEventButton.isDisabled()) {
								updateEventButton.setDisabled(true);

								Row editRow = (Row) event.getTarget()
										.getParent();
								Label divisiEventLabel = (Label) editRow
										.getChildren().get(3);
								int indexEventNow = indexEvent;
								if (usersListModelList.getSize() == indexEvent) {
									indexEventNow--;
								}
								Users userEvent = (Users) usersListModelList.get(indexEventNow);

								UsersFormWindow usersFormWindow = new UsersFormWindow(
										"Edit user", userEvent);
								usersFormWindow.setWidth("600px");
								usersFormWindow.setParent(singleWindow);
								usersFormWindow.doModal();

								Users userFromModal = usersFormWindow
										.get_user();
								divisiEventLabel.setValue(userFromModal
										.getDivisi());

								updateEventButton.setDisabled(false);
							}
						}
					});

			row.appendChild(updateButton);

			Button deleteButton = new Button("");
			deleteButton.setImage("image/delete-icon.png");
			deleteButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event event) {
							Button deleteEventButton = (Button) event
									.getTarget();
							if (!deleteEventButton.isDisabled()) {
								deleteEventButton.setDisabled(true);

								if (Messagebox.show("Delete this data?",
										"Question", Messagebox.YES
												| Messagebox.NO,
										Messagebox.QUESTION) == Messagebox.YES) {

									Row editRow = (Row) event.getTarget()
											.getParent();
									int indexEventNow = indexEvent;
									if (usersListModelList.getSize() == indexEvent) {
										indexEventNow--;
									}
									Users userEvent = (Users) usersListModelList.get(indexEventNow);

									Session session = hibernateUtil
											.getSessionFactory().openSession();
									Transaction trx = session
											.beginTransaction();
									Criteria criteria = session
											.createCriteria(Users.class);
									criteria.add(Restrictions.eq("id",
											userEvent.getId()));
									Users tbUsers = (Users) criteria
											.uniqueResult();
									session.delete(tbUsers);
									trx.commit();
									session.close();

									usersListModelList.remove(indexEventNow);
								}

								deleteEventButton.setDisabled(false);
							}
						}
					});
			row.appendChild(deleteButton);

			row.appendChild(new Label(user.getUsername()));
			row.appendChild(divisiLabel);
			row.appendChild(new Label(serviceMain.convertStringFromDate(
					"dd/MM/yyyy HH:mm", user.getLast_login())));
		}
	}

	public void refreshGrid() {
		Session sessionSelect = hibernateUtil.getSessionFactory().openSession();
		Criteria criteria = sessionSelect.createCriteria(Users.class);
		List<Users> users = criteria.list();
		usersListModelList = new ListModelList<Users>(users);
		grid.setModel(usersListModelList);
		sessionSelect.close();
	}
}
