package org.vaadin.bugrap.util;

import org.vaadin.bugrap.ui.beans.FileUploadEvent;

public interface IUploadedFileListener {

	public void uploadedFileReceived(FileUploadEvent event);
	
	public void uploadedFileDeleted(Object source);
}
