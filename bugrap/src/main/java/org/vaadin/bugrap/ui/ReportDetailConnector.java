package org.vaadin.bugrap.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class ReportDetailConnector extends UI {
	
	@Override
	protected void init(VaadinRequest request) {
		ReportsModel model = new ReportsModel(getNavigator());
		model.setSelectedReportsFromText(request.getParameter("selectedItems"));
		ReportDetailView view = new ReportDetailView(model);
		view.setSizeFull();
		setContent(view);
	}

}
