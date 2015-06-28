package apps.query.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import apps.entity.Activity;
import apps.entity.QueryData;
import apps.entity.Users;
import apps.service.ServiceImplMain;
import apps.service.ServiceMain;
import apps.service.hibernateUtil;

public class QueryTask extends Window {
	private static final long serialVersionUID = -2216130550245303155L;
	private static final Logger logger = Logger.getLogger(QueryTask.class);
	private ServiceMain serviceMain;

	private Selectbox fileTypeSelectbox;
	private QueryData _queryQueryData;
	private Window window;
	private Textbox namedTextbox;
	private Textbox sqlTextbox;

	public QueryTask(QueryData queryData) {
		super("Query operation", null, true);
		_queryQueryData = queryData;
		window = this;
		serviceMain = new ServiceImplMain();

		Vlayout vlayout = new Vlayout();
		vlayout.setParent(window);

		Grid grid = new Grid();
		grid.setParent(vlayout);
		grid.setWidth("400px");
		grid.setStyle("boder: 0");

		Rows rows = new Rows();
		rows.setParent(grid);
		rows.setStyle("boder: 0");

		Row namedRow = new Row();
		namedRow.setParent(rows);
		namedRow.setStyle("boder: 0");
		Label namedLabel = new Label("Query name");
		namedLabel.setParent(namedRow);
		namedLabel.setWidth("100px");
		namedLabel.setStyle("boder: 0");
		namedTextbox = new Textbox(_queryQueryData.getNamed());
		namedTextbox.setParent(namedRow);
		namedTextbox.setReadonly(true);
		namedTextbox.setWidth("300px");
		namedTextbox.setStyle("boder: 0");

		Row queryRow = new Row();
		queryRow.setParent(rows);
		queryRow.setStyle("boder: 0");
		Label sqlLabel = new Label("Query");
		sqlLabel.setParent(queryRow);
		sqlLabel.setWidth("100px");
		sqlLabel.setStyle("boder: 0");
		sqlTextbox = new Textbox(_queryQueryData.getSql());
		sqlTextbox.setParent(queryRow);
		sqlTextbox.setReadonly(true);
		sqlTextbox.setRows(3);
		sqlTextbox.setWidth("300px");
		sqlTextbox.setStyle("boder: 0");

		if (_queryQueryData.getSql().trim().toUpperCase().startsWith("SELECT")) {
			Row fileTypeRow = new Row();
			fileTypeRow.setParent(rows);
			fileTypeRow.setStyle("boder: 0");
			Label fileTypeLabel = new Label("File type");
			fileTypeLabel.setParent(fileTypeRow);
			fileTypeLabel.setWidth("100px");
			fileTypeLabel.setStyle("boder: 0");
			fileTypeSelectbox = new Selectbox();
			fileTypeSelectbox.setParent(fileTypeRow);
			String[] fileTypeList = new String[] { "CSV", "Excel", "PDF" };
			ListModelList<String> fileTypeListModelList = new ListModelList<String>(
					fileTypeList);
			ListModel<String> fileTypeListModel = fileTypeListModelList;
			fileTypeListModelList.addToSelection("Excel");
			fileTypeSelectbox.setModel(fileTypeListModel);
			fileTypeSelectbox.setStyle("boder: 0");
			fileTypeSelectbox.setWidth("300px");
		}

		Row goRow = new Row();
		goRow.setParent(rows);
		Cell goCell = new Cell();
		goCell.setParent(goRow);
		goCell.setColspan(2);
		goCell.setStyle("text-align: center;");
		Button goButton = new Button("GO");
		goButton.setParent(goCell);
		goButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event buttonCustomEvent) {
				if (fileTypeSelectbox == null) {
					Connection connection = null;
					try {
						connection = serviceMain.getConnection(
								_queryQueryData.getDriver(),
								_queryQueryData.getConnectionString());
						PreparedStatement preparedStatement = connection
								.prepareStatement(sqlTextbox.getValue());
						if (sqlTextbox.getValue().trim().startsWith("DELETE")) {
							if (Messagebox.show("Are you sure to delete data",
									"Important",
									Messagebox.YES | Messagebox.NO,
									Messagebox.QUESTION) == Messagebox.YES) {
								preparedStatement.executeUpdate();
								detach();
								Messagebox.show("Process Done", "Information",
										Messagebox.OK, Messagebox.INFORMATION);
							}
						} else {
							preparedStatement.executeUpdate();
							detach();
							Messagebox.show("Process Done", "Information",
									Messagebox.OK, Messagebox.INFORMATION);
						}
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
						Messagebox.show("Query error", "Error", Messagebox.OK,
								Messagebox.ERROR);
					} finally {
						if (connection != null) {
							try {
								connection.close();
							} catch (SQLException e) {
								logger.error(e.getMessage(), e);
							}
						}
					}
				} else {
					Session querySession = null;
					try {
						querySession = hibernateUtil.getSessionFactory()
								.openSession();
						Transaction trx = querySession.beginTransaction();
						Activity activity = new Activity(namedTextbox
								.getValue(), sqlTextbox.getValue(),
								(Users) Sessions.getCurrent().getAttribute(
										"userlogin"), fileTypeSelectbox
										.getModel()
										.getElementAt(
												fileTypeSelectbox
														.getSelectedIndex())
										.toString(), _queryQueryData
										.getDriver(), _queryQueryData
										.getConnectionString());
						querySession.save(activity);
						trx.commit();
						querySession.close();
						detach();
						Messagebox.show("Please wait for process this task",
								"Information", Messagebox.OK,
								Messagebox.INFORMATION);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Messagebox.show("Query error", "Error", Messagebox.OK,
								Messagebox.ERROR);
					} finally {
						if (querySession != null) {
							try {
								querySession.close();
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
						}

					}

				}
			}
		});

		grid.setSizedByContent(true);

	}
}
