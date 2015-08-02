/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apps.service;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;

import apps.controller.users.ProfileWindow;

/**
 *
 * @author ade
 */
public class hibernateUtil {
	private static final Logger logger = Logger.getLogger(ProfileWindow.class);
    public static Session getSessionFactory(Session session) {
    	if (session == null) {
    		return new AnnotationConfiguration().configure().buildSessionFactory().openSession();
    	}
        if (!session.isConnected() || !session.isOpen()) {
        	return new AnnotationConfiguration().configure().buildSessionFactory().openSession();
        }
        return session;
    }
    
}
