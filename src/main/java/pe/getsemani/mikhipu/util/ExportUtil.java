package pe.getsemani.mikhipu.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExportUtil {

    /**
     * Exporta datos genéricos a Excel.
     * @param headers Lista de cabeceras (en orden).
     * @param rows Lista de filas; cada fila es una lista de valores en orden de las cabeceras.
     * @param sheetName Nombre de la hoja (opcional, default: "Reporte").
     */
    public static byte[] exportToExcel(List<String> headers, List<List<Object>> rows, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Reporte");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Rows
            for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
                Row row = sheet.createRow(rowIdx + 1);
                List<Object> dataRow = rows.get(rowIdx);
                for (int col = 0; col < dataRow.size(); col++) {
                    Object value = dataRow.get(col);
                    Cell cell = row.createCell(col);
                    if (value == null) {
                        cell.setCellValue("");
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Autosize columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error exportando a Excel", e);
        }
    }

    /**
     * Exporta datos genéricos a PDF.
     * @param headers Lista de cabeceras (en orden).
     * @param rows Lista de filas; cada fila es una lista de valores en orden de las cabeceras.
     * @param title Título opcional para el PDF.
     */
    public static byte[] exportToPdf(List<String> headers, List<List<Object>> rows, String title) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            if (title != null && !title.isBlank()) {
                document.add(new Paragraph(title)
                        .setBold().setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
            }

            Table table = new Table(headers.size());
            table.setWidth(UnitValue.createPercentValue(100));

            // Header
            for (String h : headers) {
                table.addHeaderCell(h);
            }

            // Rows
            for (List<Object> dataRow : rows) {
                for (Object value : dataRow) {
                    table.addCell(value != null ? value.toString() : "");
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error exportando a PDF", e);
        }
    }

}
