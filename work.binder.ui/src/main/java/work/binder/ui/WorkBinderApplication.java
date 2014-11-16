package work.binder.ui;

import com.vaadin.Application;
import com.vaadin.ui.*;

public class WorkBinderApplication extends Application {

    private static final long serialVersionUID = 3203873891855176575L;

    @Override
    public void init() {
	Window mainWindow = new Window("WorkBinderApplication");

	MainHomeMenu homeMenu = new MainHomeMenu();
	mainWindow.addComponent(homeMenu);
	setMainWindow(mainWindow);
    }

}
