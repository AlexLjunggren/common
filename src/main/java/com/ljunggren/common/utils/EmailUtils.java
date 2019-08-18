package com.ljunggren.common.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ljunggren.common.config.AppProperties;

public class EmailUtils {
	
	private static String username = AppProperties.getEmailUsername();
	private static String password = AppProperties.getEmailPassword();

	public static void sendMail(String to, String from, String subject, String body) throws AddressException, MessagingException {
		Session session = createSession();
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setText(body);
		message.setContent(body, "text/html; charset=utf-8");

		Transport.send(message);
	}
	
	private static Properties getProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.host", "smtp.live.com");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		return properties;
	}
	
	private static Session createSession() {
		return Session.getInstance(getProperties(),
				new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
	}
	
}
