package app.bean;

import app.constants.Mode;
import lombok.Builder;

@Builder
public class ConnectionPath {
    Mode act;
    String code;
    String ip;
    int port;
}
