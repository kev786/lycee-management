package com.lycee.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.lycee.model.ParametresEtablissement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class PdfLayoutHelper {

    public static final String ETABLISSEMENT     = "Lycée de Démonstration";
    public static final String MINISTERE         = "MINISTÈRE DES ENSEIGNEMENTS SECONDAIRES";
    public static final String REPUBLIQUE        = "RÉPUBLIQUE DU CAMEROUN";
    public static final String DELEGATION        = "DÉLÉGATION RÉGIONALE DU CENTRE";

    public static final DeviceRgb HEADER_BG      = new DeviceRgb(0x1a, 0x53, 0x76);
    public static final DeviceRgb ROW_ALT        = new DeviceRgb(0xf0, 0xf4, 0xf8);
    public static final DeviceRgb ROUGE          = new DeviceRgb(0xdc, 0x26, 0x26);
    public static final DeviceRgb VERT           = new DeviceRgb(0x16, 0xa3, 0x4a);
    public static final DeviceRgb OR_TOP1        = new DeviceRgb(0xFF, 0xD7, 0x00);
    public static final DeviceRgb OR_TOP2        = new DeviceRgb(0xC0, 0xC0, 0xC0);
    public static final DeviceRgb OR_TOP3        = new DeviceRgb(0xCD, 0x7F, 0x32);
    public static final DeviceRgb HIGHLIGHT_OK   = new DeviceRgb(0xdc, 0xfc, 0xe7);
    public static final DeviceRgb HIGHLIGHT_FAIL = new DeviceRgb(0xfe, 0xe2, 0xe2);

    private static final DateTimeFormatter DATE_LONG =
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
    private static final DateTimeFormatter DATE_TIME =
        DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm", Locale.FRENCH);

    private PdfLayoutHelper() {}

    public static void registerFooter(PdfDocument pdf) {
        String now = LocalDateTime.now().format(DATE_TIME);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new PdfFooterHandler(now));
    }

    public static void registerFiligrane(PdfDocument pdf, ParametresEtablissement params) {
        if (params != null && params.isFiligraneLogo()) {
            var logo = params.resolveLogoPath();
            if (logo != null) {
                pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new PdfFiligraneHandler(logo));
            }
        }
    }

    public static void preparerPdf(PdfDocument pdf, ParametresEtablissement params) {
        registerFooter(pdf);
        registerFiligrane(pdf, params);
    }

    public static void addEnteteInstitutionnel(Document doc, ParametresEtablissement params,
                                                String titrePrincipal, String sousTitre) throws java.io.IOException {
        if (params == null) params = new ParametresEtablissement();

        var logoPath = params.resolveLogoPath();
        if (logoPath != null) {
            Image logoImg = new Image(ImageDataFactory.create(logoPath.toString()));
            logoImg.scaleToFit(65, 65);
            logoImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
            logoImg.setMarginBottom(6);
            doc.add(logoImg);
        }

        if (params.getEntetePdf() != null && !params.getEntetePdf().isBlank()) {
            for (String line : params.getEntetePdf().split("\\r?\\n")) {
                if (!line.isBlank()) {
                    doc.add(new Paragraph(line.trim())
                        .setTextAlignment(TextAlignment.CENTER).setFontSize(9));
                }
            }
        } else {
            doc.add(new Paragraph(params.getRepublique())
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10).setBold());
            doc.add(new Paragraph(params.getMinistere())
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9));
            doc.add(new Paragraph(params.getDelegation())
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9));
        }

        doc.add(new Paragraph(params.getNomEtablissement())
            .setTextAlignment(TextAlignment.CENTER).setFontSize(11).setBold()
            .setFontColor(HEADER_BG));
        doc.add(separateur());
        doc.add(new Paragraph(titrePrincipal)
            .setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(15)
            .setFontColor(HEADER_BG).setMarginTop(8));
        if (sousTitre != null && !sousTitre.isBlank()) {
            doc.add(new Paragraph(sousTitre)
                .setTextAlignment(TextAlignment.CENTER).setFontSize(11)
                .setMarginBottom(4));
        }
        doc.add(separateur());
    }

    public static LineSeparator separateur() {
        return new LineSeparator(new SolidLine(0.75f))
            .setMarginTop(6).setMarginBottom(6);
    }

    public static Table createTable(float... widths) {
        Table t = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();
        t.setBorder(new SolidBorder(HEADER_BG, 0.5f));
        return t;
    }

    public static void addHeaderCell(Table t, String text) {
        t.addHeaderCell(new Cell()
            .add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE).setFontSize(9))
            .setBackgroundColor(HEADER_BG)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(6));
    }

    public static Cell labelCell(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontSize(10)).setPadding(5);
    }

    public static Cell valueCell(String text) {
        return new Cell().add(new Paragraph(text != null ? text : "—").setFontSize(10)).setPadding(5);
    }

    public static String formatNote(BigDecimal note) {
        if (note == null) return "—";
        return note.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',') + " / 20";
    }

    public static String formatNoteShort(BigDecimal note) {
        if (note == null) return "—";
        return note.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "—";
        return date.format(DATE_LONG);
    }

    public static String formatDateDuJour() {
        return LocalDate.now().format(DATE_LONG);
    }

    /** Formate une date de RDV (datetime-local HTML ou texte libre). */
    public static String formatRendezVous(String dateRdv) {
        if (dateRdv == null || dateRdv.isBlank()) return "—";
        try {
            if (dateRdv.contains("T")) {
                LocalDateTime dt = LocalDateTime.parse(dateRdv);
                return dt.format(DATE_TIME);
            }
        } catch (Exception _) {
            // texte libre
        }
        return dateRdv;
    }

    public static String libelleSexe(String sexe) {
        if (sexe == null) return "—";
        return "M".equalsIgnoreCase(sexe) ? "Masculin" : "F".equalsIgnoreCase(sexe) ? "Féminin" : sexe;
    }

    public static DeviceRgb couleurNote(BigDecimal note) {
        if (note == null) return null;
        if (note.compareTo(BigDecimal.TEN) < 0) return ROUGE;
        if (note.compareTo(new BigDecimal("16")) >= 0) return VERT;
        return null;
    }

    public static String appreciationNote(BigDecimal note) {
        if (note == null) return "—";
        double d = note.doubleValue();
        if (d >= 16) return "Excellent";
        if (d >= 14) return "Bien";
        if (d >= 12) return "Assez bien";
        if (d >= 10) return "Passable";
        return "Insuffisant";
    }

    public static String appreciationGenerale(BigDecimal moy) {
        if (moy == null) return "—";
        double d = moy.doubleValue();
        if (d >= 16) return "Excellent travail. Félicitations.";
        if (d >= 14) return "Très bon travail. Compliments.";
        if (d >= 12) return "Bon travail. Encouragements.";
        if (d >= 10) return "Travail satisfaisant. Peut mieux faire.";
        return "Résultats insuffisants. Efforts à fournir.";
    }

    public static String mentionHonneur(BigDecimal moy) {
        if (moy == null) return "—";
        double d = moy.doubleValue();
        if (d >= 16) return "Félicitations";
        if (d >= 14) return "Compliments";
        return "Encouragements";
    }

    public static String decision(BigDecimal moyenne) {
        if (moyenne == null) return "—";
        return moyenne.compareTo(BigDecimal.TEN) >= 0 ? "ADMIS(E)" : "À RATTRAPER";
    }

    public static DeviceRgb couleurPodium(int rang) {
        return switch (rang) {
            case 1 -> OR_TOP1;
            case 2 -> OR_TOP2;
            case 3 -> OR_TOP3;
            default -> null;
        };
    }

    public static void addSignature(Document doc, String signataire) {
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph(signataire)
            .setTextAlignment(TextAlignment.RIGHT).setBold().setFontSize(11));
        doc.add(new Paragraph("Cachet et signature")
            .setTextAlignment(TextAlignment.RIGHT).setFontSize(9).setItalic());
    }
}
