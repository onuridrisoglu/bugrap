package org.vaadin.bugrap;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinSession;

public class BaseModel {

	public static final String NAV_LOGIN = "login";
	public static final String NAV_REPORT = "report";

	private static final String SESSIONVAR_LOGINUSER = "loginuser";

	private static final BugrapRepository repository = new BugrapRepository(DatabaseHelper.DB_LOCATION);
	private Navigator navigator;

	public BaseModel(Navigator nav) {
		navigator = nav;
	}

	protected static BugrapRepository getRepository() {
		return repository;
	}

	protected Navigator getNavigator() {
		return navigator;
	}

	public void logout() {
		setSessionVariable(SESSIONVAR_LOGINUSER, null);
		navigator.navigateTo(NAV_LOGIN);
	}

	public Reporter getLoginUser() {
		return (Reporter) getSessionVariable(SESSIONVAR_LOGINUSER);
	}

	public void setLoginUser(Reporter user) {
		setSessionVariable(SESSIONVAR_LOGINUSER, user);
	}

	private Object getSessionVariable(String key) {
		return VaadinSession.getCurrent().getAttribute(key);
	}

	private void setSessionVariable(String key, Object var) {
		VaadinSession.getCurrent().setAttribute(key, var);
	}
}
