package org.vaadin.bugrap.util;

import java.util.Collection;
import java.util.Set;

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
		common.setSummary("<b>"+reports.size() + " reports selected</b> - Select single report to view contents");
		return common;
	}

	private static Report createReportFrom(Report r) {
		Report copy = new Report();
		copy.setPriority(r.getPriority());
		copy.setType(r.getType());
		copy.setStatus(r.getStatus());
		copy.setAssigned(r.getAssigned());
		copy.setVersion(r.getVersion());
		return copy;
	}

	public static void setCommonFields(Report report, Report commonFields) {
		if (commonFields.getPriority() != null)
			report.setPriority(commonFields.getPriority());
		if (commonFields.getType() != null)
			report.setType(commonFields.getType());
		if (commonFields.getStatus() != null)
			report.setStatus(commonFields.getStatus());
		if (commonFields.getAssigned() != null)
			report.setAssigned(commonFields.getAssigned());
		if (commonFields.getVersion() != null)
			report.setVersion(commonFields.getVersion());
		
	}
}
