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
import work.binder.ui.PackageData;
import work.binder.ui.ResourceUtils;
import work.binder.ui.UserContext;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.TwinColSelect;

public class PackagesSelectionForNewJob extends LayoutReloadComponent {

    private static final long serialVersionUID = 2139331363445389774L;

    private TwinColSelect l;

    public PackagesSelectionForNewJob(List<String> items) {

	setSpacing(true);

	l = new TwinColSelect();
	for (String item : items) {
	    l.addItem(item);
	}
	l.setRows(7);
	l.setNullSelectionAllowed(true);
	l.setMultiSelect(true);
	l.setImmediate(true);
	l.setLeftColumnCaption("Available Packages");
	l.setRightColumnCaption("Packages which will be sent to the slaves");
	l.setWidth("600px");

	l.addListener(new Property.ValueChangeListener() {
	    private static final long serialVersionUID = 1481325013041936602L;

	    public void valueChange(ValueChangeEvent event) {
		@SuppressWarnings("rawtypes")
		Collection selected = (Collection) event.getProperty()
			.getValue();

		@SuppressWarnings("rawtypes")
		Iterator iterator = selected.iterator();
		if (iterator.hasNext()) {
		    File jarFile = new File(Locations.UPLOAD_PACKAGE_LOCATION,
			    iterator.next().toString());

		    UserContext.getContext().getJob().getPackages()
			    .add(jarFile.getAbsolutePath());
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

	Map<String, PackageData> futureJobs = UserContext.getContext()
		.getPackagesForSending();

	Collection<PackageData> assignedPackages = futureJobs.values();

	List<String> notAssignedPackages = new ArrayList<String>();

	for (String jar : packageList) {
	    File jarFile = new File(Locations.UPLOAD_PACKAGE_LOCATION, jar);
	    boolean notAssigned = true;
	    for (PackageData packageData : assignedPackages) {
		if (packageData.getPackages().contains(
			jarFile.getAbsolutePath())) {
		    notAssigned = false;
		    break;
		}
	    }
	    if (notAssigned) {
		notAssignedPackages.add(jar);
	    }
	}

	setSpacing(true);

	l.removeAllItems();
	for (String item : notAssignedPackages) {
	    l.addItem(item);
	}

    }
}
