package apps.controller;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
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

import apps.entity.QueryData;
import apps.entity.Users;
import apps.service.hibernateUtil;

public class QuerySavedWindow extends Window {
	private static final long serialVersionUID = 3619446823275550491L;

	private String _driverName;
	private String _url;
	private String _sql;
	private Window queryWindow;
	private Textbox tableNameTextbox;
	private Label commentLabel;
	private Button saveButton;

	public QuerySavedWindow(String title, String driverName, String url,
			String sql) {
		super(title, null, true);
		_driverName = driverName;
		_url = url;
		_sql = sql;
		queryWindow = this;
		queryWindow.setStyle("background: white");
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

		Row tableNameRow = new Row();
		Label tableNameLabel = new Label("Query name");
		tableNameRow.appendChild(tableNameLabel);
		tableNameTextbox = new Textbox();
		tableNameRow.appendChild(tableNameTextbox);
		tableNameRow.setStyle("border: 0");
		rows.appendChild(tableNameRow);

		Row buttonRow = new Row();
		Cell buttonCell = new Cell();
		buttonCell.setStyle("text-align: center;");
		buttonCell.setColspan(2);
		saveButton = new Button("Save");
		saveButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) {
						if (!saveButton.isDisabled()) {
							saveButton.setDisabled(true);

							boolean canSave = true;
							if (canSave
									&& tableNameTextbox.getValue().isEmpty()) {
								commentLabel.setVisible(true);
								commentLabel.setValue("Enter table name");
								tableNameTextbox.setFocus(true);
								canSave = false;
							}

							if (canSave) {
								org.hibernate.Session session = hibernateUtil
										.getSessionFactory().openSession();

								if (canSave) {
									Criteria criteria = session
											.createCriteria(QueryData.class);
									criteria.add(Restrictions.eq("named",
											tableNameTextbox.getValue()));
									if (criteria.list().size() > 0) {
										commentLabel.setVisible(true);
										commentLabel
												.setValue("Table name already exist");
										tableNameTextbox.setFocus(true);
										tableNameTextbox.select();
										canSave = false;
									}
								}
								

								if (canSave) {
									Transaction trx = session
											.beginTransaction();

									org.zkoss.zk.ui.Session sessionLocal = Sessions
											.getCurrent();
									Users user = (Users) sessionLocal
											.getAttribute("userlogin");

									QueryData queryData = new QueryData(
											_driverName, _url, tableNameTextbox
													.getValue(), _sql, user,
											user, new Date(), new Date());

									session.save(queryData);

									trx.commit();
									session.close();
									detach();
								}
							}

							saveButton.setDisabled(false);
						}
					}
				});
		buttonCell.appendChild(saveButton);
		Button cancelButton = new Button("Cancel");
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
		queryWindow.appendChild(grid);

	}
}
