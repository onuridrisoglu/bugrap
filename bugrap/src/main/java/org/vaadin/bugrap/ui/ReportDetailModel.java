package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.data.Binder;
import com.vaadin.navigator.Navigator;

public class ReportDetailModel extends BaseModel{
	
	private Binder<Report> binder;
	
	public ReportDetailModel(Navigator nav) {
		super(nav);
		binder = new Binder<Report>();
	}

	public Binder<Report> getBinder(){
		return binder;
	}

	public void setReport(long reportId) {
		Report report = getRepository().getReportById(reportId);
		binder.setBean(report);
	}

	public String getReportProjectAndVersion() {
		return binder.getBean().getProject().getName() + " > " + binder.getBean().getVersion().getVersion();
	}
}
