package work.binder.ui;

import java.util.List;
import java.util.Map;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.TextArea;

public class PackageCommands extends LayoutReloadComponent {

    private static final long serialVersionUID = 4035499611555525481L;

    @Override
    public void reload() {
	// TODO Auto-generated method stub
	TextArea commandsForPackages = new TextArea(
		"Please enter every new command for the slaves in the new line.");
	commandsForPackages.setWidth("600");

	commandsForPackages.addListener(new FieldEvents.TextChangeListener() {

	    private static final long serialVersionUID = 3818211730859843892L;

	    public void textChange(TextChangeEvent event) {
		String commandsString = (String) event.getText();
		String[] commands = commandsString.split("\n");

		Package job = UserContext.getContext().getJob();
		List<String> ipAddresses = job.getIpAddresses();

		Map<String, PackageData> packagesForSendingMap = UserContext
			.getContext().getPackagesForSending();

		// TODO add handling: what if there is less commands
		// than it
		// should be
		int i = 0;
		for (String ipAddressComment : ipAddresses) {

		    String ip = ipAddressComment.substring(0,
			    ipAddressComment.indexOf(" "));

		    PackageData packageData = packagesForSendingMap.get(ip);
		    if (packageData == null) {
			packageData = new PackageData();
			packagesForSendingMap.put(ip, packageData);
		    }
		    packageData.setCommand(commands[i++]);
		}
	    }
	});
	commandsForPackages.setImmediate(true);

	removeAllComponents();
	addComponent(commandsForPackages);

    }
}
