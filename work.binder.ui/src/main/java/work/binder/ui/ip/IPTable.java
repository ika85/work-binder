package work.binder.ui.ip;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.UserContext;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class IPTable extends LayoutReloadComponent {

    private Table _table;

    public static final Object IP_ADDRESS = "ipAdress";
    public static final Object BUSY = "busy";
    public static final Object COMMENT = "comment";
    public static final Object AVAILABLE = "available";
    public static final Object CANCEL = "cancel";

    // TODO should we add Remove Button for IP; and/or Edit button for the
    // comment
    public IPTable(Properties properties) {
	Table table = new Table("IPs");
	setTable(table);
	addComponent(table);

	table.setWidth("100%");
	table.setHeight("170px");

	table.setSelectable(true);
	table.setMultiSelect(true);
	table.setImmediate(true);

	table.setContainerDataSource(provideIPAddressContainer(properties));

	table.setColumnReorderingAllowed(true);
	table.setColumnCollapsingAllowed(true);

	table.setColumnHeaders(new String[] { "IP Address", "Available",
		"Busy", "Comment", "Cancel" });

	table.setColumnAlignment(IP_ADDRESS, Table.ALIGN_CENTER);
	table.setColumnAlignment(AVAILABLE, Table.ALIGN_CENTER);
	table.setColumnAlignment(BUSY, Table.ALIGN_CENTER);
	table.setColumnAlignment(COMMENT, Table.ALIGN_CENTER);
	table.setColumnAlignment(CANCEL, Table.ALIGN_CENTER);

	table.setColumnWidth(IP_ADDRESS, 90);
	table.setColumnWidth(AVAILABLE, 70);
	table.setColumnWidth(BUSY, 70);
	table.setColumnWidth(COMMENT, 90);
	table.setColumnWidth(CANCEL, 90);

    }

    public Table getTable() {
	return _table;
    }

    @Override
    public void reload() {

	reloadIPAddressContainer(getTable().getContainerDataSource());
    }

    private void setTable(Table table) {
	_table = table;
    }

    protected void fillContainerDataSource(Properties properties) {

	getTable()
		.setContainerDataSource(provideIPAddressContainer(properties));
    }

    protected IndexedContainer provideIPAddressContainer(Properties properties) {

	IndexedContainer container = new IndexedContainer();
	container.addContainerProperty(IP_ADDRESS, String.class, null);
	container.addContainerProperty(AVAILABLE, String.class, null);
	container.addContainerProperty(BUSY, String.class, null);
	container.addContainerProperty(COMMENT, String.class, null);
	container.addContainerProperty(CANCEL, Button.class, null);

	Set<Object> keys = properties.keySet();
	for (Object key : keys) {
	    Item item = container.addItem(key);
	    item.getItemProperty(IP_ADDRESS).setValue(key);
	    boolean busy = UserContext.getContext().getBusyIPs()
		    .containsKey(key);
	    item.getItemProperty(BUSY).setValue(busy);
	    boolean available = UserContext.getContext().getAvailableIPs()
		    .containsKey(key);
	    item.getItemProperty(AVAILABLE).setValue(available);
	    item.getItemProperty(COMMENT).setValue(
		    properties.get(key).toString());

	    Button cancelButton = new Button("Cancel");
	    cancelButton.addListener(new Button.ClickListener() {

		public void buttonClick(ClickEvent event) {

		    Object selectedItemsObj = getTable().getValue();
		    if (selectedItemsObj != null) {
			if (selectedItemsObj instanceof Collection) {

			    @SuppressWarnings("unchecked")
			    Collection<String> selectedItems = (Collection<String>) selectedItemsObj;

			    Iterator<String> iterator = selectedItems
				    .iterator();

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
	    });
	    cancelButton.setEnabled(busy);
	    cancelButton.setImmediate(true);
	    cancelButton.setVisible(true);

	    item.getItemProperty(CANCEL).setValue(cancelButton);
	}
	container.sort(new Object[] { IP_ADDRESS }, new boolean[] { true });
	return container;
    }

    protected void reloadIPAddressContainer(Container container) {
	Collection<?> collection = container.getItemIds();

	for (Object ipAddressObj : collection) {

	    if (ipAddressObj instanceof String) {

		String ipAddress = ipAddressObj.toString();
		Item item = container.getItem(ipAddress);

		boolean busy = UserContext.getContext().getBusyIPs()
			.containsKey(ipAddress);
		item.getItemProperty(BUSY).setValue(busy);
		boolean available = UserContext.getContext().getAvailableIPs()
			.containsKey(ipAddress);
		item.getItemProperty(AVAILABLE).setValue(available);

		((Button) item.getItemProperty(CANCEL).getValue())
			.setEnabled(busy);

	    }
	}
    }
}
