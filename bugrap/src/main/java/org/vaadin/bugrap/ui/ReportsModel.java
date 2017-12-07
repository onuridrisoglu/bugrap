package org.vaadin.bugrap.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.BugrapRepository.ReportsQuery;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Report.Type;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.bugrap.util.ReportUtil;

import com.vaadin.data.ValidationException;
import com.vaadin.navigator.Navigator;

public class ReportsModel extends BaseModel{

	public static final int SELECTIONMODE_NONE 	= 0;
	public static final int SELECTIONMODE_SINGLE = 1;
	public static final int SELECTIONMODE_MULTI 	= 2;
	
	private List<Report> selectedReports = new ArrayList<Report>();
	
	public ReportsModel(Navigator navigator) {
		super(navigator);
	}
	
	public List<Report> getSelectedReports() {
		return selectedReports;
	}

	public void setSelectedReports(Collection<Report> reports) {
		selectedReports.clear();
		selectedReports.addAll(reports);
	}
	
	public int getSelectionMode() {
		if (selectedReports == null || selectedReports.size() == 0)
			return SELECTIONMODE_NONE;
		else if (selectedReports.size() == 1)
			return SELECTIONMODE_SINGLE;
		else
			return SELECTIONMODE_MULTI;
	}
	
	public List<Project> findProjects() {
		List<Project> projects = new ArrayList<Project>();
		projects.addAll(getRepository().findProjects());
		Collections.sort(projects);
		return projects;
	}
	
	public Collection<Priority> getPriorties() {
		return Arrays.asList(Priority.values());
	}
	public Collection<Type> getTypes() {
		return Arrays.asList(Type.values());
	}
	public Collection<Status> getStatuses() {
		return Arrays.asList(Status.values());
	}
	
	public Collection<Reporter> findReporters() {
		return getRepository().findReporters();
	}
	
	public List<ProjectVersion> findProjectVersions(Project selectedProject) {
		List<ProjectVersion> projectVersions = new ArrayList<ProjectVersion>();
		projectVersions.addAll(getRepository().findProjectVersions(selectedProject));
		Collections.sort(projectVersions);
		return projectVersions;
	}
	
	public Collection<Report> findReports(Project project, ProjectVersion version){
		ReportsQuery query = new ReportsQuery();
		query.project = project;
		query.projectVersion = version;
		return getRepository().findReports(query);
	}

	public void saveReport(Report changedCopy) throws ValidationException {
		for (Report report : selectedReports) {
			ReportUtil.setFields(report, changedCopy);
			getRepository().save(report);
		}
	}

	public Report getReportById(long reportId) {
		return getRepository().getReportById(reportId);
	}

	public Report getOriginalCopyForBinder() {
		return ReportUtil.getCommonFields(selectedReports);
	}
}
