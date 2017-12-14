package org.vaadin.bugrap.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.ui.beans.ReportDistribution;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;
import org.vaadin.bugrap.ui.columns.PriorityColumnRenderer;
import org.vaadin.bugrap.ui.generated.ReportsViewBase;
import org.vaadin.bugrap.util.StringUtil;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import elemental.json.JsonType;
import elemental.json.JsonValue;

public class ReportsView extends ReportsViewBase implements View {

	public static final int SLIDER_NOSELECTION = 100;
	public static final int SLIDER_SINGLESELECTION = 52;
	public static final int SLIDER_MULTISELECTION = 78;
	
	public static final int MENUBARIDX_ASSIGNEE_ME = 0;
	public static final int MENUBARIDX_ASSIGNEE_ALL = 1;

	public static final int MENUBARIDX_STATUS_OPEN = 0;
	public static final int MENUBARIDX_STATUS_ALL = 1;
	public static final int MENUBARIDX_STATUS_CUSTOM = 2;

	public static final int MENUBARIDX_CUSTOMSTATUS_OPEN = 0;
	public static final int MENUBARIDX_CUSTOMSTATUS_FIXED = 2;
	public static final int MENUBARIDX_CUSTOMSTATUS_INVALID = 3;
	public static final int MENUBARIDX_CUSTOMSTATUS_WONTFIX = 4;
	public static final int MENUBARIDX_CUSTOMSTATUS_CANTFIX = 5;
	public static final int MENUBARIDX_CUSTOMSTATUS_DUPLICATE = 6;
	public static final int MENUBARIDX_CUSTOMSTATUS_WORKS4ME = 7;
	public static final int MENUBARIDX_CUSTOMSTATUS_NEEDSINFO = 8;
	
	private ReportsModel model;
	private Binder<Report> binder = new Binder<Report>();

	public ReportsView(ReportsModel m) {
		super();
		model = m;
		initializeGrid();
		initializeUIComponents();
		initializeBinder();
		initializeMenuBars();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		init();
	}

	public void init() {
		model.setStatusFilters(Status.values());
		drawAssigneeMenubarFromModel();
		drawStatusMenubarFromModel();
		
		btnUser.setCaption(model.getLoginUser().getName());
		refreshComboboxContent();
	}

	private void initializeMenuBars() {
		menuAssignees.getItems().get(MENUBARIDX_ASSIGNEE_ME).setCommand(evt -> onAssigneeFilterSelected(ReportsModel.ASSIGNEE_ME));
		menuAssignees.getItems().get(MENUBARIDX_ASSIGNEE_ALL).setCommand(evt -> onAssigneeFilterSelected(ReportsModel.ASSIGNEE_ALL));
		
		menuStatus.getItems().get(MENUBARIDX_STATUS_OPEN).setCommand(evt -> onStatusFilterSelected(true, true, Status.OPEN));
		menuStatus.getItems().get(MENUBARIDX_STATUS_ALL).setCommand(evt -> onStatusFilterSelected(true, true, Status.values()));
		
		MenuItem customStatusMenuItem = menuStatus.getItems().get(MENUBARIDX_STATUS_CUSTOM);
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_OPEN).setCommand(evt -> onStatusFilterSelected(false, evt.isChecked(), Status.OPEN));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_CANTFIX).setCommand(evt -> onStatusFilterSelected(false, evt.isChecked(), Status.CANT_FIX));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_DUPLICATE).setCommand(evt -> onStatusFilterSelected(false, evt.isChecked(), Status.DUPLICATE));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_FIXED).setCommand(evt -> onStatusFilterSelected(false, evt.isChecked(), Status.FIXED));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_INVALID).setCommand(evt -> onStatusFilterSelected(false, evt.isChecked(), Status.INVALID));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_NEEDSINFO).setCommand(evt -> onStatusFilterSelected(false, evt.isChecked(), Status.NEED_MORE_INFO));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_WONTFIX).setCommand(evt -> onStatusFilterSelected(false, evt.isChecked(), Status.WONT_FIX));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_WORKS4ME).setCommand(evt -> onStatusFilterSelected(false, evt.isChecked(), Status.WORKS_FOR_ME));
	}

	private void onAssigneeFilterSelected(int mode) {
		model.setAssigneeFilterMode(mode);
		drawAssigneeMenubarFromModel();
		refreshGridContent();
	}

	private void onStatusFilterSelected(boolean isSetMode, boolean isChecked, Status...status) {
		if (isSetMode)
			model.setStatusFilters(status);
		else
			model.changeStatusFilters(isChecked, status);
		drawStatusMenubarFromModel();
		refreshGridContent();
	}
	
	private void drawAssigneeMenubarFromModel() {
		menuAssignees.getItems().get(MENUBARIDX_ASSIGNEE_ME).setChecked(model.getAssigneeFilterMode() == ReportsModel.ASSIGNEE_ME);
		menuAssignees.getItems().get(MENUBARIDX_ASSIGNEE_ALL).setChecked(model.getAssigneeFilterMode() == ReportsModel.ASSIGNEE_ALL);
	}

	private void drawStatusMenubarFromModel() {
		menuStatus.getItems().get(MENUBARIDX_STATUS_OPEN).setChecked(model.getStatusFilter().size() == 1 && model.getStatusFilter().contains(Status.OPEN));
		menuStatus.getItems().get(MENUBARIDX_STATUS_ALL).setChecked(model.getStatusFilter().containsAll(Arrays.asList(Status.values())));
		
		MenuItem customStatusMenuItem = menuStatus.getItems().get(MENUBARIDX_STATUS_CUSTOM);
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_OPEN).setChecked(model.getStatusFilter().contains(Status.OPEN));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_CANTFIX).setChecked(model.getStatusFilter().contains(Status.CANT_FIX));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_DUPLICATE).setChecked(model.getStatusFilter().contains(Status.DUPLICATE));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_FIXED).setChecked(model.getStatusFilter().contains(Status.FIXED));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_INVALID).setChecked(model.getStatusFilter().contains(Status.INVALID));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_NEEDSINFO).setChecked(model.getStatusFilter().contains(Status.NEED_MORE_INFO));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_WONTFIX).setChecked(model.getStatusFilter().contains(Status.WONT_FIX));
		customStatusMenuItem.getChildren().get(MENUBARIDX_CUSTOMSTATUS_WORKS4ME).setChecked(model.getStatusFilter().contains(Status.WORKS_FOR_ME));
	}

	private void refreshComboboxContent() {
		List<Project> projects = model.findProjects();
		cmbProjectFilter.setItems(projects);
		cmbProjectFilter.setSelectedItem(projects.get(0));
		cmbEditPriority.setItems(model.getPriorties());
		cmbEditType.setItems(model.getTypes());
		cmbEditType.setItemCaptionGenerator(type -> StringUtil.converToCamelCaseString(type.name()));
		cmbEditStatus.setItems(model.getAllStatuses());
		cmbEditAssignedTo.setItems(model.findReporters());
	}

	private void initializeGrid() {
		gridReports.removeAllColumns();
		gridReports.addColumn(report -> PriorityColumnRenderer.getHtmlForPriority(report.getPriority()), new HtmlRenderer()).setCaption("PRIORITY").setExpandRatio(1);
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
