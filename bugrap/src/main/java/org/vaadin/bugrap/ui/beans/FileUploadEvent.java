package org.vaadin.bugrap.ui.beans;

import java.io.InputStream;

public class FileUploadEvent {

	private String filename;
	private String mimeType;
	private Object source;
	private InputStream stream;

	public FileUploadEvent(Object source, String filename, String mimeType, InputStream stream) {
		this.filename = filename;
		this.mimeType = mimeType;
		this.source = source;
		this.stream = stream;
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

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}
}
