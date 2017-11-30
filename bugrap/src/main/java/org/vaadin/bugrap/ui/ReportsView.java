package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.ui.generated.ReportsViewBase;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

public class ReportsView extends ReportsViewBase implements View{

	private ReportsModel model;
	
	public ReportsView() {
		
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		init();
	}
	
	public void init() {
		btnUser.setCaption(BaseModel.loginUser.getName());
		gridReports.setColumns("priority", "type", "summary", "assigned", "timestamp", "reportedTimestamp");
		cmbProjectFilter.addSelectionListener(evt -> model.processProjectChange(cmbProjectFilter.getSelectedItem(), cmbVersion));
		cmbVersion.addSelectionListener(evt -> model.processVersionChange(cmbVersion.getSelectedItem(), gridReports));
		btnLogout.addClickListener(evt -> model.logout());
		model.fillProjects(cmbProjectFilter);
	}

	public void setModel(ReportsModel model) {
		this.model = model;
	}
	
	
	
	
}
