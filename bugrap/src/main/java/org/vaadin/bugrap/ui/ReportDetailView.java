package org.vaadin.bugrap.ui;

import java.util.List;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Comment;
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
		model.setReportForEdit(reportId);
		binder.setBean(model.getReportForEdit());
		lblProjectVersionName.setValue(model.getReportProjectAndVersion());
		lblProjectSummary.setValue(model.getReportForEdit().getSummary());
		initializeComboContents();
		fillComments();
	}
	
	private void initializeComboContents() {
		cmbEditPriority.setItems(model.getPriorties());
		cmbEditType.setItems(model.getTypes());
		cmbEditStatus.setItems(model.getStatuses());
		cmbEditAssignedTo.setItems(model.findReporters());
		cmbEditVersion.setItems(model.findProjectVersions(model.getReportForEdit().getProject()));
	}
	
	private void fillComments() {
		VerticalLayout layout = (VerticalLayout) pnlCommentsThread.getContent();
		layout.removeAllComponents();
		List<Comment> comments = model.getComments();
		for (Comment comment : comments) {
			layout.addComponent(new ThreadItemView(new ThreadItemModel(comment)));
		}
	}
	
	
	private void saveReport() {
		model.save();
		Notification.show("Saved successfully", Type.TRAY_NOTIFICATION);
	}
	
	private void revertChanges() {
		model.resetReportForEdit();
		binder.setBean(model.getReportForEdit());
	}

	private void saveComment() {
		model.saveComment(txtComment.getValue(), BaseModel.loginUser);
		fillComments();
		txtComment.clear();
	}
	
}
