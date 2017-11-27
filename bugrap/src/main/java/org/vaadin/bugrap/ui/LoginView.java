package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.ui.generated.LoginViewBase;

import com.vaadin.event.ShortcutAction.KeyCode;

public class LoginView extends LoginViewBase {
	private LoginModel model;
	
	public void setModel(LoginModel model) {
		this.model = model;
	}

	public LoginView() {
		super();
		loginBtn.setClickShortcut(KeyCode.ENTER);
		loginBtn.addClickListener(evt -> model.login(username.getValue(), password.getValue()));
		clearBtn.addClickListener(evt -> clearFields());
	}
	
	private void clearFields() {
		username.clear();
		password.clear();
		username.focus();
	}
}
