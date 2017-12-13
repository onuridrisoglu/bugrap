package org.vaadin.addon.report_distribution_bar;

import org.vaadin.addon.report_distribution_bar.client.ReportDistributionBarState;

// This is the server-side UI component that provides public API 
// for ReportDistributionBar
public class ReportDistributionBar extends com.vaadin.ui.AbstractComponent {

	public void setDistributions(long closed, long assigned, long unassigned) {
    		getState().closed = closed;
    		getState().assigned = assigned;
    		getState().unassigned = unassigned;
    		getState().opened = closed + assigned + unassigned;
    }

    // We must override getState() to cast the state to ReportDistributionBarState
    @Override
    protected ReportDistributionBarState getState() {
        return (ReportDistributionBarState) super.getState();
    }
}
