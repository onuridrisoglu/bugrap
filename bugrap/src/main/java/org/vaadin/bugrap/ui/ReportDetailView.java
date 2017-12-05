package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.generated.ReportDetailViewBase;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

public class ReportDetailView extends ReportDetailViewBase implements View{

	private ReportDetailModel model;
	
	public ReportDetailView(ReportDetailModel rdm) {
		model = rdm;
		initializeBinder();
	}
	
	private void initializeBinder() {
		model.getBinder().bind(cmbEditPriority, Report::getPriority, Report::setPriority);
		model.getBinder().bind(cmbEditType, Report::getType, Report::setType);
		model.getBinder().bind(cmbEditStatus, Report::getStatus, Report::setStatus);
		model.getBinder().bind(cmbEditAssignedTo, Report::getAssigned, Report::setAssigned);
		model.getBinder().bind(cmbEditVersion, Report::getVersion, Report::setVersion);
		model.getBinder().bind(txtDescription, Report::getDescription, Report::setDescription);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		long reportId = Long.parseLong(event.getParameterMap().get("reportId"));
		init(reportId);
	}

	private void init(long reportId) {
		model.setReport(reportId);
		lblProjectVersionName.setValue(model.getReportProjectAndVersion());
		lblProjectSummary.setValue(model.getBinder().getBean().getSummary());
	}
	
}
