package app.service;

import app.bean.ScreenPacket;

import java.util.List;

public interface ScreenPacketProvider {

    List<ScreenPacket> list(String code);

    ScreenPacket get(String code, int position);

    void put(ScreenPacket screenPacket);

    void remove(String code, int position);

    void clear(String code);
}
