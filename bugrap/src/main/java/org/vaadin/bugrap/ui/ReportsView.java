package org.vaadin.bugrap.ui;

import java.util.Collection;
import java.util.List;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;
import org.vaadin.bugrap.ui.generated.ReportsViewBase;
import org.vaadin.bugrap.util.StringUtil;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class ReportsView extends ReportsViewBase implements View{

	private ReportsModel model;
	private Binder<Report> binder = new Binder<Report>();
	
	public ReportsView(ReportsModel m) {
		super();
		model = m;
		initializeGrid();
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
		List<Project> projects = model.findProjects();
		cmbProjectFilter.setItems(projects);
		cmbProjectFilter.setSelectedItem(projects.get(0));
		
		cmbEditPriority.setItems(model.getPriorties());
		
		cmbEditType.setItems(model.getTypes());
		cmbEditType.setItemCaptionGenerator(type -> StringUtil.converToCamelCaseString(type.name()));
		
		cmbEditStatus.setItems(model.getStatuses());
		
		cmbEditAssignedTo.setItems(model.findReporters());
	}
	
	private void initializeGrid() {
		gridReports.removeAllColumns();
		gridReports.addColumn("priority").setCaption("PRIORITY").setExpandRatio(1);
		gridReports.addColumn(report -> StringUtil.converToCamelCaseString(report.getType().toString())).setCaption("TYPE").setExpandRatio(1);
		gridReports.addColumn("summary").setCaption("SUMMARY").setExpandRatio(5);
		gridReports.addColumn("assigned").setCaption("ASSIGNED TO").setExpandRatio(2);
		gridReports.addColumn("timestamp", new FineDateRenderer()).setCaption("LAST MODIFIED").setExpandRatio(2);
		gridReports.addColumn("reportedTimestamp", new FineDateRenderer()).setCaption("REPORTED").setExpandRatio(2);
//		gridReports.setColumns("priority", "type", "summary", "assigned", "timestamp", "reportedTimestamp");
	}
	
	private void initializeUIComponents() {
		cmbProjectFilter.addSelectionListener(evt -> processProjectChange());
		cmbVersion.addSelectionListener(evt -> processVersionChange());
		btnUpdateReport.addClickListener(evt -> saveReport());
		btnRevertReport.addClickListener(evt -> revertChanges());
		btnLogout.addClickListener(evt -> model.logout());
		gridReports.addSelectionListener(evt -> displaySelectedReport(evt));
	}

	private void processProjectChange() {
		List<ProjectVersion> versions = model.findProjectVersions(cmbProjectFilter.getValue());
		cmbVersion.setItems(versions);
		cmbEditVersion.setItems(versions);
	}
	
	private void processVersionChange() {
		refreshGridContent();
	}

	private void refreshGridContent() {
		Collection<Report> selectedReports = gridReports.getSelectedItems();
		gridReports.setItems(model.findReports(cmbProjectFilter.getValue(), cmbVersion.getValue()));
		if (selectedReports.size() > 0)
			gridReports.getSelectedItems().addAll(selectedReports);
	}

	private void initializeBinder() {
		binder.bind(cmbEditPriority, Report::getPriority, Report::setPriority);
		binder.bind(cmbEditType, Report::getType, Report::setType);
		binder.bind(cmbEditStatus, Report::getStatus, Report::setStatus);
		binder.bind(cmbEditAssignedTo, Report::getAssigned, Report::setAssigned);
		binder.bind(cmbEditVersion, Report::getVersion, Report::setVersion);
		binder.bind(txtReportDescription, Report::getDescription, Report::setDescription);
	}

	private void displaySelectedReport(SelectionEvent<Report> evt) {
		float position = 100;
		if (evt.getFirstSelectedItem().isPresent()) {
			Report selectedReport = evt.getFirstSelectedItem().get();
			binder.setBean(selectedReport);
			btnReportSummary.setCaption(selectedReport.getSummary());
			position = 55;
		}
		vsplit.setSplitPosition(position);
	}
	
	private void saveReport() {
		Report report;
		try {
			report = model.saveReport(binder.getBean());
			Notification.show("Report["+report.getSummary()+"] is saved", Type.TRAY_NOTIFICATION);
			refreshGridContent();
		} catch (ValidationException e) {
			Notification.show("Missing information, "+e.getMessage(), Type.ERROR_MESSAGE);
		}
	}
	
	private void revertChanges() {
		Report report = model.getReportById(binder.getBean().getId());
		binder.setBean(report);
	}
}
