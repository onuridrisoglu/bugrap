package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.columns.CamelcaseTextRenderer;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;
import org.vaadin.bugrap.ui.generated.ReportsViewBase;
import org.vaadin.bugrap.util.StringUtil;

import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

public class ReportsView extends ReportsViewBase implements View{

	private ReportsModel model;
	
	public ReportsView(ReportsModel m) {
		super();
		model = m;
		initializeUIComponents();
		initializeBinder();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		init();
	}
	
	public void init() {
		btnUser.setCaption(BaseModel.loginUser.getName());
		refreshComboboxContent();
	}

	private void refreshComboboxContent() {
		model.fillProjects(cmbProjectFilter);
		model.fillPriorties(cmbEditPriority);
		model.fillTypes(cmbEditType);
		model.fillStatuses(cmbEditStatus);
		model.fillAssigned(cmbEditAssignedTo);
	}
	
	private void initializeUIComponents() {
		gridReports.removeAllColumns();
		gridReports.addColumn("priority").setCaption("PRIORITY").setExpandRatio(1);
		gridReports.addColumn(report -> StringUtil.converToCamelCaseString(report.getType().toString())).setCaption("TYPE").setExpandRatio(1);
		gridReports.addColumn("summary").setCaption("SUMMARY").setExpandRatio(5);
		gridReports.addColumn("assigned").setCaption("ASSIGNED TO").setExpandRatio(2);
		gridReports.addColumn("timestamp", new FineDateRenderer()).setCaption("LAST MODIFIED").setExpandRatio(2);
		gridReports.addColumn("reportedTimestamp", new FineDateRenderer()).setCaption("REPORTED").setExpandRatio(2);
		
		gridReports.setColumns("priority", "type", "summary", "assigned", "timestamp", "reportedTimestamp");
		
		cmbProjectFilter.addSelectionListener(evt -> model.processProjectChange(cmbProjectFilter.getSelectedItem(), cmbVersion, cmbEditVersion));
		cmbVersion.addSelectionListener(evt -> model.processVersionChange(cmbVersion.getSelectedItem(), gridReports));
		btnLogout.addClickListener(evt -> model.logout());
		gridReports.addSelectionListener(evt -> displaySelectedReport(evt));
	}
	
	private void initializeBinder() {
		model.getBinder().bind(cmbEditPriority, Report::getPriority, Report::setPriority);
		model.getBinder().bind(cmbEditType, Report::getType, Report::setType);
		model.getBinder().bind(cmbEditStatus, Report::getStatus, Report::setStatus);
		model.getBinder().bind(cmbEditAssignedTo, Report::getAssigned, Report::setAssigned);
		model.getBinder().bind(cmbEditVersion, Report::getVersion, Report::setVersion);
		model.getBinder().bind(txtReportDescription, Report::getDescription, Report::setDescription);
	}

	private void displaySelectedReport(SelectionEvent<Report> evt) {
		float position = 100;
		if (evt.getFirstSelectedItem().isPresent()) {
			Report selectedReport = evt.getFirstSelectedItem().get();
			model.getBinder().readBean(selectedReport);
			btnReportSummary.setCaption(selectedReport.getSummary());
			position = 55;
		}
		vsplit.setSplitPosition(position);
	}
	
}
