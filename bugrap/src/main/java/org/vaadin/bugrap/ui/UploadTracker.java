package org.vaadin.bugrap.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.vaadin.bugrap.ui.beans.FileUploadEvent;
import org.vaadin.bugrap.ui.generated.UploadTrackerBase;
import org.vaadin.bugrap.util.IUploadedFileListener;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class UploadTracker extends UploadTrackerBase implements Receiver, ProgressListener, SucceededListener {

	private Upload btnUpload;
	private IUploadedFileListener uploadedFileListener;
	private UploadTrackerModel model = new UploadTrackerModel();
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public UploadTracker(Upload upload, String filename) {
		btnUpload = upload;
		btnUpload.setReceiver(this);
		btnUpload.addProgressListener(this);
		btnUpload.addSucceededListener(this);
		uploadProgress.setIndeterminate(false);
		lnkFilename.setCaption(filename);
		btnCancel.addClickListener(evt -> cancel());
		arrangeComponents();
	}

	private void arrangeComponents() {
		uploadProgress.setVisible(!model.isUploadCompleted());
		if (model.isUploadCompleted()) {
			setStyleName("completed-upload", true);
			btnUpload.setVisible(false);
		}
	}

	private void cancel() {
		if (!model.isUploadCompleted()) {
			btnUpload.interruptUpload();
		}
		uploadedFileListener.uploadedFileDeleted(btnUpload);
		HorizontalLayout layout = (HorizontalLayout) getParent();
		layout.removeComponent(this);
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		StreamResource resource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				return is;
			}
		}, event.getFilename());
		FileDownloader downloader = new FileDownloader(resource);
		downloader.extend(lnkFilename);
		model.setUploadCompleted(true);
		arrangeComponents();
		uploadedFileListener.uploadedFileReceived(
				new FileUploadEvent(event.getSource(), event.getFilename(), event.getMIMEType(), is));
	}

	@Override
	public void updateProgress(long readBytes, long contentLength) {
		uploadProgress.setValue(((float) readBytes) / (float) contentLength);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		return baos;
	}

	public IUploadedFileListener getUploadedFileListener() {
		return uploadedFileListener;
	}

	public void setUploadedFileListener(IUploadedFileListener uploadedFileListener) {
		this.uploadedFileListener = uploadedFileListener;
	}
}
