package org.vaadin.bugrap.ui;

import java.util.Set;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;
import org.vaadin.bugrap.ui.generated.ReportsViewBase;
import org.vaadin.bugrap.util.ReportUtil;
import org.vaadin.bugrap.util.StringUtil;

import com.vaadin.data.ValidationException;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class ReportsView extends ReportsViewBase implements View{

	public static final int SLIDER_NOSELECTION		= 100;
	public static final int SLIDER_SINGLESELECTION	= 52;
	public static final int SLIDER_MULTISELECTION	= 78;
	
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
		
		cmbProjectFilter.addSelectionListener(evt -> model.processProjectChange(cmbProjectFilter.getSelectedItem(), cmbVersion, cmbEditVersion));
		cmbVersion.addSelectionListener(evt -> model.processVersionChange(cmbVersion.getSelectedItem(), gridReports));
		btnReportSummary.addClickListener(evt -> model.openReportDetail());
		btnUpdateReport.addClickListener(evt -> saveReport());
		btnRevertReport.addClickListener(evt -> revertChanges());
		btnLogout.addClickListener(evt -> model.logout());
		gridReports.addSelectionListener(evt -> onReportSelected(evt));
	}
	

	private void revertChanges() {
		model.revertChanges(gridReports.getSelectedItems());
	}

	private void initializeBinder() {
		model.getBinder().bind(cmbEditPriority, Report::getPriority, Report::setPriority);
		model.getBinder().bind(cmbEditType, Report::getType, Report::setType);
		model.getBinder().bind(cmbEditStatus, Report::getStatus, Report::setStatus);
		model.getBinder().bind(cmbEditAssignedTo, Report::getAssigned, Report::setAssigned);
		model.getBinder().bind(cmbEditVersion, Report::getVersion, Report::setVersion);
		model.getBinder().bind(txtReportDescription, Report::getDescription, Report::setDescription);
	}

	private void onReportSelected(SelectionEvent<Report> evt) {
		int selectedItemCount = evt.getAllSelectedItems().size();
		if (selectedItemCount == 0) {
			vsplit.setSplitPosition(SLIDER_NOSELECTION);
		}else {
			if (selectedItemCount == 1) {
				processSingleSelection(evt.getFirstSelectedItem().get());
				vsplit.setSplitPosition(SLIDER_SINGLESELECTION);
			} else if (selectedItemCount > 1) {
				processMultiSelection(evt.getAllSelectedItems());
				vsplit.setSplitPosition(SLIDER_MULTISELECTION);
			} 
		}
	}

	private void processSingleSelection(Report selectedReport) {
		model.getBinder().setBean(selectedReport);
		btnReportSummary.setCaption(selectedReport.getSummary());
		btnReportSummary.setVisible(true);
		txtReportDescription.setVisible(true);
		lblMultiReportInfo.setVisible(false);
		
	}
	
	private void processMultiSelection(Set<Report> selectedReports) {
		Report commonFields = ReportUtil.getCommonFields(selectedReports);
		model.getBinder().setBean(commonFields);
		lblMultiReportInfo.setValue(commonFields.getSummary());
		txtReportDescription.setVisible(false);
		lblMultiReportInfo.setVisible(true);
		btnReportSummary.setVisible(false);
	}

	private void saveReport() {
		try {
			boolean isBulkMode = gridReports.getSelectedItems().size() > 1;
			model.updateReport(gridReports.getSelectedItems());
			Notification.show("Report"+(isBulkMode ? "s are" : " is") +" saved", Type.TRAY_NOTIFICATION);
			model.processVersionChange(cmbVersion.getSelectedItem(), gridReports);
		} catch (ValidationException e) {
			Notification.show("Missing information, "+e.getMessage(), Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
