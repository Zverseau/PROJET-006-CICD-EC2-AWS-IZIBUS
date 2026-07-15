package com.example.izibus.services;

import com.example.izibus.dto.ReservationDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorService.class);
    private final ReservationService reservationService;

    // Définition des couleurs et polices personnalisées
    private static final Color RED_TITLE_COLOR = new Color(220, 53, 69);
    private static final Color BORDER_COLOR = new Color(222, 226, 230); // Un gris clair pour les bordures

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 22, Font.BOLD, RED_TITLE_COLOR);
    private static final Font SECTION_HEADER_FONT = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
    private static final Font AMOUNT_FONT = new Font(Font.HELVETICA, 16, Font.BOLD, RED_TITLE_COLOR);

    @Autowired
    public PdfGeneratorService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public byte[] generateTicketForReservation(Long reservationId) throws DocumentException {
        try {
            logger.info("Génération du PDF pour la réservation ID: {}", reservationId);

            ReservationDto reservation = reservationService.recupererReservationParId(reservationId);
            if (reservation == null) {
                logger.error("Réservation non trouvée pour l'ID: {}", reservationId);
                throw new IllegalArgumentException("Réservation introuvable");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A5, 20, 20, 20, 20); // Marges ajustées
            PdfWriter.getInstance(document, out);
            document.open();

            // En-tête
            addTitle(document);

            // Sections
            addSection(document, "Trajet", new String[][]{
                    {"Lieu de départ", safeGet(reservation.getLieuDepart())},
                    {"Lieu d'arrivée", safeGet(reservation.getLieuArriver())},
                    {"Date", formatDate(reservation.getDateTrajet())}
            });

            addSection(document, "Compagnie", new String[][]{
                    {"Nom de la compagnie", safeGet(reservation.getNomCompagnie())},
                    {"Nombre de places", String.valueOf(reservation.getNombrePlacesReservees())}
            });

            addSection(document, "Client", new String[][]{
                    {"Nom", safeGet(reservation.getNomPassager())},
                    {"Prénom", safeGet(reservation.getPrenomPassager())}
            });

            addSection(document, "Heure", new String[][]{
                    {"Départ", formatTime(reservation.getHeureDepart())},
                    {"Arrivée", formatTime(reservation.getHeureArriver())}
            });

            // Montant total
            addMontantTotal(document, reservation);

            // Message de fin
            addFooter(document);

            document.close();
            logger.info("PDF généré avec succès pour la réservation ID: {}", reservationId);
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du PDF pour la réservation ID: " + reservationId, e);
            throw new DocumentException("Erreur de génération du PDF: " + e.getMessage());
        }
    }

    private void addTitle(Document document) throws DocumentException {
        Paragraph title = new Paragraph("Détails de réservation", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);
    }

    private void addSection(Document document, String title, String[][] data) throws DocumentException {
        PdfPTable sectionContainer = new PdfPTable(1);
        sectionContainer.setWidthPercentage(90);
        sectionContainer.setSpacingBefore(8);
        sectionContainer.setSpacingAfter(8);
        sectionContainer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        sectionContainer.getDefaultCell().setPadding(0);

        PdfPCell titleCell = new PdfPCell(new Phrase(title, SECTION_HEADER_FONT));
        titleCell.setBackgroundColor(Color.WHITE);
        titleCell.setBorder(Rectangle.BOTTOM);
        titleCell.setBorderColor(BORDER_COLOR);
        titleCell.setPadding(6);
        titleCell.setPaddingLeft(10);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setPaddingBottom(8);
        sectionContainer.addCell(titleCell);

        PdfPTable contentTable = createKeyValueTable(data);
        PdfPCell contentCell = new PdfPCell(contentTable);
        contentCell.setBackgroundColor(Color.WHITE);
        contentCell.setBorder(Rectangle.NO_BORDER);
        contentCell.setPadding(8);
        sectionContainer.addCell(contentCell);

        PdfPCell finalSectionCell = new PdfPCell(sectionContainer);
        finalSectionCell.setBorder(Rectangle.BOX);
        finalSectionCell.setBorderColor(BORDER_COLOR);
        finalSectionCell.setPadding(0);
        finalSectionCell.setBackgroundColor(new Color(248, 249, 250));
        finalSectionCell.setUseVariableBorders(true);
        finalSectionCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPTable wrapperTable = new PdfPTable(1);
        wrapperTable.setWidthPercentage(100);
        wrapperTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        wrapperTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        wrapperTable.addCell(finalSectionCell);

        document.add(wrapperTable);
    }

    private void addMontantTotal(Document document, ReservationDto reservation) throws DocumentException {
        PdfPTable amountTable = new PdfPTable(2);
        amountTable.setWidthPercentage(90);
        amountTable.setHorizontalAlignment(Element.ALIGN_CENTER); // Le tableau lui-même reste centré
        amountTable.setSpacingBefore(10);
        amountTable.setSpacingAfter(10);
        amountTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        try {
            amountTable.setWidths(new float[]{0.6f, 0.4f}); // Laissé tel quel pour la répartition
        } catch (DocumentException e) {
            logger.error("Erreur lors de la définition des largeurs de colonne pour le montant total", e);
        }

        // Cellule pour le libellé "Montant total"
        PdfPCell labelCell = new PdfPCell(new Phrase("Montant total", NORMAL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT); // ALIGNE A GAUCHE
        labelCell.setPadding(0);
        labelCell.setPaddingLeft(10); // Ajouter un padding à gauche pour l'aligner avec le contenu des sections
        amountTable.addCell(labelCell);

        // Cellule pour la valeur du montant (reste à droite)
        String montantText = "CFA " + (reservation.getMontantTotal() > 0 ?
                String.format("%.0f", reservation.getMontantTotal()) : "N/A");
        PdfPCell valueCell = new PdfPCell(new Phrase(montantText, AMOUNT_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(0);
        valueCell.setPaddingRight(10); // Ajouter un padding à droite pour l'aligner avec le contenu des sections
        amountTable.addCell(valueCell);

        document.add(amountTable);
    }

    private void addFooter(Document document) throws DocumentException {
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BORDER_COLOR);
        separator.setOffset(-5);
        document.add(new Chunk(separator));

        Paragraph footer = new Paragraph("Merci de nous faire confiance, bon voyage !!!", NORMAL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);
    }

    private String formatTime(java.time.LocalTime time) {
        if (time == null) {
            return "N/A";
        }
        try {
            return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e) {
            logger.warn("Erreur de formatage de l'heure", e);
            return "N/A";
        }
    }

    private String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        try {
            return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            logger.warn("Erreur de formatage de la date", e);
            return "N/A";
        }
    }

    private String safeGet(String value) {
        return value != null && !value.trim().isEmpty() ? value : "N/A";
    }

    private PdfPTable createKeyValueTable(String[][] data) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        try {
            table.setWidths(new float[]{0.4f, 0.6f});
        } catch (DocumentException e) {
            logger.error("Erreur lors de la définition des largeurs de colonne", e);
        }

        for (String[] row : data) {
            if (row.length == 2) {
                PdfPCell keyCell = new PdfPCell(new Phrase(row[0], NORMAL_FONT));
                keyCell.setBorder(Rectangle.NO_BORDER);
                keyCell.setPadding(2);
                keyCell.setPaddingLeft(0);
                keyCell.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell valueCell = new PdfPCell(new Phrase(row[1], NORMAL_FONT));
                valueCell.setBorder(Rectangle.NO_BORDER);
                valueCell.setPadding(2);
                valueCell.setPaddingRight(0);
                valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(keyCell);
                table.addCell(valueCell);
            }
        }
        return table;
    }
}