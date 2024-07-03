package ru.stavtrack;

public interface PacketHandler {
    void handle(GPSData gpsData, byte[] data, int startIndex);
}
