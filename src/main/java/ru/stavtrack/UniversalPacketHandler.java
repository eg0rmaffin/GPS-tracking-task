package ru.stavtrack;

public class UniversalPacketHandler implements PacketHandler {

    @Override
    public void handle(GPSData gpsData, byte[] data, int startIndex) {
        int index = startIndex;

        int antState = data[index++] & 0xFF;
        int fixType = data[index++] & 0xFF;
        int satCount = data[index++] & 0xFF;
        int speed = ((data[index++] & 0xFF) << 8) | (data[index++] & 0xFF);
        int course = ((data[index++] & 0xFF) << 8) | (data[index++] & 0xFF);
        int reason = data[index++] & 0xFF;

        gpsData.getFields().add(new Field(1, 1, new byte[]{(byte) antState}));
        gpsData.getFields().add(new Field(2, 1, new byte[]{(byte) fixType}));
        gpsData.getFields().add(new Field(3, 1, new byte[]{(byte) satCount}));
        gpsData.getFields().add(new Field(4, 2, new byte[]{(byte) (speed >> 8), (byte) speed}));
        gpsData.getFields().add(new Field(5, 2, new byte[]{(byte) (course >> 8), (byte) course}));
        gpsData.getFields().add(new Field(6, 1, new byte[]{(byte) reason}));


        while (index < data.length) {
            int type = data[index] & 0xFF;
            if (type == 0xFF) {
                break;
            }
            index++;

            int length = data[index] & 0xFF;
            index++;

            if (index + length > data.length) {
                break;
            }

            byte[] value = new byte[length];
            System.arraycopy(data, index, value, 0, length);
            index += length;

            gpsData.getFields().add(new Field(type, length, value));
        }
    }
}
