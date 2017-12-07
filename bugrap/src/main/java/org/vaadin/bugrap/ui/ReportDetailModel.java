package org.vaadin.bugrap.ui;

import java.util.Date;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Comment.Type;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.navigator.Navigator;

public class ReportDetailModel extends ReportsModel{
	
	public ReportDetailModel(Navigator nav) {
		super(nav);
	}

	public String getReportProjectAndVersion(Report r) {
		return r.getProject().getName() + " > " +r.getVersion().getVersion();
	}

	public void returnToReportList() {
		getNavigator().navigateTo(NAV_REPORT);
	}

	public void saveComment(String commentTxt, Reporter author, Report report) {
		Comment comment = new Comment();
		comment.setReport(report);
		comment.setComment(commentTxt);
		comment.setAuthor(author);
		comment.setTimestamp(new Date());
		comment.setType(Type.COMMENT);
		getRepository().save(comment);
	}

}
