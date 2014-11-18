package work.binder.ui.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import work.binder.ui.Constants;
import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.Locations;
import work.binder.ui.ResourceUtils;
import work.binder.ui.UserContext;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.TwinColSelect;

public class SelectionPackagesForNewJob extends LayoutReloadComponent {

    private static final long serialVersionUID = 2139331363445389774L;

    private TwinColSelect l;

    public SelectionPackagesForNewJob(List<String> items) {

	setSpacing(true);

	l = new TwinColSelect();
	for (String item : items) {
	    l.addItem(item);
	}
	l.setRows(7);
	l.setNullSelectionAllowed(true);
	l.setMultiSelect(true);
	l.setImmediate(true);
	l.setLeftColumnCaption("Unassigned jars");
	l.setRightColumnCaption("Choosen jars for a new job");
	l.setWidth("600px");

	l.addListener(new Property.ValueChangeListener() {
	    private static final long serialVersionUID = 1481325013041936602L;

	    public void valueChange(ValueChangeEvent event) {
		@SuppressWarnings("rawtypes")
		Collection selected = (Collection) event.getProperty()
			.getValue();

		Iterator iterator = selected.iterator();
		if (iterator.hasNext()) {
		    File jarFile = new File(Locations.UPLOAD_PACKAGE_LOCATION,
			    iterator.next().toString());
		    // TODO4 what if there is more than one jar
		    UserContext.getContext().getJob()
			    .setJobPackage(jarFile.getAbsolutePath());
		}
	    }
	});
	l.setImmediate(true);

	addComponent(l);
    }

    @Override
    public void reload() {
	List<String> packageList = ResourceUtils.providePrepararedPackages(
		Locations.UPLOAD_PACKAGE_LOCATION, Constants.DOT_ZIP);

	Map<String, String> futureJobs = UserContext.getFutureJobs();

	// Set<String> occupiedIps = futureJobs.keySet();
	Collection<String> assignedPackages = futureJobs.values();

	List<String> notAssignedPackages = new ArrayList<String>();

	for (String jar : packageList) {
	    File jarFile = new File(Locations.UPLOAD_PACKAGE_LOCATION, jar);
	    if (!assignedPackages.contains(jarFile.getAbsoluteFile())) {
		notAssignedPackages.add(jar);
	    }
	}

	// List<String> notOccupiedIps = new ArrayList<String>();
	//
	// for (String ip : occupiedIps) {
	// if (!assignedPackages.contains(ip)) {
	// notOccupiedIps.add(ip);
	// }
	// }

	setSpacing(true);

	l.removeAllItems();
	for (String item : notAssignedPackages) {
	    l.addItem(item);
	}

    }
}
