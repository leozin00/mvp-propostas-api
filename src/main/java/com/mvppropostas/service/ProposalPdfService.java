package com.mvppropostas.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.mvppropostas.domain.entity.Client;
import com.mvppropostas.domain.entity.Proposal;
import com.mvppropostas.domain.entity.ProposalItem;
import com.mvppropostas.domain.entity.User;

@Service
public class ProposalPdfService {

  private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
  private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final Color NAVY = new Color(11, 43, 111);
  private static final Color MUTED = new Color(100, 116, 139);

  public byte[] render(Proposal proposal, User issuer) {
    Client client = proposal.getClient();
    String issuerName =
        issuer.getCompanyName() != null && !issuer.getCompanyName().isBlank()
            ? issuer.getCompanyName().trim()
            : issuer.getName();
    String reference =
        proposal.getId().toString().replace("-", "").substring(0, 8).toUpperCase();

    Document document = new Document(PageSize.A4, 48, 48, 48, 48);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      PdfWriter.getInstance(document, output);
      document.open();

      Font brand = new Font(Font.HELVETICA, 11, Font.BOLD, NAVY);
      Font title = new Font(Font.HELVETICA, 18, Font.BOLD, NAVY);
      Font body = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
      Font small = new Font(Font.HELVETICA, 9, Font.NORMAL, MUTED);
      Font tableHead = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
      Font tableBody = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
      Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD, NAVY);

      document.add(new Paragraph(issuerName, brand));
      document.add(new Paragraph("Proposta comercial · #" + reference, small));
      document.add(gap());
      document.add(new Paragraph(nullToDash(proposal.getTitle()), title));
      document.add(new Paragraph("Cliente: " + nullToDash(client != null ? client.getName() : null), body));
      document.add(new Paragraph("Validade: " + proposal.getValidUntil().format(DATE), body));
      document.add(gap());

      if (proposal.getDescription() != null && !proposal.getDescription().isBlank()) {
        document.add(new Paragraph(proposal.getDescription().trim(), body));
        document.add(gap());
      }

      PdfPTable table = new PdfPTable(new float[] {4.2f, 1f, 1.4f, 1.4f});
      table.setWidthPercentage(100);
      table.addCell(head("Descrição", tableHead));
      table.addCell(head("Qtd", tableHead));
      table.addCell(head("Unitário", tableHead));
      table.addCell(head("Total", tableHead));

      if (proposal.getItems() != null) {
        for (ProposalItem item : proposal.getItems()) {
          table.addCell(cell(nullToDash(item.getDescription()), tableBody, Element.ALIGN_LEFT));
          table.addCell(cell(String.valueOf(item.getQuantity()), tableBody, Element.ALIGN_RIGHT));
          table.addCell(cell(money(item.getUnitPrice()), tableBody, Element.ALIGN_RIGHT));
          table.addCell(cell(money(item.getTotal()), tableBody, Element.ALIGN_RIGHT));
        }
      }

      document.add(table);
      document.add(gap());
      document.add(right("Subtotal: " + money(proposal.getSubtotal()), body));
      document.add(right("Desconto: " + money(proposal.getDiscount()), body));
      document.add(right("Total: " + money(proposal.getTotal()), totalFont));
      document.add(gap());
      document.add(
          new Paragraph(
              "Documento gerado pelo Dobrio. Neste plano o PDF não inclui logo da empresa.",
              small));

      document.close();
    } catch (DocumentException exception) {
      throw new IllegalStateException("Não foi possível gerar o PDF da proposta.", exception);
    }

    return output.toByteArray();
  }

  private static Paragraph gap() {
    Paragraph paragraph = new Paragraph(" ");
    paragraph.setSpacingAfter(8);
    return paragraph;
  }

  private static Paragraph right(String text, Font font) {
    Paragraph paragraph = new Paragraph(text, font);
    paragraph.setAlignment(Element.ALIGN_RIGHT);
    return paragraph;
  }

  private static PdfPCell head(String text, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setBackgroundColor(NAVY);
    cell.setPadding(6);
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    return cell;
  }

  private static PdfPCell cell(String text, Font font, int align) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setPadding(6);
    cell.setHorizontalAlignment(align);
    return cell;
  }

  private static String money(BigDecimal value) {
    return NumberFormat.getCurrencyInstance(PT_BR).format(value == null ? BigDecimal.ZERO : value);
  }

  private static String nullToDash(String value) {
    return value == null || value.isBlank() ? "—" : value.trim();
  }
}
