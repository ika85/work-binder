package work.binder.ui.job;

import java.util.List;

import work.binder.ui.Package;
import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.UserContext;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class PackageSendingProcessor extends LayoutReloadComponent implements
	ClickListener {

    private static final long serialVersionUID = 7839304209716296708L;
    private static final String SPACE = " ";

    private PackagesSelectionForNewJob _packagesSelectionForNewJob;

    public PackageSendingProcessor(
	    PackagesSelectionForNewJob selectionJarsForNewJob) {

	setPackagesSelectionForNewJob(selectionJarsForNewJob);

	final Button saveButton = new Button("Send");
	saveButton.setDisableOnClick(true);
	saveButton.addListener(this);

	addComponent(saveButton);

	setComponentAlignment(saveButton, Alignment.TOP_RIGHT);

    }

    public void buttonClick(ClickEvent event) {
	Package job = UserContext.getContext().getJob();
	List<String> ipAddresses = job.getIpAddresses();
	String packageForSending = job.getPackage();
	// TODO2 + check are package and ipAddress set.

	if (packageForSending == null) {
	    getWindow().showNotification("Please choose package.");
	} else {
	    if (ipAddresses == null) {
		getWindow().showNotification("Please choose IP Address.");
	    } else {
		for (String ipAddressPlusComment : ipAddresses) {
		    // TODO what if there is already specified IP (with some
		    // other
		    // job)

		    String ipAddress = ipAddressPlusComment.substring(0,
			    ipAddressPlusComment.indexOf(SPACE));
		    UserContext.getPackagesForSending().put(ipAddress,
			    packageForSending);

		    // Show text that the save operation has been completed
		    addComponent(new Label(
			    String.format(
				    "In a few moments package (%s) will be sent to the chosen computer (%s).",
				    packageForSending, ipAddress)));
		}
		getPackagesSelectionForNewJob().reload();
	    }
	}
	// Re-enable the button
	event.getButton().setEnabled(true);

	// TODO6 clearing assigned package (zip) and occupied computer (IP)
	// TODO7 add OK button

    }

    @Override
    public void reload() {

    }

    private PackagesSelectionForNewJob getPackagesSelectionForNewJob() {
	return _packagesSelectionForNewJob;
    }

    private void setPackagesSelectionForNewJob(
	    PackagesSelectionForNewJob packagesSelectionForNewJob) {
	_packagesSelectionForNewJob = packagesSelectionForNewJob;
    }
}