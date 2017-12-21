package org.vaadin.bugrap.ui;

public class UploadTrackerModel {
	public static final String FILEUPLOAD_PATH = "/Users/onuridrisoglu/Downloads/temp/";

	private boolean isUploadCompleted = false;
	
	public boolean isUploadCompleted() {
		return isUploadCompleted;
	}
	public void setUploadCompleted(boolean isUploadCompleted) {
		this.isUploadCompleted = isUploadCompleted;
	}
}
