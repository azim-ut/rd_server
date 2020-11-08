package app.bean;

import app.service.ScreenPacketProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Builder
@Getter
@Setter
public class SocketState {
    private String ip;
    private int port_save;
    private int port_show;
    private int busy_save;
    private int busy_show;
    private ScreenPacketProvider provider;

    public String getIp() {
//        return ip;
        return "127.0.0.1";
    }

    public synchronized void setIp(String val) {
        this.ip = val;
    }

    public void incBusySave() {
        busy_save++;
    }

    public void decBusySave() {
        busy_save--;
    }

    public void incBusyShow() {
        busy_show++;
    }

    public void decBusyShow() {
        busy_show--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketState that = (SocketState) o;
        return port_save == that.port_save &&
                port_show == that.port_show &&
                busy_save == that.busy_save &&
                busy_show == that.busy_show &&
                ip.equals(that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port_save, port_show, busy_save, busy_show);
    }
}
