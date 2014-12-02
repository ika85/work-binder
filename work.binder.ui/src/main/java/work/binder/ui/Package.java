package work.binder.ui;

import java.util.List;

public class Package {

    private String _package;
    private List<String> _ipAddresses;

    public List<String> getIpAddresses() {
	return _ipAddresses;
    }

    public String getPackage() {
	return _package;
    }

    public void setIpAddresses(List<String> ipAddresses) {
	_ipAddresses = ipAddresses;
    }

    public void setPackage(String packageForSending) {
	_package = packageForSending;
    }
}
