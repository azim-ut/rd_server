package app.service;

public interface ScreenProvider {

    byte[] get(String key);

    void put(String key, byte[] bytes);

    void remove(String key);

    void clear(String pattern);
}
