package app.thread;

import app.bean.SocketState;
import app.runnable.ShowScreenRunnable;

public class ShowScreenThread extends Thread {
    public ShowScreenThread(SocketState state) {
        super(new ShowScreenRunnable(state), "WebSocketScreenShareThread");
    }
}
