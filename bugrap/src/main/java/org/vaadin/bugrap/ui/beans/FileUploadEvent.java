package org.vaadin.bugrap.ui.beans;

import com.vaadin.server.DownloadStream;
import com.vaadin.ui.Upload;

public class FileUploadEvent {
	
	private String filename;
	private String mimeType;
	private DownloadStream downloadStream;
	private Object source;
	
	public FileUploadEvent(Object source, String filename, String mimeType, DownloadStream downloadStream) {
		this.filename = filename;
		this.mimeType = mimeType;
		this.downloadStream = downloadStream;
		this.setSource(source);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public DownloadStream getDownloadStream() {
		return downloadStream;
	}

	public void setDownloadStream(DownloadStream downloadStream) {
		this.downloadStream = downloadStream;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}
}
