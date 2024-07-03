package ru.stavtrack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = "src/main/resources/hex.txt";
        String hexString = new String(Files.readAllBytes(Paths.get(filePath))).trim();

        byte[] data = HexParser.hexStringToByteArray(hexString);
        GPSData gpsData = new GPSData(data);
        String excelFilePath = "gps_data.xlsx";
        ExcelWriter.writeGPSDataToExcel(excelFilePath, gpsData);
    }
}
