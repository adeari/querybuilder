package apps.controller.activity;

import org.apache.log4j.Logger;
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
import apps.controller.users.UserEmailWindow;
import apps.entity.Activity;
import apps.entity.FilesData;
import apps.service.EmailService;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;

public class ActivityDetailWindow extends Window {
	private static final long serialVersionUID = 6685464214209516187L;
	private static final Logger logger = Logger
			.getLogger(ActivityDetailWindow.class);
	private ServiceMain serviceMain;

	private Activity _activity;

	public boolean refreshActivity;

	private Window window;
	private Textbox subjectTextbox;
	private Textbox descriptionTextbox;
	private Textbox emailToTextbox;
	private Button searchUserEmailButton;

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

		Row buttonRow = new Row();
		buttonRow.setParent(rows);
		Cell footerCell = new Cell();
		footerCell.setParent(buttonRow);
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
							serviceMain
									.saveUserActivity("Delete activity with query name "
											+ _activity.getQueryName());
							serviceMain.deleteActivity(_activity);
							detach();
						}
					}
				});

		if (_activity.getFileData() != null) {
			FilesData filesData = _activity.getFileData();
			
			Grid fileInfo = new Grid();
			fileInfo.setParent(hbox);
			fileInfo.setWidth("300px");
			
			Rows fileInfoRows = new Rows();
			fileInfoRows.setParent(fileInfo);
			
			Row fileSizeRow = new Row();
			fileSizeRow.setParent(fileInfoRows);
			Cell filseSizeCell = new Cell();
			filseSizeCell.setParent(fileSizeRow);
			filseSizeCell.setWidth("170px");
			Label fileSizeLabel = new Label("File size");
			fileSizeLabel.setParent(filseSizeCell);
			Textbox filseSizeTextbox = new Textbox(filesData.getFilesizeToShow());
			filseSizeTextbox.setParent(fileSizeRow);
			filseSizeTextbox.setReadonly(true);
			
			Row memoryUsedSizeRow = new Row();
			memoryUsedSizeRow.setParent(fileInfoRows);
			Label memoryUsedLabel = new Label("Memory use");
			memoryUsedLabel.setParent(memoryUsedSizeRow);
			Textbox memoryUsedTextbox = new Textbox(_activity.getShowMemoryUsed());
			memoryUsedTextbox.setParent(memoryUsedSizeRow);
			memoryUsedTextbox.setReadonly(true);
			
			Row memoryMaxSizeRow = new Row();
			memoryMaxSizeRow.setParent(fileInfoRows);
			Label memoryMaxLabel = new Label("Memory max");
			memoryMaxLabel.setParent(memoryMaxSizeRow);
			Textbox memoryMaxTextbox = new Textbox(_activity.getShowMemoryMax());
			memoryMaxTextbox.setParent(memoryMaxSizeRow);
			memoryMaxTextbox.setReadonly(true);
			
			Row durationRow = new Row();
			durationRow.setParent(fileInfoRows);
			Label durationLabel = new Label("Duration");
			durationLabel.setParent(durationRow);
			Textbox durationTextbox = new Textbox(_activity.getShowDuration());
			durationTextbox.setParent(durationRow);
			durationTextbox.setReadonly(true);
			
			Row startAtRow = new Row();
			startAtRow.setParent(fileInfoRows);
			Label startAtLabel = new Label("Process start");
			startAtLabel.setParent(startAtRow);
			Textbox startAtTextbox = new Textbox(serviceMain.convertStringFromDate("dd/MM/yyyy", _activity.getStartAt()));
			startAtTextbox.setParent(startAtRow);
			startAtTextbox.setReadonly(true);
			
			Row doneAtRow = new Row();
			doneAtRow.setParent(fileInfoRows);
			Label doneAtLabel = new Label("Process end");
			doneAtLabel.setParent(doneAtRow);
			Textbox doneAtTextbox = new Textbox(serviceMain.convertStringFromDate("dd/MM/yyyy", _activity.getDoneAt()));
			doneAtTextbox.setParent(doneAtRow);
			doneAtTextbox.setReadonly(true);
			
			Row createdAtRow = new Row();
			createdAtRow.setParent(fileInfoRows);
			Label createdAtLabel = new Label("Created at");
			createdAtLabel.setParent(createdAtRow);
			Textbox createdAtTextbox = new Textbox(serviceMain.convertStringFromDate("dd/MM/yyyy", _activity.getCreatedAt()));
			createdAtTextbox.setParent(createdAtRow);
			createdAtTextbox.setReadonly(true);
			
			
			
			Grid sendEmailGrid = new Grid();
			sendEmailGrid.setParent(hbox);

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
					+ Executions.getCurrent().getContextPath();

			Cell linkDownloadCellTextbox = new Cell();
			linkDownloadCellTextbox.setParent(linkDownloadRow);
			Textbox linkDownloadTextbox = new Textbox(url
					+ "/download/file?ridfil="
					+ filesData.getDownloadLink());
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
							String link = "<a href=\""
									+ textbox.getValue()
									+ "\" target=\"_blank\" style=\"cursor:pointer;\">Download</a>";
							if (descriptionTextbox.getValue().isEmpty()) {
								descriptionTextbox.setValue(link);
							} else {
								descriptionTextbox.setValue(descriptionTextbox
										.getValue() + "\n" + link);
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
			searchUserEmailButton = new Button();
			searchUserEmailButton.setParent(emailToTextboxCell);
			searchUserEmailButton.setImage("image/small_search_icon.png");
			searchUserEmailButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event searchUserEmailEvent) {
							if (!searchUserEmailButton.isDisabled()) {
								searchUserEmailButton.setDisabled(true);
								UserEmailWindow userEmailWindow = new UserEmailWindow();
								userEmailWindow.setParent(window);
								userEmailWindow.doModal();

								emailToTextbox.setValue(userEmailWindow
										.getUsersEmail());

								searchUserEmailButton.setDisabled(false);
							}
						}
					});

			Row subjectRow = new Row();
			subjectRow.setParent(sendEmailRows);
			Cell subjectCell = new Cell();
			subjectCell.setParent(subjectRow);
			subjectCell.setStyle("vertical-align: top");
			Label subjectLabel = new Label("Email subject");
			subjectLabel.setParent(subjectCell);
			subjectTextbox = new Textbox();
			subjectTextbox.setParent(subjectRow);
			subjectTextbox.setStyle("width: 100%;");
			
			
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

			Row sendEmailRow = new Row();
			sendEmailRow.setParent(sendEmailRows);
			Cell sendEmailCell = new Cell();
			sendEmailCell.setParent(sendEmailRow);
			sendEmailCell.setStyle("text-align: center");
			sendEmailCell.setColspan(2);
			Button sendEmailButton = new Button("Email send");
			sendEmailButton.setParent(sendEmailCell);
			sendEmailButton.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						public void onEvent(Event sendEmailEvent) {
							if (emailToTextbox.getValue().isEmpty()) {
								throw new WrongValueException(emailToTextbox,
										"Enter email");
							} else if (subjectTextbox.getValue().isEmpty()) {
								throw new WrongValueException(
										subjectTextbox, "Enter subject");
							} else if (descriptionTextbox.getValue().isEmpty()) {
								throw new WrongValueException(
										descriptionTextbox, "Enter description");
							}
							window.setClosable(false);
							EmailObject emailObject = new EmailObject(
									"from@yo.net", emailToTextbox.getValue(),
									subjectTextbox.getValue(), descriptionTextbox
											.getValue());
							serviceMain
									.saveUserActivity("Send email with \nSubject "
											+ emailObject.getSubject()
											+ "\nDescription "
											+ emailObject.getDescription());
							new EmailService(emailObject);
							window.setClosable(true);
						}
					});
		}
	}

	public boolean isRefreshActivity() {
		return refreshActivity;
	}

}
