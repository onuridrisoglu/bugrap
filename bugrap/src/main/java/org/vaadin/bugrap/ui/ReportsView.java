package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.ui.columns.CamelcaseTextRenderer;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;
import org.vaadin.bugrap.ui.generated.ReportsViewBase;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

public class ReportsView extends ReportsViewBase implements View{

	private ReportsModel model;
	
	public ReportsView() {
		initializeUIComponents();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		init();
	}
	
	public void init() {
		btnUser.setCaption(BaseModel.loginUser.getName());
		model.fillProjects(cmbProjectFilter);
	}

	public void setModel(ReportsModel model) {
		this.model = model;
	}
	
	private void initializeUIComponents() {
		gridReports.removeAllColumns();
		gridReports.addColumn("priority").setCaption("PRIORITY").setExpandRatio(1);
		gridReports.addColumn("type", new CamelcaseTextRenderer()).setCaption("TYPE").setExpandRatio(1);
		gridReports.addColumn("summary").setCaption("SUMMARY").setExpandRatio(5);
		gridReports.addColumn("assigned").setCaption("ASSIGNED TO").setExpandRatio(2);
		gridReports.addColumn("timestamp", new FineDateRenderer()).setCaption("LAST MODIFIED").setExpandRatio(2);
		gridReports.addColumn("reportedTimestamp", new FineDateRenderer()).setCaption("REPORTED").setExpandRatio(2);
		
		gridReports.setColumns("priority", "type", "summary", "assigned", "timestamp", "reportedTimestamp");
		
		cmbProjectFilter.addSelectionListener(evt -> model.processProjectChange(cmbProjectFilter.getSelectedItem(), cmbVersion));
		cmbVersion.addSelectionListener(evt -> model.processVersionChange(cmbVersion.getSelectedItem(), gridReports));
		btnLogout.addClickListener(evt -> model.logout());
		
	}
	
}
