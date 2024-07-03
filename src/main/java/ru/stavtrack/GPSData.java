package ru.stavtrack;

import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class GPSData {
    private int crc;
    private int serialId;
    private String timestamp;
    private int packetType;
    private int packetLen;
    private double latitude;
    private double longitude;
    private int sf;
    private List<Field> fields;

    public GPSData(byte[] data) {
        parseData(data);
    }


    private void parseData(byte[] data) {

        int index = 0;

        this.crc = ((data[index++] & 0xFF) << 8) | (data[index++] & 0xFF);
        this.serialId = ((data[index++] & 0xFF) << 8) | (data[index++] & 0xFF);
        long unixTimestamp = ((long) data[index++] & 0xFF) << 24 | ((long) data[index++] & 0xFF) << 16 | ((long) data[index++] & 0xFF) << 8 | ((long) data[index++] & 0xFF);
        this.timestamp = Instant.ofEpochSecond(unixTimestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.packetType = ((data[index++] & 0xFF) << 8) | (data[index++] & 0xFF);
        this.packetLen = ((data[index++] & 0xFF) << 8) | (data[index++] & 0xFF);

        long xCoord = ((long) data[index++] & 0xFF) << 24 | ((long) data[index++] & 0xFF) << 16 | ((long) data[index++] & 0xFF) << 8 | ((long) data[index++] & 0xFF);
        long yCoord = ((long) data[index++] & 0xFF) << 24 | ((long) data[index++] & 0xFF) << 16 | ((long) data[index++] & 0xFF) << 8 | ((long) data[index++] & 0xFF);

        this.latitude = xCoord * 90.0 / 0xFFFFFFFFL;
        this.longitude = yCoord * 180.0 / 0xFFFFFFFFL;

        this.sf = data[index++] & 0xFF;

        this.fields = new ArrayList<>();

        parseFields(data, index);
    }

    private void parseFields(byte[] data, int startIndex) {
        PacketHandler handler = PacketHandlerFactory.getHandler(packetType);
        if (handler != null) {
            handler.handle(this, data, startIndex);
        } else {
            System.out.println("Unknown packet type: " + packetType);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("CRC: %d, Serial ID: %d, Timestamp: %s, Packet Type: 0x%04X, Packet Len: %d, Latitude: %f, Longitude: %f, SF: %d",
                crc, serialId, timestamp, packetType, packetLen, latitude, longitude, sf));
        for (Field field : fields) {
            sb.append("\n").append(field.toString());
        }
        return sb.toString();
    }
}
