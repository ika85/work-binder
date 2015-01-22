package work.binder.ui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import work.binder.ui.ip.AddNewIPProcessor;
import work.binder.ui.ip.IPTable;
import work.binder.ui.jar.UploadFileProcessor;
import work.binder.ui.job.PackageSendingProcessor;
import work.binder.ui.job.IPsSelectionForNewJob;
import work.binder.ui.job.PackagesSelectionForNewJob;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseListener;

public class MainHomeMenu extends VerticalLayout {

    private static final long serialVersionUID = -1087618745131764334L;

    private MenuBar menubar = new MenuBar();

    static {

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
	Command addPackageCommand = prepareCommand("Upload New Package",
		"250px", "70%", uploadFileProcess);
	jobCommands.put("Upload New Package", addPackageCommand);
	PackagesSelectionForNewJob selectionJarsForNewJob = new PackagesSelectionForNewJob(
		ResourceUtils.providePrepararedPackages(
			Locations.UPLOAD_PACKAGE_LOCATION, Constants.DOT_ZIP));
	IPsSelectionForNewJob selectIPsForNewJob = new IPsSelectionForNewJob();
	PackageCommands commandsForPackages = new PackageCommands();
	PackageSendingProcessor assignJob = new PackageSendingProcessor(
		selectionJarsForNewJob, selectIPsForNewJob, commandsForPackages);
	Command addJobCommand = prepareCommand("Send Package", "650px", "95%",
		selectionJarsForNewJob, selectIPsForNewJob,
		commandsForPackages, assignJob);
	jobCommands.put("Send Package", addJobCommand);
	addItem("Packages", jobCommands);

	Map<String, Command> ipCommands = new LinkedHashMap<String, MenuBar.Command>();
	Properties ipProperties = ResourceUtils
		.loadIPAdresses(Locations.IP_ADRESSES_PROPERTIES_FILE);
	Properties slotProperties = ResourceUtils
		.loadIPAdresses(Locations.SLAVE_SLOTS_PROPERTIES_FILE);

	UserContext.getContext().setSlaveCountProperties(slotProperties);
	IPTable ipTable = new IPTable(ipProperties, slotProperties);

	AddNewIPProcessor addNewIPButton = new AddNewIPProcessor(ipTable,
		ipProperties, slotProperties);
	Command ipTableAndNewIpCommand = prepareCommand("Slaves Status",
		"830px", "80%", ipTable, addNewIPButton);
	ipCommands.put("Slaves Status", ipTableAndNewIpCommand);

	addItem("IP Adresses", ipCommands);

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

    private Button prepareCloseButton(final Window subWindow) {

	Button close = new Button("Close", new Button.ClickListener() {

	    private static final long serialVersionUID = -8540746738206577789L;

	    public void buttonClick(ClickEvent event) {

		(subWindow.getParent()).removeWindow(subWindow);
	    }
	});

	return close;
    }

    private Command prepareCommand(final String windowName,
	    final String windowWidth, final String windowHeight,
	    final LayoutReloadComponent... additionalComponents) {

	Command command = new Command() {

	    private static final long serialVersionUID = 4205606929585168953L;

	    public void menuSelected(MenuItem selectedItem) {

		final Window subwindow = new Window(windowName);
		subwindow.setModal(true);
		subwindow.setWidth(windowWidth);
		subwindow.setHeight(windowHeight);

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);

		for (LayoutReloadComponent additionalComponent : additionalComponents) {
		    additionalComponent.reload();
		    subwindow.addComponent(additionalComponent);

		}

		Button closeButton = prepareCloseButton(subwindow);
		subwindow.addComponent(closeButton);

		subwindow.addListener(new CloseListener() {

		    /**
		     * 
		     */
		    private static final long serialVersionUID = 2741339792445983680L;

		    public void windowClose(CloseEvent e) {
			// TODO Auto-generated method stub

		    }
		});

		layout.setComponentAlignment(closeButton, Alignment.TOP_RIGHT);
		layout.setSizeFull();
		getWindow().addWindow(subwindow);
	    }
	};
	return command;
    }

}