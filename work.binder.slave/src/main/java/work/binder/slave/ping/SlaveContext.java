package work.binder.slave.ping;

public class SlaveContext {

    private static boolean _occupied;
    private static boolean _finished;

    public static boolean isFinished() {
	return _finished;
    }

    public static synchronized boolean isOccupied() {
	return _occupied;
    }

    public static void setFinished(boolean finished) {
	_finished = finished;
    }

    public static synchronized void setOccupied(boolean occupied) {
	_occupied = occupied;
    }

}
