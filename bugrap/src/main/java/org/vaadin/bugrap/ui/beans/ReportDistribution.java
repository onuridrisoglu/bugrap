package org.vaadin.bugrap.ui.beans;

public class ReportDistribution {

	private long closedReports;
	private long assignedReports;
	private long unassignedReports;
	
	public long getClosedReports() {
		return closedReports;
	}
	public void setClosedReports(long closedReports) {
		this.closedReports = closedReports;
	}
	public long getUnassignedReports() {
		return unassignedReports;
	}
	public void setUnassignedReports(long unassignedReports) {
		this.unassignedReports = unassignedReports;
	}
	public long getAssignedReports() {
		return assignedReports;
	}
	public void setAssignedReports(long assignedReports) {
		this.assignedReports = assignedReports;
	}
}
