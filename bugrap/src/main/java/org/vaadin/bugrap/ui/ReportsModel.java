package org.vaadin.bugrap.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.BugrapRepository.ReportsQuery;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Report.Type;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.bugrap.ui.columns.CamelcaseTextRenderer;
import org.vaadin.bugrap.util.ReportUtil;
import org.vaadin.bugrap.util.StringUtil;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;

public class ReportsModel extends BaseModel{

	private Project selectedProject;
	private ProjectVersion selectedVersion;
	private Binder<Report> reportBinder = new Binder<Report>();
	
	public ReportsModel(Navigator navigator) {
		super(navigator);
	}
	
	public Binder<Report> getBinder(){
		return reportBinder;
	}
	
	
	public void fillProjects(ComboBox<Project> combo) {
		List<Project> projects = new ArrayList<Project>();
		projects.addAll(getRepository().findProjects());
		Collections.sort(projects);
		combo.setItems(projects);
		combo.setSelectedItem(projects.get(0));
	}
	
	public void fillPriorties(ComboBox<Priority> combo) {
		combo.setItems(Priority.values());
	}
	
	public void fillTypes(ComboBox<Type> combo) {
		combo.setItems(Type.values());
		combo.setItemCaptionGenerator(type -> StringUtil.converToCamelCaseString(type.toString()));
	}
	
	public void fillStatuses(ComboBox<Status> combo) {
		combo.setItems(Status.values());
	}

	public void fillAssigned(ComboBox<Reporter> combo) {
//		List<Reporter> reporters = new ArrayList<Reporter>();
//		reporters.addAll();
//		Collections.sort(reporters);
		combo.setItems(getRepository().findReporters());
	}
	
	public void processProjectChange(Optional<Project> selectedItem, ComboBox<ProjectVersion> cmbVersion, ComboBox<ProjectVersion> cmbVersionEdit) {
		selectedProject = selectedItem.get();
		List<ProjectVersion> projectVersions = new ArrayList<ProjectVersion>();
		projectVersions.addAll(getRepository().findProjectVersions(selectedProject));
		Collections.sort(projectVersions);
		
		cmbVersion.setItems(projectVersions);
		cmbVersionEdit.setItems(projectVersions);
		cmbVersion.setSelectedItem(projectVersions.get(0));
	}

	public void processVersionChange(Optional<ProjectVersion> selectedItem, Grid<Report> gridReports) {
		boolean isVersionChanged = selectedVersion != selectedItem.get();
		Set<Report> selectedReportsToRemember = null;
		if (!isVersionChanged)
			selectedReportsToRemember = gridReports.getSelectedItems();
		
		selectedVersion = selectedItem.get();
		
		ReportsQuery query = new ReportsQuery();
		query.project = selectedProject;
		query.projectVersion = selectedVersion;
		
		List<Report> reports = new ArrayList<Report>();
		reports.addAll(getRepository().findReports(query));
		gridReports.setItems(reports);
		
		if (selectedReportsToRemember != null)
			for (Report seletedReport : selectedReportsToRemember) {
				gridReports.select(seletedReport);
			}
	}

	public void updateReport(Set<Report> reportsToUpdate) throws ValidationException {
		boolean isBulkMode = reportsToUpdate.size() > 1;
		for (Report report : reportsToUpdate) {
			if(isBulkMode)
				ReportUtil.setCommonFields(report, reportBinder.getBean());
			getRepository().save(report);
		}
	}

	public void revertChanges(Set<Report> selectedReports) {
		boolean isBulkMode = selectedReports.size() > 1;
		List<Report> originalReports = new ArrayList<Report>();
		for (Report report : selectedReports) {
			originalReports.add(getRepository().getReportById(report.getId()));
		}
		Report report = isBulkMode ? ReportUtil.getCommonFields(originalReports) : originalReports.get(0);
		reportBinder.setBean(report);
	}

	public void bulkSaveWithCommonFields(Set<Report> selectedReports, Report commonFields) {
		for (Report report : selectedReports) {
			ReportUtil.setCommonFields(report, commonFields);
			getRepository().save(report);
		}
	}





}
