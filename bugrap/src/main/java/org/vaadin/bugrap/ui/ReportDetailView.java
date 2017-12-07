package org.vaadin.bugrap.ui;

import java.util.List;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.generated.ReportDetailViewBase;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class ReportDetailView extends ReportDetailViewBase implements View{

	private ReportDetailModel model;
	private Binder<Report> binder = new Binder<Report>();
	
	public ReportDetailView(ReportDetailModel rdm) {
		model = rdm;
		initializeBinder();
		initializeUIComponents();
	}
	

	private void initializeBinder() {
		binder.bind(cmbEditPriority, Report::getPriority, Report::setPriority);
		binder.bind(cmbEditType, Report::getType, Report::setType);
		binder.bind(cmbEditStatus, Report::getStatus, Report::setStatus);
		binder.bind(cmbEditAssignedTo, Report::getAssigned, Report::setAssigned);
		binder.bind(cmbEditVersion, Report::getVersion, Report::setVersion);
//		binder.bind(txtDescription, Report::getDescription, Report::setDescription);
	}

	private void initializeUIComponents() {
		btnUpdateReport.addClickListener(evt -> saveReport());
		btnRevertReport.addClickListener(evt -> revertChanges());
		btnDone.addClickListener(evt -> saveComment());
		btnCancel.addClickListener(evt -> model.returnToReportList());
//		btnUploadAttachments.setReceiver(this);
//		btnUploadAttachments.addStartedListener(this);
//		btnUploadAttachments.addFinishedListener(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		long reportId = Long.parseLong(event.getParameterMap().get("reportId"));
		init(reportId);
	}

	private void init(long reportId) {
		Report report = model.getReportById(reportId); 
		binder.setBean(report);
		lblProjectVersionName.setValue(model.getReportProjectAndVersion(report));
		lblProjectSummary.setValue(report.getSummary());
		initializeComboContents(report.getProject());
		fillComments(model.getComments(reportId));
	}
	
	private void initializeComboContents(Project project) {
		cmbEditPriority.setItems(model.getPriorties());
		cmbEditType.setItems(model.getTypes());
		cmbEditStatus.setItems(model.getStatuses());
		cmbEditAssignedTo.setItems(model.findReporters());
		cmbEditVersion.setItems(model.findProjectVersions(project));
	}
	
	private void fillComments(List<Comment> comments) {
		VerticalLayout layout = (VerticalLayout) pnlCommentsThread.getContent();
		layout.removeAllComponents();
		
		for (Comment comment : comments) {
			layout.addComponent(new ThreadItem(comment));
		}
	}
	
	
	private void saveReport() {
		model.save(binder.getBean());
		Notification.show("Saved successfully", Type.TRAY_NOTIFICATION);
	}
	
	private void revertChanges() {
		long reportId = binder.getBean().getId();
		binder.setBean(model.getReportById(reportId));
	}

	private void saveComment() {
		model.saveComment(txtComment.getValue(), BaseModel.loginUser, binder.getBean());
		fillComments(model.getComments(binder.getBean().getId()));
		txtComment.setValue(null);
	}
	
}
