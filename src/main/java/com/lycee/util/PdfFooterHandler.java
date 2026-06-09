package com.lycee.util;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

/**
 * Pied de page : date de génération à gauche, numéro de page à droite.
 */
public class PdfFooterHandler implements IEventHandler {

    private final String dateGeneration;

    public PdfFooterHandler(String dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        int pageNum = pdf.getPageNumber(page);
        Rectangle rect = page.getPageSize();

        try (Canvas canvas = new Canvas(page, rect)) {
            canvas.showTextAligned(
                new Paragraph("Généré le " + dateGeneration).setFontSize(8),
                rect.getLeft() + 40, 25, TextAlignment.LEFT);
            canvas.showTextAligned(
                new Paragraph("Page " + pageNum).setFontSize(8),
                rect.getRight() - 40, 25, TextAlignment.RIGHT);
        }
    }
}
