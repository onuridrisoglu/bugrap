package org.vaadin.bugrap.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.generated.ReportDetailViewBase;

import com.vaadin.data.Binder;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
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
import com.vaadin.ui.Window;

public class ReportDetailView extends ReportDetailViewBase
		implements Receiver, SucceededListener, StartedListener, ProgressListener {

	private ReportsModel model;
	private Binder<Report> binder = new Binder<Report>();

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
		btnUpload.setReceiver(this);
		btnUpload.addStartedListener(this);
		btnUpload.addSucceededListener(this);
		btnUpload.addProgressListener(this);
		txtComment.addValueChangeListener(evt -> commentsUpdated());
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
		pnlAttachments.setVisible(false);
	}

	private void initializeComboContents() {
		cmbEditPriority.setItems(model.getPriorties());
		cmbEditType.setItems(model.getTypes());
		cmbEditStatus.setItems(model.getStatuses());
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

	private void commentsUpdated() {
		btnDone.setEnabled(true);
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
		pnlAttachments.setVisible(true);
		HorizontalLayout layout = (HorizontalLayout) pnlAttachments.getContent();
		ProgressBar progress = new ProgressBar();
		progress.setCaption(event.getFilename());
		model.getUploadingUIElements().put(event.getFilename(), progress);
		layout.addComponent(progress);
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		ProgressBar progress = (ProgressBar) model.getUploadingUIElements().remove(event.getFilename());
		HorizontalLayout layout = (HorizontalLayout) pnlAttachments.getContent();
		layout.removeComponent(progress);
		FileResource file = new FileResource(new File(ReportsModel.FILEUPLOAD_PATH + event.getFilename()));
		try {
			model.attachFile(event.getFilename(), event.getMIMEType(), file.getStream());
		} catch (IOException e) {
			Notification.show("Error occurred while uploading the file", e.getMessage(), Type.ERROR_MESSAGE);
		}
		FileDownloader downloader = new FileDownloader(file);
		Link link = new Link();
		link.setCaption(event.getFilename());
		link.setStyleName("tiny");
		downloader.extend(link);
		layout.addComponent(link);
		commentsUpdated();
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(ReportsModel.FILEUPLOAD_PATH + filename);
		} catch (FileNotFoundException e) {
			Notification.show("Error occurred while uploading the file", e.getMessage(), Type.ERROR_MESSAGE);
		} finally {
			return fos;
		}
	}

	@Override
	public void updateProgress(long readBytes, long contentLength) {
		ProgressBar bar = null;
		for (Object obj : model.getUploadingUIElements().values()) {
			bar = (ProgressBar) obj;
		}
		if (bar != null)
			bar.setValue(((float) readBytes) / contentLength);
	}

	private void closeWindow() {
		Window window = (Window) getParent();
		window.close();
	}
}
