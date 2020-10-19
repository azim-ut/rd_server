package app.service;

import app.bean.ScreenPacket;

import java.util.List;

public interface ScreenPacketProvider {

    List<ScreenPacket> list(String code);

    ScreenPacket get(String id);

    void put(ScreenPacket screenPacket);

    void remove(ScreenPacket screenPacket);

    void clear(String code);
}
