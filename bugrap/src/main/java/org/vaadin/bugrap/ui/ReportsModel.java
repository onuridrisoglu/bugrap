package org.vaadin.bugrap.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.BugrapRepository.ReportsQuery;
import org.vaadin.bugrap.domain.entities.Comment;
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

	public ReportsModel(Navigator navigator) {
		super(navigator);
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

	public void saveReport(Collection<Report> selectedReports, Report changedCopy) throws ValidationException {
		for (Report report : selectedReports) {
			ReportUtil.setFields(report, changedCopy);
			save(report);
		}
	}
	
	public void save(Report report) {
		getRepository().save(report);
	}

	public Report getReportById(long reportId) {
		return getRepository().getReportById(reportId);
	}

	public void openReportDetail(long reportId) {
		getNavigator().navigateTo(NAV_REPORTDET + "/reportId="+ reportId);
	}
	
	public List<Comment> getComments(long reportId) {
		List<Comment> comments = new ArrayList<Comment>();
		Report report = getReportById(reportId);
		comments.add(createCommentFromReportDescription(report));
		comments.addAll(getRepository().findComments(report));
		return comments;
	}

	private Comment createCommentFromReportDescription(Report report) {
		Comment comment = new Comment();
		comment.setTimestamp(report.getReportedTimestamp());
		comment.setAuthor(report.getAuthor());
		comment.setComment(report.getDescription());
		comment.setConsistencyVersion(report.getConsistencyVersion());
		comment.setReport(report);
		comment.setType(org.vaadin.bugrap.domain.entities.Comment.Type.COMMENT);
		return comment;
	}

}
