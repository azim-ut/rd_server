package app;

import app.bean.SocketState;
import app.thread.SaveScreenThread;
import app.thread.ShowScreenThread;
import app.thread.UpdateSocketThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScreenSaveService {
    private final SocketState state;

    private SaveScreenThread saveScreenThread;
    private ShowScreenThread showScreenThread;
    private UpdateSocketThread updateSocketThread;
    private Thread monitor;


    public void start() {
        monitor = new Thread(() -> {
            try {
                startSaveScreenThread();
                startScreenGetThread();
                startImageSendThread();
                while (true) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                log.error("ScreenSaveService monitor: " + e.getMessage());
            }
        });
        monitor.start();
    }

    public boolean isStopped() {
        if (saveScreenThread != null && saveScreenThread.getState() == Thread.State.TERMINATED) {
            return true;
        }
        if (showScreenThread != null && showScreenThread.getState() == Thread.State.TERMINATED) {
            return true;
        }
        if (updateSocketThread != null && updateSocketThread.getState() == Thread.State.TERMINATED) {
            return true;
        }

        return false;
    }

    public void stop() {
        if (saveScreenThread != null) {
            saveScreenThread.interrupt();
        }
        if (showScreenThread != null) {
            showScreenThread.interrupt();
        }
        if (updateSocketThread != null) {
            updateSocketThread.interrupt();
        }
        if (monitor != null) {
            monitor.interrupt();
        }
    }

    private void startSaveScreenThread() {
        saveScreenThread = new SaveScreenThread(state);
        saveScreenThread.start();
    }

    private void startScreenGetThread() {
        showScreenThread = new ShowScreenThread(state);
        showScreenThread.start();
    }

    private void startImageSendThread() {
        updateSocketThread = new UpdateSocketThread(state);
        updateSocketThread.start();
    }
}
