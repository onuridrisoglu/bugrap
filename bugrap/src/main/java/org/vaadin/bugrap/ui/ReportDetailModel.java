package org.vaadin.bugrap.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Comment.Type;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.navigator.Navigator;

public class ReportDetailModel extends ReportsModel{
	
	private Map<String, Object> attachmentUIElements = new HashMap<String, Object>();
	
	public ReportDetailModel(Navigator nav) {
		super(nav);
	}
	
	public void setReportForEdit(long reportId) {
		reportForEdit = getReportById(reportId);
		getSelectedReports().clear();
		getSelectedReports().add(reportForEdit);
	}

	public String getReportProjectAndVersion() {
		return reportForEdit.getProject().getName() + " > " +reportForEdit.getVersion().getVersion();
	}

	public void returnToReportList() {
		getNavigator().navigateTo(NAV_REPORT);
	}

	public void saveComment(String commentTxt, Reporter author) {
		Comment comment = new Comment();
		comment.setReport(reportForEdit);
		comment.setComment(commentTxt);
		comment.setAuthor(author);
		comment.setTimestamp(new Date());
		comment.setType(Type.COMMENT);
		getRepository().save(comment);
	}

	public Map<String, Object> getAttachmentUIElements() {
		return attachmentUIElements;
	}
}
