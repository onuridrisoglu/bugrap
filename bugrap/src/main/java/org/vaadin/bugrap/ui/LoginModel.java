package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.navigator.Navigator;

public class LoginModel extends BaseModel {
	public LoginModel(Navigator navigator) {
		super(navigator);
	}

	public boolean authenticate(String un, String pw) {
		Reporter loginUser = getRepository().authenticate(un, pw);
		if (loginUser != null) {
			setLoginUser(loginUser);
			return true;
		}
		return false;
	}
	
	public void navigateToReports() {
		if (getLoginUser() != null)
			getNavigator().navigateTo(NAV_REPORT);
	}
}
