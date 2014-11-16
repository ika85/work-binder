package work.binder.ui.ip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.Locations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class AddNewIP extends LayoutReloadComponent implements ClickListener {

    private static final long serialVersionUID = 6469952989532073243L;

    private TextField _slaveIP;
    private TextField _comment;
    private Properties _properties;
    private IPTable _ipTable;

    public AddNewIP(IPTable ipTable, Properties properties) {

	TextField slaveIP = new TextField();
	slaveIP.setRequired(true);
	setSlaveIP(slaveIP);
	TextField comment = new TextField();
	setComment(comment);
	setIpTable(ipTable);
	setProperties(properties);
	addComponent(new Label("New IP Address:"));
	addComponent(slaveIP);
	addComponent(new Label("Comment:"));
	addComponent(comment);

	// TODO move the button more lower
	final Button saveButton = new Button("Add New Secure IP Address");
	saveButton.setDisableOnClick(true);
	saveButton.addListener(this);

	addComponent(saveButton);

    }

    public void buttonClick(ClickEvent event) {

	Object obj = getSlaveIP().getValue();
	String ip = obj.toString();

	// TODO add format checker for IP address
	if (StringUtils.isEmpty(ip)) {

	    getWindow().showNotification("IP Address must be specified");
	}

	if (getProperties().keySet().contains(ip)) {
	    addComponent(new Label(
		    String.format(
			    "IP (%s) has already been added. Type some other IP address.",
			    ip)));
	} else {
	    Object commentObj = getComment().getValue();
	    String comment = commentObj.toString();
	    getProperties().put(ip, comment);

	    try {
		FileOutputStream outputStream = new FileOutputStream(new File(
			Locations.IP_ADRESSES_PROPERTIES_FILE));
		getProperties().store(outputStream, StringUtils.EMPTY);
		outputStream.close();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
		// TODO handle exception
	    } catch (IOException e) {
		// TODO handle exception
		e.printStackTrace();
	    }

	    getIpTable().getTable().setContainerDataSource(
		    IPAddressTablePreparator
			    .provideIPAddressContainer(getProperties()));

	    addComponent(new Label(String.format("IP (%s) added.", ip)));

	    getWindow().requestRepaint();

	}
	event.getButton().setEnabled(true);

    }

    @Override
    public void reload() {

    }

    private TextField getComment() {
	return _comment;
    }

    private IPTable getIpTable() {
	return _ipTable;
    }

    private Properties getProperties() {
	return _properties;
    }

    private TextField getSlaveIP() {
	return _slaveIP;
    }

    private void setComment(TextField comment) {
	_comment = comment;
    }

    private void setIpTable(IPTable ipTable) {
	_ipTable = ipTable;
    }

    private void setProperties(Properties properties) {
	_properties = properties;
    }

    private void setSlaveIP(TextField slaveIP) {
	_slaveIP = slaveIP;
    }
}
