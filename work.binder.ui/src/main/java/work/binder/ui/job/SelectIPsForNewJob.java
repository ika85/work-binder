package work.binder.ui.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.Locations;
import work.binder.ui.ResourceUtils;
import work.binder.ui.UserContext;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.TwinColSelect;

public class SelectIPsForNewJob extends LayoutReloadComponent {

    private static final long serialVersionUID = 2139331363445389774L;

    private TwinColSelect l;
    private static Properties _allSecureIPAddresses;

    static {
	_allSecureIPAddresses = ResourceUtils
		.loadIPAdresses(Locations.IP_ADRESSES_PROPERTIES_FILE);
    }

    public SelectIPsForNewJob() {

	Map<String, Integer> availableIPs = UserContext.getContext()
		.getAvailableIPs();

	setSpacing(true);

	l = new TwinColSelect();
	addItems(availableIPs);
	l.setRows(7);
	l.setNullSelectionAllowed(true);
	l.setMultiSelect(true);
	l.setImmediate(true);
	l.setLeftColumnCaption("Available IPs");
	l.setRightColumnCaption("Choosen IPs for a new job");
	l.setWidth("600px");

	l.addListener(new Property.ValueChangeListener() {
	    private static final long serialVersionUID = 1481325013041936602L;

	    public void valueChange(ValueChangeEvent event) {
		@SuppressWarnings("rawtypes")
		Collection selected = (Collection) event.getProperty()
			.getValue();

		@SuppressWarnings("rawtypes")
		Iterator iterator = selected.iterator();

		List<String> ips = new ArrayList<String>();
		while (iterator.hasNext()) {

		    Object item = iterator.next();
		    String ip = item.toString();

		    ips.add(ip);
		}

		UserContext.getContext().getJob().setIpAddresses(ips);
	    }
	});
	l.setImmediate(true);

	addComponent(l);
    }

    @Override
    public void reload() {

	Map<String, Integer> availableIPs = UserContext.getContext()
		.getAvailableIPs();

	l.removeAllItems();

	addItems(availableIPs);

    }

    private void addItems(Map<String, Integer> availableIPs) {

	for (String item : availableIPs.keySet()) {
	    String comment = _allSecureIPAddresses.get(item).toString();
	    if (StringUtils.isEmpty(comment)) {
		l.addItem(String.format("%s", item));
	    } else {
		l.addItem(String.format("%s (%s; slot count: %d)", item,
			comment, availableIPs.get(item)));
	    }
	}
    }
}
