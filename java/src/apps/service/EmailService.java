package apps.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import apps.beans.EmailObject;

public class EmailService {
	private static final Logger logger = Logger.getLogger(EmailService.class);
	
	public EmailService(EmailObject emailObject) {
		final String username = "adeariw84@gmail.com";
		final String password = "gt5rdxsw2";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailObject.getEmaiForm().trim()));
			
			String emailTo = emailObject.getEmaiTo();
			String[] emailToSplit = emailTo.split(",");
			InternetAddress[] address = new InternetAddress[emailToSplit.length];
			for (int i = 0; i < address.length; i++) {
				address[i] = new InternetAddress(emailToSplit[i].trim());
			}
			message.setRecipients(Message.RecipientType.TO, address);

			message.setSubject(emailObject.getSubject());
			message.setText(emailObject.getDescription());
			
			Multipart mp = new MimeMultipart();

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(emailObject.getDescription().replaceAll("\n", "<br/>"), "text/html");
			mp.addBodyPart(htmlPart);
			message.setContent(mp);

			Transport.send(message);


		} catch (MessagingException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
