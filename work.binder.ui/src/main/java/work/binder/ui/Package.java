package work.binder.ui;

import java.util.ArrayList;
import java.util.List;

public class Package {

    private List<String> _packages;
    private List<String> _ipAddresses;

    public Package() {
	setPackage(new ArrayList<String>());
	setIpAddresses(new ArrayList<String>());
    }

    public List<String> getIpAddresses() {
	return _ipAddresses;
    }

    public List<String> getPackages() {
	return _packages;
    }

    public void setIpAddresses(List<String> ipAddresses) {
	_ipAddresses = ipAddresses;
    }

    public void setPackage(List<String> packagesForSending) {
	_packages = packagesForSending;
    }
}
