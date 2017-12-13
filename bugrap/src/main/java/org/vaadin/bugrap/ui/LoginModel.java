package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinSession;

public class LoginModel extends BaseModel {
	public LoginModel(Navigator navigator, VaadinSession session) {
		super(navigator, session);
	}

	public boolean login(String un, String pw) {
		Reporter loginUser = getLoginUser();
		if (loginUser == null) {
			loginUser = getRepository().authenticate(un, pw);
		}
		if (loginUser != null) {
			setLoginUser(loginUser);
			getNavigator().navigateTo(NAV_REPORT);
			return true;
		}

		return false;
	}

}
