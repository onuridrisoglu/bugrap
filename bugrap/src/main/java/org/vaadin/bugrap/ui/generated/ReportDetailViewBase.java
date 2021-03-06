package org.vaadin.bugrap.ui.generated;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class ReportDetailViewBase extends VerticalLayout {
	protected Label lblProjectSummary;
	protected ComboBox<org.vaadin.bugrap.domain.entities.Report.Priority> cmbEditPriority;
	protected ComboBox<org.vaadin.bugrap.domain.entities.Report.Type> cmbEditType;
	protected ComboBox<org.vaadin.bugrap.domain.entities.Report.Status> cmbEditStatus;
	protected ComboBox<org.vaadin.bugrap.domain.entities.Reporter> cmbEditAssignedTo;
	protected ComboBox<org.vaadin.bugrap.domain.entities.ProjectVersion> cmbEditVersion;
	protected Button btnUpdateReport;
	protected Button btnRevertReport;
	protected Panel pnlCommentsThread;
	protected TextArea txtComment;
	protected Panel pnlAttachments;
	protected Button btnDone;
	protected Upload btnUpload;
	protected Button btnCancel;

	public ReportDetailViewBase() {
		Design.read(this);
	}
}
