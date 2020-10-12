package app.service;

import java.util.List;

public interface ScreenPacketProvider {

    List<String> keys(String code);

    byte[] get(String code, int position);

    void put(String code, int position, byte[] bytes);

    void remove(String code, int position);

    void clear(String code);
}
