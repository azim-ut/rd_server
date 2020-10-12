package app.bean;

import app.constants.HostAct;
import lombok.Builder;

@Builder
public class ConnectionPath {
    HostAct act;
    String code;
    String ip;
    int port;
}
