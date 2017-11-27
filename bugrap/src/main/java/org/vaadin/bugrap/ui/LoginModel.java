package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.DatabaseHelper;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class LoginModel extends BaseModel{

	public void login(String un, String pw) {
		Reporter reporter = getRepository().authenticate(un, pw);
		if (reporter == null && DatabaseHelper.initializeIfEmpty(getRepository())) {
			System.out.println("Database was empty, but initialized. Reautenticating...");
			reporter = getRepository().authenticate(un, pw);
		}
		
		if (reporter != null) {
			Notification.show("Welcome "+reporter.getName(), Type.TRAY_NOTIFICATION);
		}else {
			Notification.show("Please check your credentials", Type.WARNING_MESSAGE);
		}
	}
	
}
