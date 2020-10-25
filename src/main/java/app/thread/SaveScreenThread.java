package app.thread;

import app.bean.SocketState;
import app.runnable.SaveScreenRunnable;

public class SaveScreenThread extends Thread {
    public SaveScreenThread(SocketState state) {
        super(new SaveScreenRunnable(state), "ScreenSaveThread");
    }
}
