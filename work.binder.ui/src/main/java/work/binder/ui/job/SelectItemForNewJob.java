package work.binder.ui.job;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.Locations;
import work.binder.ui.UserContext;

import work.binder.ui.Constants;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.TwinColSelect;

public abstract class SelectItemForNewJob extends LayoutReloadComponent {

    private static final long serialVersionUID = 2139331363445389774L;

    private TwinColSelect l;

    public SelectItemForNewJob(String[] items) {

	setSpacing(true);

	l = new TwinColSelect();
	for (int i = 0; i < items.length; i++) {
	    l.addItem(items[i]);
	}
	l.setRows(7);
	l.setNullSelectionAllowed(true);
	l.setMultiSelect(true);
	l.setImmediate(true);
	l.setLeftColumnCaption("Unassigned jars");
	l.setRightColumnCaption("Choosen jars for a new job");
	l.setWidth("350px");

	l.addListener(new Property.ValueChangeListener() {
	    private static final long serialVersionUID = 1481325013041936602L;

	    public void valueChange(ValueChangeEvent event) {
		@SuppressWarnings("rawtypes")
		Collection selected = (Collection) event.getProperty()
			.getValue();

		File jarFile = new File(Locations.UPLOAD_PACKAGE_LOCATION, selected
			.iterator().next().toString());
		// TODO4 what if there is more than one jar
		UserContext.getContext().getJob()
			.setJobPackage(jarFile.getAbsolutePath());
	    }
	});
	l.setImmediate(true);

	addComponent(l);
    }

    @Override
    public void reload() {
	File file = new File(Locations.UPLOAD_PACKAGE_LOCATION);
	String[] jarsArray = file.list(new FilenameFilter() {

	    public boolean accept(File arg0, String arg1) {

		return arg1.endsWith(Constants.DOT_JAR);
	    }
	});

	Map<String, String> futureJobs = UserContext.getFutureJobs();

	// Set<String> occupiedIps = futureJobs.keySet();
	Collection<String> assignedJars = futureJobs.values();

	List<String> notAssignedJars = new ArrayList<String>();

	for (String jar : jarsArray) {
	    File jarFile = new File(Locations.UPLOAD_PACKAGE_LOCATION, jar);
	    if (!assignedJars.contains(jarFile.getAbsoluteFile())) {
		notAssignedJars.add(jar);
	    }
	}

	// List<String> notOccupiedIps = new ArrayList<String>();
	//
	// for (String ip : occupiedIps) {
	// if (!assignedJars.contains(ip)) {
	// notOccupiedIps.add(ip);
	// }
	// }

	setSpacing(true);

	// l.removeAllItems();
	// for (String item : notAssignedJars) {
	// l.addItem(item);
	// }

    }
}
