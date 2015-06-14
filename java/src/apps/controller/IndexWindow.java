package apps.controller;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

public class IndexWindow  extends GenericForwardComposer<Window> {
	private static final long serialVersionUID = 6891864713458812220L;
	
	private Window windowMain;
	
	@Override
	public void doAfterCompose(Window window) throws Exception {
		windowMain = window;
		
		Session session = Sessions.getCurrent();
		
		LoginWindow loginWindow = new LoginWindow(windowMain);
		windowMain.appendChild(loginWindow);
		
		MenuWindow menuWindow = new MenuWindow(windowMain);
		windowMain.appendChild(menuWindow);
		
		if (session.getAttribute("userlogin") != null) {
			loginWindow.setVisible(false);
		} else {
			menuWindow.setVisible(false);
		}
		
	}
}
