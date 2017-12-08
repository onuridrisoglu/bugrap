package org.vaadin.bugrap.ui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.generated.ReportDetailViewBase;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

public class ReportDetailView extends ReportDetailViewBase implements View, Receiver, SucceededListener, StartedListener, ProgressListener{

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
		
		btnUpload.setReceiver(this);
		btnUpload.addStartedListener(this);
		btnUpload.addSucceededListener(this);
		btnUpload.addProgressListener(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		long reportId = Long.parseLong(event.getParameterMap().get("reportId"));
		init(reportId);
		((HorizontalLayout)pnlAttachments.getContent()).removeAllComponents();
		pnlAttachments.setVisible(false);
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


	@Override
	public void uploadStarted(StartedEvent event) {
		pnlAttachments.setVisible(true);
		HorizontalLayout layout = (HorizontalLayout) pnlAttachments.getContent();
		ProgressBar progress = new ProgressBar();
		progress.setCaption(event.getFilename());
		model.getAttachmentUIElements().put(event.getFilename(), progress);
		layout.addComponent(progress);
	}


	@Override
	public void uploadSucceeded(SucceededEvent event) {
		Notification.show("Uploaded", Type.TRAY_NOTIFICATION);
		ProgressBar progress = (ProgressBar) model.getAttachmentUIElements().get(event.getFilename());
		HorizontalLayout layout = (HorizontalLayout) pnlAttachments.getContent();
		layout.removeComponent(progress);
		
		Button button = new Button(event.getFilename());
		button.setStyleName("tiny borderless-colored");
		layout.addComponent(button);
		model.getAttachmentUIElements().put(event.getFilename(), button);
	}


	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("/Users/onuridrisoglu/Downloads/temp/"+filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			return fos;
		}
	}


	@Override
	public void updateProgress(long readBytes, long contentLength) {
		ProgressBar bar = null;
		for (Object obj : model.getAttachmentUIElements().values()) {
			if (obj instanceof ProgressBar)
				bar = (ProgressBar)obj;
		}
		if (bar != null)
			bar.setValue(((float)readBytes)/contentLength);
	}
	
}
