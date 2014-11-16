package work.binder.ui.job;

import java.util.List;

import work.binder.ui.Job;
import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.UserContext;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class AssignJob extends LayoutReloadComponent implements ClickListener {

    private static final long serialVersionUID = 7839304209716296708L;
    private static final String SPACE = " ";

    private SelectionJarsForNewJob _selectionJarsForNewJob;

    public AssignJob(SelectionJarsForNewJob selectionJarsForNewJob) {

	setSelectionJarsForNewJob(selectionJarsForNewJob);

	final Button saveButton = new Button("Assign a Job");
	saveButton.setDisableOnClick(true);
	saveButton.addListener(this);

	addComponent(saveButton);

    }

    public void buttonClick(ClickEvent event) {
	Job job = UserContext.getContext().getJob();
	List<String> ipAddresses = job.getIpAddresses();
	String jobPackage = job.getJobPackage();
	// TODO2 + check are package and ipAddress set.

	for (String ipAddressPlusComment : ipAddresses) {
	    // TODO what if there is already specified IP (with some other job)

	    String ipAddress = ipAddressPlusComment.substring(0,
		    ipAddressPlusComment.indexOf(SPACE));
	    UserContext.getFutureJobs().put(ipAddress, jobPackage);

	    // Show text that the save operation has been completed
	    addComponent(new Label(
		    String.format(
			    "In a few moments package (%s) will be sent to the chosen computer (%s).",
			    jobPackage, ipAddress)));
	}
	// Re-enable the button
	event.getButton().setEnabled(true);

	getSelectionJarsForNewJob().reload();

	// TODO6 clearing assigned jar and occupied computer (IP)
	// TODO7 add OK button

    }

    @Override
    public void reload() {

    }

    private SelectionJarsForNewJob getSelectionJarsForNewJob() {
	return _selectionJarsForNewJob;
    }

    private void setSelectionJarsForNewJob(
	    SelectionJarsForNewJob selectionJarsForNewJob) {
	_selectionJarsForNewJob = selectionJarsForNewJob;
    }
}