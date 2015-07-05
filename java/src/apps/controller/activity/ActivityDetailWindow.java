package apps.controller.activity;

import java.io.File;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import apps.beans.EmailObject;
import apps.entity.Activity;
import apps.entity.FilesData;
import apps.service.EmailService;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class ActivityDetailWindow extends Window {
	private static final long serialVersionUID = 6685464214209516187L;
	private static final Logger logger = Logger
			.getLogger(ActivityDetailWindow.class);
	private ServiceMain serviceMain;

	private Activity _activity;

	public boolean refreshActivity;

	private Window window;
	private Textbox descriptionTextbox;
	private Textbox emailToTextbox;

	public ActivityDetailWindow(Activity activity) {
		super("Query activity detail management", null, true);
		serviceMain = new ServiceImplMain();
		refreshActivity = false;

		window = this;
		_activity = activity;

		Hbox hbox = new Hbox();
		hbox.setParent(window);

		Grid grid = new Grid();
		grid.setParent(hbox);
		grid.setWidth("400px");

		Rows rows = new Rows();
		rows.setParent(grid);

		Row queryNameRow = new Row();
		queryNameRow.setParent(rows);
		Cell queryNameCell = new Cell();
		queryNameCell.setParent(queryNameRow);
		queryNameCell.setWidth("120px");
		Label queryNameLabel = new Label("Query name");
		queryNameLabel.setParent(queryNameCell);
		Textbox queryNameTextbox = new Textbox(activity.getQueryName());
		queryNameTextbox.setParent(queryNameRow);
		queryNameTextbox.setStyle("width: 100%;");
		queryNameTextbox.setReadonly(true);

		Row queryRow = new Row();
		queryRow.setParent(rows);
		Cell queryCell = new Cell();
		queryCell.setParent(queryRow);
		queryCell.setStyle("vertical-align: top");
		Label queryLabel = new Label("Query");
		queryLabel.setParent(queryCell);
		Textbox queryTextbox = new Textbox(activity.getQuery());
		queryTextbox.setParent(queryRow);
		queryTextbox.setStyle("width: 100%;");
		queryTextbox.setRows(4);
		queryTextbox.setReadonly(true);

		Foot foot = new Foot();
		foot.setParent(grid);
		Footer footer = new Footer();
		footer.setParent(foot);
		Cell footerCell = new Cell();
		footerCell.setParent(footer);
		footerCell.setStyle("text-align: center");
		footerCell.setColspan(2);
		Button deleteButton = new Button("Delete");
		deleteButton.setParent(footerCell);
		deleteButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event linkDownloadTextboxEvent) {
						if (Messagebox.show("Delete this activity?",
								"Question", Messagebox.YES | Messagebox.NO,
								Messagebox.QUESTION) == Messagebox.YES) {
							refreshActivity = true;
							org.hibernate.Session querySession = null;
							try {
								querySession = hibernateUtil
										.getSessionFactory().openSession();

								if (_activity.getFileData() != null) {
									FilesData filesData = _activity
											.getFileData();
									File file = new File(serviceMain
											.getQuery("location."
													+ filesData.getFiletype()
															.toLowerCase())
											+ "/" + filesData.getFilename());
									if (file.isFile()) {
										file.delete();
									}
									Transaction trx = querySession
											.beginTransaction();
									querySession.delete(filesData);
									trx.commit();
								}
								Transaction trx = querySession
										.beginTransaction();
								querySession.delete(_activity);
								trx.commit();

							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							} finally {
								if (querySession != null) {
									try {
										querySession.close();
									} catch (Exception e) {
										logger.error(e.getMessage(), e);
									}
								}
							}
							detach();
						}
					}
				});

		if (_activity.getFileData() != null) {
			Grid sendEmailGrid = new Grid();
			sendEmailGrid.setParent(hbox);
			sendEmailGrid.setWidth("400px");

			Rows sendEmailRows = new Rows();
			sendEmailRows.setParent(sendEmailGrid);

			Row linkDownloadRow = new Row();
			linkDownloadRow.setParent(sendEmailRows);
			Cell linkDownloadCell = new Cell();
			linkDownloadCell.setParent(linkDownloadRow);
			linkDownloadCell.setWidth("150px");
			linkDownloadCell.setStyle("vertical-align: top");
			Label linkDownloadLabel = new Label("Download link for copy");
			linkDownloadLabel.setParent(linkDownloadCell);

			String port = (Executions.getCurrent().getServerPort() == 80) ? ""
					: (":" + Executions.getCurrent().getServerPort());
			String url = Executions.getCurrent().getScheme() + "://"
					+ Executions.getCurrent().getServerName() + port
					+ Executions.getCurrent().getContextPath()
					+ Executions.getCurrent().getDesktop().getRequestPath();

			Cell linkDownloadCellTextbox = new Cell();
			linkDownloadCellTextbox.setParent(linkDownloadRow);
			Textbox linkDownloadTextbox = new Textbox(url + "/download/file/"
					+ _activity.getFileData().getDownloadLink());
			linkDownloadTextbox.setParent(linkDownloadCellTextbox);
			linkDownloadTextbox.setStyle("width: 80%;");
			linkDownloadTextbox.setReadonly(true);
			linkDownloadTextbox.addEventListener(Events.ON_FOCUS,
					new EventListener<Event>() {
						public void onEvent(Event linkDownloadTextboxEvent) {
							Textbox textbox = (Textbox) linkDownloadTextboxEvent
									.getTarget();
							textbox.select();
						}
					});
			linkDownloadTextbox.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event linkDownloadTextboxEvent) {
							Textbox textbox = (Textbox) linkDownloadTextboxEvent
									.getTarget();
							textbox.select();
						}
					});
			Button pasteButton = new Button();
			pasteButton.setParent(linkDownloadCellTextbox);
			pasteButton.setImage("image/paste.png");
			pasteButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event pasteEvent) {
							Textbox textbox = (Textbox) pasteEvent.getTarget()
									.getParent().getChildren().get(0);
							if (descriptionTextbox.getValue().isEmpty()) {
								descriptionTextbox.setValue(textbox.getValue());
							} else {
								descriptionTextbox.setValue(descriptionTextbox
										.getValue() + "\n" + textbox.getValue());
							}
						}
					});

			Row emailToRow = new Row();
			emailToRow.setParent(sendEmailRows);
			Cell emailToCell = new Cell();
			emailToCell.setParent(emailToRow);
			emailToCell.setStyle("vertical-align: top");
			Label emailToLabel = new Label("Email to");
			emailToLabel.setParent(emailToCell);
			Cell emailToTextboxCell = new Cell();
			emailToTextboxCell.setParent(emailToRow);
			emailToTextbox = new Textbox();
			emailToTextbox.setParent(emailToTextboxCell);
			emailToTextbox.setStyle("width: 80%;");
			Button searchUserEmailButton = new Button();
			searchUserEmailButton.setParent(emailToTextboxCell);
			searchUserEmailButton.setImage("image/small_search_icon.png");

			Row descriptionRow = new Row();
			descriptionRow.setParent(sendEmailRows);
			Cell descriptionCell = new Cell();
			descriptionCell.setParent(descriptionRow);
			descriptionCell.setStyle("vertical-align: top");
			Label descriptionLabel = new Label("Email description");
			descriptionLabel.setParent(descriptionCell);
			descriptionTextbox = new Textbox();
			descriptionTextbox.setParent(descriptionRow);
			descriptionTextbox.setStyle("width: 100%;");
			descriptionTextbox.setRows(4);

			Foot sendEmailFoot = new Foot();
			sendEmailFoot.setParent(sendEmailGrid);
			Footer sendEmailFooter = new Footer();
			sendEmailFooter.setParent(sendEmailFoot);
			Cell sendEmailCell = new Cell();
			sendEmailCell.setParent(sendEmailFooter);
			sendEmailCell.setStyle("text-align: center");
			sendEmailCell.setColspan(2);
			Button sendEmailButton = new Button("Email send");
			sendEmailButton.setParent(sendEmailCell);
			sendEmailButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
				public void onEvent(Event sendEmailEvent) {
					if (emailToTextbox.getValue().isEmpty()) {
						throw new WrongValueException(emailToTextbox, "Enter email");
					} else if (descriptionTextbox.getValue().isEmpty()) {
						throw new WrongValueException(descriptionTextbox, "Enter description");
					}
					new EmailService(new EmailObject("from@yo.net", emailToTextbox.getValue(), "Query file Download", descriptionTextbox.getValue()));
					
				}
			});
		}
	}

	public boolean isRefreshActivity() {
		return refreshActivity;
	}

}
