package work.binder.ui.ip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.Locations;
import work.binder.ui.UserContext;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class IPTable extends LayoutReloadComponent {

    private Table _table;
    private Properties _ipProperties;
    private Properties _slotProperties;

    public static final String IP_ADDRESS = "ipAdress";
    public static final String NA = "N/A";
    public static final String BUSY = "busy";
    public static final String COMMENT = "comment";
    public static final String PROCESSORS = "processors";
    public static final String SLOTS = "slots";
    public static final String CLEAR = "clear";
    public static final String AVAILABLE = "available";
    public static final String CANCEL = "cancel";

    // TODO should we add Remove Button for IP; and/or Edit button for the
    // comment
    public IPTable(Properties ipProperties, Properties slotProperties) {

	setIpProperties(ipProperties);
	setSlotProperties(slotProperties);

	Table table = new Table("IPs");
	setTable(table);
	addComponent(table);

	table.setMultiSelect(false);
	getTable().setNullSelectionAllowed(false);

	table.setWidth("100%");
	table.setHeight("170px");

	table.setSelectable(true);
	table.setMultiSelect(true);
	table.setImmediate(true);

	IndexedContainer container = provideSlavesStatusContainer(ipProperties);
	table.setContainerDataSource(container);

	table.setColumnReorderingAllowed(true);
	table.setColumnCollapsingAllowed(true);

	table.setColumnHeaders(new String[] { "IP Address", "Available",
		"Busy", "Comment", "Processor(s)", "Slot(s)", "Clear", "Cancel" });

	table.setColumnAlignment(IP_ADDRESS, Table.ALIGN_CENTER);
	table.setColumnAlignment(AVAILABLE, Table.ALIGN_CENTER);
	table.setColumnAlignment(BUSY, Table.ALIGN_CENTER);
	table.setColumnAlignment(COMMENT, Table.ALIGN_CENTER);
	table.setColumnAlignment(PROCESSORS, Table.ALIGN_CENTER);
	table.setColumnAlignment(SLOTS, Table.ALIGN_CENTER);
	table.setColumnAlignment(CLEAR, Table.ALIGN_CENTER);
	table.setColumnAlignment(CANCEL, Table.ALIGN_CENTER);

	table.setColumnWidth(IP_ADDRESS, 90);
	table.setColumnWidth(AVAILABLE, 70);
	table.setColumnWidth(BUSY, 70);
	table.setColumnWidth(COMMENT, 90);
	table.setColumnWidth(PROCESSORS, 90);
	table.setColumnWidth(SLOTS, 90);
	table.setColumnWidth(CLEAR, 90);
	table.setColumnWidth(CANCEL, 90);

	Collection<?> itemIds = container.getItemIds();
	if (itemIds.size() > 0) {
	    Object selectedItem = container.getIdByIndex(0);
	    table.select(selectedItem);
	}

    }

    public Properties getSlotProperties() {
	return _slotProperties;
    }

    public Table getTable() {
	return _table;
    }

    @Override
    public void reload() {
	getTable().setMultiSelect(false);
	getTable().setNullSelectionAllowed(false);
	fillContainerDataSource((IndexedContainer) getTable()
		.getContainerDataSource(), getIpProperties().keySet(),
		getIpProperties());

    }

    public void setSlotProperties(Properties slotProperties) {
	_slotProperties = slotProperties;
    }

    private Item addNewItem(IndexedContainer container,
	    Map<String, Integer> ipMap, Set<String> busyIPs,
	    Properties ipProperties, Object key) {

	Item item = container.addItem(key);
	item.getItemProperty(IP_ADDRESS).setValue(key);
	boolean busy = busyIPs.contains(key);
	item.getItemProperty(BUSY).setValue(busy);
	boolean available = ipMap.containsKey(key);
	item.getItemProperty(AVAILABLE).setValue(available);
	item.getItemProperty(COMMENT)
		.setValue(ipProperties.get(key).toString());

	Integer processorCount = ipMap.get(key);
	String processorCountString = null;
	if (processorCount == null) {
	    processorCountString = NA;
	} else {
	    processorCountString = String.valueOf(processorCount);
	}
	item.getItemProperty(PROCESSORS).setValue(processorCountString);

	Button cancelButton = new Button("Cancel");
	cancelButton.addListener(provideCancelButtonListener());
	cancelButton.setEnabled(busy);
	cancelButton.setImmediate(true);
	cancelButton.setVisible(true);
	item.getItemProperty(CANCEL).setValue(cancelButton);

	// TODO maybe clear button should be enabled only when there is at
	// least one package on the slave
	Button clearButton = new Button("Clear");
	clearButton.addListener(provideClearButtonListener());
	clearButton.setEnabled(true);
	clearButton.setImmediate(true);
	clearButton.setVisible(true);
	item.getItemProperty(CLEAR).setValue(clearButton);

	return item;
    }

    private Properties getIpProperties() {
	return _ipProperties;
    }

    private Button.ClickListener provideCancelButtonListener() {

	return new Button.ClickListener() {

	    public void buttonClick(ClickEvent event) {

		Object selectedItemsObj = getTable().getValue();
		if (selectedItemsObj != null) {
		    if (selectedItemsObj instanceof Collection) {

			@SuppressWarnings("unchecked")
			Collection<String> selectedItems = (Collection<String>) selectedItemsObj;

			Iterator<String> iterator = selectedItems.iterator();

			if (iterator.hasNext()) {
			    String selectedItem = iterator.next();

			    UserContext.getContext().addIPForCanceling(
				    selectedItem);
			} else {
			    getWindow()
				    .showNotification(
					    "Please click on the row with IP, which jobs you want to cancel.");
			}
		    }
		}

	    }
	};
    }

    private Button.ClickListener provideClearButtonListener() {

	return new Button.ClickListener() {

	    public void buttonClick(ClickEvent event) {

		String selectedIP = provideSelectedIP();

		UserContext.getContext().addIPForClearing(selectedIP);

		getWindow()
			.showNotification(
				"Request for clearing job will be sent in a few seconds. Please reopen this window in order to see current state of the slaves.");
	    }
	};
    }

    private String provideSelectedIP() {

	String selectedItem = null;
	Object selectedItemsObj = getTable().getValue();
	if (selectedItemsObj != null) {
	    if (selectedItemsObj instanceof String) {

		selectedItem = selectedItemsObj.toString();

		// UserContext.getContext().addIPForCanceling(
		// selectedItem);
	    } else {
		getWindow()
			.showNotification("Please click on the row with IP.");
	    }
	}

	return selectedItem;
    }

    // on window closing store to the disk values for slots
    private ValueChangeListener provideSlotTextFieldListener() {

	return new Property.ValueChangeListener() {

	    public void valueChange(ValueChangeEvent event) {
		// Assuming that the value type is a String
		String value = (String) event.getProperty().getValue();

		int slotCount = -1;

		try {
		    slotCount = Integer.parseInt(value);
		} catch (NumberFormatException e) {
		    getWindow()
			    .showNotification(
				    String.format(
					    "$s is invalid value for the number of slots",
					    value));
		}

		if (slotCount > -1) {

		    String ip = provideSelectedIP();
		    storeIPData(ip, slotCount);

		} else {
		    getWindow().showNotification(
			    String.format(
				    "$d is invalid value for number of slots",
				    slotCount));
		}
	    }
	};
    }

    private void setIpProperties(Properties ipProperties) {
	_ipProperties = ipProperties;
    }

    private void setSlotCount(Item item, String ipAddress) {

	Object slotObj = getSlotProperties().get(ipAddress);

	String slotCount;
	if (slotObj != null) {
	    slotCount = slotObj.toString();
	} else {
	    slotCount = StringUtils.EMPTY;
	}

	TextField textField = new TextField();
	textField.setValue(slotCount);
	textField.setWidth("80");
	textField.addListener(provideSlotTextFieldListener());
	textField.setImmediate(true);
	item.getItemProperty(SLOTS).setValue(textField);
    }

    private void setTable(Table table) {
	_table = table;
    }

    private void storeIPData(String ip, int slotCount) {

	File slotFile = new File(Locations.SLAVE_SLOTS_PROPERTIES_FILE);
	FileOutputStream outputStream = null;

	try {
	    Properties properties = getSlotProperties();

	    properties.put(ip, String.valueOf(slotCount));

	    outputStream = new FileOutputStream(slotFile);
	    properties.store(outputStream, StringUtils.EMPTY);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    // TODO handle exception
	} catch (IOException e) {
	    // TODO handle exception
	    e.printStackTrace();
	    try {
		outputStream.close();
	    } catch (IOException e1) {
		// TODO handle exception
		e1.printStackTrace();
	    }
	}
    }

    protected void fillContainerDataSource(IndexedContainer container,
	    Set<Object> ips, Properties ipProperties) {

	for (Object ipAddressObj : ips) {
	    if (ipAddressObj instanceof String) {

		String ipAddress = ipAddressObj.toString();
		Item item = container.getItem(ipAddress);

		if (item == null) {

		    item = addNewItem(container, UserContext.getContext()
			    .getAvailableIPs(), UserContext.getContext()
			    .getBusyIPs().keySet(), ipProperties, ipAddressObj);
		} else {

		    boolean busy = UserContext.getContext().getBusyIPs()
			    .containsKey(ipAddress);
		    item.getItemProperty(BUSY).setValue(busy);
		    boolean available = UserContext.getContext()
			    .getAvailableIPs().containsKey(ipAddress);
		    Integer processorCount = UserContext.getContext()
			    .getAvailableIPs().get(ipAddress);
		    String processorCountString = null;
		    if (processorCount == null) {
			processorCountString = NA;
		    } else {
			processorCountString = String.valueOf(processorCount);
		    }
		    item.getItemProperty(AVAILABLE).setValue(available);
		    item.getItemProperty(PROCESSORS).setValue(
			    processorCountString);

		    ((Button) item.getItemProperty(CANCEL).getValue())
			    .setEnabled(busy);

		}
		setSlotCount(item, ipAddress);
	    }
	}

	Collection<?> itemIds = container.getItemIds();
	if (itemIds.size() > 0) {
	    Object selectedItem = container.getIdByIndex(0);
	    getTable().select(selectedItem);
	}
    }

    protected IndexedContainer provideSlavesStatusContainer(
	    Properties ipProperties) {

	IndexedContainer container = new IndexedContainer();
	container.addContainerProperty(IP_ADDRESS, String.class, null);
	container.addContainerProperty(AVAILABLE, String.class, null);
	container.addContainerProperty(BUSY, String.class, null);
	container.addContainerProperty(COMMENT, String.class, null);
	container.addContainerProperty(PROCESSORS, String.class, null);
	container.addContainerProperty(SLOTS, TextField.class, null);
	container.addContainerProperty(CLEAR, Button.class, null);
	container.addContainerProperty(CANCEL, Button.class, null);

	fillContainerDataSource(container, ipProperties.keySet(), ipProperties);

	container.sort(new Object[] { IP_ADDRESS }, new boolean[] { true });
	return container;
    }
}
