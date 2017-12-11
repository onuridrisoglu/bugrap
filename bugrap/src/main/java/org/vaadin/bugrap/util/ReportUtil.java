package org.vaadin.bugrap.util;

import java.util.Collection;

import org.vaadin.bugrap.domain.entities.Report;

public class ReportUtil {

	public static Report getCommonFields(Collection<Report> reports) {
		Report common = null;
		for (Report report : reports) {
			if (common == null) {
				common = createReportFrom(report);
				continue;
			}
			if (common.getPriority() != null && !common.getPriority().equals(report.getPriority()))
				common.setPriority(null);
			if (common.getType() != null && !common.getType().equals(report.getType()))
				common.setType(null);
			if (common.getStatus() != null && !common.getStatus().equals(report.getStatus()))
				common.setStatus(null);
			if (common.getAssigned() != null && !common.getAssigned().equals(report.getAssigned()))
				common.setAssigned(null);
			if (common.getVersion() != null && !common.getVersion().equals(report.getVersion()))
				common.setVersion(null);
		}
		if (reports.size() > 1)
			common.setSummary("<b>" + reports.size() + " reports selected</b> - Select single report to view contents");
		return common;
	}

	public static Report createReportFrom(Report r) {
		Report copy = new Report();
		copy.setId(r.getId());
		copy.setPriority(r.getPriority());
		copy.setType(r.getType());
		copy.setStatus(r.getStatus());
		copy.setAssigned(r.getAssigned());
		copy.setVersion(r.getVersion());
		copy.setSummary(r.getSummary());
		copy.setDescription(r.getDescription());
		copy.setReportedTimestamp(r.getReportedTimestamp());
		copy.setAuthor(r.getAuthor());
		copy.setProject(r.getProject());
		copy.setVersion(r.getVersion());
		return copy;
	}

	public static void setFields(Report report, Report copiedReport) {
		if (copiedReport.getPriority() != null)
			report.setPriority(copiedReport.getPriority());
		if (copiedReport.getType() != null)
			report.setType(copiedReport.getType());
		if (copiedReport.getStatus() != null)
			report.setStatus(copiedReport.getStatus());
		if (copiedReport.getAssigned() != null)
			report.setAssigned(copiedReport.getAssigned());
		if (copiedReport.getVersion() != null)
			report.setVersion(copiedReport.getVersion());

	}
}
