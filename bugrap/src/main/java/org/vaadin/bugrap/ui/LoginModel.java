package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.DatabaseHelper;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class LoginModel extends BaseModel{

	public LoginModel(Navigator navigator) {
		super(navigator);
	}
	
	public boolean login(String un, String pw) {
		Reporter reporter = loginUser != null ? loginUser : getRepository().authenticate(un, pw);
		if (reporter != null) {
			loginUser = reporter;
			getNavigator().navigateTo(NAV_REPORT);
			return true;
		}
		
		return false;
	}
	
}
