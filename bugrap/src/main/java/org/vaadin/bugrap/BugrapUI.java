package org.vaadin.bugrap;

import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.vaadin.bugrap.ui.LoginModel;
import org.vaadin.bugrap.ui.LoginView;
import org.vaadin.bugrap.ui.ReportsModel;
import org.vaadin.bugrap.ui.ReportsView;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
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
@Theme("mytheme")
public class BugrapUI extends UI {
    private static Logger logger = Logger.getLogger(BugrapUI.class.getName());
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		Navigator navigator = new Navigator(this, this);
		
		final LoginView loginView = new LoginView(new LoginModel(navigator, getSession()));
		navigator.addView(BaseModel.NAV_LOGIN, loginView);

		final ReportsView reportsView = new ReportsView(new ReportsModel(navigator, getSession()));
		navigator.addView(BaseModel.NAV_REPORT, reportsView);
		navigator.navigateTo(BaseModel.NAV_LOGIN);
	}

    @WebServlet(urlPatterns = "/*", name = "BugrapUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = BugrapUI.class, productionMode = false)
    public static class BugrapUIServlet extends VaadinServlet {
    	
    		@Override
    		protected void servletInitialized() throws ServletException {
    			super.servletInitialized();
    			if (DatabaseHelper.initializeIfEmpty(BaseModel.getRepository())) {
    				logger.info("Database was empty, but initialized.");
    			}else {
    				logger.fine("Database is already initialized.");
    			}
    		}
    }
}
