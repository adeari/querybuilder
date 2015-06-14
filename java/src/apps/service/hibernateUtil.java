/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apps.service;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 * @author ade
 */
public class hibernateUtil {
 
    public static SessionFactory getSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
        	return new AnnotationConfiguration().configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
}
