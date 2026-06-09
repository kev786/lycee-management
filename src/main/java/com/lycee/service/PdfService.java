package com.lycee.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.lycee.model.Eleve;
import com.lycee.model.NoteEleve;
import com.lycee.model.ParametresEtablissement;
import com.lycee.util.Constants;
import com.lycee.util.PdfLayoutHelper;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class PdfService {

    public byte[] genererBulletin(ParametresEtablissement params, Eleve eleve, List<NoteEleve> notes,
                                   BigDecimal moyenne, int rang, int effectif, int trimestre,
                                   BigDecimal moyenneClasse, int nbAbsInjustifiees,
                                   int heuresAbsInjustifiees) throws java.io.IOException {
        if (params == null) params = new ParametresEtablissement();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(bos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(40, 40, 50, 40);
            PdfLayoutHelper.preparerPdf(pdf, params);

            String annee = params.getAnneeScolaire();
            if (eleve.getClasse() != null && eleve.getClasse().getAnneeScolaire() != null) {
                annee = eleve.getClasse().getAnneeScolaire();
            }
            String sousTitre = "Année scolaire : " + annee + "  •  Trimestre " + trimestre;
            PdfLayoutHelper.addEnteteInstitutionnel(doc, params, "BULLETIN SCOLAIRE", sousTitre);

            doc.add(buildIdentiteEleve(eleve));
            doc.add(new Paragraph(" "));

            if (notes == null || notes.isEmpty()) {
                doc.add(new Paragraph("Aucune note enregistrée pour ce trimestre.")
                    .setItalic().setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(PdfLayoutHelper.ROUGE));
            } else {
                doc.add(buildTableauNotes(notes));
            }

            doc.add(new Paragraph(" "));
            doc.add(buildResume(moyenne, moyenneClasse, rang, effectif,
                nbAbsInjustifiees, heuresAbsInjustifiees, trimestre));
            PdfLayoutHelper.addSignature(doc, "Le Censeur");
        }
        return bos.toByteArray();
    }

    private Table buildIdentiteEleve(Eleve eleve) throws java.io.IOException {
        Table info = PdfLayoutHelper.createTable(2, 3);
        info.addCell(PdfLayoutHelper.labelCell("Nom & Prénom"));
        info.addCell(PdfLayoutHelper.valueCell(eleve.getNomComplet()));
        info.addCell(PdfLayoutHelper.labelCell("Matricule"));
        info.addCell(PdfLayoutHelper.valueCell(eleve.getMatricule()));
        info.addCell(PdfLayoutHelper.labelCell("Classe"));
        info.addCell(PdfLayoutHelper.valueCell(
            eleve.getClasse() != null ? eleve.getClasse().getLibelle() : "—"));
        info.addCell(PdfLayoutHelper.labelCell("Date de naissance"));
        info.addCell(PdfLayoutHelper.valueCell(PdfLayoutHelper.formatDate(eleve.getDateNaissance())));
        info.addCell(PdfLayoutHelper.labelCell("Sexe"));
        info.addCell(PdfLayoutHelper.valueCell(PdfLayoutHelper.libelleSexe(eleve.getSexe())));
        info.addCell(PdfLayoutHelper.labelCell("Parent / Tél."));
        info.addCell(PdfLayoutHelper.valueCell(
            (eleve.getNomParent() != null ? eleve.getNomParent() : "—")
            + (eleve.getTelParent() != null ? " — " + eleve.getTelParent() : "")));
        if (eleve.getClasse() != null && eleve.getClasse().getProfPrincipal() != null) {
            info.addCell(PdfLayoutHelper.labelCell("Prof. principal"));
            info.addCell(PdfLayoutHelper.valueCell(eleve.getClasse().getProfPrincipal()));
        }

        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
        Cell photoCell = new Cell().setPadding(8).setVerticalAlignment(VerticalAlignment.MIDDLE);
        boolean photoAjoutee = false;

        if (eleve.getPhotoFilename() != null && !eleve.getPhotoFilename().isBlank()) {
            var photoPath = Paths.get(Constants.UPLOAD_DIR_PHOTOS, eleve.getPhotoFilename()).normalize();
            if (photoPath.startsWith(Paths.get(Constants.UPLOAD_DIR_PHOTOS))
                && Files.isRegularFile(photoPath)) {
                Image photo = new Image(ImageDataFactory.create(photoPath.toString()));
                photo.scaleToFit(75, 95);
                photoCell.add(photo).setTextAlignment(TextAlignment.CENTER);
                photoAjoutee = true;
            }
        }
        if (!photoAjoutee) {
            photoCell.add(new Paragraph("Photo").setFontSize(8).setItalic()
                .setTextAlignment(TextAlignment.CENTER));
        }

        wrapper.addCell(photoCell);
        wrapper.addCell(new Cell().add(info).setBorder(null));
        return wrapper;
    }

    private Table buildTableauNotes(List<NoteEleve> notes) {
        Table notesTable = PdfLayoutHelper.createTable(3, 1, 1.5f, 2, 1.5f);
        PdfLayoutHelper.addHeaderCell(notesTable, "MATIÈRE");
        PdfLayoutHelper.addHeaderCell(notesTable, "COEF.");
        PdfLayoutHelper.addHeaderCell(notesTable, "NOTE /20");
        PdfLayoutHelper.addHeaderCell(notesTable, "APPRÉCIATION");
        PdfLayoutHelper.addHeaderCell(notesTable, "PROF.");

        boolean alt = false;
        for (NoteEleve n : notes) {
            Cell c1 = new Cell().add(new Paragraph(n.getMatiere()).setFontSize(9));
            Cell c2 = new Cell().add(new Paragraph(String.valueOf(n.getCoefficient())).setFontSize(9))
                .setTextAlignment(TextAlignment.CENTER);
            Cell c3 = new Cell().add(new Paragraph(PdfLayoutHelper.formatNoteShort(n.getNotesValeur())).setFontSize(9).setBold())
                .setTextAlignment(TextAlignment.CENTER);
            var couleur = PdfLayoutHelper.couleurNote(n.getNotesValeur());
            if (couleur != null) c3.setFontColor(couleur);
            Cell c4 = new Cell().add(new Paragraph(PdfLayoutHelper.appreciationNote(n.getNotesValeur())).setFontSize(8))
                .setTextAlignment(TextAlignment.CENTER);
            Cell c5 = new Cell().add(new Paragraph(
                n.getProfSaisie() != null ? n.getProfSaisie() : "—").setFontSize(8))
                .setTextAlignment(TextAlignment.CENTER);
            if (alt) {
                c1.setBackgroundColor(PdfLayoutHelper.ROW_ALT);
                c2.setBackgroundColor(PdfLayoutHelper.ROW_ALT);
                c3.setBackgroundColor(PdfLayoutHelper.ROW_ALT);
                c4.setBackgroundColor(PdfLayoutHelper.ROW_ALT);
                c5.setBackgroundColor(PdfLayoutHelper.ROW_ALT);
            }
            notesTable.addCell(c1);
            notesTable.addCell(c2);
            notesTable.addCell(c3);
            notesTable.addCell(c4);
            notesTable.addCell(c5);
            alt = !alt;
        }
        return notesTable;
    }

    private Table buildResume(BigDecimal moyenne, BigDecimal moyenneClasse, int rang, int effectif,
                              int nbAbsInjustifiees, int heuresAbsInjustifiees, int trimestre) {
        boolean reussite = moyenne != null && moyenne.compareTo(BigDecimal.TEN) >= 0;
        Table resume = PdfLayoutHelper.createTable(2, 4);
        resume.addCell(PdfLayoutHelper.labelCell("Moyenne générale"));
        Cell moyCell = new Cell().add(new Paragraph(PdfLayoutHelper.formatNote(moyenne)).setBold().setFontSize(11));
        moyCell.setFontColor(reussite ? PdfLayoutHelper.VERT : PdfLayoutHelper.ROUGE);
        moyCell.setBackgroundColor(reussite ? PdfLayoutHelper.HIGHLIGHT_OK : PdfLayoutHelper.HIGHLIGHT_FAIL);
        resume.addCell(moyCell);
        resume.addCell(PdfLayoutHelper.labelCell("Moyenne de la classe"));
        resume.addCell(PdfLayoutHelper.valueCell(PdfLayoutHelper.formatNote(moyenneClasse)));
        resume.addCell(PdfLayoutHelper.labelCell("Rang / Effectif"));
        resume.addCell(PdfLayoutHelper.valueCell(rang + " / " + effectif));
        resume.addCell(PdfLayoutHelper.labelCell("Décision"));
        Cell decCell = new Cell().add(new Paragraph(PdfLayoutHelper.decision(moyenne)).setBold());
        decCell.setFontColor(reussite ? PdfLayoutHelper.VERT : PdfLayoutHelper.ROUGE);
        resume.addCell(decCell);
        resume.addCell(PdfLayoutHelper.labelCell("Appréciation générale"));
        resume.addCell(PdfLayoutHelper.valueCell(PdfLayoutHelper.appreciationGenerale(moyenne)));
        resume.addCell(PdfLayoutHelper.labelCell("Absences injustifiées (T" + trimestre + ")"));
        String absTxt = nbAbsInjustifiees + " séance(s) — " + heuresAbsInjustifiees + " h";
        Cell absCell = PdfLayoutHelper.valueCell(absTxt);
        if (heuresAbsInjustifiees >= 8) absCell.setFontColor(PdfLayoutHelper.ROUGE);
        resume.addCell(absCell);
        return resume;
    }

    public byte[] genererConvocation(ParametresEtablissement params, Eleve eleve, String motif,
                                      String dateRdv, int nbAbsencesInjustifiees) throws java.io.IOException {
        if (params == null) params = new ParametresEtablissement();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(bos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(50, 50, 50, 50);
            PdfLayoutHelper.preparerPdf(pdf, params);
            PdfLayoutHelper.addEnteteInstitutionnel(doc, params, "CONVOCATION DES PARENTS", null);

            doc.add(new Paragraph(params.getVille() + ", le " + PdfLayoutHelper.formatDateDuJour())
                .setTextAlignment(TextAlignment.RIGHT).setFontSize(10).setMarginBottom(16));

            String parent = eleve.getNomParent() != null ? eleve.getNomParent() : "Madame, Monsieur";
            doc.add(new Paragraph(parent).setBold().setFontSize(11));
            if (eleve.getTelParent() != null) {
                doc.add(new Paragraph("Tél. : " + eleve.getTelParent()).setFontSize(10));
            }
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Objet : Convocation — " + (motif != null ? motif : "Incident disciplinaire"))
                .setBold().setFontSize(11).setMarginBottom(12));

            String classe = eleve.getClasse() != null ? eleve.getClasse().getLibelle() : "—";
            doc.add(new Paragraph("Madame, Monsieur,").setFontSize(11));
            doc.add(new Paragraph(
                "Nous avons l'honneur de vous convoquer à " + params.getNomEtablissement()
                + " concernant votre enfant " + eleve.getNomComplet()
                + ", élève en " + classe + ", matricule " + eleve.getMatricule() + ".")
                .setFontSize(11).setTextAlignment(TextAlignment.JUSTIFIED).setMarginTop(8));

            if (nbAbsencesInjustifiees > 0) {
                doc.add(new Paragraph(
                    "À ce jour, votre enfant a accumulé " + nbAbsencesInjustifiees
                    + " absence(s) injustifiée(s) sur le trimestre en cours.")
                    .setFontSize(11).setMarginTop(8));
            }
            doc.add(new Paragraph("Motif : " + (motif != null ? motif : "—"))
                .setFontSize(11).setItalic().setMarginTop(8));
            doc.add(new Paragraph("Date et heure du rendez-vous : " + PdfLayoutHelper.formatRendezVous(dateRdv))
                .setBold().setFontSize(11).setMarginTop(12));
            doc.add(new Paragraph("Lieu : Bureau du Censeur — " + params.getNomEtablissement())
                .setFontSize(10).setMarginTop(4));
            doc.add(new Paragraph(
                "Votre présence est vivement souhaitée. En cas d'empêchement, contactez l'établissement.")
                .setFontSize(11).setTextAlignment(TextAlignment.JUSTIFIED).setMarginTop(16));
            doc.add(new Paragraph(
                "Veuillez agréer, Madame, Monsieur, l'expression de nos salutations distinguées.")
                .setFontSize(11).setMarginTop(12));

            PdfLayoutHelper.addSignature(doc, "Le Censeur");
        }
        return bos.toByteArray();
    }

    public byte[] genererTableauHonneur(ParametresEtablissement params, String classeLibelle,
                                          int trimestre, List<Map<String, Object>> top10)
            throws java.io.IOException {
        if (params == null) params = new ParametresEtablissement();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(bos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(40, 40, 50, 40);
            PdfLayoutHelper.preparerPdf(pdf, params);

            String sousTitre = "Classe : " + classeLibelle
                + "  •  Année " + params.getAnneeScolaire()
                + "  •  Trimestre " + trimestre;
            PdfLayoutHelper.addEnteteInstitutionnel(doc, params, "TABLEAU D'HONNEUR", sousTitre);

            doc.add(new Paragraph("Les dix meilleurs élèves (moyenne pondérée ≥ 10/20)")
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10).setItalic().setMarginBottom(12));

            if (top10 == null || top10.isEmpty()) {
                doc.add(new Paragraph("Aucun élève classé pour ce trimestre.")
                    .setTextAlignment(TextAlignment.CENTER).setItalic());
            } else {
                Table t = PdfLayoutHelper.createTable(1, 1.5f, 3, 1.5f, 2);
                PdfLayoutHelper.addHeaderCell(t, "RANG");
                PdfLayoutHelper.addHeaderCell(t, "MATRICULE");
                PdfLayoutHelper.addHeaderCell(t, "NOM & PRÉNOM");
                PdfLayoutHelper.addHeaderCell(t, "MOYENNE");
                PdfLayoutHelper.addHeaderCell(t, "MENTION");

                for (Map<String, Object> row : top10) {
                    int rang = row.get("rang") != null ? ((Number) row.get("rang")).intValue() : 0;
                    BigDecimal moy = (BigDecimal) row.get("moyenne");
                    var podium = PdfLayoutHelper.couleurPodium(rang);

                    Cell rankCell = new Cell().add(new Paragraph(String.valueOf(rang)).setBold().setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER);
                    Cell matCell = new Cell().add(new Paragraph((String) row.get("matricule")).setFontSize(9));
                    Cell nomCell = new Cell().add(new Paragraph(
                        row.get("prenom") + " " + row.get("nom")).setFontSize(10).setBold());
                    Cell moyCell = new Cell().add(new Paragraph(PdfLayoutHelper.formatNote(moy)).setFontSize(9))
                        .setTextAlignment(TextAlignment.CENTER);
                    Cell mentCell = new Cell().add(new Paragraph(PdfLayoutHelper.mentionHonneur(moy)).setFontSize(9))
                        .setTextAlignment(TextAlignment.CENTER).setFontColor(PdfLayoutHelper.VERT);

                    if (podium != null) {
                        rankCell.setBackgroundColor(podium);
                        matCell.setBackgroundColor(podium);
                        nomCell.setBackgroundColor(podium);
                        moyCell.setBackgroundColor(podium);
                        mentCell.setBackgroundColor(podium);
                    }
                    t.addCell(rankCell);
                    t.addCell(matCell);
                    t.addCell(nomCell);
                    t.addCell(moyCell);
                    t.addCell(mentCell);
                }
                doc.add(t);
            }
            doc.add(new Paragraph(
                "\nFélicitations à tous les élèves méritants pour leurs excellents résultats.")
                .setTextAlignment(TextAlignment.CENTER).setItalic().setFontSize(10));
            PdfLayoutHelper.addSignature(doc, "Le Proviseur");
        }
        return bos.toByteArray();
    }
}
