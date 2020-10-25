package app.bean;

import app.constants.ServerMode;
import lombok.Builder;

@Builder
public class ConnectionPath {
    ServerMode act;
    String code;
    String ip;
    int port_save;
    int port_show;
    int busy_save;
    int busy_show;
}
