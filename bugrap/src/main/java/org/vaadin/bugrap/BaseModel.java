package org.vaadin.bugrap;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.navigator.Navigator;

public class BaseModel {

	public static final String NAV_LOGIN 	= "login";
	public static final String NAV_REPORT	= "report";
	public static final String NAV_REPORTDET	= "detail";
	
	private static final BugrapRepository repository = new BugrapRepository(DatabaseHelper.DB_LOCATION);
	private Navigator navigator;
	public static Reporter loginUser;
	
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
		loginUser = null;
		navigator.navigateTo(NAV_LOGIN);
	}
}
