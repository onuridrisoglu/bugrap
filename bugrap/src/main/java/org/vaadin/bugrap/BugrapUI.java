package org.vaadin.bugrap;

import javax.servlet.annotation.WebServlet;

import org.vaadin.bugrap.ui.LoginModel;
import org.vaadin.bugrap.ui.LoginView;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("valo")
public class BugrapUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final LoginView loginView = new LoginView();
        loginView.setModel(new LoginModel());
        setContent(loginView);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = BugrapUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
