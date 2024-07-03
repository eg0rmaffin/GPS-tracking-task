package ru.stavtrack;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class ExcelWriter {

    public static void writeGPSDataToExcel(String filePath, GPSData gpsData) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("GPS Data");

        int rowIndex = 0;
        Row headerRow = sheet.createRow(rowIndex++);
        createHeaderRow(headerRow, workbook);

        Row dataRow = sheet.createRow(rowIndex++);
        writeGPSDataToRow(gpsData, dataRow);

        Row fieldHeaderRow = sheet.createRow(rowIndex++);
        createFieldHeaderRow(fieldHeaderRow, workbook);

        writeFieldsToSheet(sheet, gpsData.getFields(), rowIndex, workbook);

        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            sheet.setColumnWidth(i, 6000);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
    }

    private static void createHeaderRow(Row headerRow, Workbook workbook) {
        String[] headers = {"CRC", "Serial ID", "Timestamp", "Packet Type", "Packet Len", "Latitude", "Longitude", "SF"};
        CellStyle style = createBoldCellStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private static void createFieldHeaderRow(Row fieldHeaderRow, Workbook workbook) {
        String[] fieldHeaders = {"Field Type", "Field Length", "Field Value"};
        CellStyle style = createBoldCellStyle(workbook);
        for (int i = 0; i < fieldHeaders.length; i++) {
            Cell cell = fieldHeaderRow.createCell(i);
            cell.setCellValue(fieldHeaders[i]);
            cell.setCellStyle(style);
        }
    }

    private static CellStyle createBoldCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static void writeGPSDataToRow(GPSData gpsData, Row row) {
        int cellIndex = 0;
        row.createCell(cellIndex++).setCellValue(gpsData.getCrc());
        row.createCell(cellIndex++).setCellValue(gpsData.getSerialId());
        row.createCell(cellIndex++).setCellValue(gpsData.getTimestamp());
        row.createCell(cellIndex++).setCellValue(gpsData.getPacketType());
        row.createCell(cellIndex++).setCellValue(gpsData.getPacketLen());
        row.createCell(cellIndex++).setCellValue(gpsData.getLatitude());
        row.createCell(cellIndex++).setCellValue(gpsData.getLongitude());
        row.createCell(cellIndex++).setCellValue(gpsData.getSf());
    }

    private static void writeFieldsToSheet(Sheet sheet, List<Field> fields, int startRowIndex, Workbook workbook) {
        int rowIndex = startRowIndex;
        CellStyle cellStyle = createCellStyle(workbook);

        for (Field field : fields) {
            Row fieldRow = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            fieldRow.createCell(cellIndex++).setCellValue(field.getType());
            fieldRow.createCell(cellIndex++).setCellValue(field.getLength());
            Cell valueCell = fieldRow.createCell(cellIndex++);
            valueCell.setCellValue(convertValueToString(field));
            valueCell.setCellStyle(cellStyle);
        }
    }

    private static CellStyle createCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(false);
        cellStyle.setShrinkToFit(true);
        return cellStyle;
    }

    private static String convertValueToString(Field field) {
        byte[] value = field.getValue();
        switch (field.getLength()) {
            case 1:
                return String.valueOf(value[0] & 0xFF);
            case 2:
                return String.valueOf(ByteBuffer.wrap(value).getShort() & 0xFFFF);
            case 4:
                return String.valueOf(ByteBuffer.wrap(value).getInt());
            default:
                StringBuilder sb = new StringBuilder();
                for (byte b : value) {
                    sb.append(String.format("%02X", b));
                }
                return sb.toString();
        }
    }
}
