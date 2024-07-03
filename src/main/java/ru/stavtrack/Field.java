package ru.stavtrack;

import lombok.Data;

@Data
public class Field {
    private int type;
    private int length;
    private byte[] value;

    public Field(int type, int length, byte[] value) {
        this.type = type;
        this.length = length;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Type: %d, Length: %d, Value: ", type, length));
        for (byte b : value) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
