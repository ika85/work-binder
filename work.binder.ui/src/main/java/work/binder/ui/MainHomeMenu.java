package work.binder.ui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import work.binder.ui.ip.AddNewIP;
import work.binder.ui.ip.IPTable;
import work.binder.ui.jar.UploadFileProcessor;
import work.binder.ui.job.AssignJob;
import work.binder.ui.job.SelectIPsForNewJob;
import work.binder.ui.job.SelectionPackagesForNewJob;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Window;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

public class MainHomeMenu extends VerticalLayout {

    private static final long serialVersionUID = -1087618745131764334L;
    private MenuBar menubar = new MenuBar();

    private static Properties _allSecureIPAddresses;

    static {

	_allSecureIPAddresses = ResourceUtils
		.loadIPAdresses(Locations.IP_ADRESSES_PROPERTIES_FILE);

	File uploadPackageFolder = new File(Locations.UPLOAD_PACKAGE_LOCATION);

	if (!uploadPackageFolder.exists()) {

	    try {
		FileUtils.forceMkdir(uploadPackageFolder);
	    } catch (IOException e) {
		e.printStackTrace();
		// TODO handle exception
	    }
	}
    }

    public MainHomeMenu() {

	Map<String, Command> jobCommands = new LinkedHashMap<String, MenuBar.Command>();

	UploadFileProcessor uploadFileProcess = new UploadFileProcessor();
	Command addPackageCommand = prepareCommand("Add New Package", uploadFileProcess);
	jobCommands.put("Add New Package", addPackageCommand);
	SelectionPackagesForNewJob selectionJarsForNewJob = new SelectionPackagesForNewJob(
		ResourceUtils.providePrepararedPackages(
			Locations.UPLOAD_PACKAGE_LOCATION, Constants.DOT_ZIP));
	SelectIPsForNewJob selectIPsForNewJob = new SelectIPsForNewJob(
		UserContext.getContext().getAvailableIPs(),
		_allSecureIPAddresses);
	AssignJob assignJob = new AssignJob(selectionJarsForNewJob);
	Command addJobCommand = prepareCommand("Assign New Jobs",
		selectionJarsForNewJob, selectIPsForNewJob, assignJob);
	jobCommands.put("Assign New Jobs", addJobCommand);
	addItem("Jobs", jobCommands);

	Map<String, Command> ipCommands = new LinkedHashMap<String, MenuBar.Command>();
	Properties properties = ResourceUtils
		.loadIPAdresses(Locations.IP_ADRESSES_PROPERTIES_FILE);
	IPTable ipTable = new IPTable(properties);
	AddNewIP addNewIPButton = new AddNewIP(ipTable, properties);

	Command ipTableCommand = prepareCommand("Show All Secure IP Addresses",
		ipTable);
	ipCommands.put("Show All Secure IP Addresses", ipTableCommand);
	Command ipTableAndNewIpCommand = prepareCommand(
		"Add New Secure IP address", ipTable, addNewIPButton);
	ipCommands.put("Add New Secure IP address", ipTableAndNewIpCommand);
	addItem("IP adresses", ipCommands);

	addComponent(menubar);

    }

    private void addItem(String itemName, Map<String, Command> subItemsData) {

	final MenuBar.MenuItem menuItem = menubar.addItem(itemName, null);

	Set<String> subItems = subItemsData.keySet();

	for (String subItem : subItems) {
	    menuItem.addItem(subItem, subItemsData.get(subItem));
	}
	menuItem.addSeparator();

	menuItem.addItem("Exit", new Command() {

	    private static final long serialVersionUID = 5787085276103071758L;

	    public void menuSelected(MenuItem selectedItem) {

	    }
	});
    }

    private Command prepareCommand(final String windowName,
	    final LayoutReloadComponent... additionalComponents) {

	Command command = new Command() {

	    private static final long serialVersionUID = 4205606929585168953L;

	    public void menuSelected(MenuItem selectedItem) {
		final Window subwindow = new Window(windowName);
		subwindow.setModal(true);
		subwindow.setWidth("650px");
		subwindow.setHeight("80%");

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);

		for (LayoutReloadComponent additionalComponent : additionalComponents) {
		    additionalComponent.reload();
		    subwindow.addComponent(additionalComponent);
		}
		getWindow().addWindow(subwindow);
	    }
	};
	return command;
    }

}