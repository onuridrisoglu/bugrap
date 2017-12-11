package org.vaadin.bugrap.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.ui.generated.ReportDetailViewBase;

import com.vaadin.data.Binder;
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

public class ReportDetailView extends ReportDetailViewBase implements Receiver, SucceededListener, StartedListener, ProgressListener{


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
	}

	private void init() {
		((HorizontalLayout)pnlAttachments.getContent()).removeAllComponents();
		pnlAttachments.setVisible(false);
		Report report = model.getReportForEdit();
		binder.setBean(model.getReportForEdit());
		lblProjectSummary.setValue(report.getSummary());
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
		if (!txtComment.getOptionalValue().isPresent())
			return;
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
		ProgressBar progress = (ProgressBar) model.getAttachmentUIElements().get(event.getFilename());
		HorizontalLayout layout = (HorizontalLayout) pnlAttachments.getContent();
		layout.removeComponent(progress);
		
		FileResource file = new FileResource(new File(ReportsModel.FILEUPLOAD_PATH + event.getFilename()));
		Link link = new Link(event.getFilename(), file);
		link.setStyleName("tiny");
		layout.addComponent(link);
		model.getAttachmentUIElements().put(event.getFilename(), link);
		try {
			model.saveAttachment(event.getFilename(), event.getMIMEType(), file.getStream());
		} catch (IOException e) {
			Notification.show("Error occurred while uploading the file", e.getMessage(), Type.ERROR_MESSAGE);
		}
		//Refresh comments after new comment;
		fillComments();
	}
	

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(ReportsModel.FILEUPLOAD_PATH + filename);
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
	
	private void closeWindow() {
		Window window = (Window)getParent();
		window.close();
	}
	
}
