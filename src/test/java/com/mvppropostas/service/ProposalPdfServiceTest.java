package com.mvppropostas.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.mvppropostas.domain.entity.Client;
import com.mvppropostas.domain.entity.Proposal;
import com.mvppropostas.domain.entity.ProposalItem;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.ProposalStatus;

class ProposalPdfServiceTest {

  private final ProposalPdfService service = new ProposalPdfService();

  @Test
  void renderProducesPdfBytes() {
    Client client = new Client();
    client.setName("Aurora Design");

    ProposalItem item = new ProposalItem();
    item.setDescription("Identidade visual");
    item.setQuantity(new BigDecimal("1"));
    item.setUnitPrice(new BigDecimal("1000.00"));
    item.setTotal(new BigDecimal("1000.00"));

    Proposal proposal = new Proposal();
    proposal.setId(UUID.fromString("33333333-3333-3333-3333-333333333331"));
    proposal.setTitle("Projeto de identidade");
    proposal.setDescription("Escopo resumido.");
    proposal.setStatus(ProposalStatus.SENT);
    proposal.setValidUntil(LocalDate.of(2026, 12, 31));
    proposal.setSubtotal(new BigDecimal("1000.00"));
    proposal.setDiscount(BigDecimal.ZERO);
    proposal.setTotal(new BigDecimal("1000.00"));
    proposal.setClient(client);
    proposal.setItems(List.of(item));

    User issuer = new User();
    issuer.setName("Maria Silva");
    issuer.setCompanyName("Estúdio Norte");

    byte[] pdf = service.render(proposal, issuer);

    assertThat(pdf).isNotEmpty();
    assertThat(new String(pdf, 0, 5)).isEqualTo("%PDF-");
  }
}
