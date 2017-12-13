package org.vaadin.bugrap.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.beans.ReportDistribution;
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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ReportsView extends ReportsViewBase implements View {

	public static final int SLIDER_NOSELECTION = 100;
	public static final int SLIDER_SINGLESELECTION = 52;
	public static final int SLIDER_MULTISELECTION = 78;

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
		gridReports.addColumn(report -> StringUtil.converToCamelCaseString(report.getType().toString()))
				.setCaption("TYPE").setExpandRatio(1);
		gridReports.addColumn("summary").setCaption("SUMMARY").setExpandRatio(5);
		gridReports.addColumn("assigned").setCaption("ASSIGNED TO").setExpandRatio(2);
		gridReports.addColumn("timestamp", new FineDateRenderer()).setCaption("LAST MODIFIED").setExpandRatio(2);
		gridReports.addColumn("reportedTimestamp", new FineDateRenderer()).setCaption("REPORTED").setExpandRatio(2);
	}

	private void initializeUIComponents() {
		cmbProjectFilter.addSelectionListener(evt -> processProjectChange());
		cmbVersion.addSelectionListener(evt -> processVersionChange());
		btnUpdateReport.addClickListener(evt -> saveReport());
		btnRevertReport.addClickListener(evt -> revertChanges());
		btnLogout.addClickListener(evt -> model.logout());
		gridReports.addSelectionListener(evt -> onReportSelected(evt));
		btnReportSummary.addClickListener(evt -> openReportDetail());
	}

	private void initializeBinder() {
		binder.bind(cmbEditPriority, Report::getPriority, Report::setPriority);
		binder.bind(cmbEditType, Report::getType, Report::setType);
		binder.bind(cmbEditStatus, Report::getStatus, Report::setStatus);
		binder.bind(cmbEditAssignedTo, Report::getAssigned, Report::setAssigned);
		binder.bind(cmbEditVersion, Report::getVersion, Report::setVersion);
	}

	private void fillComments() {
		VerticalLayout layout = (VerticalLayout) pnlThreadComments.getContent();
		layout.removeAllComponents();
		List<Comment> comments = model.getComments();
		for (Comment comment : comments) {
			layout.addComponent(new ThreadItemView(new ThreadItemModel(comment)));
		}
	}

	private void processProjectChange() {
		List<ProjectVersion> versions = model.findProjectVersions(cmbProjectFilter.getValue());
		cmbVersion.setItems(versions);
		cmbVersion.setSelectedItem(versions.get(0));
		cmbEditVersion.setItems(versions);
	}

	private void processVersionChange() {
		refreshGridContent();
		refreshDistributionBar();
	}

	private void refreshGridContent() {
		List<Report> selectedReports = new ArrayList<Report>();
		selectedReports.addAll(model.getSelectedReports());
		gridReports.setItems(model.findReports(cmbProjectFilter.getValue(), cmbVersion.getValue()));
		for (Report report : selectedReports) {
			gridReports.select(report);
		}
	}
	
	private void refreshDistributionBar() {
		ReportDistribution distribution = model.getReportDistribution(cmbVersion.getValue());
		distributionBar.setDistributions(distribution.getClosedReports(), distribution.getAssignedReports(), distribution.getUnassignedReports());
	}

	private void onReportSelected(SelectionEvent<Report> evt) {
		model.setSelectedReports(gridReports.getSelectedItems());
		if (model.getSelectionMode() == ReportsModel.SELECTIONMODE_NONE) {
			vsplit.setSplitPosition(SLIDER_NOSELECTION);
		} else {
			Report report = model.getReportForEdit();
			binder.setBean(report);
			btnReportSummary.setCaption(report.getSummary());
			lblMultiReportInfo.setValue(report.getSummary());
			fillComments();
			handleUIChangesWithSelection();
		}
	}

	private void handleUIChangesWithSelection() {
		boolean isMutliSelect = model.getSelectionMode() == ReportsModel.SELECTIONMODE_MULTI;
		btnReportSummary.setVisible(!isMutliSelect);
		pnlThreadComments.setVisible(!isMutliSelect);
		lblMultiReportInfo.setVisible(isMutliSelect);
		vsplit.setSplitPosition(isMutliSelect ? SLIDER_MULTISELECTION : SLIDER_SINGLESELECTION);
	}

	private void saveReport() {
		try {
			model.saveReport();
			Notification.show("Saved successfully", Type.TRAY_NOTIFICATION);
			refreshGridContent();
		} catch (ValidationException e) {
			Notification.show("Missing information, " + e.getMessage(), Type.ERROR_MESSAGE);
		}
	}

	private void revertChanges() {
		model.resetReportForEdit();
		binder.setBean(model.getReportForEdit());
	}

	private void openReportDetail() {
		Window window = new Window();
		Report report = model.getReportForEdit();
		window.setCaption(String.format("%s > %s", report.getProject().getName(), report.getVersion().getVersion()));
		window.setSizeFull();

		ReportDetailView view = new ReportDetailView(model);
		window.setContent(view);
		getUI().addWindow(window);
	}
}
