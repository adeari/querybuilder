package apps.controller;

import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class LoginCenterWindow  extends Hbox {
	private static final long serialVersionUID = -6250727136637564563L;
	
	public LoginCenterWindow() {
		Hbox hbox = this;
		hbox.setHflex("1");
		hbox.setVflex("1");
		hbox.setAlign("center");
		hbox.setPack("center");
		hbox.setSpacing("20px");
		hbox.setClass("transparent");
		
		Vlayout vlayout = new Vlayout();
		vlayout.setParent(hbox);
		vlayout.setClass("transparent");
		
		Window windowSmall = new Window();
		windowSmall.setParent(vlayout);
		windowSmall.setHflex("min");
		windowSmall.setClass("transparent");
		
		Vbox vbox = new Vbox();
		vbox.setParent(windowSmall);
		vbox.setAlign("center");
		vbox.setHflex("min");
		vbox.setClass("transparent");
		
		Grid grid = new Grid();
		grid.setParent(vbox);
		grid.setHflex("min");
		
		Columns columns = new Columns();
		columns.setParent(grid);
		
		Column column = new Column();
		column.setParent(columns);
		Column column2 = new Column();
		column2.setParent(columns);
		
		Rows rows = new Rows();
		rows.setParent(grid);
		
		Row row = new Row();
		row.setParent(rows);
		
		Label usernameLabel = new Label("Username ");
		usernameLabel.setParent(row);
		Textbox usernameTextbox = new Textbox();
		usernameTextbox.setParent(row);
		
		Button loginButton = new Button("Login");
		loginButton.setParent(vbox);
		
	}
}
