package work.binder.ui.ip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.Locations;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class AddNewIP extends LayoutReloadComponent implements ClickListener {

    private static final long serialVersionUID = 6469952989532073243L;

    private TextField _slaveIP;
    private TextField _comment;
    private Properties _ipProperties;
    private Properties _slotProperties;
    private IPTable _ipTable;

    public AddNewIP(IPTable ipTable, Properties ipProperties,
	    Properties slotProperties) {

	setMargin(true);
	TextField slaveIP = new TextField();
	slaveIP.setRequired(true);
	setSlaveIP(slaveIP);
	TextField comment = new TextField();
	setComment(comment);
	setIpTable(ipTable);
	setIpProperties(ipProperties);
	setSlotProperties(slotProperties);
	addComponent(new Label("New IP Address:"));
	addComponent(slaveIP);
	addComponent(new Label("Comment:"));
	addComponent(comment);
	addComponent(new Label("_"));

	// TODO move the button more lower
	final Button addIPAddress = new Button("Add New IP Address");
	addIPAddress.setDisableOnClick(true);
	addIPAddress.addListener(this);

	addComponent(addIPAddress);
	setComponentAlignment(addIPAddress, Alignment.TOP_LEFT);

    }

    public void buttonClick(ClickEvent event) {

	Object obj = getSlaveIP().getValue();
	String ip = obj.toString();

	// TODO add format checker for IP address
	if (StringUtils.isEmpty(ip)) {

	    getWindow().showNotification("IP Address must be specified");
	} else if (getIpProperties().keySet().contains(ip)) {
	    getWindow()
		    .showNotification(
			    String.format(
				    "IP (%s) has already been added. Type some other IP address.",
				    ip));
	} else {
	    Object commentObj = getComment().getValue();
	    String comment = commentObj.toString();
	    getIpProperties().put(ip, comment);

	    try {
		FileOutputStream outputStream = new FileOutputStream(new File(
			Locations.IP_ADRESSES_PROPERTIES_FILE));
		getIpProperties().store(outputStream, StringUtils.EMPTY);
		outputStream.close();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
		// TODO handle exception
	    } catch (IOException e) {
		// TODO handle exception
		e.printStackTrace();
	    }

	    getIpTable().fillContainerDataSource(
		    (IndexedContainer) getIpTable().getTable()
			    .getContainerDataSource(),
		    getIpProperties().keySet(), getIpProperties());

	    addComponent(new Label(String.format("IP (%s) added.", ip)));

	    getWindow().requestRepaint();

	}
	event.getButton().setEnabled(true);

    }

    @Override
    public void reload() {
	int i = 4;
	i++;
    }

    private TextField getComment() {
	return _comment;
    }

    private Properties getIpProperties() {
	return _ipProperties;
    }

    private IPTable getIpTable() {
	return _ipTable;
    }

    private TextField getSlaveIP() {
	return _slaveIP;
    }

    private Properties getSlotProperties() {
	return _slotProperties;
    }

    private void setComment(TextField comment) {
	_comment = comment;
    }

    private void setIpProperties(Properties ipProperties) {
	_ipProperties = ipProperties;
    }

    private void setIpTable(IPTable ipTable) {
	_ipTable = ipTable;
    }

    private void setSlaveIP(TextField slaveIP) {
	_slaveIP = slaveIP;
    }

    private void setSlotProperties(Properties slotProperties) {
	_slotProperties = slotProperties;
    }
}
