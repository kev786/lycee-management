package com.lycee.service;

import com.lycee.model.Eleve;
import com.lycee.model.NoteEleve;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CellStyle boldHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle borderedStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle labelStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle valueStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    public byte[] exportEleves(List<Eleve> eleves) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Élèves");
            String[] headers = {"Matricule", "Nom", "Pr\u00e9nom", "Date Naissance", "Sexe", "Classe", "T\u00e9l\u00e9phone Parent", "Email Parent"};
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = boldHeaderStyle(wb);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            CellStyle bodyStyle = borderedStyle(wb);
            for (int i = 0; i < eleves.size(); i++) {
                Eleve e = eleves.get(i);
                Row row = sheet.createRow(i + 1);
                createCell(row, 0, e.getMatricule(), bodyStyle);
                createCell(row, 1, e.getNom(), bodyStyle);
                createCell(row, 2, e.getPrenom(), bodyStyle);
                createCell(row, 3, e.getDateNaissance() != null ? e.getDateNaissance().format(DATE_FMT) : "", bodyStyle);
                createCell(row, 4, e.getSexe(), bodyStyle);
                createCell(row, 5, e.getClasse() != null ? e.getClasse().getLibelle() : "", bodyStyle);
                createCell(row, 6, e.getTelParent(), bodyStyle);
                createCell(row, 7, e.getEmailParent(), bodyStyle);
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportBulletin(Eleve eleve, BigDecimal moyenne, int rang, int effectif,
                                  int trimestre, BigDecimal moyenneClasse, List<NoteEleve> notes) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Bulletin T" + trimestre);
            CellStyle labelSt = labelStyle(wb);
            CellStyle valueSt = valueStyle(wb);
            CellStyle boldHeaderSt = boldHeaderStyle(wb);

            // Student info section
            String[][] infos = {
                {"Nom & Pr\u00e9nom", eleve.getNomComplet()},
                {"Matricule", eleve.getMatricule()},
                {"Classe", eleve.getClasse() != null ? eleve.getClasse().getLibelle() : "\u2014"},
                {"Trimestre", String.valueOf(trimestre)}
            };
            for (int i = 0; i < infos.length; i++) {
                Row row = sheet.createRow(i);
                createCell(row, 0, infos[i][0], labelSt);
                createCell(row, 1, infos[i][1], valueSt);
            }

            // Empty row
            sheet.createRow(infos.length);

            // Notes table header
            int startRow = infos.length + 1;
            Row noteHeader = sheet.createRow(startRow);
            String[] noteCols = {"Mati\u00e8re", "Coefficient", "Note /20", "Appr\u00e9ciation", "Prof."};
            for (int i = 0; i < noteCols.length; i++) {
                Cell cell = noteHeader.createCell(i);
                cell.setCellValue(noteCols[i]);
                cell.setCellStyle(boldHeaderSt);
            }

            CellStyle bodyStyle = borderedStyle(wb);
            int r = startRow + 1;
            if (notes != null) {
                for (NoteEleve n : notes) {
                    Row row = sheet.createRow(r++);
                    createCell(row, 0, n.getMatiere(), bodyStyle);
                    createCell(row, 1, String.valueOf(n.getCoefficient()), bodyStyle);
                    createCell(row, 2, formatNote(n.getNotesValeur()), bodyStyle);
                    createCell(row, 3, appreciation(n.getNotesValeur()), bodyStyle);
                    createCell(row, 4, n.getProfSaisie() != null ? n.getProfSaisie() : "\u2014", bodyStyle);
                }
            }

            // Empty row before summary
            r++;
            // Summary section
            String[][] summary = {
                {"Moyenne g\u00e9n\u00e9rale", formatNote(moyenne) + " /20"},
                {"Moyenne de la classe", formatNote(moyenneClasse) + " /20"},
                {"Rang / Effectif", rang + " / " + effectif}
            };
            for (String[] s : summary) {
                Row row = sheet.createRow(r++);
                createCell(row, 0, s[0], labelSt);
                createCell(row, 1, s[1], valueSt);
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private String formatNote(BigDecimal note) {
        if (note == null) return "\u2014";
        return note.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    private String appreciation(BigDecimal note) {
        if (note == null) return "\u2014";
        double v = note.doubleValue();
        if (v >= 18) return "Excellent";
        if (v >= 16) return "Tr\u00e8s bien";
        if (v >= 14) return "Bien";
        if (v >= 12) return "Assez bien";
        if (v >= 10) return "Passable";
        if (v >= 8) return "Insuffisant";
        return "Faible";
    }
}
