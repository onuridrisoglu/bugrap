package org.vaadin.bugrap.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.vaadin.bugrap.ui.beans.ReportDistribution;
import org.vaadin.bugrap.util.ReportUtil;

import com.vaadin.data.ValidationException;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.VaadinSession;

public class ReportsModel extends BaseModel {

	public static final int SELECTIONMODE_NONE = 0;
	public static final int SELECTIONMODE_SINGLE = 1;
	public static final int SELECTIONMODE_MULTI = 2;
	
	public static final int ASSIGNEE_ME= 0;
	public static final int ASSIGNEE_ALL = 1;
	
	public static final int VERSIONID_ALL = 0;

	public static final String FILEUPLOAD_PATH = "/Users/onuridrisoglu/Downloads/temp/";

	private List<Report> selectedReports = new ArrayList<Report>();
	private List<Comment> uploadedFilesToSave = new ArrayList<Comment>();
//	private Map<String, Object> uploadingUIElements = new HashMap<String, Object>();
	protected Report reportForEdit;

	private int assigneeFilterMode = ASSIGNEE_ALL;
	private Set<Status> statusFilters = new HashSet<Status>();
	
	public ReportsModel(Navigator navigator, VaadinSession session) {
		super(navigator, session);
	}

	public List<Report> getSelectedReports() {
		return selectedReports;
	}

	public void setSelectedReports(Collection<Report> reports) {
		selectedReports.clear();
		selectedReports.addAll(reports);
		reportForEdit = ReportUtil.getCommonFields(selectedReports);
	}

	public int getSelectionMode() {
		if (selectedReports == null || selectedReports.size() == 0)
			return SELECTIONMODE_NONE;
		else if (selectedReports.size() == 1)
			return SELECTIONMODE_SINGLE;
		else
			return SELECTIONMODE_MULTI;
	}

	public List<Comment> getUploadedFilesToSave() {
		return uploadedFilesToSave;
	}

	public void setUploadedFilesToSave(List<Comment> uploadedFilesToSave) {
		this.uploadedFilesToSave = uploadedFilesToSave;
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

	public Collection<Status> getAllStatuses() {
		return Arrays.asList(Status.values());
	}

	public Collection<Reporter> findReporters() {
		return getRepository().findReporters();
	}

	public List<ProjectVersion> findProjectVersions() {
		return findProjectVersions(reportForEdit.getProject());
	}
	
	public List<ProjectVersion> findProjectVersionsWithAllOption(Project project) {
		List<ProjectVersion> projectVersions = findProjectVersions(project);
		
		ProjectVersion allVersion = new ProjectVersion();
		allVersion.setId(VERSIONID_ALL);
		allVersion.setVersion("All Versions");
		allVersion.setProject(project);
		
		projectVersions.add(0, allVersion);
		return projectVersions;
	}
	public List<ProjectVersion> findProjectVersions(Project project) {
		List<ProjectVersion> projectVersions = new ArrayList<ProjectVersion>();
		projectVersions.addAll(getRepository().findProjectVersions(project));
		Collections.sort(projectVersions);
		return projectVersions;
	}

	public Collection<Report> findReports(Project project, ProjectVersion version) {
		ReportsQuery query = new ReportsQuery();
		query.project = project;
		if (version.getId() != VERSIONID_ALL)
			query.projectVersion = version;
		query.reportAssignee = assigneeFilterMode == ASSIGNEE_ME ? getLoginUser() : null;
		query.reportStatuses = statusFilters;
		return getRepository().findReports(query);
	}

	public void saveReport() throws ValidationException {
		for (Report report : selectedReports) {
			ReportUtil.setFields(report, reportForEdit);
			getRepository().save(report);
		}
	}

	public void save() {
		Report report = getReportById(reportForEdit.getId());
		ReportUtil.setFields(report, reportForEdit);
		getRepository().save(report);
	}

	public Report getReportById(long reportId) {
		return getRepository().getReportById(reportId);
	}

	public List<Comment> getComments() {
		List<Comment> comments = new ArrayList<Comment>();
		if (getSelectionMode() != SELECTIONMODE_SINGLE)
			return comments; // No need to display comments if selection mode is not single
		comments.add(createCommentFromReportDescription(reportForEdit));
		comments.addAll(getRepository().findComments(reportForEdit));
		return comments;
	}

	private Comment createCommentFromReportDescription(Report report) {
		Comment comment = new Comment();
		comment.setTimestamp(report.getReportedTimestamp());
		comment.setAuthor(report.getAuthor());
		comment.setComment(report.getDescription());
		comment.setConsistencyVersion(report.getConsistencyVersion());
		comment.setReport(report);
		comment.setType(Comment.Type.COMMENT);
		return comment;
	}

	public Report getReportForEdit() {
		return reportForEdit;
	}

	public void resetReportForEdit() {
		reportForEdit = ReportUtil.getCommonFields(selectedReports);
	}

	public void saveComment(String commentTxt) {
		Comment comment = new Comment();
		comment.setReport(reportForEdit);
		comment.setComment(commentTxt);
		comment.setAuthor(getLoginUser());
		comment.setTimestamp(new Date());
		comment.setType(Comment.Type.COMMENT);
		getRepository().save(comment);
	}


	public Comment createComment(String filename, String mimeType, DownloadStream stream) throws IOException {
		Comment comment = new Comment();
		comment.setReport(reportForEdit);
		comment.setAttachment(stream.getStream().readAllBytes());
		comment.setAttachmentName(filename);
		comment.setAuthor(getLoginUser());
		comment.setTimestamp(new Date());
		comment.setType(Comment.Type.ATTACHMENT);
		return comment;
	}

	public void attachFile(String filename, String mimeType, DownloadStream stream) throws IOException {
		Comment attachmentComment = createComment(filename, mimeType, stream);
		uploadedFilesToSave.add(attachmentComment);
	}

	public void saveAttachments() {
		for (Comment attachment : uploadedFilesToSave) {
			getRepository().save(attachment);
		}
		uploadedFilesToSave.clear();
	}
	
	public ReportDistribution getReportDistribution(ProjectVersion version) {
		ReportDistribution distribution = new ReportDistribution();
		boolean isAllVersions = version.getId() == VERSIONID_ALL;
		distribution.setClosedReports(isAllVersions ? getRepository().countClosedReports(version.getProject()) : getRepository().countClosedReports(version));
		distribution.setAssignedReports(isAllVersions ? getRepository().countOpenedReports(version.getProject()) : getRepository().countOpenedReports(version));
		distribution.setUnassignedReports(isAllVersions ? getRepository().countUnassignedReports(version.getProject()) : getRepository().countUnassignedReports(version));
		return distribution;
	}

	public int getAssigneeFilterMode() {
		return assigneeFilterMode;
	}

	public void setAssigneeFilterMode(int assigneeFilterMode) {
		this.assigneeFilterMode = assigneeFilterMode;
	}

	public Set<Status> getStatusFilter() {
		return statusFilters;
	}

	public void setStatusFilters(Status...status) {
		statusFilters.clear();
		statusFilters.addAll(Arrays.asList(status));
	}

	public void changeStatusFilters(boolean isChecked, Status...status) {
		if (isChecked)
			statusFilters.addAll(Arrays.asList(status));
		else
			statusFilters.removeAll(Arrays.asList(status));
	}
}
