package org.vaadin.bugrap.ui;

import java.io.IOException;
import java.util.List;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.beans.FileUploadEvent;
import org.vaadin.bugrap.ui.generated.ReportDetailViewBase;
import org.vaadin.bugrap.util.IUploadedFileListener;

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ReportDetailView extends ReportDetailViewBase
		implements StartedListener, Component.Listener, IUploadedFileListener, FinishedListener {

	private ReportsModel model;
	private Binder<Report> binder = new Binder<Report>();
	private int numOfOngoingUploads = 0;

	public ReportDetailView(ReportsModel reportModel) {
		model = reportModel;
		initializeBinder();
		initializeUIComponents();
		init();
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
		btnCancel.addClickListener(evt -> closeWindow());
		btnUpload.addStartedListener(this);
		btnUpload.addFinishedListener(this);
		txtComment.addValueChangeListener(evt -> setDoneButtonEnablement());
		pnlAttachments.getContent().addListener(this);
	}

	private void init() {
		Report report = model.getReportForEdit();
		binder.setBean(report);
		lblProjectSummary.setValue(report.getSummary());
		initializeComboContents();
		fillComments();
		cleanAttachments();
	}

	private void cleanAttachments() {
		((HorizontalLayout) pnlAttachments.getContent()).removeAllComponents();
	}

	private void initializeComboContents() {
		cmbEditPriority.setItems(model.getPriorties());
		cmbEditType.setItems(model.getTypes());
		cmbEditStatus.setItems(model.getAllStatuses());
		cmbEditAssignedTo.setItems(model.findReporters());
		cmbEditVersion.setItems(model.findProjectVersions());
	}

	private void fillComments() {
		VerticalLayout layout = (VerticalLayout) pnlCommentsThread.getContent();
		layout.removeAllComponents();
		List<Comment> comments = model.getComments();
		for (Comment comment : comments) {
			layout.addComponent(new ThreadItemView(new ThreadItemModel(comment)));
		}
		txtComment.clear();
		btnDone.setEnabled(false);
	}

	private void setDoneButtonEnablement() {
		boolean isEnabled = false;
		if (numOfOngoingUploads > 0)
			isEnabled = false;
		else 
			isEnabled = !txtComment.isEmpty() || model.hasFilesToSave();
		btnDone.setEnabled(isEnabled);
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
		if (!txtComment.isEmpty())
			model.saveComment(txtComment.getValue());
		model.saveAttachments();
		fillComments();
		cleanAttachments();
	}

	@Override
	public void uploadStarted(StartedEvent event) {
		initiateUploadTracker(event);
		addNewUpload();
		numOfOngoingUploads++;
	}

	private void addNewUpload() {
		HorizontalLayout layout = (HorizontalLayout) btnUpload.getParent();
		Upload newUpload = new Upload();
		newUpload.setButtonCaption("Attachments...");
		newUpload.addStartedListener(this);
		newUpload.addFinishedListener(this);
		layout.addComponent(newUpload, layout.getComponentIndex(btnUpload));
		btnUpload.setStyleName("removed-upload", true);
		btnUpload = newUpload;
	}

	private void initiateUploadTracker(StartedEvent event) {
		UploadTracker uploadTracker = new UploadTracker((Upload) event.getSource(), event.getFilename());
		uploadTracker.setUploadedFileListener(this);
		HorizontalLayout layout = (HorizontalLayout) pnlAttachments.getContent();
		layout.addComponent(uploadTracker);
	}

	private void closeWindow() {
		Window window = (Window) getParent();
		window.close();
	}

	@Override
	public void componentEvent(Event event) {
		HorizontalLayout layout = (HorizontalLayout) event.getSource();
		pnlAttachments.setVisible(layout.getComponentCount() > 0);
	}

	@Override
	public void uploadedFileReceived(FileUploadEvent event) {
		try {
			model.attachFile(event.getSource(), event.getFilename(), event.getFilename(), event.getDownloadStream());
			setDoneButtonEnablement();
		} catch (IOException e) {
			Notification.show("Error uploading file : " + event.getFilename(),
					"There was an error while uploading the file :" + e.getMessage(),
					com.vaadin.ui.Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void uploadFinished(FinishedEvent event) {
		numOfOngoingUploads--;
		setDoneButtonEnablement();
	}

	@Override
	public void uploadedFileDeleted(Object source) {
		model.removeAttachedFile(source);
		setDoneButtonEnablement();
	}
}
