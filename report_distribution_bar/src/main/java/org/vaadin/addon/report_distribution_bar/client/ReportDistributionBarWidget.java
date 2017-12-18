package org.vaadin.addon.report_distribution_bar.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

// Extend any GWT Widget
public class ReportDistributionBarWidget extends Composite {

	private Label closedLabel = new Label("");
	private Label assignedLabel = new Label("");
	private Label unassignedLabel = new Label("");
	private HorizontalPanel panel = new HorizontalPanel();
	
    public ReportDistributionBarWidget() {
	    	initWidget(panel);

	    	panel.add(closedLabel);
    		panel.add(assignedLabel);
    		panel.add(unassignedLabel);
    		panel.setStyleName("distribution_panel");
    		
    		closedLabel.setStyleName("first closed report");
    		assignedLabel.setStyleName("assigned report");
    		unassignedLabel.setStyleName("last unassigned report");
    }

	public void setClosedAmount(long closed, long total) {
		closedLabel.setText(""+closed);
		panel.setCellWidth(closedLabel, getWidth(closed, total));
	}

	public void setAssignedAmount(long assigned, long total) {
		assignedLabel.setText(""+assigned);
		panel.setCellWidth(assignedLabel, getWidth(assigned, total));
	}

	public void setUnassignedAmount(long unassigned, long total) {
		unassignedLabel.setText(""+unassigned);
		panel.setCellWidth(unassignedLabel, getWidth(unassigned, total));
	}

	private String getWidth(long num, long denom) {
		long width = (num * 90)/denom;
		return width + "%";
	}
}