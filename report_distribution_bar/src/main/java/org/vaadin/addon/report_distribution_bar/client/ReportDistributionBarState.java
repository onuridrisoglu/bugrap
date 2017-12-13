package org.vaadin.addon.report_distribution_bar.client;

public class ReportDistributionBarState extends com.vaadin.shared.AbstractComponentState {

    // State can have both public variable and bean properties
    public long closed = 0;
    public long assigned = 0;
    public long unassigned = 0;
    public long opened = 100;

}