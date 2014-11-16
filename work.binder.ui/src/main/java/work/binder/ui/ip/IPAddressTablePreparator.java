package work.binder.ui.ip;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import work.binder.ui.UserContext;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public final class IPAddressTablePreparator {

    public static final Object IP_ADDRESS = "ipAdress";
    public static final Object BUSY = "busy";
    public static final Object COMMENT = "comment";
    public static final Object AVAILABLE = "available";

    protected static IndexedContainer provideIPAddressContainer(
	    Properties properties) {
	IndexedContainer container = new IndexedContainer();
	container.addContainerProperty(IP_ADDRESS, String.class, null);
	container.addContainerProperty(AVAILABLE, String.class, null);
	container.addContainerProperty(BUSY, String.class, null);
	container.addContainerProperty(COMMENT, String.class, null);

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
	}
	container.sort(new Object[] { IP_ADDRESS }, new boolean[] { true });
	return container;
    }

    protected static void reloadIPAddressContainer(Container container) {
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
	    }
	}
    }
}
