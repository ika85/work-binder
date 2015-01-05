package work.binder.ui.job;

import java.util.Iterator;
import java.util.List;

import work.binder.ui.Package;
import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.PackageData;
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
    private static final String COMMA = ",";

    private PackagesSelectionForNewJob _packagesSelectionForNewJob;
    private IPsSelectionForNewJob _iPsSelectionForNewJob;

    public PackageSendingProcessor(
	    PackagesSelectionForNewJob selectionJarsForNewJob,
	    IPsSelectionForNewJob iPsSelectionForNewJob) {

	setPackagesSelectionForNewJob(selectionJarsForNewJob);
	setiPsSelectionForNewJob(iPsSelectionForNewJob);

	final Button saveButton = new Button("Send");
	saveButton.setDisableOnClick(true);
	saveButton.addListener(this);

	addComponent(saveButton);

	setComponentAlignment(saveButton, Alignment.TOP_RIGHT);

    }

    public void buttonClick(ClickEvent event) {
	Package job = UserContext.getContext().getJob();
	List<String> ipAddresses = job.getIpAddresses();
	List<String> packagesForSending = job.getPackages();
	// TODO2 + check are package and ipAddress set.

	if (packagesForSending == null) {
	    getWindow().showNotification("Please choose package.");
	} else {
	    if (ipAddresses == null) {
		getWindow().showNotification("Please choose IP Address.");
	    } else {
		for (String ipAddressComment : ipAddresses) {
		    // TODO what if there is already specified IP (with some
		    // other
		    // job)

		    String ip = ipAddressComment.substring(0,
			    ipAddressComment.indexOf(" "));

		    PackageData packageData = UserContext.getContext()
			    .getPackagesForSending().get(ip);
		    if (packageData == null) {
			packageData = new PackageData();
			UserContext.getContext().getPackagesForSending()
				.put(ip, packageData);
		    }
		    packageData.setPackages(packagesForSending);
		    UserContext.getContext().getAvailableIPs().remove(ip);
		    UserContext.getContext().getBusyIPs()
			    .put(ip, packagesForSending);

		    // Show text that the save operation has been completed

		    StringBuilder packages = new StringBuilder();
		    Iterator<String> packageIterator = packagesForSending
			    .iterator();
		    while (packageIterator.hasNext()) {
			packages.append(packageIterator.next());
			if (packageIterator.hasNext()) {
			    packages.append(COMMA);
			    packages.append(SPACE);
			}
		    }

		    addComponent(new Label(
			    String.format(
				    "In a few moments chosen packages (%s) will be sent to the chosen computer (%s).",
				    packages, ip)));
		}

		getPackagesSelectionForNewJob().reload();
		getiPsSelectionForNewJob().reload();
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

    private IPsSelectionForNewJob getiPsSelectionForNewJob() {
	return _iPsSelectionForNewJob;
    }

    private PackagesSelectionForNewJob getPackagesSelectionForNewJob() {
	return _packagesSelectionForNewJob;
    }

    private void setiPsSelectionForNewJob(
	    IPsSelectionForNewJob iPsSelectionForNewJob) {
	_iPsSelectionForNewJob = iPsSelectionForNewJob;
    }

    private void setPackagesSelectionForNewJob(
	    PackagesSelectionForNewJob packagesSelectionForNewJob) {
	_packagesSelectionForNewJob = packagesSelectionForNewJob;
    }
}