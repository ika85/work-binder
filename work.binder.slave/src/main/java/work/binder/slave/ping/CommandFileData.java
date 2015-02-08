package work.binder.slave.ping;

public class CommandFileData {

    private String _commandFilePath;
    private boolean _batchFileIndicator;

    public CommandFileData(String commandFilePath, boolean batchFileIndicator) {
	setCommandFilePath(commandFilePath);
	setBatchFileIndicator(batchFileIndicator);

    }

    public String getCommandFilePath() {
	return _commandFilePath;
    }

    public boolean isBatchFileIndicator() {
	return _batchFileIndicator;
    }

    private void setBatchFileIndicator(boolean batchFileIndicator) {
	_batchFileIndicator = batchFileIndicator;
    }

    private void setCommandFilePath(String commandFilePath) {
	_commandFilePath = commandFilePath;
    }

}
