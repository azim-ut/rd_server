package app.thread;

import app.bean.SocketState;
import app.runnable.UpdateSocketRunnable;

public class UpdateSocketThread extends Thread{
    public UpdateSocketThread(SocketState state) {
        super(new UpdateSocketRunnable(state), "UpdateSocketThread");
    }
}
