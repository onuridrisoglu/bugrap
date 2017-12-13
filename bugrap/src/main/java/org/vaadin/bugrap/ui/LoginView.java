package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.ui.generated.LoginViewBase;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class LoginView extends LoginViewBase implements View{
	
	private LoginModel model;
	
	public LoginView(LoginModel m) {
		super();
		model = m;
		loginBtn.setClickShortcut(KeyCode.ENTER);
		loginBtn.addClickListener(evt -> login());
		clearBtn.addClickListener(evt -> clearFields());
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (model.getLoginUser() != null)
			model.login(username.getValue(), password.getValue());
		clearFields();
	}
	
	private void clearFields() {
		username.clear();
		password.clear();
		username.focus();
	}
	
	private void login() {
		boolean isAuthenticated = model.login(username.getValue(), password.getValue());
		if (!isAuthenticated)
			Notification.show("Authentication failed, please check your credentials", Type.WARNING_MESSAGE);
	}
}
