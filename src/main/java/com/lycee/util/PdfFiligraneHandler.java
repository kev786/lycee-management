package com.lycee.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;

import java.nio.file.Path;

/** Filigrane logo en arrière-plan (opacité ~10 %). */
public class PdfFiligraneHandler implements IEventHandler {

    private final Path logoPath;

    public PdfFiligraneHandler(Path logoPath) {
        this.logoPath = logoPath;
    }

    @Override
    public void handleEvent(Event event) {
        if (logoPath == null) return;
        try {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            ImageData imageData = ImageDataFactory.create(logoPath.toString());

            float pw = page.getPageSize().getWidth();
            float ph = page.getPageSize().getHeight();
            float size = 160f;
            float x = (pw - size) / 2;
            float y = (ph - size) / 2;

            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
            canvas.saveState();
            PdfExtGState gs = new PdfExtGState().setFillOpacity(0.10f);
            canvas.setExtGState(gs);
            canvas.addImageFittedIntoRectangle(imageData, new Rectangle(x, y, size, size), false);
            canvas.restoreState();
        } catch (Exception ignored) {
            // Filigrane optionnel : ne pas bloquer la génération
        }
    }
}
