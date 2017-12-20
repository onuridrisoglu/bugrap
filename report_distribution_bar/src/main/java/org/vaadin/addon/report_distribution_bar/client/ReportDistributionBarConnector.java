package org.vaadin.addon.report_distribution_bar.client;

import org.vaadin.addon.report_distribution_bar.ReportDistributionBar;

import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@Connect(ReportDistributionBar.class)
public class ReportDistributionBarConnector extends AbstractComponentConnector {

    // ServerRpc is used to send events to server. Communication implementation
    // is automatically created here
    ReportDistributionBarServerRpc rpc = RpcProxy.create(ReportDistributionBarServerRpc.class, this);

    public ReportDistributionBarConnector() {
        
    }

    // We must implement getWidget() to cast to correct type 
    // (this will automatically create the correct widget type)
    @Override
    public ReportDistributionBarWidget getWidget() {
        return (ReportDistributionBarWidget) super.getWidget();
    }

    // We must implement getState() to cast to correct type
    @Override
    public ReportDistributionBarState getState() {
        return (ReportDistributionBarState) super.getState();
    }

    // Whenever the state changes in the server-side, this method is called
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        
        ReportDistributionBarState state = getState();
        getWidget().setClosedAmount(state.closed, state.opened);
        getWidget().setAssignedAmount(state.assigned, state.opened);
        getWidget().setUnassignedAmount(state.unassigned, state.opened);
    }
}
