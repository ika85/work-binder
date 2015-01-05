package work.binder.ui;

import java.util.ArrayList;
import java.util.List;

public class PackageData {

    private List<String> _packages = new ArrayList<String>();
    private String _command;

    public String getCommand() {
	return _command;
    }

    public List<String> getPackages() {
	return _packages;
    }

    public void setCommand(String command) {
	_command = command;
    }

    public void setPackages(List<String> packages) {
	_packages = packages;
    }
}
